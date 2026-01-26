'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';

// PRODUCTION DEPLOYMENT - DO NOT CHANGE THIS URL
const API_BASE_URL = 'https://careermap-production.up.railway.app';
console.log('🚨 AuthContext using API_BASE_URL:', API_BASE_URL);

interface User {
  userId: number;
  name: string;
  email: string;
  xp: number;
  level: number;
  streak: number;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<{ success: boolean; message?: string }>;
  signup: (name: string, email: string, password: string) => Promise<{ success: boolean; message?: string }>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  // Load token and user from localStorage on mount
  useEffect(() => {
    const storedToken = localStorage.getItem('authToken');
    if (storedToken) {
      // Verify token with backend
      fetch(`${API_BASE_URL}/api/v1/me`, {
        headers: {
          'Authorization': `Bearer ${storedToken}`,
        },
      })
        .then(res => res.json())
        .then(data => {
          if (data.success) {
            setToken(storedToken);
            setUser({
              userId: data.userId,
              name: data.name,
              email: data.email,
              xp: data.xp || 0,
              level: data.level || 1,
              streak: data.streak || 0,
            });
          } else {
            // Token invalid, clear it
            localStorage.removeItem('authToken');
          }
        })
        .catch(() => {
          localStorage.removeItem('authToken');
        })
        .finally(() => {
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/v1/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (data.success && data.token) {
        setToken(data.token);
        setUser({
          userId: data.userId,
          name: data.name,
          email: data.email,
          xp: data.xp || 0,
          level: data.level || 1,
          streak: data.streak || 0,
        });
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userId', data.userId.toString());
        return { success: true };
      } else {
        return { success: false, message: data.message || 'Login failed' };
      }
    } catch (error) {
      return { success: false, message: 'Network error. Please try again.' };
    }
  };

  const signup = async (name: string, email: string, password: string) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/v1/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, email, password }),
      });

      const data = await response.json();

      if (data.success && data.token) {
        setToken(data.token);
        setUser({
          userId: data.userId,
          name: data.name,
          email: data.email,
          xp: 0,
          level: 1,
          streak: 0,
        });
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userId', data.userId.toString());
        return { success: true };
      } else {
        return { success: false, message: data.message || 'Signup failed' };
      }
    } catch (error) {
      return { success: false, message: 'Network error. Please try again.' };
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        signup,
        logout,
        isAuthenticated: !!token && !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
