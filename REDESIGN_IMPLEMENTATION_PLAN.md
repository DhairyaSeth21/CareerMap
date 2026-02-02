ok# CareerMap Redesign: Implementation Plan

## Phase 1: Core Flow & State Correctness (Week 1)

### 1.1 Fix Assessment → State Update Pipeline
**Priority: CRITICAL**

**Problem:** Quiz completion doesn't reliably update skill states (Cryptography quiz bug)

**Tasks:**
- [ ] Create `AssessmentResultService.java`
- [ ] Implement strict scoring curve (make PROVED harder to achieve)
- [ ] Auto-create evidence record from quiz results
- [ ] Auto-link evidence to tested skill with support score
- [ ] Trigger state machine update
- [ ] Add transaction boundaries (all-or-nothing)
- [ ] Test: Take quiz → verify state change → verify evidence created

**Acceptance Criteria:**
- 80% quiz score → ACTIVE skill reaches 70%+ confidence
- 90% quiz score → ACTIVE skill → PROVED (if prereqs met)
- Evidence record auto-created in DB
- Frontend sees immediate state update on page refresh

---

### 1.2 Build Path Generation Service
**Priority: HIGH**

**New Backend Service:**

```java
@Service
public class PathGenerationService {

    public LearningPath generatePath(int userId, int roleId, String timeframe) {
        // 1. Get user's current skill states
        List<UserSkillState> current = stateRepo.findByUserId(userId);
        List<Integer> provedSkills = current.stream()
            .filter(s -> s.getStatus() == PROVED)
            .map(UserSkillState::getSkillId)
            .collect(toList());

        // 2. Get role requirements
        List<RoleSkillWeight> requirements = weightRepo.findByRoleId(roleId);

        // 3. Get full prereq graph
        List<PrereqEdge> edges = edgeRepo.findAll();

        // 4. Call OpenAI to generate path
        String prompt = buildPathPrompt(roleId, provedSkills, requirements, edges, timeframe);
        String response = gptService.callChatGPT(prompt, 0.3); // low temp for consistency

        // 5. Parse JSON response
        PathStructure path = parsePathJSON(response);

        // 6. Validate path (prereq order, skill existence)
        validatePath(path);

        // 7. Save to database
        LearningPath saved = savePath(userId, roleId, path);

        // 8. Initialize progress tracker
        PathProgress progress = new PathProgress();
        progress.setPathId(saved.getId());
        progress.setCurrentWeek(1);
        progress.setCurrentStepIndex(0);
        progressRepo.save(progress);

        return saved;
    }

    private String buildPathPrompt(int roleId, List<Integer> proved,
                                    List<RoleSkillWeight> req,
                                    List<PrereqEdge> edges,
                                    String timeframe) {
        return """
            You are a curriculum designer for technical roles.

            Role: %s
            Timeframe: %s
            Skills Already Proved: %s
            Required Skills: %s
            Prerequisite Relationships: %s

            Generate a structured learning path as JSON:
            {
              "totalWeeks": 12,
              "units": [
                {
                  "week": 1,
                  "title": "Unit Title",
                  "goal": "What student will achieve",
                  "steps": [
                    {
                      "skillId": 20,
                      "skillName": "Linux",
                      "action": "ASSESS",
                      "requiredConfidence": 0.85,
                      "rationale": "Why this step matters"
                    }
                  ]
                }
              ]
            }

            Rules:
            - Start with skills user hasn't proved
            - Respect prerequisite order (use provided graph)
            - Each unit = 1 week, 2-4 skills
            - Sequence from foundations → advanced
            - Total path should cover 20-30 most important skills
            - requiredConfidence: 0.7-0.9 depending on importance
            - action: ASSESS (quiz), PROJECT (build), or STUDY (learn)

            Return ONLY valid JSON, no explanation.
            """.formatted(getRoleName(roleId), timeframe, proved, req, edges);
    }
}
```

**New Database Tables:**

```sql
-- V5_learning_paths.sql

CREATE TABLE learning_paths (
    path_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id),
    role_id INT NOT NULL REFERENCES roles(role_id),
    timeframe VARCHAR(20) NOT NULL, -- '12_WEEKS'
    path_data JSONB NOT NULL, -- OpenAI response
    generated_at TIMESTAMP DEFAULT NOW(),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE path_progress (
    progress_id SERIAL PRIMARY KEY,
    path_id INT NOT NULL REFERENCES learning_paths(path_id),
    user_id INT NOT NULL REFERENCES users(user_id),
    current_week INT NOT NULL DEFAULT 1,
    current_step_index INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_path_user ON learning_paths(user_id, is_active);
CREATE INDEX idx_progress_path ON path_progress(path_id);
```

**New API Endpoints:**

```java
@RestController
@RequestMapping("/api/v3/path")
public class PathController {

    @PostMapping("/generate")
    public ResponseEntity<PathResponse> generatePath(@RequestBody PathRequest req) {
        LearningPath path = pathService.generatePath(
            req.getUserId(),
            req.getRoleId(),
            req.getTimeframe()
        );
        return ResponseEntity.ok(new PathResponse(path));
    }

    @GetMapping("/current")
    public ResponseEntity<PathViewResponse> getCurrentPath(@RequestParam int userId) {
        LearningPath path = pathService.getActivePath(userId);
        PathProgress progress = pathService.getProgress(userId);

        // Build response with current focus, completed, locked
        PathViewResponse response = pathService.buildPathView(path, progress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/advance")
    public ResponseEntity<Void> advanceStep(@RequestParam int userId,
                                             @RequestParam int skillId) {
        // Called after skill reaches PROVED status
        pathService.advanceIfReady(userId, skillId);
        return ResponseEntity.ok().build();
    }
}
```

**Tasks:**
- [ ] Create PathGenerationService
- [ ] Create PathController with 3 endpoints
- [ ] Create DB migration V5
- [ ] Implement OpenAI prompt building
- [ ] Implement JSON parsing + validation
- [ ] Test: Generate path → verify valid JSON → verify DB save
- [ ] Test: Prove skill → verify path advances

---

### 1.3 Create Onboarding Flow (Frontend)
**Priority: HIGH**

**New Components:**

```typescript
// careermap-ui/src/app/onboarding/page.tsx

'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

const SCREENS = ['welcome', 'how-it-works', 'role-selection', 'generating'] as const;

export default function OnboardingFlow() {
  const router = useRouter();
  const [screen, setScreen] = useState<typeof SCREENS[number]>('welcome');
  const [selectedRole, setSelectedRole] = useState<number | null>(null);

  const handleRoleSelect = async (roleId: number) => {
    setSelectedRole(roleId);
    setScreen('generating');

    // Call path generation API
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const response = await fetch('http://localhost:8080/api/v3/path/generate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: user.userId,
        roleId,
        timeframe: '12_WEEKS'
      })
    });

    if (response.ok) {
      // Mark onboarding complete
      localStorage.setItem('onboardingComplete', 'true');
      router.push('/path'); // new main screen
    }
  };

  return (
    <div className="h-screen w-screen bg-[#0a1628] flex items-center justify-center">
      {screen === 'welcome' && (
        <WelcomeScreen onNext={() => setScreen('how-it-works')} />
      )}
      {screen === 'how-it-works' && (
        <HowItWorksScreen onNext={() => setScreen('role-selection')} />
      )}
      {screen === 'role-selection' && (
        <RoleSelectionScreen onSelect={handleRoleSelect} />
      )}
      {screen === 'generating' && (
        <GeneratingScreen />
      )}
    </div>
  );
}

function WelcomeScreen({ onNext }: { onNext: () => void }) {
  return (
    <div className="max-w-2xl text-center">
      <h1 className="text-5xl font-bold text-white mb-6">
        Welcome to CareerMap
      </h1>
      <p className="text-xl text-gray-300 mb-8">
        A mentor-driven learning system that builds a custom curriculum
        for your target role and tracks your mastery through evidence.
      </p>
      <button
        onClick={onNext}
        className="px-8 py-4 bg-[#00d4ff] text-[#0a1628] rounded-lg text-lg font-bold hover:bg-[#00ffff] transition-all transform hover:scale-105"
      >
        Get Started →
      </button>
    </div>
  );
}

function HowItWorksScreen({ onNext }: { onNext: () => void }) {
  return (
    <div className="max-w-4xl">
      <h2 className="text-4xl font-bold text-white mb-8">How It Works</h2>

      <div className="grid grid-cols-3 gap-6 mb-12">
        <div className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-6">
          <div className="text-4xl mb-4">1️⃣</div>
          <h3 className="text-xl font-bold text-white mb-2">Choose Your Role</h3>
          <p className="text-gray-400">
            We generate a 12-week curriculum tailored to your target position
          </p>
        </div>

        <div className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-6">
          <div className="text-4xl mb-4">2️⃣</div>
          <h3 className="text-xl font-bold text-white mb-2">Prove Your Skills</h3>
          <p className="text-gray-400">
            Take assessments and submit evidence. We track mastery, not self-ratings.
          </p>
        </div>

        <div className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-6">
          <div className="text-4xl mb-4">3️⃣</div>
          <h3 className="text-xl font-bold text-white mb-2">Progress Through Path</h3>
          <p className="text-gray-400">
            Each skill unlocks the next. Trust the sequence—we explain every step.
          </p>
        </div>
      </div>

      {/* State Machine Animation */}
      <div className="bg-black/40 rounded-xl p-8 mb-8">
        <h3 className="text-2xl font-bold text-white mb-6">The Evidence-Driven State Machine</h3>
        <div className="flex items-center justify-center gap-4">
          <div className="state-node unseen">UNSEEN</div>
          <div className="arrow">→</div>
          <div className="state-node inferred">INFERRED</div>
          <div className="arrow">→</div>
          <div className="state-node active">ACTIVE</div>
          <div className="arrow">→</div>
          <div className="state-node proved">PROVED</div>
        </div>
        <p className="text-center text-gray-400 mt-4">
          Submit evidence → We evaluate → Your path updates
        </p>
      </div>

      <button
        onClick={onNext}
        className="w-full px-8 py-4 bg-[#00d4ff] text-[#0a1628] rounded-lg text-lg font-bold hover:bg-[#00ffff] transition-all"
      >
        Continue →
      </button>
    </div>
  );
}

function RoleSelectionScreen({ onSelect }: { onSelect: (roleId: number) => void }) {
  const roles = [
    { id: 1, name: 'Security Engineer', icon: '🔐' },
    { id: 2, name: 'Frontend Developer', icon: '🎨' },
    { id: 3, name: 'Backend Engineer', icon: '⚙️' },
    { id: 4, name: 'Data Scientist', icon: '📊' },
  ];

  return (
    <div className="max-w-4xl">
      <h2 className="text-4xl font-bold text-white mb-4">Choose Your Path</h2>
      <p className="text-xl text-gray-400 mb-12">
        This determines your entire curriculum. You can change this later.
      </p>

      <div className="grid grid-cols-2 gap-6">
        {roles.map(role => (
          <button
            key={role.id}
            onClick={() => onSelect(role.id)}
            className="bg-white/5 backdrop-blur border-2 border-white/10 hover:border-[#00d4ff] rounded-xl p-8 text-left transition-all transform hover:scale-105 group"
          >
            <div className="text-6xl mb-4">{role.icon}</div>
            <h3 className="text-2xl font-bold text-white group-hover:text-[#00d4ff]">
              {role.name}
            </h3>
          </button>
        ))}
      </div>
    </div>
  );
}

function GeneratingScreen() {
  return (
    <div className="max-w-2xl text-center">
      <div className="mb-8">
        <div className="w-16 h-16 border-4 border-[#00d4ff] border-t-transparent rounded-full animate-spin mx-auto"></div>
      </div>
      <h2 className="text-3xl font-bold text-white mb-4">
        Generating Your Learning Path...
      </h2>
      <p className="text-gray-400">
        Our AI is building a 12-week curriculum tailored to your role.
        This takes 5-10 seconds.
      </p>
    </div>
  );
}
```

**Tasks:**
- [ ] Create onboarding page component
- [ ] Create 4 screen components (welcome, how-it-works, role-select, generating)
- [ ] Add CSS animations for state machine visualization
- [ ] Add loading animation for path generation
- [ ] Update AuthGuard to redirect first-time users to /onboarding
- [ ] Test full flow: signup → onboarding → role selection → path generation → main app

---

## Phase 2: Path-Driven UI (Week 2)

### 2.1 Build New Path View (Main Screen)

**Replace Frontier as Landing Page:**

```typescript
// careermap-ui/src/app/path/page.tsx

'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';

interface PathViewData {
  currentWeek: number;
  totalWeeks: number;
  progress: number;
  currentFocus: {
    skillId: number;
    skillName: string;
    status: string;
    confidence: number;
    targetConfidence: number;
    nextAction: {
      type: string;
      label: string;
      requiredScore?: number;
    };
    rationale: string;
  };
  recentlyCompleted: Array<{
    skillId: number;
    skillName: string;
    completedAt: string;
  }>;
  upcomingLocked: Array<{
    skillId: number;
    skillName: string;
    blockedBy: number[];
    estimatedWeek: number;
  }>;
}

export default function PathView() {
  const router = useRouter();
  const [pathData, setPathData] = useState<PathViewData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPathData();
  }, []);

  const fetchPathData = async () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const response = await fetch(
      `http://localhost:8080/api/v3/path/current?userId=${user.userId}`
    );
    const data = await response.json();
    setPathData(data);
    setLoading(false);
  };

  if (loading) {
    return <LoadingScreen />;
  }

  if (!pathData) {
    return <ErrorScreen />;
  }

  return (
    <div className="min-h-screen bg-[#0a1628] p-8">
      {/* Progress Bar */}
      <div className="max-w-6xl mx-auto mb-12">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-3xl font-bold text-white">
            Week {pathData.currentWeek} of {pathData.totalWeeks}
          </h1>
          <span className="text-[#00d4ff] text-2xl font-bold">
            {Math.round(pathData.progress * 100)}% Complete
          </span>
        </div>
        <div className="h-3 bg-white/10 rounded-full overflow-hidden">
          <div
            className="h-full bg-gradient-to-r from-[#00d4ff] to-[#00ff88] transition-all duration-500"
            style={{ width: `${pathData.progress * 100}%` }}
          />
        </div>
      </div>

      {/* Current Focus Card */}
      <div className="max-w-6xl mx-auto mb-8">
        <h2 className="text-xl text-gray-400 mb-4">CURRENT FOCUS</h2>
        <div className="bg-white/5 backdrop-blur-xl border-2 border-[#00d4ff] rounded-2xl p-8">
          <div className="flex items-start justify-between mb-6">
            <div>
              <h3 className="text-4xl font-bold text-white mb-2">
                {pathData.currentFocus.skillName}
              </h3>
              <p className="text-gray-400">
                Status: <span className="text-[#00d4ff] font-semibold">
                  {pathData.currentFocus.status}
                </span>
              </p>
            </div>
            <div className="text-right">
              <div className="text-5xl font-bold text-[#00d4ff]">
                {Math.round(pathData.currentFocus.confidence * 100)}%
              </div>
              <div className="text-sm text-gray-400">
                Target: {Math.round(pathData.currentFocus.targetConfidence * 100)}%
              </div>
            </div>
          </div>

          {/* Confidence Meter */}
          <div className="mb-6">
            <div className="h-4 bg-black/40 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-[#ffaa00] via-[#00d4ff] to-[#00ff88] transition-all duration-500"
                style={{ width: `${pathData.currentFocus.confidence * 100}%` }}
              />
            </div>
            <div className="flex justify-between mt-2 text-xs text-gray-500">
              <span>0%</span>
              <span>Target ({Math.round(pathData.currentFocus.targetConfidence * 100)}%)</span>
              <span>100%</span>
            </div>
          </div>

          {/* Rationale */}
          <div className="bg-black/40 rounded-xl p-4 mb-6">
            <p className="text-sm text-gray-300 leading-relaxed">
              <span className="text-[#00d4ff] font-semibold">Why this matters:</span>
              {' '}
              {pathData.currentFocus.rationale}
            </p>
          </div>

          {/* Next Action Button */}
          <button
            onClick={() => handleAction(pathData.currentFocus.nextAction.type, pathData.currentFocus.skillId)}
            className="w-full px-8 py-4 bg-[#00d4ff] text-[#0a1628] rounded-xl text-xl font-bold hover:bg-[#00ffff] transition-all transform hover:scale-105 shadow-lg shadow-[#00d4ff]/20"
          >
            {pathData.currentFocus.nextAction.label}
            {pathData.currentFocus.nextAction.requiredScore && (
              <span className="text-sm font-normal ml-2">
                (Need {Math.round(pathData.currentFocus.nextAction.requiredScore * 100)}%+)
              </span>
            )}
          </button>
        </div>
      </div>

      {/* Two-Column Layout */}
      <div className="max-w-6xl mx-auto grid grid-cols-2 gap-8">
        {/* Recently Completed */}
        <div>
          <h2 className="text-xl text-gray-400 mb-4">RECENTLY COMPLETED</h2>
          <div className="space-y-3">
            {pathData.recentlyCompleted.map(skill => (
              <div
                key={skill.skillId}
                className="bg-white/5 backdrop-blur border border-green-500/30 rounded-xl p-4 flex items-center gap-3"
              >
                <div className="text-3xl">✓</div>
                <div>
                  <div className="text-white font-semibold">{skill.skillName}</div>
                  <div className="text-xs text-gray-500">
                    {new Date(skill.completedAt).toLocaleDateString()}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Coming Next (Locked) */}
        <div>
          <h2 className="text-xl text-gray-400 mb-4">COMING NEXT</h2>
          <div className="space-y-3">
            {pathData.upcomingLocked.map(skill => (
              <div
                key={skill.skillId}
                className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-4 flex items-center gap-3 opacity-50"
              >
                <div className="text-3xl">🔒</div>
                <div>
                  <div className="text-white font-semibold">{skill.skillName}</div>
                  <div className="text-xs text-gray-500">
                    Week {skill.estimatedWeek} • Blocked by {skill.blockedBy.length} skill(s)
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Actions */}
      <div className="max-w-6xl mx-auto mt-12 flex gap-4">
        <button
          onClick={() => router.push('/frontier')}
          className="flex-1 px-6 py-3 bg-white/5 border border-white/20 rounded-xl text-white hover:bg-white/10 transition-all"
        >
          View Full Graph
        </button>
        <button
          onClick={() => setShowEvidenceModal(true)}
          className="flex-1 px-6 py-3 bg-white/5 border border-white/20 rounded-xl text-white hover:bg-white/10 transition-all"
        >
          Submit Evidence
        </button>
      </div>
    </div>
  );

  function handleAction(type: string, skillId: number) {
    switch (type) {
      case 'ASSESS':
        router.push(`/assessment?skillId=${skillId}&mode=focus`);
        break;
      case 'PROJECT':
        setShowEvidenceModal(true);
        break;
      case 'STUDY':
        router.push(`/resources?skillId=${skillId}`);
        break;
    }
  }
}
```

**Tasks:**
- [ ] Create new /path page (replaces /frontier as main screen)
- [ ] Implement PathView component with all sections
- [ ] Style with mission control theme
- [ ] Add progress animations
- [ ] Add confidence meter visualization
- [ ] Update AuthGuard to redirect to /path after onboarding
- [ ] Test: Navigate through path → complete skill → see progress update

---

### 2.2 Build Focus Mode Assessment

**Full-screen, distraction-free quiz experience:**

```typescript
// careermap-ui/src/app/assessment/focus/page.tsx

'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';

interface Question {
  id: number;
  text: string;
  options: string[];
  correctIndex: number;
}

export default function FocusAssessment() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const skillId = searchParams.get('skillId');

  const [questions, setQuestions] = useState<Question[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState<number[]>([]);
  const [showResults, setShowResults] = useState(false);
  const [score, setScore] = useState(0);
  const [impact, setImpact] = useState<any>(null);

  useEffect(() => {
    fetchQuestions();
  }, [skillId]);

  const fetchQuestions = async () => {
    const response = await fetch(
      `http://localhost:8080/api/quiz/generate?skillId=${skillId}&count=10`
    );
    const data = await response.json();
    setQuestions(data.questions);
  };

  const handleAnswer = (optionIndex: number) => {
    const newAnswers = [...answers];
    newAnswers[currentIndex] = optionIndex;
    setAnswers(newAnswers);

    if (currentIndex < questions.length - 1) {
      setCurrentIndex(currentIndex + 1);
    } else {
      submitAssessment(newAnswers);
    }
  };

  const submitAssessment = async (finalAnswers: number[]) => {
    const correctCount = finalAnswers.reduce((count, answer, idx) => {
      return count + (answer === questions[idx].correctIndex ? 1 : 0);
    }, 0);

    setScore(correctCount);

    // Submit to backend for state update
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const response = await fetch('http://localhost:8080/api/v3/assessment/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: user.userId,
        skillId: parseInt(skillId || '0'),
        score: correctCount,
        totalQuestions: questions.length,
        answers: finalAnswers
      })
    });

    const impactData = await response.json();
    setImpact(impactData);
    setShowResults(true);
  };

  if (questions.length === 0) {
    return <LoadingScreen />;
  }

  if (showResults) {
    return (
      <ResultsScreen
        score={score}
        total={questions.length}
        impact={impact}
        onContinue={() => router.push('/path')}
      />
    );
  }

  const currentQuestion = questions[currentIndex];
  const progress = ((currentIndex + 1) / questions.length) * 100;

  return (
    <div className="h-screen w-screen bg-[#0a1628] flex items-center justify-center p-8">
      <div className="max-w-3xl w-full">
        {/* Progress */}
        <div className="mb-8">
          <div className="flex justify-between text-sm text-gray-400 mb-2">
            <span>Question {currentIndex + 1} of {questions.length}</span>
            <span>{Math.round(progress)}% Complete</span>
          </div>
          <div className="h-2 bg-white/10 rounded-full overflow-hidden">
            <div
              className="h-full bg-[#00d4ff] transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>

        {/* Question Card */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8">
          <h2 className="text-2xl text-white font-semibold mb-8">
            {currentQuestion.text}
          </h2>

          <div className="space-y-4">
            {currentQuestion.options.map((option, idx) => (
              <button
                key={idx}
                onClick={() => handleAnswer(idx)}
                className="w-full text-left px-6 py-4 bg-white/5 border-2 border-white/10 hover:border-[#00d4ff] rounded-xl text-white transition-all transform hover:scale-102"
              >
                <span className="font-semibold text-[#00d4ff] mr-3">
                  {String.fromCharCode(65 + idx)}.
                </span>
                {option}
              </button>
            ))}
          </div>
        </div>

        {/* ESC to exit hint */}
        <div className="mt-6 text-center text-sm text-gray-500">
          Press ESC to exit (progress will be lost)
        </div>
      </div>
    </div>
  );
}

function ResultsScreen({ score, total, impact, onContinue }: any) {
  const percentage = (score / total) * 100;
  const passed = percentage >= 70;

  return (
    <div className="h-screen w-screen bg-[#0a1628] flex items-center justify-center p-8">
      <div className="max-w-2xl w-full">
        {/* Score */}
        <div className="text-center mb-12">
          <div className="text-8xl font-bold mb-4">
            <span className={passed ? 'text-[#00ff88]' : 'text-[#ffaa00]'}>
              {Math.round(percentage)}%
            </span>
          </div>
          <div className="text-2xl text-white">
            {score} / {total} Correct
          </div>
        </div>

        {/* Impact */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8 mb-8">
          <h3 className="text-xl font-bold text-white mb-4">IMPACT</h3>

          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <span className="text-gray-400">{impact.skillName}</span>
              <span className="text-white font-semibold">
                {Math.round(impact.oldConfidence * 100)}% → {Math.round(impact.newConfidence * 100)}%
              </span>
            </div>

            {impact.statusChange && (
              <div className="bg-[#00ff88]/10 border border-[#00ff88]/30 rounded-xl p-4">
                <div className="text-[#00ff88] font-semibold">
                  Status: {impact.oldStatus} → {impact.newStatus} ✓
                </div>
              </div>
            )}

            {impact.nextStepUnlocked && (
              <div className="bg-[#00d4ff]/10 border border-[#00d4ff]/30 rounded-xl p-4">
                <div className="text-[#00d4ff] font-semibold">
                  🔓 Next Step Unlocked: {impact.nextStepName}
                </div>
              </div>
            )}
          </div>
        </div>

        <button
          onClick={onContinue}
          className="w-full px-8 py-4 bg-[#00d4ff] text-[#0a1628] rounded-xl text-xl font-bold hover:bg-[#00ffff] transition-all transform hover:scale-105"
        >
          Continue to Path
        </button>
      </div>
    </div>
  );
}
```

**Tasks:**
- [ ] Create focus mode assessment page
- [ ] Implement question flow UI
- [ ] Implement results screen with impact visualization
- [ ] Add keyboard navigation (ESC to exit, 1-4 for answers)
- [ ] Style with mission control theme
- [ ] Test: Complete quiz → see state update → see next step unlock

---

## Phase 3: AI-Powered Enhancements (Week 3)

### 3.1 Study Resources Generation

**New Service:**

```java
@Service
public class ResourceCurationService {

    public List<Resource> curateResourcesForStep(int skillId, String stepContext) {
        String prompt = """
            You are a technical education curator.

            Skill: %s
            Context: %s

            Recommend 2-4 high-quality learning resources:
            {
              "resources": [
                {
                  "title": "Resource name",
                  "url": "https://...",
                  "type": "VIDEO" | "ARTICLE" | "COURSE" | "DOCS",
                  "duration": "30 min",
                  "difficulty": "BEGINNER" | "INTERMEDIATE" | "ADVANCED",
                  "rationale": "Why this resource is perfect for this step"
                }
              ]
            }

            Criteria:
            - Free or widely accessible
            - Recently updated (prefer 2023-2024)
            - Highly rated
            - Tightly scoped to the skill
            - Mix of formats (video + article)

            Return ONLY JSON.
            """.formatted(getSkillName(skillId), stepContext);

        String response = gptService.callChatGPT(prompt, 0.3);
        return parseResourceJSON(response);
    }
}
```

**Frontend:**

```typescript
// careermap-ui/src/app/resources/page.tsx

export default function ResourcesPage() {
  const [resources, setResources] = useState<Resource[]>([]);

  useEffect(() => {
    fetchResources();
  }, []);

  return (
    <div className="min-h-screen bg-[#0a1628] p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-4xl font-bold text-white mb-4">
          Study Resources for {skillName}
        </h1>
        <p className="text-gray-400 mb-12">
          Curated by AI, tailored to your current step
        </p>

        <div className="space-y-6">
          {resources.map(resource => (
            <div className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-6">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h3 className="text-xl font-bold text-white mb-1">
                    {resource.title}
                  </h3>
                  <div className="flex gap-3 text-sm text-gray-400">
                    <span>{resource.type}</span>
                    <span>•</span>
                    <span>{resource.duration}</span>
                    <span>•</span>
                    <span>{resource.difficulty}</span>
                  </div>
                </div>
                <a
                  href={resource.url}
                  target="_blank"
                  className="px-4 py-2 bg-[#00d4ff] text-[#0a1628] rounded-lg font-semibold hover:bg-[#00ffff]"
                >
                  Open →
                </a>
              </div>

              <p className="text-gray-300 text-sm leading-relaxed">
                <span className="text-[#00d4ff] font-semibold">Why this:</span>
                {' '}
                {resource.rationale}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
```

---

## Phase 4: Polish & Deployment (Week 4)

### 4.1 Fix All Bugs
- [ ] Assessment → state update pipeline (CRITICAL)
- [ ] Role selection → path update
- [ ] Evidence extraction → skill normalization edge cases
- [ ] OpenAI error handling (show errors to user)

### 4.2 Add Analytics
- [ ] Track: Time per skill, attempts per quiz, evidence quality
- [ ] Show: Progress chart, skill growth timeline
- [ ] Export: PDF report of proved skills

### 4.3 Performance
- [ ] Add Redis caching for path data
- [ ] Optimize OpenAI calls (batch where possible)
- [ ] Add loading skeletons everywhere
- [ ] Lazy load graph visualization

### 4.4 Testing
- [ ] E2E tests: Onboarding → path → assessment → evidence
- [ ] Unit tests: State machine transitions
- [ ] Integration tests: OpenAI mocking

---

## ACCEPTANCE CRITERIA

### Must Have (MVP)
✅ First-time user sees onboarding
✅ User selects role → path generates in <10 seconds
✅ Path view shows current focus + progress
✅ Taking quiz updates skill state reliably
✅ Skill state change unlocks next step
✅ Evidence submission works end-to-end
✅ UI feels "alive" not boring

### Should Have
✅ Study resources auto-curated
✅ Path adapts when skills proved out of order
✅ Error messages visible (not silent failures)
✅ Mobile responsive

### Nice to Have
⭕ Skill decay implementation
⭕ Multi-role tracking
⭕ Social features (share progress)
⭕ Export to PDF/LinkedIn

---

**Start with Phase 1, Task 1.1: Fix assessment pipeline.**

This is the foundation. Everything else depends on reliable state updates.
