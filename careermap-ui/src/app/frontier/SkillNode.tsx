import { memo } from 'react';
import { Handle, Position } from 'reactflow';

interface SkillNodeProps {
  data: {
    skillName: string;
    status: 'UNSEEN' | 'INFERRED' | 'ACTIVE' | 'PROVED' | 'STALE';
    confidence: number;
    demandWeight: number;
    unlockPotential?: number;
    isRecommended?: boolean;
    isProminent?: boolean;
  };
}

function SkillNode({ data }: SkillNodeProps) {
  const { skillName, status, confidence, demandWeight, unlockPotential = 0, isRecommended, isProminent } = data;

  // Glow intensity based on unlock potential
  const glowIntensity = unlockPotential * 40;
  const glowOpacity = Math.min(unlockPotential, 0.8);

  // Style based on status with enhanced glow for recommended
  const getNodeStyle = () => {
    const base = 'px-4 py-3 rounded-lg border-2 transition-all min-w-[160px] relative';

    // Background based on confidence (fill)
    const confidenceFill = confidence > 0
      ? `linear-gradient(to right, rgba(59, 130, 246, ${confidence * 0.3}) ${confidence * 100}%, transparent ${confidence * 100}%)`
      : 'transparent';

    if (isRecommended) {
      return {
        className: `${base} bg-blue-900/60 border-blue-400 animate-pulse-slow`,
        style: {
          boxShadow: `0 0 ${glowIntensity}px rgba(59, 130, 246, ${glowOpacity}), 0 0 ${glowIntensity * 1.5}px rgba(59, 130, 246, ${glowOpacity * 0.6})`,
          background: confidenceFill,
        },
      };
    }

    if (isProminent) {
      switch (status) {
        case 'PROVED':
          return {
            className: `${base} bg-green-900/40 border-green-500`,
            style: {
              boxShadow: `0 0 ${glowIntensity * 0.5}px rgba(16, 185, 129, ${glowOpacity * 0.4})`,
              background: confidenceFill,
            },
          };
        case 'ACTIVE':
          return {
            className: `${base} bg-blue-900/40 border-blue-400`,
            style: {
              boxShadow: `0 0 ${glowIntensity * 0.5}px rgba(59, 130, 246, ${glowOpacity * 0.4})`,
              background: confidenceFill,
            },
          };
        case 'INFERRED':
          return {
            className: `${base} bg-yellow-900/30 border-yellow-500 border-dashed`,
            style: {
              boxShadow: `0 0 ${glowIntensity * 0.3}px rgba(234, 179, 8, ${glowOpacity * 0.3})`,
              background: confidenceFill,
            },
          };
        case 'STALE':
          return {
            className: `${base} bg-red-900/30 border-red-500 border-dashed`,
            style: { background: confidenceFill },
          };
        case 'UNSEEN':
        default:
          return {
            className: `${base} bg-gray-900/50 border-gray-600`,
            style: { background: confidenceFill },
          };
      }
    }

    // Non-prominent nodes (faded)
    return {
      className: `${base} bg-gray-900/20 border-gray-700`,
      style: {},
    };
  };

  const getIcon = () => {
    switch (status) {
      case 'PROVED':
        return '✓';
      case 'ACTIVE':
        return '⚡';
      case 'INFERRED':
        return '•';
      case 'STALE':
        return '⚠';
      case 'UNSEEN':
      default:
        return '🔒';
    }
  };

  // Size based on demand weight - more dramatic for recommended
  const baseScale = isRecommended ? 1.2 : 0.9;
  const scale = baseScale + (demandWeight * 0.3);

  const nodeStyle = getNodeStyle();

  return (
    <div
      className="relative"
      style={{ transform: `scale(${scale})`, transformOrigin: 'center' }}
    >
      <Handle type="target" position={Position.Left} className="w-2 h-2" />

      <div className={nodeStyle.className} style={nodeStyle.style}>
        {isRecommended && (
          <>
            <div className="absolute -top-1 -right-1 w-3 h-3 bg-blue-500 rounded-full animate-ping" />
            <div className="absolute -top-1 -right-1 w-3 h-3 bg-blue-500 rounded-full" />
          </>
        )}

        <div className="flex items-center gap-2 mb-1">
          <span className="text-lg">{getIcon()}</span>
          <h4 className="font-semibold text-white text-sm">{skillName}</h4>
        </div>

        <div className="flex justify-between items-center text-xs">
          <span className="text-gray-400">{status}</span>
          {confidence > 0 && (
            <span className="text-gray-300 font-medium">
              {Math.round(confidence * 100)}%
            </span>
          )}
        </div>

        {/* Unlock potential indicator */}
        {isRecommended && unlockPotential > 0.5 && (
          <div className="mt-2 text-[10px] text-blue-300 font-semibold">
            ⚡ Unlocks {Math.round(unlockPotential * 10)} skills
          </div>
        )}
      </div>

      <Handle type="source" position={Position.Right} className="w-2 h-2" />
    </div>
  );
}

export default memo(SkillNode);
