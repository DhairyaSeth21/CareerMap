'use client';

import { useEffect, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { motion, AnimatePresence } from 'framer-motion';
import { PlayCircle, X, BookOpen, ExternalLink, HelpCircle } from 'lucide-react';
import DomainView from './DomainView';
import PathTutorial from '../../components/PathTutorial';
import RoleView from './RoleView';
import PathView from './PathView';
import NodeResources from './NodeResources';
import {
  Domain,
  CareerRole,
  DetailedPathNode,
  FocusNode,
  Quiz,
  QuizQuestion,
  ZoomLevel,
  ZoomState,
} from './types';
import { API_URL } from '../../config/api';

// Calibration state interface
interface CalibrationState {
  domainScores: Record<string, number>;
  strong: string[];
  gaps: string[];
  unknown: string[];
  recommendation: string;
  recommendedDomain: string;
  timestamp: number;
  mode: 'guided' | 'exploratory';
}

/**
 * Generate mock learning path when backend fails or returns empty
 * This ensures the UI always has something to display
 */
function generateMockPath(role: CareerRole): DetailedPathNode[] {
  const categories = ['foundational', 'core', 'advanced', 'specialized'];
  const mockNodes: DetailedPathNode[] = [];

  for (let i = 0; i < 8; i++) {
    mockNodes.push({
      skillNodeId: i + 1,
      name: `${role.name} Skill ${i + 1}`,
      whyItMatters: `This skill is essential for ${role.name} because it forms the ${categories[i % 4]} knowledge you need.`,
      learnResources: [
        {
          type: 'video',
          title: `Introduction to Skill ${i + 1}`,
          url: '#',
          description: 'Learn the fundamentals',
          estimatedMinutes: 30,
        },
        {
          type: 'article',
          title: `Deep Dive: Skill ${i + 1}`,
          url: '#',
          description: 'Advanced concepts',
          estimatedMinutes: 45,
        },
      ],
      assessmentType: i % 3 === 0 ? 'PROBE' : i % 3 === 1 ? 'BUILD' : 'PROVE',
      proofRequirement: `Complete a project demonstrating ${role.name} Skill ${i + 1}`,
      dependencies: i > 0 ? [i] : [],
      unlocks: i < 7 ? [i + 2] : [],
      difficulty: Math.min(10, 3 + i),
      estimatedHours: 5 + i * 2,
      category: categories[i % 4],
    });
  }

  return mockNodes;
}

/**
 * Frontier - THE APP with Zoom Architecture
 *
 * Zoom Levels:
 * 1. Domain Universe (Level 0) - Star map of career domains
 * 2. Role Constellation (Level 1) - Roles within selected domain
 * 3. Path Constellation (Level 2) - Detailed learning path with nodes
 * 4. Frontier Focus (Level 3) - Focus mode on ONE glowing node
 *
 * This is camera movement through nested visualizations, NOT routing/pages
 */

export default function Frontier() {
  const searchParams = useSearchParams();
  const [userId] = useState(() => {
    const storedUserId = localStorage.getItem('userId');
    return storedUserId ? parseInt(storedUserId) : 1; // Default to user 1 if not found
  });

  // Calibration state
  const [calibration, setCalibration] = useState<CalibrationState | null>(null);
  const [mode, setMode] = useState<'guided' | 'exploratory' | null>(null);

  // Completed nodes tracking
  const [completedNodeIds, setCompletedNodeIds] = useState<Set<number>>(new Set());

  // EDLSG phase tracking: Map<skillNodeId, phase>
  const [edlsgPhases, setEdlsgPhases] = useState<Map<number, 'explore' | 'decide' | 'learn' | 'score' | 'grow'>>(new Map());

  // Banner state - auto-dismiss after 5 seconds
  const [showBanner, setShowBanner] = useState(true);

  // Zoom state
  const [zoomState, setZoomState] = useState<ZoomState>({
    level: 'domain',
  });

  // Data state
  const [domains, setDomains] = useState<Domain[]>([]);
  const [roles, setRoles] = useState<CareerRole[]>([]);
  const [path, setPath] = useState<DetailedPathNode[]>([]);
  const [focusNode, setFocusNode] = useState<FocusNode | null>(null);
  const [sessionId, setSessionId] = useState<number | null>(null);

  // PROBE state
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<Map<number, string>>(new Map());

  // Results state
  const [results, setResults] = useState<any>(null);

  // UI state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showFocusPanel, setShowFocusPanel] = useState(false);
  const [showQuiz, setShowQuiz] = useState(false);
  const [showResults, setShowResults] = useState(false);

  // Tutorial state
  const [showTutorial, setShowTutorial] = useState(false);

  // Check if first-time user and show tutorial
  useEffect(() => {
    const tutorialComplete = localStorage.getItem('pathTutorialComplete');
    if (!tutorialComplete) {
      // Small delay to let the page render first
      const timer = setTimeout(() => {
        setShowTutorial(true);
      }, 1000);
      return () => clearTimeout(timer);
    }
  }, []);

  // Auto-dismiss banner after 5 seconds
  useEffect(() => {
    if (showBanner && calibration && zoomState.level === 'path') {
      const timer = setTimeout(() => {
        setShowBanner(false);
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [showBanner, calibration, zoomState.level]);

  // Load calibration state and handle mode on mount
  useEffect(() => {
    const calibrationData = localStorage.getItem('calibration');
    if (calibrationData) {
      const parsed: CalibrationState = JSON.parse(calibrationData);
      setCalibration(parsed);

      // Get mode from URL or calibration state
      const urlMode = searchParams.get('mode') as 'guided' | 'exploratory' | null;
      const effectiveMode = urlMode || parsed.mode;
      setMode(effectiveMode);
    }

    fetchDomains();
  }, [searchParams]);

  const fetchDomains = async () => {
    try {
      const response = await fetch(`${API_URL}/api/frontier/domains`);
      const data = await response.json();
      setDomains(data);
    } catch (err) {
      console.error('Failed to fetch domains:', err);
      setError('Failed to load domains');
    }
  };

  // Guided mode: Auto-select domain after domains load
  useEffect(() => {
    if (mode === 'guided' && calibration && domains.length > 0 && zoomState.level === 'domain') {
      // Extract recommended domain name from recommendation text
      const recommendedDomainName = calibration.recommendedDomain ||
        calibration.strong[0] ||
        Object.entries(calibration.domainScores).sort((a, b) => b[1] - a[1])[0][0];

      // Find matching domain
      const matchedDomain = domains.find(d =>
        d.name.toLowerCase().includes(recommendedDomainName.toLowerCase()) ||
        recommendedDomainName.toLowerCase().includes(d.name.toLowerCase())
      );

      if (matchedDomain) {
        // Auto-select the recommended domain
        handleSelectDomain(matchedDomain);
      }
    }
  }, [mode, calibration, domains, zoomState.level]);

  // Guided mode: DO NOT auto-select role - let user choose
  // (Removed auto-selection so users can pick their desired role)

  const fetchRolesForDomain = async (domainId: number) => {
    try {
      const response = await fetch(`${API_URL}/api/frontier/domains/${domainId}/roles`);
      const data = await response.json();
      setRoles(data);
    } catch (err) {
      console.error('Failed to fetch roles:', err);
      setError('Failed to load roles');
    }
  };

  /**
   * ZOOM LEVEL 0 → 1: Select Domain
   */
  const handleSelectDomain = async (domain: Domain) => {
    setLoading(true);
    setError(null);

    try {
      await fetchRolesForDomain(domain.domainId);
      setZoomState({
        level: 'role',
        selectedDomain: domain,
      });
    } catch (err) {
      setError('Failed to load roles');
    } finally {
      setLoading(false);
    }
  };

  /**
   * ZOOM LEVEL 1 → 2: Select Role & Generate Path
   */
  const handleSelectRole = async (role: CareerRole) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `${API_URL}/api/core-loop/generate-detailed-path?userId=${userId}&roleId=${role.careerRoleId}`,
        { method: 'POST' }
      );

      if (!response.ok) throw new Error('Failed to generate path');

      const data = await response.json();

      // If backend returns empty path, use mock data as fallback
      if (!data.path || data.path.length === 0) {
        console.warn('Backend returned empty path, using mock data');
        const mockPath = generateMockPath(role);
        setPath(mockPath);
        setFocusNode(mockPath[0]);
        setSessionId(null);

        setZoomState({
          level: 'path',
          selectedDomain: zoomState.selectedDomain,
          selectedRole: role,
          path: mockPath,
          focusNode: mockPath[0],
        });
      } else {
        setPath(data.path || []);

        // Apply calibration results to mark skills as completed
        if (calibration && calibration.strong && calibration.strong.length > 0) {
          const completedIds = new Set<number>();

          // Mark nodes as completed if their name matches calibration strong skills
          data.path.forEach((node: DetailedPathNode) => {
            const nodeNameLower = node.name.toLowerCase();
            const isKnown = calibration.strong.some(skill =>
              nodeNameLower.includes(skill.toLowerCase()) ||
              skill.toLowerCase().includes(nodeNameLower)
            );

            if (isKnown) {
              completedIds.add(node.skillNodeId);
              console.log(`[CALIBRATION] Marking "${node.name}" as completed based on calibration`);
            }
          });

          setCompletedNodeIds(completedIds);
        }

        // Set focus node to first UNCOMPLETED node, not just first node
        const firstIncompleteNode = data.path.find((node: DetailedPathNode) =>
          !completedNodeIds.has(node.skillNodeId)
        );

        setFocusNode(firstIncompleteNode || data.focusNode || data.path[0] || null);
        setSessionId(data.session?.id || null);

        setZoomState({
          level: 'path',
          selectedDomain: zoomState.selectedDomain,
          selectedRole: role,
          path: data.path || [],
          focusNode: firstIncompleteNode || data.focusNode || data.path[0] || null,
        });
      }
    } catch (err) {
      console.error('Failed to generate path:', err);
      // On error, also use mock data
      const mockPath = generateMockPath(role);
      setPath(mockPath);
      setFocusNode(mockPath[0]);
      setZoomState({
        level: 'path',
        selectedDomain: zoomState.selectedDomain,
        selectedRole: role,
        path: mockPath,
        focusNode: mockPath[0],
      });
    } finally {
      setLoading(false);
    }
  };

  /**
   * ZOOM LEVEL 2 → 3: Select Node & Show Focus
   */
  const handleSelectNode = (node: DetailedPathNode) => {
    setFocusNode(node);
    setShowFocusPanel(true);
    setZoomState({
      ...zoomState,
      level: 'frontier',
      focusNode: node,
    });

    // EDLSG: When user clicks a node, they enter EXPLORE phase
    if (!edlsgPhases.has(node.skillNodeId)) {
      setEdlsgPhases(prev => new Map(prev).set(node.skillNodeId, 'explore'));
    }
  };

  /**
   * Start PROBE session for focus node
   */
  const handleStartProbe = async () => {
    if (!focusNode) {
      setError('No focus node selected');
      return;
    }

    // EDLSG: User is now in SCORE phase (taking assessment)
    setEdlsgPhases(prev => new Map(prev).set(focusNode.skillNodeId, 'score'));

    setLoading(true);
    setError(null);

    try {
      // Generate quiz directly using skill name (bypasses session creation)
      // This works even when skill nodes don't exist in the database
      const response = await fetch(`${API_URL}/api/quizzes/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId,
          skillName: focusNode.name,
          difficulty: 'Intermediate',
          numQuestions: 10
        })
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Failed to generate quiz');
      }

      const data = await response.json();
      setQuiz(data);
      setCurrentQuestionIndex(0);
      setAnswers(new Map());
      setShowFocusPanel(false);
      setShowQuiz(true);
    } catch (err) {
      console.error('Failed to start PROBE:', err);
      setError('Failed to start assessment');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Submit quiz answers
   */
  const handleSubmitQuiz = async () => {
    if (!quiz) return;

    setLoading(true);
    setError(null);

    try {
      // Convert answers Map to submission format
      // Backend expects answers as a Map<String, String> where key is questionId
      const answersMap: Record<string, string> = {};
      quiz.questions.forEach((q: any, index: number) => {
        answersMap[q.questionId?.toString() || index.toString()] = answers.get(index) || '';
      });

      const response = await fetch(
        `${API_URL}/api/quizzes/${quiz.quizId}/submit`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            answers: answersMap,
            timeTaken: 300 // 5 minutes default
          }),
        }
      );

      if (!response.ok) throw new Error('Failed to submit quiz');

      const data = await response.json();
      setResults(data);
      setShowQuiz(false);
      setShowResults(true);

      // Mark the skill as completed if passed
      if (data.passed && focusNode) {
        setCompletedNodeIds(prev => new Set(prev).add(focusNode.skillNodeId));
        setEdlsgPhases(prev => new Map(prev).set(focusNode.skillNodeId, 'grow'));
      }
    } catch (err) {
      console.error('Failed to submit quiz:', err);
      setError('Failed to submit answers');
    } finally {
      setLoading(false);
    }
  };

  /**
   * Zoom back handlers
   */
  const handleBackToDomains = () => {
    setZoomState({ level: 'domain' });
    setRoles([]);
  };

  const handleBackToRoles = () => {
    setZoomState({
      level: 'role',
      selectedDomain: zoomState.selectedDomain,
    });
    setPath([]);
    setFocusNode(null);
  };

  const handleBackToPath = () => {
    setShowFocusPanel(false);
    setZoomState({
      ...zoomState,
      level: 'path',
    });
  };

  const handleContinueLearning = () => {
    // Mark node as completed if score is high enough (80%+)
    if (results && results.score >= 0.8 && focusNode) {
      setCompletedNodeIds(prev => new Set([...prev, focusNode.skillNodeId]));
      // EDLSG: Node completed → GROW phase
      setEdlsgPhases(prev => new Map(prev).set(focusNode.skillNodeId, 'grow'));
    }
    setShowResults(false);
    setShowFocusPanel(true);
  };

  // Render current zoom level
  const renderZoomLevel = () => {
    switch (zoomState.level) {
      case 'domain':
        return <DomainView domains={domains} onSelectDomain={handleSelectDomain} calibration={calibration} />;

      case 'role':
        if (!zoomState.selectedDomain) return null;
        return (
          <RoleView
            domain={zoomState.selectedDomain}
            roles={roles}
            onSelectRole={handleSelectRole}
            onBack={handleBackToDomains}
          />
        );

      case 'path':
      case 'frontier':
        if (!zoomState.selectedRole || !path.length) return null;
        return (
          <PathView
            role={zoomState.selectedRole}
            path={path}
            focusNode={focusNode}
            completedNodeIds={completedNodeIds}
            edlsgPhases={edlsgPhases}
            onSelectNode={handleSelectNode}
            onBack={handleBackToRoles}
          />
        );

      default:
        return null;
    }
  };

  return (
    <div className="relative w-full h-screen overflow-hidden bg-slate-950">
      {/* Calibration-based path banner (auto-dismisses after 5 seconds) */}
      <AnimatePresence>
        {calibration && zoomState.level === 'path' && showBanner && (
          <motion.div
            className="fixed top-4 left-1/2 transform -translate-x-1/2 z-[100] bg-gradient-to-r from-purple-900/80 to-pink-900/80 backdrop-blur-sm border border-purple-500/50 text-white px-6 py-3 rounded-lg shadow-lg max-w-2xl"
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <p className="text-sm text-center">
              This path was generated based on your calibration results. You can explore other domains anytime.
            </p>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Error banner */}
      <AnimatePresence>
        {error && (
          <motion.div
            className="fixed top-4 left-1/2 transform -translate-x-1/2 z-[100] bg-red-900 border border-red-700 text-white px-6 py-3 rounded-lg shadow-lg flex items-center gap-3"
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <span>{error}</span>
            <button onClick={() => setError(null)} className="hover:text-red-200">
              <X className="w-5 h-5" />
            </button>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Loading overlay */}
      <AnimatePresence>
        {loading && (
          <motion.div
            className="fixed inset-0 bg-black/50 backdrop-blur-sm z-[90] flex items-center justify-center"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="w-16 h-16 border-4 border-purple-500 border-t-transparent rounded-full"
              animate={{ rotate: 360 }}
              transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
            />
          </motion.div>
        )}
      </AnimatePresence>

      {/* Main zoom canvas */}
      <AnimatePresence mode="wait">
        <motion.div
          key={zoomState.level}
          className="absolute inset-0"
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 1.2 }}
          transition={{ duration: 0.6, type: 'spring' }}
        >
          {renderZoomLevel()}
        </motion.div>
      </AnimatePresence>

      {/* Focus Panel (Frontier Level 3) */}
      <AnimatePresence>
        {showFocusPanel && focusNode && (
          <motion.div
            className="fixed inset-0 bg-black/80 backdrop-blur-md z-50 flex items-center justify-center p-8"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={handleBackToPath}
          >
            <motion.div
              className="bg-gradient-to-br from-slate-900 to-slate-800 border border-purple-500 rounded-2xl p-8 max-w-3xl w-full max-h-[90vh] overflow-y-auto"
              initial={{ scale: 0.8, y: 50 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0.8, y: 50 }}
              onClick={(e) => e.stopPropagation()}
            >
              {/* Close button */}
              <button
                className="absolute top-4 right-4 text-slate-400 hover:text-white"
                onClick={handleBackToPath}
              >
                <X className="w-6 h-6" />
              </button>

              {/* Node details */}
              <div className="space-y-6">
                <div>
                  <div className="flex items-center gap-3 mb-2">
                    <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-pink-600 rounded-full flex items-center justify-center">
                      <BookOpen className="w-6 h-6 text-white" />
                    </div>
                    <div>
                      <h2 className="text-3xl font-bold text-white">{focusNode.name}</h2>
                      <p className="text-purple-400 text-sm">
                        Difficulty {focusNode.difficulty}/10 • {focusNode.estimatedHours} hours • {focusNode.assessmentType}
                      </p>
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-white mb-2">Why It Matters</h3>
                  <p className="text-slate-300">{focusNode.whyItMatters}</p>
                </div>

                {/* Dynamic Learning Resources with Personalization */}
                <NodeResources
                  nodeId={focusNode.skillNodeId}
                  userId={userId}
                  nodeName={focusNode.name}
                />

                <div>
                  <h3 className="text-lg font-semibold text-white mb-2">Proof Requirement</h3>
                  <p className="text-slate-300">{focusNode.proofRequirement}</p>
                </div>

                {/* Start PROBE button */}
                <button
                  className="w-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 text-white font-bold py-4 px-6 rounded-xl flex items-center justify-center gap-2 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                  onClick={handleStartProbe}
                  disabled={loading}
                  title="Start assessment"
                >
                  <PlayCircle className="w-5 h-5 flex-shrink-0" />
                  <span className="whitespace-nowrap">Start {focusNode.assessmentType.toUpperCase()} Assessment</span>
                </button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Quiz Overlay */}
      <AnimatePresence>
        {showQuiz && quiz && (
          <motion.div
            className="fixed inset-0 bg-black/90 backdrop-blur-md z-60 flex items-center justify-center p-8"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="bg-gradient-to-br from-slate-900 to-slate-800 border border-indigo-500 rounded-2xl p-8 max-w-3xl w-full"
              initial={{ scale: 0.8, y: 50 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0.8, y: 50 }}
            >
              {/* Progress */}
              <div className="mb-6">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-slate-400 text-sm">
                    Question {currentQuestionIndex + 1} of {quiz.questions.length}
                  </span>
                  <span className="text-slate-400 text-sm">
                    {Math.round(((currentQuestionIndex + 1) / quiz.questions.length) * 100)}%
                  </span>
                </div>
                <div className="w-full bg-slate-800 rounded-full h-2">
                  <div
                    className="bg-gradient-to-r from-indigo-500 to-purple-500 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${((currentQuestionIndex + 1) / quiz.questions.length) * 100}%` }}
                  />
                </div>
              </div>

              {/* Question */}
              {(() => {
                const question = quiz.questions[currentQuestionIndex];
                console.log('[QUIZ DEBUG] Current question:', JSON.stringify(question, null, 2));
                console.log('[QUIZ DEBUG] All questions:', quiz.questions);
                if (!question) return null;

                // Build options array from individual fields
                const options: { letter: string; text: string }[] = [];
                if (question.optionA) options.push({ letter: 'A', text: question.optionA });
                if (question.optionB) options.push({ letter: 'B', text: question.optionB });
                if (question.optionC) options.push({ letter: 'C', text: question.optionC });
                if (question.optionD) options.push({ letter: 'D', text: question.optionD });

                console.log('[QUIZ DEBUG] questionType:', question.questionType, 'options:', options);

                return (
                  <div className="space-y-6">
                    <h3 className="text-2xl font-bold text-white">{question.questionText || 'No question text'}</h3>

                    {/* MCQ Options - show regardless of questionType for debugging */}
                    {options.length > 0 ? (
                      <div className="space-y-3">
                        {options.map((option) => (
                          <button
                            key={option.letter}
                            className={`w-full text-left p-4 rounded-lg border-2 transition-all
                              ${answers.get(currentQuestionIndex) === option.letter
                                ? 'border-indigo-500 bg-indigo-500/20'
                                : 'border-slate-700 hover:border-slate-600 bg-slate-800'
                              }`}
                            onClick={() => {
                              const newAnswers = new Map(answers);
                              newAnswers.set(currentQuestionIndex, option.letter);
                              setAnswers(newAnswers);
                            }}
                          >
                            <span className="text-slate-400 font-bold mr-3">{option.letter}.</span>
                            <span className="text-white">{option.text}</span>
                          </button>
                        ))}
                      </div>
                    ) : (
                      <div className="p-4 bg-red-500/20 border border-red-500 rounded-lg">
                        <p className="text-red-400">No options available. Question type: {question.questionType || 'undefined'}</p>
                        <pre className="text-xs text-slate-400 mt-2 overflow-auto">{JSON.stringify(question, null, 2)}</pre>
                      </div>
                    )}

                    {/* Navigation */}
                    <div className="flex gap-4">
                      {currentQuestionIndex < quiz.questions.length - 1 ? (
                        <button
                          className="flex-1 bg-indigo-600 hover:bg-indigo-500 text-white font-bold py-3 px-6 rounded-lg transition-all disabled:opacity-50"
                          onClick={() => setCurrentQuestionIndex(currentQuestionIndex + 1)}
                          disabled={!answers.has(currentQuestionIndex)}
                        >
                          Next Question
                        </button>
                      ) : (
                        <button
                          className="flex-1 bg-gradient-to-r from-green-600 to-emerald-600 hover:from-green-500 hover:to-emerald-500 text-white font-bold py-3 px-6 rounded-lg transition-all disabled:opacity-50"
                          onClick={handleSubmitQuiz}
                          disabled={answers.size !== quiz.questions.length}
                        >
                          Submit Quiz
                        </button>
                      )}
                    </div>
                  </div>
                );
              })()}
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Results Overlay */}
      <AnimatePresence>
        {showResults && results && (
          <motion.div
            className="fixed inset-0 bg-black/90 backdrop-blur-md z-60 flex items-center justify-center p-8 overflow-y-auto"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="bg-gradient-to-br from-slate-900 to-slate-800 border border-green-500 rounded-2xl p-8 max-w-3xl w-full my-8"
              initial={{ scale: 0.8, y: 50 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0.8, y: 50 }}
            >
              {/* Score */}
              <div className="text-center mb-8">
                <motion.div
                  className="inline-block text-6xl font-bold bg-gradient-to-r from-green-400 to-emerald-400 bg-clip-text text-transparent mb-4"
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ type: 'spring', delay: 0.2 }}
                >
                  {Math.round(results.score * 100)}%
                </motion.div>
                <p className="text-2xl text-white font-semibold mb-2">
                  {results.score >= 0.8 ? 'Excellent!' : results.score >= 0.6 ? 'Good Job!' : 'Keep Learning!'}
                </p>
                <p className="text-slate-400">
                  {results.correct} out of {results.total} correct
                </p>
                {results.confidenceChange && (
                  <p className="text-purple-400 mt-2">
                    Confidence: {Math.round(results.confidenceChange.before * 100)}% → {Math.round(results.confidenceChange.after * 100)}%
                  </p>
                )}
              </div>

              {/* Answer Review */}
              <div className="space-y-4 mb-8">
                {results.gradedAnswers && results.gradedAnswers.map((answer: any, index: number) => (
                  <div
                    key={index}
                    className={`p-4 rounded-lg border ${
                      answer.isCorrect ? 'border-green-700 bg-green-900/20' : 'border-red-700 bg-red-900/20'
                    }`}
                  >
                    <div className="flex items-start gap-3 mb-2">
                      <div className={`w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 ${
                        answer.isCorrect ? 'bg-green-600' : 'bg-red-600'
                      }`}>
                        <span className="text-white text-sm font-bold">{index + 1}</span>
                      </div>
                      <p className="text-white flex-1">{answer.question}</p>
                    </div>
                    {!answer.isCorrect && (
                      <div className="ml-9 space-y-2">
                        <p className="text-red-400 text-sm">
                          Your answer: {answer.userAnswer}
                        </p>
                        <p className="text-green-400 text-sm">
                          Correct answer: {answer.correctAnswer}
                        </p>
                        <p className="text-slate-300 text-sm mt-2">{answer.explanation}</p>
                      </div>
                    )}
                  </div>
                ))}
              </div>

              {/* Continue button */}
              <button
                className="w-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 text-white font-bold py-4 px-6 rounded-xl transition-all"
                onClick={handleContinueLearning}
              >
                Continue Learning
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Help/Tutorial Button */}
      <motion.button
        onClick={() => setShowTutorial(true)}
        className="fixed bottom-6 right-6 z-40 w-14 h-14 bg-purple-600 hover:bg-purple-500 rounded-full shadow-lg shadow-purple-500/30 flex items-center justify-center transition-all hover:scale-110"
        whileHover={{ scale: 1.1 }}
        whileTap={{ scale: 0.95 }}
        title="How does this work?"
      >
        <HelpCircle className="w-6 h-6 text-white" />
      </motion.button>

      {/* Path Tutorial Overlay */}
      <PathTutorial
        isOpen={showTutorial}
        onClose={() => setShowTutorial(false)}
        onComplete={() => {
          localStorage.setItem('pathTutorialComplete', 'true');
        }}
      />
    </div>
  );
}
