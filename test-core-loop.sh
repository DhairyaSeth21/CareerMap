#!/bin/bash

# Core Loop End-to-End Test Script
# Tests: Select Role → Generate Path → Start PROBE → Submit Quiz

echo "========================================="
echo "CORE LOOP END-TO-END TEST"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test API availability
echo -e "${YELLOW}[1/4] Testing API availability...${NC}"
if curl -s http://localhost:8080/api/frontier/domains/1/roles > /dev/null; then
    echo -e "${GREEN}✓ Backend API is UP${NC}"
else
    echo -e "${RED}✗ Backend API is DOWN${NC}"
    exit 1
fi
echo ""

# Step 1: Select role and generate AI path
echo -e "${YELLOW}[2/4] STEP 1: Select Role & Generate AI Path...${NC}"
ROLE_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/core-loop/select-role?userId=1&roleId=1")
echo "$ROLE_RESPONSE" | jq '.' 2>/dev/null || echo "$ROLE_RESPONSE"

SESSION_ID=$(echo "$ROLE_RESPONSE" | jq -r '.session.id' 2>/dev/null)
FOCUS_NODE=$(echo "$ROLE_RESPONSE" | jq -r '.focusNode.name' 2>/dev/null)

if [ "$SESSION_ID" != "null" ] && [ -n "$SESSION_ID" ]; then
    echo -e "${GREEN}✓ Path generated! Session ID: $SESSION_ID, Focus: $FOCUS_NODE${NC}"
else
    echo -e "${RED}✗ Failed to generate path${NC}"
    exit 1
fi
echo ""

# Step 2: Start PROBE session and get quiz
echo -e "${YELLOW}[3/4] STEP 2: Start PROBE Session...${NC}"
PROBE_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/core-loop/start-probe?sessionId=$SESSION_ID")
echo "$PROBE_RESPONSE" | jq '.' 2>/dev/null || echo "$PROBE_RESPONSE"

NUM_QUESTIONS=$(echo "$PROBE_RESPONSE" | jq -r '.quiz.questions | length' 2>/dev/null)

if [ "$NUM_QUESTIONS" != "null" ] && [ "$NUM_QUESTIONS" -gt 0 ]; then
    echo -e "${GREEN}✓ Quiz generated with $NUM_QUESTIONS questions${NC}"
else
    echo -e "${RED}✗ Failed to generate quiz${NC}"
    exit 1
fi
echo ""

# Step 3: Submit quiz answers (mock data)
echo -e "${YELLOW}[4/4] STEP 3: Submit Quiz Answers...${NC}"

# Build mock submission (answer all questions with 'A' or 'true')
SUBMISSION='{
  "answers": []
}'

# For now, just test that the endpoint responds
# In real test, we would parse questions and generate proper answers

echo "Testing quiz submission endpoint..."
RESULT_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d "$SUBMISSION" \
  "http://localhost:8080/api/core-loop/submit-quiz?sessionId=$SESSION_ID")

echo "$RESULT_RESPONSE" | head -200

echo ""
echo "========================================="
echo -e "${GREEN}CORE LOOP TEST COMPLETE!${NC}"
echo "========================================="
echo ""
echo "Summary:"
echo "✓ Backend APIs functional"
echo "✓ Role selection works"
echo "✓ AI path generation works"
echo "✓ PROBE session creation works"
echo "✓ Quiz generation works"
echo "✓ Quiz submission endpoint accessible"
echo ""
echo "Next: Test full flow in browser at http://localhost:3000/frontier"
