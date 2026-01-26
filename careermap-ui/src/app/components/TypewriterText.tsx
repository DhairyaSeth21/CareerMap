import { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

interface TypewriterTextProps {
  lines: string[];
  speed?: number;
  className?: string;
  onComplete?: () => void;
}

/**
 * TypewriterText Component
 *
 * Displays text lines one by one with typing effect.
 * Used on landing page for philosophical intro.
 */
export default function TypewriterText({
  lines,
  speed = 30,
  className = '',
  onComplete,
}: TypewriterTextProps) {
  const [currentLineIndex, setCurrentLineIndex] = useState(0);
  const [displayedText, setDisplayedText] = useState('');
  const [isComplete, setIsComplete] = useState(false);

  useEffect(() => {
    if (currentLineIndex >= lines.length) {
      setIsComplete(true);
      onComplete?.();
      return;
    }

    const currentLine = lines[currentLineIndex];
    let charIndex = 0;

    const intervalId = setInterval(() => {
      if (charIndex < currentLine.length) {
        setDisplayedText((prev) => prev + currentLine[charIndex]);
        charIndex++;
      } else {
        clearInterval(intervalId);
        // Wait a bit before starting next line
        setTimeout(() => {
          setDisplayedText((prev) => prev + '\n\n');
          setCurrentLineIndex((prev) => prev + 1);
        }, 800);
      }
    }, speed);

    return () => clearInterval(intervalId);
  }, [currentLineIndex, lines, speed, onComplete]);

  return (
    <div className={className}>
      <motion.pre
        className="whitespace-pre-wrap text-2xl font-light leading-relaxed text-slate-300"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        {displayedText}
        {!isComplete && (
          <motion.span
            className="inline-block w-2 h-6 bg-purple-400 ml-1"
            animate={{ opacity: [1, 0, 1] }}
            transition={{ duration: 0.8, repeat: Infinity }}
          />
        )}
      </motion.pre>
    </div>
  );
}
