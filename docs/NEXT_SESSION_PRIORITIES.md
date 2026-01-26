# NEXT SESSION PRIORITIES

**Date:** January 13, 2026
**Status:** Planning Document

---

## CRITICAL - Bug Fixes

### 1. **Fix Explore Mode - Can't Select Role After Domain**
**Current Bug:** When in exploratory mode, clicking a domain doesn't show roles to select
**Expected:** Domain → Role selection screen → Path generation

**Investigation Needed:**
- Check `careermap-ui/src/app/frontier/page.tsx` zoom logic
- Ensure exploratory mode allows role selection
- May be related to auto-navigation logic interfering

**File:** `careermap-ui/src/app/frontier/page.tsx:107-132`

---

### 2. **Add User Goal Setting Feature**
**Current:** Users authenticate but never explicitly set their career goal
**Need:** Goal setting step after authentication

**Implementation:**
- New screen: `/goal-setting` or part of first-time onboarding
- Prompt: "What's your career goal?" with textarea
- Examples: "Become a Senior Backend Engineer", "Transition to DevOps", "Learn Cloud Security"
- Store in user profile (new table: user_goals)
- Show goal on dashboard/frontier header

**Flow:**
```
Sign Up → Set Goal → Pre-calibration Onboarding → Calibration → Results → Frontier
```

**Files:**
- NEW: `careermap-ui/src/app/goal-setting/page.tsx`
- Backend: Add `user_goals` table with `user_id`, `goal_text`, `target_role_id`, `created_at`
- Update auth flow to check if goal is set

---

### 3. **Add More Roles - Some Domains Have None**
**Current Problem:** Some domains are empty when clicked
**Need:** Populate all domains with relevant roles

**Action Items:**
- Audit current domain-role mapping
- Identify empty domains
- Add 3-5 roles per domain minimum
- Focus on popular career paths

**Suggested Roles to Add:**
- **Cloud/DevOps:** Site Reliability Engineer, Platform Engineer, Cloud Architect
- **Security:** Penetration Tester, Security Analyst, Compliance Engineer
- **Data:** Data Engineer, ML Engineer, Analytics Engineer
- **Frontend:** UI Engineer, React Developer, Frontend Architect
- **Backend:** API Developer, Microservices Engineer, Database Engineer
- **Mobile:** iOS Developer, Android Developer, Mobile Architect

**Files:**
- Backend database seed scripts
- `backend/src/main/resources/data.sql` or migration scripts

---

## CRITICAL - Map Overhaul

### 1. **Increase Granularity to 100+ Nodes**
**Current:** 40-60 nodes per role
**Target:** 100+ nodes per role for true depth

**Changes Needed:**
- Update OpenAI prompt to request 100-150 nodes minimum
- Increase token limit from 8000 to 16000
- Adjust map layout algorithm to handle 100+ nodes efficiently
- Test with actual 100+ node generation

**File:** `backend/src/main/java/com/careermappro/services/OpenAIService.java:328-369`

---

### 2. **Implement Node Locking System**
**Current:** All nodes clickable anytime
**Should Be:** Locked until prerequisites completed

**Implementation:**
- Add `isLocked` computed property based on dependencies
- Check if all dependency nodes are completed
- Gray out locked nodes with lock icon
- Disable click on locked nodes
- Show tooltip: "Complete [prerequisite] first"

**Files:**
- `careermap-ui/src/app/frontier/PathView.tsx`
- `careermap-ui/src/app/frontier/page.tsx` (track completed nodes)

**Visual States:**
- **Locked:** Gray, lock icon, 50% opacity, no hover
- **Unlocked:** Full color, no lock, hover scale
- **Completed:** Checkmark, green glow, 80% opacity
- **Current/Focus:** Pulsing glow, white border

---

### 3. **Integrate EDLSG Framework Visually**
**Current:** EDLSG exists in concept but not in UI
**Need:** Show which EDLSG phase user is in for each node

**EDLSG States Per Node:**
1. **E (Explore)** - User viewing node details, reading "Why it matters"
2. **D (Decide)** - User selected node, viewing resources, planning
3. **L (Learn)** - User consuming learning resources (external links)
4. **S (Score)** - User taking PROBE/BUILD/PROVE assessment
5. **G (Grow)** - Node completed, state updated, unlocks visible

**Visual Implementation:**
- Add EDLSG progress ring around each node (5 segments)
- Highlight current segment based on node state
- Color code: Explore (blue), Decide (purple), Learn (yellow), Score (orange), Grow (green)
- Show EDLSG legend in UI
- Add "Current Phase" indicator on node details panel

**Files:**
- `careermap-ui/src/app/frontier/PathView.tsx` - Node rendering
- `careermap-ui/src/app/frontier/types.ts` - Add EDLSG state to DetailedPathNode
- Backend: Track EDLSG phase per user per node

**Questions:**
- Should we force linear EDLSG progression? (Can't Score without Learn?)
- Or allow skipping? (Jump straight to Score if confident?)

---

### 4. **Add Recenter/Jump to Start Button**
**Current:** No easy way to return to first node
**Need:** "Jump to Start" button to navigate back

**Implementation:**
- Add button next to zoom controls (bottom-left)
- Click → smooth pan animation to first node (skillNodeId 1 or lowest)
- Also zoom to optimal level (0.5 or 0.6)

**File:** `careermap-ui/src/app/frontier/PathView.tsx`

---

### 5. **Improve Visual Feedback - Glowing & States**
**Current:** Minimal visual feedback
**Need:** Clear, prominent visual states

**Implementation:**
- **Focused/Selected Node:**
  - Large pulsing glow (animate scale/opacity)
  - Thicker white border (4px)
  - Bring to front (z-index 100)

- **Locked Nodes:**
  - Gray scale filter
  - Lock icon overlay
  - Red-tinted border
  - No hover effect

- **Completed Nodes:**
  - Green checkmark icon
  - Subtle green glow
  - 80% opacity
  - Smaller scale (0.9)

- **Unlocked Nodes:**
  - Full vibrant color
  - Blue-tinted border
  - Hover scale 1.2
  - Shine effect on hover

**File:** `careermap-ui/src/app/frontier/PathView.tsx:150-185`

---

### 6. **Match UI with Calibration/Landing (Michelin Star Quality)**
**Current:** Dark purple space theme (disconnected from rest of app)
**Target:** Clean, minimal black/white/cobalt theme like calibration

**Design Changes:**
- **Background:** Black instead of purple gradient
- **Nodes:** White/cobalt borders instead of purple
- **Connections:** Thin white/gray lines (1px) instead of thick purple
- **Typography:** Match calibration font sizes and weights
- **Controls:** Minimal white buttons with cobalt hover states
- **Legend:** Clean card design matching calibration results screen
- **Animations:** Subtle, smooth (match calibration transitions)

**Reference Screens:**
- Calibration results screen (heatmap design)
- Landing page (clean hero section)

**Files:**
- `careermap-ui/src/app/frontier/PathView.tsx` - Complete redesign
- Consider creating new component: `PathViewV2.tsx`

---

### 7. **Better Map Navigation UX**
**Current:** Not intuitive, hard to explore
**Improvements Needed:**

**Minimap:**
- Small overview map in corner (100x100px)
- Shows all nodes as dots
- Current viewport highlighted
- Click to jump to area

**Search/Filter:**
- Search bar to find specific topics
- Filter by: Locked/Unlocked/Completed/Category
- Highlight matching nodes

**Path Navigation:**
- "Next Recommended" button → auto-pan to next unlocked node
- "Previous Node" / "Next Node" buttons
- Breadcrumb trail showing completed path

**Zoom Presets:**
- "View All" - 30% zoom
- "Comfortable" - 60% zoom
- "Detail" - 100% zoom

**Files:**
- `careermap-ui/src/app/frontier/PathView.tsx`
- New component: `careermap-ui/src/app/frontier/Minimap.tsx`
- New component: `careermap-ui/src/app/frontier/PathControls.tsx`

---

## CRITICAL - Onboarding

### 1. **Redesign Onboarding to Michelin Star Quality**
**Current:** Basic tutorial, doesn't match app quality
**Target:** Match calibration/landing design quality

**Design Requirements:**
- Full-screen immersive experience
- Beautiful typography and spacing
- Smooth animations
- Interactive elements
- Visual consistency with calibration

**File:** `careermap-ui/src/app/onboarding/page.tsx`

---

### 2. **Split Onboarding: Pre-Calibration + Post-Calibration**
**Current:** One tutorial after calibration
**Should Be:** Two separate onboarding flows

**Pre-Calibration (First-time users):**
- What is CareerMap?
- How does the EDLSG framework work?
- What to expect in calibration
- 3-4 screens max
- Call to action: "Start Calibration"

**Post-Calibration (After results):**
- How to use the Frontier map
- Understanding node states (locked/unlocked/completed)
- How to start sessions (PROBE/BUILD/PROVE)
- How evidence tracking works
- 4-5 screens max
- Call to action: "Explore Your Frontier"

**Implementation:**
- Split into two components
- Pre-calibration: `/onboarding/intro`
- Post-calibration: `/onboarding/frontier-guide`
- Update routing in calibration page

**Files:**
- NEW: `careermap-ui/src/app/onboarding/intro/page.tsx`
- NEW: `careermap-ui/src/app/onboarding/frontier-guide/page.tsx`
- Update: `careermap-ui/src/app/calibration/page.tsx` (routing)

---

## MEDIUM PRIORITY

### 1. **UI Consistency Pass**
- Review all screens for design consistency
- Ensure typography matches across app
- Standardize button styles
- Consistent spacing/padding
- Consistent color palette

**Screens to Review:**
- Landing page
- Calibration questions
- Calibration results
- Onboarding
- Frontier (all views)
- PROBE/BUILD/PROVE overlays
- Evidence modal

---

### 2. **Spacing & Pixel Alignment Fixes**
- Fine-tune all pixel spacing
- Ensure proper alignment
- Fix any layout jank
- Test responsive breakpoints

---

### 3. **PROBE Quiz Improvements**
- Better code editor for CODING questions
- Syntax highlighting?
- Line numbers?
- Better textarea sizing
- Test with actual generated quiz questions

---

## OPEN QUESTIONS FOR NEXT SESSION

### About EDLSG Integration:
1. **Should EDLSG be linear?** (Must complete E→D→L→S→G in order?)
2. **Or flexible?** (Jump to Score if confident, skip Learn?)
3. **Should Learn phase track external link clicks?** (Analytics?)
4. **How to show "Learn" phase if resources are external?** (Honor system?)

### About Node Locking:
1. **Should we allow "preview" of locked nodes?** (View details but can't start session?)
2. **Or completely hide locked node details?** (Just show "Locked" message?)
3. **Should locks be strict?** (All dependencies required?)
4. **Or smart?** (Alternative paths if user proves mastery another way?)

### About Map Design:
1. **Keep space theme or go minimal?** (Personal preference?)
2. **Should completed nodes shrink/fade?** (To focus on active nodes?)
3. **Should we cluster nodes by unit?** (Group 1.x nodes visually?)
4. **Linear path or tree structure?** (Current: circular rings)

### About 100+ Nodes:
1. **Will OpenAI generate 100+ nodes reliably?** (May need fallback?)
2. **Should we split into multiple API calls?** (Generate Units 1-3, then 4-6, etc.?)
3. **Performance concerns with 100+ nodes?** (React rendering, SVG lines?)
4. **Should we paginate/lazy-load nodes?** (Show 20 at a time?)

---

## ESTIMATED WORK (Next Session)

### Must Do (3-4 hours):
1. Increase to 100+ nodes ✅ (30 min - prompt update)
2. Implement node locking ✅ (1 hour)
3. Add recenter button ✅ (15 min)
4. Improve glowing/states ✅ (1 hour)
5. Fix EDLSG integration basics ✅ (1 hour)

### Should Do (2-3 hours):
1. Redesign map to match calibration UI ✅ (2 hours)
2. Add minimap ✅ (1 hour)

### Nice to Have (if time):
1. Split onboarding
2. Path navigation controls
3. Search/filter nodes

---

## SUCCESS METRICS (Next Session)

✅ Map generates 100+ nodes consistently
✅ Locked nodes visually clear and disabled
✅ EDLSG state visible on each node
✅ Map UI matches calibration/landing quality
✅ Can recenter to first node easily
✅ Visual feedback is prominent and clear
✅ User can understand their progress at a glance

---

## FINAL THOUGHTS

**EDLSG Integration is KEY.** You're right that we built the framework but haven't made it visible to users. The map should make it crystal clear:
- Where you are in the learning cycle for each node
- What phase comes next
- How progress is tracked

**100+ Nodes = True Depth.** This will make the experience feel comprehensive and professional. Combined with hierarchical numbering (1.1.1.1), it'll truly feel like a textbook curriculum.

**UI Consistency = Michelin Star.** The map should feel like a natural extension of calibration, not a separate product. Clean, minimal, focused.

**Node Locking = Progression.** Without it, the map is just a menu. With it, it becomes a guided journey.

---

## FUTURE PLANNING - Business & Growth

### Product Strategy Beyond Tech
**Think About (Not High Priority, But Important):**

**Monetization:**
- Freemium model? (Free basic path, premium for AI coaching/advanced features?)
- B2B enterprise? (Company licenses for employee upskilling?)
- Certification partnerships? (Issue verified skill badges?)
- Premium content? (Expert-created courses?)

**Marketing & Growth:**
- Who is the target user? (Students? Career switchers? Junior devs?)
- How do users discover CareerMap? (SEO? Content marketing? Reddit/HN?)
- What's the competitive advantage? (AI-personalized vs Udemy/Coursera?)
- Social proof? (Success stories, testimonials, case studies?)

**Content & Quality:**
- How to ensure learning resources stay updated?
- How to verify AI-generated paths are accurate?
- Expert review process?
- Community contributions? (User-submitted resources?)

**Partnerships:**
- Integration with coding platforms? (LeetCode, CodeSignal, GitHub?)
- Bootcamp partnerships? (Feed students into CareerMap post-graduation?)
- Employer partnerships? (Companies looking for upskilled talent?)

**Legal & Compliance:**
- Privacy policy for user data
- Terms of service
- GDPR compliance (if targeting EU)
- Content licensing (for curated resources)

**User Retention:**
- What keeps users coming back daily/weekly?
- Gamification? (Streaks, achievements, leaderboards?)
- Social features? (Study groups, peer accountability?)
- Progress tracking & reminders?

**Data & Analytics:**
- What metrics matter? (Completion rates? Time to mastery? User NPS?)
- How to measure success of learning paths?
- A/B testing framework?

**These are strategic questions to discuss when the product is stable.**
**Document for future brainstorming sessions.**

---

## TOMORROW'S SESSION CHECKLIST

### Must Complete (Critical):
- [ ] Fix explore mode bug (can't select role)
- [ ] Add user goal setting feature
- [ ] Add more roles to empty domains
- [ ] Increase to 100+ nodes per path
- [ ] Implement node locking system
- [ ] Add recenter button
- [ ] Improve visual feedback (glowing, states)

### Should Complete (High Priority):
- [ ] Integrate EDLSG framework visually
- [ ] Redesign map to match calibration UI
- [ ] Split onboarding (pre/post calibration)

### Nice to Have (If Time):
- [ ] Add minimap
- [ ] Path navigation controls
- [ ] Search/filter nodes
- [ ] UI consistency pass

---

**Ready for tomorrow's session! 🚀**
**Focus: Fix bugs, add roles/goals, 100+ nodes, node locking, EDLSG integration**
