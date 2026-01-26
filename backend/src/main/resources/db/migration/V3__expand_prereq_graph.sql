-- V3: Expand Prerequisite Graph
-- Add edges only for skills that exist in skill_nodes

-- Web Development Path
-- JavaScript → TypeScript (HARD: Must know JS to learn TS)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'JavaScript'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'TypeScript'),
    'HARD', 0.95;

-- JavaScript → Node.js (HARD: JS required for backend Node)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'JavaScript'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Node.js'),
    'HARD', 0.9;

-- React → Redux (SOFT: React useful but not required)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'React'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Redux'),
    'SOFT', 0.7;

-- TypeScript → Next.js (SOFT: TS helpful for Next)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'TypeScript'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Next.js'),
    'SOFT', 0.6;

-- React → React Native (HARD: React required for RN)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'React'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'React Native'),
    'HARD', 0.9;

-- Backend Development
-- Python → Django/Flask (HARD: Python required)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Python'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Machine Learning'),
    'HARD', 0.9;

-- Java → Spring Boot (HARD: Java required)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Java'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Spring Boot'),
    'HARD', 0.95;

-- Database Prerequisites
-- SQL → MySQL/PostgreSQL (HARD: SQL fundamentals required)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'SQL'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'MySQL'),
    'HARD', 0.85;

INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'SQL'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'PostgreSQL'),
    'HARD', 0.85;

-- JavaScript → MongoDB (SOFT: JS commonly used with Mongo)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'JavaScript'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'MongoDB'),
    'SOFT', 0.5;

-- Security Path (most important for our Security Engineer role)
-- Linux → Network Security (HARD: Linux sysadmin needed)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Linux'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Network Security'),
    'HARD', 0.9;

-- Networking → Network Security (HARD: Network fundamentals required)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Networking'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Network Security'),
    'HARD', 0.95;

-- Network Security → Penetration Testing (HARD: Must understand security first)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Network Security'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Penetration Testing'),
    'HARD', 0.95;

-- Python → Penetration Testing (SOFT: Python helpful for scripting)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Python'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Penetration Testing'),
    'SOFT', 0.7;

-- Cryptography → Security (SOFT: Crypto helps with security understanding)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Cryptography'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Security'),
    'SOFT', 0.75;

-- Network Security → Security (HARD: NetSec foundational for security)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Network Security'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Security'),
    'HARD', 0.85;

-- Security → Compliance (SOFT: Security knowledge helps with compliance)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Security'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Compliance'),
    'SOFT', 0.65;

-- Cloud & DevOps
-- Linux → Docker (HARD: Linux concepts essential for Docker)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Linux'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Docker'),
    'HARD', 0.9;

-- Docker → Kubernetes (HARD: Docker required for K8s)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Docker'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Kubernetes'),
    'HARD', 0.95;

-- Git → CI/CD (HARD: Version control required for CI/CD)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Git'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'CI/CD'),
    'HARD', 0.85;

-- Infrastructure as Code dependencies
-- Terraform → AWS/Azure/GCP (SOFT: IaC useful with cloud)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Terraform'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'AWS'),
    'SOFT', 0.6;

INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Terraform'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Azure'),
    'SOFT', 0.6;

INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Terraform'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'GCP'),
    'SOFT', 0.6;

-- Data Science/ML Path
-- Python → PyTorch/TensorFlow (HARD: Python required for ML frameworks)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Python'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'PyTorch'),
    'HARD', 0.9;

INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Python'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'TensorFlow'),
    'HARD', 0.9;

-- Machine Learning → PyTorch/TensorFlow (SOFT: ML concepts help)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Machine Learning'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'PyTorch'),
    'SOFT', 0.75;

INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Machine Learning'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'TensorFlow'),
    'SOFT', 0.75;

-- Statistics → Machine Learning (HARD: Stats required for ML)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Statistics'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Machine Learning'),
    'HARD', 0.85;

-- Big Data
-- SQL → Data Warehousing (HARD: SQL fundamentals required)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'SQL'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Data Warehousing'),
    'HARD', 0.85;

-- Python → Spark/Airflow (HARD: Python used in data pipelines)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Python'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Spark'),
    'HARD', 0.8;

INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Python'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Airflow'),
    'HARD', 0.8;

-- Testing Path
-- JavaScript → Jest (HARD: JS required for Jest)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'JavaScript'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Jest'),
    'HARD', 0.85;

-- JavaScript → Selenium (SOFT: JS useful for Selenium scripting)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'JavaScript'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Selenium'),
    'SOFT', 0.6;

-- Architecture & Design
-- Microservices → Kubernetes (SOFT: K8s commonly used for microservices)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Microservices'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Kubernetes'),
    'SOFT', 0.7;

-- API Design → GraphQL (SOFT: API design principles help)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'API Design'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'GraphQL'),
    'SOFT', 0.65;

-- System Design → Architecture (SOFT: System design feeds into architecture)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'System Design'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Architecture'),
    'SOFT', 0.75;

-- Data Structures & Algorithms → System Design (HARD: Fundamentals required)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Data Structures'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'System Design'),
    'HARD', 0.8;

INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Algorithms'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'System Design'),
    'HARD', 0.8;

-- Monitoring & Observability
-- System Design → Monitoring (SOFT: Understanding systems helps monitoring)
INSERT IGNORE INTO prereq_edges (from_skill_id, to_skill_id, type, strength)
SELECT
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'System Design'),
    (SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'Monitoring'),
    'SOFT', 0.6;

SELECT COUNT(*) as total_prereq_edges FROM prereq_edges;
