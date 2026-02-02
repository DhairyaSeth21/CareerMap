import "./globals.css";
import { Inter } from 'next/font/google';
import { ThemeProvider } from "../providers/ThemeProvider";
import { AuthProvider } from "../contexts/AuthContext";
import ConditionalLayout from "../components/layout/ConditionalLayout";

const inter = Inter({
  subsets: ['latin'],
  display: 'swap',
  variable: '--font-inter'
});

export const metadata = {
  title: "Provn",
  description: "Evidence-based learning platform - Know what you actually know",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning className={inter.variable}>
      <body className="bg-gray-100 dark:bg-gray-900 text-gray-900 dark:text-white">
        <ThemeProvider>
          <AuthProvider>
            <ConditionalLayout>
              {children}
            </ConditionalLayout>
          </AuthProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
