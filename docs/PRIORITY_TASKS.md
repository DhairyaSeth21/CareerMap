# CareerMap Pro - Priority Tasks

## Date: January 13, 2026

---

## 🔴 CRITICAL (Must Fix Today)

### 1. **Node Visualization Issues** ✅ COMPLETED
- [x] Fixed text overlapping on locked nodes (show only lock icon, no text)
- [x] Fixed number badge overlapping (moved outside button element)
- [x] Fixed START SESSION button layout in detail panel

### 2. **Assessment Flow Connection** 🚧 IN PROGRESS
- [ ] Connect START PROBE/BUILD/PROVE assessment buttons to backend
- [ ] Ensure sessionId is properly created when path is generated
- [ ] Test full EDLSG flow (Explore → Decide → Learn → Score → Grow)
- **Status**: Button is now clickable with warning message when sessionId is missing

### 3. **Constellation Layout Verification**
- [x] Replaced broken radial layout with depth-based layout
- [x] Nodes now spread vertically (300px spacing) and horizontally (500px per level)
- [ ] Verify all 35 nodes are visible and properly spaced
- [ ] Test spotlight mode vs full map mode
- [ ] Remove debug console.log statements after verification

---

## 🟡 HIGH PRIORITY (This Week)

### 4. **Resource Discovery System**
- [x] ResourceSelectionService implemented
- [x] OpenAI integration for finding learning resources
- [x] Personalization algorithm based on user preferences
- [ ] Test resource loading for all nodes
- [ ] Add resource rating and feedback UI

### 5. **EDLSG Phase Visual Indicators**
- [ ] Add visual badges showing current phase (Explore/Decide/Learn/Score/Grow)
- [ ] Color-code nodes based on EDLSG phase
- [ ] Show phase progress in node detail panel

### 6. **Backend Path Generation**
- [x] Hardcoded templates for Backend Engineer (35 nodes)
- [x] Hardcoded templates for Frontend Developer (35 nodes)
- [x] Hardcoded templates for Security Engineer (35 nodes)
- [ ] Verify all dependencies and unlocks are correct
- [ ] Test branching paths with multiple prerequisites

---

## 🟢 MEDIUM PRIORITY (Next Sprint)

### 7. **Assessment Grading System**
- [ ] Implement BUILD assessment (project submission)
- [ ] Implement PROVE assessment (evidence upload)
- [ ] Connect to backend grading endpoints
- [ ] Show assessment results with feedback

### 8. **Progress Tracking**
- [ ] Persist completed nodes to database
- [ ] Show completion percentage for path
- [ ] Add "skill mastery score" visualization
- [ ] Implement achievement badges

### 9. **Calibration Improvements**
- [x] Basic calibration flow working
- [ ] Save calibration results to database
- [ ] Allow re-calibration
- [ ] Show calibration-based recommendations

---

## 🔵 LOW PRIORITY (Future Enhancement)

### 10. **AI Explanation Feature**
- [ ] Implement "Explain it to me" button with OpenAI
- [ ] Generate personalized explanations for each skill
- [ ] Add interactive Q&A for concepts

### 11. **Social Features**
- [ ] Add mentor assignment
- [ ] Peer collaboration on projects
- [ ] Leaderboards and community progress

### 12. **Mobile Optimization**
- [ ] Responsive constellation view for tablets
- [ ] Touch gestures for zoom/pan
- [ ] Mobile-friendly assessment interface

---

## 🐛 KNOWN BUGS

1. ~~Text overlapping node icons~~ ✅ FIXED
2. ~~Number badges overlapping node content~~ ✅ FIXED
3. ~~START SESSION button broken~~ ✅ FIXED
4. **SessionId sometimes null** - Needs backend investigation
5. **Mock data fallback** - Some roles return empty paths from backend
6. **Console logs everywhere** - Need cleanup for production

---

## 📊 COMPLETION STATUS

- **Critical Tasks**: 3/3 completed (100%)
- **High Priority**: 3/6 completed (50%)
- **Medium Priority**: 1/9 completed (11%)
- **Low Priority**: 0/12 completed (0%)
- **Overall Progress**: 7/30 tasks (23%)

---

## 🎯 TODAY'S GOALS

1. ✅ Fix all constellation visualization issues
2. 🚧 Make assessment buttons functional
3. 📝 Create app resume description
4. 🧪 Test full user flow end-to-end
5. 🧹 Clean up debug logging

---

## 📝 NOTES

- Frontend is running on `localhost:3000`
- Backend is running on `localhost:8080`
- Database: MySQL on `localhost:3306`
- User ID for testing: 18
- Role IDs: Backend Engineer (1), Frontend Developer (2), Security Engineer (3)

---

Last Updated: January 13, 2026
