'use client';

import { useState } from 'react';
import { motion } from 'framer-motion';
import { Upload, CheckCircle, XCircle, ArrowLeft, Loader2 } from 'lucide-react';
import { useRouter, useSearchParams } from 'next/navigation';
import { API_URL } from '../../../config/api';

interface ExtractedSkill {
  name: string;
  experience: string;
  proficiency: string;
  mappedNodes?: number[];
}

interface AnalysisResult {
  userId: number;
  skills: ExtractedSkill[];
  experience: Record<string, number>;
  matchedNodes: {
    roles: Record<number, number[]>;
  };
  timestamp: string;
}

export default function EvidencePage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const from = searchParams?.get('from');

  const [uploading, setUploading] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [applying, setApplying] = useState(false);
  const [analysis, setAnalysis] = useState<AnalysisResult | null>(null);
  const [selectedSkills, setSelectedSkills] = useState<Set<string>>(new Set());
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    setAnalyzing(true);
    setError(null);

    try {
      // Get userId from localStorage
      const userId = parseInt(localStorage.getItem('userId') || '1');

      // Use FormData to send actual file (not text) - backend will handle all formats
      const formData = new FormData();
      formData.append('userId', userId.toString());
      formData.append('file', file); // Send the actual file, not text

      const response = await fetch(`${API_URL}/api/v1/resume/analyze`, {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Resume analysis failed');
      }

      const result: AnalysisResult = await response.json();
      setAnalysis(result);

      // Pre-select all intermediate/advanced skills
      const preSelected = new Set<string>();
      result.skills.forEach((skill) => {
        if (skill.proficiency === 'intermediate' || skill.proficiency === 'advanced') {
          preSelected.add(skill.name);
        }
      });
      setSelectedSkills(preSelected);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Upload failed');
    } finally {
      setUploading(false);
      setAnalyzing(false);
    }
  };

  const handleApply = async () => {
    if (!analysis) return;

    setApplying(true);
    setError(null);

    try {
      const userId = parseInt(localStorage.getItem('userId') || '1');

      const response = await fetch(`${API_URL}/api/v1/resume/mark-skills`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId,
          matchedNodes: analysis.matchedNodes.roles,
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to mark skills');
      }

      const result = await response.json();
      setSuccess(true);

      // Update calibration heatmap with resume analysis data
      const calibrationData = localStorage.getItem('calibration');
      if (calibrationData) {
        try {
          const calibration = JSON.parse(calibrationData);

          // Update domain scores based on resume skills
          const domainMapping: Record<string, string> = {
            'Backend': 'Backend Engineering',
            'Frontend': 'Frontend Engineering',
            'Database': 'Backend Engineering',
            'DevOps': 'Cloud & DevOps',
            'Security': 'Backend Engineering',
          };

          const groupedSkills = groupSkillsByDomain();
          Object.entries(groupedSkills).forEach(([domain, skills]) => {
            const mappedDomain = domainMapping[domain] || domain;
            if (mappedDomain && calibration.domainScores) {
              // Boost domain score based on number of skills (cap at 0.9)
              const boost = Math.min(0.3, skills.length * 0.05);
              calibration.domainScores[mappedDomain] = Math.min(0.9,
                (calibration.domainScores[mappedDomain] || 0.3) + boost
              );
            }
          });

          // Update strong/gaps/unknown lists
          const updatedStrong = Object.entries(calibration.domainScores)
            .filter(([_, score]) => Number(score) >= 0.7)
            .map(([domain]) => domain);

          const updatedGaps = Object.entries(calibration.domainScores)
            .filter(([_, score]) => Number(score) >= 0.3 && Number(score) < 0.7)
            .map(([domain]) => domain);

          calibration.strong = updatedStrong;
          calibration.gaps = updatedGaps;
          calibration.resumeAnalyzed = true;
          calibration.resumeTimestamp = Date.now();

          localStorage.setItem('calibration', JSON.stringify(calibration));
        } catch (e) {
          console.error('Failed to update calibration data:', e);
        }
      }

      // Check if coming from calibration
      const sourceParam = searchParams?.get('source');

      // Redirect to Frontier after 2 seconds
      setTimeout(() => {
        if (sourceParam === 'calibration') {
          router.push('/frontier?mode=guided&resume=analyzed');
        } else {
          router.push('/frontier?mode=guided');
        }
      }, 2000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Apply failed');
    } finally {
      setApplying(false);
    }
  };

  const groupSkillsByDomain = () => {
    if (!analysis) return {};

    const grouped: Record<string, ExtractedSkill[]> = {
      Backend: [],
      Frontend: [],
      Database: [],
      DevOps: [],
      Security: [],
      Other: [],
    };

    analysis.skills.forEach((skill) => {
      const name = skill.name.toLowerCase();
      if (name.includes('node') || name.includes('express') || name.includes('api') || name.includes('rest')) {
        grouped.Backend.push(skill);
      } else if (name.includes('react') || name.includes('vue') || name.includes('angular') || name.includes('html') || name.includes('css')) {
        grouped.Frontend.push(skill);
      } else if (name.includes('sql') || name.includes('mongo') || name.includes('postgres') || name.includes('database')) {
        grouped.Database.push(skill);
      } else if (name.includes('docker') || name.includes('kubernetes') || name.includes('aws') || name.includes('ci/cd')) {
        grouped.DevOps.push(skill);
      } else if (name.includes('security') || name.includes('auth') || name.includes('jwt') || name.includes('oauth')) {
        grouped.Security.push(skill);
      } else {
        grouped.Other.push(skill);
      }
    });

    // Filter out empty groups
    return Object.fromEntries(
      Object.entries(grouped).filter(([, skills]) => skills.length > 0)
    );
  };

  const toggleSkill = (skillName: string) => {
    const newSet = new Set(selectedSkills);
    if (newSet.has(skillName)) {
      newSet.delete(skillName);
    } else {
      newSet.add(skillName);
    }
    setSelectedSkills(newSet);
  };

  const getTotalMatchedNodes = () => {
    if (!analysis) return 0;
    return Object.values(analysis.matchedNodes.roles).reduce(
      (sum, nodes) => sum + nodes.length,
      0
    );
  };

  return (
    <div className="min-h-screen bg-black text-white p-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <button
            onClick={() => router.back()}
            className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors mb-4"
          >
            <ArrowLeft className="w-5 h-5" />
            Back
          </button>
          <h1 className="text-4xl font-bold mb-2">Resume & Evidence</h1>
          <p className="text-slate-400">
            Import skills you've already proven so your Frontier starts closer to your real level.
          </p>
        </div>

        {/* Upload Card */}
        {!analysis && !success && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="p-8 bg-slate-900/30 border border-slate-800 rounded-2xl text-center"
          >
            <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-blue-500/20 flex items-center justify-center">
              <Upload className="w-8 h-8 text-blue-400" />
            </div>
            <h2 className="text-2xl font-bold mb-2">Upload Resume</h2>
            <p className="text-slate-400 mb-6">
              Takes ~30 seconds. We'll ask you to confirm before applying.
            </p>

            <label className="inline-block">
              <input
                type="file"
                accept=".txt"
                onChange={handleFileUpload}
                disabled={uploading}
                className="hidden"
              />
              <div className="px-8 py-4 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-700 disabled:cursor-not-allowed text-white font-semibold rounded-xl cursor-pointer transition-colors inline-flex items-center gap-2">
                {uploading ? (
                  <>
                    <Loader2 className="w-5 h-5 animate-spin" />
                    {analyzing ? 'Analyzing...' : 'Uploading...'}
                  </>
                ) : (
                  <>
                    <Upload className="w-5 h-5" />
                    Choose File (TXT only)
                  </>
                )}
              </div>
            </label>

            {error && (
              <div className="mt-4 p-4 bg-red-500/20 border border-red-500/50 rounded-lg flex items-center gap-2 text-red-300">
                <XCircle className="w-5 h-5 flex-shrink-0" />
                <span>{error}</span>
              </div>
            )}
          </motion.div>
        )}

        {/* Analysis Results */}
        {analysis && !success && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-6"
          >
            {/* Summary */}
            <div className="p-6 bg-slate-900/30 border border-slate-800 rounded-2xl">
              <h2 className="text-2xl font-bold mb-4">Extracted Skills</h2>
              <div className="grid grid-cols-3 gap-4">
                <div className="p-4 bg-blue-900/20 border border-blue-800/30 rounded-lg text-center">
                  <div className="text-3xl font-bold text-blue-400">{analysis.skills.length}</div>
                  <div className="text-sm text-slate-400">Skills Found</div>
                </div>
                <div className="p-4 bg-green-900/20 border border-green-800/30 rounded-lg text-center">
                  <div className="text-3xl font-bold text-green-400">{getTotalMatchedNodes()}</div>
                  <div className="text-sm text-slate-400">Nodes Matched</div>
                </div>
                <div className="p-4 bg-purple-900/20 border border-purple-800/30 rounded-lg text-center">
                  <div className="text-3xl font-bold text-purple-400">{selectedSkills.size}</div>
                  <div className="text-sm text-slate-400">Selected</div>
                </div>
              </div>
            </div>

            {/* Skill Confirmation by Domain */}
            {Object.entries(groupSkillsByDomain()).map(([domain, skills]) => (
              <div key={domain} className="p-6 bg-slate-900/30 border border-slate-800 rounded-2xl">
                <h3 className="text-xl font-bold mb-4">{domain}</h3>
                <div className="space-y-2">
                  {skills.map((skill) => (
                    <button
                      key={skill.name}
                      onClick={() => toggleSkill(skill.name)}
                      className="w-full p-4 bg-slate-800/50 hover:bg-slate-800 border border-slate-700 rounded-lg transition-colors flex items-center gap-4"
                    >
                      <div className={`w-5 h-5 rounded border-2 flex items-center justify-center flex-shrink-0 ${
                        selectedSkills.has(skill.name)
                          ? 'bg-blue-600 border-blue-600'
                          : 'border-slate-600'
                      }`}>
                        {selectedSkills.has(skill.name) && (
                          <CheckCircle className="w-4 h-4 text-white" />
                        )}
                      </div>
                      <div className="flex-1 text-left">
                        <div className="font-semibold text-white">{skill.name}</div>
                        <div className="text-sm text-slate-400">
                          {skill.experience} • {skill.proficiency}
                        </div>
                      </div>
                      <div className={`px-2 py-1 rounded text-xs font-semibold ${
                        skill.proficiency === 'advanced' ? 'bg-green-500/20 text-green-300' :
                        skill.proficiency === 'intermediate' ? 'bg-blue-500/20 text-blue-300' :
                        'bg-slate-500/20 text-slate-300'
                      }`}>
                        {skill.proficiency}
                      </div>
                    </button>
                  ))}
                </div>
              </div>
            ))}

            {/* Apply Button */}
            <div className="flex items-center gap-4">
              <button
                onClick={handleApply}
                disabled={applying || selectedSkills.size === 0}
                className="flex-1 px-8 py-4 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-700 disabled:cursor-not-allowed text-white font-bold text-lg rounded-xl transition-colors flex items-center justify-center gap-2"
              >
                {applying ? (
                  <>
                    <Loader2 className="w-5 h-5 animate-spin" />
                    Applying...
                  </>
                ) : (
                  <>Apply to Frontier ({selectedSkills.size} skills)</>
                )}
              </button>
              <button
                onClick={() => {
                  setAnalysis(null);
                  setSelectedSkills(new Set());
                }}
                className="px-6 py-4 border-2 border-slate-700 hover:border-slate-600 text-slate-300 font-semibold rounded-xl transition-colors"
              >
                Cancel
              </button>
            </div>

            {error && (
              <div className="p-4 bg-red-500/20 border border-red-500/50 rounded-lg flex items-center gap-2 text-red-300">
                <XCircle className="w-5 h-5 flex-shrink-0" />
                <span>{error}</span>
              </div>
            )}
          </motion.div>
        )}

        {/* Success State */}
        {success && (
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="p-12 bg-green-900/20 border border-green-800/50 rounded-2xl text-center"
          >
            <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-green-500/20 flex items-center justify-center">
              <CheckCircle className="w-12 h-12 text-green-400" />
            </div>
            <h2 className="text-3xl font-bold text-green-400 mb-2">Success!</h2>
            <p className="text-lg text-slate-300 mb-4">
              {getTotalMatchedNodes()} nodes marked as PROVED
            </p>
            <p className="text-sm text-slate-400">
              Redirecting to Frontier...
            </p>
          </motion.div>
        )}
      </div>
    </div>
  );
}
