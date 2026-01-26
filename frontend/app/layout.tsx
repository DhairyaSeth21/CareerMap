import type { Metadata } from "next";
import "./globals.css";
import Sidebar from "../components/Sidebar";
import type { ReactNode } from "react";

export const metadata: Metadata = {
  title: "CareerMap Pro",
  description: "AI-powered career roadmapping and skill tracking",
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en" className="h-full">
      <body className="h-full bg-gradient-to-br from-[#020617] via-[#020617] to-[#020617] text-slate-50 antialiased">
        <div className="flex min-h-screen">
          {/* Glass sidebar */}
          <Sidebar />

          {/* Main content */}
          <main className="flex-1 px-4 py-8 md:px-8 md:py-10">
            <div className="mx-auto max-w-6xl">
              {children}
            </div>
          </main>
        </div>
      </body>
    </html>
  );
}
