# Frontier as Root - System Architecture

## Paradigm Shift: From Web App to Living System

The Frontier Map is **NOT a page** - it's **THE APPLICATION**.

## Core Principle

**The Frontier is a navigable world, not a UI component.**

Think of it like:
- Figma canvas (you're inside the design)
- Bloomberg terminal (control room for live data)
- Mission control dashboard (stateful system you inhabit)

NOT like:
- A tab in a multi-page app
- A component within a layout
- A "view" among many views

## User Experience Flow

### What the user experiences:

1. **Login** → Lands directly in Frontier
2. **Frontier becomes their reality** - full viewport, no chrome
3. **All actions happen FROM Frontier** - not "navigate away"
4. **System updates in place** - add evidence → graph morphs
5. **Other features are temporary modes** - not destinations

### Mental Model:

```
Before (Wrong):
Login → Homepage → Click "Frontier" tab → View graph

After (Correct):
Login → YOU ARE INSIDE THE SYSTEM → Make one move → World updates
```

## Architecture Changes

### 1. Routing

**Frontier is the root landing experience:**

```typescript
// AuthGuard.tsx
if (user && (pathname === "/" || pathname === "/landing")) {
  router.push("/frontier");  // Default landing = Frontier
}

// Login.tsx
if (data.success) {
  router.push("/frontier");  // Direct to Frontier, not "/"
}
```

### 2. Layout Exclusion

**Frontier bypasses AppLayout (no navbar, no wrapper):**

```typescript
// ConditionalLayout.tsx
const noLayoutPages = ["/landing", "/login", "/signup", "/frontier"];
```

Why: The navbar implies hierarchy above the system. There should be none.

### 3. Floating Controls

**All navigation is embedded IN the canvas:**

- **Top-left**: Floating role indicator (pulsing blue dot)
- **Top-right**:
  - `+ Evidence` button
  - Menu button (opens mode switcher)
- **Bottom-center**: Recommended action strip
- **On-click**: Inspector panel overlay

Everything is translucent/glassy with backdrop blur - the canvas feels uninterrupted.

### 4. Other Pages Become "Modes"

**Current implementation:**

Legacy pages (Path, Assessment, Opportunities) still exist as separate routes BUT:

1. They open in **new tabs** (via `window.open`) to preserve Frontier state
2. Navbar shows **"← Back to Frontier"** as first item
3. They are treated as temporary departures, not destinations

**Future implementation (recommended):**

Convert to modal overlays:

```typescript
// Assessment Mode
<AssessmentModal
  skillId={selectedSkillId}
  onComplete={() => {
    // Close modal
    // Refetch Frontier data
    // Watch graph update in real-time
  }}
/>
```

Why: This creates **continuity** not **navigation**. The Frontier is always behind everything.

## Visual Design Philosophy

### The Canvas is Primary

- **Full viewport** (100vw × 100vh)
- **No fixed headers/footers** that create "window in page" feeling
- **All UI floats** over the canvas
- **Background**: Pure black (#000)
- **Overlays**: Glass morphism (black/80, backdrop-blur-xl)

### Attention Management

**The system enforces focus:**

- ONE recommended node (100% opacity, blue glow, pulse)
- Relevant nodes (70% opacity, subtle glow)
- Everything else (15% opacity, near-invisible)

**The camera is part of UX:**

- Auto-centers on recommended node
- Smooth 800ms transitions
- Users pan/zoom to explore

### State Visualization

**The graph is a projection of system state:**

- Node size = demand weight
- Glow intensity = unlock potential
- Fill gradient = confidence %
- Edge thickness = prerequisite strength
- Animated edges = from recommended skill

When evidence is submitted:
1. Modal closes
2. API refetches frontier
3. Graph nodes transition state
4. Camera re-centers on new recommendation

**This makes the decision engine visible and tangible.**

## Why This Matters

### The Backend is a Decision System

Not a CRUD app. It's:
- State machine over skill graph
- Multi-factor scoring algorithm
- Evidence-driven inference engine

### The UI Must Reflect This

If Frontier is "one tab among others":
- Users treat it as **something to browse**
- It feels like a report/dashboard
- Decisions feel optional

If Frontier is the root experience:
- Users treat it as **the place where decisions happen**
- It feels like a live system
- Recommended action feels unavoidable

**That distinction is EVERYTHING.**

## Code Structure

### Key Files

```
careermap-ui/src/
├── app/
│   ├── frontier/
│   │   ├── page.tsx              # Root experience (full viewport)
│   │   ├── SkillNode.tsx          # Visual encoding of state
│   │   └── EvidenceModal.tsx      # Evidence submission mode
│   ├── page.tsx                   # Legacy Path view (remove later)
│   ├── assessment/page.tsx        # Legacy (convert to modal)
│   └── opportunities/page.tsx     # Legacy (convert to modal)
├── components/
│   └── layout/
│       ├── ConditionalLayout.tsx  # Excludes Frontier from layout
│       ├── AuthGuard.tsx          # Redirects to Frontier
│       └── Navbar.tsx             # Shows "Back to Frontier" first
└── globals.css                    # Pulse-slow animation
```

### Floating Controls Implementation

```typescript
// Frontier top bar
<div className="absolute top-0 left-0 right-0 z-30 px-6 py-4 pointer-events-none">
  {/* Role indicator - pointer-events-auto */}
  <div className="bg-black/70 backdrop-blur-xl pointer-events-auto">
    <span>{role.name}</span>
  </div>

  {/* Action buttons - pointer-events-auto */}
  <div className="pointer-events-auto">
    <button onClick={() => setShowEvidenceModal(true)}>+ Evidence</button>
    <button onClick={() => setShowModeMenu(true)}>☰</button>
  </div>
</div>
```

**Pattern**: Parent has `pointer-events-none` (canvas clickable), children have `pointer-events-auto` (buttons work).

## Migration Path

### Phase 1: Current State ✅

- Frontier bypasses AppLayout
- Login redirects to Frontier
- Legacy pages open in new tabs
- Navbar shows "Back to Frontier"

### Phase 2: Convert to Modals (Recommended)

Convert Assessment/Opportunities to overlay modes:

```typescript
// In Frontier page.tsx
const [mode, setMode] = useState<'map' | 'assessment' | 'opportunities'>('map');

{mode === 'assessment' && (
  <AssessmentOverlay
    skillId={recommendedSkillId}
    onClose={() => {
      setMode('map');
      fetchFrontier(userId); // Refresh graph
    }}
  />
)}
```

### Phase 3: Remove Legacy Routes

Delete `/page.tsx` (Path), `/assessment`, `/opportunities` routes.

Only public routes remain: `/landing`, `/login`, `/signup`

**Result**: The app IS the Frontier. Everything else is a mode within it.

## Testing the Experience

1. **Logout** and **login again**
2. You should land **directly in Frontier** (full screen, no navbar)
3. **Pan and zoom** the graph - it feels like a world you're inside
4. **Click recommended action** - opens in new tab (preserves Frontier)
5. **Click menu → Path View** - legacy view, but "Back to Frontier" is prominent
6. **Submit evidence** - modal closes, graph updates, camera re-centers

**Goal**: You never feel like you "left" the Frontier. It's always your reality.

## Key Quotes from Design Brief

> "This Frontier screen is not a page inside the app. This Frontier screen **is the app**."

> "Think of the Frontier as a world you move inside, not a component you look at."

> "The backend is a decision system. The UI must behave like a decision surface, not a website."

> "If this becomes the root, full-screen, navigable world, users treat it as: **the place where decisions happen**. That distinction is EVERYTHING."

---

**Bottom Line**: The Frontier Map is now the primary application reality. All navigation is embedded within it. Other features are temporary modes that always return to the Frontier. This transforms the UX from "browsing a website" to "inhabiting a live decision system."
