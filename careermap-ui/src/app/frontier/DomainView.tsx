import { motion } from 'framer-motion';
import { Domain } from './types';
import { Sparkles, TrendingUp, AlertTriangle, HelpCircle } from 'lucide-react';

interface CalibrationState {
  domainScores: Record<string, number>;
  strong: string[];
  gaps: string[];
  unknown: string[];
}

interface DomainViewProps {
  domains: Domain[];
  onSelectDomain: (domain: Domain) => void;
  calibration?: CalibrationState | null;
}

/**
 * DomainView - Level 0 Zoom: Star Map of Career Domains
 * Displays domains as stars in a constellation
 * NOW STATEFUL: Visual appearance changes based on calibration results
 */
export default function DomainView({ domains, onSelectDomain, calibration }: DomainViewProps) {
  // Helper to get calibration category for a domain
  const getDomainCategory = (domainName: string): 'strong' | 'gap' | 'unknown' | null => {
    if (!calibration) return null;

    // Check if domain matches any in calibration data
    const matchesStrong = calibration.strong.some(s =>
      s.toLowerCase().includes(domainName.toLowerCase()) ||
      domainName.toLowerCase().includes(s.toLowerCase())
    );
    if (matchesStrong) return 'strong';

    const matchesGap = calibration.gaps.some(g =>
      g.toLowerCase().includes(domainName.toLowerCase()) ||
      domainName.toLowerCase().includes(g.toLowerCase())
    );
    if (matchesGap) return 'gap';

    const matchesUnknown = calibration.unknown.some(u =>
      u.toLowerCase().includes(domainName.toLowerCase()) ||
      domainName.toLowerCase().includes(u.toLowerCase())
    );
    if (matchesUnknown) return 'unknown';

    return null;
  };

  // Helper to get visual styles based on category
  const getVisualStyles = (category: 'strong' | 'gap' | 'unknown' | null) => {
    switch (category) {
      case 'strong':
        return {
          size: 'scale-110', // Larger
          glow: 'from-green-500 to-emerald-500',
          border: 'border-green-500/50',
          badge: { icon: TrendingUp, color: 'text-green-400', bg: 'bg-green-900/30', label: 'Strong foundation' },
        };
      case 'gap':
        return {
          size: 'scale-100', // Medium
          glow: 'from-yellow-500 to-orange-500',
          border: 'border-yellow-500/50',
          badge: { icon: AlertTriangle, color: 'text-yellow-400', bg: 'bg-yellow-900/30', label: 'High upside' },
        };
      case 'unknown':
        return {
          size: 'scale-90', // Smaller
          glow: 'from-slate-600 to-slate-700',
          border: 'border-slate-700',
          badge: { icon: HelpCircle, color: 'text-slate-500', bg: 'bg-slate-900/50', label: 'Uncharted' },
        };
      default:
        return {
          size: 'scale-100',
          glow: 'from-indigo-500 to-purple-500',
          border: 'border-slate-700',
          badge: null,
        };
    }
  };
  return (
    <div className="relative w-full h-screen flex items-center justify-center overflow-hidden">
      {/* Background stars */}
      <div className="absolute inset-0 bg-gradient-to-b from-slate-950 via-indigo-950 to-slate-950">
        {[...Array(50)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-1 h-1 bg-white rounded-full"
            style={{
              top: `${Math.random() * 100}%`,
              left: `${Math.random() * 100}%`,
              opacity: Math.random() * 0.5 + 0.2,
            }}
            animate={{
              opacity: [0.2, 0.8, 0.2],
            }}
            transition={{
              duration: 2 + Math.random() * 3,
              repeat: Infinity,
              delay: Math.random() * 2,
            }}
          />
        ))}
      </div>

      {/* Title */}
      <motion.div
        className="absolute top-20 left-1/2 transform -translate-x-1/2 text-center"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
      >
        <h1 className="text-5xl font-bold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">
          Choose Your Domain
        </h1>
        <p className="text-slate-400 mt-4">Select a star to begin your journey</p>
      </motion.div>

      {/* Domain constellation */}
      <div className="relative w-full max-w-6xl mx-auto grid grid-cols-3 gap-12 p-12">
        {domains.map((domain, index) => {
          const category = getDomainCategory(domain.name);
          const styles = getVisualStyles(category);

          return (
            <motion.button
              key={domain.domainId}
              className={`relative group ${styles.size}`}
              initial={{ opacity: 0, scale: 0 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{
                duration: 0.5,
                delay: index * 0.1,
                type: 'spring',
              }}
              whileHover={{ scale: 1.1 }}
              onClick={() => onSelectDomain(domain)}
            >
              {/* Glow effect - personalized by calibration */}
              <div className={`absolute inset-0 bg-gradient-to-r ${styles.glow} rounded-2xl blur-xl opacity-0 group-hover:opacity-60 transition-opacity duration-300`} />

              {/* Star card */}
              <div className={`relative bg-gradient-to-br from-slate-800 to-slate-900 border ${styles.border} rounded-2xl p-8 hover:border-indigo-500 transition-all duration-300`}>
                {/* Calibration Badge (if applicable) */}
                {styles.badge && (() => {
                  const Icon = styles.badge.icon;
                  return (
                    <div className={`absolute -top-3 left-1/2 -translate-x-1/2 ${styles.badge.bg} ${styles.badge.color} px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1 border ${styles.border}`}>
                      <Icon className="w-3 h-3" />
                      {styles.badge.label}
                    </div>
                  );
                })()}

                {/* Icon */}
                <div className="flex justify-center mb-4 mt-2">
                  <div className={`w-20 h-20 bg-gradient-to-br ${styles.glow} rounded-full flex items-center justify-center`}>
                    <Sparkles className="w-10 h-10 text-white" />
                  </div>
                </div>

                {/* Name */}
                <h3 className="text-2xl font-bold text-white mb-2 text-center">
                  {domain.name}
                </h3>

                {/* Description */}
                <p className="text-slate-400 text-sm text-center line-clamp-2">
                  {domain.description}
                </p>

                {/* Pulsing indicator */}
                <motion.div
                  className={`absolute -top-2 -right-2 w-4 h-4 bg-gradient-to-r ${styles.glow} rounded-full`}
                  animate={{
                    scale: [1, 1.2, 1],
                    opacity: [0.8, 1, 0.8],
                  }}
                  transition={{
                    duration: 2,
                    repeat: Infinity,
                  }}
                />
              </div>
            </motion.button>
          );
        })}
      </div>
    </div>
  );
}
