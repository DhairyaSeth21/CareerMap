'use client';

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  X,
  ChevronRight,
  ChevronLeft,
  Sparkles,
  Target,
  Trophy,
  BookOpen,
  Code,
  FileCheck,
  Lock,
  Unlock,
  TrendingUp,
  Zap,
  HelpCircle,
  CheckCircle2,
  XCircle,
  AlertCircle
} from 'lucide-react';

interface PathTutorialProps {
  isOpen: boolean;
  onClose: () => void;
  onComplete?: () => void;
}

interface TutorialSlide {
  id: string;
  title: string;
  subtitle: string;
  content: React.ReactNode;
  icon: React.ReactNode;
  gradient: string;
}

export default function PathTutorial({ isOpen, onClose, onComplete }: PathTutorialProps) {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [direction, setDirection] = useState(0);

  const slides: TutorialSlide[] = [
    {
      id: 'welcome',
      title: 'Welcome to Your Learning Journey',
      subtitle: 'Let\'s understand how skill progression works',
      icon: <Sparkles className="w-12 h-12" />,
      gradient: 'from-purple-600 to-pink-600',
      content: (
        <div className="space-y-6">
          <p className="text-lg text-slate-300">
            This platform uses an <span className="text-purple-400 font-semibold">evidence-based learning system</span> to track your real skills, not just completion checkmarks.
          </p>
          <div className="grid grid-cols-2 gap-4 mt-6">
            <div className="bg-slate-800/50 rounded-xl p-4 border border-slate-700">
              <Target className="w-8 h-8 text-blue-400 mb-2" />
              <h4 className="font-semibold text-white">Skill Tracking</h4>
              <p className="text-sm text-slate-400">Every skill has a state that reflects your actual mastery</p>
            </div>
            <div className="bg-slate-800/50 rounded-xl p-4 border border-slate-700">
              <Trophy className="w-8 h-8 text-yellow-400 mb-2" />
              <h4 className="font-semibold text-white">Evidence-Based</h4>
              <p className="text-sm text-slate-400">Progress through quizzes, projects, and real work</p>
            </div>
          </div>
        </div>
      ),
    },
    {
      id: 'states',
      title: 'The 4 Skill States',
      subtitle: 'EDLSG Progression Model',
      icon: <TrendingUp className="w-12 h-12" />,
      gradient: 'from-blue-600 to-cyan-600',
      content: (
        <div className="space-y-4">
          <p className="text-slate-300 mb-6">
            Each skill progresses through 4 states. You can only advance by providing evidence of your knowledge.
          </p>

          {/* State progression visualization */}
          <div className="flex items-center justify-between bg-slate-800/50 rounded-xl p-4">
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 rounded-full bg-slate-700 flex items-center justify-center mb-2">
                <span className="text-2xl">👻</span>
              </div>
              <span className="text-xs text-slate-400">UNSEEN</span>
            </div>
            <ChevronRight className="w-6 h-6 text-slate-500" />
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 rounded-full bg-blue-900/50 border-2 border-blue-500 flex items-center justify-center mb-2">
                <span className="text-2xl">🔮</span>
              </div>
              <span className="text-xs text-blue-400">INFERRED</span>
            </div>
            <ChevronRight className="w-6 h-6 text-slate-500" />
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 rounded-full bg-yellow-900/50 border-2 border-yellow-500 flex items-center justify-center mb-2">
                <span className="text-2xl">🔥</span>
              </div>
              <span className="text-xs text-yellow-400">ACTIVE</span>
            </div>
            <ChevronRight className="w-6 h-6 text-slate-500" />
            <div className="flex flex-col items-center">
              <div className="w-16 h-16 rounded-full bg-green-900/50 border-2 border-green-500 flex items-center justify-center mb-2">
                <span className="text-2xl">✅</span>
              </div>
              <span className="text-xs text-green-400">PROVED</span>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 mt-4">
            <div className="bg-slate-800/30 rounded-lg p-3 border-l-4 border-slate-500">
              <h5 className="font-medium text-slate-300">UNSEEN</h5>
              <p className="text-xs text-slate-500">Haven't encountered this skill yet</p>
            </div>
            <div className="bg-blue-900/20 rounded-lg p-3 border-l-4 border-blue-500">
              <h5 className="font-medium text-blue-300">INFERRED</h5>
              <p className="text-xs text-slate-500">Detected from calibration/resume</p>
            </div>
            <div className="bg-yellow-900/20 rounded-lg p-3 border-l-4 border-yellow-500">
              <h5 className="font-medium text-yellow-300">ACTIVE</h5>
              <p className="text-xs text-slate-500">Currently learning this skill</p>
            </div>
            <div className="bg-green-900/20 rounded-lg p-3 border-l-4 border-green-500">
              <h5 className="font-medium text-green-300">PROVED</h5>
              <p className="text-xs text-slate-500">Mastered! Evidence confirms it</p>
            </div>
          </div>
        </div>
      ),
    },
    {
      id: 'scoring',
      title: 'Scoring & Advancement',
      subtitle: 'What scores let you progress?',
      icon: <Target className="w-12 h-12" />,
      gradient: 'from-green-600 to-emerald-600',
      content: (
        <div className="space-y-4">
          <p className="text-slate-300 mb-4">
            We use a <span className="text-yellow-400 font-semibold">strict scoring curve</span> to ensure you truly understand the material before advancing.
          </p>

          {/* Scoring visualization */}
          <div className="bg-slate-800/50 rounded-xl p-4 space-y-3">
            <h4 className="font-semibold text-white mb-3">Quiz Score → Skill Support</h4>

            <div className="space-y-2">
              <div className="flex items-center gap-3">
                <div className="w-20 text-right">
                  <span className="text-green-400 font-mono">95-100%</span>
                </div>
                <div className="flex-1 h-6 bg-slate-700 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-green-500 to-emerald-400" style={{ width: '100%' }}></div>
                </div>
                <div className="w-32 flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-green-400" />
                  <span className="text-green-400 text-sm">→ PROVED</span>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="w-20 text-right">
                  <span className="text-blue-400 font-mono">85-94%</span>
                </div>
                <div className="flex-1 h-6 bg-slate-700 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-blue-500 to-cyan-400" style={{ width: '85%' }}></div>
                </div>
                <div className="w-32 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-blue-400" />
                  <span className="text-blue-400 text-sm">Strong</span>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="w-20 text-right">
                  <span className="text-yellow-400 font-mono">70-84%</span>
                </div>
                <div className="flex-1 h-6 bg-slate-700 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-yellow-500 to-orange-400" style={{ width: '65%' }}></div>
                </div>
                <div className="w-32 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-yellow-400" />
                  <span className="text-yellow-400 text-sm">→ ACTIVE</span>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="w-20 text-right">
                  <span className="text-orange-400 font-mono">50-69%</span>
                </div>
                <div className="flex-1 h-6 bg-slate-700 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-orange-500 to-red-400" style={{ width: '40%' }}></div>
                </div>
                <div className="w-32 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-orange-400" />
                  <span className="text-orange-400 text-sm">Weak</span>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="w-20 text-right">
                  <span className="text-red-400 font-mono">&lt;50%</span>
                </div>
                <div className="flex-1 h-6 bg-slate-700 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-red-500 to-red-600" style={{ width: '20%' }}></div>
                </div>
                <div className="w-32 flex items-center gap-2">
                  <XCircle className="w-4 h-4 text-red-400" />
                  <span className="text-red-400 text-sm">No change</span>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-purple-900/20 border border-purple-500/30 rounded-lg p-3 mt-4">
            <p className="text-sm text-purple-300">
              <Zap className="w-4 h-4 inline mr-1" />
              <strong>Pro tip:</strong> You need 95%+ to mark a skill as PROVED. Keep practicing until you truly master it!
            </p>
          </div>
        </div>
      ),
    },
    {
      id: 'assessments',
      title: 'Assessment Types',
      subtitle: '3 ways to prove your skills',
      icon: <FileCheck className="w-12 h-12" />,
      gradient: 'from-orange-600 to-red-600',
      content: (
        <div className="space-y-4">
          <p className="text-slate-300 mb-4">
            Different skills require different types of proof. Here are the three assessment methods:
          </p>

          <div className="space-y-4">
            {/* PROBE */}
            <div className="bg-slate-800/50 rounded-xl p-4 border border-blue-500/30">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-lg bg-blue-500/20 flex items-center justify-center flex-shrink-0">
                  <HelpCircle className="w-6 h-6 text-blue-400" />
                </div>
                <div className="flex-1">
                  <h4 className="font-semibold text-blue-400 flex items-center gap-2">
                    PROBE
                    <span className="text-xs bg-blue-500/20 px-2 py-0.5 rounded">Quick Assessment</span>
                  </h4>
                  <p className="text-sm text-slate-400 mt-1">
                    5-10 multiple choice questions to test your conceptual understanding. Great for theory-heavy skills.
                  </p>
                  <div className="flex gap-2 mt-2">
                    <span className="text-xs bg-slate-700 px-2 py-1 rounded">~5 minutes</span>
                    <span className="text-xs bg-slate-700 px-2 py-1 rounded">AI-generated</span>
                  </div>
                </div>
              </div>
            </div>

            {/* BUILD */}
            <div className="bg-slate-800/50 rounded-xl p-4 border border-yellow-500/30">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-lg bg-yellow-500/20 flex items-center justify-center flex-shrink-0">
                  <Code className="w-6 h-6 text-yellow-400" />
                </div>
                <div className="flex-1">
                  <h4 className="font-semibold text-yellow-400 flex items-center gap-2">
                    BUILD
                    <span className="text-xs bg-yellow-500/20 px-2 py-0.5 rounded">Project-Based</span>
                  </h4>
                  <p className="text-sm text-slate-400 mt-1">
                    Submit a project or code that demonstrates the skill. AI analyzes your work and extracts skill evidence.
                  </p>
                  <div className="flex gap-2 mt-2">
                    <span className="text-xs bg-slate-700 px-2 py-1 rounded">GitHub/Code</span>
                    <span className="text-xs bg-slate-700 px-2 py-1 rounded">AI-analyzed</span>
                  </div>
                </div>
              </div>
            </div>

            {/* PROVE */}
            <div className="bg-slate-800/50 rounded-xl p-4 border border-green-500/30">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-lg bg-green-500/20 flex items-center justify-center flex-shrink-0">
                  <Trophy className="w-6 h-6 text-green-400" />
                </div>
                <div className="flex-1">
                  <h4 className="font-semibold text-green-400 flex items-center gap-2">
                    PROVE
                    <span className="text-xs bg-green-500/20 px-2 py-0.5 rounded">External Evidence</span>
                  </h4>
                  <p className="text-sm text-slate-400 mt-1">
                    Submit certifications, work samples, or portfolio pieces as proof. Great for professional experience.
                  </p>
                  <div className="flex gap-2 mt-2">
                    <span className="text-xs bg-slate-700 px-2 py-1 rounded">Certs</span>
                    <span className="text-xs bg-slate-700 px-2 py-1 rounded">Portfolio</span>
                    <span className="text-xs bg-slate-700 px-2 py-1 rounded">Work samples</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      ),
    },
    {
      id: 'dependencies',
      title: 'Skill Dependencies',
      subtitle: 'How skills unlock',
      icon: <Unlock className="w-12 h-12" />,
      gradient: 'from-violet-600 to-purple-600',
      content: (
        <div className="space-y-4">
          <p className="text-slate-300 mb-4">
            Skills have <span className="text-purple-400 font-semibold">prerequisites</span>. You must prove earlier skills to unlock advanced ones.
          </p>

          {/* Dependency visualization */}
          <div className="bg-slate-800/50 rounded-xl p-6">
            <div className="flex flex-col items-center space-y-4">
              {/* Level 1 */}
              <div className="flex items-center gap-4">
                <div className="w-24 h-16 rounded-lg bg-green-900/50 border-2 border-green-500 flex flex-col items-center justify-center">
                  <CheckCircle2 className="w-5 h-5 text-green-400" />
                  <span className="text-xs text-green-400 mt-1">JavaScript</span>
                </div>
              </div>

              {/* Arrow */}
              <div className="flex flex-col items-center">
                <div className="w-0.5 h-4 bg-slate-600"></div>
                <div className="text-slate-500 text-xs">prerequisite</div>
                <div className="w-0.5 h-4 bg-slate-600"></div>
              </div>

              {/* Level 2 */}
              <div className="flex items-center gap-4">
                <div className="w-24 h-16 rounded-lg bg-yellow-900/50 border-2 border-yellow-500 flex flex-col items-center justify-center">
                  <Unlock className="w-5 h-5 text-yellow-400" />
                  <span className="text-xs text-yellow-400 mt-1">React</span>
                </div>
              </div>

              {/* Arrow */}
              <div className="flex flex-col items-center">
                <div className="w-0.5 h-4 bg-slate-600"></div>
                <div className="text-slate-500 text-xs">prerequisite</div>
                <div className="w-0.5 h-4 bg-slate-600"></div>
              </div>

              {/* Level 3 */}
              <div className="flex items-center gap-4">
                <div className="w-24 h-16 rounded-lg bg-slate-700 border-2 border-slate-600 flex flex-col items-center justify-center opacity-50">
                  <Lock className="w-5 h-5 text-slate-500" />
                  <span className="text-xs text-slate-500 mt-1">Next.js</span>
                </div>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 mt-4">
            <div className="bg-green-900/20 rounded-lg p-3 border border-green-500/30">
              <div className="flex items-center gap-2 mb-1">
                <Unlock className="w-4 h-4 text-green-400" />
                <span className="text-green-400 font-medium">Unlocked</span>
              </div>
              <p className="text-xs text-slate-400">All prerequisites are PROVED</p>
            </div>
            <div className="bg-slate-800/50 rounded-lg p-3 border border-slate-600">
              <div className="flex items-center gap-2 mb-1">
                <Lock className="w-4 h-4 text-slate-500" />
                <span className="text-slate-400 font-medium">Locked</span>
              </div>
              <p className="text-xs text-slate-500">Complete prerequisites first</p>
            </div>
          </div>
        </div>
      ),
    },
    {
      id: 'tips',
      title: 'Pro Tips',
      subtitle: 'Maximize your learning',
      icon: <Zap className="w-12 h-12" />,
      gradient: 'from-yellow-500 to-orange-500',
      content: (
        <div className="space-y-4">
          <div className="space-y-3">
            <div className="bg-slate-800/50 rounded-xl p-4 border-l-4 border-purple-500">
              <h4 className="font-semibold text-purple-400 mb-1">1. Don't rush quizzes</h4>
              <p className="text-sm text-slate-400">Take time to understand concepts. A 95% score is worth more than three 70% attempts.</p>
            </div>

            <div className="bg-slate-800/50 rounded-xl p-4 border-l-4 border-blue-500">
              <h4 className="font-semibold text-blue-400 mb-1">2. Submit real projects</h4>
              <p className="text-sm text-slate-400">GitHub repos and real code count more than quizzes. Our AI can extract multiple skills from one project.</p>
            </div>

            <div className="bg-slate-800/50 rounded-xl p-4 border-l-4 border-green-500">
              <h4 className="font-semibold text-green-400 mb-1">3. Follow the path order</h4>
              <p className="text-sm text-slate-400">Skills are ordered by difficulty. Skipping ahead often leads to gaps in understanding.</p>
            </div>

            <div className="bg-slate-800/50 rounded-xl p-4 border-l-4 border-yellow-500">
              <h4 className="font-semibold text-yellow-400 mb-1">4. Use resources first</h4>
              <p className="text-sm text-slate-400">Each skill has curated videos, articles, and docs. Study before taking the assessment.</p>
            </div>

            <div className="bg-slate-800/50 rounded-xl p-4 border-l-4 border-pink-500">
              <h4 className="font-semibold text-pink-400 mb-1">5. Retry is okay!</h4>
              <p className="text-sm text-slate-400">Low scores won't hurt you. Study more and try again when you're ready.</p>
            </div>
          </div>
        </div>
      ),
    },
    {
      id: 'ready',
      title: 'You\'re Ready!',
      subtitle: 'Start your journey',
      icon: <Trophy className="w-12 h-12" />,
      gradient: 'from-emerald-500 to-green-500',
      content: (
        <div className="space-y-6 text-center">
          <div className="w-24 h-24 rounded-full bg-gradient-to-br from-green-400 to-emerald-500 mx-auto flex items-center justify-center">
            <CheckCircle2 className="w-12 h-12 text-white" />
          </div>

          <div>
            <h3 className="text-2xl font-bold text-white mb-2">You've got this!</h3>
            <p className="text-slate-400">
              Now you understand how skill progression works. Time to start learning!
            </p>
          </div>

          <div className="bg-slate-800/50 rounded-xl p-4 text-left">
            <h4 className="font-semibold text-white mb-3">Quick Recap:</h4>
            <ul className="space-y-2 text-sm text-slate-300">
              <li className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-purple-500"></div>
                4 skill states: UNSEEN → INFERRED → ACTIVE → PROVED
              </li>
              <li className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-green-500"></div>
                95%+ quiz score = PROVED status
              </li>
              <li className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-blue-500"></div>
                3 assessment types: PROBE, BUILD, PROVE
              </li>
              <li className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-yellow-500"></div>
                Skills unlock based on prerequisites
              </li>
            </ul>
          </div>

          <p className="text-slate-500 text-sm">
            You can access this tutorial anytime from the <HelpCircle className="w-4 h-4 inline" /> help button
          </p>
        </div>
      ),
    },
  ];

  const nextSlide = () => {
    if (currentSlide < slides.length - 1) {
      setDirection(1);
      setCurrentSlide(prev => prev + 1);
    } else {
      // Last slide - complete tutorial
      localStorage.setItem('pathTutorialComplete', 'true');
      onComplete?.();
      onClose();
    }
  };

  const prevSlide = () => {
    if (currentSlide > 0) {
      setDirection(-1);
      setCurrentSlide(prev => prev - 1);
    }
  };

  const goToSlide = (index: number) => {
    setDirection(index > currentSlide ? 1 : -1);
    setCurrentSlide(index);
  };

  // Keyboard navigation
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (!isOpen) return;
      if (e.key === 'ArrowRight' || e.key === 'Enter') nextSlide();
      if (e.key === 'ArrowLeft') prevSlide();
      if (e.key === 'Escape') onClose();
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, currentSlide]);

  if (!isOpen) return null;

  const slide = slides[currentSlide];

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4"
        onClick={(e) => e.target === e.currentTarget && onClose()}
      >
        <motion.div
          initial={{ scale: 0.9, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          exit={{ scale: 0.9, opacity: 0 }}
          className="w-full max-w-2xl bg-slate-900 rounded-2xl border border-slate-700 shadow-2xl overflow-hidden"
        >
          {/* Header */}
          <div className={`bg-gradient-to-r ${slide.gradient} p-6`}>
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-4">
                <div className="w-16 h-16 rounded-xl bg-white/20 flex items-center justify-center text-white">
                  {slide.icon}
                </div>
                <div>
                  <h2 className="text-2xl font-bold text-white">{slide.title}</h2>
                  <p className="text-white/80">{slide.subtitle}</p>
                </div>
              </div>
              <button
                onClick={onClose}
                className="p-2 rounded-lg bg-white/10 hover:bg-white/20 transition-colors text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          </div>

          {/* Content */}
          <div className="p-6 min-h-[400px]">
            <AnimatePresence mode="wait">
              <motion.div
                key={currentSlide}
                initial={{ opacity: 0, x: direction * 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -direction * 20 }}
                transition={{ duration: 0.2 }}
              >
                {slide.content}
              </motion.div>
            </AnimatePresence>
          </div>

          {/* Footer */}
          <div className="px-6 pb-6">
            {/* Progress dots */}
            <div className="flex justify-center gap-2 mb-4">
              {slides.map((_, index) => (
                <button
                  key={index}
                  onClick={() => goToSlide(index)}
                  className={`w-2 h-2 rounded-full transition-all ${
                    index === currentSlide
                      ? 'w-6 bg-purple-500'
                      : index < currentSlide
                      ? 'bg-purple-500/50'
                      : 'bg-slate-600'
                  }`}
                />
              ))}
            </div>

            {/* Navigation buttons */}
            <div className="flex justify-between items-center">
              <button
                onClick={prevSlide}
                disabled={currentSlide === 0}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-colors ${
                  currentSlide === 0
                    ? 'text-slate-600 cursor-not-allowed'
                    : 'text-slate-300 hover:bg-slate-800'
                }`}
              >
                <ChevronLeft className="w-5 h-5" />
                Back
              </button>

              <span className="text-slate-500 text-sm">
                {currentSlide + 1} / {slides.length}
              </span>

              <button
                onClick={nextSlide}
                className={`flex items-center gap-2 px-6 py-2 rounded-lg font-medium transition-colors ${
                  currentSlide === slides.length - 1
                    ? 'bg-green-600 hover:bg-green-500 text-white'
                    : 'bg-purple-600 hover:bg-purple-500 text-white'
                }`}
              >
                {currentSlide === slides.length - 1 ? (
                  <>
                    Start Learning
                    <Sparkles className="w-5 h-5" />
                  </>
                ) : (
                  <>
                    Next
                    <ChevronRight className="w-5 h-5" />
                  </>
                )}
              </button>
            </div>
          </div>
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
}
