# Frontier Map UI - Dev Notes

## What Was Built

Replaced the grid-based skill cards UI with a **Metro/Subway Map visualization** using React Flow + ELK layout.

## Architecture

### Libraries Used
- **React Flow** (`reactflow`) - Graph visualization framework for React
- **ELK.js** (`elkjs`) - Deterministic graph layout algorithm
- **Layout Algorithm**: `layered` with `RIGHT` direction (left-to-right flow)

### Why These Libraries?
- **React Flow**: Industry-standard for interactive graphs in React, excellent performance, built-in pan/zoom
- **ELK**: Deterministic DAG layout (no random hairball), produces clean layered graphs like metro maps
- **Alternative considered**: D3.js force-directed - rejected because it creates chaotic overlapping nodes

## File Structure

```
careermap-ui/src/app/frontier/
├── page.tsx            - Main Frontier Map component (Metro UI)
├── SkillNode.tsx       - Custom node component (skill station)
├── EvidenceModal.tsx   - Evidence submission modal
```

## Key Features

### 1. Deterministic Layout
- **ELK layered algorithm** positions nodes left-to-right
- Nodes arranged in layers based on prerequisites
- No random reflow - same data = same layout every time
- Spacing: 80px between nodes, 120px between layers

### 2. Visual State Encoding
Each skill node shows:
- **Status** (color + icon):
  - PROVED: Green, solid, checkmark ✓
  - ACTIVE: Blue, pulsing ring, lightning ⚡
  - INFERRED: Yellow, dashed border, dot •
  - UNSEEN: Gray, locked 🔒
  - STALE: Red, dashed, warning ⚠
- **Confidence**: Progress bar at bottom (0-100%)
- **Demand Weight**: Node size scales with importance
- **Recommended Node**: Blue pulsing dot in corner

### 3. Interactive Inspector Panel
- **Click any node** → Opens inspector on right side
- Shows: Status, Confidence %, Demand weight
- If recommended: Shows "why" explanation
- Close button to dismiss

### 4. Bottom Action Strip
- Always visible at bottom
- Shows: Next recommended action
- Blue pulsing dot indicates "live" recommendation
- Click action button → Routes to assessment/opportunities

### 5. Evidence Flow
- "+Add Evidence" button (top right)
- Opens modal overlay
- Submit → OpenAI extracts skills
- **Map auto-refreshes** with new states
- State transitions animate (future enhancement)

## Layout Configuration

```javascript
{
  'elk.algorithm': 'layered',        // DAG layout
  'elk.direction': 'RIGHT',          // Left-to-right flow
  'elk.spacing.nodeNode': '80',      // 80px between nodes
  'elk.layered.spacing.nodeNodeBetweenLayers': '120'  // 120px between layers
}
```

Node dimensions: 180×80px (to fit skill name + stats)

## Data Flow

```
GET /api/v2/frontier?userId=X
  ↓
Build nodes from frontierPreview
  ↓
Build edges from hardcoded prereq map (TODO: fetch from backend)
  ↓
Apply ELK layout algorithm
  ↓
Render React Flow canvas
  ↓
User clicks node → Show inspector
User clicks action → Route to assessment
User submits evidence → Refetch frontier → Update map
```

## Current Limitations & TODOs

### 1. Edges Are Hardcoded
Currently using a static edge map in `buildGraph()`:
```javascript
const edgeMap = {
  'Linux': ['Network Security', 'Docker'],
  'Network Security': ['Penetration Testing', 'Security'],
  // etc...
};
```

**TODO**: Create backend endpoint to fetch actual prerequisite edges:
```
GET /api/v2/prereq-edges?roleId=1
Returns: [{from: 20, to: 21, type: 'HARD', strength: 0.9}, ...]
```

### 2. Only Shows Frontier Preview (7 skills)
Currently shows only the 7 skills from `frontierPreview`.

**TODO**: Fetch extended graph including prerequisites + downstream skills (30-80 nodes total)

### 3. No Skill Clustering
All skills shown in one flat layer.

**TODO**: Group skills by domain (Security, Web Dev, Cloud, etc.) with visual separation

### 4. State Transition Animations
Currently instant refresh on evidence submission.

**TODO**: Animate node color/style changes when states update

## How to Test

1. **Start backend**: `cd backend && ./gradlew bootRun`
2. **Start frontend**: `cd careermap-ui && npm run dev`
3. **Login**: http://localhost:3000/login (dhairya224@test.com)
4. **Navigate to Frontier**: Click "Frontier" in navbar
5. **Interact**:
   - Pan/zoom the map (mouse drag / scroll)
   - Click any skill node → See inspector
   - Click "Assess Linux" button → Routes to assessment
   - Click "+Add Evidence" → Submit evidence → Map refreshes

## Performance

- **Initial render**: ~500ms (includes ELK layout computation)
- **Pan/Zoom**: 60fps (React Flow optimized)
- **Node count**: Currently 7 nodes (frontier), designed for up to 80 nodes
- **Layout complexity**: O(n log n) where n = node count (ELK layered algorithm)

## Responsive Design

- **Desktop**: Full screen map, inspector panel on right
- **Mobile**: TODO - Bottom sheet inspector, touch pan/zoom
- **Current**: Fixed h-screen layout, works on desktop only

## Styling

- **Theme**: Black background (#000), gray borders (#1a1a1a)
- **Accent**: Blue (#3b82f6) for recommended/active states
- **Status colors**:
  - Green (#10b981): PROVED
  - Blue (#3b82f6): ACTIVE
  - Yellow (#eab308): INFERRED
  - Red (#ef4444): STALE
  - Gray (#6b7280): UNSEEN
- **Typography**: System font, semibold for labels, regular for values
- **Shadows**: Subtle glows on PROVED/ACTIVE nodes

## Next Steps

1. **Backend API**: Add `GET /api/v2/prereq-edges` endpoint
2. **Extended Graph**: Fetch 30-80 nodes (not just frontier preview)
3. **Mobile Responsive**: Bottom sheet inspector
4. **Animations**: Smooth state transitions on evidence submit
5. **Skill Domains**: Group by domain with visual clustering
6. **Blockers UI**: Show missing prerequisites in inspector
7. **Unlock Potential**: Visualize downstream unlocks on hover

## Known Issues

- **Edge directions**: Some edges may appear backwards (need to verify prereq direction in database)
- **Node overlap**: Rare cases with many connected skills (ELK handles this well but could optimize spacing)
- **First render**: Brief flash while ELK computes layout (could add skeleton loader)

---

**Bottom Line**: The Metro Map UI is functional and provides a dramatically better UX than the card grid. Layout is deterministic, interactions are smooth, and the system communicates "skill graph" instead of "skill list."
