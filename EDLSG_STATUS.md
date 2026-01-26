# EDLSG Implementation Status

## Phase 0: Schema Migration ✅ COMPLETE

### Database Tables Created
```
✓ skill_nodes (70 skills seeded)
✓ prereq_edges (7 core edges seeded)
✓ user_skill_states (11 existing states migrated)
✓ role_skill_weights (119 weights migrated)
✓ evidence (ready)
✓ evidence_skill_links (ready)
✓ path_snapshots (ready)
✓ ai_calls (ready)
```

### Migration Stats
- **70 skill nodes** created from existing skills table
- **119 role-skill weights** migrated (35 roles × avg 3-4 skills each)
- **11 user skill states** migrated (5 PROVED, 6 ACTIVE)
- **7 prerequisite edges** seeded:
  - JavaScript → React (HARD, 1.0)
  - CSS → React (HARD, 0.9)
  - React → Next.js (HARD, 0.95)
  - JavaScript → TypeScript (HARD, 0.9)
  - Linux → Docker (SOFT, 0.7)
  - Docker → Kubernetes (HARD, 0.95)
  - Git → CI/CD (SOFT, 0.6)

## Phase 1: State Machine ✅ COMPLETE

### Entities Created
- **SkillNode** (`entities/SkillNode.java`) - Canonical skill ontology with aliases, decay settings
- **PrereqEdge** (`entities/PrereqEdge.java`) - Skill prerequisites with HARD/SOFT type + strength
- **UserSkillState** (updated `entities/UserSkillState.java`) - State machine with UNSEEN/INFERRED/ACTIVE/PROVED/STALE

### Repositories Created
- **SkillNodeRepository** - Query by canonical name, domain, aliases
- **PrereqEdgeRepository** - Query prereqs by to_skill_id, from_skill_id
- **UserSkillStateRepository** - Query by user, status, find stale states

### Services Implemented
- **StateTransitionService** (`services/StateTransitionService.java`)
  - `updateStateFromEvidence()` - Deterministic state transitions based on support level + evidence type
  - `recomputeFrontierForSkill()` - Check if prereqs satisfied, transition INFERRED → ACTIVE
  - `recomputeUserFrontier()` - Recompute all frontier skills for user
  - `runDecayJob()` - Mark PROVED skills as STALE after decay period

### State Transition Rules (Deterministic)
```
RULE 1: Strong evidence (support > 0.7) + high-trust source (QUIZ/CERT)
  INFERRED/ACTIVE → PROVED
  confidence = max(confidence, support)
  evidenceScore += support * 10
  staleAt = now + decayHalfLifeDays

RULE 2: Medium evidence (support > 0.4)
  UNSEEN → INFERRED
  confidence = max(confidence, support * 0.7)
  evidenceScore += support * 5

FRONTIER RULE: Prereq satisfaction check
  HARD prereqs: ALL must have confidence >= 0.9
  SOFT prereqs: Weighted avg confidence >= 0.65
  If satisfied: INFERRED → ACTIVE
```

### Backward Compatibility
- **PathService** patched to use `evidenceScore` as proxy for old `level` field
- Existing `/api/v1/path` endpoint still works
- Old proficiencies table preserved

## Next Phases (TODO)

### Phase 2: Decision Engine (NEXT)
- [ ] Create `DecisionEngineService`
- [ ] Implement scoring: `demandWeight * unlockPotential * (1 - confidence) * feasibility`
- [ ] Select ONE next action: PROBE/BUILD/APPLY
- [ ] Create `/api/v2/frontier` endpoint

### Phase 3: Evidence Extraction
- [ ] Create `EvidenceExtractionService`
- [ ] OpenAI strict JSON schema for skill extraction
- [ ] Normalize extracted skills to SkillNode IDs via aliases
- [ ] Create `/api/v2/evidence` endpoint (POST)

### Phase 4: Prerequisite Graph Expansion
- [ ] Seed 100-200 curated edges for top 30 skills
- [ ] Create `GraphSeedingService`

### Phase 5: API v2 Endpoints
- [ ] `GET /api/v2/frontier` - Returns highlighted skill + recommended action
- [ ] `POST /api/v2/frontier/action` - Execute PROBE/BUILD/APPLY
- [ ] `POST /api/v2/evidence` - Ingest raw text, extract skills, update states

### Phase 6: Frontend - Frontier Map UI
- [ ] Create `careermap-ui/src/app/frontier/page.tsx`
- [ ] Implement layered SVG graph (not force-directed)
- [ ] Visual state encoding (PROVED/ACTIVE/LOCKED/STALE)
- [ ] Single highlighted node + action button
- [ ] Command palette (Cmd+K)

### Phase 7: Evidence Input Modal
- [ ] Create `EvidenceModal.tsx`
- [ ] Paste project/repo/experience → extract → update graph

### Phase 8: Testing & Documentation
- [ ] End-to-end smoke test script
- [ ] Update API documentation
- [ ] Frontend/backend integration tests

## Verification Commands

### Test Schema
```bash
mysql -u root -pAms110513200 careermap -e "
SELECT COUNT(*) FROM skill_nodes;
SELECT COUNT(*) FROM prereq_edges;
SELECT status, COUNT(*) FROM user_skill_states GROUP BY status;
"
```

### Test Backend Compilation
```bash
cd backend
./gradlew build -x test
# Should succeed
```

### Test State Transitions (Manual)
```java
// In StateTransitionService
stateTransitionService.updateStateFromEvidence(1, 5, 0.85, "QUIZ");
stateTransitionService.recomputeUserFrontier(1, 1);
```

### Test Prereq Query
```sql
SELECT
  from_s.canonical_name as prereq,
  to_s.canonical_name as skill,
  pe.type,
  pe.strength
FROM prereq_edges pe
JOIN skill_nodes from_s ON pe.from_skill_id = from_s.skill_node_id
JOIN skill_nodes to_s ON pe.to_skill_id = to_s.skill_node_id
WHERE to_s.canonical_name = 'React';
```

## Key Design Decisions

1. **In-place refactor** - Extended existing entities rather than greenfield
2. **Table name: `user_skill_states` (plural)** - New EDLSG table alongside old `user_skill_state` (singular)
3. **entities package** - Uses existing `com.careermappro.entities` not new `models` package
4. **evidenceScore as level proxy** - PathService backward compat without dual-write
5. **Minimal prereq graph** - 7 core edges to start, will expand to 100-200
6. **Thresholds**:
   - HARD prereq: 0.9 confidence required
   - SOFT prereq: 0.65 weighted average
   - PROVED support: 0.7 from high-trust evidence

## File Locations

### Backend
```
backend/src/main/java/com/careermappro/
├── entities/
│   ├── SkillNode.java ✅
│   ├── PrereqEdge.java ✅
│   └── UserSkillState.java ✅ (updated)
├── repositories/
│   ├── SkillNodeRepository.java ✅
│   ├── PrereqEdgeRepository.java ✅
│   └── UserSkillStateRepository.java ✅
└── services/
    └── StateTransitionService.java ✅
```

### Database
```
backend/src/main/resources/db/migration/
└── V2__edlsg_schema.sql ✅
```

## Current System State

- **Backend builds successfully** ✅
- **Database schema migrated** ✅
- **State machine implemented** ✅
- **7 prereq edges seeded** ✅
- **70 skills in graph** ✅
- **Existing app still functional** ✅ (PathService patched)

**Next: Implement Decision Engine (Phase 2)**
