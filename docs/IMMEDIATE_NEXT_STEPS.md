# Immediate Next Steps - Quick Win Checklist

**Goal**: Get the MVP running end-to-end in 2-3 days

---

## 🎯 Today's Tasks (Most Important)

### 1. Wire Up PathController Database (2-3 hours)
**Why First**: Everything else depends on this working

**File**: `backend/src/main/java/com/careermappro/controllers/PathController.java`

**What to Do**:
```java
// Add repository injections
private final LearningPathRepository learningPathRepository;
private final PathUnitRepository pathUnitRepository;
private final PathStepRepository pathStepRepository;
private final StudyResourceRepository studyResourceRepository;
private final PathProgressRepository pathProgressRepository;

// Update constructor
public PathController(
    PathService pathService,
    PathGenerationService pathGenerationService,
    LearningPathRepository learningPathRepository,
    PathUnitRepository pathUnitRepository,
    PathStepRepository pathStepRepository,
    StudyResourceRepository studyResourceRepository,
    PathProgressRepository pathProgressRepository
) {
    // ... assign all fields
}

// Replace mock data in each endpoint
@GetMapping("/learning-path/{pathId}/units")
public List<Map<String, Object>> getPathUnits(@PathVariable Integer pathId) {
    List<PathUnit> units = pathUnitRepository.findByPathIdOrderByUnitNumber(pathId);
    return units.stream()
        .map(unit -> {
            Map<String, Object> map = new HashMap<>();
            map.put("unitId", unit.getUnitId());
            map.put("title", unit.getTitle());
            map.put("status", unit.getStatus().toString());
            // ... map all fields
            return map;
        })
        .collect(Collectors.toList());
}
```

**Test**: Hit endpoints, verify real data returns (not mocks)

---

### 2. Implement Path Generation Database Persistence (3-4 hours)
**Why**: This creates the actual learning paths

**File**: `backend/src/main/java/com/careermappro/services/PathGenerationService.java`

**Current** (lines 90-140):
```java
// Step 4: Create database records
// TODO: Implement database persistence
```

**Replace With**:
```java
@Transactional
public Integer generatePath(Integer userId, Integer roleId, String roleName, Map<Integer, SkillState> userSkills) {

    // Step 1-3: Build prompt + call OpenAI (already done)
    String response = gptService.generateCompletion(systemPrompt, pathPrompt, 4000);
    JsonNode pathJson = objectMapper.readTree(response);

    // Step 4: Create LearningPath record
    LearningPath path = new LearningPath(userId, roleId, pathJson.get("title").asText());
    path.setDescription(pathJson.get("description").asText());
    path.setDifficulty(LearningPath.Difficulty.valueOf(pathJson.get("difficulty").asText()));
    path.setGenerationPrompt(pathPrompt);
    path.setStartedAt(LocalDateTime.now());
    path = learningPathRepository.save(path);

    // Step 5: Create PathUnit records
    JsonNode unitsJson = pathJson.get("units");
    int totalSteps = 0;
    for (int i = 0; i < unitsJson.size(); i++) {
        JsonNode unitJson = unitsJson.get(i);

        PathUnit unit = new PathUnit(path.getPathId(), i + 1, unitJson.get("title").asText());
        unit.setDescription(unitJson.get("description").asText());
        unit.setEstimatedHours(unitJson.get("estimatedHours").asInt());

        // First unit is AVAILABLE, rest are LOCKED
        if (i == 0) {
            unit.setStatus(PathUnit.UnitStatus.AVAILABLE);
            unit.setUnlockedAt(LocalDateTime.now());
        }

        unit = pathUnitRepository.save(unit);

        // Step 6: Create PathStep records
        JsonNode stepsJson = unitJson.get("steps");
        for (int j = 0; j < stepsJson.size(); j++) {
            JsonNode stepJson = stepsJson.get(j);

            // Look up skill by name
            String skillName = stepJson.get("skillName").asText();
            Optional<SkillNode> skillNode = skillNodeRepository.findByCanonicalName(skillName);
            if (skillNode.isEmpty()) {
                System.err.println("Skill not found: " + skillName + ", skipping step");
                continue;
            }

            PathStep step = new PathStep(unit.getUnitId(), skillNode.get().getSkillNodeId(), j + 1, stepJson.get("title").asText());
            step.setDescription(stepJson.get("description").asText());
            step.setLearningObjectives(stepJson.get("learningObjectives").asText());
            step.setSuccessCriteria(stepJson.get("successCriteria").asText());
            step.setConfidenceTarget(new BigDecimal(stepJson.get("confidenceTarget").asDouble()));

            // First step of first unit is AVAILABLE
            if (i == 0 && j == 0) {
                step.setStatus(PathStep.StepStatus.AVAILABLE);
                step.setUnlockedAt(LocalDateTime.now());
            }

            step = pathStepRepository.save(step);
            totalSteps++;

            // Step 7: Create StudyResource records
            JsonNode resourcesJson = stepJson.get("resources");
            for (JsonNode resourceJson : resourcesJson) {
                StudyResource resource = new StudyResource(
                    step.getStepId(),
                    StudyResource.ResourceType.valueOf(resourceJson.get("type").asText()),
                    resourceJson.get("title").asText(),
                    resourceJson.get("url").asText(),
                    StudyResource.Difficulty.valueOf(resourceJson.get("difficulty").asText())
                );
                resource.setDescription(resourceJson.get("description").asText());
                resource.setEstimatedTimeMinutes(resourceJson.get("estimatedTimeMinutes").asInt());
                studyResourceRepository.save(resource);
            }
        }
    }

    // Step 8: Create PathProgress record
    PathProgress progress = new PathProgress(path.getPathId(), userId);
    progress.setTotalUnits(unitsJson.size());
    progress.setTotalSteps(totalSteps);
    progress.setLastActivityAt(LocalDateTime.now());
    pathProgressRepository.save(progress);

    // Step 9: Log activity
    PathActivity activity = new PathActivity(userId, path.getPathId(), PathActivity.ActivityType.PATH_STARTED);
    activity.setDescription("Generated learning path: " + path.getTitle());
    pathActivityRepository.save(activity);

    return path.getPathId();
}
```

**Test**: Call `/api/v1/learning-path/generate`, check database tables populated

---

## 🔥 Tomorrow's Tasks

### 3. Implement Step Completion Logic (2-3 hours)
**File**: `PathGenerationService.java` → `updatePathProgress()` method

**Pseudo-code**:
```java
@Transactional
public void updatePathProgress(Integer userId, Integer stepId) {
    // 1. Get current step
    PathStep step = pathStepRepository.findById(stepId).orElseThrow();

    // 2. Check if success criteria met
    UserSkillState skillState = userSkillStateRepository
        .findByUserIdAndSkillId(userId, step.getSkillId())
        .orElse(null);

    if (skillState == null || skillState.getConfidence() < step.getConfidenceTarget().doubleValue()) {
        throw new RuntimeException("Success criteria not met yet");
    }

    // 3. Mark step as COMPLETED
    step.setStatus(PathStep.StepStatus.COMPLETED);
    step.setCompletedAt(LocalDateTime.now());
    pathStepRepository.save(step);

    // 4. Get parent unit
    PathUnit unit = pathUnitRepository.findById(step.getUnitId()).orElseThrow();

    // 5. Check if all steps in unit are done
    long completedSteps = pathStepRepository.countByUnitIdAndStatus(unit.getUnitId(), PathStep.StepStatus.COMPLETED);
    long totalSteps = pathStepRepository.countByUnitId(unit.getUnitId());

    if (completedSteps == totalSteps) {
        // Unit is done!
        unit.setStatus(PathUnit.UnitStatus.COMPLETED);
        unit.setCompletedAt(LocalDateTime.now());
        pathUnitRepository.save(unit);

        // Unlock next unit
        Optional<PathUnit> nextUnit = pathUnitRepository.findNextUnit(unit.getPathId(), unit.getUnitNumber());
        if (nextUnit.isPresent()) {
            PathUnit next = nextUnit.get();
            next.setStatus(PathUnit.UnitStatus.AVAILABLE);
            next.setUnlockedAt(LocalDateTime.now());
            pathUnitRepository.save(next);

            // Unlock first step of next unit
            Optional<PathStep> firstStep = pathStepRepository.findByUnitIdAndStepNumber(next.getUnitId(), 1);
            if (firstStep.isPresent()) {
                firstStep.get().setStatus(PathStep.StepStatus.AVAILABLE);
                firstStep.get().setUnlockedAt(LocalDateTime.now());
                pathStepRepository.save(firstStep.get());
            }
        }
    } else {
        // Unlock next step in current unit
        Optional<PathStep> nextStep = pathStepRepository.findNextStep(unit.getUnitId(), step.getStepNumber());
        if (nextStep.isPresent()) {
            nextStep.get().setStatus(PathStep.StepStatus.AVAILABLE);
            nextStep.get().setUnlockedAt(LocalDateTime.now());
            pathStepRepository.save(nextStep.get());
        }
    }

    // 6. Update PathProgress
    PathProgress progress = pathProgressRepository
        .findByPathIdAndUserId(unit.getPathId(), userId)
        .orElseThrow();

    progress.setCompletedSteps(progress.getCompletedSteps() + 1);
    progress.setOverallProgress(
        new BigDecimal((progress.getCompletedSteps() * 100.0) / progress.getTotalSteps())
    );
    progress.setLastActivityAt(LocalDateTime.now());
    pathProgressRepository.save(progress);

    // 7. Log activity
    pathActivityRepository.save(new PathActivity(userId, unit.getPathId(), stepId, PathActivity.ActivityType.STEP_COMPLETED));
}
```

**Test**: Complete a step, verify next step unlocks, progress updates

---

### 4. Update Frontend Routing (30 min)
**Files**: `AuthGuard.tsx`, `ConditionalLayout.tsx`, `login/page.tsx`

**Changes**:
```typescript
// AuthGuard.tsx - Redirect authenticated users to /learning-path
if (user && (pathname === "/" || pathname === "/landing")) {
  router.push("/learning-path");  // Changed from "/" or "/frontier"
}

// ConditionalLayout.tsx - Exclude /learning-path from layout
const noLayoutPages = ["/landing", "/login", "/signup", "/frontier", "/learning-path", "/onboarding"];

// login/page.tsx - Redirect to /learning-path after login
if (data.success) {
  router.push("/learning-path");  // Changed from "/"
}
```

**Test**: Login → lands on learning path, not old dashboard

---

## 🚀 Day After Tomorrow

### 5. Build Focus Mode Assessment (2-3 hours)
**File**: Create `careermap-ui/src/app/assessment/focus/[skillName]/page.tsx`

**Requirements**:
- Full-screen (no layout)
- Black background
- Large question text
- Timer in corner (optional)
- Question counter
- Submit → show state transition animation
- Redirect back to `/learning-path`

**Copy from**: Existing `/assessment/page.tsx` and modify for full-screen

---

### 6. Test End-to-End (1-2 hours)
**Flow**:
1. Logout
2. Login → redirects to `/onboarding`
3. Complete onboarding → choose role → generates path
4. Land on `/learning-path` → see current step
5. Click "Take Quiz" → full-screen assessment
6. Complete quiz with 90% → see "INFERRED → ACTIVE" animation
7. Return to path → see next step unlocked
8. Verify progress bar updated

---

## 🐛 Bug Fixes (After MVP)

### Priority 1: Quiz Not Updating States
**Where**: `QuizService.submitQuiz()` line 146

**Current**:
```java
AssessmentResultService.AssessmentImpact impact = processAssessmentImpact(...);
```

**Issue**: This creates the evidence + updates state, but may not be working

**Debug Steps**:
1. Add logging: `System.out.println("Assessment impact: " + impact);`
2. Check if `skillNode` is found (line 216 in QuizService)
3. Verify `updateStateFromEvidence()` is called (line 86 in AssessmentResultService)
4. Check `user_skill_states` table directly after quiz

**Fix**: Likely a transaction issue or skill name mapping problem

---

### Priority 2: Evidence Scoring Too Lenient
**Where**: `EvidenceExtractionService.java`

**Add strict curve** (similar to quiz curve):
```java
private double applyEvidenceStrictCurve(double llmConfidence, String evidenceType) {
    // Weight by evidence type
    double typeMultiplier = switch (evidenceType) {
        case "CERT" -> 1.0;     // Certifications are trustworthy
        case "QUIZ" -> 0.95;    // Quizzes are validated
        case "WORK_SAMPLE" -> 0.8;
        case "PROJECT" -> 0.7;  // Projects need verification
        default -> 0.5;
    };

    // Apply curve (stricter than quiz)
    if (llmConfidence >= 0.9) {
        return llmConfidence * typeMultiplier;
    } else if (llmConfidence >= 0.7) {
        return (llmConfidence * 0.8) * typeMultiplier;  // Penalize medium confidence
    } else {
        return (llmConfidence * 0.6) * typeMultiplier;  // Heavy penalty for low confidence
    }
}
```

**Test**: Submit project → verify confidence is more conservative

---

## 📋 Checklist

### Backend
- [ ] Inject repositories into PathController
- [ ] Replace all mock data with real queries
- [ ] Implement generatePath() database persistence
- [ ] Implement updatePathProgress() logic
- [ ] Test all 5 API endpoints
- [ ] Compile successfully: `./gradlew compileJava`

### Frontend
- [ ] Update AuthGuard routing
- [ ] Update ConditionalLayout exclusions
- [ ] Update login redirect
- [ ] Build focus mode assessment
- [ ] Test onboarding flow
- [ ] Test learning path UI

### End-to-End
- [ ] Generate path via onboarding
- [ ] View path with real data
- [ ] Take quiz
- [ ] Verify state changes
- [ ] Verify next step unlocks
- [ ] Verify progress updates

### Bug Fixes (Post-MVP)
- [ ] Debug quiz → state update issue
- [ ] Implement strict evidence scoring
- [ ] Fix skill name mapping issues
- [ ] Add proper error handling

---

## 🎯 Success Criteria

**MVP is complete when**:
1. User completes onboarding
2. Path is generated with OpenAI (real, not mock)
3. User sees current step with resources
4. User takes quiz
5. Quiz result updates skill state
6. Next step unlocks automatically
7. Progress bar updates
8. No crashes or errors

---

## 💪 You've Got This!

**Already Built** (60% done):
- Complete database schema ✅
- All entity classes ✅
- All repositories ✅
- Service structure ✅
- API endpoints (with mocks) ✅
- Learning Path UI ✅
- Onboarding UI ✅

**Just Need To** (40% remaining):
- Wire up database (3 hours)
- Implement progression logic (3 hours)
- Update routing (30 min)
- Build focus mode (2 hours)
- Test end-to-end (2 hours)
- Fix bugs (2-4 hours)

**Total Remaining**: ~12-14 hours = 2 solid days of work

---

**You're so close! The hard part (architecture, schema, infrastructure) is DONE. Now it's just wiring and polishing. Let's finish strong! 🚀**
