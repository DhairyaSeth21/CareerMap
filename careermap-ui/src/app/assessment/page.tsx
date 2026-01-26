"use client";

import { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";
import { useCurrentUser } from "@/hooks/useCurrentUser";

interface Skill {
  skillId: number;
  name: string;
  category: string;
}

interface Quiz {
  quizId: number;
  skillName: string;
  difficulty: string;
  numQuestions: number;
  questions: QuizQuestion[];
}

interface QuizQuestion {
  questionId: number;
  questionNumber: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
}

interface QuizResult {
  score: number;
  correctCount: number;
  totalQuestions: number;
  proficiencyAwarded: number;
  timeTaken: number;
  breakdown: any[];
}

export default function AssessmentPage() {
  const [skills, setSkills] = useState<Skill[]>([]);
  const [selectedSkillId, setSelectedSkillId] = useState<number | null>(null);
  const [selectedSkillName, setSelectedSkillName] = useState<string>("");
  const [difficulty, setDifficulty] = useState<string>("Intermediate");
  const [activeQuiz, setActiveQuiz] = useState<Quiz | null>(null);
  const [quizAnswers, setQuizAnswers] = useState<Record<string, string>>({});
  const [quizStartTime, setQuizStartTime] = useState<number>(0);
  const [quizResult, setQuizResult] = useState<QuizResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const user = useCurrentUser();
  const searchParams = useSearchParams();
  const userId = user?.userId;

  useEffect(() => {
    loadSkills();
  }, []);

  useEffect(() => {
    const skillIdParam = searchParams?.get('skillId');
    if (skillIdParam && skills.length > 0) {
      const skillId = parseInt(skillIdParam);
      const skill = skills.find(s => s.skillId === skillId);
      if (skill) {
        setSelectedSkillId(skillId);
        setSelectedSkillName(skill.name);
        // Auto-generate quiz
        setTimeout(() => generateQuiz(skill.name), 100);
      }
    }
  }, [searchParams, skills]);

  const loadSkills = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/skills/catalog');
      const data = await res.json();
      setSkills(data);
    } catch (err: any) {
      setError("Failed to load skills");
    }
  };

  const generateQuiz = async (skillName?: string) => {
    if (!userId) return;
    const skill = skillName || selectedSkillName;
    if (!skill) {
      setError("Please select a skill");
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const res = await fetch('http://localhost:8080/api/quizzes/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId,
          skillName: skill,
          difficulty,
          numQuestions: 10
        })
      });

      if (!res.ok) throw new Error('Failed to generate quiz');
      const data = await res.json();
      setActiveQuiz(data);
      setQuizAnswers({});
      setQuizStartTime(Date.now());
    } catch (err: any) {
      setError(err.message || "Failed to generate quiz");
    } finally {
      setLoading(false);
    }
  };

  const submitQuiz = async () => {
    if (!activeQuiz || !userId) return;

    const unanswered = activeQuiz.questions.filter(
      q => !quizAnswers[q.questionId.toString()]
    ).length;

    if (unanswered > 0) {
      if (!confirm(`${unanswered} questions unanswered. Submit anyway?`)) return;
    }

    try {
      setLoading(true);
      const timeTaken = Math.floor((Date.now() - quizStartTime) / 1000);

      const res = await fetch(`http://localhost:8080/api/quizzes/${activeQuiz.quizId}/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ answers: quizAnswers, timeTaken })
      });

      if (!res.ok) throw new Error('Failed to submit quiz');
      const data = await res.json();
      setQuizResult(data);
      setActiveQuiz(null);
    } catch (err: any) {
      setError(err.message || "Failed to submit quiz");
    } finally {
      setLoading(false);
    }
  };

  if (loading && !activeQuiz) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="w-6 h-6 border-2 rounded-full animate-spin mx-auto mb-3" style={{ borderColor: 'var(--border)', borderTopColor: 'var(--accent)' }}></div>
          <div className="text-[13px] font-medium" style={{ color: 'var(--text-secondary)' }}>Generating quiz...</div>
        </div>
      </div>
    );
  }

  // Quiz Result
  if (quizResult) {
    return (
      <div className="space-y-6">
        <div className="p-6 rounded-xl border" style={{ background: 'var(--card-bg)', borderColor: 'var(--border)' }}>
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-[28px] font-bold mb-2" style={{ color: 'var(--text-primary)' }}>Quiz Completed</h1>
              <p className="text-[14px]" style={{ color: 'var(--text-secondary)' }}>Your proficiency has been updated</p>
            </div>
            <div className="text-[48px] font-bold" style={{ color: '#10b981' }}>
              {quizResult.score.toFixed(1)}%
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
            <div className="p-4 rounded-lg border" style={{ background: 'var(--background)', borderColor: 'var(--border)' }}>
              <div className="text-[24px] font-bold" style={{ color: 'var(--accent)' }}>{quizResult.correctCount}/{quizResult.totalQuestions}</div>
              <div className="text-[11px] mt-1" style={{ color: 'var(--text-secondary)' }}>Correct</div>
            </div>
            <div className="p-4 rounded-lg border" style={{ background: 'var(--background)', borderColor: 'var(--border)' }}>
              <div className="text-[24px] font-bold" style={{ color: 'var(--accent)' }}>{quizResult.proficiencyAwarded.toFixed(1)}/10</div>
              <div className="text-[11px] mt-1" style={{ color: 'var(--text-secondary)' }}>Proficiency</div>
            </div>
            <div className="p-4 rounded-lg border" style={{ background: 'var(--background)', borderColor: 'var(--border)' }}>
              <div className="text-[24px] font-bold" style={{ color: 'var(--accent)' }}>{Math.floor(quizResult.timeTaken / 60)}:{String(quizResult.timeTaken % 60).padStart(2, '0')}</div>
              <div className="text-[11px] mt-1" style={{ color: 'var(--text-secondary)' }}>Time</div>
            </div>
            <div className="p-4 rounded-lg border" style={{ background: 'var(--background)', borderColor: 'var(--border)' }}>
              <div className="text-[24px] font-bold" style={{ color: 'var(--accent)' }}>{quizResult.breakdown.length}</div>
              <div className="text-[11px] mt-1" style={{ color: 'var(--text-secondary)' }}>Topics</div>
            </div>
          </div>

          <div className="flex gap-3">
            <button
              onClick={() => { setQuizResult(null); setSelectedSkillId(null); setSelectedSkillName(""); }}
              className="flex-1 px-4 py-3 rounded-lg font-medium text-[13px]"
              style={{ background: 'var(--accent)', color: '#ffffff' }}
            >
              Take Another Quiz
            </button>
            <button
              onClick={() => window.location.href = '/'}
              className="flex-1 px-4 py-3 rounded-lg border font-medium text-[13px]"
              style={{ borderColor: 'var(--border)', color: 'var(--text-primary)' }}
            >
              Back to Path
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Active Quiz
  if (activeQuiz) {
    const progress = (Object.keys(quizAnswers).length / activeQuiz.numQuestions) * 100;

    return (
      <div className="space-y-6">
        <div className="p-6 rounded-xl border" style={{ background: 'var(--card-bg)', borderColor: 'var(--border)' }}>
          <div className="flex items-center justify-between mb-4">
            <div>
              <h1 className="text-[24px] font-bold" style={{ color: 'var(--text-primary)' }}>{activeQuiz.skillName}</h1>
              <p className="text-[13px]" style={{ color: 'var(--text-secondary)' }}>Difficulty: {activeQuiz.difficulty}</p>
            </div>
            <div className="text-[32px] font-bold" style={{ color: 'var(--accent)' }}>
              {Object.keys(quizAnswers).length}/{activeQuiz.numQuestions}
            </div>
          </div>

          <div className="rounded-full h-2" style={{ background: 'var(--border)' }}>
            <div className="h-2 rounded-full" style={{ background: 'var(--accent)', width: `${progress}%` }} />
          </div>
        </div>

        <div className="space-y-4">
          {activeQuiz.questions.map((q) => (
            <div key={q.questionId} className="p-5 rounded-xl border" style={{ background: 'var(--card-bg)', borderColor: 'var(--border)' }}>
              <div className="flex items-start gap-3 mb-4">
                <div className="w-6 h-6 rounded-full flex items-center justify-center text-[11px] font-bold" style={{ background: 'var(--accent)', color: '#ffffff' }}>
                  {q.questionNumber}
                </div>
                <p className="text-[14px] font-medium" style={{ color: 'var(--text-primary)' }}>{q.questionText}</p>
              </div>

              <div className="grid grid-cols-1 gap-2">
                {["A", "B", "C", "D"].map((option) => {
                  const optionText = (q as any)[`option${option}`];
                  const isSelected = quizAnswers[q.questionId.toString()] === option;

                  return (
                    <button
                      key={option}
                      onClick={() => setQuizAnswers({ ...quizAnswers, [q.questionId.toString()]: option })}
                      className="p-3 rounded-lg border text-left"
                      style={{
                        background: isSelected ? 'rgba(94, 106, 210, 0.1)' : 'var(--background)',
                        borderColor: isSelected ? 'var(--accent)' : 'var(--border)',
                        color: 'var(--text-primary)'
                      }}
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-5 h-5 rounded-full border flex items-center justify-center text-[11px] font-medium" style={{ borderColor: isSelected ? 'var(--accent)' : 'var(--border)', background: isSelected ? 'var(--accent)' : 'transparent', color: isSelected ? '#ffffff' : 'var(--text-tertiary)' }}>
                          {option}
                        </div>
                        <span className="text-[13px]">{optionText}</span>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        <div className="flex gap-3 sticky bottom-4">
          <button
            onClick={() => { if (confirm("Cancel this quiz?")) { setActiveQuiz(null); setQuizAnswers({}); } }}
            className="px-4 py-3 rounded-lg border font-medium text-[13px]"
            style={{ borderColor: 'var(--border)', color: 'var(--text-secondary)' }}
          >
            Cancel
          </button>
          <button
            onClick={submitQuiz}
            disabled={loading}
            className="flex-1 px-4 py-3 rounded-lg font-medium text-[13px] disabled:opacity-50"
            style={{ background: loading ? 'var(--text-tertiary)' : '#10b981', color: '#ffffff' }}
          >
            {loading ? "Submitting..." : "Submit Quiz"}
          </button>
        </div>
      </div>
    );
  }

  // Skill Selection
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-[32px] font-bold mb-2" style={{ color: 'var(--text-primary)' }}>Skill Assessment</h1>
        <p className="text-[14px]" style={{ color: 'var(--text-secondary)' }}>Take quizzes to prove your skills and track your progress</p>
      </div>

      <div className="p-6 rounded-xl border" style={{ background: 'var(--card-bg)', borderColor: 'var(--border)' }}>
        <div className="space-y-5">
          <div>
            <label className="block text-[12px] font-medium mb-2" style={{ color: 'var(--text-secondary)' }}>
              Select Skill
            </label>
            <select
              value={selectedSkillName}
              onChange={(e) => {
                setSelectedSkillName(e.target.value);
                const skill = skills.find(s => s.name === e.target.value);
                setSelectedSkillId(skill?.skillId || null);
              }}
              className="w-full px-4 py-3 rounded-lg border text-[13px]"
              style={{ background: 'var(--background)', borderColor: 'var(--border)', color: 'var(--text-primary)' }}
            >
              <option value="">Choose a skill</option>
              {skills.map((skill) => (
                <option key={skill.skillId} value={skill.name}>{skill.name} ({skill.category})</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-[12px] font-medium mb-2" style={{ color: 'var(--text-secondary)' }}>
              Difficulty
            </label>
            <div className="grid grid-cols-4 gap-2">
              {["Beginner", "Intermediate", "Advanced", "Expert"].map((diff) => (
                <button
                  key={diff}
                  onClick={() => setDifficulty(diff)}
                  className="px-3 py-2 rounded-lg border font-medium text-[12px]"
                  style={{
                    background: difficulty === diff ? 'rgba(94, 106, 210, 0.1)' : 'var(--background)',
                    borderColor: difficulty === diff ? 'var(--accent)' : 'var(--border)',
                    color: difficulty === diff ? 'var(--accent)' : 'var(--text-secondary)'
                  }}
                >
                  {diff}
                </button>
              ))}
            </div>
          </div>

          {error && (
            <div className="p-3 rounded-lg border" style={{ background: 'rgba(239, 68, 68, 0.1)', borderColor: '#ef4444' }}>
              <p className="text-[12px]" style={{ color: '#ef4444' }}>{error}</p>
            </div>
          )}

          <button
            onClick={() => generateQuiz()}
            disabled={!selectedSkillName || loading}
            className="w-full px-4 py-3 rounded-lg font-medium text-[14px] disabled:opacity-50"
            style={{ background: loading || !selectedSkillName ? 'var(--text-tertiary)' : 'var(--accent)', color: '#ffffff' }}
          >
            {loading ? "Generating..." : "Generate Quiz"}
          </button>
        </div>
      </div>
    </div>
  );
}
