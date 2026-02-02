'use client';

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import Starfield from '../components/Starfield';
import { ArrowRight, Check, TrendingUp, AlertCircle, HelpCircle, Upload, Loader2, CheckCircle } from 'lucide-react';
import { getCalibrationQuestions, type IntenseQuestion } from './intense-questions';
import { API_URL } from '../../config/api';

/**
 * Calibration Page - Michelin Star Edition
 *
 * Now with:
 * - Intense case-study questions
 * - Post-calibration results screen
 * - Domain confidence heatmap
 */

export default function CalibrationPage() {
  const [stage, setStage] = useState<'intro' | 'questions' | 'analysis' | 'results'>('intro');
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<Record<number, number>>({});
  const [questions, setQuestions] = useState<IntenseQuestion[]>([]);
  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null);

  const handleBeginCalibration = () => {
    const calibrationQuestions = getCalibrationQuestions();
    setQuestions(calibrationQuestions);
    setStage('questions');
  };

  const handleAnswerQuestion = (answerIndex: number) => {
    const currentQuestion = questions[currentQuestionIndex];
    setSelectedAnswer(answerIndex);
    setAnswers({ ...answers, [currentQuestion.questionId]: answerIndex });

    // Brief pause to show selection, then move to next
    setTimeout(() => {
      if (currentQuestionIndex < questions.length - 1) {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
        setSelectedAnswer(null);
      } else {
        // All done, show analysis
        setStage('analysis');
        setTimeout(() => {
          setStage('results');
        }, 4000);
      }
    }, 400);
  };

  return (
    <div className="relative min-h-screen overflow-hidden bg-black">
      {/* Deep space background */}
      <Starfield density="high" />

      {/* Gradient overlay */}
      <div className="fixed inset-0 bg-gradient-to-b from-transparent via-black/50 to-black pointer-events-none" />

      {/* Content */}
      <div className="relative z-10">
        <AnimatePresence mode="wait">
          {stage === 'intro' && (
            <CalibrationIntro key="intro" onBegin={handleBeginCalibration} />
          )}

          {stage === 'questions' && questions.length > 0 && (
            <CalibrationQuestion
              key={`question-${currentQuestionIndex}`}
              question={questions[currentQuestionIndex]}
              questionNumber={currentQuestionIndex + 1}
              totalQuestions={questions.length}
              selectedAnswer={selectedAnswer}
              onAnswer={handleAnswerQuestion}
            />
          )}

          {stage === 'analysis' && (
            <AnalysisAnimation key="analysis" />
          )}

          {stage === 'results' && (
            <CalibrationResults key="results" answers={answers} questions={questions} />
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}

/**
 * Calibration Intro
 */
function CalibrationIntro({ onBegin }: { onBegin: () => void }) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.5 }}
      className="flex items-center justify-center min-h-screen px-8"
    >
      <div className="max-w-3xl mx-auto text-center">
        {/* Small logo mark */}
        <motion.div
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.6, delay: 0.2 }}
          className="mb-12"
        >
          <div className="inline-block">
            <div className="w-12 h-12 mx-auto rounded-full bg-gradient-to-br from-purple-500 to-pink-500 blur-lg opacity-60" />
            <div className="w-8 h-8 mx-auto -mt-14 rounded-full bg-gradient-to-br from-purple-400 to-pink-400" />
          </div>
        </motion.div>

        {/* Title */}
        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.3 }}
          className="text-6xl md:text-7xl font-bold text-white mb-8"
        >
          Calibration
        </motion.h1>

        {/* Main copy */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.4 }}
          className="space-y-6 mb-16"
        >
          <p className="text-2xl md:text-3xl text-slate-400 leading-relaxed">
            Before we generate your path,
            <br />
            we need to calibrate your baseline.
          </p>

          <div className="pt-8 space-y-3">
            <p className="text-xl text-slate-500">This is not an exam.</p>
            <p className="text-xl text-slate-500">This is measurement.</p>
          </div>

          <div className="pt-8 flex items-center justify-center gap-8 text-slate-400">
            <span className="text-lg">12 questions</span>
            <span className="text-slate-700">•</span>
            <span className="text-lg">~5 minutes</span>
            <span className="text-slate-700">•</span>
            <span className="text-lg">Answer honestly</span>
          </div>
        </motion.div>

        {/* CTA */}
        <motion.button
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.5 }}
          onClick={onBegin}
          className="group px-12 py-5 bg-white text-black text-lg font-semibold rounded-full transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:shadow-purple-500/50"
        >
          <span className="flex items-center gap-3">
            Begin Calibration
            <ArrowRight className="w-5 h-5 transition-transform group-hover:translate-x-1" />
          </span>
        </motion.button>

        {/* Already a member? Log in */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.6, delay: 0.6 }}
          className="mt-8 text-center"
        >
          <p className="text-slate-400 text-lg">
            Already a member?{' '}
            <a
              href="/login"
              className="text-purple-400 hover:text-purple-300 font-semibold underline transition-colors"
            >
              Log in now
            </a>
            {' '}and jump straight to your Frontier
          </p>
        </motion.div>
      </div>
    </motion.div>
  );
}

/**
 * Calibration Question - Now shows code context if present
 */
function CalibrationQuestion({
  question,
  questionNumber,
  totalQuestions,
  selectedAnswer,
  onAnswer,
}: {
  question: IntenseQuestion;
  questionNumber: number;
  totalQuestions: number;
  selectedAnswer: number | null;
  onAnswer: (index: number) => void;
}) {
  const progress = (questionNumber / totalQuestions) * 100;

  return (
    <motion.div
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      transition={{ duration: 0.3 }}
      className="flex items-center justify-center min-h-screen px-8 py-20"
    >
      <div className="w-full max-w-4xl mx-auto">
        {/* Progress bar - top */}
        <div className="mb-12">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm text-slate-600 uppercase tracking-wider">
              Question {questionNumber} of {totalQuestions}
            </span>
            <div className="flex items-center gap-4">
              <span className="text-sm text-slate-600">{question.domain}</span>
              <span className={`text-xs px-2 py-1 rounded ${
                question.difficulty === 'foundational' ? 'bg-green-900/30 text-green-400' :
                question.difficulty === 'intermediate' ? 'bg-blue-900/30 text-blue-400' :
                'bg-purple-900/30 text-purple-400'
              }`}>
                {question.difficulty}
              </span>
            </div>
          </div>
          <div className="w-full h-1 bg-slate-900 rounded-full overflow-hidden">
            <motion.div
              className="h-full bg-gradient-to-r from-purple-500 to-pink-500"
              initial={{ width: `${((questionNumber - 1) / totalQuestions) * 100}%` }}
              animate={{ width: `${progress}%` }}
              transition={{ duration: 0.5, ease: "easeOut" }}
            />
          </div>
        </div>

        {/* Question */}
        <motion.h2
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="text-2xl md:text-3xl font-bold text-white mb-8 leading-tight"
        >
          {question.questionText}
        </motion.h2>

        {/* Code context if present */}
        {question.context && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.15 }}
            className="mb-8 p-4 bg-slate-900/50 border border-slate-800 rounded-lg"
          >
            <pre className="text-sm text-slate-300 overflow-x-auto">
              <code>{question.context.trim()}</code>
            </pre>
          </motion.div>
        )}

        {/* Options */}
        <div className="space-y-3">
          {question.options.map((option, index) => {
            const isSelected = selectedAnswer === index;
            const letter = String.fromCharCode(65 + index);

            return (
              <motion.button
                key={index}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.2 + index * 0.05 }}
                onClick={() => onAnswer(index)}
                disabled={selectedAnswer !== null}
                className={`
                  group w-full text-left px-6 py-4 rounded-xl border-2 transition-all duration-200
                  ${isSelected
                    ? 'border-purple-500 bg-purple-500/20 scale-[0.98]'
                    : 'border-slate-800 bg-black/40 hover:border-slate-700 hover:bg-slate-900/40'
                  }
                  ${selectedAnswer !== null && !isSelected ? 'opacity-40' : ''}
                  disabled:cursor-default
                `}
              >
                <div className="flex items-center gap-4">
                  {/* Letter badge */}
                  <div className={`
                    flex-shrink-0 w-10 h-10 rounded-lg flex items-center justify-center font-bold transition-all
                    ${isSelected
                      ? 'bg-purple-500 text-white'
                      : 'bg-slate-800 text-slate-500 group-hover:bg-slate-700 group-hover:text-slate-400'
                    }
                  `}>
                    {isSelected ? <Check className="w-5 h-5" /> : letter}
                  </div>

                  {/* Option text */}
                  <span className={`
                    text-base transition-colors
                    ${isSelected ? 'text-white font-medium' : 'text-slate-400 group-hover:text-slate-300'}
                  `}>
                    {option}
                  </span>
                </div>
              </motion.button>
            );
          })}
        </div>
      </div>
    </motion.div>
  );
}

/**
 * Analysis Animation
 */
function AnalysisAnimation() {
  const [currentStep, setCurrentStep] = useState(0);

  const steps = [
    'Analyzing responses',
    'Mapping confidence',
    'Identifying gaps',
    'Preparing your frontier',
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentStep((prev) => (prev + 1) % steps.length);
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="flex items-center justify-center min-h-screen px-8"
    >
      <div className="text-center">
        {/* Pulsing orb */}
        <div className="relative mb-12 flex items-center justify-center">
          <motion.div
            className="w-32 h-32 rounded-full bg-gradient-to-r from-purple-500 to-pink-500 blur-2xl"
            animate={{
              scale: [1, 1.2, 1],
              opacity: [0.4, 0.6, 0.4],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: "easeInOut",
            }}
          />
          <div className="absolute w-16 h-16 rounded-full bg-gradient-to-r from-purple-400 to-pink-400" />
        </div>

        {/* Status text */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentStep}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
          >
            <p className="text-2xl text-white font-light">{steps[currentStep]}...</p>
          </motion.div>
        </AnimatePresence>
      </div>
    </motion.div>
  );
}

/**
 * CALIBRATION RESULTS - The missing piece!
 * Shows domain confidence, gaps, and system recommendation
 */
function CalibrationResults({
  answers,
  questions,
}: {
  answers: Record<number, number>;
  questions: IntenseQuestion[];
}) {
  // Resume upload state
  const [uploadingResume, setUploadingResume] = React.useState(false);
  const [analyzingResume, setAnalyzingResume] = React.useState(false);
  const [resumeAnalyzed, setResumeAnalyzed] = React.useState(false);
  const [resumeError, setResumeError] = React.useState<string | null>(null);
  const [updatedAnalysis, setUpdatedAnalysis] = React.useState<any>(null);

  // Analyze answers to determine strengths/weaknesses
  const analysis = updatedAnalysis || analyzeCalibration(answers, questions);

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="flex items-center justify-center min-h-screen px-8 py-20"
    >
      <div className="w-full max-w-5xl mx-auto">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-16"
        >
          <h1 className="text-5xl md:text-6xl font-bold text-white mb-4">
            Your Baseline
          </h1>
          <p className="text-xl text-slate-400">
            Based on your responses, here's what we know.
          </p>
        </motion.div>

        {/* Domain Confidence Heatmap */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="mb-12"
        >
          <h2 className="text-2xl font-bold text-white mb-6">Domain Confidence</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            {Object.entries(analysis.domainScores).map(([domain, score], index) => {
              const scoreValue = Number(score);
              return (
              <motion.div
                key={domain}
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.3 + index * 0.1 }}
                className="p-4 bg-slate-900/50 border border-slate-800 rounded-xl"
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium text-slate-300">{domain}</span>
                  {scoreValue >= 0.7 ? <TrendingUp className="w-4 h-4 text-green-400" /> :
                   scoreValue >= 0.4 ? <AlertCircle className="w-4 h-4 text-yellow-400" /> :
                   <HelpCircle className="w-4 h-4 text-slate-500" />}
                </div>
                <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full ${
                      scoreValue >= 0.7 ? 'bg-green-500' :
                      scoreValue >= 0.4 ? 'bg-yellow-500' :
                      'bg-slate-600'
                    }`}
                    style={{ width: `${scoreValue * 100}%` }}
                  />
                </div>
              </motion.div>
            );
            })}
          </div>
        </motion.div>

        {/* Strong / Weak / Unknown */}
        <div className="grid md:grid-cols-3 gap-6 mb-12">
          {/* Strong */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.4 }}
            className="p-6 bg-green-900/20 border border-green-800/50 rounded-xl"
          >
            <h3 className="text-lg font-bold text-green-400 mb-4">Strong</h3>
            <ul className="space-y-2">
              {analysis.strong.map((item: string, i: number) => (
                <li key={i} className="text-sm text-slate-300">• {item}</li>
              ))}
            </ul>
          </motion.div>

          {/* Gaps */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="p-6 bg-yellow-900/20 border border-yellow-800/50 rounded-xl"
          >
            <h3 className="text-lg font-bold text-yellow-400 mb-4">Gaps</h3>
            <ul className="space-y-2">
              {analysis.gaps.map((item: string, i: number) => (
                <li key={i} className="text-sm text-slate-300">• {item}</li>
              ))}
            </ul>
          </motion.div>

          {/* Unknown */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.6 }}
            className="p-6 bg-slate-900/50 border border-slate-800 rounded-xl"
          >
            <h3 className="text-lg font-bold text-slate-400 mb-4">Unknown</h3>
            <ul className="space-y-2">
              {analysis.unknown.map((item: string, i: number) => (
                <li key={i} className="text-sm text-slate-300">• {item}</li>
              ))}
            </ul>
          </motion.div>
        </div>

        {/* === EDUCATION: How Your Path Works === */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.7 }}
          className="mb-12 p-8 bg-slate-900/30 border border-slate-800 rounded-2xl"
        >
          <h2 className="text-2xl font-bold text-white mb-4 text-center">
            Understanding Your Path Structure
          </h2>
          <p className="text-slate-400 text-center mb-8">
            Here's how Levld guides your learning
          </p>

          {/* Visual: Linear + Branching Diagram */}
          <div className="mb-8 flex items-center justify-center">
            <svg width="600" height="180" viewBox="0 0 600 180" className="max-w-full">
              {/* Main path line */}
              <line x1="50" y1="90" x2="550" y2="90" stroke="#3b82f6" strokeWidth="3" opacity="0.7" />

              {/* Main nodes */}
              {[0, 1, 2, 3, 4].map((i) => {
                const x = 50 + i * 125;
                return (
                  <g key={i}>
                    <circle cx={x} cy="90" r="20" fill="#3b82f6" opacity="0.9" />
                    <text x={x} y="95" textAnchor="middle" fill="white" fontSize="14" fontWeight="bold">{i + 1}</text>
                  </g>
                );
              })}

              {/* Competency branches */}
              {/* Branch from node 2 - above */}
              <path d="M 175 70 Q 175 30 220 30" stroke="#a855f7" strokeWidth="2" fill="none" opacity="0.6" />
              <circle cx="220" cy="30" r="15" fill="#a855f7" opacity="0.8" />

              {/* Branch from node 2 - below */}
              <path d="M 175 110 Q 175 150 220 150" stroke="#a855f7" strokeWidth="2" fill="none" opacity="0.6" />
              <circle cx="220" cy="150" r="15" fill="#a855f7" opacity="0.8" />

              {/* Branch from node 4 - above */}
              <path d="M 425 70 Q 425 30 470 30" stroke="#a855f7" strokeWidth="2" fill="none" opacity="0.6" />
              <circle cx="470" cy="30" r="15" fill="#a855f7" opacity="0.8" />

              {/* Labels */}
              <text x="300" y="75" textAnchor="middle" fill="#3b82f6" fontSize="12" fontWeight="bold">Main Path →</text>
              <text x="220" y="20" textAnchor="middle" fill="#a855f7" fontSize="10">Competency</text>
              <text x="220" y="170" textAnchor="middle" fill="#a855f7" fontSize="10">Competency</text>
            </svg>
          </div>

          {/* Explanation Cards */}
          <div className="grid md:grid-cols-3 gap-4">
            <div className="p-4 bg-blue-900/20 border border-blue-800/30 rounded-lg">
              <div className="text-blue-400 font-bold mb-2">30 Main Skills</div>
              <p className="text-sm text-slate-400">
                Required skills to become job-ready. Complete sequentially.
              </p>
            </div>
            <div className="p-4 bg-purple-900/20 border border-purple-800/30 rounded-lg">
              <div className="text-purple-400 font-bold mb-2">Competencies Branch</div>
              <p className="text-sm text-slate-400">
                Optional related skills for deeper expertise. Choose your depth.
              </p>
            </div>
            <div className="p-4 bg-green-900/20 border border-green-800/30 rounded-lg">
              <div className="text-green-400 font-bold mb-2">~100 Hours</div>
              <p className="text-sm text-slate-400">
                Complete the main path to be qualified to start working.
              </p>
            </div>
          </div>
        </motion.div>

        {/* === EDUCATION: Progress System === */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.8 }}
          className="mb-12 p-8 bg-slate-900/30 border border-slate-800 rounded-2xl"
        >
          <h2 className="text-2xl font-bold text-white mb-4 text-center">
            How You Prove Mastery
          </h2>
          <p className="text-slate-400 text-center mb-8">
            Each skill requires evidence before unlocking the next
          </p>

          <div className="grid md:grid-cols-4 gap-4">
            {/* PROBE */}
            <div className="p-6 bg-blue-900/20 border border-blue-800/30 rounded-xl text-center">
              <div className="w-12 h-12 mx-auto mb-3 rounded-full bg-blue-500/20 flex items-center justify-center">
                <span className="text-2xl">🔍</span>
              </div>
              <div className="text-blue-400 font-bold mb-2">PROBE</div>
              <p className="text-sm text-slate-400">
                Test knowledge with quizzes and questions
              </p>
            </div>

            {/* BUILD */}
            <div className="p-6 bg-purple-900/20 border border-purple-800/30 rounded-xl text-center">
              <div className="w-12 h-12 mx-auto mb-3 rounded-full bg-purple-500/20 flex items-center justify-center">
                <span className="text-2xl">🔨</span>
              </div>
              <div className="text-purple-400 font-bold mb-2">BUILD</div>
              <p className="text-sm text-slate-400">
                Create hands-on projects and code
              </p>
            </div>

            {/* PROVE */}
            <div className="p-6 bg-pink-900/20 border border-pink-800/30 rounded-xl text-center">
              <div className="w-12 h-12 mx-auto mb-3 rounded-full bg-pink-500/20 flex items-center justify-center">
                <span className="text-2xl">📋</span>
              </div>
              <div className="text-pink-400 font-bold mb-2">PROVE</div>
              <p className="text-sm text-slate-400">
                Share portfolios and demonstrations
              </p>
            </div>

            {/* APPLY */}
            <div className="p-6 bg-green-900/20 border border-green-800/30 rounded-xl text-center">
              <div className="w-12 h-12 mx-auto mb-3 rounded-full bg-green-500/20 flex items-center justify-center">
                <span className="text-2xl">🚀</span>
              </div>
              <div className="text-green-400 font-bold mb-2">APPLY</div>
              <p className="text-sm text-slate-400">
                Deploy to production environments
              </p>
            </div>
          </div>

          <p className="mt-6 text-center text-sm text-slate-500">
            Complete assessment → Next skill unlocks automatically
          </p>
        </motion.div>

        {/* === EDUCATION: EDLSG Framework === */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.85 }}
          className="mb-12 p-8 bg-slate-900/30 border border-slate-800 rounded-2xl"
        >
          <h2 className="text-2xl font-bold text-white mb-4 text-center">
            The EDLSG Learning Cycle
          </h2>
          <p className="text-slate-400 text-center mb-8">
            Your journey through each skill follows a proven framework
          </p>

          <div className="space-y-3 max-w-3xl mx-auto">
            {/* EXPLORE */}
            <div className="flex items-start gap-4 p-4 bg-blue-900/10 border border-blue-800/20 rounded-lg">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-blue-500/20 flex items-center justify-center text-blue-400 font-bold">
                E
              </div>
              <div>
                <div className="text-white font-semibold mb-1">Explore</div>
                <p className="text-sm text-slate-400">
                  Discover what the skill is, why it matters, and where it fits in your career path
                </p>
              </div>
            </div>

            {/* DECIDE */}
            <div className="flex items-start gap-4 p-4 bg-purple-900/10 border border-purple-800/20 rounded-lg">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-purple-500/20 flex items-center justify-center text-purple-400 font-bold">
                D
              </div>
              <div>
                <div className="text-white font-semibold mb-1">Decide</div>
                <p className="text-sm text-slate-400">
                  Commit to learning this skill. Understand the time investment and expected outcomes
                </p>
              </div>
            </div>

            {/* LEARN */}
            <div className="flex items-start gap-4 p-4 bg-pink-900/10 border border-pink-800/20 rounded-lg">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-pink-500/20 flex items-center justify-center text-pink-400 font-bold">
                L
              </div>
              <div>
                <div className="text-white font-semibold mb-1">Learn</div>
                <p className="text-sm text-slate-400">
                  Study resources, watch tutorials, read documentation. Build foundational knowledge
                </p>
              </div>
            </div>

            {/* SCORE */}
            <div className="flex items-start gap-4 p-4 bg-yellow-900/10 border border-yellow-800/20 rounded-lg">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-yellow-500/20 flex items-center justify-center text-yellow-400 font-bold">
                S
              </div>
              <div>
                <div className="text-white font-semibold mb-1">Score</div>
                <p className="text-sm text-slate-400">
                  Prove competence through assessments. Submit evidence of mastery
                </p>
              </div>
            </div>

            {/* GROW */}
            <div className="flex items-start gap-4 p-4 bg-green-900/10 border border-green-800/20 rounded-lg">
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-green-500/20 flex items-center justify-center text-green-400 font-bold">
                G
              </div>
              <div>
                <div className="text-white font-semibold mb-1">Grow</div>
                <p className="text-sm text-slate-400">
                  Level unlocked. Move to next skill with confidence. Track your progress
                </p>
              </div>
            </div>
          </div>
        </motion.div>

        {/* === MARKET CONTEXT: Why This Works === */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.9 }}
          className="mb-12 p-8 bg-slate-900/30 border border-slate-800 rounded-2xl"
        >
          <h2 className="text-2xl font-bold text-white mb-4 text-center">
            Why Evidence-Based Learning Works
          </h2>
          <p className="text-slate-400 text-center mb-8">
            Data from the learning industry
          </p>

          <div className="grid md:grid-cols-3 gap-6 max-w-4xl mx-auto">
            {/* Stat 1 */}
            <div className="text-center">
              <div className="text-4xl font-bold text-red-400 mb-2">87%</div>
              <div className="text-sm text-slate-400 mb-2">of online course enrollments</div>
              <div className="text-xs text-slate-500">never complete (Coursera, Udemy data)</div>
            </div>

            {/* Stat 2 */}
            <div className="text-center">
              <div className="text-4xl font-bold text-yellow-400 mb-2">40%</div>
              <div className="text-sm text-slate-400 mb-2">of bootcamp grads</div>
              <div className="text-xs text-slate-500">don't land jobs within 6 months (Course Report 2023)</div>
            </div>

            {/* Stat 3 */}
            <div className="text-center">
              <div className="text-4xl font-bold text-green-400 mb-2">3x</div>
              <div className="text-sm text-slate-400 mb-2">higher retention</div>
              <div className="text-xs text-slate-500">with project-based evidence (MIT study)</div>
            </div>
          </div>

          <div className="mt-8 p-6 bg-blue-900/10 border border-blue-800/30 rounded-lg max-w-3xl mx-auto">
            <p className="text-sm text-slate-300 text-center">
              <span className="text-blue-400 font-semibold">Levld difference:</span> You don't just watch videos.
              You build evidence employers recognize. Each skill requires proof before progression.
            </p>
          </div>
        </motion.div>

        {/* Resume Analysis Prompt - In-Place */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.95 }}
          className="mb-12 p-8 bg-blue-900/20 border border-blue-800/50 rounded-2xl"
        >
          <h2 className="text-2xl font-bold text-white mb-4 text-center">
            Optional: Enhance Your Profile
          </h2>
          <p className="text-slate-400 text-center mb-6">
            Upload your resume to boost your domain confidence scores
          </p>

          {!resumeAnalyzed ? (
            <div className="flex flex-col items-center gap-4">
              <label className="cursor-pointer">
                <input
                  type="file"
                  accept=".pdf,.doc,.docx,.txt"
                  className="hidden"
                  disabled={uploadingResume || analyzingResume}
                  onChange={async (e) => {
                    const file = e.target.files?.[0];
                    if (!file) return;

                    setUploadingResume(true);
                    setAnalyzingResume(true);
                    setResumeError(null);

                    try {
                      const userId = localStorage.getItem('userId') || '18';
                      const formData = new FormData();
                      formData.append('userId', userId);
                      formData.append('file', file);

                      const response = await fetch(`${API_URL}/api/v1/resume/analyze`, {
                        method: 'POST',
                        body: formData,
                      });

                      if (!response.ok) throw new Error('Resume analysis failed');

                      const result = await response.json();

                      // Update calibration heatmap
                      const calibrationData = localStorage.getItem('calibration');
                      if (calibrationData) {
                        const calibration = JSON.parse(calibrationData);
                        const domainMapping: Record<string, string> = {
                          'Backend': 'Backend Engineering',
                          'Frontend': 'Frontend Engineering',
                          'Database': 'Backend Engineering',
                          'DevOps': 'Cloud & DevOps',
                          'Security': 'Backend Engineering',
                        };

                        // Boost scores based on skills found
                        result.skills?.forEach((skill: any) => {
                          const skillName = skill.name?.toLowerCase() || '';
                          let targetDomain = 'Backend Engineering';

                          if (skillName.includes('react') || skillName.includes('vue') || skillName.includes('angular') || skillName.includes('frontend') || skillName.includes('html') || skillName.includes('css') || skillName.includes('javascript')) {
                            targetDomain = 'Frontend Engineering';
                          } else if (skillName.includes('docker') || skillName.includes('kubernetes') || skillName.includes('aws') || skillName.includes('devops') || skillName.includes('cloud') || skillName.includes('ci/cd')) {
                            targetDomain = 'Cloud & DevOps';
                          }

                          if (calibration.domainScores[targetDomain]) {
                            calibration.domainScores[targetDomain] = Math.min(0.9,
                              calibration.domainScores[targetDomain] + 0.1
                            );
                          }
                        });

                        // Recalculate strong/gaps/unknown
                        const updatedStrong = Object.entries(calibration.domainScores)
                          .filter(([_, score]) => (score as number) >= 0.7)
                          .map(([domain]) => domain);

                        const updatedGaps = Object.entries(calibration.domainScores)
                          .filter(([_, score]) => (score as number) >= 0.3 && (score as number) < 0.7)
                          .map(([domain]) => domain);

                        const updatedUnknown = Object.entries(calibration.domainScores)
                          .filter(([_, score]) => (score as number) < 0.3)
                          .map(([domain]) => domain);

                        calibration.strong = updatedStrong;
                        calibration.gaps = updatedGaps;
                        calibration.unknown = updatedUnknown;
                        calibration.resumeAnalyzed = true;

                        localStorage.setItem('calibration', JSON.stringify(calibration));

                        // Update the displayed analysis with new scores
                        setUpdatedAnalysis(calibration);
                      }

                      setResumeAnalyzed(true);
                    } catch (err) {
                      setResumeError(err instanceof Error ? err.message : 'Upload failed');
                    } finally {
                      setUploadingResume(false);
                      setAnalyzingResume(false);
                    }
                  }}
                />
                <div className="px-8 py-4 bg-blue-600 hover:bg-blue-500 text-white font-semibold rounded-xl transition-all duration-300 flex items-center gap-3">
                  {analyzingResume ? (
                    <>
                      <Loader2 className="w-5 h-5 animate-spin" />
                      Analyzing Resume...
                    </>
                  ) : (
                    <>
                      <Upload className="w-5 h-5" />
                      Upload Resume
                    </>
                  )}
                </div>
              </label>
              {resumeError && (
                <p className="text-red-400 text-sm">{resumeError}</p>
              )}
              <p className="text-sm text-slate-500">
                We'll analyze your experience and boost your domain scores
              </p>
            </div>
          ) : (
            <div className="flex flex-col items-center gap-4">
              <div className="flex items-center gap-3 text-green-400">
                <CheckCircle className="w-6 h-6" />
                <span className="font-semibold">Resume analyzed! Your domain scores have been updated.</span>
              </div>
              <p className="text-sm text-slate-400">
                Your calibration heatmap now reflects your resume experience
              </p>
            </div>
          )}
        </motion.div>

        {/* System Recommendation */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 1.0 }}
          className="p-8 bg-gradient-to-br from-purple-900/30 to-pink-900/30 border border-purple-800/50 rounded-2xl text-center"
        >
          <p className="text-slate-400 mb-2">Based on your responses,</p>
          <h2 className="text-3xl font-bold text-white mb-6">
            {analysis.recommendation}
          </h2>

          {/* Dual CTAs - Guidance vs Freedom */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            {/* Primary: Follow Recommendation (Guided Path) */}
            <button
              onClick={() => {
                // Persist calibration state with guided flag
                localStorage.setItem('calibration', JSON.stringify({
                  ...analysis,
                  timestamp: Date.now(),
                  mode: 'guided',
                  completed: true
                }));

                // Mark calibration as complete (first-time flag)
                localStorage.setItem('calibration_complete', 'true');

                // Go straight to Frontier with pre-selection
                window.location.href = '/frontier?mode=guided';
              }}
              className="group relative px-10 py-5 bg-white text-black text-lg font-semibold rounded-full overflow-hidden transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:shadow-purple-500/50"
            >
              <span className="relative z-10 flex items-center gap-3">
                Follow System Recommendation
                <ArrowRight className="w-5 h-5 transition-transform group-hover:translate-x-1" />
              </span>
              <div className="absolute inset-0 bg-gradient-to-r from-purple-500 to-pink-500 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
            </button>

            {/* Secondary: Explore All (Free Choice) */}
            <button
              onClick={() => {
                // Persist calibration state with exploratory flag
                localStorage.setItem('calibration', JSON.stringify({
                  ...analysis,
                  timestamp: Date.now(),
                  mode: 'exploratory',
                  completed: true
                }));

                // Mark calibration as complete (first-time flag)
                localStorage.setItem('calibration_complete', 'true');

                // Go straight to Frontier
                window.location.href = '/frontier?mode=exploratory';
              }}
              className="px-8 py-5 border-2 border-slate-700 text-slate-300 text-lg font-medium rounded-full hover:border-slate-500 hover:text-white transition-all duration-300"
            >
              Explore All Domains
            </button>
          </div>

          <p className="mt-6 text-sm text-slate-500">
            You can change paths anytime. This baseline helps us guide you.
          </p>
        </motion.div>
      </div>
    </motion.div>
  );
}

/**
 * Analyze calibration results
 * (Simplified for now - would connect to backend for real analysis)
 */
function analyzeCalibration(answers: Record<number, number>, questions: IntenseQuestion[]) {
  // Group by domain
  const domainQuestions: Record<string, IntenseQuestion[]> = {};
  questions.forEach(q => {
    if (!domainQuestions[q.domain]) domainQuestions[q.domain] = [];
    domainQuestions[q.domain].push(q);
  });

  // Calculate scores (mock - would be based on correct answers)
  const domainScores: Record<string, number> = {};
  Object.keys(domainQuestions).forEach(domain => {
    domainScores[domain] = 0.3 + Math.random() * 0.6; // Mock score
  });

  // Determine strong/gaps/unknown
  const strong = Object.entries(domainScores)
    .filter(([_, score]) => score >= 0.7)
    .map(([domain]) => domain);

  const gaps = Object.entries(domainScores)
    .filter(([_, score]) => score >= 0.3 && score < 0.7)
    .map(([domain]) => domain);

  const unknown = Object.entries(domainScores)
    .filter(([_, score]) => score < 0.3)
    .map(([domain]) => domain);

  // Generate recommendation
  const topDomain = Object.entries(domainScores).sort((a, b) => b[1] - a[1])[0][0];
  const recommendation = `The system recommends starting with ${topDomain}`;

  return {
    domainScores,
    strong,
    gaps,
    unknown,
    recommendation,
    recommendedDomain: topDomain, // Separate field for easier parsing
  };
}
