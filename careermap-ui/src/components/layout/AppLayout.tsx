import Navbar from "./Navbar";

export default function AppLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen w-screen" style={{ background: 'var(--background)' }}>
      <Navbar />
      <main style={{ background: 'var(--background)' }}>
        <div className="max-w-[1400px] mx-auto px-6 py-8 lg:px-12 lg:py-12">
          {children}
        </div>
      </main>
    </div>
  );
}
