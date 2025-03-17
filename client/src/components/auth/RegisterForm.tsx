import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { TwoFactorModal } from './TwoFactorModal';
import { useToast } from '@/hooks/use-toast';
import api from '@/utils/api';
import { registerSchema } from '@/types/authScheemas';


type RegisterForm = z.infer<typeof registerSchema>;

interface RegisterFormProps {
  onSwitchToLogin: () => void;
}

export function RegisterForm({ onSwitchToLogin }: RegisterFormProps) {
  const { toast } = useToast();
  const [showTwoFactor, setShowTwoFactor] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState("");

  const form = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      username: "",
    },
  });

  const onSubmit = async (data: RegisterForm) => {
    try {
      const response = await api.post("/auth/signup", data);
      console.log({response})
      toast({ title: response.data.message });
      if (response.data.status) {
        setRegisteredEmail(data.email);
        setShowTwoFactor(true);
      }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
      console.log(error.response.data)
      toast({
        title: "Error",
        description: error?.message || "Unexpected error",
        variant: "destructive",
      });
    }
  };

  const handleTwoFactorSuccess = () => {
    setShowTwoFactor(false);
    toast({
      title: "Registration Successful",
      description: "Your account has been created and verified successfully.",
    });
    onSwitchToLogin();
  };

  return (
    <>
      <form className="space-y-4" onSubmit={form.handleSubmit(onSubmit)}>
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label htmlFor="firstName">First Name</Label>
            <Input id="firstName" placeholder="Enter first name" {...form.register("firstName")} />
            {form.formState.errors.firstName && (
              <p className="text-sm text-red-500">{form.formState.errors.firstName.message}</p>
            )}
          </div>
          <div className="space-y-2">
            <Label htmlFor="lastName">Last Name</Label>
            <Input id="lastName" placeholder="Enter last name" {...form.register("lastName")} />
            {form.formState.errors.lastName && (
              <p className="text-sm text-red-500">{form.formState.errors.lastName.message}</p>
            )}
          </div>
        </div>
        <div className="space-y-2">
          <Label htmlFor="username">Username</Label>
          <Input id="username" type="text" placeholder="Enter username" {...form.register("username")} />
          {form.formState.errors.username && (
            <p className="text-sm text-red-500">{form.formState.errors.username.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="email">Email address</Label>
          <Input id="email" type="email" placeholder="Enter email" {...form.register("email")} />
          {form.formState.errors.email && (
            <p className="text-sm text-red-500">{form.formState.errors.email.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="new-password">Password</Label>
          <Input id="new-password" type="password" placeholder="Enter password" {...form.register("password")} />
          {form.formState.errors.password && (
            <p className="text-sm text-red-500">{form.formState.errors.password.message}</p>
          )}
        </div>
        <Button type="submit" className="w-full bg-blue-500 hover:bg-blue-600">
          Sign Up
        </Button>
        <p className="text-center text-sm text-muted-foreground">
          Already have an Account?{" "}
          <Button type="button" variant="link" className="px-0" onClick={onSwitchToLogin}>
            Sign in
          </Button>
        </p>
      </form>

      {showTwoFactor && (
        <TwoFactorModal
          email={registeredEmail}
          onClose={() => setShowTwoFactor(false)}
          onSuccess={handleTwoFactorSuccess}
        />
      )}
    </>
  );
}
