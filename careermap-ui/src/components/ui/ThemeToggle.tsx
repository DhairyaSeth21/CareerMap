"use client";

import { useTheme } from "next-themes";
import { useState, useEffect } from "react";

export default function ThemeToggle() {
  const { theme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);
  if (!mounted) return null;

  return (
    <button
      onClick={() => setTheme(theme === "light" ? "dark" : "light")}
      className="px-2.5 py-1.5 rounded-md text-[12px] font-medium transition-all hover:opacity-70"
      style={{
        background: 'var(--card-bg)',
        border: '1px solid var(--border)',
        color: 'var(--text-secondary)'
      }}
    >
      {theme === "light" ? "Dark" : "Light"}
    </button>
  );
}
