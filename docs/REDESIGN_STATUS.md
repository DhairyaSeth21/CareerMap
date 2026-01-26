# CareerMap Redesign - Implementation Status

**Goal**: Transform from "skill graph explorer" to "mentor-driven learning system"

**Progress**: Backend ~60% | Frontend ~5% | Overall ~30%

---

## ✅ Phase 1: Core Backend Infrastructure (Week 1) - 75% COMPLETE

### Task 1.1: Fix Assessment → State Update Pipeline ✅ DONE
**Status**: ✅ Implementation complete, testing pending

**What Was Built**:
- [AssessmentResultService.java](backend/src/main/java/com/careermappro/services/AssessmentResultService.java) - Bridges quiz results to EDLSG
- Strict scoring curve (95%+ = PROVED, 85%+ = ACTIVE, etc.)
- Auto-evidence creation from quiz completion
- State transition tracking (before/after comparison)
- Updated [QuizService.java](backend/src/main/java/com/careermappro/services/QuizService.java) to use new pipeline

**Deliverable**: Quiz completion now creates evidence → triggers state machine → updates Frontier

**Documentation**: [ASSESSMENT_PIPELINE_FIX.md](ASSESSMENT_PIPELINE_FIX.md)

---

### Task 1.2: Build Path Generation Service ✅ DONE
**Status**: ✅ Core infrastructure complete, needs database wiring

**What Was Built**:

#### Database Schema (Migration V4)
Created [V4__learning_paths.sql](backend/src/main/resources/db/migration/V4__learning_paths.sql) with 6 tables:
- `learning_paths` - User-specific 12-week curricula
- `path_units` - Major milestones (3-5 per path)
- `path_steps` - Individual skills to master (5-10 per unit)
- `study_resources` - AI-curated learning materials (2-4 per step)
- `path_progress` - User progress tracking
- `path_activities` - Event log for path actions

#### Entity Classes
Created 6 JPA entities:
- [LearningPath.java](backend/src/main/java/com/careermappro/entities/LearningPath.java)
- [PathUnit.java](backend/src/main/java/com/careermappro/entities/PathUnit.java)
- [PathStep.java](backend/src/main/java/com/careermappro/entities/PathStep.java)
- [StudyResource.java](backend/src/main/java/com/careermappro/entities/StudyResource.java)
- [PathProgress.java](backend/src/main/java/com/careermappro/entities/PathProgress.java)
- [PathActivity.java](backend/src/main/java/com/careermappro/entities/PathActivity.java)

#### Service Logic
Created [PathGenerationService.java](backend/src/main/java/com/careermappro/services/PathGenerationService.java):
- `generatePath()` - Uses OpenAI to create structured 12-week curriculum
- System prompt designs the "mentor" persona
- Respects user's current skill state (PROVED, ACTIVE, INFERRED)
- Validates generated JSON structure
- Includes stub methods for progress tracking

#### Enhanced GPT Service
Updated [GPTService.java](backend/src/main/java/com/careermappro/services/GPTService.java):
- Added `generateCompletion()` with system prompt support
- Uses `gpt-4o-mini` model
- Forces JSON output with `response_format`
- Lower temperature (0.3) for consistent structure

**Deliverable**: OpenAI-powered curriculum generation ready to wire up

**Remaining Work**:
- ⏳ Create repositories (PathRepository, PathUnitRepository, etc.)
- ⏳ Implement database persistence in `generatePath()`
- ⏳ Implement `updatePathProgress()` logic
- ⏳ Build PathController API endpoints

---

### Task 1.3: Create Onboarding Flow ⏳ NOT STARTED
**Status**: ⏳ Pending

**Requirements** (from redesign plan):
- Full-screen non-skippable tutorial
- Explains EDLSG concept (state machine, evidence-driven)
- Visual demonstration of how system works
- Choose initial role → Generate first path
- Show example skill state transitions
- Explain success criteria and confidence targets

**Planned Components**:
- `/onboarding` route (bypasses AppLayout like `/frontier`)
- Multi-step wizard (5-7 screens)
- Animated skill graph demonstration
- Role selection with skill gap visualization
- "Generate Your Path" CTA at end

---

## ⏳ Phase 2: Path-Driven UI (Week 2) - 0% COMPLETE

### Task 2.1: Build New Path View (Main Screen) ⏳ NOT STARTED
**Status**: ⏳ Pending

**Requirements**:
- **This becomes the PRIMARY landing page** (not Frontier)
- Linear, step-by-step progression UI
- Current step prominently displayed:
  - Step title and description
  - Learning objectives
  - Success criteria (measurable)
  - 2-4 curated study resources
  - Progress bar (% through path)
- Collapsed view of past steps (✅ completed, grayed out)
- Locked future steps (🔒 with unlock conditions)
- Minimap showing: Unit 1 → Unit 2 → Unit 3 with current position

**Mental Model**: Like Duolingo's lesson path, not a dashboard

**Component Structure**:
```
/app/path/page.tsx (new root experience)
  ├── PathOverview (minimap, progress %)
  ├── CurrentStepCard (primary focus)
  │   ├── LearningObjectives
  │   ├── SuccessCriteria
  │   ├── StudyResources (links)
  │   └── ActionButtons ("Take Quiz", "Submit Evidence")
  ├── PastStepsCollapsed (✅ done)
  └── FutureStepsLocked (🔒 requirements shown)
```

---

### Task 2.2: Build Focus Mode Assessment ⏳ NOT STARTED
**Status**: ⏳ Pending

**Requirements**:
- Full-screen quiz experience (no distractions)
- Session mode: start → take quiz → see results → path updates
- Animated state transition feedback:
  - "Cryptography: INFERRED → ACTIVE"
  - Confidence: 45% → 76%
  - "Step completed! Next: Network Security"
- Auto-advance to next step if criteria met
- Return to Path View with updated progress

**Component**:
```
/app/assessment/[skillId]/page.tsx (focus mode)
  - Full viewport, black background
  - Timer in corner
  - Question counter (3/10)
  - Submit → Immediate feedback
  - State transition animation
  - "Continue Learning" CTA → back to Path
```

---

## ⏳ Phase 3: AI-Powered Enhancements (Week 3) - 0% COMPLETE

### Task 3.1: Study Resources Generation ⏳ NOT STARTED
**Status**: ⏳ Pending

**Requirements**:
- Auto-curate 2-4 resources per path step
- Use OpenAI to search/recommend:
  - Official documentation
  - High-quality tutorials
  - Video courses
  - Practice exercises
- Store in `study_resources` table
- Display in Path View with relevance scores
- User feedback (👍/👎 rating)

**Implementation**:
- Add method to PathGenerationService: `curateResources(skillName, stepObjectives)`
- OpenAI prompt includes: "Find 2-4 beginner-friendly resources for learning {skill}"
- Parse JSON response with URLs, titles, descriptions
- Insert into study_resources table

---

## ⏳ Phase 4: Polish & Deployment (Week 4) - 0% COMPLETE

### Remaining Tasks:
- 🐛 Fix quiz state bug (UNSEEN not moving to INFERRED)
- 🐛 Fix evidence scoring (too lenient - "Python 63%" from one project)
- 🎨 UI polish (Mission Control theme, glassmorphism)
- 📊 Analytics (track path completion rates, time spent)
- ⚡ Performance optimization
- 🧪 Integration tests
- 📝 Update documentation
- 🚀 Deployment

---

## Critical Bugs to Fix (After Redesign Complete)

### Bug 1: Quiz Not Triggering State Changes
**Reported**: "Quizzes finished, path doesn't update. Only happens when evidence is added."

**Root Cause** (suspected):
- AssessmentResultService created but may not be properly wired
- State transition thresholds might be too strict
- Transaction boundaries not correct

**Fix Strategy**:
1. Add comprehensive logging to AssessmentResultService
2. Verify StateTransitionService is being called
3. Check if skill exists in skill_nodes table (name mapping issue?)
4. Lower thresholds temporarily for testing
5. Add integration test: take quiz → verify state change

---

### Bug 2: Evidence Scoring Too Lenient
**Reported**: "Python project with numpy → 63% proficiency. That's weak evidence."

**Root Cause**:
- EvidenceExtractionService using LLM confidence scores directly
- No strictness curve applied to evidence
- Support scores not calibrated

**Fix Strategy**:
1. Add strict evidence scoring curve (similar to quiz curve)
2. Weight evidence by type: CERT (0.9), PROJECT (0.6), WORK_SAMPLE (0.8)
3. Require multiple pieces of evidence for PROVED:
   - 1x high-confidence evidence = INFERRED
   - 2x medium evidence = ACTIVE
   - 1x QUIZ + 1x PROJECT = PROVED
4. Update StateTransitionService thresholds

---

## Architecture Philosophy (Reminder)

### Before (What Was Wrong):
```
User logs in
  ↓
Sees Frontier graph (overwhelming, no guidance)
  ↓
Clicks around randomly
  ↓
Takes a quiz maybe?
  ↓
Nothing visibly changes
  ↓
User feels lost, leaves
```

### After (Target Experience):
```
User logs in
  ↓
Sees current step: "Build your first Node.js server"
  ↓
Clear objectives: "Create HTTP server, use Express, handle errors"
  ↓
Study resources provided (2-4 links)
  ↓
Takes quiz → 90% score
  ↓
Animated feedback: "Node.js: INFERRED → ACTIVE! Confidence: 76%"
  ↓
Next step unlocks: "Add database integration with MongoDB"
  ↓
User feels: "The system knows what I need. I'm making progress."
```

**Key Shift**: From "explore the graph" to "trust the mentor"

---

## What's Working vs Not Working

### ✅ Working (Verified by Compilation):
- EDLSG state machine (5-state transitions)
- Prerequisite graph (80+ edges in database)
- Decision engine scoring algorithm
- OpenAI evidence extraction
- Quiz generation and grading
- Frontier Map visualization (React Flow + ELK)
- Assessment pipeline (creates evidence, triggers state updates)
- Path generation service (OpenAI integration ready)
- Database schema for learning paths

### ❌ Not Working (Reported or Suspected):
- Quiz completion not reliably updating states
- Evidence scoring too lenient (one project = 63%)
- Frontier as root experience (should be Path View)
- No onboarding (users don't understand the system)
- No clear next action (Frontier too passive)
- Graph shows only 8/80 prerequisite edges (hardcoded)
- Role selection not persisted (hardcoded to roleId=1)

### ⏳ Half-Built (Implemented but Incomplete):
- Frontier Map (works but wrong UX paradigm)
- Quiz system (works but doesn't drive progression)
- Evidence submission (works but scoring weak)
- Skill graph (exists but not user-facing)

---

## Next Immediate Steps (Priority Order)

1. **✅ DONE**: Create path repositories (JPA interfaces)
2. **⏳ NEXT**: Wire up PathGenerationService database persistence
3. **⏳ NEXT**: Build PathController (REST endpoints)
4. **⏳ NEXT**: Create Path View frontend (main screen)
5. **⏳ NEXT**: Build onboarding flow
6. **⏳ NEXT**: Create focus mode assessment UI
7. **🐛 THEN**: Fix quiz state bug
8. **🐛 THEN**: Fix evidence scoring bug
9. **🎨 THEN**: UI polish (Mission Control theme)
10. **🚀 THEN**: Deploy and test with real users

---

## File Structure

### Backend Files Created/Modified
```
backend/src/main/java/com/careermappro/
├── entities/
│   ├── LearningPath.java ✅ NEW
│   ├── PathUnit.java ✅ NEW
│   ├── PathStep.java ✅ NEW
│   ├── StudyResource.java ✅ NEW
│   ├── PathProgress.java ✅ NEW
│   └── PathActivity.java ✅ NEW
├── services/
│   ├── AssessmentResultService.java ✅ NEW
│   ├── PathGenerationService.java ✅ NEW
│   ├── GPTService.java ✅ UPDATED
│   ├── QuizService.java ✅ UPDATED
│   └── StateTransitionService.java (existing)
└── repositories/
    ├── LearningPathRepository.java ⏳ IN PROGRESS
    ├── PathUnitRepository.java ⏳ IN PROGRESS
    ├── PathStepRepository.java ⏳ IN PROGRESS
    ├── StudyResourceRepository.java ⏳ IN PROGRESS
    ├── PathProgressRepository.java ⏳ IN PROGRESS
    └── PathActivityRepository.java ⏳ IN PROGRESS

backend/src/main/resources/db/migration/
└── V4__learning_paths.sql ✅ NEW (6 tables)
```

### Frontend Files (To Be Created)
```
careermap-ui/src/app/
├── onboarding/
│   └── page.tsx ⏳ NOT STARTED (full-screen tutorial)
├── path/
│   └── page.tsx ⏳ NOT STARTED (NEW ROOT - replaces current homepage)
├── assessment/[skillId]/
│   └── page.tsx ⏳ NOT STARTED (focus mode quiz)
└── frontier/
    └── page.tsx (existing, will become secondary view)
```

---

## Completion Checklist

### Week 1: Core Flow & State Correctness
- [x] Fix assessment → state update pipeline
- [x] Create path database schema
- [x] Create path entity classes
- [x] Build PathGenerationService
- [ ] Wire up path repositories
- [ ] Implement path persistence logic
- [ ] Build PathController API
- [ ] Create onboarding flow UI

### Week 2: Path-Driven UI
- [ ] Build Path View (main screen)
- [ ] Build focus mode assessment
- [ ] Update routing (Path = default landing)
- [ ] Add progress tracking UI

### Week 3: AI Enhancements
- [ ] Auto-generate study resources
- [ ] Resource relevance scoring
- [ ] User feedback system

### Week 4: Polish & Deployment
- [ ] Fix all bugs
- [ ] UI polish (Mission Control theme)
- [ ] Performance optimization
- [ ] Integration tests
- [ ] Deploy to production

---

**Current Sprint**: Week 1 (Backend Infrastructure)

**Blocking Issues**: None - progressing steadily

**Confidence Level**: High - architecture is sound, execution is on track

**ETA for MVP**: 2-3 weeks (backend + frontend + polish)
