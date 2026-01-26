-- ============================================================================
-- EDLSG (Evidence-Driven Living Skill Graph) Schema Migration
-- Version: 2.0.0
-- ============================================================================

-- Skill graph ontology
CREATE TABLE IF NOT EXISTS skill_nodes (
    skill_node_id INT AUTO_INCREMENT PRIMARY KEY,
    canonical_name VARCHAR(255) UNIQUE NOT NULL,
    aliases JSON,
    domain VARCHAR(100),
    difficulty TINYINT DEFAULT 3 CHECK (difficulty BETWEEN 1 AND 5),
    decay_half_life_days INT DEFAULT 180,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_canonical (canonical_name),
    INDEX idx_domain (domain)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prereq_edges (
    edge_id INT AUTO_INCREMENT PRIMARY KEY,
    from_skill_id INT NOT NULL,
    to_skill_id INT NOT NULL,
    type ENUM('HARD', 'SOFT') NOT NULL DEFAULT 'SOFT',
    strength DECIMAL(3,2) NOT NULL DEFAULT 0.70 CHECK (strength BETWEEN 0 AND 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_skill_id) REFERENCES skill_nodes(skill_node_id) ON DELETE CASCADE,
    FOREIGN KEY (to_skill_id) REFERENCES skill_nodes(skill_node_id) ON DELETE CASCADE,
    UNIQUE KEY unique_prereq (from_skill_id, to_skill_id),
    INDEX idx_to_skill (to_skill_id),
    INDEX idx_from_skill (from_skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Evidence system
CREATE TABLE IF NOT EXISTS evidence (
    evidence_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type ENUM('QUIZ', 'PROJECT', 'REPO', 'CERT', 'WORK_SAMPLE') NOT NULL,
    source_uri VARCHAR(512),
    raw_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS evidence_skill_links (
    link_id INT AUTO_INCREMENT PRIMARY KEY,
    evidence_id INT NOT NULL,
    skill_id INT NOT NULL,
    support DECIMAL(3,2) NOT NULL CHECK (support BETWEEN 0 AND 1),
    extracted_by VARCHAR(50) DEFAULT 'LLM',
    confidence DECIMAL(3,2) DEFAULT 0.80 CHECK (confidence BETWEEN 0 AND 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (evidence_id) REFERENCES evidence(evidence_id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill_nodes(skill_node_id) ON DELETE CASCADE,
    INDEX idx_evidence (evidence_id),
    INDEX idx_skill (skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User state machine
CREATE TABLE IF NOT EXISTS user_skill_states (
    state_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    status ENUM('UNSEEN', 'INFERRED', 'ACTIVE', 'PROVED', 'STALE') NOT NULL DEFAULT 'UNSEEN',
    confidence DECIMAL(3,2) NOT NULL DEFAULT 0.00 CHECK (confidence BETWEEN 0 AND 1),
    evidence_score DECIMAL(5,2) DEFAULT 0.00,
    last_evidence_at TIMESTAMP NULL,
    stale_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill_nodes(skill_node_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_skill (user_id, skill_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_stale (stale_at),
    INDEX idx_user_updated (user_id, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Role demand weights
CREATE TABLE IF NOT EXISTS role_skill_weights (
    weight_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    skill_id INT NOT NULL,
    weight DECIMAL(3,2) NOT NULL DEFAULT 0.10 CHECK (weight BETWEEN 0 AND 1),
    required_level DECIMAL(3,1) DEFAULT 5.0 CHECK (required_level BETWEEN 0 AND 10),
    source ENUM('CURATED', 'MARKET_INFERRED') DEFAULT 'CURATED',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill_nodes(skill_node_id) ON DELETE CASCADE,
    UNIQUE KEY unique_role_skill (role_id, skill_id),
    INDEX idx_role (role_id),
    INDEX idx_source (source)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Observability: path snapshots
CREATE TABLE IF NOT EXISTS path_snapshots (
    snapshot_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    readiness_score DECIMAL(4,2),
    breakdown_json TEXT,
    next_action_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_role_created (role_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Observability: AI call tracking
CREATE TABLE IF NOT EXISTS ai_calls (
    call_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    feature VARCHAR(50) NOT NULL,
    model VARCHAR(50) DEFAULT 'gpt-4',
    prompt_hash CHAR(64),
    latency_ms INT,
    status VARCHAR(20),
    error_code VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_feature (user_id, feature),
    INDEX idx_created (created_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- DATA MIGRATION: Seed skill_nodes from existing skills table
-- ============================================================================
INSERT INTO skill_nodes (canonical_name, aliases, domain, difficulty)
SELECT
    name AS canonical_name,
    JSON_ARRAY() AS aliases,
    category AS domain,
    CASE
        WHEN category IN ('Programming Languages', 'Frameworks', 'Backend') THEN 4
        WHEN category IN ('Tools', 'DevOps', 'Cloud') THEN 3
        ELSE 3
    END AS difficulty
FROM skills
ON DUPLICATE KEY UPDATE canonical_name = canonical_name;

-- ============================================================================
-- DATA MIGRATION: Seed user_skill_states from proficiencies
-- ============================================================================
INSERT INTO user_skill_states (user_id, skill_id, status, confidence, evidence_score, last_evidence_at)
SELECT
    p.user_id,
    sn.skill_node_id,
    CASE
        WHEN p.proficiency >= 7.0 THEN 'PROVED'
        WHEN p.proficiency >= 4.0 THEN 'ACTIVE'
        WHEN p.proficiency > 0 THEN 'INFERRED'
        ELSE 'UNSEEN'
    END AS status,
    LEAST(p.proficiency / 10.0, 1.0) AS confidence,
    p.proficiency AS evidence_score,
    p.updated_at
FROM proficiencies p
JOIN skill_nodes sn ON p.skill = sn.canonical_name
ON DUPLICATE KEY UPDATE
    confidence = GREATEST(confidence, VALUES(confidence)),
    evidence_score = GREATEST(evidence_score, VALUES(evidence_score)),
    last_evidence_at = GREATEST(last_evidence_at, VALUES(last_evidence_at));

-- ============================================================================
-- DATA MIGRATION: Copy role_skill to role_skill_weights
-- ============================================================================
INSERT INTO role_skill_weights (role_id, skill_id, weight, required_level, source)
SELECT
    rs.role_id,
    sn.skill_node_id,
    COALESCE(rs.weight, 0.10) AS weight,
    COALESCE(rs.required_level, 5.0) AS required_level,
    'CURATED' AS source
FROM role_skill rs
JOIN skills s ON rs.skill_id = s.skill_id
JOIN skill_nodes sn ON s.name = sn.canonical_name
ON DUPLICATE KEY UPDATE
    weight = VALUES(weight),
    required_level = VALUES(required_level);

-- ============================================================================
-- VERIFICATION QUERIES (comment out in production)
-- ============================================================================
-- SELECT 'skill_nodes count:' AS check_name, COUNT(*) AS count FROM skill_nodes;
-- SELECT 'user_skill_states count:' AS check_name, COUNT(*) AS count FROM user_skill_states;
-- SELECT 'role_skill_weights count:' AS check_name, COUNT(*) AS count FROM role_skill_weights;
-- SELECT 'Status distribution:' AS check_name, status, COUNT(*) FROM user_skill_states GROUP BY status;
