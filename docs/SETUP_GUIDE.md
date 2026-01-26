# CareerMap MVP - Setup Guide for Testing

**Student:** Dhairya Arjun Seth
**Course:** [Your Course]
**Professor:** [Professor Name]
**Date:** January 24, 2026

---

## Overview

CareerMap is an evidence-based learning platform that helps software engineers identify skill gaps through calibration, navigate personalized learning paths, and validate knowledge through assessments. This MVP demonstrates:

- **Smart Calibration:** Scenario-based questions to assess current skill levels
- **Personalized Frontier:** Interactive visual learning path based on calibration
- **PROBE Assessments:** Multi-format quizzes to validate skill mastery
- **AI Explanations:** GPT-4o-powered explanations for 100% retention

---

## Quick Start (5 Minutes)

### Prerequisites
- **MySQL** installed and running
- **Java 21** (for Spring Boot backend)
- **Node.js 18+** (for Next.js frontend)
- **OpenAI API Key** (optional, for AI explanations - get free trial at https://platform.openai.com)

### Step 1: Database Setup

```bash
# Start MySQL (if not running)
mysql.server start  # macOS
# OR
sudo service mysql start  # Linux

# The database "careermap" should already exist and be seeded
# If not, run:
mysql -u root -p
CREATE DATABASE careermap;
```

### Step 2: Start Backend (Terminal 1)

```bash
cd /Users/dhairyaarjunseth/Documents/CareerMap/backend

# Set environment variables
export DB_PASSWORD=Ams110513200
export OPENAI_API_KEY=your-openai-key-here  # Optional for AI features

# Start Spring Boot application
./gradlew bootRun

# Wait for: "Started CareerMapBackendApplication"
```

**Verify backend is running:**
```bash
curl http://localhost:8080/api/frontier/domains
# Should return JSON with domains
```

### Step 3: Start Frontend (Terminal 2)

```bash
cd /Users/dhairyaarjunseth/Documents/CareerMap/careermap-ui

# Install dependencies (first time only)
npm install

# Start Next.js dev server
npm run dev

# Wait for: "Local: http://localhost:3000"
```

### Step 4: Access Application

Open browser: **http://localhost:3000**

---

## Testing the Application

### Recommended Test Flow (20 minutes)

1. **Landing Page** (1 min)
   - Navigate to http://localhost:3000
   - Click "Get Started"

2. **Signup** (2 min)
   - Create account with:
     - Name: Test User
     - Email: test@example.com
     - Password: TestPass123

3. **Calibration** (5 min)
   - Answer 12 scenario-based questions
   - Questions test Backend, Frontend, and Cloud knowledge
   - Submit to see domain scores and recommendations

4. **Frontier - Interactive Learning Path** (5 min)
   - Select "Backend Engineering" domain
   - Choose "Backend Engineer" role
   - Explore the interactive graph:
     - Pan by dragging
     - Zoom with scroll wheel
     - Click nodes to see details

5. **AI Explanation** (3 min)
   - Click any node (e.g., "HTTP Protocol Deep Dive")
   - Click "✨ Explain it to me (AI)"
   - Wait 10-20 seconds for personalized explanation
   - Note: Requires OPENAI_API_KEY to be set

6. **PROBE Assessment** (4 min)
   - Click "START SESSION" on first unlocked node
   - Answer quiz questions (multiple choice, true/false, code completion)
   - **Key Feature:** Code completion questions now have text input
   - Submit and view results
   - Node marked complete if score ≥80%

### Quick Smoke Test (5 minutes)

If pressed for time:
1. Signup → Calibration (answer randomly)
2. Select domain → role
3. Click a node → try AI explanation
4. Start PROBE → answer 1-2 questions
5. Verify UI is smooth and professional

---

## Key Features to Demonstrate

### 1. Smart Calibration
- **Not trivia:** Questions are scenario-based, testing decision-making
- **Example:** "Your API response time degrades from 50ms to 500ms when concurrent users increase. What is the MOST LIKELY bottleneck?"
- **Output:** Domain confidence scores (Backend: 0.7, Frontend: 0.4, etc.)

### 2. Personalized Learning Paths
- **Generated** based on calibration results
- **Visual graph** with ~20-30 skill nodes
- **Dependencies** shown (locked until prerequisites completed)
- **EDLSG phases** tracked (Explore, Decide, Learn, Score, Grow)

### 3. Interactive Frontier
- **Zoom architecture:** Domain → Role → Path → Node
- **Graph controls:** Pan, zoom, spotlight mode
- **Compact UI:** Command bar doesn't block graph (recent fix)
- **Real-time state:** Locked/Available/Completed nodes

### 4. Evidence-Based Progression
- **PROBE assessments** validate each skill
- **Multiple question types:**
  - Multiple choice (4 options)
  - True/false
  - **Code completion** (text input) ⭐
- **Score-based unlocking:** Must score ≥80% to progress

### 5. AI-Powered Learning
- **OpenAI GPT-4o-mini** integration
- **Retention-focused** explanations with:
  - Core concept (simple terms)
  - Memorable analogy
  - Real-world use case (company examples)
  - Key technical details
  - Common pitfalls
  - Mastery checklist
- **Beautiful modal** with markdown formatting

---

## Architecture Overview

### Tech Stack

**Backend:**
- Spring Boot 3.5.7
- Java 21
- MySQL 8.0
- JWT Authentication (BCrypt)
- OpenAI API integration

**Frontend:**
- Next.js 15 (React 19)
- TypeScript
- Framer Motion (animations)
- Tailwind CSS

### Key APIs

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/v1/auth/register` | POST | User signup |
| `/api/v1/auth/login` | POST | User login (returns JWT) |
| `/api/frontier/domains` | GET | Get all domains |
| `/api/core-loop/generate-detailed-path` | POST | Generate personalized path |
| `/api/core-loop/start-probe` | POST | Start PROBE assessment |
| `/api/core-loop/submit-quiz` | POST | Submit quiz answers |
| `/api/ai/explain` | POST | Generate AI explanation |

---

## Database Schema (Simplified)

```sql
users (id, name, email, password, xp, level, streak)
domains (id, name, description, icon, color)
career_roles (id, name, description, domain_id)
sessions (id, user_id, skill_node_id, state, score, expires_at)
skill_nodes (id, canonical_name, domain, difficulty)
```

---

## Known Limitations (MVP Scope)

### Intentional Simplifications
1. **Mock Learning Resources** - Real resources not integrated yet
2. **Generated Paths** - Dynamic generation, not pre-curated content
3. **Simplified Quiz Engine** - Questions are placeholders, not domain-specific
4. **No User Profile** - Basic auth only, no settings page

### Technical Constraints
1. **OpenAI API Required** - AI explanations need valid API key
2. **Local Development Only** - Not deployed/hosted
3. **Session TTL:** 24 hours (then auto-expires)

---

## Troubleshooting

### Backend won't start
```bash
# Check MySQL is running
mysql -u root -p

# Check correct password
export DB_PASSWORD=Ams110513200

# View logs
tail -f /tmp/backend.log
```

### Frontend won't start
```bash
# Clear cache
rm -rf .next node_modules
npm install
npm run dev
```

### AI Explanation fails
- Error: "OpenAI API key not configured"
- Solution: `export OPENAI_API_KEY=sk-...`
- Get key: https://platform.openai.com/api-keys

### Database errors
```bash
# Reset database (if needed)
mysql -u root -p
DROP DATABASE careermap;
CREATE DATABASE careermap;
# Restart backend (auto-creates tables)
```

---

## What Makes This MVP Special

1. **Evidence-Based Learning**
   - Not just content consumption
   - Actual skill validation through assessments
   - Can't proceed without proving mastery

2. **Personalization**
   - Calibration determines starting point
   - Path adapts to user's current level
   - No wasted time on known skills

3. **Visual Progress**
   - See entire learning journey as interactive map
   - Understand dependencies and skill relationships
   - Gamification through node completion

4. **AI Assistance**
   - Get explanations designed for retention
   - Real-world examples and analogies
   - Understand WHY, not just WHAT

5. **Production-Ready Foundation**
   - Proper authentication (JWT + BCrypt)
   - RESTful API architecture
   - Type-safe frontend (TypeScript)
   - Modern tech stack

---

## Expected Outcomes

After testing, you should observe:

✅ **Complete user journey** from signup to skill validation
✅ **Beautiful, responsive UI** with smooth animations
✅ **Functional PROBE assessments** with code completion input
✅ **AI-generated explanations** (if API key configured)
✅ **No critical bugs** or crashes
✅ **Professional polish** worthy of production

---

## Demo Script (If Presenting Live)

**1. Introduction** (1 min)
> "CareerMap is an evidence-based learning platform for software engineers. Unlike traditional platforms that just provide videos, CareerMap requires proof of skill mastery before progression."

**2. Calibration** (2 min)
> "First, we assess current skill levels with scenario-based questions - not trivia, but real engineering decisions."
> [Show a calibration question, complete assessment]

**3. Personalized Path** (3 min)
> "Based on calibration, CareerMap generates a personalized learning path. This is the Frontier - a visual map of skills."
> [Navigate domain → role → path, pan/zoom graph]

**4. AI Explanation** (2 min)
> "Each skill node has AI-powered explanations designed for maximum retention."
> [Click node, generate AI explanation, show structured format]

**5. Assessment** (2 min)
> "Progress requires validation. PROBE assessments test understanding."
> [Start PROBE, show code completion input, submit quiz]

---

## Contact

**Student:** Dhairya Arjun Seth
**Email:** [Your Email]
**Repository:** [GitHub URL if available]

For questions or issues, please contact me directly.

---

**Thank you for testing CareerMap!** 🚀
