# Multi-Level Frontier Implementation Plan

## Goal
Transform the basic skill graph into a 4-level zoomable hierarchy:
- Level 1: Domains (Cybersecurity, ML, Backend, Frontend, Cloud, Mobile)
- Level 2: Roles within domain (Security Engineer, Cloud Security, etc.)
- Level 3: Deep paths (12-week skill progression for a role)
- Level 4: Atomic competencies (current skill graph)

---

## Phase 1: Database Schema

### New Tables

#### 1. `domains` table
```sql
CREATE TABLE domains (
  domain_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  icon VARCHAR(50),
  color VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. `career_roles` table (separate from existing `roles`)
```sql
CREATE TABLE career_roles (
  career_role_id INT PRIMARY KEY AUTO_INCREMENT,
  domain_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  icon VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (domain_id) REFERENCES domains(domain_id)
);
```

#### 3. `deep_paths` table
```sql
CREATE TABLE deep_paths (
  deep_path_id INT PRIMARY KEY AUTO_INCREMENT,
  career_role_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  duration_weeks INT DEFAULT 12,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (career_role_id) REFERENCES career_roles(career_role_id)
);
```

#### 4. `deep_path_steps` table
```sql
CREATE TABLE deep_path_steps (
  step_id INT PRIMARY KEY AUTO_INCREMENT,
  deep_path_id INT NOT NULL,
  week_number INT NOT NULL,
  skill_id INT NOT NULL,
  order_in_week INT DEFAULT 0,
  FOREIGN KEY (deep_path_id) REFERENCES deep_paths(deep_path_id),
  FOREIGN KEY (skill_id) REFERENCES skill_nodes(skill_node_id)
);
```

---

## Phase 2: Backend Entities & Services

### Entities to Create
1. `Domain.java`
2. `CareerRole.java` 
3. `DeepPath.java`
4. `DeepPathStep.java`

### Repositories
1. `DomainRepository`
2. `CareerRoleRepository`
3. `DeepPathRepository`
4. `DeepPathStepRepository`

### Service: `MultiLevelFrontierService.java`
Methods:
- `List<Domain> getAllDomains()`
- `List<CareerRole> getRolesByDomain(Integer domainId)`
- `DeepPath getDeepPathForRole(Integer careerRoleId, Integer userId)`
- `List<SkillNode> getSkillsForDeepPath(Integer deepPathId)`

### Controller: `MultiLevelFrontierController.java`
Endpoints:
- `GET /api/frontier/domains` - Get all domains
- `GET /api/frontier/domains/{domainId}/roles` - Get roles for domain
- `GET /api/frontier/roles/{roleId}/path?userId={userId}` - Get deep path
- `GET /api/frontier/path/{pathId}/skills` - Get skills for path

---

## Phase 3: Frontend Implementation

### State Management (in frontier/page.tsx)
```typescript
const [zoomLevel, setZoomLevel] = useState<1 | 2 | 3 | 4>(4); // Start at skills
const [selectedDomain, setSelectedDomain] = useState<number | null>(null);
const [selectedRole, setSelectedRole] = useState<number | null>(null);
const [selectedPath, setSelectedPath] = useState<number | null>(null);
```

### Components to Create
1. `DomainView.tsx` - Level 1: Shows 6-8 domain bubbles
2. `RoleView.tsx` - Level 2: Shows roles within selected domain
3. `PathView.tsx` - Level 3: Shows 12-week progression timeline
4. `SkillGraphView.tsx` - Level 4: Current React Flow graph (refactor existing)

### Zoom Controls Component
```typescript
<ZoomControls 
  currentLevel={zoomLevel}
  onZoomIn={() => setZoomLevel(prev => Math.min(4, prev + 1))}
  onZoomOut={() => setZoomLevel(prev => Math.max(1, prev - 1))}
  onBreadcrumbClick={(level) => setZoomLevel(level)}
/>
```

### Graph Rendering Logic
```typescript
const renderFrontierView = () => {
  switch(zoomLevel) {
    case 1: return <DomainView domains={domains} onSelectDomain={...} />;
    case 2: return <RoleView roles={roles} domainId={selectedDomain} onSelectRole={...} />;
    case 3: return <PathView path={deepPath} roleId={selectedRole} onSelectSkill={...} />;
    case 4: return <SkillGraphView nodes={nodes} edges={edges} />;
  }
};
```

---

## Phase 4: Data Seeding

### Seed Data Structure
```javascript
const SEED_DATA = {
  domains: [
    { name: "Backend Engineering", icon: "🔧", color: "#3b82f6" },
    { name: "Frontend Engineering", icon: "🎨", color: "#8b5cf6" },
    { name: "Cloud & DevOps", icon: "☁️", color: "#10b981" },
    { name: "Machine Learning", icon: "🤖", color: "#f59e0b" },
    { name: "Cybersecurity", icon: "🔒", color: "#ef4444" },
    { name: "Mobile Development", icon: "📱", color: "#06b6d4" }
  ],
  
  roles: {
    "Backend Engineering": [
      "Backend Engineer",
      "API Developer", 
      "Microservices Architect",
      "Database Engineer"
    ],
    "Cybersecurity": [
      "Security Engineer",
      "Cloud Security Specialist",
      "Penetration Tester",
      "Security Analyst"
    ]
    // ... etc
  },
  
  deepPaths: {
    "Backend Engineer": {
      weeks: [
        { week: 1, skills: ["REST APIs", "HTTP Fundamentals", "JSON"] },
        { week: 2, skills: ["Database Design", "SQL", "Transactions"] },
        // ... 12 weeks total
      ]
    }
  }
};
```

---

## Implementation Order (Step by Step)

1. ✅ Create database tables (SQL migration)
2. ✅ Create backend entities (Domain, CareerRole, DeepPath, DeepPathStep)
3. ✅ Create repositories
4. ✅ Seed initial data (domains, roles, sample paths)
5. ✅ Create MultiLevelFrontierService
6. ✅ Create API endpoints
7. ✅ Test backend with curl
8. ✅ Create frontend zoom state management
9. ✅ Create DomainView component
10. ✅ Create RoleView component
11. ✅ Create PathView component
12. ✅ Refactor existing graph into SkillGraphView
13. ✅ Integrate all 4 levels into frontier/page.tsx
14. ✅ Add zoom controls UI
15. ✅ Test complete flow: domain → role → path → skills

---

## Success Criteria
- ✅ User can see 6+ domains at level 1
- ✅ Clicking domain zooms to roles (level 2)
- ✅ Clicking role zooms to 12-week path (level 3)
- ✅ Clicking skill zooms to detailed skill graph (level 4)
- ✅ Breadcrumb navigation to go back up levels
- ✅ Smooth transitions between levels
- ✅ Current progress state preserved across zoom levels

