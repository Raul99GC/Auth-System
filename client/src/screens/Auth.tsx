import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { SocialAuthButtons } from "@/components/auth/SocialAuthButtons";
import { FormDivider } from "@/components/auth/FormDivider";
import { LoginForm } from "@/components/auth/LoginForm";
import { RegisterForm } from "@/components/auth/RegisterForm";
import { Toaster } from "@/components/ui/toaster";
import { ForgotPasswordModal } from "@/components/ForgotPasswordModal";

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [showForgotPassword, setShowForgotPassword] = useState(false);


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

          <SocialAuthButtons />

          <FormDivider text={`or ${isLogin ? "sign in" : "sign up"} with`} />

          <AnimatePresence mode="wait">
            {isLogin ? (
              <motion.div
                key="login"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.2 }}
              >
                <LoginForm
                  onForgotPassword={() => setShowForgotPassword(true)}
                  onSwitchToRegister={() => setIsLogin(false)}
                />
              </motion.div>
            ) : (
              <motion.div
                key="register"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.2 }}
              >
                <RegisterForm onSwitchToLogin={() => setIsLogin(true)} />
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>

      {/* Modal para "Forgot Password" */}
      {showForgotPassword && (
        <ForgotPasswordModal
          isOpen={showForgotPassword}
          onClose={() => setShowForgotPassword(false)}
        />
      )}

      <Toaster />
    </div>
  );
}