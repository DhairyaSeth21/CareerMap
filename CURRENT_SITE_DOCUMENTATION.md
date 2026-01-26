# CareerMap - Current Site Documentation
**Last Updated:** January 13, 2026
**Purpose:** Complete documentation of current state for onboarding & landing page design

---

## 🎯 What CareerMap Is

**CareerMap** is an AI-powered career learning platform that helps software engineers build skills through personalized, evidence-based learning paths.

### Core Value Proposition
- **Personalized Learning Paths:** AI generates custom learning journeys based on your chosen career role
- **Evidence-Based Progress:** Track your skills through PROBE/BUILD/PROVE/APPLY assessments
- **Visual Skill Mapping:** See your learning journey as an interactive constellation of connected skills
- **Instant AI Feedback:** Get explanations for wrong answers and guidance on what to learn next

---

## 🏗️ Current Architecture

### Backend (Spring Boot + MySQL)
- **Tech Stack:** Java 21, Spring Boot 3.5.7, MySQL 9.5, Hibernate, OpenAI API
- **Running On:** http://localhost:8080
- **Key Features:**
  - Session-based engagement system (PROBE/BUILD/PROVE/APPLY)
  - AI-powered learning path generation via OpenAI GPT-4
  - Quiz generation with fresh questions per attempt
  - State machine for skill progression (UNSEEN → INFERRED → ACTIVE → PROVED → STALE)
  - Bayesian confidence updates based on assessment performance

### Frontend (Next.js + React)
- **Tech Stack:** Next.js 16, React 19, TypeScript, Framer Motion, Tailwind CSS
- **Running On:** http://localhost:3000
- **Key Feature:** Zoom-based multi-level visualization (NOT routing/pages)

---

## 🌌 The Zoom Architecture (How It Works)

CareerMap uses a **zoom metaphor** - think of it like Google Maps zooming from world → country → city → street.

### Level 0: Domain Universe 🌟
**What You See:**
- A star map showing 6 career domains:
  1. **Backend Engineering** (blue) - APIs, databases, system architecture
  2. **Frontend Engineering** (purple) - UI/UX, web applications
  3. **Cloud & DevOps** (green) - CI/CD, containers, infrastructure
  4. **Machine Learning** (orange) - AI, data science, models
  5. **Cybersecurity** (red) - Security engineering, pen testing
  6. **Mobile Development** (cyan) - iOS, Android, cross-platform

**What You Do:** Click a domain star to zoom in

**Visual Design:**
- Dark gradient background (slate-950 → indigo-950)
- Animated starfield with twinkling background stars
- Large star cards with glowing effects on hover
- Pulsing indicators on each star

---

### Level 1: Role Constellation 💼
**What You See:**
- Roles within the selected domain displayed as glowing stars
- Example (Backend Engineering domain):
  - Backend Engineer
  - API Developer
  - Microservices Architect
  - Database Engineer

**What You Do:** Click a role star to generate your personalized learning path

**Visual Design:**
- Domain-colored ambient particles floating in background
- Role cards with purple/indigo gradients
- Spiral animation on card entrance
- Back button to return to domains

**Data Flow:**
- Frontend calls: `POST /api/core-loop/generate-detailed-path?userId=18&roleId=X`
- Backend calls OpenAI GPT-4 to generate personalized path
- AI considers: available skills, role requirements, logical prerequisites

---

### Level 2: Path Constellation 🛤️
**What You See:**
- Your personalized learning path as an interconnected constellation of skill nodes
- Each node represents an atomic, testable competency
- Connection lines show dependencies (which skills unlock others)
- Nodes are color-coded by category:
  - 🟢 **Foundational** (Green) - Basics required for everything
  - 🔵 **Core** (Blue) - Essential skills for the role
  - 🟣 **Advanced** (Purple) - Deeper expertise
  - 🟠 **Specialized** (Orange) - Niche/expert-level skills

**What You Do:** Click any node to zoom to Focus mode

**Visual Design:**
- Deep space background with nebula effects
- Nodes arranged in spiral constellation pattern
- SVG lines connecting dependent nodes
- Hover tooltips showing node details (name, difficulty, hours)
- Difficulty badges on each node (1-10 scale)

**Node Structure:**
Each node contains:
- **name:** "Explain symmetric vs asymmetric encryption"
- **whyItMatters:** "Foundational distinction used in TLS/SSL..."
- **learnResources:** Array of actual URLs to articles/videos
- **assessmentType:** probe/build/prove/apply
- **proofRequirement:** What you must demonstrate
- **dependencies:** Skill IDs that block this node
- **unlocks:** Skill IDs this node enables
- **difficulty:** 1-10
- **estimatedHours:** Time to complete
- **category:** foundational/core/advanced/specialized

---

### Level 3: Frontier Focus 🎯
**What You See:**
- Full-screen overlay showing detailed information for ONE node
- Learning resources with clickable URLs
- "Start PROBE" button to begin assessment

**Sections:**
1. **Node Header:** Name, difficulty, estimated hours, assessment type
2. **Why It Matters:** Context and importance
3. **Learning Resources:** 2-4 curated resources with:
   - Type (article/video/course/documentation)
   - Title
   - URL (clickable, opens in new tab)
   - Description
   - Estimated reading/watching time
4. **Proof Requirement:** What you need to demonstrate
5. **Start Assessment Button:** Begins the PROBE quiz

**What You Do:**
- Review learning resources
- Click URLs to learn
- Click "Start PROBE" when ready to test your knowledge

---

## 📝 The PROBE Assessment Flow

### What is PROBE?
PROBE is an AI-generated quiz that tests your understanding of a skill. It's the first step in proving competency.

### The Flow:

**1. Start PROBE**
- Frontend calls: `POST /api/core-loop/start-probe?sessionId=X`
- Backend calls OpenAI to generate 5 fresh questions
- Questions are never repeated - each attempt is unique

**2. Take Quiz**
- Questions appear one at a time with progress bar
- Question types:
  - **Multiple Choice** (70%) - Pick A/B/C/D
  - **True/False** (15%) - Simple boolean questions
  - **Code Completion** (15%) - Fill in the blank

**3. Submit & Grade**
- Frontend calls: `POST /api/core-loop/submit-quiz`
- Backend grades each answer
- For wrong answers: OpenAI generates personalized explanations
- Score calculated (e.g., 4/5 = 80%)
- Confidence updated using Bayesian formula

**4. View Results**
- See your score with visual feedback (Excellent!/Good Job!/Keep Learning!)
- Confidence change shown: "0% → 32%"
- Review each question:
  - ✅ Correct answers marked green
  - ❌ Wrong answers marked red with:
    - Your answer
    - Correct answer
    - AI explanation of why you were wrong and how to remember it

**5. State Transition**
Based on performance, your skill state changes:
- **Score < 60%:** UNSEEN → INFERRED (you've been exposed but need more work)
- **Score 60-79%:** UNSEEN → ACTIVE (you're actively learning this)
- **Score 80%+:** UNSEEN → PROVED (you've demonstrated competency)

All state transitions are logged in backend console for transparency.

---

## 🎨 Visual Design Language

### Color Palette
- **Background:** Deep space gradient (slate-950 → indigo-950 → slate-950)
- **Primary:** Purple/Indigo gradient (#8b5cf6 → #6366f1)
- **Success:** Green (#10b981)
- **Error:** Red (#ef4444)
- **Warning:** Yellow/Orange (#f59e0b)
- **Text:** White on dark, slate-400 for secondary

### Animations
- **Zoom Transitions:** Scale + opacity with spring physics
- **Card Hover:** Scale 1.05-1.1 with glow effect
- **Progress Bars:** Smooth width transitions
- **Loading:** Spinning gradient ring
- **Results:** Score scales up with spring bounce

### Typography
- **Headers:** Bold, gradient text (purple → pink)
- **Body:** Clean, readable slate-300/400
- **Buttons:** Bold white text on gradient background

### Layout Principles
- **Single Canvas:** Everything happens on one page - no routing
- **Overlays:** Focus panel, quiz, and results appear as overlays
- **Mission Control:** Dark, professional, space-themed aesthetic
- **Consistent Spacing:** 8px grid system

---

## 📊 Database Structure

### Key Tables

**domains**
- domainId, name, description, icon, color
- Examples: Backend Engineering, Cybersecurity, etc.

**career_roles**
- careerRoleId, name, description, icon, domainId
- Examples: Backend Engineer, Security Engineer, etc.

**skill_nodes**
- skillNodeId, canonicalName, difficulty, domain
- Atomic competencies like "RESTful API Design"

**sessions**
- sessionId, userId, skillNodeId, sessionType, sessionState
- Tracks PROBE/BUILD/PROVE/APPLY attempts
- States: PROPOSED → ACTIVE → COMPLETED/EXPIRED

**user_skill_states**
- stateId, userId, skillId, status, confidence
- Status: UNSEEN/INFERRED/ACTIVE/PROVED/STALE
- Confidence: 0.00-1.00 (Bayesian updated)

**users**
- id, name, email, password, level, streak, xp
- Currently has test users with IDs starting at 18

---

## 🔌 API Endpoints

### Domain & Role APIs
```
GET  /api/frontier/domains
     → Returns all domains with nested roles

GET  /api/frontier/domains/{id}/roles
     → Returns roles for specific domain
```

### Core Loop APIs
```
POST /api/core-loop/generate-detailed-path?userId=X&roleId=Y
     → Generates AI learning path for role
     → Returns: role info, path nodes[], focusNode, session

POST /api/core-loop/start-probe?sessionId=X
     → Starts PROBE session, generates quiz
     → Returns: session info, quiz with 5 questions

POST /api/core-loop/submit-quiz?sessionId=X
     Body: { answers: [...] }
     → Grades quiz, generates explanations
     → Returns: score, gradedAnswers[], confidenceChange
```

---

## 🐛 Current Issues

### 1. OpenAI API Timeout ⚠️
**Problem:** When generating detailed paths, OpenAI times out
**Cause:** Large prompt (all skill nodes) + network latency
**Status:** Identified, needs fix
**Impact:** Path generation returns empty array

### 2. Empty Domains (Mobile & ML) 📱🤖
**Problem:** Mobile Development and Machine Learning have no roles
**Status:** Need to add roles to database
**Impact:** Users can't select these domains

### 3. No Onboarding 🚪
**Problem:** App dumps users directly into Domain Universe
**Status:** Need to design and implement
**Impact:** Users don't understand what CareerMap does

### 4. No Landing Page 🏠
**Problem:** No home page explaining the product
**Status:** Need to design and implement
**Impact:** Can't share with users, no call-to-action

---

## 🎯 Current User Flow (What Works)

1. **Navigate to** http://localhost:3000/frontier
2. **See** Domain Universe with 6 domain stars
3. **Click** a domain (e.g., Backend Engineering)
4. **Zoom to** Role Constellation
5. **Click** a role (e.g., Backend Engineer)
6. **Wait** for AI path generation (currently fails due to timeout)
7. **IF path generates successfully:**
   - See Path Constellation with learning nodes
   - Click a node to see details
   - Review learning resources
   - Start PROBE assessment
   - Take quiz (5 questions)
   - Submit and see results with AI explanations
   - State transitions happen automatically

---

## 🚀 What Needs to Be Built

### Critical Path:
1. **Fix OpenAI Timeout** - Reduce prompt size or increase timeout
2. **Add Roles to Empty Domains** - Populate Mobile & ML
3. **Design Onboarding Flow** - How to introduce users to CareerMap
4. **Build Landing Page** - Marketing/explanation page
5. **Test End-to-End** - Verify full zoom flow works

### Onboarding Considerations:
- **First-time users:** Need to understand the zoom metaphor
- **Career goal:** Should we ask what they want to become?
- **Current skill level:** Calibration quiz? Or skip?
- **Tutorial overlay?** Show tooltips on first visit?

### Landing Page Considerations:
- **Hero section:** What's the hook?
- **Value props:** Why use CareerMap over alternatives?
- **Demo/screenshots:** Show the constellation UI
- **CTA:** "Start Your Journey" → goes to onboarding or frontier?
- **Social proof:** Testimonials, stats, logos?

---

## 💡 Key Differentiators (Why CareerMap is Unique)

1. **AI-Generated Paths** - Not pre-built roadmaps, personalized for YOU
2. **Evidence-Based** - Prove skills through assessments, not time spent
3. **Visual Learning** - Constellation metaphor shows dependencies
4. **Real Learning Resources** - Curated URLs, not generic advice
5. **State Machine** - Clear progression: UNSEEN → PROVED
6. **Zoom Interface** - Intuitive navigation without complex menus

---

## 🎭 User Personas

### 1. Career Switcher (Primary)
- **Goal:** Break into backend engineering from non-tech background
- **Pain:** Don't know what to learn or in what order
- **Needs:** Clear path, validation of progress, curated resources

### 2. Junior Engineer (Secondary)
- **Goal:** Level up from junior to mid-level
- **Pain:** Feel stuck, don't know what skills to prioritize
- **Needs:** Structured learning, proof of competency for resume

### 3. Experienced Engineer (Tertiary)
- **Goal:** Learn new domain (e.g., backend → ML)
- **Pain:** Too much info, hard to find high-quality resources
- **Needs:** Efficient path, skip basics, focus on gaps

---

## 📝 Technical Debt

1. **User Authentication** - Currently using hardcoded userId=18
2. **Error Handling** - Needs better user-facing error messages
3. **Loading States** - Some transitions feel slow
4. **Responsive Design** - Optimized for desktop, needs mobile work
5. **Path Caching** - Re-generates path every time (expensive)
6. **Quiz History** - Users can't review past assessments
7. **Skill Search** - No way to search for specific skills

---

## 🎬 Next Steps for Onboarding & Landing

### Onboarding Flow Options:

**Option A: Minimal Friction**
1. Landing page with video/GIF demo
2. "Start Learning" → goes directly to Domain Universe
3. Inline tooltips explain zoom metaphor as you use it

**Option B: Guided Setup**
1. Landing page with benefits
2. "Get Started" → onboarding wizard:
   - What do you want to become? (select role)
   - What's your experience level? (beginner/intermediate/advanced)
   - Generate custom path immediately
3. Tour overlay on first Frontier visit

**Option C: Calibration First**
1. Landing page
2. "Take Calibration Quiz" → 10-question assessment
3. AI recommends best starting role based on results
4. Jump directly to Path Constellation with recommendations

### Landing Page Structure:

**Hero Section:**
- Headline: "Your Personalized Path to Software Mastery"
- Subheadline: "AI-powered learning paths that adapt to your goals. No guesswork, just progress."
- CTA: "Start Your Journey" (primary) + "See How It Works" (secondary)
- Hero visual: Animated constellation

**Features Section:**
- AI-Powered Paths
- Evidence-Based Progress
- Visual Skill Mapping
- Instant Feedback

**How It Works:**
1. Choose your career goal
2. AI generates your path
3. Learn, assess, and prove your skills
4. Track your progress in real-time

**Social Proof:**
- "X engineers leveled up their careers"
- Testimonial quotes
- Skill badges/achievements

**CTA Section:**
- "Ready to build your future?" → Sign Up / Start Free

---

**END OF DOCUMENTATION**
