"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { API_URL } from '../../../../config/api';

/**
 * Focus Mode Assessment - Full-screen quiz experience
 *
 * This is the immersive quiz mode that replaces the old assessment page.
 * Features:
 * - Full-screen, no distractions
 * - Black background
 * - Animated state transition feedback
 * - Auto-returns to learning path after completion
 */

interface Question {
  questionId: number;
  questionText: string;
  options: string[];
  correctAnswer?: string;
  userAnswer?: string;
}

export default function FocusAssessmentPage() {
  const router = useRouter();
  const params = useParams();
  const skillName = params.skillName as string;

  const [userId] = useState(1); // TODO: Get from auth
  const [quizId, setQuizId] = useState<number | null>(null);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState<string>("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [results, setResults] = useState<any>(null);
  const [startTime] = useState(Date.now());

  useEffect(() => {
    generateQuiz();
  }, [skillName]);

  const generateQuiz = async () => {
    try {
      const res = await fetch("${API_URL}/api/quizzes/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userId,
          skillName,
          difficulty: "Intermediate",
          numQuestions: 10,
        }),
      });

      const data = await res.json();
      setQuizId(data.quizId);
      setQuestions(data.questions || []);
      setLoading(false);
    } catch (error) {
      console.error("Failed to generate quiz:", error);
      setLoading(false);
    }
  };

  const handleAnswerSelect = (answer: string) => {
    setSelectedAnswer(answer);
  };

  const handleNext = () => {
    // Save answer
    if (selectedAnswer && questions[currentQuestionIndex]) {
      questions[currentQuestionIndex].userAnswer = selectedAnswer;
    }

    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
      setSelectedAnswer(questions[currentQuestionIndex + 1]?.userAnswer || "");
    } else {
      handleSubmit();
    }
  };

  const handleSubmit = async () => {
    if (!quizId) return;

    setSubmitting(true);
    const timeTaken = Math.floor((Date.now() - startTime) / 1000);

    try {
      const res = await fetch(`${API_URL}/api/quizzes/${quizId}/submit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          answers: questions.map((q) => ({
            questionId: q.questionId,
            userAnswer: q.userAnswer || "",
          })),
          timeTaken,
        }),
      });

      const data = await res.json();
      setResults(data);
      setShowResults(true);
    } catch (error) {
      console.error("Failed to submit quiz:", error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleContinue = () => {
    router.push("/learning-path");
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center">
        <div className="text-center">
          <div className="text-2xl text-blue-400 mb-4">Generating {skillName} Quiz...</div>
          <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
        </div>
      </div>
    );
  }

  if (showResults && results) {
    const score = results.score || 0;
    const stateTransition = results.stateTransition || {};
    const stateChanged = stateTransition.stateChanged || false;
    const oldStatus = stateTransition.oldStatus || "UNKNOWN";
    const newStatus = stateTransition.newStatus || "UNKNOWN";
    const confidence = Math.round((stateTransition.newConfidence || 0) * 100);

    return (
      <div className="min-h-screen bg-black flex items-center justify-center px-6">
        <div className="max-w-2xl w-full text-center">
          {/* Score Display */}
          <div className="mb-12">
            <div className="text-6xl font-bold mb-4">
              <span className={score >= 85 ? "text-green-400" : score >= 70 ? "text-blue-400" : "text-yellow-400"}>
                {Math.round(score)}%
              </span>
            </div>
            <div className="text-xl text-gray-400">
              {results.correctCount} / {results.totalQuestions} correct
            </div>
          </div>

          {/* State Transition Animation */}
          {stateChanged ? (
            <div className="mb-12 p-8 bg-gradient-to-br from-blue-900/30 to-purple-900/30 border border-blue-500/30 rounded-xl">
              <div className="text-3xl font-bold mb-6 text-white">Progress Updated!</div>

              <div className="flex items-center justify-center gap-6 mb-6">
                <div className="text-center">
                  <div className="text-sm text-gray-400 mb-2">Previous</div>
                  <div className="px-4 py-2 bg-gray-700 rounded-lg text-lg font-mono">{oldStatus}</div>
                </div>

                <div className="text-3xl text-blue-400">→</div>

                <div className="text-center">
                  <div className="text-sm text-gray-400 mb-2">Current</div>
                  <div className="px-4 py-2 bg-blue-600 rounded-lg text-lg font-mono font-bold">{newStatus}</div>
                </div>
              </div>

              <div className="text-gray-300">
                <span className="font-semibold">{skillName}</span> confidence: {confidence}%
              </div>
            </div>
          ) : (
            <div className="mb-12 p-6 bg-gray-900/50 border border-gray-700 rounded-xl">
              <div className="text-lg text-gray-300">
                {score >= 85
                  ? `Great score! Keep building evidence to reach PROVED status.`
                  : score >= 70
                  ? `Good work! Take more quizzes or submit projects to improve your confidence.`
                  : `Keep practicing! Try reviewing the study resources and retake the quiz.`}
              </div>
            </div>
          )}

          {/* Breakdown */}
          {results.breakdown && results.breakdown.length > 0 && (
            <div className="mb-8">
              <div className="text-sm text-gray-400 mb-4">TOPIC BREAKDOWN</div>
              <div className="space-y-2">
                {results.breakdown.map((topic: any, idx: number) => (
                  <div key={idx} className="flex items-center justify-between p-3 bg-gray-900/50 rounded-lg">
                    <span className="text-gray-300">{topic.subtopic}</span>
                    <span className={topic.percentage >= 80 ? "text-green-400" : "text-yellow-400"}>
                      {topic.correct}/{topic.total} ({topic.percentage}%)
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Action Button */}
          <button
            onClick={handleContinue}
            className="w-full px-8 py-4 bg-blue-600 hover:bg-blue-500 rounded-lg text-lg font-semibold transition-colors"
          >
            Continue Learning →
          </button>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center">
        <div className="text-center">
          <div className="text-xl text-red-400 mb-4">No questions available for {skillName}</div>
          <button
            onClick={() => router.push("/learning-path")}
            className="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition-colors"
          >
            Back to Learning Path
          </button>
        </div>
      </div>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const progress = ((currentQuestionIndex + 1) / questions.length) * 100;

  return (
    <div className="min-h-screen bg-black text-white flex flex-col">
      {/* Header: Progress Bar + Counter */}
      <div className="border-b border-gray-800 bg-black/50 backdrop-blur-xl">
        <div className="max-w-4xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between mb-3">
            <div className="text-sm text-gray-400">{skillName} Assessment</div>
            <div className="text-sm font-mono text-gray-400">
              Question {currentQuestionIndex + 1} / {questions.length}
            </div>
          </div>

          {/* Progress Bar */}
          <div className="w-full h-2 bg-gray-800 rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-blue-500 to-purple-500 transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      </div>

      {/* Question Area */}
      <div className="flex-1 flex items-center justify-center px-6 py-12">
        <div className="max-w-3xl w-full">
          {/* Question Text */}
          <div className="text-3xl font-semibold mb-12 leading-relaxed">{currentQuestion.questionText}</div>

          {/* Answer Options */}
          <div className="space-y-4 mb-12">
            {currentQuestion.options.map((option, idx) => (
              <button
                key={idx}
                onClick={() => handleAnswerSelect(option)}
                className={`w-full p-6 text-left text-lg rounded-xl border-2 transition-all ${
                  selectedAnswer === option
                    ? "border-blue-500 bg-blue-900/30"
                    : "border-gray-700 bg-gray-900/30 hover:border-gray-600 hover:bg-gray-900/50"
                }`}
              >
                <div className="flex items-center gap-4">
                  <div
                    className={`w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 ${
                      selectedAnswer === option ? "border-blue-500 bg-blue-500" : "border-gray-600"
                    }`}
                  >
                    {selectedAnswer === option && <div className="w-3 h-3 bg-white rounded-full"></div>}
                  </div>
                  <span>{option}</span>
                </div>
              </button>
            ))}
          </div>

          {/* Navigation */}
          <button
            onClick={handleNext}
            disabled={!selectedAnswer || submitting}
            className="w-full px-8 py-4 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed rounded-lg text-lg font-semibold transition-colors"
          >
            {submitting
              ? "Submitting..."
              : currentQuestionIndex === questions.length - 1
              ? "Submit Quiz"
              : "Next Question →"}
          </button>
        </div>
      </div>
    </div>
  );
}
