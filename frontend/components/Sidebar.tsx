"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const navItems = [
  { href: "/dashboard", label: "Dashboard" },
  { href: "/users", label: "Users" },
];

export default function Sidebar() {
  const pathname = usePathname();

  return (
    <aside
      className="
        hidden
        sm:flex
        w-64
        flex-col
        border-r border-white/10
        bg-black/30
        backdrop-blur-2xl
        shadow-[0_0_40px_rgba(0,0,0,0.7)]
      "
    >
      <div className="flex items-center gap-2 px-6 pt-6 pb-4">
        <div className="h-8 w-8 rounded-2xl bg-gradient-to-tr from-amber-400 via-orange-500 to-rose-500 shadow-[0_0_25px_rgba(248,180,0,0.7)]" />
        <div className="flex flex-col">
          <span className="text-sm font-semibold tracking-wide">
            CareerMap
          </span>
          <span className="text-[11px] uppercase tracking-[0.25em] text-amber-300/70">
            Pro
          </span>
        </div>
      </div>

      <nav className="mt-4 flex-1 space-y-1 px-3">
        {navItems.map((item) => {
          const active =
            pathname === item.href || pathname.startsWith(item.href + "/");

          return (
            <Link
              key={item.href}
              href={item.href}
              className={[
                "group flex items-center rounded-xl px-3 py-2 text-sm font-medium transition",
                active
                  ? "bg-gradient-to-r from-amber-400/20 via-orange-500/20 to-rose-500/20 text-amber-100 border border-amber-400/40 shadow-[0_0_20px_rgba(248,180,0,0.6)]"
                  : "text-slate-300 hover:text-amber-100 hover:bg-white/5",
              ].join(" ")}
            >
              <span className="inline-flex h-1.5 w-1.5 rounded-full bg-amber-400/80 mr-2 opacity-0 group-hover:opacity-100 transition" />
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="px-6 pb-6 pt-2 text-xs text-slate-400/80">
        <p className="mb-1 text-[11px] uppercase tracking-[0.2em] text-amber-400/80">
          Alpha build
        </p>
        <p>
          AI skill graphs, job analysis & proficiency tracking coming soon.
        </p>
      </div>
    </aside>
  );
}
