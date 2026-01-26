-- V7: Create Learning Resource Management Tables
-- Enables dynamic, personalized resource recommendations

-- Table: curated_resources
-- Stores all learning resources with quality tracking
CREATE TABLE IF NOT EXISTS curated_resources (
  resource_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  url TEXT NOT NULL,
  type VARCHAR(50), -- video, article, documentation, course, interactive
  source VARCHAR(100), -- official_docs, mdn, youtube, freecodecamp, etc.
  description TEXT,
  estimated_minutes INT,
  tags JSON, -- ["sql", "postgresql", "joins"]
  avg_quality_score FLOAT DEFAULT 3.0, -- Global rating (1-5)
  total_ratings INT DEFAULT 0,
  last_verified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_type (type),
  INDEX idx_source (source),
  INDEX idx_quality (avg_quality_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: user_resource_ratings
-- Tracks user preferences to personalize recommendations
CREATE TABLE IF NOT EXISTS user_resource_ratings (
  user_id INT NOT NULL,
  resource_id INT NOT NULL,
  rating FLOAT NOT NULL, -- 1-5 stars
  helpful BOOLEAN DEFAULT TRUE, -- True = "This helped", False = "Find different"
  feedback TEXT, -- Optional user comment
  rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, resource_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (resource_id) REFERENCES curated_resources(resource_id) ON DELETE CASCADE,
  INDEX idx_helpful (helpful),
  INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: node_resources
-- Links constellation nodes to learning resources with priority
CREATE TABLE IF NOT EXISTS node_resources (
  node_id INT NOT NULL,
  resource_id INT NOT NULL,
  priority INT DEFAULT 1, -- 1 = primary, 2 = alternative, etc.
  added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (node_id, resource_id),
  FOREIGN KEY (node_id) REFERENCES skill_nodes(skill_node_id) ON DELETE CASCADE,
  FOREIGN KEY (resource_id) REFERENCES curated_resources(resource_id) ON DELETE CASCADE,
  INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed with initial curated resources for common skills
-- JavaScript Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('JavaScript.info - The Modern JavaScript Tutorial', 'https://javascript.info/', 'documentation', 'javascript.info', 'Comprehensive JavaScript guide from basics to advanced topics', 180, '["javascript", "web-development", "tutorial"]'),
('JavaScript Crash Course For Beginners', 'https://www.youtube.com/watch?v=hdI2bqOjy3c', 'video', 'youtube', 'Complete JavaScript crash course by Traversy Media', 100, '["javascript", "beginner", "crash-course"]'),
('MDN JavaScript Guide', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide', 'documentation', 'mdn', 'Official Mozilla JavaScript documentation', 120, '["javascript", "reference", "official"]');

-- React Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('React Official Documentation', 'https://react.dev/', 'documentation', 'official_docs', 'Official React documentation with interactive examples', 150, '["react", "frontend", "official"]'),
('React Tutorial for Beginners', 'https://www.youtube.com/watch?v=SqcY0GlETPk', 'video', 'youtube', 'Complete React tutorial by Programming with Mosh', 150, '["react", "beginner", "tutorial"]'),
('Full Modern React Tutorial', 'https://www.youtube.com/playlist?list=PL4cUxeGkcC9gZD-Tvwfod2gaISzfRiP9d', 'video', 'youtube', 'Complete React course by The Net Ninja', 240, '["react", "project-based", "modern"]');

-- SQL Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('SQL Tutorial - Full Database Course', 'https://www.youtube.com/watch?v=HXV3zeQKqGY', 'video', 'youtube', 'Complete SQL course by freeCodeCamp', 240, '["sql", "database", "beginner"]'),
('SQLBolt - Learn SQL Interactively', 'https://sqlbolt.com/', 'interactive', 'sqlbolt', 'Interactive SQL lessons with exercises', 90, '["sql", "interactive", "hands-on"]'),
('PostgreSQL Tutorial', 'https://www.postgresqltutorial.com/', 'documentation', 'tutorial', 'Comprehensive PostgreSQL guide with examples', 120, '["postgresql", "sql", "database"]');

-- Python Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('Python for Everybody Specialization', 'https://www.py4e.com/', 'course', 'py4e', 'Complete Python course with free materials', 300, '["python", "beginner", "comprehensive"]'),
('Python Crash Course - FreeCodeCamp', 'https://www.youtube.com/watch?v=rfscVS0vtbw', 'video', 'youtube', 'Complete Python crash course', 240, '["python", "crash-course", "beginner"]'),
('Real Python Tutorials', 'https://realpython.com/', 'article', 'realpython', 'High-quality Python tutorials and guides', 60, '["python", "intermediate", "advanced"]');

-- Git Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('Pro Git Book', 'https://git-scm.com/book/en/v2', 'documentation', 'official_docs', 'Official Git documentation and guide', 180, '["git", "version-control", "official"]'),
('Git & GitHub Crash Course', 'https://www.youtube.com/watch?v=RGOj5yH7evk', 'video', 'youtube', 'Complete Git and GitHub tutorial by freeCodeCamp', 60, '["git", "github", "crash-course"]'),
('Learn Git Branching', 'https://learngitbranching.js.org/', 'interactive', 'learngitbranching', 'Interactive Git branching tutorial', 45, '["git", "interactive", "branching"]');

-- Docker Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('Docker Official Documentation', 'https://docs.docker.com/get-started/', 'documentation', 'official_docs', 'Official Docker getting started guide', 90, '["docker", "containers", "official"]'),
('Docker Tutorial for Beginners', 'https://www.youtube.com/watch?v=fqMOX6JJhGo', 'video', 'youtube', 'Complete Docker course by freeCodeCamp', 180, '["docker", "devops", "beginner"]'),
('Docker Mastery Course', 'https://www.youtube.com/watch?v=3c-iBn73dDE', 'video', 'youtube', 'Docker crash course by TechWorld with Nana', 90, '["docker", "kubernetes", "production"]');

-- Node.js Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('Node.js Official Docs', 'https://nodejs.org/en/docs/', 'documentation', 'official_docs', 'Official Node.js documentation', 120, '["nodejs", "backend", "official"]'),
('Node.js Crash Course', 'https://www.youtube.com/watch?v=fBNz5xF-Kx4', 'video', 'youtube', 'Complete Node.js tutorial by Traversy Media', 90, '["nodejs", "javascript", "backend"]'),
('Node.js Full Course', 'https://www.youtube.com/watch?v=Oe421EPjeBE', 'video', 'youtube', 'Complete Node.js course by freeCodeCamp', 480, '["nodejs", "express", "mongodb"]');

-- TypeScript Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('TypeScript Official Handbook', 'https://www.typescriptlang.org/docs/handbook/intro.html', 'documentation', 'official_docs', 'Official TypeScript documentation', 150, '["typescript", "javascript", "official"]'),
('TypeScript Course for Beginners', 'https://www.youtube.com/watch?v=BwuLxPH8IDs', 'video', 'youtube', 'Complete TypeScript course by freeCodeCamp', 180, '["typescript", "beginner", "crash-course"]'),
('TypeScript Deep Dive', 'https://basarat.gitbook.io/typescript/', 'documentation', 'gitbook', 'Comprehensive TypeScript guide', 240, '["typescript", "advanced", "deep-dive"]');

-- Network Security Resources
INSERT INTO curated_resources (title, url, type, source, description, estimated_minutes, tags) VALUES
('Network Security Fundamentals', 'https://www.youtube.com/watch?v=qiQR5rTSshw', 'video', 'youtube', 'Complete network security course', 300, '["security", "networking", "fundamentals"]'),
('OWASP Top 10', 'https://owasp.org/www-project-top-ten/', 'documentation', 'owasp', 'Top 10 web application security risks', 60, '["security", "web", "owasp"]'),
('Cybersecurity Full Course', 'https://www.youtube.com/watch?v=U_P23SqJaDc', 'video', 'youtube', 'Complete cybersecurity course by freeCodeCamp', 360, '["security", "cybersecurity", "comprehensive"]');

SELECT 'Resource tables created successfully!' as status;
SELECT COUNT(*) as total_seeded_resources FROM curated_resources;
