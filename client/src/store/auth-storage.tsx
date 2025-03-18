import { create } from "zustand";
import { persist, createJSONStorage } from 'zustand/middleware'
import api from "@/utils/api";
import * as z from 'zod';
import { loginSchema, registerSchema } from "@/types/authScheemas";

export type Role = "USER" | "ADMIN" | "SUPER_ADMIN";

export interface User {
  id: string;
  username: string;
  email: string;
  roles: Role[];
  firstName: string;
  lastName: string;
  provider: string | null;
}

type loginForm = z.infer<typeof loginSchema>

type registerForm = z.infer<typeof registerSchema>

interface AuthStore {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  loginUser: (formData: loginForm) => Promise<void>;
  logoutUser: () => Promise<void>;
  signupUser: (formData: registerForm) => Promise<void>;
  checkAuth: () => Promise<void>;
  hasRole: (role: Role) => boolean;
  forgotPassword: (email: string) => Promise<void>;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,
      loginUser: async (formData) => {
        set({ isLoading: true });
        try {
          const response = await api.post(
            "/auth/login",
            formData,
          );
          const data = response.data;
          if (data.status) {
            set({
              user: data.user,
              isAuthenticated: true,
              isLoading: false,
            });
          } else {
            set({
              user: null,
              isAuthenticated: false,
              isLoading: false,
            });
          }
        } catch {
          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
          });
        }
      },

      logoutUser: async () => {
        set({ isLoading: true });
        try {
          await api.post(
            "/auth/logout",
            {},
          );
          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
          });
        } catch {
          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
          });
        }
      },

      signupUser: async (formData) => {
        set({ isLoading: true });
        try {
          const response = await api.post(
            "/auth/signup",
            formData,
          );
          const data = response.data;
          if (data.status) {
            set({
              user: data.user,
              isAuthenticated: true,
              isLoading: false,
            });
          } else {
            set({
              user: null,
              isAuthenticated: false,
              isLoading: false,
            });
          }
        } catch {
          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
          });
        }
      },

      checkAuth: async () => {
        set({ isLoading: true });
        try {
          const response = await api.get("/auth/check-auth", {
          });
          const data = response.data;
          if (data.status) {
            set({
              user: data.user,
              isAuthenticated: true,
              isLoading: false,
            });
          } else {
            set({
              user: null,
              isAuthenticated: false,
              isLoading: false,
            });
          }
        } catch {
          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
          });
        }
      },

      // Función para verificar si el usuario tiene un rol específico
      hasRole: (role: Role) => {
        const { user } = get();
        if (!user) return false;
        return user.roles.includes(role);
      },

      forgotPassword: async (email: string) => {
        try {
          await api.post(`/auth/forgot-password?email=${email}`);
        } catch (error) {
          console.error("Error en forgotPassword:", error);
        }
      }


    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => sessionStorage),
      partialize: (state) => {
        const { user, isAuthenticated, isLoading } = state;
        const partialUser = {
          roles: user?.roles,
          username: user?.username,
        }

        return { user: partialUser, isAuthenticated, isLoading };
      }
    },
  ),
)