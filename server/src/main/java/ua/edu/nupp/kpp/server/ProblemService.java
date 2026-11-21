package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ProblemService {
    private static final Logger LOG = Logger.getLogger(ProblemService.class.getName());

    private final ProblemDAO dao = new ProblemDAO();
    private final ObservationDAO observationDAO = new ObservationDAO();

    public List<Problem> getAll() throws SQLException {
        return dao.getAll();
    }

    public Problem getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(long observationId) throws SQLException {
        dao.add(fromObservation(null, observationId, ProblemState.ВИЯВЛЕНО));
        LOG.info("Створено проблему");
    }

    public void update(long id, long observationId) throws SQLException {
        Problem old = dao.getById(id);
        if (old == null) {
            throw new IllegalArgumentException("Проблему не знайдено");
        }
        dao.update(fromObservation(id, observationId, old.getState()));
        LOG.info("Оновлено проблему");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено проблему");
    }

    public static boolean isAllowedTransition(ProblemState from, ProblemState to) {
        return (from == ProblemState.ВИЯВЛЕНО && to == ProblemState.ПІДТВЕРДЖЕНО)
                || (from == ProblemState.ПІДТВЕРДЖЕНО && to == ProblemState.ОБРОБКУ_ЗАПЛАНОВАНО)
                || (from == ProblemState.ОБРОБКУ_ЗАПЛАНОВАНО && to == ProblemState.УСУНЕНО);
    }

    public void changeState(long id, ProblemState next) throws SQLException {
        Problem current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Проблему не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан: " + current.getState() + " -> " + next);
    }

    private Problem fromObservation(Long id, long observationId, ProblemState state) throws SQLException {
        Observation obs = observationDAO.getById(observationId);
        if (obs == null) {
            throw new IllegalArgumentException("Спостереження не знайдено");
        }
        double riskIndex = RiskCalculator.calculate(obs.getF1(), obs.getF2(), obs.getF3());
        return new Problem(id, observationId, state, riskIndex);
    }
}