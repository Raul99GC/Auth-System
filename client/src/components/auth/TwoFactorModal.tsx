import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import { Check } from "lucide-react";
import { Button } from "../ui/button";
import { Checkbox } from "@radix-ui/react-checkbox";
import { useToast } from "@/hooks/use-toast";
import api from "@/utils/api";

interface TwoFactorModalProps {
  email: string;
  onClose: () => void;
  onSuccess: () => void;
}

export function TwoFactorModal({ email, onClose, onSuccess }: TwoFactorModalProps) {
  const [code, setCode] = useState(["", "", "", "", "", ""]);
  const [timer, setTimer] = useState(299);
  const [isTrustedDevice, setIsTrustedDevice] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);
  const { toast } = useToast();

  useEffect(() => {
    // Auto focus en el primer input
    inputRefs.current[0]?.focus();

    // Inicia el contador
    const interval = setInterval(() => {
      setTimer((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const handleInputChange = (index: number, value: string) => {
    if (value.length > 1) return; // Evitar pegar múltiples caracteres

    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);

    // Auto-avanza al siguiente input
    if (value !== "" && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Backspace" && code[index] === "" && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    const enteredCode = code.join("");
    try {
      const response = await api.post("/auth/activate-account", {
        email,
        token: enteredCode,
      });
      toast({ title: response.data.message });
      if (response.data.status) {
        onSuccess();
      } else {
        // Código inválido: reiniciamos y volvemos a enfocar el primer input
        setCode(["", "", "", "", "", ""]);
        inputRefs.current[0]?.focus();
      }
    } catch (error: any) {
      toast({
        title: "Error",
        description: error?.message || "Unexpected error",
        variant: "destructive",
      });
      setCode(["", "", "", "", "", ""]);
      inputRefs.current[0]?.focus();
    }
    setIsSubmitting(false);
  };

  const handleResend = async () => {

    try {
      const respnse = await api.post("/auth/create-new-token?email=" + email);
      console.log(respnse)

    } catch (error) {
      console.log(error)
    }

    toast({
      title: "Code Resent",
      description: "A new authentication code has been sent to your email.",
    });
    setTimer(119);
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.95 }}
        className="bg-white rounded-xl p-8 w-full max-w-md absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
      >
        <div className="flex flex-col items-center text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-6">
            <Check className="w-8 h-8 text-green-500" />
          </div>

          <h2 className="text-2xl font-semibold mb-2">Verify Your Account</h2>
          <p className="text-gray-500 mb-6">
            To complete your registration, please enter the verification code
            <br />
            sent to your email: {email}
          </p>

          <div className="flex gap-2 mb-6">
            {code.map((digit, index) => (
              <input
                key={index}
                ref={(el) => (inputRefs.current[index] = el)}
                type="text"
                maxLength={1}
                className="w-12 h-12 border-2 rounded-lg text-center text-xl font-semibold focus:border-blue-500 focus:ring-2 focus:ring-blue-200 outline-none transition-all"
                value={digit}
                onChange={(e) => handleInputChange(index, e.target.value)}
                onKeyDown={(e) => handleKeyDown(index, e)}
              />
            ))}
          </div>

          <p className="text-sm text-gray-500 mb-4">
            {timer > 0 ? (
              `Resend available in ${Math.floor(timer / 60)}:${(timer % 60)
                .toString()
                .padStart(2, "0")} seconds`
            ) : (
              <Button variant="link" className="p-0" onClick={handleResend}>
                Resend code
              </Button>
            )}
          </p>

          <div className="flex items-center gap-2 mb-6">
            <Checkbox
              id="trusted-device"
              checked={isTrustedDevice}
              onCheckedChange={(checked) => setIsTrustedDevice(checked === true)}
            />
            <label htmlFor="trusted-device" className="text-sm text-gray-600">
              Trusted Device
            </label>
          </div>

          <div className="flex gap-3 w-full">
            <Button variant="outline" className="flex-1" onClick={onClose} disabled={isSubmitting}>
              Cancel
            </Button>
            <Button
              className="flex-1 bg-blue-500 hover:bg-blue-600"
              onClick={handleSubmit}
              disabled={code.some((digit) => digit === "") || isSubmitting}
            >
              {isSubmitting ? "Verifying..." : "Verify & Complete Registration"}
            </Button>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
