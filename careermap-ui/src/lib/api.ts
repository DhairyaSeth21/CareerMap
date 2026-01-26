import axios from "axios";

const api = axios.create({
  baseURL: "https://careermap-production.up.railway.app",
});

console.log('🔥 API configured with baseURL:', api.defaults.baseURL);

export default api;
