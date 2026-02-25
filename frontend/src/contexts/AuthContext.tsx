import { createContext, useState, type ReactNode } from "react";
import type {
    AuthContextType,
    LoginCredentials,
    User,
} from "@/types/auth";

export const AuthContext = createContext<AuthContextType | null>(null);

const MOCK_USER: User = {
    id: "1",
    name: "John Doe",
    email: "john.doe@company.com",
    role: "recruiter",
    department: "HR Department",
    phone: "+1 (555) 123-4567",
    initials: "JD",
};

// Simulated valid credentials for demo
const VALID_CREDENTIALS = {
    email: "john.doe@company.com",
    password: "password123",
};

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    const login = async (credentials: LoginCredentials): Promise<void> => {
        setIsLoading(true);
        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 1000));
        setIsLoading(false);

        if (
            credentials.email === VALID_CREDENTIALS.email &&
            credentials.password === VALID_CREDENTIALS.password
        ) {
            setUser(MOCK_USER);
        } else {
            throw new Error("Invalid credentials");
        }
    };

    const logout = () => {
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
