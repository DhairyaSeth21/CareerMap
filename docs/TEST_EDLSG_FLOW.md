# EDLSG End-to-End Test Script

## Prerequisites
1. Backend running on `http://localhost:8080`
2. Frontend running on `http://localhost:3000`
3. MySQL database with EDLSG schema (V2 + V3 migrations)
4. OpenAI API key configured

## Test Flow

### 1. Login
- Navigate to `http://localhost:3000/login`
- Login with existing user (e.g., dhairya224@test.com)
- User ID should be stored in localStorage

### 2. Navigate to Frontier
- Click "Frontier" in navigation bar
- URL: `http://localhost:3000/frontier`

**Expected**:
- Page loads with frontier visualization
- Shows "Security Engineer" role
- Displays 7 skill cards in frontier preview
- Shows highlighted skill (should be "Linux" based on unlock potential)
- Recommended action button visible (e.g., "Assess Linux ~10 min")

### 3. View Skill States
**Expected to see**:
- INFERRED skills (yellow/dotted): Python, Penetration Testing
- UNSEEN skills (gray/locked): Linux, Security, Network Security, Cryptography, Compliance
- Each card shows:
  - Status icon (🔒 for UNSEEN, • for INFERRED)
  - Skill name
  - Status label
  - Confidence % (if > 0)
  - Demand weight

### 4. Submit New Evidence
- Click "+ Add Evidence" button
- Modal opens

**Fill out form**:
- Evidence Type: "Certification"
- Description: "Completed Linux System Administration certification covering command line, bash scripting, user management, file permissions, and network configuration. Passed with 92% score."
- Source URL: "https://certification.linux.org/12345"
- Click "Submit Evidence"

**Expected**:
- Modal shows "Processing..."
- Backend calls OpenAI API
- Skills extracted: Linux, Bash (if exists), Networking
- Modal closes
- Frontier automatically refreshes

### 5. Verify State Updates
**After evidence submission**:
- Linux should transition: UNSEEN → INFERRED
- Confidence should update to ~0.9
- Frontier re-renders with updated states
- Highlighted skill may change (based on new scores)
- Recommended action may change

### 6. Check Backend Logs
```bash
tail -f /tmp/backend.log
```

**Should show**:
- POST /api/v2/evidence request
- OpenAI extraction call
- Skill normalization
- State transitions logged
- Frontier recomputation

### 7. Verify Database
```sql
-- Check evidence saved
SELECT * FROM evidence ORDER BY created_at DESC LIMIT 1;

-- Check skill links created
SELECT esl.*, sn.canonical_name
FROM evidence_skill_links esl
JOIN skill_nodes sn ON sn.skill_node_id = esl.skill_id
WHERE esl.evidence_id = (SELECT MAX(evidence_id) FROM evidence);

-- Check user state updated
SELECT uss.*, sn.canonical_name
FROM user_skill_states uss
JOIN skill_nodes sn ON sn.skill_node_id = uss.skill_id
WHERE uss.user_id = 18 AND sn.canonical_name = 'Linux';
```

**Expected**:
- Evidence row with rawText, type=CERT
- 2-3 evidence_skill_link rows with support > 0.7
- user_skill_states row for Linux: status=INFERRED, confidence~0.9

### 8. Test Recommended Action
- Click the recommended action button (e.g., "Assess Linux")

**Expected behavior**:
- PROBE action → Redirects to `/assessment?skillId=20`
- BUILD action → Shows alert "Build tasks coming soon!"
- APPLY action → Redirects to `/opportunities`

### 9. Submit Multiple Evidence Types
Test different evidence types to verify extraction works for all:

**PROJECT**:
```
Built a security monitoring dashboard using Python, Flask, and PostgreSQL.
Implemented real-time threat detection and alerting system.
```

**QUIZ**:
```
Cryptography Fundamentals Quiz - Score: 88/100.
Topics: AES encryption, RSA, hashing algorithms, digital signatures.
```

**REPO**:
```
Open-source penetration testing toolkit repository.
Technologies: Python, Scapy, network scanning, vulnerability assessment.
URL: https://github.com/user/pentest-toolkit
```

### 10. Verify Unlock Potential Algorithm
**Test scenario**: User has Linux INFERRED
- Linux is prerequisite for Network Security (HARD: 0.9)
- Network Security is prerequisite for Penetration Testing (HARD: 0.95)
- Network Security is prerequisite for Security (HARD: 0.85)

**Expected decision engine behavior**:
- If user proves Linux → Network Security becomes ACTIVE
- This unlocks downstream: Penetration Testing, Security
- Decision engine should calculate high unlock potential for Linux

**Verify**:
1. Check current highlighted skill
2. Submit evidence that proves Linux (CERT with high confidence)
3. Fetch frontier again
4. Network Security should now show as ACTIVE or have higher score
5. Recommended action should shift to next gating skill

## Success Criteria

✅ **Phase 6: Frontend Complete**
- [x] Frontier page renders without errors
- [x] Skill cards display with correct state colors
- [x] Highlighted skill shown prominently
- [x] Recommended action button functional
- [x] Evidence modal opens and closes
- [x] Form validation works

✅ **Phase 7: Evidence Integration Complete**
- [x] Evidence submission sends POST to /api/v2/evidence
- [x] OpenAI extracts skills with support/confidence
- [x] Skills normalized to canonical names
- [x] State transitions triggered
- [x] Frontier automatically refreshes after submission
- [x] UI shows loading state during processing

✅ **Phase 8: End-to-End Flow Working**
- [x] Login → Frontier → View skills → Submit evidence → State updates → Frontier refreshes
- [x] Decision engine selects correct next action
- [x] Prerequisite graph influences recommendations
- [x] All skill states render correctly (UNSEEN/INFERRED/ACTIVE/PROVED/STALE)

## Known Issues / Future Enhancements

1. **Performance**: Frontier computation could be cached (currently recomputes on every request)
2. **Skill Graph Visualization**: Current grid layout works but could be enhanced with actual graph visualization showing prerequisite edges
3. **Real-time Updates**: Could add WebSocket for live frontier updates
4. **Action Execution**: BUILD actions not yet implemented (needs mini-task generator)
5. **Evidence History**: No UI to view past evidence submissions
6. **Confidence Decay**: Stale detection implemented but no UI indicator for when skills will become stale

## Performance Benchmarks

**Expected response times**:
- GET /api/v2/frontier: < 500ms (with 70 skills, 42 edges)
- POST /api/v2/evidence (with OpenAI): 2-5 seconds
- Frontend render: < 100ms (7-30 skill cards)

**Scalability**:
- Current implementation handles 70 skills, 42 prerequisite edges efficiently
- Decision engine complexity: O(n × m) where n = frontier size, m = avg prerequisites per skill
- Recommended max: 200 skills, 500 edges before optimization needed
