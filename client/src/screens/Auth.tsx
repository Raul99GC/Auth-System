import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { useToast } from "@/hooks/use-toast";
import { Toaster } from "@/components/ui/toaster";
import { ForgotPasswordModal } from "@/components/ForgotPasswordModal";

const loginSchema = z.object({
  username: z.string().min(3, "Username must be at least 3 characters"),
  password: z.string().min(6, "Password must be at least 6 characters"),
  remember: z.boolean().optional(),
});

const registerSchema = z.object({
  name: z.string().min(2, "Name must be at least 2 characters"),
  email: z.string().email("Invalid email address"),
  password: z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/[0-9]/, "Password must contain at least one number"),
});

type LoginForm = z.infer<typeof loginSchema>;
type RegisterForm = z.infer<typeof registerSchema>;

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true)
  const [showForgotPassword, setShowForgotPassword] = useState(false)
  const { toast } = useToast()

  const loginForm = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      username: "",
      password: "",
      remember: false,
    },
  })

  const registerForm = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      name: "",
      email: "",
      password: "",
    },
  })

  const onLoginSubmit = (data: LoginForm) => {
    console.log("Login data:", data)
    toast({
      title: "Login Successful",
      description: "Welcome back! Redirecting...",
    })
  }

  const onRegisterSubmit = (data: RegisterForm) => {
    console.log("Register data:", data)
    toast({
      title: "Registration Successful",
      description: "Account created successfully!",
    })
  }

  return (
    <div className="grid min-h-screen grid-cols-1 md:grid-cols-[70%_30%]">
      <div className="hidden bg-blue-50 md:flex h-[100vh] items-center justify-center">
        <img
          src="https://bootstrapdemos.adminmart.com/modernize/dist/assets/images/backgrounds/login-security.svg"
          alt="Security illustration"
          className="object-contain p-12"
          style={{ width: "70%", height: "70%" }}
        />
      </div>

      <div className="flex items-center justify-center p-8">
        <div className="w-full max-w-[400px] space-y-6">
          <div className="space-y-2 text-center">
            <h1 className="text-3xl font-bold tracking-tight">Welcome to Modernize</h1>
            <p className="text-sm text-muted-foreground">Your Admin Dashboard</p>
          </div>

          <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
            <Button variant="outline" className="w-full">
              <svg className="mr-2 h-4 w-4" viewBox="0 0 24 24">
                <path
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  fill="#4285F4"
                />
                <path
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  fill="#34A853"
                />
                <path
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  fill="#FBBC05"
                />
                <path
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  fill="#EA4335"
                />
              </svg>
              with Google
            </Button>
            <Button variant="outline" className="w-full">
              <svg className="mr-2 h-4 w-4" viewBox="0 0 24 24">
                <path
                  fillRule="evenodd"
                  clipRule="evenodd"
                  d="M12 2C6.477 2 2 6.484 2 12.02c0 4.425 2.865 8.166 6.839 9.504.5.091.682-.217.682-.482 0-.237-.009-.868-.013-1.703-2.782.604-3.369-1.342-3.369-1.342-.454-1.155-1.11-1.463-1.11-1.463-.908-.62.069-.607.069-.607 1.004.07 1.532 1.033 1.532 1.033.892 1.532 2.341 1.09 2.91.834.091-.647.35-1.09.636-1.342-2.22-.253-4.555-1.113-4.555-4.945 0-1.09.39-1.984 1.03-2.682-.103-.253-.447-1.27.098-2.646 0 0 .84-.269 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.295 2.748-1.026 2.748-1.026.547 1.376.203 2.393.1 2.646.64.698 1.03 1.592 1.03 2.682 0 3.84-2.337 4.688-4.566 4.936.359.31.678.92.678 1.855 0 1.337-.012 2.415-.012 2.742 0 .268.18.58.688.482A10.02 10.02 0 0022 12.02C22 6.484 17.523 2 12 2z"
                  fill="currentColor"
                />
              </svg>
              with GitHub
            </Button>
          </div>

          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <span className="w-full border-t" />
            </div>
            <div className="relative flex justify-center text-xs uppercase">
              <span className="bg-background px-2 text-muted-foreground">
                or {isLogin ? "sign in" : "sign up"} with
              </span>
            </div>
          </div>

          <AnimatePresence mode="wait">
            {isLogin ? (
              <motion.form
                key="login"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.2 }}
                className="space-y-4"
                onSubmit={loginForm.handleSubmit(onLoginSubmit)}
              >
                <div className="space-y-2">
                  <Label htmlFor="username">Username</Label>
                  <Input id="username" placeholder="Enter username" {...loginForm.register("username")} />
                  {loginForm.formState.errors.username && (
                    <p className="text-sm text-red-500">{loginForm.formState.errors.username.message}</p>
                  )}
                </div>
                <div className="space-y-2">
                  <Label htmlFor="password">Password</Label>
                  <Input
                    id="password"
                    type="password"
                    placeholder="Enter password"
                    {...loginForm.register("password")}
                  />
                  {loginForm.formState.errors.password && (
                    <p className="text-sm text-red-500">{loginForm.formState.errors.password.message}</p>
                  )}
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Checkbox id="remember" {...loginForm.register("remember")} />
                    <label
                      htmlFor="remember"
                      className="text-sm font-medium leading-none"
                    >
                      Remember this Device
                    </label>
                  </div>
                  <Button
                    type="button"
                    variant="link"
                    className="px-0 font-normal"
                    onClick={() => setShowForgotPassword(true)}
                  >
                    Forgot Password?
                  </Button>
                </div>
                <Button type="submit" className="w-full bg-blue-500 hover:bg-blue-600">
                  Sign In
                </Button>
                <p className="text-center text-sm text-muted-foreground">
                  New to Modernize?{" "}
                  <Button
                    variant="link"
                    className="px-0"
                    onClick={() => {
                      setIsLogin(false)
                      loginForm.reset()
                    }}
                  >
                    Create an account
                  </Button>
                </p>
              </motion.form>
            ) : (
              <motion.form
                key="register"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.2 }}
                className="space-y-4"
                onSubmit={registerForm.handleSubmit(onRegisterSubmit)}
              >
                <div className="space-y-2">
                  <Label htmlFor="name">Name</Label>
                  <Input id="name" placeholder="Enter your name" {...registerForm.register("name")} />
                  {registerForm.formState.errors.name && (
                    <p className="text-sm text-red-500">{registerForm.formState.errors.name.message}</p>
                  )}
                </div>
                <div className="space-y-2">
                  <Label htmlFor="email">Email address</Label>
                  <Input id="email" type="email" placeholder="Enter email" {...registerForm.register("email")} />
                  {registerForm.formState.errors.email && (
                    <p className="text-sm text-red-500">{registerForm.formState.errors.email.message}</p>
                  )}
                </div>
                <div className="space-y-2">
                  <Label htmlFor="new-password">Password</Label>
                  <Input
                    id="new-password"
                    type="password"
                    placeholder="Enter password"
                    {...registerForm.register("password")}
                  />
                  {registerForm.formState.errors.password && (
                    <p className="text-sm text-red-500">{registerForm.formState.errors.password.message}</p>
                  )}
                </div>
                <Button type="submit" className="w-full bg-blue-500 hover:bg-blue-600">
                  Sign Up
                </Button>
                <p className="text-center text-sm text-muted-foreground">
                  Already have an Account?{" "}
                  <Button
                    variant="link"
                    className="px-0"
                    onClick={() => {
                      setIsLogin(true)
                      registerForm.reset()
                    }}
                  >
                    Sign in
                  </Button>
                </p>
              </motion.form>
            )}
          </AnimatePresence>
        </div>
      </div>

      {/* Modal de "Forgot Password" */}
      <ForgotPasswordModal isOpen={showForgotPassword} onClose={() => setShowForgotPassword(false)} />

      <Toaster />
    </div>
  )
}
