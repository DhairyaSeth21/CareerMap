package com.careermappro.core;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {

    private static final String DB_URL = ConfigService.getOrDefault("db.url", "");
    private static final String DB_USER = ConfigService.getOrDefault("db.user", "");
    private static final String DB_PASS = ConfigService.getOrDefault("db.pass", "");

    public DatabaseService() {
        // No auto table creation for MySQL (you created them manually)
    }

    private Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public int upsertUser(String name, String email) {
        String sql = """
                INSERT INTO users (name, email)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE name = VALUES(name)
                """;

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();

            // Try getting generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getInt(1);
            }

            // If duplicate update: fetch existing ID
            return getUserIdByEmail(email);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void saveProficiencies(int userId, Map<String, Double> profs) {
        String sql = """
                INSERT INTO proficiencies (user_id, skill, proficiency)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE proficiency = VALUES(proficiency)
                """;

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (var entry : profs.entrySet()) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, entry.getKey());
                pstmt.setDouble(3, entry.getValue());
                pstmt.addBatch();
            }
            pstmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upsertProficiency(int userId, String skill, double value) {
        String sql = """
                INSERT INTO proficiencies (user_id, skill, proficiency)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE proficiency = VALUES(proficiency)
                """;

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, skill.toLowerCase());
            pstmt.setDouble(3, value);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("DB update failed for " + skill + ": " + e.getMessage());
        }
    }

    public Map<String, Double> loadProficiencies(int userId) {
        Map<String, Double> profs = new HashMap<>();
        String sql = "SELECT skill, proficiency FROM proficiencies WHERE user_id = ?";

        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                profs.put(rs.getString("skill").toLowerCase(), rs.getDouble("proficiency"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profs;
    }

    public void restoreTo(ProficiencyService prof, int userId) {
        Map<String, Double> loaded = loadProficiencies(userId);
        for (var e : loaded.entrySet()) {
            prof.addSkill(e.getKey(), e.getValue());
        }
    }

    public void saveAnalyticsSnapshot(int userId, double readiness) {
        String sql = """
                INSERT INTO analytics_snapshots (user_id, readiness)
                VALUES (?, ?)
                """;

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDouble(2, readiness);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveDomainSnapshot(int userId, Map<String, Double> domainScores) {
        String sql = """
                INSERT INTO readiness_trend (user_id, domain, score)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            for (var e : domainScores.entrySet()) {
                ps.setInt(1, userId);
                ps.setString(2, e.getKey());
                ps.setDouble(3, e.getValue());
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> loadLastDomainScores(int userId) {
        Map<String, Double> map = new HashMap<>();

        String sql = """
                SELECT domain, score FROM readiness_trend
                WHERE id IN (
                    SELECT MAX(id)
                    FROM readiness_trend
                    WHERE user_id = ?
                    GROUP BY domain
                )
                """;

        try (Connection conn = connect();
                PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("domain"), rs.getDouble("score"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Map<String, Double>> loadDomainSnapshots(int userId) {
        String sql = """
                SELECT domain, score, timestamp
                FROM readiness_trend
                WHERE user_id = ?
                ORDER BY timestamp ASC
                """;

        List<Map<String, Double>> snapshots = new ArrayList<>();
        Map<String, Double> currentSnapshot = new HashMap<>();
        Timestamp lastTime = null;

        try (Connection conn = connect();
                PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Timestamp time = rs.getTimestamp("timestamp");

                if (lastTime != null && !time.equals(lastTime)) {
                    snapshots.add(currentSnapshot);
                    currentSnapshot = new HashMap<>();
                }

                currentSnapshot.put(
                        rs.getString("domain"),
                        rs.getDouble("score"));

                lastTime = time;
            }

            if (!currentSnapshot.isEmpty()) {
                snapshots.add(currentSnapshot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return snapshots;
    }

    public static void main(String[] args) {
        DatabaseService db = new DatabaseService();

        int id = db.upsertUser("TestUser", "test@local.com");
        System.out.println("User ID = " + id);

        db.upsertProficiency(id, "java", 7.5);
        db.upsertProficiency(id, "spring", 6.2);

        Map<String, Double> profs = db.loadProficiencies(id);
        System.out.println("Loaded proficiencies: " + profs);

        db.saveAnalyticsSnapshot(id, 6.9);
        db.saveDomainSnapshot(id, Map.of("Backend", 7.2, "Cloud", 5.9));
        System.out.println("Saved analytics snapshot.");

        Map<String, Double> last = db.loadLastDomainScores(id);
        System.out.println("Last domain scores: " + last);

    }
}
