
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router';
import * as z from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useToast } from '@/hooks/use-toast';
import api from '@/utils/api';
import { loginSchema } from '@/types/authScheemas';

import { useAuthStore } from "@/store/auth-storage";
import { useEffect } from 'react';

type LoginForm = z.infer<typeof loginSchema>;

interface LoginFormProps {
  onForgotPassword: () => void;
  onSwitchToRegister: () => void;
}

export function LoginForm({ onForgotPassword, onSwitchToRegister }: LoginFormProps) {
  const { toast } = useToast();
  const navigate = useNavigate();
  const { loginUser, isAuthenticated } = useAuthStore();

  const form = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const onSubmit = async (data: LoginForm) => {

    try {
      await loginUser(data)
    } catch (error: any) {
      console.log({ error })
      toast({
        title: "Error",
        description: error?.data.message || "Unexpected error",
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/home/me");
    }
  }, [isAuthenticated])


  return (
    <form className="space-y-4" onSubmit={form.handleSubmit(onSubmit)}>
      <div className="space-y-2">
        <Label htmlFor="email">Email</Label>
        <Input id="email" placeholder="Enter email" {...form.register("email")} />
        {form.formState.errors.email && (
          <p className="text-sm text-red-500">{form.formState.errors.email.message}</p>
        )}
      </div>
      <div className="space-y-2">
        <Label htmlFor="password">Password</Label>
        <Input id="password" type="password" placeholder="Enter password" {...form.register("password")} />
        {form.formState.errors.password && (
          <p className="text-sm text-red-500">{form.formState.errors.password.message}</p>
        )}
      </div>
      <div className="flex items-center justify-center">
        <Button type="button" variant="link" className="px-0 font-normal" onClick={onForgotPassword}>
          Forgot Password?
        </Button>
      </div>
      <Button type="submit" className="w-full bg-blue-500 hover:bg-blue-600">
        Sign In
      </Button>
      <p className="text-center text-sm text-muted-foreground">
        New to Modernize?{" "}
        <Button type="button" variant="link" className="px-0" onClick={onSwitchToRegister}>
          Create an account
        </Button>
      </p>
    </form>
  );
}