# CareerMap Pro - Resume Description

---

## 📄 Short Version (For Resume Bullet Points)

**CareerMap Pro** - AI-powered adaptive learning platform with personalized skill assessment and mastery tracking

- Engineered full-stack career development platform using **Next.js**, **Spring Boot**, and **MySQL** with OpenAI API integration for intelligent resource curation
- Implemented novel **EDLSG (Explore-Decide-Learn-Score-Grow)** learning methodology with adaptive assessments and dynamic skill graph visualization
- Built constellation-style skill tree interface with **React**, **Framer Motion**, and **Canvas API** featuring 35+ interconnected skill nodes with branching dependency paths
- Designed intelligent resource recommendation engine using collaborative filtering and user preference learning, achieving personalized content discovery
- Created multi-stage assessment system (Probe/Build/Prove) with automated grading pipelines and evidence-based skill verification

---

## 📝 Medium Version (For Portfolio/Project Description)

### CareerMap Pro - Intelligent Career Development Platform

**Duration**: November 2025 - January 2026
**Role**: Full-Stack Developer
**Tech Stack**: Next.js 16, Spring Boot 3.x, MySQL 8, OpenAI API, TypeScript, Java, React, Framer Motion

**Project Overview**:
CareerMap Pro is an AI-powered adaptive learning platform that revolutionizes career skill development through personalized learning paths, intelligent assessments, and dynamic progress tracking. The platform uses a novel "EDLSG" methodology (Explore-Decide-Learn-Score-Grow) to guide learners through structured skill acquisition.

**Key Technical Achievements**:

1. **Frontend Architecture**:
   - Built immersive constellation-style skill tree visualization using React, Framer Motion, and HTML5 Canvas
   - Implemented multi-level zoom interface (Domain → Role → Path → Node) with smooth camera transitions
   - Designed responsive node graph with depth-based layout algorithm supporting 35+ interconnected skills
   - Created spotlight mode for focused learning vs. exploratory full-map navigation

2. **Backend Systems**:
   - Developed RESTful API with Spring Boot handling path generation, assessment orchestration, and progress tracking
   - Designed normalized MySQL database schema with role-skill mappings, learning resources, and user progress tables
   - Implemented intelligent resource selection algorithm using scoring system based on user preferences and learning style
   - Integrated OpenAI GPT-4o-mini for dynamic resource discovery and content personalization

3. **Assessment Engine**:
   - Built three-tier assessment system: **PROBE** (knowledge verification), **BUILD** (project creation), **PROVE** (evidence submission)
   - Designed EDLSG phase tracking system to manage learner progression through skill nodes
   - Created dependency-based skill unlocking system preventing premature advancement

4. **Resource Recommendation**:
   - Implemented ML-inspired collaborative filtering for resource personalization
   - Built rating system with implicit (time spent, completion) and explicit (thumbs up/down) feedback
   - Integrated real-time resource validation with URL verification and source credibility scoring

**Impact**:
- Supports 3 comprehensive career tracks (Backend Engineer, Frontend Developer, Security Engineer)
- Manages 35+ skill nodes per track with branching dependencies and parallel learning paths
- Delivers personalized learning resources from 10+ reputable sources (MDN, freeCodeCamp, YouTube, etc.)
- Enables evidence-based skill verification and mastery tracking

---

## 📖 Long Version (For GitHub README / Portfolio Case Study)

# CareerMap Pro - Intelligent Career Development Platform

## 🎯 Project Vision

CareerMap Pro reimagines career skill development by transforming traditional linear learning paths into an interactive, game-like experience. Inspired by skill trees in RPGs and the adaptive learning principles of Bloom's Taxonomy, the platform provides a structured yet flexible approach to mastering complex career skills.

## 🏗️ System Architecture

### High-Level Overview
```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Next.js 16    │────▶│  Spring Boot 3.x  │────▶│   MySQL 8.0     │
│   (Frontend)    │◀────│    (Backend)      │◀────│   (Database)    │
└─────────────────┘     └──────────────────┘     └─────────────────┘
         │                       │
         │                       ▼
         │              ┌──────────────────┐
         │              │   OpenAI API     │
         │              │   (GPT-4o-mini)  │
         │              └──────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│  Framer Motion + Canvas API (Visualization) │
└─────────────────────────────────────────────┘
```

### Technology Stack

**Frontend**:
- **Framework**: Next.js 16 with React 19 and TypeScript
- **Animation**: Framer Motion for smooth transitions and micro-interactions
- **Rendering**: HTML5 Canvas API for constellation visualization
- **Styling**: Tailwind CSS with custom gradient design system
- **State Management**: React hooks (useState, useEffect) with local state

**Backend**:
- **Framework**: Spring Boot 3.x (Java 17)
- **API**: RESTful architecture with JSON payloads
- **ORM**: Spring Data JPA with Hibernate
- **Services**: Modular service layer (OpenAIService, ResourceSelectionService, CoreLoopService)
- **Database**: MySQL 8.0 with normalized schema

**AI Integration**:
- **Provider**: OpenAI GPT-4o-mini API
- **Use Cases**:
  - Dynamic resource discovery
  - Skill assessment question generation
  - Personalized learning explanations
  - Content summarization

## 🔧 Technical Implementation

### 1. Constellation Visualization System

**Challenge**: Create an intuitive, visually engaging skill tree that shows dependencies, progress, and paths.

**Solution**:
- Implemented depth-based layout algorithm calculating node positions based on dependency chains
- Used Framer Motion for smooth zoom/pan animations and state transitions
- Created spotlight mode that highlights current node + prerequisites + immediate unlocks
- Designed responsive viewport with camera controls (zoom, pan, center, reset)

**Key Code Features**:
```typescript
// Depth-based positioning with huge vertical spread
const calculateDepth = (nodeId: number): number => {
  // Recursively calculates longest dependency path
  return maxDepth(dependencies) + 1;
};

// Nodes positioned at:
// x = level * 500px (horizontal progression)
// y = verticalSpacing * 300px (prevents overlap)
```

### 2. EDLSG Methodology Implementation

**EDLSG Phases**:
1. **Explore**: User views skill node and learning resources
2. **Decide**: User commits to learning the skill
3. **Learn**: User engages with resources and materials
4. **Score**: User takes assessment (Probe/Build/Prove)
5. **Grow**: User reflects on mastery and unlocks next skills

**Technical Implementation**:
- Map-based phase tracking: `Map<skillNodeId, edlsgPhase>`
- Phase-triggered resource loading and UI state changes
- Assessment type routing based on skill characteristics

### 3. Intelligent Resource Selection Engine

**Algorithm**:
```
resourceScore =
  (qualityScore * 0.3) +        // Source credibility
  (typeMatchScore * 0.25) +     // User's preferred types
  (durationMatchScore * 0.2) +  // User's time availability
  (recencyScore * 0.15) +       // How recent the resource is
  (userRatingScore * 0.1)       // Explicit user feedback
```

**Features**:
- Collaborative filtering based on similar users' ratings
- Real-time URL validation to prevent dead links
- Source reputation scoring (official docs > blog posts)
- User preference learning (video vs. article vs. interactive)

### 4. Assessment System Architecture

**Three Assessment Types**:

**PROBE** (Knowledge Check):
- Multiple-choice and short-answer questions
- Automatically generated by OpenAI based on skill content
- Instant grading with feedback
- Determines if user should LEARN or proceed to BUILD

**BUILD** (Project Creation):
- User builds a project demonstrating the skill
- Submission includes code repository link or live demo
- Manual or AI-assisted code review
- Validates practical application ability

**PROVE** (Evidence Submission):
- User submits proof of real-world skill application
- Can be portfolio work, open-source contributions, or professional experience
- Evidence-based verification with peer or mentor review

### 5. Database Schema Design

**Core Tables**:
- `skill_nodes`: All available skills with metadata
- `career_roles`: Job titles and descriptions
- `role_skill`: Many-to-many mapping with weight scores
- `learning_resources`: Curated resources per skill
- `user_progress`: Completed skills and EDLSG phase
- `assessment_attempts`: Quiz results and BUILD submissions
- `resource_ratings`: User feedback for personalization

**Key Relationships**:
```sql
skill_nodes (1) ─── (N) dependencies (N) ─── (1) skill_nodes
skill_nodes (1) ─── (N) learning_resources
career_roles (1) ─── (N) role_skill (N) ─── (1) skill_nodes
users (1) ─── (N) user_progress (N) ─── (1) skill_nodes
```

## 🎨 UI/UX Design Philosophy

1. **Game-Inspired**: Skill trees feel like RPG progression systems
2. **Visual Feedback**: Nodes pulse, glow, and change colors based on state
3. **Minimal Friction**: One-click actions, smooth transitions, no page reloads
4. **Information Hierarchy**: Focus mode isolates critical information
5. **Progressive Disclosure**: Details appear only when needed

## 📊 Features by User Journey

### 1. Onboarding (Calibration Phase)
- User takes 10-question calibration quiz
- System identifies strong skills, gaps, and unknowns
- AI recommends optimal starting domain and role

### 2. Path Selection
- Domain selection from star map (Web Dev, Data, Security, etc.)
- Role selection within domain (Backend, Frontend, Full-Stack)
- Path generation with 35+ personalized skill nodes

### 3. Learning Experience
- Click node to enter Explore phase
- View curated resources (videos, articles, docs)
- Rate resources to improve future recommendations
- Decide to commit and start learning

### 4. Assessment Flow
- Complete PROBE to verify knowledge
- Build project for BUILD assessment
- Submit evidence for PROVE verification
- Receive instant feedback and unlock next skills

### 5. Progress Tracking
- Visual completion indicators on nodes
- Skill mastery percentage
- EDLSG phase badges
- Achievement system (future)

## 🚀 Performance Optimizations

1. **Lazy Loading**: Resources fetched only when node is selected
2. **Caching**: OpenAI responses cached to prevent redundant API calls
3. **Debouncing**: Pan/zoom events debounced for smooth performance
4. **Code Splitting**: Next.js automatic code splitting by page
5. **Database Indexing**: Optimized queries with indexes on foreign keys

## 🧪 Testing Strategy

1. **Manual Testing**: Full user flow testing for all 3 career tracks
2. **Backend Testing**: API endpoint verification with Postman
3. **Database Testing**: SQL query performance profiling
4. **UI Testing**: Visual regression testing for constellation layout
5. **Integration Testing**: End-to-end flow from calibration to assessment

## 📈 Future Enhancements

1. **AI Tutor**: ChatGPT-style Q&A for each skill concept
2. **Peer Collaboration**: Pair programming and project reviews
3. **Mentor Matching**: Connect learners with industry professionals
4. **Mobile App**: React Native version for on-the-go learning
5. **Gamification**: XP points, achievements, leaderboards
6. **Social Learning**: Share progress, celebrate milestones
7. **Career Analytics**: Job market demand tracking per skill

## 🎓 Learning Outcomes

By building CareerMap Pro, I developed expertise in:

**Frontend**:
- Advanced React patterns (custom hooks, context, optimization)
- Animation libraries and smooth UX transitions
- Canvas rendering and graph visualization algorithms
- TypeScript for type-safe component development

**Backend**:
- RESTful API design with Spring Boot
- JPA/Hibernate for database ORM
- Service layer architecture and dependency injection
- OpenAI API integration and prompt engineering

**System Design**:
- Multi-tier application architecture
- Database normalization and query optimization
- Algorithm design for graph traversal and layout
- Recommendation system implementation

**DevOps**:
- Local development environment setup
- MySQL database management
- API testing and debugging
- Git version control

## 📞 Contact & Links

- **GitHub**: [github.com/yourusername/careermap](https://github.com)
- **Live Demo**: Coming soon
- **Documentation**: Available in repository
- **Contact**: your.email@example.com

---

**Built with ❤️ by [Your Name]**
**January 2026**
