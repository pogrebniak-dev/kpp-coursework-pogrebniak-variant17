package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ObservationService {
    private static final Logger LOG = Logger.getLogger(ObservationService.class.getName());

    private final ObservationDAO dao = new ObservationDAO();
    private final FieldDAO fieldDAO = new FieldDAO();

    public List<Observation> getAll() throws SQLException {
        return dao.getAll();
    }

    public Observation getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Observation obs) throws SQLException {
        validate(obs);
        requireField(obs.getFieldId());
        dao.add(obs);
        LOG.info("Додано спостереження");
    }

    public void update(Observation obs) throws SQLException {
        validate(obs);
        requireField(obs.getFieldId());
        dao.update(obs);
        LOG.info("Оновлено спостереження");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено спостереження");
    }

    private void validate(Observation obs) {
        if (obs == null) {
            throw new IllegalArgumentException("Спостереження не може бути null");
        }
        if (obs.getFieldId() <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор поля");
        }
        if (obs.getObsDate() == null || obs.getObsDate().isBlank()) {
            throw new IllegalArgumentException("Дата спостереження є обов'язковою");
        }
        if (outOfRange(obs.getF1()) || outOfRange(obs.getF2()) || outOfRange(obs.getF3())) {
            throw new IllegalArgumentException("Фактори F1, F2 і F3 мають бути в діапазоні 0..100");
        }
    }

    private boolean outOfRange(double value) {
        return value < 0 || value > 100;
    }

    private void requireField(long fieldId) throws SQLException {
        Field field = fieldDAO.getById(fieldId);
        if (field == null) {
            throw new IllegalArgumentException("Поле не знайдено");
        }
    }
}
