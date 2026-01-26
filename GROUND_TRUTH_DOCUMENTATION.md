# CareerMap: Ground-Truth Documentation

**Last Updated:** 2026-01-11
**Version:** 0.2-alpha
**Codebase:** ~5,500 LOC backend (Java), ~2,000 LOC frontend (TypeScript)

---

## 1. What This Is

CareerMap is a **skill evidence tracker** that uses AI to extract technical skills from user submissions (projects, certifications, work samples) and maintains a state machine over those skills based on prerequisite satisfaction.

**It solves this problem:** Traditional resume parsers and skill trackers rely on self-reported proficiency sliders ("Rate your React skills 1-10"). This system instead asks for evidence and infers skill levels from concrete proof.

**Built for:** Individual contributors in technical roles who want structured skill tracking without manual data entry. In practice, the system assumes familiarity with software engineering concepts (prerequisites, dependencies, confidence scores).

**Not for:** Job matching, resume generation, or career coaching. It's a tracker with AI extraction, not an advisor.

---

## 2. Core User Loop (As Implemented)

```
1. User logs in
   ↓
2. Lands on "Frontier" screen
   - Sees a black canvas with skill nodes in a graph
   - Most nodes are gray/locked (UNSEEN state)
   - One node glows blue (recommended action)
   ↓
3. Clicks recommended action button
   - If button says "Assess [Skill]": opens quiz in new tab
   - If button says "Build [Task]": shows alert "coming soon"
   ↓
4. Alternatively: clicks "+ Evidence" button
   - Modal opens
   - User types free-form text: "Built a React app with Redux"
   - Submits
   ↓
5. Backend calls OpenAI GPT-4o-mini
   - Extracts skills: [{name: "React", support: 0.85}, {name: "Redux", support: 0.7}]
   - Normalizes to canonical names from database
   - Creates evidence_skill_links with support scores
   ↓
6. State machine runs
   - Checks if extracted skills have satisfied prerequisites
   - Updates user_skill_states table
   - React: UNSEEN → INFERRED (if prereq JavaScript exists)
   - React: INFERRED → ACTIVE (if JavaScript confidence > 0.9)
   ↓
7. Frontier recomputes
   - Decision engine re-scores all skills
   - New recommended action selected (highest score)
   - Graph refreshes (nodes change color, new recommendation glows)
   ↓
8. User repeats: submit evidence → states update → frontier changes
```

**Loop strength:** The evidence → extraction → state update cycle works end-to-end. OpenAI integration is functional. State transitions are deterministic.

**Loop weakness:** The loop is one-directional (only adds skills, never corrects mistakes). No way to verify OpenAI extractions. No way to remove incorrectly linked skills. If a user submits garbage, it pollutes their skill graph permanently.

**Why users return:** To submit new evidence and watch their skill graph grow. Dopamine loop: submit evidence → see nodes light up → get new recommendation.

**Why users leave:** After 3-5 submissions, most skills are still UNSEEN because the prerequisite graph is sparse. The "recommended action" stays the same because feasibility scores are low for skills with unmet prereqs. Feels stuck.

---

## 3. Primary Screen: Frontier (as Implemented)

### What It Represents

The Frontier is a **projection of the user's skill state onto a graph**, filtered to show only "actionable" skills (skills the user could reasonably work on next). Not a complete skill graph—a curated subset.

### What Data Is Shown

**API Endpoint:** `GET /api/v2/frontier?userId=X`

**Response Structure:**
```json
{
  "role": { "id": 1, "name": "Security Engineer", "category": "Engineering" },
  "frontierPreview": [
    {
      "skillId": 20,
      "skillName": "Linux",
      "status": "ACTIVE",
      "confidence": 0.75,
      "demandWeight": 0.28
    },
    // ... up to 30 skills
  ],
  "highlightedSkill": {
    "id": 20,
    "name": "Linux",
    "status": "ACTIVE",
    "confidence": 0.75,
    "why": "High demand (0.28) and unlocks 8 downstream skills"
  },
  "recommendedAction": {
    "type": "PROBE",
    "label": "Assess Linux",
    "skillId": 20,
    "skillName": "Linux",
    "estimatedMinutes": 15,
    "payload": {}
  }
}
```

**Frontier Inclusion Rules** (`FrontierController.java:67-85`):
1. All ACTIVE skills (confidence > 0, all prereqs met)
2. All INFERRED skills (partial prereq satisfaction)
3. High-demand UNSEEN skills (role weight > 0.15)
4. Limited to 30 nodes total

**Status Color Coding:**
- **PROVED** (green) - Confidence > 0.85, strong evidence
- **ACTIVE** (blue) - Confidence > 0, prerequisites satisfied
- **INFERRED** (yellow, dashed) - Partial evidence or prereqs
- **UNSEEN** (gray, locked) - No evidence, high demand
- **STALE** (red, dashed) - Was proved but decayed (not implemented)

**Visual Hierarchy:**
- Recommended skill: 100% opacity, blue glow (40px blur), slow pulse animation
- Adjacent skills (prereqs + unlocks): 70% opacity, subtle glow
- All other nodes: 15% opacity (near-invisible)

### What Decisions It Helps Make

**Primary decision:** "What should I work on next?"

The recommended action button tells you:
- Which skill to focus on (highest-scoring)
- What type of action (PROBE = quiz, BUILD = task, APPLY = job)
- How long it takes (estimated minutes)

**Secondary decisions:**
- "What skills am I missing?" (gray locked nodes)
- "What skills unlock if I learn X?" (hover/click to see connections)
- "How confident is the system in my abilities?" (confidence %)

**Decisions it does NOT help with:**
- Which evidence to submit (no guidance on quality)
- How to improve low-confidence skills (no learning resources)
- Whether recommendations are actually useful (no explanation of scoring)

### What Actions Are Possible

**1. Click Recommended Action Button**
- `type: "PROBE"` → Opens `/assessment?skillId=X` in new tab
- `type: "BUILD"` → Shows alert "Build mode coming soon"
- `type: "APPLY"` → Opens `/opportunities` in new tab

**2. Click "+ Evidence" Button**
- Opens evidence submission modal
- User types free-form text (2000 char limit in OpenAI call)
- Selects evidence type (PROJECT, QUIZ, REPO, CERT, WORK_SAMPLE)
- Optional: adds source URL
- Submits → OpenAI extraction → state updates

**3. Click Any Skill Node**
- Opens inspector panel (right side overlay)
- Shows: status, confidence %, demand weight
- If recommended node: shows "why" explanation
- No other actions (can't edit, can't delete)

**4. Click Hamburger Menu (top-right)**
- Dropdown shows: Path View, Opportunities, Settings, Sign Out
- Opens legacy screens in current window (not new tab)

**5. Pan/Zoom Canvas**
- Mouse drag to pan
- Scroll to zoom
- Camera auto-centers on recommended node on load

### What Happens After Each Action

**After Submitting Evidence:**
1. Modal closes immediately (optimistic UI)
2. Backend processes for ~2-3 seconds
3. Frontier API called again (`GET /frontier`)
4. Graph nodes re-render with new states
5. New recommended action appears
6. Camera re-centers on new recommendation (800ms smooth transition)

**After Opening Assessment:**
- New tab opens
- Frontier stays open in original tab
- No automatic state update (user must manually submit quiz results as evidence)
- **Disconnect:** Taking quiz doesn't auto-create evidence link

**After Clicking a Node:**
- Inspector panel slides in from right
- Selected node remains highlighted
- Rest of graph stays interactive
- Close inspector with X button or click another node

---

## 4. Backend System Behavior

### Skill State Model

**5 States** (`StateTransitionService.java`):

```
UNSEEN ─────────────────────────────────> INFERRED
  │                                          │
  │                                          │
  │                                          ▼
  └────────────────────────────────────> ACTIVE ──────> PROVED
                                             │              │
                                             │              │
                                             └──────────────┴──> STALE
                                                    (not implemented)
```

**Transition Rules:**

**Rule 1: High-Trust Evidence** (`support > 0.7` from QUIZ or CERT):
- UNSEEN → INFERRED
- INFERRED → PROVED
- ACTIVE → PROVED
- Confidence = max(current, support)
- Evidence score += support × 10

**Rule 2: Medium Evidence** (`support > 0.4` from any source):
- UNSEEN → INFERRED
- Confidence = max(current, support × 0.7)
- Evidence score += support × 5

**Rule 3: Prerequisite Activation** (runs after every evidence update):
- For each INFERRED skill:
  - Load all prereqs from `prereq_edges` table
  - Check HARD prereqs: ALL must have confidence ≥ 0.9
  - Check aggregate: Weighted avg ≥ 0.65
  - If both pass: INFERRED → ACTIVE

**Thresholds** (hardcoded):
- HARD prereq threshold: 0.9
- SOFT prereq threshold: 0.65
- PROVED support threshold: 0.7

**Example:**
```
User submits: "Built React dashboard with TypeScript"
OpenAI extracts: React (0.85), TypeScript (0.8)

React:
  - Current state: UNSEEN
  - Evidence support: 0.85, not high-trust (PROJECT type)
  - Rule 2 applies: UNSEEN → INFERRED
  - Confidence = 0.85 × 0.7 = 0.595
  - Check prereqs:
    - JavaScript (HARD, strength 0.9): user confidence 0.95 ✓
    - HTML (SOFT, strength 0.6): user confidence 0.7 ✓
    - Aggregate: (0.95×0.9 + 0.7×0.6) / 1.5 = 0.85 ≥ 0.65 ✓
  - INFERRED → ACTIVE

TypeScript:
  - Current state: UNSEEN
  - Rule 2 applies: UNSEEN → INFERRED
  - Confidence = 0.56
  - Check prereqs:
    - JavaScript (HARD, strength 0.95): user confidence 0.95 ✓
    - Aggregate: 0.95 ≥ 0.65 ✓
  - INFERRED → ACTIVE
```

### Decision Engine (How Next Action Is Chosen)

**Scoring Formula** (`DecisionEngineService.java:107-116`):
```java
score = demandWeight × unlockPotential × (1 - confidence) × feasibility
```

**Component Breakdown:**

**1. DemandWeight** (0-1, from `role_skill_weights.weight`):
- Market demand for skill in target role
- Curated data per role
- Higher = more valuable for career progression

**2. UnlockPotential** (0-10, calculated):
```java
unlockPotential = Σ (downstream_skill_weight × edge_strength) × 5
```
- For each skill that lists this as a prereq:
  - Multiply downstream skill's role weight by edge strength
  - Sum all products
  - Scale by 5×, cap at 10

Example: JavaScript unlocks TypeScript (0.3 × 0.95), Node.js (0.25 × 0.9), React (0.35 × 0.8)
= (0.285 + 0.225 + 0.28) × 5 = 3.95

**3. Uncertainty** (0-1):
```java
uncertainty = 1 - confidence
```
- Prioritizes low-confidence skills (need assessment)
- If confidence = 0.2, uncertainty = 0.8 (high priority)
- If confidence = 0.9, uncertainty = 0.1 (low priority)

**4. Feasibility** (0-1, calculated):
```java
feasibility = Σ (prereq_confidence × edge_strength) / Σ edge_strength
```
- Weighted average of prereq satisfaction
- If all prereqs have high confidence, feasibility ≈ 1.0
- If prereqs missing, feasibility ≈ 0.0
- If no prereqs, returns 1.0 (leaf node)

**Winner Selection:**
- Score all frontier skills
- Sort descending by score
- Take top skill
- Determine action type based on confidence:
  - < 0.5 → PROBE (quiz)
  - 0.5-0.85 → BUILD (task)
  - > 0.85 → BUILD (already mastered, work on projects)

**Why This Works:**
- High-demand skills with low confidence bubble to top
- Foundational skills (high unlock potential) prioritized
- Skills with unmet prereqs automatically filtered out
- Balances career value (demand) with learning efficiency (feasibility)

**Why This Fails:**
- If prerequisite graph is sparse, unlockPotential ≈ 1.0 for most skills (no differentiation)
- If user has no skills proved, feasibility ≈ 0 for everything (stuck)
- Hardcoded thresholds (0.5, 0.85) may not suit all learning styles

### Evidence Ingestion

**API:** `POST /api/v2/evidence`

**Input:**
```json
{
  "userId": 1,
  "type": "PROJECT",
  "rawText": "Built a React dashboard with Redux state management and TypeScript",
  "sourceUri": "https://github.com/user/project" // optional
}
```

**Processing Pipeline** (`EvidenceExtractionService.java`):

**Step 1: Save Evidence Record**
```sql
INSERT INTO evidence (user_id, type, source_uri, raw_text, created_at)
VALUES (1, 'PROJECT', 'https://...', 'Built a React...', NOW());
```

**Step 2: OpenAI Skill Extraction**

**Model:** `gpt-4o-mini`
**Temperature:** 0.2
**Response Format:** `{ type: "json_object" }` (strict JSON mode)

**Prompt Structure:**
```
[Context based on evidence type]
For type=PROJECT: "This is a project. Extract technical skills demonstrated."
For type=QUIZ: "This is a quiz result. Extract tested skills."

Text:
[User's raw_text, truncated to 2000 chars]

Extract technical skills and return JSON in this EXACT format:
{
  "skills": [
    {
      "name": "JavaScript",
      "support": 0.85,
      "confidence": 0.9
    }
  ]
}

Rules:
- support: how strongly this evidence proves the skill (0-1)
- confidence: your confidence in extraction accuracy (0-1)
- Only specific technical skills (React, Python, AWS)
- Normalize names (JS → JavaScript, React.js → React)
```

**Example Response:**
```json
{
  "skills": [
    {"name": "React", "support": 0.85, "confidence": 0.9},
    {"name": "Redux", "support": 0.7, "confidence": 0.85},
    {"name": "TypeScript", "support": 0.8, "confidence": 0.9}
  ]
}
```

**Step 3: Skill Normalization**

For each extracted skill name:
1. Try exact match against `skill_nodes.canonical_name`
2. Try case-insensitive match
3. Try alias match (e.g., "React.js" → "React" via `skill_nodes.aliases` JSONB column)
4. If no match found: **silently drop the skill** (not added to database)

**Step 4: Create Evidence-Skill Links**
```sql
INSERT INTO evidence_skill_links (evidence_id, skill_id, support, extracted_by, confidence, created_at)
VALUES (123, 20, 0.85, 'openai:gpt-4o-mini', 0.9, NOW());
```

**Step 5: Update Skill States**

For each linked skill, calls `StateTransitionService.updateStateFromEvidence()`:
- Runs transition rules (see State Model section)
- Updates `user_skill_states` table
- Recalculates confidence based on all evidence

**Step 6: Recompute Frontier**
- Checks if any INFERRED skills became ACTIVE (prereq satisfaction)
- Re-runs decision engine to pick new recommended action
- Returns updated frontier to client

**Failure Modes:**

1. **No OpenAI API Key:**
   - Returns empty skills array
   - Evidence saved but no skills extracted
   - User sees "Evidence processed successfully" (lie)

2. **OpenAI Returns Invalid JSON:**
   - Catches exception, logs error
   - Returns empty skills array
   - Same silent failure as #1

3. **Skill Not in Ontology:**
   - Normalization returns null
   - Skill dropped from results
   - No feedback to user (which skills were recognized)

4. **OpenAI Extracts Garbage:**
   - No validation of skill names
   - Could link evidence to wrong skill
   - No way to undo

### Use of OpenAI

**Where:**
- `EvidenceExtractionService.extractSkillsFromText()` (line 101-123)

**For What:**
- Convert free-form text → structured skill list with confidence scores
- Example: "I built a web app" → `[{name: "Web Development", support: 0.6}]`

**Not Used For:**
- Quiz generation (separate legacy system)
- Career advice
- Job matching
- Skill recommendations (pure algorithm)

**Cost:**
- Model: `gpt-4o-mini` (~$0.15 per 1M input tokens, ~$0.60 per 1M output tokens)
- Average call: 500 input tokens (prompt + user text), 100 output tokens (JSON array)
- Per evidence submission: ~$0.00001 (effectively free)

**Guarantees:**
- JSON mode enforced (`response_format: { type: "json_object" }`)
- But no schema validation—OpenAI can return `{"skills": "oops"}` and code would crash
- Fallback: empty array on any error

**Prompt Engineering:**
- Temperature 0.2 (low variance, consistent)
- Context-aware prompts (different for PROJECT vs QUIZ vs CERT)
- Explicit instructions: "only technical skills", "normalize names"
- No few-shot examples (relies on base model knowledge)

**Quality Control:**
- None. No human-in-loop. No validation. No ability to dispute extractions.

---

## 5. Data Model (Only What's Actually Used)

### Core Tables

**`skill_nodes`** (70 skills seeded):
```sql
skill_node_id    INT PRIMARY KEY
canonical_name   VARCHAR(100) UNIQUE  -- "React", "Python"
aliases          JSONB                -- ["React.js", "ReactJS"]
domain           VARCHAR(50)          -- "Frontend", "Backend"
difficulty       INT                  -- 1-10
decay_half_life_days INT              -- 180 (not used yet)
```
**Drives:** Skill normalization, alias matching, domain categorization

**`prereq_edges`** (80 edges seeded):
```sql
edge_id         INT PRIMARY KEY
from_skill_id   INT → skill_nodes    -- JavaScript
to_skill_id     INT → skill_nodes    -- React
type            ENUM('HARD','SOFT')  -- HARD = required, SOFT = helpful
strength        DECIMAL(3,2)         -- 0.95 (very strong prereq)
```
**Drives:** Unlock potential calculation, feasibility scoring, ACTIVE state transitions

**`role_skill_weights`** (213 rows seeded):
```sql
weight_id       INT PRIMARY KEY
role_id         INT → roles
skill_id        INT → skill_nodes
weight          DECIMAL(3,2)         -- 0.35 (demand weight, 0-1)
required_level  INT                  -- 7 (target proficiency, 0-10, not used)
source          VARCHAR(50)          -- "MANUAL" (would be "MARKET_API")
```
**Drives:** Demand weight in scoring formula, frontier inclusion (weight > 0.15)

**`user_skill_states`** (THE CORE TABLE):
```sql
state_id            INT PRIMARY KEY
user_id             INT → users
skill_id            INT → skill_nodes
status              ENUM('UNSEEN','INFERRED','ACTIVE','PROVED','STALE')
confidence          DECIMAL(3,2)     -- 0.75 (0-1 score)
evidence_score      DECIMAL(10,2)    -- 45.3 (cumulative points)
last_evidence_at    TIMESTAMP        -- last time evidence submitted
stale_at            TIMESTAMP        -- when to decay (not enforced)
```
**Drives:** Everything. State machine, confidence tracking, frontier filtering.

**`evidence`** (user submissions):
```sql
evidence_id     INT PRIMARY KEY
user_id         INT → users
type            ENUM('PROJECT','QUIZ','REPO','CERT','WORK_SAMPLE')
source_uri      VARCHAR(500)         -- GitHub URL, cert link, etc
raw_text        TEXT                 -- free-form user description
created_at      TIMESTAMP
```
**Drives:** Audit trail, evidence browsing (not implemented)

**`evidence_skill_links`** (many-to-many):
```sql
link_id         INT PRIMARY KEY
evidence_id     INT → evidence
skill_id        INT → skill_nodes
support         DECIMAL(3,2)         -- 0.85 (how strongly evidence proves skill)
extracted_by    VARCHAR(50)          -- "openai:gpt-4o-mini"
confidence      DECIMAL(3,2)         -- 0.9 (extraction confidence)
created_at      TIMESTAMP
```
**Drives:** State transitions (support → confidence updates), evidence scoring

### Unused Tables (Legacy V1)

- **`skills`** - Old flat skill list (migrated to skill_nodes)
- **`proficiencies`** - Old manual proficiency scores (migrated to user_skill_states)
- **`role_skill`** - Old many-to-many (migrated to role_skill_weights)

These tables still exist but are not read by V2 APIs.

### What Data Drives Decisions

**For "What Should I Work On Next?":**
- `user_skill_states.status` (only ACTIVE/INFERRED eligible)
- `user_skill_states.confidence` (uncertainty factor in scoring)
- `role_skill_weights.weight` (demand weight in scoring)
- `prereq_edges` (unlock potential + feasibility)

**For "Am I Ready for This Skill?":**
- `prereq_edges.type` (HARD = must have, SOFT = helpful)
- `prereq_edges.strength` (0.9 = very important, 0.5 = somewhat important)
- `user_skill_states.confidence` for prereq skills

**For "What Skills Did I Prove?":**
- `user_skill_states.status = 'PROVED'`
- `evidence_skill_links.support` (why it's proved)

### What Data Is Just Displayed

- `skill_nodes.domain` - Shown in UI but not used in algorithms
- `skill_nodes.difficulty` - Stored but ignored
- `role_skill_weights.required_level` - Stored but ignored
- `evidence.source_uri` - Saved but never shown
- `evidence.created_at` - Saved but never shown

---

## 6. Secondary Screens / Modes

### Path View (Legacy)
**URL:** `/`
**API:** `GET /api/v1/path`

**What It Shows:**
- List of all skills for selected role
- Readiness % per skill (based on proficiency scores)
- Overall readiness % for role
- "Start Quiz" button per skill

**Why It Exists:**
- This was the original UI before Frontier was built
- Still functional, pulls from legacy V1 tables
- Users expect familiar list view

**How Often Used:**
- Rarely. Most users land on Frontier first.
- Accessed via hamburger menu → "Path View"
- Feels outdated compared to graph UI

**Problems:**
- Shows different data than Frontier (different API, different state model)
- Role selection works here but is ignored by Frontier
- Readiness % calculation is manual proficiency / 10 (not evidence-based)

### Assessment Page
**URL:** `/assessment?skillId=X`
**Controller:** `QuizController.java` (legacy)

**What It Shows:**
- Multiple-choice quiz for specific skill
- 5-10 questions pulled from `quiz_questions` table
- Submit answers → score shown
- "Back to Path" button

**Why It Exists:**
- PROBE action type needs somewhere to route to
- Quiz completion should → confidence boost
- Legacy system from V1

**How Often Used:**
- Only when recommended action type = PROBE
- Opens in new tab (not modal)
- User must manually copy quiz results and submit as evidence

**Problems:**
- Taking quiz doesn't auto-update skill states
- No API integration between QuizController and StateTransitionService
- User has to submit quiz results as evidence manually (broken loop)

### Opportunities Page
**URL:** `/opportunities`
**What It Shows:**
- Fake job listings (hardcoded)
- "Apply" buttons that do nothing

**Why It Exists:**
- Placeholder for future job matching feature
- Recommended action type APPLY routes here

**How Often Used:**
- Never. No real functionality.

### Settings Page
**URL:** `/settings`
**What It Shows:**
- Empty page with "Settings coming soon"

**Why It Exists:**
- Standard expectation for any app
- Future: skill ontology management, role selection, OpenAI API key input

**How Often Used:**
- Never.

---

## 7. What the App Does NOT Do

### Explicitly Unsupported

1. **Job Matching:** No integration with job boards, no resume parsing from JDs
2. **Learning Resources:** No course recommendations, no tutorial links
3. **Skill Verification:** No third-party validation (LinkedIn, GitHub, etc)
4. **Career Coaching:** No personalized advice, no goal setting
5. **Team/Organization:** No manager view, no team skill gaps
6. **Skill Discovery:** Can't add new skills if not in ontology
7. **Evidence Editing:** Can't delete or modify submitted evidence
8. **Export:** Can't generate resume, can't export skill data
9. **Analytics:** No historical trends, no skill growth charts
10. **Social:** No sharing, no profiles, no recommendations from peers

### Assumptions Made

1. **User knows their target role:** System doesn't help you discover what role fits
2. **User can describe evidence:** Assumes literacy in technical terminology
3. **Skills are in ontology:** 70 skills seeded, can't add more without DB access
4. **Prerequisites are correct:** No way to dispute or customize prereq edges
5. **OpenAI extractions are accurate:** No validation, no correction flow
6. **English only:** No i18n, prompt hardcoded in English
7. **Single role per user:** Can't track multiple career paths
8. **Evidence is honest:** No verification, no fraud detection

### Intentionally Missing (Design Choices)

1. **No manual skill entry:** Must submit evidence, can't just claim skills
2. **No proficiency sliders:** Confidence comes from evidence, not self-rating
3. **No learning paths:** Shows next action, not full roadmap
4. **No time estimates:** Says "15 min" for quizzes but no total time to mastery
5. **No skill browsing:** Can't explore full ontology, only see frontier
6. **No undo:** State transitions are one-way, can't reset
7. **No explanations:** Scoring formula is black box to user

---

## 8. Known Rough Edges / Incompleteness

### UX Gaps

1. **Silent OpenAI Failures:**
   - If API key missing, evidence accepted but skills not extracted
   - User sees "Success!" but nothing changed
   - No feedback about which skills were recognized

2. **Evidence Quality Unknown:**
   - No guidance on what makes good evidence
   - "I know React" vs "Built 5 React apps for 100k users" both accepted
   - No examples, no templates

3. **Stuck States:**
   - If all frontier skills need prereqs, no actionable recommendations
   - User doesn't know what evidence to submit to unblock
   - No "onboarding flow" to seed initial skills

4. **Graph Is Hardcoded:**
   - Frontend shows 8 edge relationships
   - Backend uses 80+ edges for calculations
   - User sees incomplete graph, doesn't match recommendations

5. **No Error Recovery:**
   - If OpenAI links wrong skill, can't undo
   - If submitted evidence for wrong skill, can't delete
   - State machine has no reverse transitions

6. **Role Selection Broken:**
   - User can select role in Path View
   - Frontier ignores it, hardcodes to role 1
   - No feedback that selection didn't work

### Conceptual Gaps

1. **Confidence vs Evidence Score:**
   - Two separate metrics: confidence (0-1), evidence_score (unbounded)
   - Evidence score not shown to user
   - Unclear which matters more

2. **Support vs Confidence (from OpenAI):**
   - Support = how strong the evidence is
   - Confidence = how sure OpenAI is about extraction
   - Both stored, but only support used in state transitions
   - OpenAI confidence is ignored

3. **HARD vs SOFT Prerequisites:**
   - HARD prereqs must be > 0.9 confidence
   - SOFT prereqs averaged into aggregate > 0.65
   - No way to dispute classification (is JavaScript HARD for React?)

4. **Action Types Don't Make Sense:**
   - PROBE = quiz (makes sense)
   - BUILD = mini-task (not implemented)
   - APPLY = job search (not integrated)
   - Why no "LEARN" action (take a course)?

5. **Unlock Potential Is Fragile:**
   - Depends on complete prerequisite graph
   - With 80 edges for 70 skills, most nodes are leaves
   - Returns 1.0 for skills that don't unlock anything (no signal)

### Features That Exist But Don't Feel Useful

1. **Skill Graph Visualization:**
   - Looks impressive, only shows 8 connections
   - Can't zoom to see node details (text too small)
   - 70% of nodes ghosted at 15% opacity (why show them?)

2. **Inspector Panel:**
   - Clicking a node shows: status, confidence, demand
   - But user already knows this from visual encoding (color = status, size = demand)
   - "Why" explanation only for recommended node (others have none)

3. **Evidence Type Selection:**
   - PROJECT, QUIZ, REPO, CERT, WORK_SAMPLE
   - Only QUIZ and CERT get bonus weighting (support > 0.7 threshold)
   - Other types treated identically
   - Why 5 options if 3 do nothing?

4. **Recommended Action Bottom Strip:**
   - Shows next action twice (in strip + on button)
   - Estimated time shown but not accurate (always 15 min for quizzes)
   - Takes 40% of screen width, could be smaller

5. **State Machine Complexity:**
   - 5 states (UNSEEN, INFERRED, ACTIVE, PROVED, STALE)
   - STALE never reached (decay not implemented)
   - INFERRED vs ACTIVE distinction unclear to users
   - 3 states would suffice: UNKNOWN, LEARNING, MASTERED

---

## Final Assessment

### What's Genuinely Built and Works

✅ **End-to-end evidence pipeline:** Submit text → OpenAI → skills extracted → states updated → graph refreshes
✅ **Prerequisite-aware state machine:** Won't recommend React until JavaScript proved
✅ **Multi-factor recommendation scoring:** Balances demand, unlock potential, confidence, feasibility
✅ **Skill normalization:** "React.js" → "React" works via aliases
✅ **Clean REST API:** Frontend/backend decoupled, standard conventions
✅ **Database migrations:** Flyway versioned schema, can reproduce

### What's Half-Built

⚠️ **Graph visualization:** ReactFlow + ELK layout works, but edges are hardcoded (8 shown, 80 exist)
⚠️ **Role selection:** UI exists in Path View, but Frontier ignores it (hardcoded to role 1)
⚠️ **Assessment integration:** Quiz page exists, but taking quiz doesn't auto-update states
⚠️ **BUILD actions:** Referenced in code, alert shown, no implementation
⚠️ **Skill decay:** STALE state exists, decay job never runs

### What's Missing Entirely

❌ **Evidence quality feedback:** No guidance, no validation, no correction flow
❌ **Error handling:** OpenAI failures silent, skill mismatches hidden
❌ **Skill ontology management:** Can't add skills, can't edit prereqs, locked to 70 seeded skills
❌ **Learning resources:** No courses, no tutorials, no next steps beyond "take quiz"
❌ **Analytics/history:** No charts, no trends, no progress tracking
❌ **Export:** Can't generate resume, can't share skill profile

### The Core Innovation That Works

**Evidence-driven confidence tracking with prerequisite-aware recommendations.**

This is genuinely better than manual proficiency sliders. The state machine prevents premature advancement (can't claim React without JavaScript). The decision engine balances multiple factors (demand × unlock potential × uncertainty × feasibility). The OpenAI integration removes manual tagging.

The algorithm is solid. The data model is clean. The state transitions are deterministic.

### The Problem: UX vs Algorithm Disconnect

The impressive parts (graph viz, glow effects, animations) are disconnected from the working parts (state machine, scoring, evidence pipeline).

- Graph shows 8 edges, algorithm uses 80
- Recommended action chosen by complex formula, user sees "Take quiz" with no explanation
- Evidence quality matters hugely, user gets no feedback
- OpenAI can fail silently, user thinks it worked

**To ship this:**
1. Connect frontend graph to backend `prereq_edges` table
2. Show OpenAI extraction results before saving (let user confirm/edit)
3. Implement role selection or remove the UI
4. Complete BUILD action workflow or remove from recommendation types
5. Add onboarding flow to seed initial skills
6. Surface scoring factors ("This is recommended because: high demand + unlocks 8 skills")

### What Would Make This Production-Ready

**Minimum viable fixes:**
- Fix graph edge rendering (API call, not hardcoded map)
- Fix role selection (read from user, not hardcoded ID)
- Add error handling for OpenAI (show which skills extracted, warn if none)
- Remove BUILD action type (not ready) or implement it
- Add evidence quality feedback (examples, validation, confirmation)

**To make it actually useful:**
- Complete prerequisite graph (200+ edges, cover all 70 skills)
- Add skill discovery (search, browse, request new skills)
- Add evidence editing (delete, modify, dispute)
- Implement skill decay (STALE state) or remove it
- Link assessments to evidence (auto-create evidence on quiz completion)
- Add learning resources (course links, tutorial recs)

**To make it compelling:**
- Build out BUILD mode (project suggestions, task templates)
- Add analytics (skill growth over time, evidence timeline)
- Export to resume/LinkedIn
- Multi-role tracking
- Social proof (show others' evidence for same skill)

### Honest Take

You built a working skill inference engine with a beautiful but deceptive UI.

The backend algorithm (evidence → extraction → state machine → recommendation) is smarter than most resume parsers. The prerequisite awareness prevents false positives. The multi-factor scoring balances career value with learning feasibility.

But the frontend graph is theater. It shows 8% of the relationships the algorithm uses. The glow effects and animations suggest completeness that doesn't exist. The user experience feels magical until you try to use it for real career planning.

The gap between "impressive demo" and "useful tool" is:
1. Completing the prerequisite graph (10× more edges)
2. Surfacing algorithm transparency (why this recommendation?)
3. Adding quality controls (evidence validation, extraction confirmation)
4. Closing the loop (quiz → evidence, build → evidence)

If you close those gaps, this becomes genuinely innovative. Until then, it's a sophisticated tech demo with 30% of the connective tissue missing.

---

**Key Code Locations:**

Backend:
- State machine: `backend/src/main/java/com/careermappro/services/StateTransitionService.java` (177 lines)
- Decision engine: `backend/src/main/java/com/careermappro/services/DecisionEngineService.java` (212 lines)
- OpenAI integration: `backend/src/main/java/com/careermappro/services/EvidenceExtractionService.java` (358 lines)
- Main API: `backend/src/main/java/com/careermappro/controllers/FrontierController.java` (214 lines)

Frontend:
- Frontier UI: `careermap-ui/src/app/frontier/page.tsx` (465 lines)
- Graph node: `careermap-ui/src/app/frontier/SkillNode.tsx` (98 lines)
- Evidence modal: `careermap-ui/src/app/frontier/EvidenceModal.tsx` (145 lines)

Database:
- Schema: `backend/src/main/resources/db/migration/V2__edlsg_schema.sql`
- Prereq edges: `backend/src/main/resources/db/migration/V3__expand_prereq_graph.sql`
- Seed data: `backend/src/main/resources/db/migration/V4__seed_data.sql`

**Lines of Code:**
- Backend: ~5,500 LOC Java
- Frontend: ~2,000 LOC TypeScript
- Database: ~800 lines SQL
- Total: ~8,300 LOC

**Last Reviewed:** 2026-01-11
