# Assessment Pipeline Fix - Completed

## Problem Identified

**Critical Bug**: Quiz completion was not updating the EDLSG state machine (`user_skill_states` table).

### Root Cause
[QuizService.java:138](backend/src/main/java/com/careermappro/services/QuizService.java#L138) was calling `updateUserProficiency()` which only updated the **legacy V1 `proficiencies` table**, not the **V2 `user_skill_states` table** that the EDLSG system uses.

```java
// OLD CODE (line 138)
updateUserProficiency(quiz.getUserId(), quiz.getSkillName(), proficiencyAwarded, score);

// This method updated WRONG table:
private void updateUserProficiency(...) {
    proficiencyRepository.save(prof); // ← Updates 'proficiencies' table
}
```

**Impact**: Taking quizzes didn't trigger state transitions (UNSEEN → INFERRED → ACTIVE → PROVED). The Frontier Map never updated after assessments.

---

## Solution Implemented

### 1. Created `AssessmentResultService.java`

**Location**: [backend/src/main/java/com/careermappro/services/AssessmentResultService.java](backend/src/main/java/com/careermappro/services/AssessmentResultService.java)

**Purpose**: Bridge quiz results with the EDLSG state machine.

**Flow**:
```
Quiz Completed
    ↓
processQuizResult()
    ↓
1. Apply Strict Scoring Curve
    ↓
2. Auto-Create Evidence Record (type=QUIZ)
    ↓
3. Link Evidence to Skill (with support score)
    ↓
4. Trigger State Machine Update (StateTransitionService)
    ↓
5. Recompute Frontier (check if prereqs unlock new skills)
    ↓
Return AssessmentImpact DTO
```

### 2. Strict Scoring Curve (Makes PROVED Harder)

**Design Philosophy**: Make PROVED status meaningful by requiring high performance.

```java
Raw Score    → Support Score
95-100%      → 0.85-1.0  (Perfect - can trigger PROVED)
85-95%       → 0.70-0.85 (Strong - at PROVED threshold)
70-85%       → 0.50-0.70 (Passing - ACTIVE/INFERRED likely)
50-70%       → 0.30-0.50 (Weak - may stay INFERRED)
<50%         → 0.0-0.30  (Poor - minimal impact)
```

**Key Threshold** (from [StateTransitionService.java:26](backend/src/main/java/com/careermappro/services/StateTransitionService.java#L26)):
```java
PROVED_SUPPORT_THRESHOLD = 0.7
```

This means:
- **85%+ quiz score** → support ≥ 0.7 → can reach PROVED (if prereqs met)
- **70-85% quiz score** → support 0.5-0.7 → likely ACTIVE
- **<70% quiz score** → support <0.5 → likely stays INFERRED

**Why Non-Linear?**
We penalize scores below 85% more aggressively because PROVED should represent genuine mastery, not "just passed."

### 3. Auto-Evidence Creation

Every quiz now automatically creates:

**Evidence Record**:
```java
Evidence {
    userId: <user_id>,
    type: QUIZ,
    rawText: "Quiz: JavaScript (Intermediate) | Score: 9/10 (90%) | Completed: 2026-01-11T...",
    sourceUri: "quiz://assessment/javascript-intermediate"
}
```

**Evidence-Skill Link**:
```java
EvidenceSkillLink {
    evidenceId: <evidence_id>,
    skillId: <skill_node_id>,
    support: 0.76,              // Calculated from strict curve
    extractedBy: "assessment-service",
    confidence: 1.0             // We're certain (not LLM)
}
```

### 4. Updated `QuizService.java`

**Changes**:

1. **Added Dependencies** (lines 7, 21-22, 28-29):
```java
import com.careermappro.repositories.SkillNodeRepository;
private final SkillNodeRepository skillNodeRepository;
private final AssessmentResultService assessmentResultService;
```

2. **New Method** `processAssessmentImpact()` (lines 208-244):
   - Looks up `skillNodeId` from `skillName` (canonical name)
   - Delegates to `AssessmentResultService.processQuizResult()`
   - Handles missing skills gracefully (logs warning, returns no-op)

3. **Updated** `submitQuiz()` (lines 144-156):
```java
// NEW: Update EDLSG state machine
AssessmentResultService.AssessmentImpact impact = processAssessmentImpact(...);

// LEGACY: Still update old proficiencies table for backwards compatibility
// TODO: Remove this once all systems migrate to user_skill_states
updateUserProficiency(...);
```

4. **Enhanced Response** (lines 181-189):
```json
{
  "quizId": 123,
  "score": 90.0,
  "correctCount": 9,
  "totalQuestions": 10,
  "proficiencyAwarded": 8.5,
  "breakdown": [...],
  "timeTaken": 180,
  "stateTransition": {
    "stateChanged": true,
    "oldStatus": "INFERRED",
    "newStatus": "ACTIVE",
    "oldConfidence": 0.45,
    "newConfidence": 0.76,
    "supportAwarded": 0.76
  }
}
```

---

## What This Fixes

### Before (Broken):
```
User takes Cryptography quiz (90% score)
    ↓
Updates 'proficiencies' table (legacy V1)
    ↓
'user_skill_states' table UNCHANGED
    ↓
Frontier Map doesn't update
    ↓
User sees no progress
```

### After (Fixed):
```
User takes Cryptography quiz (90% score)
    ↓
Creates Evidence record (type=QUIZ)
    ↓
Links to Cryptography skill (support=0.76)
    ↓
StateTransitionService.updateStateFromEvidence()
    ↓
State: UNSEEN → INFERRED or INFERRED → ACTIVE
    ↓
Frontier Map refetched, graph updates
    ↓
User sees: "Cryptography moved to ACTIVE! Confidence: 76%"
```

---

## Testing Checklist

**To verify the fix works**:

1. ✅ **Compilation**: `./gradlew compileJava` → BUILD SUCCESSFUL
2. ⏳ **Start Backend**: `./gradlew bootRun`
3. ⏳ **Take Quiz**:
   - Navigate to `/assessment` in frontend
   - Take a quiz for a skill (e.g., "JavaScript")
   - Submit answers
4. ⏳ **Verify State Change**:
   - Check response includes `stateTransition` object
   - Verify `stateChanged: true` if score ≥ 50%
   - Check `user_skill_states` table directly:
     ```sql
     SELECT status, confidence FROM user_skill_states
     WHERE user_id = ? AND skill_id = (
       SELECT skill_node_id FROM skill_nodes WHERE canonical_name = 'JavaScript'
     );
     ```
5. ⏳ **Verify Evidence Created**:
   ```sql
   SELECT * FROM evidence WHERE user_id = ? ORDER BY created_at DESC LIMIT 1;
   SELECT * FROM evidence_skill_links WHERE evidence_id = ?;
   ```
6. ⏳ **Verify Frontier Updates**:
   - Navigate to `/frontier`
   - Check if the tested skill's node changed color/opacity
   - Verify confidence value updated

---

## Files Modified

| File | Changes |
|------|---------|
| [AssessmentResultService.java](backend/src/main/java/com/careermappro/services/AssessmentResultService.java) | **NEW** - Core service bridging assessments to EDLSG |
| [QuizService.java](backend/src/main/java/com/careermappro/services/QuizService.java) | Updated to use AssessmentResultService, added state feedback to response |

---

## Next Steps (From Redesign Plan)

### Immediate (Week 1):
- [ ] **Test the fix** (manual + integration tests)
- [ ] **Task 1.2**: Build Path Generation Service (OpenAI-powered curriculum)
- [ ] **Task 1.3**: Create Onboarding Flow (full-screen, non-skippable tutorial)

### Week 2:
- [ ] **Task 2.1**: Build New Path View (main screen showing linear progression)
- [ ] **Task 2.2**: Build Focus Mode Assessment (full-screen quiz experience)

### Week 3+:
- [ ] AI-powered study resources
- [ ] Polish and deployment

---

## Technical Notes

### Why Keep Legacy `proficiencies` Table?

The old `updateUserProficiency()` call is still executed for **backwards compatibility**:
- Old dashboard/reports may still read from `proficiencies` table
- Gradual migration path (can remove later)
- No harm in keeping both updated during transition

### Transaction Safety

Both `AssessmentResultService.processQuizResult()` and `QuizService.submitQuiz()` are `@Transactional`:
- If evidence creation fails → quiz result rolled back
- If state update fails → evidence rolled back
- **All-or-nothing** guarantee

### Skill Name Mapping

Quiz uses `skillName` (String), but EDLSG uses `skillNodeId` (Integer):
```java
Optional<SkillNode> skillNode = skillNodeRepository.findByCanonicalName(skillName);
```

If skill not found in `skill_nodes` table:
- Logs warning to console
- Returns no-op impact (no state change)
- Quiz still completes successfully (fail-safe)

### Support Score vs Confidence

**Support Score**: How much evidence supports a skill (0.0-1.0)
- Calculated from quiz performance + strict curve
- Stored in `evidence_skill_links.support`
- Used by StateTransitionService for transitions

**Confidence**: System's certainty about user's skill level (0.0-1.0)
- Aggregated from all evidence for that skill
- Stored in `user_skill_states.confidence`
- Used by DecisionEngine for scoring

---

## Success Metrics

This fix is successful if:

1. **State transitions work**: Taking quizzes changes skill status (UNSEEN → INFERRED → ACTIVE → PROVED)
2. **Frontier updates**: Graph reflects quiz results immediately after submission
3. **Evidence trail exists**: Every quiz creates evidence + link records
4. **Strict scoring works**: Only high scores (85%+) can reach PROVED
5. **UI feedback visible**: Response includes state transition details

---

**Status**: ✅ **IMPLEMENTATION COMPLETE** | ⏳ **TESTING PENDING**

**Fixes**: Critical bug preventing assessment → state updates
**Adds**: Strict scoring curve, auto-evidence creation, state feedback
**Impact**: Makes EDLSG system actually work with assessments
