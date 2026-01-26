# POST-CALIBRATION FLOW CONFUSION

**Date:** January 13, 2026
**Status:** CRITICAL DESIGN QUESTION

---

## ✅ What We Just Built (Priority 1 & 2):

### Priority 1: Intense Calibration Questions ✅
- **12 case-study questions** (not textbook trivia)
- Real-world scenarios: debugging, system design, architecture decisions
- Code snippets with context
- Mix of foundational, intermediate, and advanced difficulty
- Tags: performance, security, concurrency, trade-offs
- Examples:
  - "Your API is 500ms under load, 2 hours before demo - what do you investigate FIRST?"
  - "This async function has sequential awaits - what's the issue?"
  - "Deadlock between two transactions - what's the fix?"
  - "10GB file, 1GB RAM, find duplicates - what's your approach?"

### Priority 2: Post-Calibration Results Screen ✅
- **"Your Baseline"** heading
- **Domain Confidence Heatmap** - visual grid showing scores across domains
- **Strong / Gaps / Unknown** - three-column breakdown
  - Strong: Domains with 70%+ confidence (green)
  - Gaps: Domains with 30-70% confidence (yellow)
  - Unknown: Domains with <30% confidence (gray)
- **System Recommendation** - "Based on your responses, the system recommends starting with [Domain]"
- **"View Your Frontier"** CTA button

---

## 🚨 THE CONFUSION: What Happens AFTER Calibration?

### Current Flow (What We Have):
1. Landing → Auth → Calibration
2. 12 questions
3. Analysis animation
4. **Results screen** (NEW! Shows confidence heatmap, strong/gaps/unknown, recommendation)
5. User clicks **"View Your Frontier"**
6. → Redirects to `/frontier` (the existing 4-level zoom system)

### The Problem:

**When a user clicks "View Your Frontier", they land on the domain selection page.**

But this creates **zero continuity**:
- We just told them "System recommends Backend Engineering"
- But now they're looking at ALL 6 domains again
- There's no visual connection between their calibration results and what they see
- It feels like the analysis was pointless

**Why would every user always end up on the same "select domain" page?**

This breaks the promise of personalization.

---

## 🤔 Key Questions We Need To Answer:

### 1. **Should calibration skip domain selection entirely?**
- Option A: Calibration → Results → System auto-selects top domain → Show role selection
- Option B: Calibration → Results → Domain selection (but with visual hints from calibration)
- Option C: Calibration → Results → Directly into a pre-generated path for recommended domain/role

### 2. **What does "personalization" actually mean here?**
- Is the path personalized? (Yes - AI generates it based on role)
- Is the starting point personalized? (Currently NO - everyone starts at domain selection)
- Should the UI reflect their baseline? (Currently NO - Frontier looks same for everyone)

### 3. **How do we show continuity between calibration and Frontier?**
Current experience:
```
Calibration: "You're strong in Backend, weak in Security, unknown in ML"
                    ↓
Frontier: "Here are 6 domains, pick one"
                    ↓
User: "Wait, why am I picking? Didn't you just analyze me?"
```

Better experience (?):
```
Calibration: "You're strong in Backend, weak in Security, unknown in ML"
                    ↓
Frontier: Shows 6 domains BUT:
          - Backend is glowing/highlighted
          - Security shows "Gap detected"
          - ML shows "Unknown territory"
                    ↓
User: "Oh, the system is guiding me based on my results"
```

### 4. **Should returning users skip calibration?**
- First visit: Calibration → Results → Frontier
- Return visit: Straight to Frontier? Or show updated baseline?
- Do we re-calibrate periodically?

### 5. **What does "Frontier IS the app" actually mean for this flow?**
Currently:
- Calibration is a separate experience
- Then you "enter" Frontier
- Frontier is a generic domain selector

Should it be:
- Calibration IS part of Frontier (measuring your frontier)
- Results ARE your personalized Frontier view
- The domain selection IS your frontier (with visual state based on calibration)

---

## 💭 Possible Solutions (Brainstorm):

### Option 1: Weighted Domain Selection
- After calibration, user still sees all 6 domains
- BUT: Domains are sized/glowing based on calibration results
  - Strong domains: Large, bright, "Proven strength here"
  - Gap domains: Medium, yellow, "Improvement opportunity"
  - Unknown domains: Small, dim, "Uncharted territory"
- User can still pick any domain, but system guides them visually

### Option 2: Recommended Path First
- After calibration, system says: "Start with Backend Engineering → API Developer"
- Shows that path immediately
- User can say "Show me other options" to see domain selection
- Most users follow the recommendation (path of least resistance)

### Option 3: Frontier Reflects Calibration
- Frontier IS persistent
- It remembers your calibration
- When you return, it shows YOUR version of the frontier:
  - Nodes in "strong" domains are pre-unlocked or marked as "Familiar"
  - Nodes in "gap" domains are highlighted as "Priority"
  - Nodes in "unknown" domains are grayed out as "Not assessed"

### Option 4: Two-Tier System
- **First-time users:** Calibration → Guided experience → One recommended path
- **Returning users:** Skip straight to their personalized Frontier
- **Exploration mode:** Advanced users can "Reset baseline" and explore other domains

### Option 5: Progressive Disclosure
- Calibration → Results screen (current)
- Results screen has TWO buttons:
  - **"Follow Recommendation"** → Takes you straight to recommended role's path
  - **"Explore All Domains"** → Takes you to domain selection

---

## 🎯 What Needs To Be Decided:

1. **Does the system guide (strong recommendation) or let user choose (weak recommendation)?**
2. **Should Frontier look different for each user based on calibration?**
3. **What happens on return visits - do they see their previous frontier state?**
4. **How do we make the transition from calibration to Frontier feel seamless?**

---

## 📝 Next Steps:

1. **Decide on post-calibration flow** (need user input / product decision)
2. Once decided, implement:
   - Visual connection between calibration results and Frontier
   - State persistence (remember user's baseline)
   - Guided vs exploratory modes
3. Then move to Priority 3: Frontier command strip

---

## Questions for You:

**What should happen after a user sees their calibration results?**
- Should we guide them strongly (auto-select best domain)?
- Should we show all options but with visual hints (highlight recommended domain)?
- Should Frontier be personalized (their view vs everyone's view)?
- Should returning users skip calibration or see their baseline persist?

**The core tension:**
- Too much guidance → Feels restrictive, "why can't I explore?"
- Too little guidance → Feels generic, "why did I take that assessment?"

What's the right balance?
