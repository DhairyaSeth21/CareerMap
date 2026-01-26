/**
 * INTENSE CALIBRATION QUESTIONS
 *
 * These are NOT textbook trivia or guessable questions.
 * These require deep technical thinking, trade-off analysis, and real-world judgment.
 *
 * Design Principles:
 * - No "obvious right answer" - all options are defensible
 * - Require understanding WHY, not just WHAT
 * - Test decision-making under constraints
 * - Expose depth of experience vs surface knowledge
 */

export interface IntenseQuestion {
  questionId: number;
  questionText: string;
  context?: string; // Optional code snippet or scenario
  options: string[];
  domain: string;
  difficulty: 'foundational' | 'intermediate' | 'advanced';
  tags: string[];
}

export const intenseCalibrationQuestions: IntenseQuestion[] = [
  // === BACKEND ENGINEERING ===
  {
    questionId: 1,
    questionText: 'Your API response time degrades from 50ms to 500ms when concurrent users increase from 100 to 1000. Database queries remain fast (10ms avg). What is the MOST LIKELY bottleneck?',
    domain: 'Backend Engineering',
    difficulty: 'advanced',
    tags: ['performance', 'debugging', 'system-design'],
    options: [
      'Thread pool exhaustion - application server running out of threads to handle concurrent requests',
      'Database connection pool exhaustion - connections being held too long',
      'Memory pressure causing garbage collection pauses',
      'Network bandwidth saturation',
    ],
  },

  {
    questionId: 2,
    questionText: 'You must design a payment processing system that handles 50K transactions/hour. A transaction must NEVER be processed twice, even if the request is retried. Which strategy is MOST reliable?',
    domain: 'Backend Engineering',
    difficulty: 'advanced',
    tags: ['distributed-systems', 'idempotency', 'reliability'],
    options: [
      'Generate idempotency key from request payload hash + timestamp, store processed keys in Redis with TTL',
      'Client provides unique idempotency key with each request, deduplicate using database unique constraint',
      'Use distributed lock (e.g., Redis SETNX) for each transaction ID',
      'Process transactions synchronously in a single-threaded queue',
    ],
  },

  {
    questionId: 3,
    questionText: 'Two microservices need to stay synchronized. Service A updates inventory, Service B handles orders. An order should fail if inventory is insufficient. What pattern handles this BEST at scale?',
    domain: 'Backend Engineering',
    difficulty: 'advanced',
    tags: ['microservices', 'distributed-transactions', 'architecture'],
    options: [
      'Saga pattern with compensating transactions - Order service reserves inventory, then commits or rolls back',
      'Two-phase commit protocol across both services',
      'Service B calls Service A synchronously to check and decrement inventory atomically',
      'Event sourcing - both services listen to inventory events and maintain their own state',
    ],
  },

  {
    questionId: 4,
    questionText: 'Your database has a users table (10M rows) and posts table (100M rows). Query: "Find users who posted in the last 7 days" takes 30 seconds. What optimization gives MAXIMUM impact?',
    domain: 'Backend Engineering',
    difficulty: 'intermediate',
    tags: ['database', 'sql', 'performance'],
    options: [
      'Add index on posts.created_at, then use EXISTS subquery instead of JOIN',
      'Denormalize and add last_post_date column to users table, update via trigger',
      'Partition posts table by date range',
      'Use materialized view that refreshes hourly',
    ],
  },

  {
    questionId: 5,
    questionText: 'You discover this code in a critical payment flow. What is the PRIMARY risk?',
    context: `
async function processPayment(orderId) {
  const order = await Order.findById(orderId);
  const payment = await stripe.charge(order.amount);

  order.status = 'paid';
  order.paymentId = payment.id;
  await order.save();

  return { success: true };
}
    `,
    domain: 'Backend Engineering',
    difficulty: 'advanced',
    tags: ['reliability', 'transactions', 'error-handling'],
    options: [
      'No error handling - if order.save() fails after charging, money is taken but order remains unpaid',
      'Race condition if two payments process same order simultaneously',
      'Blocking await prevents concurrent payment processing',
      'Missing input validation on orderId',
    ],
  },

  {
    questionId: 6,
    questionText: 'You need to implement rate limiting for an API: 1000 requests/hour per user. The system has 100K active users and runs on 10 servers. What approach is MOST efficient?',
    domain: 'Backend Engineering',
    difficulty: 'advanced',
    tags: ['rate-limiting', 'distributed-systems', 'scalability'],
    options: [
      'Sliding window counter in Redis with user-specific keys and EXPIRE',
      'Token bucket algorithm with in-memory counter per server (10K req/hour per server)',
      'Database row with request_count and reset_time columns per user',
      'Fixed window counter that resets every hour (accept burst)',
    ],
  },

  // === FRONTEND ENGINEERING ===
  {
    questionId: 7,
    questionText: 'Your React app rerenders excessively. Profiler shows parent rerenders trigger 50+ child component rerenders, but props haven\'t changed. What is the MOST LIKELY cause?',
    domain: 'Frontend Engineering',
    difficulty: 'intermediate',
    tags: ['react', 'performance', 'debugging'],
    options: [
      'Parent passes inline object/array props or inline functions, creating new references every render',
      'Missing React.memo on child components',
      'setState called in render method causing infinite loop',
      'Too many components in component tree',
    ],
  },

  {
    questionId: 8,
    questionText: 'You must render a table with 100K rows efficiently. User can scroll, sort, and filter. Which strategy is BEST?',
    domain: 'Frontend Engineering',
    difficulty: 'advanced',
    tags: ['performance', 'virtualization', 'ux'],
    options: [
      'Virtual scrolling (render only visible rows + buffer) + pagination + backend sorting/filtering',
      'Render all 100K rows but hide offscreen rows with CSS (display: none)',
      'Use Web Workers to render rows in background thread',
      'Paginate into 1000 pages of 100 rows each',
    ],
  },

  {
    questionId: 9,
    questionText: 'This code causes a memory leak in a long-running SPA. Why?',
    context: `
useEffect(() => {
  const interval = setInterval(() => {
    fetchDashboardData().then(data => setData(data));
  }, 5000);
}, []);
    `,
    domain: 'Frontend Engineering',
    difficulty: 'intermediate',
    tags: ['react', 'memory-leaks', 'cleanup'],
    options: [
      'No cleanup function to clearInterval - interval continues after component unmounts',
      'fetchDashboardData creates new Promise objects repeatedly',
      'setData called with stale data reference',
      'Empty dependency array prevents garbage collection',
    ],
  },

  // === CLOUD & DEVOPS ===
  {
    questionId: 10,
    questionText: 'Your Kubernetes pods are being OOMKilled during traffic spikes, but average memory usage is 40%. Horizontal Pod Autoscaler (HPA) doesn\'t help. What is the BEST solution?',
    domain: 'Cloud & DevOps',
    difficulty: 'advanced',
    tags: ['kubernetes', 'memory', 'scaling'],
    options: [
      'Increase memory limits while keeping requests low - allows burst capacity for spikes',
      'Set memory requests equal to limits (guaranteed QoS class)',
      'Use Vertical Pod Autoscaler (VPA) instead of HPA',
      'Enable memory overcommit on nodes',
    ],
  },

  {
    questionId: 11,
    questionText: 'Your CI/CD pipeline deploys 20 times/day. Deployments succeed but occasionally cause 5-10 minutes of elevated error rates. What deployment strategy BEST reduces risk?',
    domain: 'Cloud & DevOps',
    difficulty: 'advanced',
    tags: ['deployment', 'reliability', 'cicd'],
    options: [
      'Canary deployment with automated rollback - route 5% traffic to new version, monitor metrics, gradually increase or rollback',
      'Blue-green deployment with instant cutover',
      'Rolling update with readiness probes',
      'Deploy during low-traffic hours only',
    ],
  },

  {
    questionId: 12,
    questionText: 'A critical service has this SLA: 99.9% uptime (43 min downtime/month allowed). Last month: 6 incidents totaling 38 minutes downtime. You have budget for ONE improvement. What maximizes reliability?',
    domain: 'Cloud & DevOps',
    difficulty: 'advanced',
    tags: ['sre', 'reliability', 'incident-management'],
    options: [
      'Implement automated canary analysis with immediate rollback on error spike',
      'Add more monitoring and alerting',
      'Increase server capacity to handle 3x normal load',
      'Implement chaos engineering to find weaknesses',
    ],
  },
];

/**
 * Get 12 random questions with balanced difficulty
 */
export function getCalibrationQuestions(): IntenseQuestion[] {
  // Ensure mix of difficulties
  const foundational = intenseCalibrationQuestions.filter(q => q.difficulty === 'foundational');
  const intermediate = intenseCalibrationQuestions.filter(q => q.difficulty === 'intermediate');
  const advanced = intenseCalibrationQuestions.filter(q => q.difficulty === 'advanced');

  // Pick: 0 foundational (none exist now), 4 intermediate, 8 advanced
  const selected = [
    ...shuffle(intermediate).slice(0, 4),
    ...shuffle(advanced).slice(0, 8),
  ];

  return shuffle(selected).slice(0, 12);
}

function shuffle<T>(array: T[]): T[] {
  const shuffled = [...array];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
}
