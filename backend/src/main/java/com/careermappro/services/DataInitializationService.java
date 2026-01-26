package com.careermappro.services;

import com.careermappro.entities.Achievement;
import com.careermappro.entities.Role;
import com.careermappro.entities.RoleSkill;
import com.careermappro.entities.Skill;
import com.careermappro.repositories.AchievementRepository;
import com.careermappro.repositories.RoleRepository;
import com.careermappro.repositories.RoleSkillRepository;
import com.careermappro.repositories.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataInitializationService implements CommandLineRunner {

    private final AchievementRepository achievementRepository;
    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final RoleSkillRepository roleSkillRepository;

    public DataInitializationService(
        AchievementRepository achievementRepository,
        RoleRepository roleRepository,
        SkillRepository skillRepository,
        RoleSkillRepository roleSkillRepository
    ) {
        this.achievementRepository = achievementRepository;
        this.roleRepository = roleRepository;
        this.skillRepository = skillRepository;
        this.roleSkillRepository = roleSkillRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize achievements if database is empty
        if (achievementRepository.count() == 0) {
            achievementRepository.save(new Achievement(
                "First Steps",
                "Complete your first quiz",
                "🎯",
                50,
                "quiz",
                "1"
            ));

            achievementRepository.save(new Achievement(
                "Knowledge Seeker",
                "Complete 5 quizzes",
                "📚",
                100,
                "quiz",
                "5"
            ));

            achievementRepository.save(new Achievement(
                "Skill Collector",
                "Track 10 different skills",
                "⭐",
                150,
                "skill",
                "10"
            ));

            achievementRepository.save(new Achievement(
                "Week Warrior",
                "Maintain a 7-day login streak",
                "🔥",
                200,
                "streak",
                "7"
            ));

            achievementRepository.save(new Achievement(
                "Goal Setter",
                "Set your first career goal",
                "🎖️",
                50,
                "goal",
                "1"
            ));

            achievementRepository.save(new Achievement(
                "Master",
                "Reach 90% proficiency in any skill",
                "💎",
                300,
                "skill",
                "1"
            ));

            achievementRepository.save(new Achievement(
                "Quiz Champion",
                "Complete 50 quizzes",
                "🏆",
                500,
                "quiz",
                "50"
            ));

            achievementRepository.save(new Achievement(
                "Consistency King",
                "Maintain a 30-day login streak",
                "💪",
                1000,
                "streak",
                "30"
            ));

            System.out.println("✅ Initialized 8 default achievements");
        }

        // Initialize roles if database is empty
        if (roleRepository.count() == 0) {
            initializeRoles();
        }
    }

    private void initializeRoles() {
        Map<String, String[]> roleSkillMap = new HashMap<>();

        // Software Development (10 roles)
        roleSkillMap.put("Senior Software Engineer", new String[]{"Java", "Python", "System Design", "SQL", "Git", "Docker", "Kubernetes"});
        roleSkillMap.put("Backend Engineer", new String[]{"Node.js", "Python", "PostgreSQL", "REST API", "MongoDB", "Redis", "Microservices"});
        roleSkillMap.put("Full Stack Developer", new String[]{"React", "Node.js", "TypeScript", "PostgreSQL", "AWS", "Docker", "Git"});
        roleSkillMap.put("Frontend Specialist", new String[]{"React", "TypeScript", "CSS", "JavaScript", "Redux", "Webpack", "Testing"});
        roleSkillMap.put("Mobile Developer", new String[]{"React Native", "Swift", "Kotlin", "Mobile UI", "REST API", "Firebase", "Git"});
        roleSkillMap.put("API Engineer", new String[]{"REST API", "GraphQL", "Node.js", "API Design", "Microservices", "Documentation", "Security"});
        roleSkillMap.put("Product Engineer", new String[]{"React", "Node.js", "Product Thinking", "A/B Testing", "Analytics", "User Research", "Agile"});
        roleSkillMap.put("Game Developer", new String[]{"Unity", "C#", "3D Graphics", "Physics", "Game Design", "Optimization", "Multiplayer"});
        roleSkillMap.put("Embedded Engineer", new String[]{"C", "C++", "Microcontrollers", "Hardware", "RTOS", "Assembly", "Debugging"});
        roleSkillMap.put("Blockchain Developer", new String[]{"Solidity", "Web3", "Ethereum", "Smart Contracts", "Cryptography", "JavaScript", "Testing"});

        // Infrastructure & Cloud (8 roles)
        roleSkillMap.put("DevOps Engineer", new String[]{"Docker", "Kubernetes", "AWS", "CI/CD", "Terraform", "Linux", "Python"});
        roleSkillMap.put("Cloud Architect", new String[]{"AWS", "Azure", "GCP", "Terraform", "Kubernetes", "System Design", "Security"});
        roleSkillMap.put("Platform Engineer", new String[]{"Kubernetes", "Docker", "CI/CD", "Monitoring", "Linux", "Python", "Infrastructure as Code"});
        roleSkillMap.put("Systems Engineer", new String[]{"Linux", "Networking", "Security", "Python", "Monitoring", "Performance Tuning", "System Design"});
        roleSkillMap.put("Site Reliability Engineer", new String[]{"Monitoring", "Incident Response", "Automation", "Kubernetes", "Python", "Performance", "Scalability"});
        roleSkillMap.put("Network Engineer", new String[]{"Networking", "TCP/IP", "Routing", "Firewalls", "VPN", "Security", "Linux"});
        roleSkillMap.put("Database Administrator", new String[]{"PostgreSQL", "MySQL", "MongoDB", "Performance Tuning", "Backup", "Replication", "SQL"});
        roleSkillMap.put("Infrastructure Architect", new String[]{"System Design", "AWS", "Networking", "Security", "Scalability", "High Availability", "Cost Optimization"});

        // Data & AI (7 roles)
        roleSkillMap.put("ML Engineer", new String[]{"Python", "TensorFlow", "PyTorch", "Machine Learning", "Statistics", "SQL", "AWS"});
        roleSkillMap.put("Data Engineer", new String[]{"Python", "Spark", "Airflow", "SQL", "AWS", "Kafka", "Data Warehousing"});
        roleSkillMap.put("Data Scientist", new String[]{"Python", "Statistics", "Machine Learning", "SQL", "Data Visualization", "R", "Jupyter"});
        roleSkillMap.put("AI Research Engineer", new String[]{"PyTorch", "Deep Learning", "Research", "Python", "Mathematics", "Computer Vision", "NLP"});
        roleSkillMap.put("Analytics Engineer", new String[]{"SQL", "Python", "dbt", "Data Modeling", "BI Tools", "Analytics", "ETL"});
        roleSkillMap.put("Business Intelligence Engineer", new String[]{"SQL", "Tableau", "Power BI", "Data Warehousing", "ETL", "Python", "Data Visualization"});
        roleSkillMap.put("Computer Vision Engineer", new String[]{"Python", "OpenCV", "Deep Learning", "Image Processing", "PyTorch", "Mathematics", "GPU Programming"});

        // Security (4 roles)
        roleSkillMap.put("Security Engineer", new String[]{"Security", "Penetration Testing", "Cryptography", "Linux", "Python", "Network Security", "Compliance"});
        roleSkillMap.put("Application Security Engineer", new String[]{"Security", "OWASP", "Code Review", "Penetration Testing", "Python", "Cryptography", "Compliance"});
        roleSkillMap.put("Cloud Security Engineer", new String[]{"AWS", "Security", "IAM", "Compliance", "Encryption", "Monitoring", "Incident Response"});
        roleSkillMap.put("Security Architect", new String[]{"Security", "System Design", "Compliance", "Risk Management", "Cryptography", "Architecture", "Threat Modeling"});

        // Quality & Testing (2 roles)
        roleSkillMap.put("QA Engineer", new String[]{"Testing", "Selenium", "Jest", "Python", "CI/CD", "SQL", "REST API"});
        roleSkillMap.put("Test Automation Engineer", new String[]{"Selenium", "Python", "CI/CD", "Testing Frameworks", "JavaScript", "Performance Testing", "API Testing"});

        // Leadership (4 roles)
        roleSkillMap.put("Technical Lead", new String[]{"System Design", "Architecture", "Mentoring", "Project Management", "Code Review", "Java", "AWS"});
        roleSkillMap.put("Engineering Manager", new String[]{"Leadership", "Mentoring", "Project Management", "Agile", "System Design", "Communication", "Strategy"});
        roleSkillMap.put("Solutions Architect", new String[]{"System Design", "AWS", "Architecture Patterns", "Microservices", "Security", "Scalability", "Documentation"});
        roleSkillMap.put("Staff Engineer", new String[]{"System Design", "Architecture", "Technical Strategy", "Mentoring", "Cross-functional", "Innovation", "Impact"});

        for (Map.Entry<String, String[]> entry : roleSkillMap.entrySet()) {
            String roleName = entry.getKey();
            String[] skillNames = entry.getValue();

            Role role = new Role(roleName, "Engineering", "Tech role: " + roleName, "💼");
            role = roleRepository.save(role);

            for (String skillName : skillNames) {
                Skill skill = skillRepository.findByName(skillName)
                    .orElseGet(() -> {
                        Skill newSkill = new Skill();
                        newSkill.setName(skillName);
                        newSkill.setCategory("Technical");
                        return skillRepository.save(newSkill);
                    });

                RoleSkill roleSkill = new RoleSkill(role, skill, 1.0, 7.0);
                roleSkillRepository.save(roleSkill);
            }
        }

        System.out.println("✅ Initialized 35 roles with skill requirements");
    }
}
