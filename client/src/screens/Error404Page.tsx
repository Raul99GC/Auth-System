import { Link } from "react-router";
import { Button } from "@/components/ui/button";

export default function Error404Page() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4">
      <div className="text-center space-y-8 max-w-[500px]">
        <div className="relative w-full h-[200px] sm:h-[300px]">
          <img
            src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-Yj4I1RKsP28NtYBUbn9P5zSCTB0Dfq.png"
            alt="404 Error Robot"
            className="object-contain w-full h-full"
          />
        </div>

        <div className="space-y-4">
          <h1 className="text-4xl sm:text-6xl font-bold tracking-tight">Oops!</h1>
          <p className="text-xl text-muted-foreground">
            The page you're looking for seems to have wandered off...
          </p>
        </div>

        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link to="/">
            <Button size="lg">Return Home</Button>
          </Link>
          <Link to="/auth">
            <Button variant="outline" size="lg">
              Sign In
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}