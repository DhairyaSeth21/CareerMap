import { motion } from 'framer-motion';
import { DesignSystem } from '../design-system';

interface StarfieldProps {
  density?: 'low' | 'medium' | 'high';
  className?: string;
}

/**
 * Starfield Background
 *
 * UNIVERSAL component used on:
 * - Landing page
 * - Auth modal
 * - Calibration
 * - Frontier
 * - All overlays
 *
 * This ensures IDENTICAL visual experience everywhere.
 */
export default function Starfield({ density = 'medium', className = '' }: StarfieldProps) {
  const starCount = {
    low: 30,
    medium: 50,
    high: 100,
  }[density];

  const stars = Array.from({ length: starCount }, (_, i) => ({
    id: i,
    size: Math.random() > 0.7 ? 2 : 1,
    top: `${Math.random() * 100}%`,
    left: `${Math.random() * 100}%`,
    opacity: Math.random() * 0.5 + 0.2,
    animationDelay: Math.random() * 3,
    animationDuration: 2 + Math.random() * 3,
  }));

  return (
    <div className={`absolute inset-0 overflow-hidden ${className}`}>
      {/* Main gradient background */}
      <div className={`absolute inset-0 bg-gradient-to-b ${DesignSystem.colors.background.primary}`} />

      {/* Animated stars */}
      <div className="absolute inset-0">
        {stars.map((star) => (
          <motion.div
            key={star.id}
            className="absolute bg-white rounded-full"
            style={{
              width: star.size,
              height: star.size,
              top: star.top,
              left: star.left,
            }}
            animate={{
              opacity: [star.opacity, star.opacity + 0.3, star.opacity],
            }}
            transition={{
              duration: star.animationDuration,
              repeat: Infinity,
              delay: star.animationDelay,
              ease: 'easeInOut',
            }}
          />
        ))}
      </div>

      {/* Subtle nebula glow (optional, adds depth) */}
      <div className="absolute inset-0 opacity-20">
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-purple-600 rounded-full blur-[120px]" />
        <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-indigo-600 rounded-full blur-[120px]" />
      </div>
    </div>
  );
}
