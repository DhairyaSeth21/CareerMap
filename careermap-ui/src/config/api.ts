// API Configuration
// Uses environment variable for production, falls back to localhost for development
// Note: NEXT_PUBLIC_API_URL must be set at build time for Railway deployment
export const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

console.log('API_URL configured as:', API_URL);

export const API_ENDPOINTS = {
  auth: {
    login: `${API_URL}/api/v1/auth/login`,
    register: `${API_URL}/api/v1/auth/register`,
  },
  calibration: {
    submit: `${API_URL}/api/calibration/submit`,
  },
  coreLoop: {
    generatePath: `${API_URL}/api/core-loop/generate-detailed-path`,
    getPath: `${API_URL}/api/core-loop/path`,
  },
  sessions: {
    propose: `${API_URL}/api/sessions/propose-probe`,
    start: `${API_URL}/api/sessions/start`,
    submit: `${API_URL}/api/sessions/submit-results`,
  },
  ai: {
    explain: `${API_URL}/api/ai/explain`,
  },
  frontier: {
    getDomains: `${API_URL}/api/frontier/domains`,
    getRoles: `${API_URL}/api/frontier/roles`,
    getPath: `${API_URL}/api/frontier/path`,
  },
};
