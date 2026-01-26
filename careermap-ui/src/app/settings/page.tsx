"use client";

import { useCurrentUser } from "@/hooks/useCurrentUser";
import ThemeToggle from "@/components/ui/ThemeToggle";

export default function SettingsPage() {
  const user = useCurrentUser();

  return (
    <div className="min-h-screen p-8" style={{ background: "var(--background)" }}>
      <div className="max-w-3xl mx-auto">
        <h1 className="text-[32px] font-bold tracking-tight mb-2" style={{ color: "var(--text-primary)" }}>
          Settings
        </h1>
        <p className="text-[14px] mb-8" style={{ color: "var(--text-secondary)" }}>
          Manage your account and preferences
        </p>

        {/* Profile Section */}
        <div className="p-6 rounded-xl border mb-6" style={{ background: "var(--card-bg)", borderColor: "var(--border)" }}>
          <h2 className="text-[18px] font-semibold mb-4" style={{ color: "var(--text-primary)" }}>
            Profile
          </h2>
          <div className="space-y-4">
            <div>
              <label className="text-[13px] font-medium block mb-1.5" style={{ color: "var(--text-secondary)" }}>
                Name
              </label>
              <input
                type="text"
                value={user?.name || ""}
                disabled
                className="w-full px-4 py-2 rounded-lg border text-[14px]"
                style={{
                  background: "var(--sidebar-bg)",
                  borderColor: "var(--border)",
                  color: "var(--text-primary)",
                  cursor: "not-allowed",
                  opacity: 0.7
                }}
              />
            </div>
            <div>
              <label className="text-[13px] font-medium block mb-1.5" style={{ color: "var(--text-secondary)" }}>
                Email
              </label>
              <input
                type="email"
                value={user?.email || ""}
                disabled
                className="w-full px-4 py-2 rounded-lg border text-[14px]"
                style={{
                  background: "var(--sidebar-bg)",
                  borderColor: "var(--border)",
                  color: "var(--text-primary)",
                  cursor: "not-allowed",
                  opacity: 0.7
                }}
              />
            </div>
          </div>
        </div>

        {/* Appearance Section */}
        <div className="p-6 rounded-xl border mb-6" style={{ background: "var(--card-bg)", borderColor: "var(--border)" }}>
          <h2 className="text-[18px] font-semibold mb-4" style={{ color: "var(--text-primary)" }}>
            Appearance
          </h2>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-[14px] font-medium" style={{ color: "var(--text-primary)" }}>
                Theme
              </p>
              <p className="text-[13px]" style={{ color: "var(--text-secondary)" }}>
                Toggle between light and dark mode
              </p>
            </div>
            <ThemeToggle />
          </div>
        </div>

        {/* OpenAI Status */}
        <div className="p-6 rounded-xl border" style={{ background: "var(--card-bg)", borderColor: "var(--border)" }}>
          <h2 className="text-[18px] font-semibold mb-4" style={{ color: "var(--text-primary)" }}>
            AI Integration
          </h2>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-[14px] font-medium" style={{ color: "var(--text-primary)" }}>
                OpenAI API Status
              </p>
              <p className="text-[13px]" style={{ color: "var(--text-secondary)" }}>
                Used for quiz generation and job description analysis
              </p>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 rounded-full" style={{ background: "#10b981" }}></div>
              <span className="text-[13px] font-medium" style={{ color: "#10b981" }}>
                Configured
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
