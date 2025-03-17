import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Loader2, Eye, EyeOff, KeyRound } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";
import { Toaster } from "@/components/ui/toaster";
import { useNavigate, Link } from "react-router";
import { passwordSchema } from "@/types/authScheemas";

const resetPassword = async (data: z.infer<typeof passwordSchema>) => {
  await new Promise((resolve) => setTimeout(resolve, 1500));
  if (Math.random() > 0.05) {
    return true;
  }
  throw new Error("Failed to reset password. Please try again.");
};

export default function PasswordResetPage() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const { toast } = useToast();
  const navigate = useNavigate();

  const form = useForm({
    resolver: zodResolver(passwordSchema),
    defaultValues: {
      newPassword: "",
      confirmPassword: "",
    },
  });

  const onSubmit = async (data) => {
    try {
      setIsSubmitting(true);
      await resetPassword(data);
      toast({
        title: "Password Reset Successful",
        description: "Your password has been reset successfully. You can now log in with your new password.",
      });
      form.reset();
      setTimeout(() => {
        navigate("/auth");
      }, 2000);
    } catch (error) {
      toast({
        title: "Error",
        description: error.message || "Failed to reset password. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-md overflow-hidden">
        <div className="bg-blue-500 p-6 flex justify-center">
          <div className="flex flex-col items-center text-white">
            <div className="w-16 h-16 bg-blue-400 rounded-full flex items-center justify-center mb-3">
              <KeyRound className="h-8 w-8" />
            </div>
            <h1 className="text-xl font-bold">Reset Your Password</h1>
          </div>
        </div>

        <div className="p-6">
          <p className="text-gray-600 mb-6 text-center">
            Please create a new password for your account. Make sure it's strong and secure.
          </p>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="newPassword">New Password</Label>
                <div className="relative">
                  <Input id="newPassword" type={showNewPassword ? "text" : "password"} {...form.register("newPassword")} placeholder="Enter new password" />
                  <Button type="button" variant="ghost" size="sm" className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent" onClick={() => setShowNewPassword(!showNewPassword)}>
                    {showNewPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                    <span className="sr-only">{showNewPassword ? "Hide password" : "Show password"}</span>
                  </Button>
                </div>
                {form.formState.errors.newPassword && <p className="text-sm text-red-500">{form.formState.errors.newPassword.message}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Confirm New Password</Label>
                <div className="relative">
                  <Input id="confirmPassword" type={showConfirmPassword ? "text" : "password"} {...form.register("confirmPassword")} placeholder="Confirm new password" />
                  <Button type="button" variant="ghost" size="sm" className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent" onClick={() => setShowConfirmPassword(!showConfirmPassword)}>
                    {showConfirmPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                    <span className="sr-only">{showConfirmPassword ? "Hide password" : "Show password"}</span>
                  </Button>
                </div>
                {form.formState.errors.confirmPassword && <p className="text-sm text-red-500">{form.formState.errors.confirmPassword.message}</p>}
              </div>
            </div>

            <div className="flex flex-col gap-4">
              <Button type="submit" disabled={isSubmitting} className="bg-blue-500 hover:bg-blue-600 w-full">
                {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                {isSubmitting ? "Resetting Password..." : "Reset Password"}
              </Button>
              <div className="text-center text-sm text-gray-500">
                Remember your password? <Link to="/auth" className="text-blue-500 hover:underline">Back to Login</Link>
              </div>
            </div>
          </form>
        </div>
      </div>
      <Toaster />
    </div>
  );
}
