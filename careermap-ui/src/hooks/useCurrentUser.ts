"use client";

import { useState, useEffect } from "react";

interface User {
  userId: number;
  name: string;
  email: string;
}

export function useCurrentUser(): User | null {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const userString = localStorage.getItem("user");
    if (userString) {
      try {
        const userData = JSON.parse(userString);
        setUser({
          userId: userData.userId,
          name: userData.name,
          email: userData.email,
        });
      } catch (e) {
        console.error("Failed to parse user data:", e);
        setUser(null);
      }
    }
  }, []);

  return user;
}
