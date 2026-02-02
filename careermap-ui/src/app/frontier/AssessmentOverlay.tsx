"use client";

import { useEffect, useState } from "react";
import { API_URL } from '../../config/api';

interface Question {
  questionId: number;
  questionText: string;
  questionType?: "MCQ" | "FRQ" | "CODING";
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  options?: string[];
  correctAnswer?: string;
  userAnswer?: string;
}

interface AssessmentOverlayProps {
  skillName: string;
  userId: number;
  onClose: () => void;
  onComplete: () => void;
}

export default function AssessmentOverlay({ skillName, userId, onClose, onComplete }: AssessmentOverlayProps) {
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

      // Transform backend format (optionA, optionB, optionC, optionD) to frontend format (options array)
      const transformedQuestions = (data.questions || [])
        .map((q: any) => ({
          ...q,
          options: [q.optionA, q.optionB, q.optionC, q.optionD].filter(Boolean)
        }))
        .filter((q: any) => {
          // If it's MCQ, it MUST have 4 options
          if (!q.questionType || q.questionType === "MCQ") {
            return q.options && q.options.length === 4;
          }
          // Include FRQ and CODING questions
          if (q.questionType === "FRQ" || q.questionType === "CODING") {
            return true;
          }
          return false;
        });

      setQuestions(transformedQuestions);
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
      // Backend expects answers as a Map<String, String> where key is questionId as string
      const answersMap: Record<string, string> = {};
      questions.forEach((q) => {
        answersMap[q.questionId.toString()] = q.userAnswer || "";
      });

      const res = await fetch(`${API_URL}/api/quizzes/${quizId}/submit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          answers: answersMap,
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
    onComplete();
    onClose();
  };

  if (loading) {
    return (
      <div className="fixed inset-0 bg-black z-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-sm text-gray-500 mb-3">Generating {skillName} Quiz</div>
          <div className="w-6 h-6 border-2 border-gray-700 border-t-gray-400 rounded-full animate-spin mx-auto"></div>
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
    const allResults = results.allResults || [];

    return (
      <div className="fixed inset-0 bg-black z-50 overflow-y-auto">
        <div className="min-h-screen flex items-center justify-center px-6 py-12">
          <div className="max-w-2xl w-full">
            <button
              onClick={handleContinue}
              className="absolute top-4 right-4 text-gray-600 hover:text-gray-400 text-xl"
            >
              ×
            </button>

            <div className="text-center mb-8">
              {/* Score */}
              <div className="mb-8">
                <div className="text-5xl font-bold mb-2 text-white">
                  {Math.round(score)}%
                </div>
                <div className="text-sm text-gray-500">
                  {results.correctCount} / {results.totalQuestions} correct
                </div>
              </div>

              {/* State Transition */}
              {stateChanged && (
                <div className="mb-8 p-4 bg-gray-900 border border-gray-800 rounded">
                  <div className="text-xs text-gray-500 mb-3">STATE UPDATED</div>
                  <div className="flex items-center justify-center gap-4 text-sm">
                    <span className="text-gray-600">{oldStatus}</span>
                    <span className="text-gray-700">→</span>
                    <span className="text-white font-medium">{newStatus}</span>
                  </div>
                  <div className="text-xs text-gray-600 mt-2">{confidence}% confidence</div>
                </div>
              )}
            </div>

            {/* ALL questions with answers */}
            {allResults.length > 0 && (
              <div className="mb-8 space-y-4">
                <div className="text-sm text-gray-500 mb-4">Review all answers:</div>
                {allResults.map((result: any, idx: number) => (
                  <div key={idx} className={`p-4 rounded text-left ${result.isCorrect ? 'bg-green-900/20 border border-green-800/30' : 'bg-red-900/20 border border-red-800/30'}`}>
                    <div className="flex items-center justify-between mb-2">
                      <div className="text-xs text-gray-600">Question {result.questionNumber}</div>
                      <div className={`text-xs font-bold ${result.isCorrect ? 'text-green-500' : 'text-red-500'}`}>
                        {result.isCorrect ? '✓ CORRECT' : '✗ INCORRECT'}
                      </div>
                    </div>
                    <div className="text-sm text-white mb-3">{result.questionText}</div>
                    <div className="text-xs mb-2">
                      <div className={`mb-1 ${result.isCorrect ? 'text-green-500' : 'text-red-500'}`}>
                        Your answer: <span className="font-bold">{result.yourAnswer}</span> - {result.yourAnswerText}
                      </div>
                      {!result.isCorrect && (
                        <div className="text-green-500">
                          Correct: <span className="font-bold">{result.correctAnswer}</span> - {result.correctAnswerText}
                        </div>
                      )}
                    </div>
                    {result.explanation && result.explanation !== "No explanation provided" && (
                      <div className="text-xs text-gray-400 leading-relaxed mt-2 pt-2 border-t border-gray-800">
                        {result.explanation}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* Continue */}
            <button
              onClick={handleContinue}
              className="w-full px-6 py-3 bg-white text-black text-sm font-medium hover:bg-gray-200 transition-colors"
            >
              Continue
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="fixed inset-0 bg-black z-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-sm text-gray-500 mb-4">No questions available</div>
          <button
            onClick={onClose}
            className="px-6 py-2 bg-white text-black text-sm hover:bg-gray-200 transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    );
  }

  const currentQuestion = questions[currentQuestionIndex];
  const progress = ((currentQuestionIndex + 1) / questions.length) * 100;

  return (
    <div className="fixed inset-0 bg-black z-50 flex flex-col">
      {/* Header */}
      <div className="border-b border-gray-900 px-6 py-4">
        <div className="max-w-2xl mx-auto flex items-center justify-between">
          <div className="text-xs text-gray-600">
            {currentQuestionIndex + 1} / {questions.length}
          </div>
          <button
            onClick={onClose}
            className="text-gray-600 hover:text-gray-400 text-sm"
          >
            Exit
          </button>
        </div>
      </div>

      {/* Question */}
      <div className="flex-1 flex items-center justify-center px-6">
        <div className="max-w-2xl w-full">
          {/* Question Type Indicator */}
          <div className="text-xs text-gray-600 mb-2 font-mono">
            {currentQuestion.questionType || "MCQ"}
          </div>

          <div className="text-xl text-white mb-8 leading-relaxed">
            {currentQuestion.questionText}
          </div>

          {/* Render based on question type */}
          {(!currentQuestion.questionType || currentQuestion.questionType === "MCQ") && (
            <div className="space-y-3 mb-8">
              {currentQuestion.options?.map((option, idx) => {
                const optionLetter = String.fromCharCode(65 + idx); // A, B, C, D
                return (
                  <button
                    key={idx}
                    onClick={() => handleAnswerSelect(optionLetter)}
                    className={`w-full p-4 text-left text-sm transition-all ${
                      selectedAnswer === optionLetter
                        ? "bg-white text-black"
                        : "bg-gray-900 text-gray-300 hover:bg-gray-800 border border-gray-800"
                  }`}
                >
                  <span className="font-bold mr-3">{optionLetter}.</span>
                  {option}
                </button>
              );
              })}
            </div>
          )}

          {(currentQuestion.questionType === "FRQ" || currentQuestion.questionType === "CODING") && (
            <div className="mb-8">
              <textarea
                value={selectedAnswer}
                onChange={(e) => setSelectedAnswer(e.target.value)}
                placeholder={currentQuestion.questionType === "CODING" ? "Enter your code here..." : "Enter your answer here..."}
                className={`w-full p-4 bg-gray-900 border border-gray-800 text-white text-sm placeholder-gray-600 focus:outline-none focus:border-gray-700 ${
                  currentQuestion.questionType === "CODING" ? "font-mono h-64" : "h-32"
                }`}
              />
              <div className="text-xs text-gray-600 mt-2">
                {currentQuestion.questionType === "CODING" ? "Write clean, working code" : "Provide a clear, concise answer"}
              </div>
            </div>
          )}

          <button
            onClick={handleNext}
            disabled={!selectedAnswer || submitting}
            className="w-full px-6 py-3 bg-white text-black text-sm font-medium hover:bg-gray-200 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
          >
            {submitting
              ? "Submitting..."
              : currentQuestionIndex === questions.length - 1
              ? "Submit"
              : "Next"}
          </button>
        </div>
      </div>

      {/* Progress */}
      <div className="h-1 bg-gray-900">
        <div
          className="h-full bg-white transition-all duration-300"
          style={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
}
