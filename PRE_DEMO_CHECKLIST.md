# Pre-Demo Checklist for CareerMap MVP

Before sending to your professor or conducting a demo, verify all items below:

## ✅ Environment Setup

- [ ] **MySQL is running**
  ```bash
  mysql -u root -p  # Should connect successfully
  ```

- [ ] **Backend is running on port 8080**
  ```bash
  curl http://localhost:8080/api/frontier/domains
  # Should return JSON with 6 domains
  ```

- [ ] **Frontend is running on port 3000**
  ```bash
  curl http://localhost:3000
  # Should return HTML with CareerMap title
  ```

- [ ] **OpenAI API Key configured (for AI explanations)**
  ```bash
  echo $OPENAI_API_KEY
  # Should show: sk-...
  # If not set: export OPENAI_API_KEY=your-key-here
  ```

---

## ✅ Quick Functional Test (10 Minutes)

### 1. User Registration
- [ ] Navigate to http://localhost:3000
- [ ] Click "Get Started"
- [ ] Fill signup form (use unique email each time)
- [ ] Successfully creates account
- [ ] Redirects to calibration page

### 2. Calibration
- [ ] 12 questions load correctly
- [ ] Can select answers
- [ ] Progress bar updates
- [ ] Submit button works
- [ ] Results page shows:
  - [ ] Domain scores (Backend, Frontend, Cloud)
  - [ ] Heatmap visualization
  - [ ] Recommended path
  - [ ] "Proceed to Frontier" button

### 3. Frontier Navigation
- [ ] Domain cards display (6 total)
- [ ] Click Backend Engineering
- [ ] Role cards appear (4 roles)
- [ ] Click Backend Engineer
- [ ] Path generates and displays as graph
- [ ] Can pan graph by dragging
- [ ] Can zoom with scroll wheel

### 4. Node Interaction
- [ ] Click first node in path
- [ ] Detail panel slides in from right
- [ ] Shows node information
- [ ] "START SESSION" button visible
- [ ] "✨ Explain it to me (AI)" button visible

### 5. AI Explanation
- [ ] Click "AI Explanation" button
- [ ] Modal appears with loading spinner
- [ ] Explanation generates within 30 seconds
- [ ] Shows structured content:
  - [ ] Core Concept
  - [ ] Perfect Analogy
  - [ ] Real-World Use Case
  - [ ] Key Technical Details
  - [ ] Common Pitfalls
  - [ ] Mastery Checklist
- [ ] Can close modal

### 6. PROBE Assessment
- [ ] Click "START SESSION"
- [ ] Quiz modal appears
- [ ] Questions display correctly:
  - [ ] Multiple choice options clickable
  - [ ] True/false buttons work
  - [ ] **Code completion has text input field** ⭐
- [ ] Can answer and proceed through questions
- [ ] Submit quiz
- [ ] Results show score and feedback
- [ ] Can return to frontier

### 7. UI Polish
- [ ] No console errors in browser DevTools (F12)
- [ ] Animations are smooth
- [ ] Command bar is compact (not blocking graph)
- [ ] All buttons are accessible
- [ ] Loading states work properly
- [ ] Error messages are user-friendly

---

## ✅ Data Verification

- [ ] **Users table has test accounts**
  ```sql
  SELECT COUNT(*) FROM users;
  # Should show at least 1
  ```

- [ ] **Domains loaded**
  ```sql
  SELECT COUNT(*) FROM domains;
  # Should show 6
  ```

- [ ] **Sessions created during PROBE**
  ```sql
  SELECT * FROM sessions ORDER BY created_at DESC LIMIT 5;
  # Should show recent sessions
  ```

---

## ✅ Documentation Ready

- [ ] **TEST_PLAN.md** exists and is complete
- [ ] **SETUP_GUIDE.md** exists with clear instructions
- [ ] **PRE_DEMO_CHECKLIST.md** (this file) reviewed
- [ ] README.md updated (if applicable)

---

## ✅ Known Issues Acknowledged

Be prepared to explain these limitations:

1. **Learning Resources**: Currently placeholder data
2. **Quiz Questions**: Mock data, not domain-specific
3. **OpenAI Dependency**: AI explanations require API key
4. **Local Only**: Not deployed/hosted
5. **Session Expiration**: 24-hour TTL

---

## 🚨 Critical Issues to Fix Before Demo

If any of these are broken, fix immediately:

- ❌ **Backend crashes on startup**
- ❌ **Frontend shows blank page**
- ❌ **Cannot create account (signup fails)**
- ❌ **Calibration questions don't load**
- ❌ **Graph doesn't render on frontier**
- ❌ **Code completion input missing in PROBE**
- ❌ **AI explanation throws error (even with API key)**

---

## ✅ Performance Check

- [ ] **Backend response time < 500ms** for API calls
- [ ] **Frontend initial load < 3 seconds**
- [ ] **Graph renders within 2 seconds**
- [ ] **AI explanation generates < 30 seconds**
- [ ] **No memory leaks** (check browser Task Manager)

---

## ✅ Cross-Browser Testing (Optional)

Test in at least one browser:
- [ ] Chrome ✅ (Recommended)
- [ ] Firefox
- [ ] Safari
- [ ] Edge

---

## 📋 Before Sending to Professor

### Required Files/Info to Share:

1. **SETUP_GUIDE.md** - Complete setup instructions
2. **TEST_PLAN.md** - Detailed test scenarios
3. **Database credentials**:
   - Host: localhost
   - User: root
   - Password: Ams110513200
   - Database: careermap
4. **OpenAI API Key** - Provide your key or instructions to get one
5. **Access URLs**:
   - Frontend: http://localhost:3000
   - Backend: http://localhost:8080

### Optional:
- Video walkthrough (Loom, YouTube unlisted)
- Screenshots of key features
- GitHub repository link (if code is shared)

---

## 🎯 Demo Script Outline

If presenting live, follow this 10-minute flow:

**Slide 1: Problem** (30 sec)
- Engineers don't know what to learn next
- Too many courses, no clear path
- No validation of actual skill

**Slide 2: Solution - CareerMap** (30 sec)
- Evidence-based learning platform
- Personalized paths based on calibration
- Proof required to progress

**Slide 3: Demo - Calibration** (2 min)
- Show scenario-based questions
- Submit and show results

**Slide 4: Demo - Frontier** (3 min)
- Navigate domain → role → path
- Show interactive graph
- Explain dependencies and progression

**Slide 5: Demo - AI Explanation** (2 min)
- Click node, generate explanation
- Show structured format

**Slide 6: Demo - PROBE Assessment** (2 min)
- Start assessment
- Show code completion input
- Submit and show results

**Slide 7: Conclusion** (30 sec)
- Recap: Calibration → Personalized Path → Validation
- Next steps: Real content, deployment, user testing

---

## ✅ Final Checklist

Before hitting send:

- [ ] All servers running
- [ ] Quick test completed successfully
- [ ] Documentation is clear and complete
- [ ] Known issues documented
- [ ] Contact info included for questions
- [ ] **OpenAI API key provided or noted as required**
- [ ] Test account credentials included (or create fresh one for demo)

---

## 🎉 You're Ready!

If all boxes are checked, your MVP is ready for third-party testing.

**Estimated Setup Time for Professor:** 5-10 minutes
**Estimated Test Time:** 20-30 minutes
**Total Time Investment:** ~30-40 minutes

Good luck with your demo! 🚀

---

**Last Updated:** January 24, 2026
**Status:** ✅ Production Ready (MVP)
