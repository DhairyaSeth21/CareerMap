import { motion } from 'framer-motion';
import { useState, useRef, useEffect } from 'react';
import { DetailedPathNode, CareerRole } from './types';
import { ArrowLeft, Target, Maximize2, Lock, CheckCircle, Play, BookOpen, Eye, X, Loader2, Sparkles } from 'lucide-react';

interface PathViewProps {
  role: CareerRole;
  path: DetailedPathNode[];
  focusNode: DetailedPathNode | null;
  completedNodeIds: Set<number>;
  edlsgPhases: Map<number, 'explore' | 'decide' | 'learn' | 'score' | 'grow'>;
  onSelectNode: (node: DetailedPathNode) => void;
  onBack: () => void;
}

export default function PathView({ role, path, focusNode, completedNodeIds, edlsgPhases, onSelectNode, onBack }: PathViewProps) {
  const isNodeLocked = (node: DetailedPathNode): boolean => {
    if (!node.dependencies || node.dependencies.length === 0) return false;
    return !node.dependencies.every(depId => completedNodeIds.has(depId));
  };

  const containerRef = useRef<HTMLDivElement>(null);
  const [zoom, setZoom] = useState(1);
  const [pan, setPan] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({ x: 0, y: 0 });
  const [spotlightMode, setSpotlightMode] = useState(true);
  const [showDetailPanel, setShowDetailPanel] = useState(false);
  const [selectedNode, setSelectedNode] = useState<DetailedPathNode | null>(null);
  const [hoveredNode, setHoveredNode] = useState<number | null>(null);

  // AI Explanation state
  const [showAIExplanation, setShowAIExplanation] = useState(false);
  const [aiExplanation, setAiExplanation] = useState<string>('');
  const [loadingExplanation, setLoadingExplanation] = useState(false);
  const [explanationError, setExplanationError] = useState<string | null>(null);

  // Calculate node positions using LINEAR LAYOUT with COMPETENCIES branching out
  const nodePositions = useState(() => {
    const positions = new Map<number, { x: number; y: number }>();
    const HORIZONTAL_SPACING = 400;  // Space between main nodes

    // First, identify which nodes are competencies by checking if they're referenced
    const competencyIds = new Set<number>();
    path.forEach(node => {
      if (node.competencies) {
        node.competencies.forEach(compId => competencyIds.add(compId));
      }
    });

    console.log('[LAYOUT] Competency node IDs:', Array.from(competencyIds));

    // Position MAIN PATH nodes (not competencies) in a straight line
    let mainNodeIndex = 0;
    path.forEach(node => {
      if (!competencyIds.has(node.skillNodeId)) {
        // Main path node - position on horizontal line
        positions.set(node.skillNodeId, {
          x: mainNodeIndex * HORIZONTAL_SPACING,
          y: 0
        });
        mainNodeIndex++;
      }
    });

    // Position COMPETENCY nodes branching out from their parent nodes
    path.forEach(node => {
      if (node.competencies && node.competencies.length > 0) {
        const parentPos = positions.get(node.skillNodeId);
        if (!parentPos) return;

        node.competencies.forEach((compId, compIndex) => {
          const compNode = path.find(n => n.skillNodeId === compId);
          if (!compNode) return;

          // Position competency nodes ABOVE and BELOW the main line
          const branchY = compIndex % 2 === 0 ? -200 : 200; // Alternate above/below
          const branchX = parentPos.x + (50 * (compIndex + 1)); // Slight offset horizontally

          positions.set(compId, {
            x: branchX,
            y: branchY
          });
        });
      }
    });

    console.log('[LAYOUT] All positions:', Array.from(positions.entries()));
    return positions;
  })[0];

  // Helper: Determine if node is a competency (branch)
  const isCompetency = (nodeId: number): boolean => {
    return path.some(node => node.competencies?.includes(nodeId));
  };

  // Helper: Get semantic label for branch nodes
  const getBranchSemantics = (node: DetailedPathNode): { label: string; color: string; tooltip: string } | null => {
    if (!isCompetency(node.skillNodeId)) return null;

    // Analyze node characteristics to determine semantic type
    const name = node.name.toLowerCase();
    const category = node.category.toLowerCase();
    const difficulty = node.difficulty;

    // Advanced/specialized → red advanced
    if (difficulty >= 7 || category === 'specialized' || name.includes('advanced') || name.includes('deep')) {
      return {
        label: '🔴 Advanced',
        color: 'bg-red-500/20 border-red-500/50 text-red-300',
        tooltip: 'Advanced optional skill — for deeper expertise'
      };
    }

    // Specialized domain knowledge → yellow specialization
    if (name.includes('pattern') || name.includes('architecture') || name.includes('design') || category === 'specialized') {
      return {
        label: '🟡 Specialization',
        color: 'bg-yellow-500/20 border-yellow-500/50 text-yellow-300',
        tooltip: 'Specialization — builds unique expertise'
      };
    }

    // Hands-on practice → blue reinforcement
    if (node.assessmentType === 'build' || node.assessmentType === 'apply' || name.includes('practice') || name.includes('exercise')) {
      return {
        label: '🔵 Reinforcement',
        color: 'bg-blue-500/20 border-blue-500/50 text-blue-300',
        tooltip: 'Reinforcement — strengthens core skills'
      };
    }

    // Default: depth module (purple)
    return {
      label: '🟣 Depth Module',
      color: 'bg-purple-500/20 border-purple-500/50 text-purple-300',
      tooltip: 'Optional depth — strengthens long-term mastery'
    };
  };

  // Find frontier node (MUST BE DEFINED BEFORE fitToGraph)
  const frontierNode = path.find(node => !isNodeLocked(node) && !completedNodeIds.has(node.skillNodeId));

  // Spotlight mode: determine which nodes should be visible
  const getSpotlightNodes = () => {
    if (!spotlightMode || !frontierNode) return new Set(path.map(n => n.skillNodeId));

    const visible = new Set<number>();

    // Add current frontier node
    visible.add(frontierNode.skillNodeId);

    // Add its prerequisites
    if (frontierNode.dependencies) {
      frontierNode.dependencies.forEach(depId => visible.add(depId));
    }

    // Add its immediate unlocks (next 1-2 nodes on main path)
    if (frontierNode.unlocks) {
      frontierNode.unlocks.forEach(unlockId => {
        visible.add(unlockId);

        // STEP 3: Add next node's unlocks too (look-ahead of 2 nodes)
        const nextNode = path.find(n => n.skillNodeId === unlockId);
        if (nextNode?.unlocks) {
          nextNode.unlocks.forEach(nextUnlockId => visible.add(nextUnlockId));
        }
      });
    }

    // STEP 3: Branch disclosure - only show branches from visible main nodes
    // Add competencies attached to frontier node
    if (frontierNode.competencies) {
      frontierNode.competencies.forEach(compId => visible.add(compId));
    }

    // Add competencies attached to immediate next nodes
    if (frontierNode.unlocks) {
      frontierNode.unlocks.forEach(unlockId => {
        const nextNode = path.find(n => n.skillNodeId === unlockId);
        if (nextNode?.competencies) {
          nextNode.competencies.forEach(compId => visible.add(compId));
        }
      });
    }

    return visible;
  };

  // Fit graph to screen (spotlight mode: fit only visible nodes)
  const fitToGraph = () => {
    console.log('[FITGRAPH] Starting fitToGraph');
    console.log('[FITGRAPH] containerRef:', containerRef.current);
    console.log('[FITGRAPH] nodePositions size:', nodePositions.size);
    console.log('[FITGRAPH] All positions:', Array.from(nodePositions.entries()));

    if (!containerRef.current || nodePositions.size === 0) {
      console.log('[FITGRAPH] EARLY RETURN: no container or no positions');
      return;
    }

    // In spotlight mode, only fit visible nodes
    const visibleNodeIds = spotlightMode && frontierNode
      ? Array.from(getSpotlightNodes())
      : Array.from(nodePositions.keys());

    console.log('[FITGRAPH] spotlightMode:', spotlightMode);
    console.log('[FITGRAPH] frontierNode:', frontierNode);
    console.log('[FITGRAPH] visibleNodeIds:', visibleNodeIds);

    const positions = visibleNodeIds
      .map(id => nodePositions.get(id))
      .filter((pos): pos is { x: number; y: number } => pos !== undefined);

    console.log('[FITGRAPH] Filtered positions:', positions);

    if (positions.length === 0) {
      console.log('[FITGRAPH] EARLY RETURN: no positions after filtering');
      return;
    }

    const minX = Math.min(...positions.map(p => p.x));
    const maxX = Math.max(...positions.map(p => p.x));
    const minY = Math.min(...positions.map(p => p.y));
    const maxY = Math.max(...positions.map(p => p.y));

    const graphWidth = maxX - minX;
    const graphHeight = maxY - minY;
    const graphCenterX = (minX + maxX) / 2;
    const graphCenterY = (minY + maxY) / 2;

    const viewportWidth = containerRef.current.clientWidth;
    const viewportHeight = containerRef.current.clientHeight;

    const padding = 150; // Extra padding for command bar
    const scaleX = (viewportWidth - padding * 2) / (graphWidth || 1);
    const scaleY = (viewportHeight - padding * 2) / (graphHeight || 1);
    const newZoom = Math.min(scaleX, scaleY, 1.5);

    console.log('[FITGRAPH] Calculated values:');
    console.log('  graphWidth:', graphWidth, 'graphHeight:', graphHeight);
    console.log('  graphCenter:', graphCenterX, graphCenterY);
    console.log('  viewport:', viewportWidth, viewportHeight);
    console.log('  newZoom:', newZoom);
    console.log('  newPan:', {
      x: viewportWidth / 2 - graphCenterX * newZoom,
      y: viewportHeight / 2 - graphCenterY * newZoom,
    });

    setZoom(newZoom);
    setPan({
      x: viewportWidth / 2 - graphCenterX * newZoom,
      y: viewportHeight / 2 - graphCenterY * newZoom,
    });
  };

  // Find current frontier node
  const findCurrentNode = () => {
    const frontier = path.find(node => !isNodeLocked(node) && !completedNodeIds.has(node.skillNodeId));
    if (!frontier) return;

    const pos = nodePositions.get(frontier.skillNodeId);
    if (!pos || !containerRef.current) return;

    const viewportWidth = containerRef.current.clientWidth;
    const viewportHeight = containerRef.current.clientHeight;
    const newZoom = 1.2;

    setZoom(newZoom);
    setPan({
      x: viewportWidth / 2 - pos.x * newZoom,
      y: viewportHeight / 2 - pos.y * newZoom,
    });
  };

  // Fit on mount and when spotlight mode changes
  useEffect(() => {
    if (path.length > 0) {
      setTimeout(() => fitToGraph(), 100);
    }
  }, [path.length, spotlightMode]);

  const handleMouseDown = (e: React.MouseEvent) => {
    setIsDragging(true);
    setDragStart({ x: e.clientX - pan.x, y: e.clientY - pan.y });
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    if (!isDragging) return;
    setPan({
      x: e.clientX - dragStart.x,
      y: e.clientY - dragStart.y,
    });
  };

  const handleMouseUp = () => setIsDragging(false);

  const handleWheel = (e: React.WheelEvent) => {
    e.preventDefault();
    const delta = e.deltaY > 0 ? 0.9 : 1.1;
    setZoom((prev) => Math.max(0.3, Math.min(3, prev * delta)));
  };

  // Get node state for command bar
  const getNodeState = (node: DetailedPathNode | null | undefined) => {
    if (!node) return 'LOCKED';
    if (completedNodeIds.has(node.skillNodeId)) return 'COMPLETED';
    if (isNodeLocked(node)) return 'LOCKED';
    return 'AVAILABLE';
  };

  const spotlightNodes = getSpotlightNodes();

  return (
    <div
      ref={containerRef}
      className="relative w-full h-screen overflow-hidden bg-black"
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
      onMouseLeave={handleMouseUp}
      onWheel={handleWheel}
      style={{ cursor: isDragging ? 'grabbing' : 'grab' }}
    >
      {/* Header */}
      <div className="absolute top-0 left-0 right-0 z-50 bg-gradient-to-b from-black/80 to-transparent p-6">
        <div className="flex items-center justify-between">
          <button onClick={onBack} className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors">
            <ArrowLeft className="w-5 h-5" />
            <span>Back</span>
          </button>

          <div className="text-center">
            <h1 className="text-3xl font-bold text-white">{role.name}</h1>
            {/* Spotlight Mode Toggle */}
            <div className="mt-3 inline-flex bg-slate-800/50 rounded-lg p-1">
              <button
                onClick={() => setSpotlightMode(true)}
                className={`px-4 py-2 rounded-md text-sm font-semibold transition-colors ${
                  spotlightMode
                    ? 'bg-blue-600 text-white'
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                <Eye className="w-4 h-4 inline mr-2" />
                Spotlight
              </button>
              <button
                onClick={() => setSpotlightMode(false)}
                className={`px-4 py-2 rounded-md text-sm font-semibold transition-colors ${
                  !spotlightMode
                    ? 'bg-blue-600 text-white'
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                Full Map
              </button>
            </div>
          </div>

          <div className="flex gap-2">
            <button onClick={onBack} className="p-2 bg-slate-700 hover:bg-slate-600 rounded-lg transition-colors" title="Back">
              <ArrowLeft className="w-5 h-5 text-white" />
            </button>
          </div>
        </div>
      </div>

      {/* Graph viewport - SINGLE TRANSFORMED CONTAINER */}
      <div
        className="absolute inset-0"
        style={{
          transform: `translate(${pan.x}px, ${pan.y}px) scale(${zoom})`,
          transformOrigin: '0 0',
        }}
      >
        {/* SVG for edges - SAME COORDINATE SPACE AS NODES */}
        <svg className="absolute inset-0 pointer-events-none" style={{ overflow: 'visible' }}>
          <defs>
            {/* Main path arrow - sleek and modern */}
            <marker id="arrowhead-main" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto">
              <path d="M0,0 L0,10 L10,5 z" fill="#3b82f6" />
            </marker>
            {/* Competency arrow - subtle and distinct */}
            <marker id="arrowhead-competency" markerWidth="8" markerHeight="8" refX="7" refY="4" orient="auto">
              <path d="M0,0 L0,8 L8,4 z" fill="#a855f7" />
            </marker>
          </defs>

          {/* Draw main path arrows (sequential unlocks) - straight blue lines */}
          {path.map((node) => {
            const sourcePos = nodePositions.get(node.skillNodeId);
            if (!sourcePos) return null;

            return node.unlocks.map((unlockId) => {
              const targetPos = nodePositions.get(unlockId);
              if (!targetPos) return null;

              // STEP 1: Visual hierarchy - thicker spine edges
              // STEP 4: Dynamic edge calculation - center to center
              const MAIN_NODE_RADIUS = 70; // Increased for 1.2x visual size
              const dx = targetPos.x - sourcePos.x;
              const dy = targetPos.y - sourcePos.y;
              const distance = Math.sqrt(dx * dx + dy * dy);

              // Calculate edge start/end points (center to center minus radius)
              const x1 = sourcePos.x + (dx / distance) * MAIN_NODE_RADIUS;
              const y1 = sourcePos.y + (dy / distance) * MAIN_NODE_RADIUS;
              const x2 = targetPos.x - (dx / distance) * MAIN_NODE_RADIUS;
              const y2 = targetPos.y - (dy / distance) * MAIN_NODE_RADIUS;

              return (
                <path
                  key={`unlock-${node.skillNodeId}-${unlockId}`}
                  d={`M ${x1},${y1} L ${x2},${y2}`}
                  stroke="#3b82f6"
                  strokeWidth="5"
                  fill="none"
                  markerEnd="url(#arrowhead-main)"
                  opacity="0.8"
                />
              );
            });
          })}

          {/* Draw competency arrows (branch out from main path) - smooth curves */}
          {path.map((node, nodeIndex) => {
            const sourcePos = nodePositions.get(node.skillNodeId);
            if (!sourcePos || !node.competencies || node.competencies.length === 0) return null;

            return node.competencies.map((compId, compIndex) => {
              const targetPos = nodePositions.get(compId);
              if (!targetPos) return null;

              // STEP 1: Visual hierarchy - thinner branch edges
              // STEP 4: Dynamic edge calculation - center to center
              const MAIN_NODE_RADIUS = 70;  // Main spine node radius (1.2x)
              const BRANCH_NODE_RADIUS = 50; // Branch node radius (0.8x)

              // Calculate direction and distance
              const dx = targetPos.x - sourcePos.x;
              const dy = targetPos.y - sourcePos.y;
              const distance = Math.sqrt(dx * dx + dy * dy);

              // Calculate connection points based on direction
              const isAbove = targetPos.y < sourcePos.y;

              // Start point: edge of main node in direction of branch
              const x1 = sourcePos.x + (dx / distance) * MAIN_NODE_RADIUS;
              const y1 = sourcePos.y + (dy / distance) * MAIN_NODE_RADIUS;

              // End point: edge of branch node in direction of main
              const x2 = targetPos.x - (dx / distance) * BRANCH_NODE_RADIUS;
              const y2 = targetPos.y - (dy / distance) * BRANCH_NODE_RADIUS;

              // Smooth curve control points
              const controlPoint1X = x1 + (x2 - x1) * 0.3;
              const controlPoint1Y = y1;
              const controlPoint2X = x2 - (x2 - x1) * 0.3;
              const controlPoint2Y = y2;

              // Check if this branch is locked
              const branchNode = path.find(n => n.skillNodeId === compId);
              const isBranchLocked = branchNode ? isNodeLocked(branchNode) : false;

              return (
                <path
                  key={`comp-${node.skillNodeId}-${compId}`}
                  d={`M ${x1},${y1} C ${controlPoint1X},${controlPoint1Y} ${controlPoint2X},${controlPoint2Y} ${x2},${y2}`}
                  stroke={isBranchLocked ? "#64748b" : "#a855f7"}
                  strokeWidth="2"
                  fill="none"
                  markerEnd="url(#arrowhead-competency)"
                  opacity={isBranchLocked ? "0.3" : "0.6"}
                  strokeLinecap="round"
                />
              );
            });
          })}
        </svg>

        {/* Nodes - POSITIONED IN WORLD COORDINATES */}
        {path.map((node, index) => {
          const pos = nodePositions.get(node.skillNodeId);
          if (!pos) return null;

          const isLocked = isNodeLocked(node);
          const isCompleted = completedNodeIds.has(node.skillNodeId);
          const isFrontier = node.skillNodeId === frontierNode?.skillNodeId;
          const isInSpotlight = spotlightNodes.has(node.skillNodeId);
          const isBranch = isCompetency(node.skillNodeId);

          // Spotlight mode opacity logic
          let opacity = 1;
          if (spotlightMode) {
            if (isFrontier) {
              opacity = 1; // Current node: 100%
            } else if (isInSpotlight) {
              // Prerequisites: 60%, Unlocks: 40%
              const isPrereq = frontierNode?.dependencies?.includes(node.skillNodeId);
              opacity = isPrereq ? 0.6 : 0.4;
            } else {
              opacity = 0.15; // Everything else: 15%
            }
          } else {
            opacity = isLocked ? 0.2 : 1; // Full map mode: original logic
          }

          // STEP 1: Visual hierarchy - main spine larger (1.2x), branches smaller (0.8x)
          let nodeSize: string;
          if (isFrontier) {
            // Frontier gets extra emphasis
            nodeSize = isBranch ? 'w-28 h-28' : 'w-40 h-40';
          } else if (isBranch) {
            // Branch nodes: smaller (0.8x of base 28 ≈ 22)
            nodeSize = 'w-24 h-24';
          } else {
            // Main spine nodes: larger (1.2x of base 28 ≈ 34)
            nodeSize = 'w-36 h-36';
          }

          return (
            <div
              key={node.skillNodeId}
              className="absolute pointer-events-auto"
              style={{
                left: pos.x,
                top: pos.y,
                transform: isFrontier ? 'translate(-50%, -50%) scale(1.15)' : 'translate(-50%, -50%)',
                opacity,
                transition: 'transform 0.3s ease, opacity 0.3s ease',
              }}
            >
              <button
                onClick={() => {
                  if (!isLocked) {
                    setSelectedNode(node);
                    setShowDetailPanel(true);
                  }
                }}
                onMouseEnter={() => setHoveredNode(node.skillNodeId)}
                onMouseLeave={() => setHoveredNode(null)}
                disabled={isLocked}
                className={`
                  relative ${nodeSize} rounded-full
                  flex items-center justify-center
                  transition-all duration-200
                  ${isLocked
                    ? isBranch
                      ? 'bg-slate-700/30 cursor-not-allowed border-2 border-slate-700/50 saturate-50'
                      : 'bg-slate-800/50 cursor-not-allowed border-2 border-slate-700'
                    : isCompleted
                      ? 'bg-gradient-to-br from-green-600 to-emerald-700 cursor-pointer hover:shadow-2xl hover:shadow-green-500/50'
                      : 'bg-gradient-to-br from-blue-600 to-purple-600 cursor-pointer hover:shadow-2xl hover:shadow-blue-500/50 hover:scale-110'}
                  ${isFrontier ? 'ring-4 ring-yellow-400 shadow-2xl shadow-yellow-500/50 animate-pulse' : ''}
                  ${!isLocked && !isFrontier ? 'ring-2 ring-white/30' : ''}
                `}
              >
                {/* Number badge - INSIDE BUTTON, positioned at top-left */}
                <div className={`absolute top-0 left-0 ${isFrontier ? 'w-9 h-9 text-sm -translate-x-1 -translate-y-1' : 'w-7 h-7 text-xs -translate-x-0.5 -translate-y-0.5'} bg-slate-900 rounded-full flex items-center justify-center text-white font-bold border-2 border-slate-700`}>
                  {index + 1}
                </div>

                {/* Icon overlay - ONLY show for locked/completed, hide text */}
                {isLocked && (
                  <div className="absolute inset-0 flex items-center justify-center">
                    <Lock className="w-10 h-10 text-slate-600" />
                  </div>
                )}
                {isCompleted && !isFrontier && (
                  <div className="absolute top-0 right-0 bg-green-500 rounded-full p-1 translate-x-1 -translate-y-1">
                    <CheckCircle className="w-5 h-5 text-white" />
                  </div>
                )}

                {/* Text - only show when NOT locked (icon would cover it) */}
                {!isLocked && (
                  <div className="text-center p-2 relative">
                    <div className={`text-white font-bold line-clamp-2 ${isFrontier ? 'text-sm' : 'text-xs'}`}>
                      {node.name}
                    </div>
                  </div>
                )}

              </button>

              {/* EDLSG State Badge - shows current skill state */}
              <div className="absolute left-1/2 -translate-x-1/2 pointer-events-none" style={{ top: '100%', marginTop: '8px' }}>
                <div className={`px-2 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider whitespace-nowrap ${
                  isCompleted
                    ? 'bg-green-500/20 text-green-400 border border-green-500/50'
                    : isFrontier
                    ? 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/50 animate-pulse'
                    : isLocked
                    ? 'bg-slate-700/50 text-slate-500 border border-slate-600/50'
                    : 'bg-blue-500/20 text-blue-400 border border-blue-500/50'
                }`}>
                  {isCompleted ? '✓ PROVED' : isFrontier ? '◉ ACTIVE' : isLocked ? '🔒 LOCKED' : '○ READY'}
                </div>
              </div>

              {/* STEP 2: Semantic label badge for branch nodes on hover - OUTSIDE button for proper z-index */}
              {isBranch && hoveredNode === node.skillNodeId && (() => {
                const semantics = getBranchSemantics(node);
                return semantics ? (
                  <div className="absolute left-1/2 -translate-x-1/2 pointer-events-none" style={{ top: '100%', marginTop: '8px', zIndex: 9999 }}>
                    <div className={`px-3 py-2 rounded-lg border ${semantics.color} backdrop-blur-sm shadow-2xl whitespace-nowrap text-xs font-semibold`}>
                      {semantics.label}
                    </div>
                    <div className="mt-1 px-3 py-1 bg-slate-900/95 border border-slate-700 rounded text-xs text-slate-300 shadow-2xl max-w-[200px] text-center">
                      {semantics.tooltip}
                    </div>
                  </div>
                ) : null;
              })()}
            </div>
          );
        })}
      </div>

      {/* DETAIL PANEL - Right side teacher panel */}
      {showDetailPanel && selectedNode && (
        <div className="absolute top-0 right-0 bottom-0 w-96 bg-gradient-to-l from-black via-slate-900 to-transparent z-40 pointer-events-auto">
          <div className="h-full overflow-y-auto p-6 space-y-6">
            {/* Close button */}
            <button
              onClick={() => setShowDetailPanel(false)}
              className="absolute top-4 right-4 p-2 bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors"
            >
              <ArrowLeft className="w-5 h-5 text-white" />
            </button>

            {/* Node header */}
            <div className="space-y-2 pt-12">
              <div className="text-sm text-slate-400 uppercase tracking-wider font-semibold">
                Node #{path.findIndex(n => n.skillNodeId === selectedNode.skillNodeId) + 1}
              </div>
              <h2 className="text-3xl font-bold text-white">{selectedNode.name}</h2>
              <div className="flex items-center gap-2 text-sm text-slate-400">
                <span>{selectedNode.estimatedHours}h</span>
                <span>•</span>
                <span>Difficulty {selectedNode.difficulty}/10</span>
              </div>
            </div>

            {/* What you'll be able to do */}
            <div className="space-y-2">
              <h3 className="text-lg font-semibold text-white">What you'll be able to do</h3>
              <p className="text-slate-300">{selectedNode.whyItMatters}</p>
            </div>

            {/* Assessment */}
            <div className="space-y-2">
              <h3 className="text-lg font-semibold text-white">How you'll prove it</h3>
              <div className="bg-slate-800/50 p-4 rounded-lg">
                <div className="text-sm text-slate-400 mb-2">Assessment Type</div>
                <div className="text-white font-semibold uppercase">{selectedNode.assessmentType}</div>
                <div className="text-slate-300 mt-2 text-sm">{selectedNode.proofRequirement}</div>
              </div>
            </div>

            {/* Prerequisites */}
            {selectedNode.dependencies && selectedNode.dependencies.length > 0 && (
              <div className="space-y-2">
                <h3 className="text-lg font-semibold text-white">Prerequisites</h3>
                <div className="space-y-2">
                  {selectedNode.dependencies.map(depId => {
                    const depNode = path.find(n => n.skillNodeId === depId);
                    const isCompleted = completedNodeIds.has(depId);
                    return depNode ? (
                      <div key={depId} className="flex items-center gap-2 text-sm">
                        {isCompleted ? (
                          <CheckCircle className="w-4 h-4 text-green-500 flex-shrink-0" />
                        ) : (
                          <div className="w-4 h-4 border-2 border-slate-600 rounded-full flex-shrink-0" />
                        )}
                        <span className={isCompleted ? 'text-slate-400 line-through' : 'text-white'}>
                          {depNode.name}
                        </span>
                      </div>
                    ) : null;
                  })}
                </div>
              </div>
            )}

            {/* Resources */}
            <div className="space-y-2">
              <h3 className="text-lg font-semibold text-white">Learning Resources</h3>
              <div className="space-y-2">
                {selectedNode.learnResources && selectedNode.learnResources.length > 0 ? (
                  selectedNode.learnResources.map((resource, idx) => (
                    <a
                      key={idx}
                      href={resource.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="block p-3 bg-slate-800/50 hover:bg-slate-800 rounded-lg transition-colors"
                    >
                      <div className="text-white font-semibold text-sm">{resource.title}</div>
                      <div className="text-slate-400 text-xs mt-1">{resource.type} • {resource.estimatedMinutes}min</div>
                    </a>
                  ))
                ) : (
                  <div className="text-slate-400 text-sm">Resources will be loaded when you start this node</div>
                )}
              </div>
            </div>

            {/* Actions */}
            <div className="space-y-3 pb-6">
              <button
                onClick={() => {
                  onSelectNode(selectedNode);
                  setShowDetailPanel(false);
                }}
                disabled={isNodeLocked(selectedNode)}
                className={`w-full py-4 px-6 rounded-xl font-bold text-lg flex items-center justify-center gap-2 transition-all ${
                  isNodeLocked(selectedNode)
                    ? 'bg-slate-700 text-slate-500 cursor-not-allowed'
                    : 'bg-blue-600 hover:bg-blue-700 text-white hover:scale-105'
                }`}
              >
                <Play className="w-5 h-5 flex-shrink-0" />
                <span>{isNodeLocked(selectedNode) ? 'LOCKED' : 'START SESSION'}</span>
              </button>
              <button
                onClick={async () => {
                  if (!selectedNode) return;

                  setLoadingExplanation(true);
                  setExplanationError(null);
                  setShowAIExplanation(true);

                  try {
                    const response = await fetch('http://localhost:8080/api/ai/explain', {
                      method: 'POST',
                      headers: {
                        'Content-Type': 'application/json',
                      },
                      body: JSON.stringify({
                        skillName: selectedNode.name,
                        whyItMatters: selectedNode.whyItMatters,
                        proofRequirement: selectedNode.proofRequirement,
                        learnResources: selectedNode.learnResources,
                      }),
                    });

                    if (!response.ok) {
                      const errorData = await response.json();
                      throw new Error(errorData.error || 'Failed to generate explanation');
                    }

                    const data = await response.json();
                    setAiExplanation(data.explanation);
                  } catch (err: any) {
                    setExplanationError(err.message || 'Failed to generate AI explanation');
                  } finally {
                    setLoadingExplanation(false);
                  }
                }}
                className="w-full py-3 bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 text-white rounded-xl font-semibold transition-all"
              >
                ✨ Explain it to me (AI)
              </button>
            </div>
          </div>
        </div>
      )}

      {/* COMMAND BAR - Fixed bottom - COMPACT VERSION */}
      <div className="absolute bottom-0 left-0 right-0 z-50 bg-gradient-to-t from-black via-black/95 to-transparent px-6 py-4">
        <div className="max-w-5xl mx-auto">
          {frontierNode ? (
            <div className="flex items-center justify-between gap-6">
              {/* Current Mission - Compact */}
              <div className="flex-1 min-w-0">
                <div className="text-xs text-slate-500 uppercase tracking-wider font-semibold mb-1">Current Mission</div>
                <h2 className="text-2xl font-bold text-white truncate">{frontierNode.name}</h2>
                <div className="flex items-center gap-3 mt-1">
                  <div className={`px-3 py-1 rounded-full font-semibold text-xs flex items-center gap-1.5 ${
                    getNodeState(frontierNode) === 'LOCKED' ? 'bg-red-500/20 text-red-400' :
                    getNodeState(frontierNode) === 'COMPLETED' ? 'bg-green-500/20 text-green-400' :
                    'bg-blue-500/20 text-blue-400'
                  }`}>
                    {getNodeState(frontierNode) === 'LOCKED' && <Lock className="w-3 h-3" />}
                    {getNodeState(frontierNode) === 'COMPLETED' && <CheckCircle className="w-3 h-3" />}
                    {getNodeState(frontierNode) === 'AVAILABLE' && <Play className="w-3 h-3" />}
                    {getNodeState(frontierNode)}
                  </div>
                  <div className="text-slate-400 text-xs">
                    {frontierNode.estimatedHours}h • {frontierNode.difficulty}/10
                  </div>
                </div>
              </div>

              {/* Actions - Compact */}
              <div className="flex items-center gap-3">
                {getNodeState(frontierNode) === 'AVAILABLE' && (
                  <button
                    onClick={() => onSelectNode(frontierNode)}
                    className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl transition-all hover:scale-105 flex items-center gap-2"
                  >
                    <Play className="w-5 h-5" />
                    START SESSION
                  </button>
                )}
                {getNodeState(frontierNode) === 'LOCKED' && (
                  <button
                    disabled
                    className="px-6 py-3 bg-slate-700 text-slate-500 font-bold rounded-xl cursor-not-allowed flex items-center gap-2"
                  >
                    <Lock className="w-5 h-5" />
                    LOCKED
                  </button>
                )}
                <button
                  onClick={() => {
                    setSelectedNode(frontierNode);
                    setShowDetailPanel(true);
                  }}
                  className="px-4 py-3 bg-slate-700 hover:bg-slate-600 text-white font-semibold rounded-xl transition-colors flex items-center gap-2"
                >
                  <BookOpen className="w-4 h-4" />
                  VIEW
                </button>
                <button
                  onClick={findCurrentNode}
                  className="p-3 bg-slate-700 hover:bg-slate-600 text-white rounded-xl transition-colors"
                  title="Center on Node"
                >
                  <Target className="w-4 h-4" />
                </button>
                <button
                  onClick={fitToGraph}
                  className="p-3 bg-slate-700 hover:bg-slate-600 text-white rounded-xl transition-colors"
                  title="Reset View"
                >
                  <Maximize2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ) : (
            <div className="text-center">
              <h2 className="text-2xl font-bold text-white mb-2">All skills completed!</h2>
              <p className="text-slate-400">You've mastered this learning path.</p>
            </div>
          )}
        </div>
      </div>

      {/* AI EXPLANATION MODAL */}
      {showAIExplanation && (
        <div className="absolute inset-0 bg-black/90 backdrop-blur-md z-[100] flex items-center justify-center p-8">
          <div className="bg-gradient-to-br from-slate-900 via-purple-900/20 to-slate-900 border border-purple-500/50 rounded-2xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
            {/* Header */}
            <div className="flex items-center justify-between p-6 border-b border-purple-500/30">
              <div className="flex items-center gap-3">
                <Sparkles className="w-6 h-6 text-purple-400" />
                <h2 className="text-2xl font-bold text-white">AI Explanation</h2>
              </div>
              <button
                onClick={() => {
                  setShowAIExplanation(false);
                  setAiExplanation('');
                  setExplanationError(null);
                }}
                className="p-2 hover:bg-slate-800 rounded-lg transition-colors"
              >
                <X className="w-6 h-6 text-slate-400" />
              </button>
            </div>

            {/* Content */}
            <div className="flex-1 overflow-y-auto p-6">
              {loadingExplanation ? (
                <div className="flex flex-col items-center justify-center py-12 space-y-4">
                  <Loader2 className="w-12 h-12 text-purple-400 animate-spin" />
                  <p className="text-slate-300 text-lg">Generating personalized explanation...</p>
                  <p className="text-slate-500 text-sm">This may take 10-20 seconds</p>
                </div>
              ) : explanationError ? (
                <div className="bg-red-900/20 border border-red-500/50 rounded-lg p-6 text-center">
                  <p className="text-red-400 font-semibold mb-2">Failed to generate explanation</p>
                  <p className="text-slate-400 text-sm">{explanationError}</p>
                  <button
                    onClick={() => setShowAIExplanation(false)}
                    className="mt-4 px-4 py-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg transition-colors"
                  >
                    Close
                  </button>
                </div>
              ) : aiExplanation ? (
                <div className="prose prose-invert prose-purple max-w-none">
                  <div
                    className="text-slate-200 leading-relaxed"
                    dangerouslySetInnerHTML={{
                      __html: formatMarkdown(aiExplanation)
                    }}
                  />
                </div>
              ) : null}
            </div>

            {/* Footer */}
            {aiExplanation && !loadingExplanation && (
              <div className="p-6 border-t border-purple-500/30 bg-slate-900/50">
                <p className="text-slate-400 text-sm text-center">
                  💡 This explanation is generated by AI and tailored to help you master this concept
                </p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

/**
 * Simple markdown formatter for AI explanations
 */
function formatMarkdown(markdown: string): string {
  return markdown
    // Headers
    .replace(/^### (.*$)/gim, '<h3 class="text-xl font-bold text-purple-300 mt-6 mb-3">$1</h3>')
    .replace(/^## (.*$)/gim, '<h2 class="text-2xl font-bold text-purple-200 mt-8 mb-4">$1</h2>')
    .replace(/^# (.*$)/gim, '<h1 class="text-3xl font-bold text-purple-100 mt-8 mb-4">$1</h1>')
    // Bold and italic
    .replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
    .replace(/\*\*(.+?)\*\*/g, '<strong class="text-white font-semibold">$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    // Code blocks
    .replace(/```([\s\S]*?)```/g, '<pre class="bg-slate-800 p-4 rounded-lg overflow-x-auto my-4"><code class="text-green-300">$1</code></pre>')
    // Inline code
    .replace(/`([^`]+)`/g, '<code class="bg-slate-800 px-2 py-1 rounded text-green-300">$1</code>')
    // Lists
    .replace(/^\- (.+)$/gim, '<li class="ml-6 my-2">• $1</li>')
    .replace(/^  \- (.+)$/gim, '<li class="ml-12 my-1 text-slate-300">○ $1</li>')
    // Checkboxes
    .replace(/- \[ \] (.+)$/gim, '<li class="ml-6 my-2">☐ $1</li>')
    .replace(/- \[x\] (.+)$/gim, '<li class="ml-6 my-2">✓ $1</li>')
    // Line breaks
    .replace(/\n\n/g, '<br/><br/>')
    .replace(/\n/g, '<br/>');
}
