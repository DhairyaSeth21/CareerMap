"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const navItems: Array<{ name: string; href: string; icon: string }> = [
  { name: "Dashboard", href: "/", icon: "🏠" },
  { name: "Analyze Job", href: "/analyze", icon: "🔍" },
  { name: "Skill Progress", href: "/progress", icon: "📈" },
  { name: "Skill Map", href: "/skillmap", icon: "🗺️" },
  { name: "Trends", href: "/trends", icon: "📊" },
  { name: "Goals", href: "/goals", icon: "🎯" },
];

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside
      className="h-full w-[240px] flex flex-col border-r animate-slide-in"
      style={{
        background: 'var(--sidebar-bg)',
        borderColor: 'var(--border)'
      }}
    >
      {/* Logo / Header */}
      <div className="px-5 py-6">
        <h1 className="text-[15px] font-semibold tracking-tight" style={{ color: 'var(--text-primary)' }}>
          Levld
        </h1>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 overflow-y-auto">
        <div className="space-y-0.5">
          {navItems.map((item, index) => {
            const active = pathname === item.href;

            return (
              <Link
                key={item.href}
                href={item.href}
                className="animate-fade-in"
                style={{ animationDelay: `${index * 50}ms` }}
              >
                <div
                  className={`flex items-center gap-2.5 px-2.5 py-1.5 rounded-md text-[13px] font-medium transition-all ${
                    active
                      ? ''
                      : 'hover:bg-black/5 dark:hover:bg-white/5'
                  }`}
                  style={{
                    background: active ? 'var(--accent)' : 'transparent',
                    color: active ? '#ffffff' : 'var(--text-secondary)'
                  }}
                >
                  <span className="text-base opacity-90">{item.icon}</span>
                  <span>{item.name}</span>
                </div>
              </Link>
            );
          })}
        </div>
      </nav>

      {/* Footer */}
      <div className="px-5 py-4 border-t" style={{ borderColor: 'var(--border)' }}>
        <div className="flex items-center gap-2">
          <div className="w-1.5 h-1.5 rounded-full bg-green-500"></div>
          <span className="text-[11px] font-medium" style={{ color: 'var(--text-tertiary)' }}>
            All systems operational
          </span>
        </div>
      </div>
    </aside>
  );
}
