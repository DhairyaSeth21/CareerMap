export default function DashboardPage() {
  return (
    <div className="space-y-10">
      {/* Header */}
      <header className="space-y-4">
        <p className="text-xs font-semibold uppercase tracking-[0.25em] text-amber-300/70">
          CareerMap Pro
        </p>

        <h1 className="text-4xl md:text-5xl font-semibold tracking-tight">
          <span className="bg-gradient-to-r from-amber-400 via-orange-500 to-rose-500 bg-clip-text text-transparent">
            Welcome to your AI career map
          </span>
        </h1>

        <p className="max-w-2xl text-sm md:text-base text-slate-300/90">
          Track your skills, map them to real jobs, and watch your{" "}
          <span className="text-amber-300">readiness score</span> evolve with
          every project, course, and internship.
        </p>
      </header>

      {/* Stat cards */}
      <section className="grid gap-5 md:grid-cols-3">
        <div className="rounded-3xl border border-white/10 bg-white/5 p-5 backdrop-blur-xl shadow-[0_0_30px_rgba(0,0,0,0.6)]">
          <p className="text-xs font-medium text-slate-400">Current Track</p>
          <p className="mt-2 text-lg font-semibold">Full-stack + AI</p>
          <p className="mt-3 text-xs text-slate-400">
            You’re building backend + frontend foundations with Spring Boot &
            Next.js. Perfect base for senior product-engineer energy.
          </p>
        </div>

        <div className="rounded-3xl border border-white/10 bg-white/5 p-5 backdrop-blur-xl shadow-[0_0_30px_rgba(0,0,0,0.6)]">
          <p className="text-xs font-medium text-slate-400">
            Skills being tracked
          </p>
          <p className="mt-2 text-lg font-semibold">Java, Spring, React, SQL</p>
          <div className="mt-3 h-2 w-full rounded-full bg-white/5">
            <div className="h-full w-2/3 rounded-full bg-gradient-to-r from-amber-400 via-orange-500 to-rose-500" />
          </div>
          <p className="mt-2 text-[11px] text-slate-400">
            Prototype: 4 / ∞ skills modeled in the graph.
          </p>
        </div>

        <div className="rounded-3xl border border-amber-400/40 bg-gradient-to-br from-amber-400/15 via-orange-500/10 to-rose-500/15 p-5 backdrop-blur-2xl shadow-[0_0_40px_rgba(248,180,0,0.6)]">
          <p className="text-xs font-medium text-amber-100">
            Readiness Snapshot
          </p>
          <p className="mt-2 text-3xl font-semibold">Level 1</p>
          <p className="mt-2 text-xs text-amber-100/80">
            Backend connected · UI online · Data flowing.
          </p>
          <p className="mt-3 text-[11px] text-amber-100/70">
            Next milestone: live skill graph visualization & decay / growth
            curves.
          </p>
        </div>
      </section>

      {/* Coming soon card */}
      <section>
        <div className="rounded-3xl border border-white/15 bg-black/40 p-8 backdrop-blur-2xl shadow-[0_0_50px_rgba(0,0,0,0.8)]">
          <h2 className="text-2xl font-semibold mb-3">
            What&apos;s coming soon?
          </h2>

          <p className="text-sm text-slate-300/90 mb-5 max-w-2xl">
            This alpha build is the skeleton. Over the next phases you&apos;ll
            plug in AI, graphs, and real user data.
          </p>

          <div className="grid gap-4 md:grid-cols-3 text-sm">
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-xs font-semibold text-amber-300/80 mb-1">
                Skill Graph Visualizer
              </p>
              <p className="text-slate-300/90 text-xs">
                Navigate skills as a graph. BFS/DFS through your stack and see
                dependencies like a minimap for your brain.
              </p>
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-xs font-semibold text-amber-300/80 mb-1">
                AI Job Analysis
              </p>
              <p className="text-slate-300/90 text-xs">
                Paste job descriptions, let GPT extract skills, gaps, and a
                concrete grind plan.
              </p>
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-xs font-semibold text-amber-300/80 mb-1">
                Proficiency Timeline
              </p>
              <p className="text-slate-300/90 text-xs">
                Track how your Java, React, and Spring scores rise with each
                semester, project and internship.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
