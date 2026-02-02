/**
 * Type definitions for the Frontier zoom architecture
 */

export type ZoomLevel = 'domain' | 'role' | 'path' | 'frontier';

export interface Domain {
  domainId: number;
  name: string;
  description: string;
  icon: string;
}

export interface CareerRole {
  careerRoleId: number;
  name: string;
  description: string;
  icon: string;
  domainId: number;
}

export interface LearningResource {
  type: string;
  title: string;
  url: string;
  description: string;
  estimatedMinutes: number;
}

export type EDLSGPhase = 'explore' | 'decide' | 'learn' | 'score' | 'grow' | 'not_started';

export interface DetailedPathNode {
  skillNodeId: number;
  name: string;
  whyItMatters: string;
  learnResources: LearningResource[];
  assessmentType: string;
  proofRequirement: string;
  dependencies: number[];
  unlocks: number[];
  competencies?: number[]; // Related skills that branch out (not prerequisites)
  difficulty: number;
  estimatedHours: number;
  category: string;
  edlsgPhase?: EDLSGPhase; // Current EDLSG phase for this node
}

export interface FocusNode extends DetailedPathNode {
  confidence?: number;
  status?: string;
}

export interface Quiz {
  quizId: number;
  skillName: string;
  questions: QuizQuestion[];
}

export interface QuizQuestion {
  questionId: number;
  questionNumber: number;
  questionText: string;
  questionType: string; // 'MCQ', 'FRQ', 'CODING'
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
}

export interface ZoomState {
  level: ZoomLevel;
  selectedDomain?: Domain;
  selectedRole?: CareerRole;
  path?: DetailedPathNode[];
  focusNode?: FocusNode;
}
