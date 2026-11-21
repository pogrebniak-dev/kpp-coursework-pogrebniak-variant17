package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TreatmentActionService {
    private static final Logger LOG = Logger.getLogger(TreatmentActionService.class.getName());

    private final TreatmentActionDAO dao = new TreatmentActionDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();

    public List<TreatmentAction> getAll() throws SQLException {
        return dao.getAll();
    }

    public TreatmentAction getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(TreatmentAction action) throws SQLException {
        validate(action);
        requireProblem(action.getProblemId());
        dao.add(action);
        LOG.info("Додано захід обробки");
    }

    public void update(TreatmentAction action) throws SQLException {
        validate(action);
        requireProblem(action.getProblemId());
        dao.update(action);
        LOG.info("Оновлено захід обробки");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено захід обробки");
    }

    private void validate(TreatmentAction t) {
        if (t == null || t.getProblemId() <= 0
        || blank(t.getDescription()) ||  blank(t.getPlannedDate())) {
            throw new IllegalArgumentException("Некоректні дані заходу обробки");
        }
    }

    private void requireProblem(long problemId) throws SQLException {
        Problem problem = problemDAO.getById(problemId);
        if (problem == null) {
            throw new IllegalArgumentException("Проблему не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
