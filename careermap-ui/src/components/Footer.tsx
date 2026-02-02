'use client';

import { Code, Github, Linkedin, Mail } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="w-full bg-slate-900 border-t border-slate-800 mt-auto">
      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* About Provn */}
          <div>
            <h3 className="text-white font-bold text-lg mb-3">Provn</h3>
            <p className="text-slate-400 text-sm leading-relaxed">
              Evidence-based learning platform for software engineers.
              Built with Spring Boot, React, and AI.
            </p>
          </div>

          {/* Creator Info */}
          <div>
            <h3 className="text-white font-bold text-lg mb-3">Created By</h3>
            <div className="space-y-2">
              <p className="text-white font-semibold">Dhairya Arjun Seth</p>
              <p className="text-slate-400 text-sm">Computer Science Student</p>
            </div>
          </div>

          {/* Tech Stack & Links */}
          <div>
            <h3 className="text-white font-bold text-lg mb-3">Built With</h3>
            <div className="flex flex-wrap gap-2 mb-4">
              <span className="px-2 py-1 bg-blue-600/20 text-blue-400 text-xs rounded border border-blue-600/30">
                Spring Boot
              </span>
              <span className="px-2 py-1 bg-purple-600/20 text-purple-400 text-xs rounded border border-purple-600/30">
                React
              </span>
              <span className="px-2 py-1 bg-green-600/20 text-green-400 text-xs rounded border border-green-600/30">
                TypeScript
              </span>
              <span className="px-2 py-1 bg-pink-600/20 text-pink-400 text-xs rounded border border-pink-600/30">
                OpenAI
              </span>
            </div>
            <div className="flex gap-3">
              <a
                href="https://github.com/dhairyaseth"
                target="_blank"
                rel="noopener noreferrer"
                className="text-slate-400 hover:text-white transition-colors"
                title="GitHub"
              >
                <Github className="w-5 h-5" />
              </a>
              <a
                href="https://linkedin.com/in/dhairyaseth"
                target="_blank"
                rel="noopener noreferrer"
                className="text-slate-400 hover:text-white transition-colors"
                title="LinkedIn"
              >
                <Linkedin className="w-5 h-5" />
              </a>
              <a
                href="mailto:dhairya.seth@example.com"
                className="text-slate-400 hover:text-white transition-colors"
                title="Email"
              >
                <Mail className="w-5 h-5" />
              </a>
            </div>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-slate-800 mt-6 pt-6 flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2 text-slate-400 text-sm">
            <Code className="w-4 h-4" />
            <span>Designed & Developed by <span className="text-white font-semibold">Dhairya Seth</span></span>
          </div>
          <div className="text-slate-500 text-xs">
            © 2026 Provn. All rights reserved.
          </div>
        </div>
      </div>
    </footer>
  );
}
