package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProblemDAO {

    public void add(Problem problem) throws SQLException {
        String sql = "INSERT INTO problem (observation_id, state, risk_index) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, problem.getObservationId());
            ps.setString(2, problem.getState().name());
            ps.setDouble(3, problem.getRiskIndex());
            ps.executeUpdate();
        }
    }

    public List<Problem> getAll() throws SQLException {
        String sql = "SELECT id, observation_id, state, risk_index FROM problem ORDER BY id";
        List<Problem> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Problem getById(long id) throws SQLException {
        String sql = "SELECT id, observation_id, state, risk_index FROM problem WHERE id = ?";
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

    public void update(Problem problem) throws SQLException {
        String sql = "UPDATE problem SET observation_id = ?, state = ?, risk_index = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, problem.getObservationId());
            ps.setString(2, problem.getState().name());
            ps.setDouble(3, problem.getRiskIndex());
            ps.setLong(4, problem.getId());
            ps.executeUpdate();
        }
    }

    public void updateState(long id, ProblemState state) throws SQLException {
        String sql = "UPDATE problem SET state = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, state.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM problem WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Problem map(ResultSet rs) throws SQLException {
        Problem p = new Problem();
        p.setId(rs.getLong("id"));
        p.setObservationId(rs.getLong("observation_id"));
        p.setState(ProblemState.valueOf(rs.getString("state")));
        p.setRiskIndex(rs.getDouble("risk_index"));
        return p;
    }
}
