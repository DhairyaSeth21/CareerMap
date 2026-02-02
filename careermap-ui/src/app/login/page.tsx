"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const result = await login(email, password);

      if (result.success) {
        // Check if user has already completed calibration
        const calibrationComplete = localStorage.getItem('calibration_complete');
        if (calibrationComplete === 'true') {
          router.push("/frontier");
        } else {
          router.push("/calibration");
        }
      } else {
        setError(result.message || "Login failed");
      }
    } catch (err: any) {
      setError(err.message || "Unable to connect to server");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center" style={{ background: 'var(--background)' }}>
      {/* Background gradient */}
      <div
        className="absolute inset-0 opacity-20"
        style={{
          background: 'radial-gradient(circle at 30% 30%, var(--accent) 0%, transparent 60%)'
        }}
      />

      <div className="relative z-10 w-full max-w-md px-6">
        <div className="text-center mb-8 animate-fade-in">
          <Link href="/landing">
            <h1 className="text-[24px] font-bold tracking-tight mb-2" style={{ color: 'var(--text-primary)' }}>
              Provn
            </h1>
          </Link>
          <p className="text-[14px]" style={{ color: 'var(--text-secondary)' }}>
            Sign in to your account
          </p>
        </div>

        <div
          className="p-8 rounded-2xl border animate-scale-in"
          style={{
            background: 'var(--card-bg)',
            borderColor: 'var(--border)',
            animationDelay: '100ms'
          }}
        >
          <form onSubmit={handleLogin} className="space-y-5">
            <div>
              <label className="block text-[13px] font-medium mb-2" style={{ color: 'var(--text-primary)' }}>
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border text-[14px] transition-all focus:outline-none focus:ring-2"
                style={{
                  background: 'var(--background)',
                  borderColor: 'var(--border)',
                  color: 'var(--text-primary)'
                }}
                placeholder="you@example.com"
                required
              />
            </div>

            <div>
              <label className="block text-[13px] font-medium mb-2" style={{ color: 'var(--text-primary)' }}>
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border text-[14px] transition-all focus:outline-none focus:ring-2"
                style={{
                  background: 'var(--background)',
                  borderColor: 'var(--border)',
                  color: 'var(--text-primary)'
                }}
                placeholder="••••••••"
                required
              />
            </div>

            {error && (
              <div className="p-3 rounded-lg border text-[13px]" style={{ background: 'rgba(239, 68, 68, 0.1)', borderColor: '#ef4444', color: '#ef4444' }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full px-4 py-3 rounded-lg text-[14px] font-semibold transition-all hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
              style={{ background: loading ? 'var(--text-tertiary)' : 'var(--accent)', color: 'var(--accent-text)' }}
            >
              {loading ? "Signing in..." : "Sign in"}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-[13px]" style={{ color: 'var(--text-secondary)' }}>
              Don't have an account?{" "}
              <Link href="/signup" className="font-medium hover:underline" style={{ color: 'var(--accent)' }}>
                Sign up
              </Link>
            </p>
          </div>
        </div>

        <div className="mt-6 text-center">
          <Link href="/landing" className="text-[13px] hover:underline" style={{ color: 'var(--text-secondary)' }}>
            ← Back to home
          </Link>
        </div>
      </div>
    </div>
  );
}
