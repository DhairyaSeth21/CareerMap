"use client";

import { usePathname } from "next/navigation";
import AppLayout from "./AppLayout";
import AuthGuard from "./AuthGuard";

export default function ConditionalLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  // Pages that should not have the app layout (navbar/sidebar)
  // Landing, Calibration, Frontier are full-screen experiences - no chrome, no layout wrapper
  const noLayoutPages = ["/", "/landing", "/login", "/signup", "/calibration", "/frontier", "/onboarding"];

  return (
    <AuthGuard>
      {noLayoutPages.includes(pathname) ? (
        <>{children}</>
      ) : (
        <AppLayout>{children}</AppLayout>
      )}
    </AuthGuard>
  );
}
