package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FieldDAO {

    public void add(Field field) throws SQLException {
        String sql = "INSERT INTO field (name, area, crop_type) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, field.getName());
            ps.setDouble(2, field.getArea());
            ps.setString(3, field.getCropType());
            ps.executeUpdate();
        }
    }

    public List<Field> getAll() throws SQLException {
        String sql = "SELECT id, name, area, crop_type FROM field ORDER BY id";
        List<Field> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Field getById(long id) throws SQLException {
        String sql = "SELECT id, name, area, crop_type FROM field WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void update(Field field) throws SQLException {
        String sql = "UPDATE field SET name = ?, area = ?, crop_type = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, field.getName());
            ps.setDouble(2, field.getArea());
            ps.setString(3, field.getCropType());
            ps.setLong(4, field.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM field WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Field map(ResultSet rs) throws SQLException {
        Field f = new Field();
        f.setId(rs.getLong("id"));
        f.setName(rs.getString("name"));
        f.setArea(rs.getDouble("area"));
        f.setCropType(rs.getString("crop_type"));
        return f;
    }
}

