'use client';

import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useRouter } from 'next/navigation';
import Starfield from './components/Starfield';
import Footer from '../components/Footer';
import { ArrowRight, Zap, Target, TrendingUp } from 'lucide-react';

/**
 * Landing Page - Michelin Star Edition
 *
 * Design Philosophy:
 * - Brutally simple. No clutter.
 * - Movement and depth through parallax
 * - Typography as hero
 * - Confidence through restraint
 * - Every pixel intentional
 */

export default function Landing() {
  const [showAuth, setShowAuth] = useState(false);

  return (
    <div className="relative min-h-screen overflow-hidden bg-black">
      {/* Deep space background - always present */}
      <Starfield density="high" />

      {/* Gradient overlay for depth */}
      <div className="fixed inset-0 bg-gradient-to-b from-transparent via-black/50 to-black pointer-events-none" />

      {/* Hero Section */}
      <section className="relative flex items-center justify-center min-h-screen px-8">
        <div className="max-w-5xl mx-auto text-center">
          {/* Logo mark - minimal */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, ease: [0.16, 1, 0.3, 1] }}
            className="mb-8"
          >
            <div className="inline-block">
              <div className="w-16 h-16 mx-auto mb-6 rounded-full bg-gradient-to-br from-purple-500 to-pink-500 blur-xl opacity-60" />
              <div className="w-12 h-12 mx-auto -mt-20 mb-6 rounded-full bg-gradient-to-br from-purple-400 to-pink-400" />
            </div>
          </motion.div>

          {/* Main headline - massive, confident */}
          <motion.h1
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.2, ease: [0.16, 1, 0.3, 1] }}
            className="text-7xl md:text-9xl font-bold tracking-tight mb-8"
          >
            <span className="block text-white">Know</span>
            <span className="block bg-gradient-to-r from-purple-300 via-pink-300 to-purple-300 bg-clip-text text-transparent">
              What You Know
            </span>
          </motion.h1>

          {/* Subhead - surgical precision */}
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.4, ease: [0.16, 1, 0.3, 1] }}
            className="text-xl md:text-2xl text-slate-400 max-w-2xl mx-auto mb-16 leading-relaxed font-light"
          >
            Most people overestimate their skills.
            <br />
            Most roadmaps are guesswork.
            <br />
            <span className="text-white font-normal">We measure. We prove. We guide.</span>
          </motion.p>

          {/* Primary CTA - unmissable */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.6, ease: [0.16, 1, 0.3, 1] }}
          >
            <button
              onClick={() => setShowAuth(true)}
              className="group relative px-12 py-5 bg-white text-black text-lg font-semibold rounded-full overflow-hidden transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:shadow-purple-500/50"
            >
              <span className="relative z-10 flex items-center gap-3">
                Start Calibration
                <ArrowRight className="w-5 h-5 transition-transform group-hover:translate-x-1" />
              </span>
              <div className="absolute inset-0 bg-gradient-to-r from-purple-500 to-pink-500 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
            </button>
          </motion.div>
        </div>

        {/* Scroll indicator */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.5, duration: 1 }}
          className="absolute bottom-12 left-1/2 -translate-x-1/2"
        >
          <motion.div
            animate={{ y: [0, 10, 0] }}
            transition={{ duration: 2, repeat: Infinity, ease: "easeInOut" }}
            className="flex flex-col items-center gap-2 text-slate-600"
          >
            <span className="text-xs uppercase tracking-wider">Scroll</span>
            <div className="w-px h-12 bg-gradient-to-b from-slate-600 to-transparent" />
          </motion.div>
        </motion.div>
      </section>

      {/* How It Works - Three brutal truths */}
      <section className="relative py-32 px-8">
        <div className="max-w-6xl mx-auto">
          <motion.h2
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            className="text-5xl md:text-6xl font-bold text-white mb-24 text-center"
          >
            Three Steps. Zero Bullshit.
          </motion.h2>

          <div className="grid md:grid-cols-3 gap-12">
            {[
              {
                icon: Target,
                number: "01",
                title: "Calibrate",
                description: "12 questions. Adaptive difficulty. We find your baseline across domains.",
              },
              {
                icon: TrendingUp,
                number: "02",
                title: "Navigate",
                description: "AI generates your path. Not generic. Not templated. Yours.",
              },
              {
                icon: Zap,
                number: "03",
                title: "Prove",
                description: "Build evidence. Real work. Real proof. Your living skill graph.",
              },
            ].map((step, index) => (
              <motion.div
                key={step.number}
                initial={{ opacity: 0, y: 40 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.8, delay: index * 0.2 }}
                className="group relative"
              >
                {/* Glow effect on hover */}
                <div className="absolute inset-0 bg-gradient-to-br from-purple-500/20 to-pink-500/20 rounded-2xl blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-500" />

                <div className="relative p-8 border border-slate-800 rounded-2xl bg-black/40 backdrop-blur-sm hover:border-slate-700 transition-all duration-300">
                  {/* Step number */}
                  <div className="text-slate-700 text-6xl font-bold mb-4">{step.number}</div>

                  {/* Icon */}
                  <step.icon className="w-12 h-12 text-purple-400 mb-6" />

                  {/* Title */}
                  <h3 className="text-2xl font-bold text-white mb-4">{step.title}</h3>

                  {/* Description */}
                  <p className="text-slate-400 text-lg leading-relaxed">{step.description}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Final CTA - reinforcement */}
      <section className="relative py-32 px-8">
        <div className="max-w-4xl mx-auto text-center">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
          >
            <h2 className="text-5xl md:text-6xl font-bold text-white mb-8">
              Stop Guessing.
              <br />
              <span className="bg-gradient-to-r from-purple-300 to-pink-300 bg-clip-text text-transparent">
                Start Proving.
              </span>
            </h2>

            <p className="text-xl text-slate-400 mb-12 max-w-2xl mx-auto">
              The system adapts to you. The path is yours. The evidence is real.
            </p>

            <button
              onClick={() => setShowAuth(true)}
              className="group relative px-12 py-5 bg-white text-black text-lg font-semibold rounded-full overflow-hidden transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:shadow-purple-500/50"
            >
              <span className="relative z-10 flex items-center gap-3">
                Begin Calibration
                <ArrowRight className="w-5 h-5 transition-transform group-hover:translate-x-1" />
              </span>
              <div className="absolute inset-0 bg-gradient-to-r from-purple-500 to-pink-500 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
            </button>
          </motion.div>
        </div>
      </section>

      {/* About the Creator Section */}
      <section className="relative py-24 px-8 border-t border-slate-900/50">
        <div className="max-w-4xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.8 }}
            className="text-center"
          >
            <div className="inline-flex items-center gap-3 mb-6 px-6 py-2 bg-purple-900/20 border border-purple-800/50 rounded-full">
              <span className="text-purple-400 text-sm font-semibold">DESIGNED & DEVELOPED BY</span>
            </div>

            <h2 className="text-4xl md:text-5xl font-bold text-white mb-4">
              Dhairya Arjun Seth
            </h2>

            <p className="text-xl text-slate-400 mb-8">
               Computer Science Student - UW Madison
            </p>

            <div className="max-w-2xl mx-auto space-y-4 text-left">
              <div className="p-6 bg-slate-900/50 border border-slate-800 rounded-xl">
                <h3 className="text-lg font-semibold text-white mb-3">About This Project</h3>
                <p className="text-slate-400 leading-relaxed">
                  Ascent was built from the ground up as a comprehensive evidence-based learning platform.
                  Every line of code, from the Spring Boot backend to the React frontend, was written with
                  the goal of solving a real problem: helping engineers know what they actually know, not what they think they know.
                </p>
              </div>

              <div className="grid md:grid-cols-2 gap-4">
                <div className="p-4 bg-slate-900/30 border border-slate-800 rounded-lg">
                  <h4 className="text-sm font-semibold text-purple-400 mb-2">Backend</h4>
                  <p className="text-slate-400 text-sm">Spring Boot 3.5, MySQL, JWT Authentication, OpenAI Integration</p>
                </div>
                <div className="p-4 bg-slate-900/30 border border-slate-800 rounded-lg">
                  <h4 className="text-sm font-semibold text-pink-400 mb-2">Frontend</h4>
                  <p className="text-slate-400 text-sm">Next.js 15, React 19, TypeScript, Framer Motion</p>
                </div>
              </div>

              <div className="text-center pt-6">
                <p className="text-slate-500 text-sm">
                • Built in 2026 •
                </p>
              </div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Footer with creator credits */}
      <Footer />

      {/* Auth Modal */}
      <AnimatePresence>
        {showAuth && <AuthModal onClose={() => setShowAuth(false)} />}
      </AnimatePresence>
    </div>
  );
}

/**
 * Auth Modal - Clean, focused, no distractions
 */
function AuthModal({ onClose }: { onClose: () => void }) {
  const [mode, setMode] = useState<'choice' | 'signup' | 'login'>('choice');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await fetch('https://careermap-production.up.railway.app/api/v1/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password }),
      });

      const data = await response.json();

      if (data.success && data.token) {
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userId', data.userId.toString());
        window.location.href = '/calibration';
      } else {
        setError(data.message || 'Signup failed');
      }
    } catch (err: any) {
      setError('Unable to connect to server');
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await fetch('https://careermap-production.up.railway.app/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (data.success && data.token) {
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userId', data.userId.toString());
        // Check if user has already completed calibration
        const calibrationComplete = localStorage.getItem('calibration_complete');
        if (calibrationComplete === 'true') {
          window.location.href = '/frontier';
        } else {
          window.location.href = '/calibration';
        }
      } else {
        setError(data.message || 'Login failed');
      }
    } catch (err: any) {
      setError('Unable to connect to server');
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/90 backdrop-blur-xl"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      onClick={onClose}
    >
      <motion.div
        className="relative w-full max-w-md bg-slate-950 border border-slate-800 rounded-2xl p-8 shadow-2xl"
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.9, opacity: 0 }}
        transition={{ type: "spring", duration: 0.5 }}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute top-6 right-6 text-slate-500 hover:text-white transition-colors"
        >
          <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        {mode === 'choice' && (
          <>
            <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-white mb-2">Begin Your Journey</h2>
              <p className="text-slate-400">Choose how you'd like to get started</p>
            </div>

            <div className="space-y-4">
              <button
                onClick={() => setMode('signup')}
                className="w-full py-4 bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold rounded-lg hover:from-purple-600 hover:to-pink-600 transition-all duration-300 shadow-lg shadow-purple-500/50"
              >
                Create Account
              </button>

              <button
                onClick={() => setMode('login')}
                className="w-full py-4 border-2 border-slate-700 text-white font-semibold rounded-lg hover:border-slate-600 hover:bg-slate-900 transition-all duration-300"
              >
                Log In
              </button>
            </div>

            <p className="mt-8 text-xs text-center text-slate-600">
              Evidence-driven skill development · Track your progress · Prove your expertise
            </p>
          </>
        )}

        {mode === 'signup' && (
          <>
            <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-white mb-2">Create Account</h2>
              <p className="text-slate-400">Join Ascent today</p>
            </div>

            <form onSubmit={handleSignup} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Full Name</label>
                <input
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                  placeholder="John Doe"
                  className="w-full px-4 py-3 bg-black border border-slate-800 rounded-lg text-white placeholder-slate-600 focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Email</label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  placeholder="you@example.com"
                  className="w-full px-4 py-3 bg-black border border-slate-800 rounded-lg text-white placeholder-slate-600 focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Password</label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  placeholder="At least 6 characters"
                  className="w-full px-4 py-3 bg-black border border-slate-800 rounded-lg text-white placeholder-slate-600 focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
                />
              </div>

              {error && (
                <div className="p-3 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400 text-sm">
                  {error}
                </div>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full py-4 bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold rounded-lg hover:from-purple-600 hover:to-pink-600 transition-all duration-300 disabled:opacity-50"
              >
                {loading ? 'Creating Account...' : 'Create Account'}
              </button>

              <button
                type="button"
                onClick={() => setMode('choice')}
                className="w-full py-2 text-slate-400 hover:text-white transition-colors"
              >
                ← Back
              </button>
            </form>
          </>
        )}

        {mode === 'login' && (
          <>
            <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-white mb-2">Welcome Back</h2>
              <p className="text-slate-400">Sign in to continue your journey</p>
            </div>

            <form onSubmit={handleLogin} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Email</label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  placeholder="you@example.com"
                  className="w-full px-4 py-3 bg-black border border-slate-800 rounded-lg text-white placeholder-slate-600 focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-400 mb-2">Password</label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  placeholder="Your password"
                  className="w-full px-4 py-3 bg-black border border-slate-800 rounded-lg text-white placeholder-slate-600 focus:border-purple-500 focus:outline-none focus:ring-1 focus:ring-purple-500"
                />
              </div>

              {error && (
                <div className="p-3 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400 text-sm">
                  {error}
                </div>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full py-4 bg-gradient-to-r from-purple-500 to-pink-500 text-white font-semibold rounded-lg hover:from-purple-600 hover:to-pink-600 transition-all duration-300 disabled:opacity-50"
              >
                {loading ? 'Signing In...' : 'Sign In'}
              </button>

              <button
                type="button"
                onClick={() => setMode('choice')}
                className="w-full py-2 text-slate-400 hover:text-white transition-colors"
              >
                ← Back
              </button>
            </form>
          </>
        )}
      </motion.div>
    </motion.div>
  );
}
