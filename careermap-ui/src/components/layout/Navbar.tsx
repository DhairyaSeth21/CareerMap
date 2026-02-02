"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import ThemeToggle from "../ui/ThemeToggle";
import { useCurrentUser } from "@/hooks/useCurrentUser";

const navItems: Array<{ name: string; href: string }> = [
  { name: "← Back to Frontier", href: "/frontier" },
  { name: "Path", href: "/" },
  { name: "Assessment", href: "/assessment" },
  { name: "Opportunities", href: "/opportunities" },
];

export default function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const [showMenu, setShowMenu] = useState(false);
  const user = useCurrentUser();

  const handleLogout = () => {
    localStorage.removeItem("user");
    router.push("/login");
  };

  const displayName = user?.name || "User";
  const initials = displayName.split(" ").map(n => n[0]).join("").toUpperCase().slice(0, 2);

  return (
    <header
      className="sticky top-0 z-50 w-full flex items-center justify-between px-8 border-b"
      style={{
        height: '72px',
        borderColor: 'var(--border)',
        background: 'rgba(var(--background-rgb), 0.8)',
        backdropFilter: 'blur(20px)',
        WebkitBackdropFilter: 'blur(20px)',
        boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)'
      }}
    >
      {/* Left section - Logo + Navigation */}
      <div className="flex items-center gap-12">
        <Link href="/">
          <h1 className="text-[17px] font-semibold tracking-tight hover:opacity-70 transition-opacity" style={{ color: 'var(--text-primary)' }}>
            Provn
          </h1>
        </Link>

        <nav className="hidden lg:flex items-center gap-1">
          {navItems.map((item) => {
            const active = pathname === item.href;

            return (
              <Link
                key={item.href}
                href={item.href}
              >
                <div
                  className="relative px-3 py-2 text-[13px] font-medium transition-all hover:opacity-70"
                  style={{
                    color: active ? 'var(--text-primary)' : 'var(--text-secondary)'
                  }}
                >
                  {item.name}
                  {active && (
                    <div
                      className="absolute bottom-0 left-0 right-0 h-[2px] rounded-full"
                      style={{
                        background: 'var(--accent)'
                      }}
                    />
                  )}
                </div>
              </Link>
            );
          })}
        </nav>
      </div>

      {/* Right section */}
      <div className="flex items-center gap-4">
        <div className="relative">
          <button
            onClick={() => setShowMenu(!showMenu)}
            className="flex items-center gap-3 px-3 py-2 rounded-lg transition-all hover:bg-black/5 dark:hover:bg-white/5"
          >
            <div className="hidden sm:flex flex-col items-end">
              <span className="text-[12px] font-medium" style={{ color: 'var(--text-primary)' }}>
                {displayName}
              </span>
            </div>
            <div
              className="w-8 h-8 rounded-full flex items-center justify-center text-[12px] font-semibold transition-all hover:opacity-70"
              style={{
                background: 'var(--accent)',
                color: 'var(--accent-text)'
              }}
            >
              {initials}
            </div>
          </button>

          {showMenu && (
            <div
              className="absolute right-0 top-full mt-2 w-56 rounded-xl border overflow-hidden animate-scale-in"
              style={{
                background: 'var(--card-bg)',
                borderColor: 'var(--border)',
                boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)'
              }}
            >
              <div className="p-2">
                <Link href="/settings">
                  <div className="flex items-center gap-3 px-4 py-2.5 rounded-lg text-[13px] font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5 cursor-pointer">
                    <span style={{ color: 'var(--text-primary)' }}>Settings</span>
                  </div>
                </Link>
              </div>
              <div className="border-t" style={{ borderColor: 'var(--border)' }}>
                <button
                  onClick={handleLogout}
                  className="w-full px-4 py-3 text-left text-[13px] font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5 flex items-center gap-3"
                  style={{ color: 'var(--text-primary)' }}
                >
                  <span>Sign Out</span>
                </button>
              </div>
            </div>
          )}
        </div>

        <div className="w-px h-6" style={{ background: 'var(--border)' }}></div>
        <ThemeToggle />
      </div>
    </header>
  );
}
