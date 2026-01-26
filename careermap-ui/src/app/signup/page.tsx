"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";

export default function SignupPage() {
  const router = useRouter();
  const { signup } = useAuth();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (password.length < 6) {
        setError("Password must be at least 6 characters");
        setLoading(false);
        return;
      }

      const result = await signup(name, email, password);

      if (result.success) {
        router.push("/calibration");
      } else {
        setError(result.message || "Signup failed");
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
          background: 'radial-gradient(circle at 70% 30%, var(--accent) 0%, transparent 60%)'
        }}
      />

      <div className="relative z-10 w-full max-w-md px-6">
        <div className="text-center mb-8 animate-fade-in">
          <Link href="/landing">
            <h1 className="text-[24px] font-bold tracking-tight mb-2" style={{ color: 'var(--text-primary)' }}>
              Ascent
            </h1>
          </Link>
          <p className="text-[14px]" style={{ color: 'var(--text-secondary)' }}>
            Create your account
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
          <form onSubmit={handleSignup} className="space-y-5">
            <div>
              <label className="block text-[13px] font-medium mb-2" style={{ color: 'var(--text-primary)' }}>
                Full Name
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full px-4 py-3 rounded-lg border text-[14px] transition-all focus:outline-none focus:ring-2"
                style={{
                  background: 'var(--background)',
                  borderColor: 'var(--border)',
                  color: 'var(--text-primary)'
                }}
                placeholder="John Doe"
                required
              />
            </div>

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
              <p className="text-[11px] mt-1.5" style={{ color: 'var(--text-tertiary)' }}>
                Must be at least 6 characters
              </p>
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
              {loading ? "Creating account..." : "Create account"}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-[13px]" style={{ color: 'var(--text-secondary)' }}>
              Already have an account?{" "}
              <Link href="/login" className="font-medium hover:underline" style={{ color: 'var(--accent)' }}>
                Sign in
              </Link>
            </p>
          </div>
        </div>

        <div className="mt-6 text-center">
          <Link href="/landing" className="text-[13px] hover:underline" style={{ color: 'var(--text-secondary)' }}>
            ← Back to home
          </Link>
        </div>

        <p className="text-[11px] text-center mt-8" style={{ color: 'var(--text-tertiary)' }}>
          By signing up, you agree to our Terms of Service and Privacy Policy
        </p>
      </div>
    </div>
  );
}
