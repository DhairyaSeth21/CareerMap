export interface User {
  id: number;
  name: string;
  email: string;
  password?: string;
}

export interface Proficiency {
  id: number;
  userId: number;
  skill: string;
  proficiency: number;
}

export interface CareerGoal {
  id: number;
  userId: number;
  goal: string;
  readiness: number;
  skill?: string;
  weight?: number;
  isPrimary?: boolean;
}

export interface AnalyticsSnapshot {
  id: number;
  userId: number;
  readiness: number;
  timestamp: string;
}

export interface ReadinessTrend {
  id: number;
  userId: number;
  domain: string;
  score: number;
  timestamp: string;
}

// Quiz Types
export interface SkillCatalogItem {
  skillId: number;
  name: string;
  category: string;
  description?: string;
  difficultyLevel: string;
}

export interface QuizQuestion {
  questionId: number;
  questionNumber: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
}

export interface Quiz {
  quizId: number;
  skillName: string;
  difficulty: string;
  numQuestions: number;
  questions: QuizQuestion[];
}

export interface QuizResult {
  quizId: number;
  score: number;
  correctCount: number;
  totalQuestions: number;
  proficiencyAwarded: number;
  breakdown: {
    subtopic: string;
    correct: number;
    total: number;
    percentage: number;
  }[];
  timeTaken: number;
}

export interface QuizHistory {
  quizId: number;
  skillName: string;
  difficulty: string;
  score: number;
  proficiencyAwarded: number;
  status: string;
  createdAt: string;
  completedAt: string;
  timeTaken: number;
}
