# CareerMap MVP - Complete Test Plan

**Date:** January 24, 2026
**Version:** 1.0 (MVP)
**Tester:** [Your Name]
**Environment:** Local Development (localhost)

---

## Pre-Test Setup ✅

### Backend Status
- ✅ Backend running at: `http://localhost:8080`
- ✅ Database: MySQL connected
- ✅ API endpoints responding correctly
- ✅ OpenAI API key configured (for AI explanations)

### Frontend Status
- ✅ Frontend running at: `http://localhost:3000`
- ✅ Next.js dev server active
- ✅ React 19 with TypeScript

---

## Test Scenarios

### 1. Landing Page & Navigation (5 minutes)

**Steps:**
1. Navigate to `http://localhost:3000`
2. Verify landing page displays with:
   - CareerMap branding
   - Value proposition text
   - "Get Started" button
   - "Sign In" option

**Expected Results:**
- ✅ Landing page loads within 2 seconds
- ✅ All animations work smoothly
- ✅ Buttons are clickable and styled correctly

---

### 2. User Registration (5 minutes)

**Steps:**
1. Click "Get Started" or navigate to `/signup`
2. Fill in the form:
   - **Name:** Test User
   - **Email:** `test@careermap.com`
   - **Password:** `TestPass123`
3. Click "Sign up"

**Expected Results:**
- ✅ Form validates input (email format, password length)
- ✅ Success message appears
- ✅ JWT token stored in localStorage
- ✅ Redirects to `/calibration` page
- ✅ User created in database

**Verification:**
```sql
SELECT * FROM users WHERE email = 'test@careermap.com';
```

---

### 3. Calibration Assessment (15 minutes)

**Steps:**
1. After signup, you should be on `/calibration`
2. Read the welcome screen explaining the calibration
3. Click "Begin Calibration"
4. Answer all 12 questions:
   - Mix of Backend, Frontend, Cloud & DevOps questions
   - Scenario-based (not trivia)
   - Questions show code context where applicable
5. Submit calibration

**Expected Results:**
- ✅ Questions display one at a time
- ✅ Progress bar shows completion percentage
- ✅ Cannot proceed without selecting an answer
- ✅ All question types render correctly:
  - Multiple choice with 4 options
  - Code snippets formatted properly
- ✅ After submission, see calibration results page

**Calibration Results Page:**
- ✅ Domain scores displayed (Backend, Frontend, Cloud & DevOps)
- ✅ Heatmap visualization
- ✅ Strengths, Gaps, and Unknown areas identified
- ✅ Recommended domain/role shown
- ✅ "Proceed to Frontier" button visible

**Optional - Resume Upload:**
1. Click "Upload Resume" on results page
2. Upload a sample resume (PDF, DOCX, or TXT)
3. Verify domain scores are boosted based on skills found

---

### 4. Frontier - Domain Selection (5 minutes)

**Steps:**
1. Click "Proceed to Frontier" or navigate to `/frontier`
2. View domain cards (Backend, Frontend, Cloud, ML, Security, Mobile)
3. Click on "Backend Engineering"

**Expected Results:**
- ✅ 6 domain cards displayed
- ✅ Each domain shows:
  - Icon
  - Name
  - Description
  - Color coding
- ✅ Clicking a domain zooms to role selection
- ✅ If came from calibration, see banner: "This path was generated based on your calibration results"

---

### 5. Frontier - Role Selection (5 minutes)

**Steps:**
1. After selecting Backend Engineering domain
2. View available roles:
   - Backend Engineer
   - DevOps Engineer
   - Database Engineer
   - Microservices Architect
3. Click "Backend Engineer"

**Expected Results:**
- ✅ Role cards displayed with icons and descriptions
- ✅ "Back" button returns to domain view
- ✅ Clicking role generates personalized learning path
- ✅ Loading indicator during path generation

---

### 6. Frontier - Learning Path Visualization (10 minutes)

**Steps:**
1. After selecting "Backend Engineer" role
2. Explore the interactive graph:
   - **Pan:** Click and drag to move around
   - **Zoom:** Scroll to zoom in/out
   - **Spotlight Mode:** Focus on current mission node
3. Examine the path structure:
   - Main path nodes in linear sequence
   - Competency branches (optional skills)
   - Dependencies shown with connecting lines
4. Use control buttons:
   - "CENTER ON NODE" - centers view on current frontier
   - "RESET VIEW" - fits entire graph in view

**Expected Results:**
- ✅ Graph renders with ~20-30 skill nodes
- ✅ Nodes arranged in linear path with competency branches
- ✅ Current mission highlighted (first unlocked node)
- ✅ Locked nodes show lock icon
- ✅ Completed nodes show checkmark
- ✅ Dependencies drawn correctly
- ✅ Smooth pan and zoom interactions
- ✅ Compact command bar at bottom shows current mission

---

### 7. Frontier - Node Detail Panel (5 minutes)

**Steps:**
1. Click on the first node (e.g., "HTTP Protocol Deep Dive")
2. Detail panel slides in from right
3. Review information:
   - Node name and number
   - Estimated hours (3h)
   - Difficulty rating (2/10)
   - "What you'll be able to do" description
   - Assessment type: PROBE
   - Proof requirement
   - Prerequisites (if any)
   - Learning resources list

**Expected Results:**
- ✅ Panel appears with smooth animation
- ✅ All node metadata displayed
- ✅ Learning resources listed (with types and duration)
- ✅ Two action buttons visible:
  - "START SESSION" (blue, primary)
  - "✨ Explain it to me (AI)" (purple gradient)
- ✅ Close button (X) works

---

### 8. AI Explanation Feature (10 minutes)

**Steps:**
1. With a node detail panel open
2. Click "✨ Explain it to me (AI)"
3. Wait for AI explanation to generate (10-20 seconds)
4. Review the explanation content

**Expected Results:**
- ✅ Modal appears with loading spinner
- ✅ Loading message: "Generating personalized explanation..."
- ✅ AI explanation generated within 30 seconds
- ✅ Explanation includes structured sections:
  - **1. THE CORE CONCEPT** - Simple explanation (2-3 sentences)
  - **2. THE PERFECT ANALOGY** - Memorable real-world comparison
  - **3. REAL-WORLD USE CASE** - Concrete example with company names
  - **4. KEY TECHNICAL DETAILS** - 3-5 bullet points
  - **5. COMMON PITFALLS** - What mistakes to avoid
  - **6. MASTERY CHECKLIST** - Tasks to prove understanding
- ✅ Markdown formatting renders correctly (headers, bold, bullets)
- ✅ Close button works

**Error Handling:**
- If OpenAI API key not set, should show error: "OpenAI API key not configured"

---

### 9. PROBE Assessment (15 minutes)

**Steps:**
1. Close AI explanation modal (if open)
2. Click "START SESSION" on an unlocked node
3. PROBE assessment begins:
   - Loading resources message appears
   - Quiz modal opens with questions
4. Answer all questions (typically 5-10 questions)
   - **Multiple choice:** Click an option
   - **True/false:** Click true or false
   - **Code completion:** Type answer in text field
5. Progress through all questions
6. Submit quiz

**Expected Results:**
- ✅ Quiz modal appears with dark theme
- ✅ Progress bar shows question X of Y
- ✅ All question types render:
  - Multiple choice with 4 options
  - True/false with 2 buttons
  - **Code completion with text input field** ⭐ (NEW FIX)
- ✅ Cannot proceed without answering current question
- ✅ "Next Question" button advances
- ✅ Last question shows "Submit Quiz" button
- ✅ Submit only enabled when all questions answered

**Results Screen:**
- ✅ Score displayed as percentage
- ✅ Breakdown of correct/incorrect answers
- ✅ Feedback for each question
- ✅ "Continue Learning" button returns to frontier
- ✅ If score ≥80%, node marked as completed

---

### 10. Session Management (5 minutes)

**Steps:**
1. Start a PROBE session but don't complete it
2. Refresh the browser page
3. Navigate back to the same node
4. Try to start the session again

**Expected Results:**
- ✅ Can resume the same session (idempotent)
- ✅ Or session expires after 24 hours
- ✅ No duplicate session errors
- ✅ Session state persists correctly

---

### 11. Login Flow (5 minutes)

**Steps:**
1. Logout (if button available) or open incognito window
2. Navigate to `http://localhost:3000/login`
3. Enter credentials:
   - **Email:** `test@careermap.com`
   - **Password:** `TestPass123`
4. Click "Sign in"

**Expected Results:**
- ✅ Login form validates input
- ✅ JWT token generated and stored
- ✅ Redirects to `/frontier` (since calibration already completed)
- ✅ User's learning path and progress loaded
- ✅ Completed nodes show checkmarks
- ✅ Current frontier node highlighted

---

### 12. UI/UX Polish (5 minutes)

**Visual Checks:**
- ✅ Consistent color scheme (purple/pink accents)
- ✅ Smooth animations throughout
- ✅ Responsive design (try resizing window)
- ✅ No console errors in browser DevTools
- ✅ Proper loading states everywhere
- ✅ Error messages are user-friendly

**Frontier UI Specific:**
- ✅ Command bar is compact (not blocking graph)
- ✅ All buttons accessible
- ✅ Node tooltips/labels readable
- ✅ Graph controls work intuitively

---

## Known Issues / Limitations

### Functional
- ❌ Real learning resources not loaded (placeholder data)
- ❌ Actual quiz questions are mock data (not domain-specific)
- ❌ No user profile/settings page yet
- ⚠️ OpenAI API key must be set as environment variable for AI explanations

### Data
- ✅ Database seeded with domains and roles
- ⚠️ Learning paths are dynamically generated (mock data)
- ⚠️ Node dependencies are simplified for demo

---

## Success Criteria ✅

For a successful MVP demo to your professor, you should be able to complete:

1. ✅ **Full User Journey:** Signup → Calibration → Frontier → PROBE → AI Explanation
2. ✅ **All Core Features Working:**
   - User authentication (JWT)
   - Calibration assessment
   - Personalized path generation
   - Interactive frontier visualization
   - PROBE assessments (including code completion input)
   - AI-powered explanations
3. ✅ **No Critical Bugs:**
   - No server crashes
   - No data loss
   - No broken UI components
4. ✅ **Professional Polish:**
   - Clean design
   - Smooth interactions
   - Helpful error messages

---

## Quick Smoke Test (5 minutes)

If short on time, run this quick test:

1. ✅ Navigate to `http://localhost:3000`
2. ✅ Signup with new email
3. ✅ Complete calibration (answer randomly)
4. ✅ Select a domain and role
5. ✅ Click a node and try "AI Explanation"
6. ✅ Start a PROBE and answer one question with code completion
7. ✅ Verify UI is responsive and polished

---

## Test Results Log

**Date:** ___________
**Tester:** ___________

| Test Scenario | Status | Notes |
|---------------|--------|-------|
| 1. Landing Page | ⬜ Pass / ⬜ Fail | |
| 2. Registration | ⬜ Pass / ⬜ Fail | |
| 3. Calibration | ⬜ Pass / ⬜ Fail | |
| 4. Domain Selection | ⬜ Pass / ⬜ Fail | |
| 5. Role Selection | ⬜ Pass / ⬜ Fail | |
| 6. Path Visualization | ⬜ Pass / ⬜ Fail | |
| 7. Node Detail Panel | ⬜ Pass / ⬜ Fail | |
| 8. AI Explanation | ⬜ Pass / ⬜ Fail | |
| 9. PROBE Assessment | ⬜ Pass / ⬜ Fail | |
| 10. Session Management | ⬜ Pass / ⬜ Fail | |
| 11. Login Flow | ⬜ Pass / ⬜ Fail | |
| 12. UI/UX Polish | ⬜ Pass / ⬜ Fail | |

---

## Environment Setup for Professor

To run the application, they will need:

```bash
# 1. Start MySQL database (must be running)

# 2. Backend - Terminal 1
cd backend
export DB_PASSWORD=Ams110513200
export OPENAI_API_KEY=<your-openai-key>
./gradlew bootRun

# 3. Frontend - Terminal 2
cd careermap-ui
npm run dev

# 4. Access application
# Open browser: http://localhost:3000
```

**Required Environment Variables:**
- `DB_PASSWORD`: Database password (Ams110513200)
- `OPENAI_API_KEY`: OpenAI API key for AI explanations (get from https://platform.openai.com)

---

## Contact & Support

**Developer:** [Your Name]
**Email:** [Your Email]
**GitHub:** [Repository URL if applicable]

For questions or issues during testing, please contact the developer.

---

**End of Test Plan**
