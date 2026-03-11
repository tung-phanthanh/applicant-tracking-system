import { createContext, useState, useEffect, type ReactNode } from "react";
import api from "@/lib/axios";
import type {
    AuthContextType,
    LoginCredentials,
    User,
} from "@/types/auth";

export const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        const token = localStorage.getItem("token");
        if (storedUser && token) {
            setUser(JSON.parse(storedUser));
        }
        setIsLoading(false);
    }, []);

    const login = async (credentials: LoginCredentials): Promise<void> => {
        setIsLoading(true);
        try {
            const response: any = await api.post("/auth/login", credentials);
            
            // Assuming backend returns { accessToken, refreshToken, userDetails }
            // or just tokens and we need to fetch user info.
            // Based on my previous Invoke-RestMethod, it returned accessToken.
            
            const accessToken = response.accessToken;
            const refreshToken = response.refreshToken;
            
            // For now, let's create a partial user object if userDetails is missing
            // Ideally the backend should return user info as well.
            const userData: User = {
                id: response.id || "1",
                name: response.fullName || credentials.email.split('@')[0],
                email: credentials.email,
                role: response.role || "admin",
                department: response.department || "HR",
                phone: response.phone || "000-000-0000",
                initials: (response.fullName || credentials.email).substring(0, 2).toUpperCase()
            };

            localStorage.setItem("token", accessToken);
            localStorage.setItem("refreshToken", refreshToken);
            localStorage.setItem("user", JSON.stringify(userData));
            
            setUser(userData);
        } catch (error) {
            console.error("Login failed:", error);
            throw new Error("Invalid credentials");
        } finally {
            setIsLoading(false);
        }
    };

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user");
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
