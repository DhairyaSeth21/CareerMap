# CAREERMAP - COMPLETE APPLICATION DOCUMENTATION

**Generated:** January 8, 2026
**Version:** Current State Analysis

---

## TABLE OF CONTENTS
1. [Application Overview](#1-application-overview)
2. [Technology Stack](#2-technology-stack)
3. [Complete Feature List](#3-complete-feature-list)
4. [Data Flow Diagrams](#4-data-flow)
5. [Database Schema](#5-database-schema)
6. [API Endpoints](#6-api-endpoints)
7. [File Structure](#7-file-structure)
8. [Current Issues](#8-current-issues)

---

## 1. APPLICATION OVERVIEW

**CareerMap** is an AI-powered career development and skill tracking platform designed to help professionals plan their career paths, assess their skills, and track their learning progress.

### Main Purpose
- Help users identify and set clear career goals
- Assess current skill levels through AI-generated quizzes
- Visualize learning paths with dependency-based skill maps
- Analyze job opportunities against user skills
- Provide AI-powered career coaching and recommendations
- Track progress with analytics, trends, and achievements

### Target Users
- Software engineers planning career growth
- Professionals transitioning between roles
- Developers tracking skill acquisition
- Anyone wanting structured career development

---

## 2. TECHNOLOGY STACK

### Frontend Technologies
- **Next.js 16.0.3** - React framework for production
- **React 19.2.0** - UI library
- **TypeScript 5.x** - Type-safe JavaScript
- **Tailwind CSS 4.x** - Utility-first CSS framework
- **Axios 1.13.2** - HTTP client for API calls
- **ReactFlow 11.11.4** - Interactive skill map visualization
- **Recharts 3.6.0** - Analytics charts
- **next-themes 0.4.6** - Dark/light theme support

### Backend Technologies
- **Spring Boot 3.x** - Java framework
- **Java 17+** - Programming language
- **Spring Data JPA** - Database access
- **MySQL** - Relational database
- **Hibernate** - ORM framework

### External APIs
- **OpenAI GPT-3.5** - Powers AI features (quiz generation, job analysis, coaching, recommendations)

---

## 3. COMPLETE FEATURE LIST

### 3.1 User Authentication ✅
**Status:** Working

**Features:**
- User registration with name, email, password
- Email/password login
- Session management via localStorage
- Profile persistence

**Pages:**
- `/login` - Login page
- `/signup` - Registration page

**API Endpoints:**
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/user/{userId}`

---

### 3.2 Dashboard (Home Page) ✅
**Status:** Working
**Location:** `/` (authenticated home)

**Features:**
- Personalized welcome message
- Primary career goal display with readiness score
- Statistics overview cards:
  - Average proficiency across all skills
  - Advanced skills count (7+ proficiency)
  - In-progress skills count (4-7 proficiency)
  - Total career goals
- Top 5 skills with proficiency bars
- All career goals grid view
- Quick action buttons

**Data Sources:**
- User proficiencies from `/api/proficiencies/{userId}`
- Career goals from `/api/goals/{userId}`

**Design:** Clean card-based layout with accent color highlights

---

### 3.3 Career Goals Management ✅
**Status:** Working
**Location:** `/goals`

**Features:**
- 17 predefined career paths:
  1. Senior Software Engineer
  2. Backend Engineer
  3. Full Stack Developer
  4. DevOps Engineer
  5. Frontend Specialist
  6. ML Engineer
  7. Data Engineer
  8. Mobile Developer
  9. Security Engineer
  10. Cloud Architect
  11. Platform Engineer
  12. QA Engineer
  13. Systems Engineer
  14. API Engineer
  15. Technical Lead
  16. Product Engineer
  17. Solutions Architect

**Each Career Path Includes:**
- Icon and category badge
- Required skills list
- Recommended skills list
- 4 key milestones with descriptions
- Estimated timeline
- Real-time readiness calculation (0-10 scale)

**Goal Management:**
- Add new goals from predefined list
- Set primary goal
- View detailed skill breakdown
- Delete goals
- Automatic readiness calculation

**Readiness Calculation:**
- Based on user's current skill proficiencies
- Weighted average of required skills
- Updates in real-time as skills improve

**API Endpoints:**
- `GET /api/goals/{userId}` - Fetch all goals
- `POST /api/goals` - Create new goal
- `DELETE /api/goals/{goalId}` - Remove goal
- `POST /api/goals/{goalId}/set-primary` - Set primary goal

**Design:** Grid layout with detailed modal for each career path

---

### 3.4 Skill Assessment & Progress Tracking ✅
**Status:** Partially Working (AI quiz generation requires valid OpenAI key)
**Location:** `/progress`

**Features:**

#### AI-Powered Quiz System
- Generate 10-question quizzes per skill
- 4 difficulty levels: Beginner, Intermediate, Advanced, Expert
- AI-generated questions via OpenAI GPT-3.5
- Multiple choice format (4 options: A, B, C, D)
- Real-time progress tracking during quiz
- Timer tracking
- Automatic grading

#### Quiz Results
- Score percentage calculation
- Correct/incorrect breakdown
- Proficiency awarded (0-10 scale, weighted by difficulty)
- Time taken display
- Performance breakdown by subtopic
- XP rewards

#### Proficiency System
- 0-10 scale proficiency tracking
- Automatic updates based on quiz performance
- Historical tracking of all attempts
- Difficulty progression

#### Skill Categories
Skills organized by:
- Programming Languages (JavaScript, Python, Java, Go, etc.)
- Frontend (React, Vue, Angular, CSS, etc.)
- Backend (Node.js, Spring Boot, Django, etc.)
- Databases (PostgreSQL, MongoDB, Redis, etc.)
- DevOps (Docker, Kubernetes, CI/CD, etc.)
- Cloud (AWS, Azure, GCP)
- Security & Testing

**API Endpoints:**
- `POST /api/quizzes/generate` - Generate AI quiz
- `POST /api/quizzes/{quizId}/submit` - Submit answers
- `GET /api/quizzes/user/{userId}` - Get quiz history
- `GET /api/quizzes/user/{userId}/skill/{skillName}` - Skill-specific history
- `GET /api/skills/catalog` - Browse available skills

**Current Issue:** Requires valid OpenAI API key for quiz generation

**Design:** Tabbed interface with quiz selection, active quiz, and results views

---

### 3.5 Interactive Skill Map 🎯
**Status:** Working
**Location:** `/skillmap`

**Features:**

#### Visual Learning Path
- ReactFlow-based interactive node graph
- Skill dependency visualization
- Prerequisite relationships shown as arrows
- Drag, zoom, and pan controls
- MiniMap for navigation

#### Skill Status System
Skills color-coded by status:
- **COMPLETED** (Green) - Mastered skills (proficiency ≥7)
- **IN_PROGRESS** (Blue) - Currently learning (4-7 proficiency)
- **NEXT_UP** (Orange) - Ready to start (prerequisites met)
- **BLOCKED** (Gray) - Prerequisites not met
- **MISSING_PREREQS** (Red) - Missing dependencies

#### Features
- Goal selector dropdown
- View filters (all, active, completed, blocked)
- Skill detail side panel
- Progress vs target indicators
- Statistics dashboard:
  - Total skills count
  - Completed count
  - In-progress count
  - Ready to start count
  - Overall completion percentage

#### AI Recommendations
- Suggested focus areas based on goal
- Next skills to prioritize

**API Endpoints:**
- `GET /api/skillmap/user/{userId}` - Get skill graph
- `GET /api/skillmap/learning-path?goal={goal}&userId={userId}` - Get learning path

**Design:** Full-screen interactive graph with controls and stats overlay

---

### 3.6 Analytics & Trends 📊
**Status:** Working
**Location:** `/trends`

**Features:**

#### Gap Analysis Chart
- Bar chart comparing 3 levels per skill:
  - Current proficiency (blue)
  - Target proficiency (green)
  - Market average (gray)
- Visual identification of skill gaps

#### Priority Skills List
- Top 5 skills with largest gaps
- Current vs target display
- Progress bars
- Actionable focus areas

#### Radar Chart
- Multi-dimensional visualization
- Top 6 skills comparison
- Quick skill profile overview

#### Category Breakdown
- Horizontal bar chart
- Skills acquired vs total by category
- Progress tracking by domain

#### Statistics Cards
- Total skills tracked
- Advanced skills count (≥7 proficiency)
- Average proficiency level
- Overall mastery percentage

**API Endpoints:**
- `GET /api/proficiencies/{userId}` - All user proficiencies
- `GET /api/skillmap/user/{userId}` - Skill graph data
- `GET /api/goals/{userId}` - Career goals

**Design:** Dashboard-style with Recharts visualizations

---

### 3.7 AI Career Coach 🤖
**Status:** Requires Valid OpenAI Key
**Location:** `/coach`

**Features:**

#### Chatbot Interface
- Conversational AI powered by OpenAI GPT-3.5
- Real-time message streaming
- Message history with timestamps
- User and AI message bubbles
- Typing indicators

#### Suggested Questions
Predefined prompts:
- "How do I transition from frontend to full-stack development?"
- "What skills should I focus on to become a senior engineer?"
- "How can I build a strong portfolio as a developer?"
- "What should I prepare for technical interviews?"

#### Coaching Topics
- Strategic career planning
- Skill gap analysis
- Technical interview preparation
- Resume optimization
- Salary negotiation
- Learning path guidance

**API Endpoints:**
- `POST /api/ai/chat` - Send message to AI coach

**Current Issue:** Returns error without valid OpenAI API key

**Design:** Modern chat interface with minimal, professional styling

---

### 3.8 Job Description Analyzer 🔍
**Status:** Requires Valid OpenAI Key
**Location:** `/analyze`

**Features:**

#### AI-Powered Job Matching
- Paste job description
- AI analyzes requirements vs your skills
- Match percentage calculation (0-100%)
- Detailed breakdown

#### Analysis Output
- Overall match score with color coding:
  - 80-100%: Excellent Match (green)
  - 60-79%: Good Match (blue)
  - Below 60%: Needs Work (orange)
- Matched skills list (skills you have)
- Skill gaps list (skills you're missing)
- Personalized recommendations
- Actionable next steps

**API Endpoints:**
- `POST /api/ai/analyze-job` - Analyze job description

**Current Issue:** Requires valid OpenAI API key

**Design:** Clean input area with formatted results cards

---

### 3.9 Achievements & Gamification 🏆
**Status:** Working
**Location:** `/achievements`

**Features:**

#### XP System
- Earn XP from activities (quizzes, goals, streaks)
- Level progression (100 XP per level)
- XP to next level tracking
- Progress bar visualization

#### Daily Streaks
- Login streak counter
- Streak-based achievements
- Streak display on dashboard

#### Achievement Badges (8 Total)
1. **First Steps** (🎯) - Complete first quiz (50 XP)
2. **Knowledge Seeker** (📚) - Complete 5 quizzes (100 XP)
3. **Skill Collector** (⭐) - Track 10 skills (150 XP)
4. **Week Warrior** (🔥) - 7-day streak (200 XP)
5. **Goal Setter** (🎖️) - Set first career goal (50 XP)
6. **Master** (💎) - Reach 90% proficiency in any skill (300 XP)
7. **Quiz Champion** (🏆) - Complete 50 quizzes (500 XP)
8. **Consistency King** (💪) - 30-day streak (1000 XP)

#### Leaderboard
- Global rankings by XP
- Weekly leaderboard
- User position highlighted
- XP and streak comparison
- Top 3 medal indicators

#### Achievement Tracking
- Locked vs unlocked badges
- Progress bars for partial completion
- Achievement categories (quiz, skill, streak, goal)
- Unlock timestamps

**API Endpoints:**
- `GET /api/gamification/user/{userId}` - Get user gamification data
- `GET /api/gamification/leaderboard` - Get global leaderboard
- `POST /api/gamification/update-xp/{userId}` - Award XP
- `POST /api/gamification/update-streak/{userId}` - Update streak

**Database Tables:**
- `achievements` - Achievement definitions
- `user_achievements` - User unlocks and progress

**Design:** Tabbed interface (Overview, Badges, Leaderboard)

---

### 3.10 Learning Resources 📚
**Status:** Partially Working
**Location:** `/resources`

**Features:**

#### Resource Catalog
Currently shows hardcoded sample resources:
- Courses (Udemy, Coursera, etc.)
- Articles (Medium, Dev.to)
- Videos (YouTube)
- Projects (GitHub)

#### Filtering System
- By skill (React, Python, Docker, etc.)
- By resource type (course/article/video/project)
- By difficulty level (beginner/intermediate/advanced)

#### Resource Details
Each resource shows:
- Title and description
- Platform
- Duration estimate
- Difficulty level
- Rating (if available)
- Free vs paid indicator
- Clickable links

#### AI Recommendations
- "Get AI Recommendations" button
- Personalized learning path suggestions
- Industry trends
- Must-know technologies
- Practical project ideas
- Context-aware based on selected filters

**API Endpoints:**
- `POST /api/resources/generate` - Generate AI recommendations

**Current Issue:**
- Main resources are hardcoded (sample data)
- AI recommendations require valid OpenAI key

**Design:** Grid layout with filter sidebar

---

### 3.11 Landing Page 🚀
**Status:** Working
**Location:** `/landing`

**Features:**

#### Hero Section
- Main value proposition
- CTA buttons (Sign Up, See How It Works)
- Stats showcase (10,000+ skills, 50+ career paths, AI-powered)
- Gradient background

#### Features Showcase
6 key feature cards:
1. Smart Career Goals
2. Skill Analytics
3. Learning Roadmap
4. Job Matching
5. Quiz Assessment
6. Personalized Insights

#### How It Works Section
4-step process with mockups:
1. Set your career goals
2. Assess your skills
3. Get personalized roadmap
4. Track your progress

#### Skill Map Showcase
- Large interactive preview
- Feature highlights
- Dependency mapping demo

#### Footer
- Product links
- Company info
- Resources
- Copyright

**Design:** Modern, gradient-heavy design with animations

---

### 3.12 Theme System 🎨
**Status:** Working
**Location:** All pages

**Features:**
- Light and dark mode support
- System preference detection
- Manual toggle in navbar
- Persistent theme storage
- CSS custom properties for theming
- Smooth transitions

**Colors:**
- Light mode: White background, dark text
- Dark mode: Dark background, light text
- Accent color: Purple/blue gradient

---

## 4. DATA FLOW

### 4.1 User Registration Flow
```
User fills form (name, email, password)
  ↓
Frontend: POST /api/auth/register
  ↓
AuthController.register()
  ↓
AuthService validates & creates user
  ↓
Database: INSERT into users (xp=0, level=1, streak=0)
  ↓
Response: { success, userId, email, name }
  ↓
Frontend: Store in localStorage, redirect to dashboard
```

### 4.2 Quiz Generation Flow
```
User selects skill + difficulty + clicks "Generate Quiz"
  ↓
Frontend: POST /api/quizzes/generate
  Body: { userId, skillName, difficulty, numQuestions }
  ↓
QuizController.generateQuiz()
  ↓
QuizService.generateQuiz()
  ↓
GPTService calls OpenAI API
  ↓
OpenAI generates 10 questions with 4 options each
  ↓
Database: INSERT into quizzes (status=PENDING)
          INSERT 10 rows into quiz_questions
  ↓
Response: Quiz object with all questions
  ↓
Frontend: Display quiz interface
```

### 4.3 Quiz Submission Flow
```
User answers questions + clicks "Submit"
  ↓
Frontend: POST /api/quizzes/{quizId}/submit
  Body: { answers: { questionId: "A" }, timeTaken }
  ↓
QuizController.submitQuiz()
  ↓
QuizService grades answers
  ↓
Calculate score percentage
  ↓
Calculate proficiency (weighted by difficulty):
  - Beginner: score * 0.3
  - Intermediate: score * 0.5
  - Advanced: score * 0.7
  - Expert: score * 1.0
  ↓
Database: UPDATE quizzes SET score, proficiency, status=COMPLETED
          UPDATE/INSERT proficiencies
          UPDATE users SET xp, level
  ↓
Response: { score, breakdown, proficiencyAwarded, xpEarned }
  ↓
Frontend: Display results screen
```

### 4.4 Goal Creation Flow
```
User selects career path
  ↓
Frontend: POST /api/goals
  Body: { userId, goal }
  ↓
CareerGoalController.createGoal()
  ↓
CareerGoalService.createGoal()
  ↓
Get predefined career path requirements
  ↓
Get user's current proficiencies
  ↓
Calculate readiness:
  - For each required skill:
    * If user has it: contribute proficiency to average
    * If missing: contribute 0
  - Weighted average = total / required count
  ↓
Database: INSERT into career_goals
          If first goal: set isPrimary=true
  ↓
Response: Goal with readiness score
  ↓
Frontend: Update goals list
```

### 4.5 Skill Map Generation Flow
```
User navigates to /skillmap
  ↓
Frontend: GET /api/goals/{userId}
          GET /api/skillmap/learning-path?goal={goal}&userId={userId}
  ↓
SkillMapController.getLearningPath()
  ↓
LearningPathService.buildPath()
  ↓
Get all skills required for selected goal
  ↓
Get user's current proficiencies
  ↓
For each skill, determine status:
  - Proficiency ≥7 → COMPLETED
  - Proficiency 4-7 → IN_PROGRESS
  - Proficiency <4 && prerequisites met → NEXT_UP
  - Proficiency <4 && prerequisites not met → BLOCKED
  - Not assessed → MISSING_PREREQS
  ↓
Database: SELECT from skills
          SELECT from proficiencies WHERE userId
          SELECT from skill_dependencies
  ↓
Response: {
  completedSkills,
  inProgressSkills,
  nextUpSkills,
  blockedSkills,
  stats,
  recommendations
}
  ↓
Frontend: ReactFlow renders nodes & edges
          Color-code by status
          Draw dependency arrows
```

### 4.6 AI Coach Chat Flow
```
User types message + clicks "Send"
  ↓
Frontend: POST /api/ai/chat
  Body: { prompt: "You are CareerPro AI... [message]" }
  ↓
AIController.chat()
  ↓
GPTService.generateText()
  ↓
OpenAI API call (GPT-3.5-turbo)
  ↓
AI generates career advice response
  ↓
Response: { response: "AI message" }
  ↓
Frontend: Display in chat interface with timestamp
```

### 4.7 Job Analysis Flow
```
User pastes job description + clicks "Analyze"
  ↓
Frontend: POST /api/ai/analyze-job
  Body: { userId, jobDescription }
  ↓
AIController.analyzeJob()
  ↓
RoleMatcherService.analyzeJobDescription()
  ↓
Get user's proficiencies from database
  ↓
Format prompt for OpenAI:
  "User has skills: [list]
   Job requires: [job description]
   Analyze match, identify gaps, provide recommendations"
  ↓
GPTService calls OpenAI
  ↓
AI analyzes and generates:
  - Match percentage
  - Matched skills
  - Missing skills
  - Recommendations
  ↓
Response: { result: "AI analysis" }
  ↓
Frontend: Parse and display formatted results
```

### 4.8 Achievements Flow
```
User navigates to /achievements
  ↓
Frontend: GET /api/gamification/user/{userId}
  ↓
GamificationController.getUserGamificationData()
  ↓
Fetch from database:
  - User (xp, level, streak)
  - All achievements definitions
  - User's unlocked achievements
  ↓
For each achievement, calculate progress:
  - quiz achievements: count user's quizzes
  - skill achievements: count user's skills
  - streak achievements: check user's streak
  - goal achievements: count user's goals
  ↓
Response: {
  xp, level, streak,
  achievements: [{ id, name, icon, unlocked, progress }]
}
  ↓
Frontend: Display stats + achievement cards
```

---

## 5. DATABASE SCHEMA

### users
| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| id | INT | NO | AUTO_INCREMENT | Primary key |
| name | VARCHAR(255) | NO | - | User's full name |
| email | VARCHAR(255) | NO | - | Unique email |
| password | VARCHAR(255) | NO | - | Hashed password |
| xp | INT | YES | 0 | Experience points |
| level | INT | YES | 1 | User level |
| streak | INT | YES | 0 | Daily login streak |

**Indexes:** UNIQUE(email)

---

### proficiencies
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| user_id | INT | NO | Foreign key → users.id |
| skill | VARCHAR(255) | NO | Skill name |
| proficiency | DOUBLE | NO | Proficiency level (0-10) |

**Foreign Keys:** user_id → users.id

---

### career_goals
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| user_id | INT | NO | Foreign key → users.id |
| goal | VARCHAR(255) | NO | Goal title |
| readiness | DOUBLE | YES | Readiness score (0-10) |
| skill | VARCHAR(255) | YES | Associated skill (optional) |
| weight | DOUBLE | YES | Weight for calculation |
| is_primary | BOOLEAN | YES | Is this primary goal? |

**Foreign Keys:** user_id → users.id

---

### skills
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| skill_id | INT | NO | Primary key (AUTO_INCREMENT) |
| name | VARCHAR(255) | NO | Skill name (UNIQUE) |
| category | VARCHAR(255) | YES | Skill category |
| description | TEXT | YES | Skill description |
| difficulty_level | ENUM | YES | Beginner/Intermediate/Advanced/Expert |
| created_at | TIMESTAMP | YES | Creation timestamp |
| updated_at | TIMESTAMP | YES | Last update timestamp |

**Indexes:** UNIQUE(name)

---

### skill_dependencies
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| skill_id | INT | NO | Foreign key → skills.skill_id |
| prerequisite_skill_id | INT | NO | Foreign key → skills.skill_id |
| type | VARCHAR(255) | YES | Dependency type |

**Foreign Keys:**
- skill_id → skills.skill_id
- prerequisite_skill_id → skills.skill_id

---

### quizzes
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| quiz_id | INT | NO | Primary key (AUTO_INCREMENT) |
| user_id | INT | NO | Foreign key → users.id |
| skill_name | VARCHAR(255) | NO | Skill being assessed |
| difficulty | ENUM | NO | Beginner/Intermediate/Advanced/Expert |
| num_questions | INT | NO | Number of questions |
| score | DOUBLE | YES | Score percentage (0-100) |
| proficiency_awarded | DOUBLE | YES | Proficiency awarded (0-10) |
| status | ENUM | NO | PENDING/IN_PROGRESS/COMPLETED/ABANDONED |
| created_at | TIMESTAMP | YES | Creation timestamp |
| completed_at | TIMESTAMP | YES | Completion timestamp |
| time_taken_seconds | INT | YES | Time taken |

**Foreign Keys:** user_id → users.id

---

### quiz_questions
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| question_id | INT | NO | Primary key (AUTO_INCREMENT) |
| quiz_id | INT | NO | Foreign key → quizzes.quiz_id |
| question_number | INT | NO | Question order (1-10) |
| question_text | TEXT | NO | Question content |
| option_a | TEXT | NO | Option A |
| option_b | TEXT | NO | Option B |
| option_c | TEXT | NO | Option C |
| option_d | TEXT | NO | Option D |
| correct_answer | CHAR(1) | NO | Correct option (A/B/C/D) |
| subtopic | VARCHAR(255) | YES | Question subtopic |

**Foreign Keys:** quiz_id → quizzes.quiz_id (CASCADE DELETE)

---

### achievements
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| name | VARCHAR(255) | NO | Achievement name |
| description | VARCHAR(255) | NO | Achievement description |
| icon | VARCHAR(255) | YES | Emoji or icon |
| xp_reward | INT | YES | XP awarded when unlocked |
| category | VARCHAR(255) | YES | goal/quiz/streak/skill |
| unlock_criteria | VARCHAR(255) | YES | Criteria to unlock |

**Initial Data:** 8 predefined achievements seeded on startup

---

### user_achievements
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| user_id | INT | NO | Foreign key → users.id |
| achievement_id | INT | NO | Foreign key → achievements.id |
| unlocked_at | TIMESTAMP | YES | When unlocked |
| progress | INT | YES | Progress toward achievement |

**Foreign Keys:**
- user_id → users.id
- achievement_id → achievements.id

---

### user_skills
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| user_id | INT | NO | Foreign key → users.id |
| skill_id | INT | NO | Foreign key → skills.skill_id |
| proficiency_level | DOUBLE | YES | User's proficiency (0-10) |
| acquired_at | TIMESTAMP | YES | When skill was acquired |

**Foreign Keys:**
- user_id → users.id
- skill_id → skills.skill_id

---

### analytics_snapshot
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| user_id | INT | NO | Foreign key → users.id |
| readiness | DOUBLE | YES | Overall readiness snapshot |
| timestamp | TIMESTAMP | YES | Snapshot timestamp |

**Foreign Keys:** user_id → users.id

---

### readiness_trend
| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| id | INT | NO | Primary key (AUTO_INCREMENT) |
| user_id | INT | NO | Foreign key → users.id |
| domain | VARCHAR(255) | YES | Skill domain/category |
| score | DOUBLE | YES | Readiness score for domain |
| timestamp | TIMESTAMP | YES | Trend data timestamp |

**Foreign Keys:** user_id → users.id

---

## 6. API ENDPOINTS

### Authentication (`/api/auth`)

#### POST /api/auth/register
Register new user

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "userId": 1,
  "email": "john@example.com",
  "name": "John Doe",
  "message": "Registration successful"
}
```

---

#### POST /api/auth/login
User login

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "userId": 1,
  "email": "john@example.com",
  "name": "John Doe",
  "message": "Login successful"
}
```

---

#### GET /api/auth/user/{userId}
Get user details

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "xp": 1250,
  "level": 5,
  "streak": 7
}
```

---

### Proficiency (`/api/proficiencies`)

#### GET /api/proficiencies/{userId}
Get all user proficiencies

**Response:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "skill": "React",
    "proficiency": 8.5
  },
  {
    "id": 2,
    "userId": 1,
    "skill": "Python",
    "proficiency": 6.2
  }
]
```

---

#### POST /api/proficiencies
Create or update proficiency

**Request Body:**
```json
{
  "userId": 1,
  "skill": "React",
  "proficiency": 8.5
}
```

---

### Career Goals (`/api/goals`)

#### GET /api/goals/{userId}
Get all user career goals

**Response:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "goal": "Senior Software Engineer",
    "readiness": 7.8,
    "isPrimary": true
  }
]
```

---

#### POST /api/goals
Create new career goal

**Request Body:**
```json
{
  "userId": 1,
  "goal": "Full Stack Developer"
}
```

**Response:**
```json
{
  "id": 2,
  "userId": 1,
  "goal": "Full Stack Developer",
  "readiness": 5.4,
  "isPrimary": false
}
```

---

#### DELETE /api/goals/{goalId}
Delete career goal

**Response:**
```json
{
  "message": "Goal deleted successfully"
}
```

---

#### POST /api/goals/{goalId}/set-primary
Set goal as primary

**Query Parameters:**
- `userId` (required)

**Response:**
```json
{
  "id": 2,
  "userId": 1,
  "goal": "Full Stack Developer",
  "readiness": 5.4,
  "isPrimary": true
}
```

---

### Quizzes (`/api/quizzes`)

#### POST /api/quizzes/generate
Generate AI quiz

**Request Body:**
```json
{
  "userId": 1,
  "skillName": "React",
  "difficulty": "INTERMEDIATE",
  "numQuestions": 10
}
```

**Response:**
```json
{
  "quizId": 45,
  "userId": 1,
  "skillName": "React",
  "difficulty": "INTERMEDIATE",
  "numQuestions": 10,
  "status": "PENDING",
  "questions": [
    {
      "questionId": 450,
      "questionNumber": 1,
      "questionText": "What is the purpose of useEffect hook?",
      "optionA": "To manage state",
      "optionB": "To handle side effects",
      "optionC": "To create components",
      "optionD": "To render JSX",
      "subtopic": "React Hooks"
    }
  ]
}
```

---

#### POST /api/quizzes/{quizId}/submit
Submit quiz answers

**Request Body:**
```json
{
  "answers": {
    "450": "B",
    "451": "A",
    "452": "C"
  },
  "timeTaken": 420
}
```

**Response:**
```json
{
  "score": 85.0,
  "correctCount": 8,
  "totalQuestions": 10,
  "proficiencyAwarded": 4.25,
  "xpEarned": 50,
  "breakdown": [
    {
      "subtopic": "React Hooks",
      "correct": 3,
      "total": 4
    }
  ]
}
```

---

#### GET /api/quizzes/user/{userId}
Get user quiz history

**Response:**
```json
[
  {
    "quizId": 45,
    "skillName": "React",
    "difficulty": "INTERMEDIATE",
    "score": 85.0,
    "proficiencyAwarded": 4.25,
    "completedAt": "2026-01-08T10:30:00",
    "timeTakenSeconds": 420
  }
]
```

---

#### GET /api/quizzes/user/{userId}/skill/{skillName}
Get quiz history for specific skill

---

### Skills (`/api/skills`)

#### GET /api/skills/catalog
Get all skills in catalog

**Response:**
```json
[
  {
    "skillId": 1,
    "name": "React",
    "category": "Frontend",
    "difficultyLevel": "INTERMEDIATE",
    "description": "JavaScript library for building UIs"
  }
]
```

---

### Skill Map (`/api/skillmap`)

#### GET /api/skillmap/user/{userId}
Get user's skill graph

**Response:**
```json
{
  "nodes": [
    {
      "id": "react",
      "label": "React",
      "status": "COMPLETED",
      "proficiency": 8.5
    }
  ],
  "edges": [
    {
      "source": "javascript",
      "target": "react"
    }
  ]
}
```

---

#### GET /api/skillmap/learning-path
Get learning path for goal

**Query Parameters:**
- `goal` (required) - Goal name
- `userId` (required) - User ID

**Response:**
```json
{
  "completedSkills": [
    { "name": "React", "proficiency": 8.5 }
  ],
  "inProgressSkills": [
    { "name": "Node.js", "proficiency": 5.2 }
  ],
  "nextUpSkills": [
    { "name": "GraphQL", "proficiency": 0 }
  ],
  "blockedSkills": [
    { "name": "Microservices", "missingPrereqs": ["Docker"] }
  ],
  "stats": {
    "total": 25,
    "completed": 8,
    "inProgress": 5,
    "nextUp": 4,
    "blocked": 8,
    "completionPercent": 32
  },
  "recommendations": "Focus on completing Node.js to unlock GraphQL"
}
```

---

### AI Features (`/api/ai`)

#### GET /api/ai/role-matches/{userId}
Get AI role recommendations

**Response:**
```json
{
  "result": "Based on your skills, you're well-suited for..."
}
```

---

#### POST /api/ai/analyze-job
Analyze job description

**Request Body:**
```json
{
  "userId": 1,
  "jobDescription": "We are looking for a Full Stack Developer with React and Node.js experience..."
}
```

**Response:**
```json
{
  "result": "Match: 75%\n\nMatched Skills:\n- React (8.5/10)\n- Node.js (5.2/10)\n\nSkill Gaps:\n- GraphQL\n- Docker\n\nRecommendations:\n1. Improve Node.js proficiency...\n2. Learn GraphQL basics..."
}
```

---

#### POST /api/ai/chat
Chat with AI career coach

**Request Body:**
```json
{
  "prompt": "How do I transition to a senior role?"
}
```

**Response:**
```json
{
  "response": "To transition to a senior role, focus on..."
}
```

---

### Gamification (`/api/gamification`)

#### GET /api/gamification/user/{userId}
Get user gamification data

**Response:**
```json
{
  "xp": 1250,
  "level": 5,
  "streak": 7,
  "totalBadges": 3,
  "achievements": [
    {
      "id": 1,
      "name": "First Steps",
      "description": "Complete your first quiz",
      "icon": "🎯",
      "xpReward": 50,
      "category": "quiz",
      "unlocked": true,
      "unlockedAt": "2026-01-05T14:22:00",
      "progress": 100
    },
    {
      "id": 2,
      "name": "Knowledge Seeker",
      "description": "Complete 5 quizzes",
      "icon": "📚",
      "xpReward": 100,
      "category": "quiz",
      "unlocked": false,
      "progress": 60
    }
  ]
}
```

---

#### GET /api/gamification/leaderboard
Get global leaderboard

**Response:**
```json
[
  {
    "name": "Alice",
    "xp": 2500,
    "level": 8,
    "streak": 15
  },
  {
    "name": "John Doe",
    "xp": 1250,
    "level": 5,
    "streak": 7
  }
]
```

---

#### POST /api/gamification/update-xp/{userId}
Update user XP

**Request Body:**
```json
{
  "xp": 50
}
```

**Response:**
```json
{
  "xp": 1300,
  "level": 5,
  "message": "XP updated successfully"
}
```

---

#### POST /api/gamification/update-streak/{userId}
Update user streak

**Request Body:**
```json
{
  "streak": 8
}
```

**Response:**
```json
{
  "streak": 8
}
```

---

### Resources (`/api/resources`)

#### POST /api/resources/generate
Generate AI learning resources

**Request Body:**
```json
{
  "skill": "React",
  "level": "intermediate",
  "type": "course"
}
```

**Response:**
```json
{
  "resources": "1. React - The Complete Guide (Udemy)..."
}
```

---

## 7. FILE STRUCTURE

### Frontend (`/careermap-ui`)

```
careermap-ui/
├── src/
│   ├── app/                          # Next.js App Router pages
│   │   ├── landing/page.tsx          # Landing page (unauthenticated)
│   │   ├── login/page.tsx            # Login page
│   │   ├── signup/page.tsx           # Signup page
│   │   ├── page.tsx                  # Dashboard (home for authenticated users)
│   │   ├── goals/page.tsx            # Career goals management
│   │   ├── progress/page.tsx         # Skill assessment & quizzes
│   │   ├── skillmap/page.tsx         # Interactive skill dependency map
│   │   ├── analyze/page.tsx          # Job description analyzer
│   │   ├── trends/page.tsx           # Analytics & trends visualization
│   │   ├── coach/page.tsx            # AI career coach chatbot
│   │   ├── achievements/page.tsx     # Gamification & achievements
│   │   ├── resources/page.tsx        # Learning resources catalog
│   │   └── layout.tsx                # Root layout wrapper
│   │
│   ├── components/                   # Reusable React components
│   │   ├── layout/
│   │   │   ├── Navbar.tsx            # Top navigation bar
│   │   │   ├── Sidebar.tsx           # Side navigation (unused?)
│   │   │   ├── AppLayout.tsx         # Main app layout wrapper
│   │   │   ├── AuthGuard.tsx         # Route protection component
│   │   │   └── ConditionalLayout.tsx # Conditional layout renderer
│   │   └── ui/
│   │       ├── ThemeToggle.tsx       # Dark/light mode switcher
│   │       └── TypewriterText.tsx    # Typewriter animation component
│   │
│   ├── hooks/
│   │   └── useCurrentUser.ts         # Custom hook for user session
│   │
│   ├── lib/
│   │   └── api.ts                    # Axios instance configuration
│   │
│   ├── types/
│   │   └── index.ts                  # TypeScript type definitions
│   │
│   └── globals.css                   # Global styles & CSS variables
│
├── public/                           # Static assets
├── package.json                      # Dependencies & scripts
├── tsconfig.json                     # TypeScript configuration
├── tailwind.config.ts                # Tailwind CSS configuration
└── next.config.js                    # Next.js configuration
```

---

### Backend (`/backend`)

```
backend/
├── src/main/java/com/careermappro/
│   ├── controllers/                  # REST API Controllers
│   │   ├── AuthController.java       # Authentication endpoints
│   │   ├── AIController.java         # AI features (coach, job analysis)
│   │   ├── QuizController.java       # Quiz generation & submission
│   │   ├── CareerGoalController.java # Career goal management
│   │   ├── ProficiencyController.java# Skill proficiency tracking
│   │   ├── SkillMapController.java   # Skill graph & learning path
│   │   ├── SkillCatalogController.java
│   │   ├── GamificationController.java# Achievements & XP
│   │   ├── ResourcesController.java  # Learning resources
│   │   ├── UserController.java
│   │   ├── DebugController.java      # Debug endpoints
│   │   ├── TestController.java       # Test endpoints
│   │   └── MigrationController.java  # Data migration
│   │
│   ├── services/                     # Business logic layer
│   │   ├── AuthService.java          # Authentication logic
│   │   ├── QuizService.java          # Quiz logic
│   │   ├── CareerGoalService.java    # Goal management logic
│   │   ├── ProficiencyService.java   # Proficiency tracking
│   │   ├── SkillMapService.java      # Skill graph building
│   │   ├── LearningPathService.java  # Learning path generation
│   │   ├── GPTService.java           # OpenAI API integration
│   │   ├── RoleMatcherService.java   # Job matching logic
│   │   ├── AnalyticsService.java     # Analytics calculations
│   │   ├── UserService.java
│   │   └── DataInitializationService.java # Seed data
│   │
│   ├── entities/                     # JPA Entity classes
│   │   ├── User.java                 # User entity
│   │   ├── Proficiency.java          # Skill proficiency
│   │   ├── CareerGoal.java           # Career goal
│   │   ├── Skill.java                # Skill definition
│   │   ├── SkillDependency.java      # Skill prerequisites
│   │   ├── UserSkill.java            # User-skill junction
│   │   ├── Quiz.java                 # Quiz entity
│   │   ├── QuizQuestion.java         # Quiz question
│   │   ├── Achievement.java          # Achievement definition
│   │   ├── UserAchievement.java      # User-achievement junction
│   │   ├── AnalyticsSnapshot.java    # Analytics snapshot
│   │   └── ReadinessTrend.java       # Readiness trend data
│   │
│   ├── repositories/                 # JPA Repositories (data access)
│   │   ├── UserRepository.java
│   │   ├── ProficiencyRepository.java
│   │   ├── CareerGoalRepository.java
│   │   ├── SkillRepository.java
│   │   ├── SkillDependencyRepository.java
│   │   ├── UserSkillRepository.java
│   │   ├── QuizRepository.java
│   │   ├── QuizQuestionRepository.java
│   │   ├── AchievementRepository.java
│   │   ├── UserAchievementRepository.java
│   │   ├── AnalyticsSnapshotRepository.java
│   │   └── ReadinessTrendRepository.java
│   │
│   └── CareerMapBackendApplication.java  # Spring Boot main class
│
├── src/main/resources/
│   └── application.properties        # Spring Boot config
│
└── pom.xml / build.gradle            # Maven/Gradle dependencies
```

---

## 8. CURRENT ISSUES

### 🔴 Critical Issues

#### 1. Invalid OpenAI API Key
**Status:** Blocking AI features
**Error:** `401 Unauthorized` from OpenAI API
**Affected Features:**
- AI Career Coach (returns error message)
- Quiz Generation (cannot generate questions)
- Job Description Analyzer (cannot analyze)
- Learning Resources AI Recommendations (cannot generate)

**Solution Required:** Update to valid OpenAI API key

---

#### 2. Learning Resources Still Hardcoded
**Status:** Partial implementation
**Issue:** Main resource catalog shows sample/hardcoded data
**What Works:** AI recommendations button (when API key is valid)
**What Doesn't:** Dynamic resource population based on user skills

**Potential Solutions:**
- Option A: Fetch from external API (Udemy, Coursera, etc.)
- Option B: Populate database with curated resources
- Option C: Use AI to generate resource suggestions on-demand
- Option D: Keep hardcoded but expand catalog

---

### ⚠️ Minor Issues

#### 3. Sidebar Component Unused
**Location:** `/components/layout/Sidebar.tsx`
**Issue:** File exists but doesn't appear to be used anywhere
**Impact:** Minimal (just dead code)

---

#### 4. Multiple Proficiency Tracking Systems
**Issue:** Two overlapping systems:
- `proficiencies` table (current, simpler)
- `user_skills` table (unused, more complex)

**Recommendation:** Keep `proficiencies`, remove `user_skills` if truly unused

---

#### 5. Analytics Snapshot & Trend Tables Empty
**Tables:** `analytics_snapshot`, `readiness_trend`
**Issue:** Defined but not actively populated
**Impact:** Historical trend tracking not working
**Status:** Feature incomplete

---

### ✅ Working Features

- User authentication (register, login)
- Dashboard with stats
- Career goal management (17 paths)
- Skill map visualization
- Trends/analytics page
- Achievements & gamification (8 badges)
- Leaderboard
- Theme switching (dark/light)
- Landing page

---

## SUMMARY

**CareerMap** is a feature-rich, full-stack career development platform with:

✅ **Strengths:**
- Comprehensive feature set
- Clean, modern UI
- Solid backend architecture
- Gamification for engagement
- Visual skill mapping
- Multiple assessment methods

❌ **Weaknesses:**
- OpenAI API dependency (currently broken)
- Some hardcoded data (resources)
- Incomplete analytics features
- Some unused code/tables

**Critical Path to Full Functionality:**
1. Update OpenAI API key
2. Test all AI features
3. Decide on learning resources approach (dynamic vs curated)
4. Clean up unused code/tables
5. Populate analytics trends

---

## RECOMMENDATIONS FOR REVIEW

### Features to Potentially Keep
- ✅ Dashboard (core overview)
- ✅ Goals (clear value)
- ✅ Progress/Quizzes (skill assessment)
- ✅ Skill Map (unique visualization)
- ✅ Trends (analytics)
- ✅ Achievements (engagement)

### Features to Consider Removing/Simplifying
- ⚠️ Landing Page (if this is for personal use, not public)
- ⚠️ AI Coach (expensive, could use chatbot widget instead)
- ⚠️ Job Analyzer (overlaps with skill matching)
- ⚠️ Resources (hardcoded, could just link to external sites)
- ⚠️ Leaderboard (if single-user, not useful)

### Redundant Tables to Clean Up
- `user_skills` (if `proficiencies` is sufficient)
- `analytics_snapshot` (if not actively used)
- `readiness_trend` (if not actively used)
