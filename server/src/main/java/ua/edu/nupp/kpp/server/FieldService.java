package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class FieldService {
    private static final Logger LOG = Logger.getLogger(FieldService.class.getName());

    private final FieldDAO dao = new FieldDAO();

    public List<Field> getAll() throws SQLException {
        return dao.getAll();
    }

    public Field getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Field field) throws SQLException {
        validate(field);
        dao.add(field);
        LOG.info("Додано поле");
    }

    public void update(Field field) throws SQLException {
        validate(field);
        dao.update(field);
        LOG.info("Оновлено поле");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено поле");
    }

    static void validate(Field f) {
        if (f == null || blank(f.getName()) || blank(f.getCropType())) {
            throw new IllegalArgumentException("Назва поля та культура є обов'язковими");
        }
        if (f.getArea() <= 0) {
            throw new IllegalArgumentException("Площа поля має бути більшою за 0");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}