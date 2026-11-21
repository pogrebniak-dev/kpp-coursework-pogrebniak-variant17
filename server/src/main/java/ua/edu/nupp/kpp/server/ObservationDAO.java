package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObservationDAO {

    public void add(Observation obs) throws SQLException {
        String sql = "INSERT INTO observation (field_id, obs_date, f1, f2, f3) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, obs);
            ps.executeUpdate();
        }
    }

    public List<Observation> getAll() throws SQLException {
        String sql = "SELECT id, field_id, obs_date, f1, f2, f3 FROM observation ORDER BY id";
        List<Observation> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Observation getById(long id) throws SQLException {
        String sql = "SELECT id, field_id, obs_date, f1, f2, f3 FROM observation WHERE id = ?";
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

    public void update(Observation obs) throws SQLException {
        String sql = "UPDATE observation SET field_id = ?, obs_date = ?, f1 = ?, f2 = ?, f3 = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, obs);
            ps.setLong(6, obs.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM observation WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Observation obs) throws SQLException {
        ps.setLong(1, obs.getFieldId());
        ps.setString(2, obs.getObsDate());
        ps.setDouble(3, obs.getF1());
        ps.setDouble(4, obs.getF2());
        ps.setDouble(5, obs.getF3());
    }

    private Observation map(ResultSet rs) throws SQLException {
        Observation obs = new Observation();
        obs.setId(rs.getLong("id"));
        obs.setFieldId(rs.getLong("field_id"));
        obs.setObsDate(rs.getString("obs_date"));
        obs.setF1(rs.getDouble("f1"));
        obs.setF2(rs.getDouble("f2"));
        obs.setF3(rs.getDouble("f3"));
        return obs;
    }
}

