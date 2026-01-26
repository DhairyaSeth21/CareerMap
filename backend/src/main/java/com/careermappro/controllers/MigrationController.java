package com.careermappro.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final JdbcTemplate jdbcTemplate;

    public MigrationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Add primary goal column
     */
    @PostMapping("/add-primary-goal-column")
    public Map<String, Object> addPrimaryGoalColumn() {
        try {
            // Check if column exists first
            Integer columnExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'careermap_db' AND TABLE_NAME = 'career_goals' AND COLUMN_NAME = 'is_primary'",
                Integer.class
            );

            if (columnExists == 0) {
                jdbcTemplate.execute("ALTER TABLE career_goals ADD COLUMN is_primary BOOLEAN DEFAULT FALSE");
            }

            jdbcTemplate.update("""
                UPDATE career_goals
                SET is_primary = TRUE
                WHERE user_id = 28
                  AND id = (SELECT MIN(id) FROM (SELECT * FROM career_goals WHERE user_id = 28) AS temp)
                  AND NOT EXISTS (SELECT 1 FROM career_goals WHERE user_id = 28 AND is_primary = TRUE)
                """);
            return Map.of("success", true, "message", "Primary goal column added", "columnAdded", columnExists == 0);
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    /**
     * Migrate proficiencies data to user_skills table
     */
    @PostMapping("/proficiencies-to-user-skills")
    public Map<String, Object> migrateProficiencies() {
        String sql = """
            INSERT INTO user_skills (user_id, skill_id, proficiency_level, evidence_type, evidence_text, created_at, last_updated)
            SELECT
                p.user_id,
                s.skill_id,
                LEAST(100, GREATEST(0, CAST(p.proficiency * 10 AS SIGNED))) as proficiency_level,
                'self_assessment',
                CONCAT('Migrated from proficiencies table with level ', p.proficiency),
                NOW(),
                NOW()
            FROM proficiencies p
            INNER JOIN skills s ON LOWER(p.skill) = LOWER(s.name)
            WHERE NOT EXISTS (
                SELECT 1 FROM user_skills us
                WHERE us.user_id = p.user_id AND us.skill_id = s.skill_id
            )
            """;

        int rowsAffected = jdbcTemplate.update(sql);

        return Map.of(
            "success", true,
            "message", "Migration completed",
            "rowsMigrated", rowsAffected
        );
    }
}
