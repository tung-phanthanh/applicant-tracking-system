import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Briefcase, Lock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/hooks/useAuth";

export default function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setIsLoading(true);

        try {
            await login({ email, password });
            navigate("/dashboard");
        } catch {
            setError("Invalid credentials. Please check your email and password.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="w-full max-w-md space-y-8 rounded-lg bg-card p-8 shadow-lg">
            {/* Logo & Title */}
            <div className="text-center">
                <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
                    <Briefcase className="h-6 w-6" />
                </div>
                <h2 className="mt-6 text-3xl font-bold tracking-tight text-foreground">
                    Sign in to your account
                </h2>
                <p className="mt-2 text-sm text-muted-foreground">
                    Or{" "}
                    <a
                        href="#"
                        className="font-medium text-primary underline-offset-4 hover:underline"
                    >
                        start your 14-day free trial
                    </a>
                </p>
            </div>

            {/* Form */}
            <form onSubmit={handleSubmit} className="space-y-5">
                <div className="space-y-4">
                    <div className="space-y-1.5">
                        <Label htmlFor="email">Email address</Label>
                        <Input
                            id="email"
                            name="email"
                            type="email"
                            autoComplete="email"
                            required
                            placeholder="john.doe@company.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            disabled={isLoading}
                        />
                    </div>
                    <div className="space-y-1.5">
                        <Label htmlFor="password">Password</Label>
                        <Input
                            id="password"
                            name="password"
                            type="password"
                            autoComplete="current-password"
                            required
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            disabled={isLoading}
                        />
                    </div>
                </div>

                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <input
                            id="remember-me"
                            name="remember-me"
                            type="checkbox"
                            className="h-4 w-4 rounded border-border text-primary focus:ring-ring"
                        />
                        <Label htmlFor="remember-me" className="text-sm font-normal">
                            Remember me
                        </Label>
                    </div>
                    <a
                        href="#"
                        className="text-sm font-medium text-muted-foreground hover:text-foreground"
                    >
                        Forgot your password?
                    </a>
                </div>

                {/* Error Message */}
                {error && (
                    <div className="rounded-md bg-destructive/10 p-4">
                        <p className="text-sm font-medium text-destructive">{error}</p>
                        <p className="mt-1 text-sm text-destructive/80">
                            Please check your email and password and try again.
                        </p>
                    </div>
                )}

                {/* Demo hint */}
                <div className="rounded-md bg-muted p-3 text-xs text-muted-foreground">
                    <strong>Demo:</strong> john.doe@company.com / password123
                </div>

                <Button
                    type="submit"
                    className="relative w-full"
                    disabled={isLoading}
                    size="lg"
                >
                    <Lock className="mr-2 h-4 w-4" />
                    {isLoading ? (
                        <>
                            <span
                                className="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                                aria-hidden="true"
                            />
                            Signing in...
                        </>
                    ) : (
                        "Sign in"
                    )}
                </Button>
            </form>
        </div>
    );
}
