package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TreatmentActionDAO {

    public void add(TreatmentAction action) throws SQLException {
        String sql = "INSERT INTO treatment_action (problem_id, description, planned_date) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, action.getProblemId());
            ps.setString(2, action.getDescription());
            ps.setString(3, action.getPlannedDate());
            ps.executeUpdate();
        }
    }

    public List<TreatmentAction> getAll() throws SQLException {
        String sql = "SELECT id, problem_id, description, planned_date FROM treatment_action ORDER BY id";
        List<TreatmentAction> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public TreatmentAction getById(long id) throws SQLException {
        String sql = "SELECT id, problem_id, description, planned_date FROM treatment_action WHERE id = ?";
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

    public void update(TreatmentAction action) throws SQLException {
        String sql = "UPDATE treatment_action SET problem_id = ?, description = ?, planned_date = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, action.getProblemId());
            ps.setString(2, action.getDescription());
            ps.setString(3, action.getPlannedDate());
            ps.setLong(4, action.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM treatment_action WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private TreatmentAction map(ResultSet rs) throws SQLException {
        TreatmentAction ta = new TreatmentAction();
        ta.setId(rs.getLong("id"));
        ta.setProblemId(rs.getLong("problem_id"));
        ta.setDescription(rs.getString("description"));
        ta.setPlannedDate(rs.getString("planned_date"));
        return ta;
    }
}