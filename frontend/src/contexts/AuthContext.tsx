import {
    createContext,
    useState,
    useEffect,
    useCallback,
    type ReactNode,
} from "react";
import type { AuthContextType, LoginCredentials, User } from "@/types/auth";
import { authService } from "@/services/authService";

export const AuthContext = createContext<AuthContextType | null>(null);

// Tokens can be in either localStorage (remember me) or sessionStorage (session only)
function getStoredItem(key: string): string | null {
    return localStorage.getItem(key) || sessionStorage.getItem(key);
}

function clearSession() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
    sessionStorage.removeItem("accessToken");
    sessionStorage.removeItem("refreshToken");
    sessionStorage.removeItem("user");
}

function buildUserFromStorage(): User | null {
    try {
        const raw = getStoredItem("user");
        if (!raw) return null;
        return JSON.parse(raw) as User;
    } catch {
        return null;
    }
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(MOCK_USER);
    const [user, setUser] = useState<User | null>(buildUserFromStorage);
    const [isLoading, setIsLoading] = useState(false);

    // Restore session on mount: if token exists but user missing, clear stale data
    useEffect(() => {
        const accessToken = getStoredItem("accessToken");
        const storedUser = buildUserFromStorage();
        if (!accessToken || !storedUser) {
            clearSession();
            setUser(null);
        }
    }, []);

    const login = useCallback(
        async (credentials: LoginCredentials, rememberMe = false): Promise<void> => {
            setIsLoading(true);
            try {
                const response = await authService.login(
                    credentials.email,
                    credentials.password
                );

                const userData: User = {
                    id: response.userId,
                    fullName: response.fullName,
                    email: response.email,
                    role: response.role,
                };

                const storage = rememberMe ? localStorage : sessionStorage;
                storage.setItem("accessToken", response.accessToken);
                storage.setItem("refreshToken", response.refreshToken);
                storage.setItem("user", JSON.stringify(userData));

                setUser(userData);
            } finally {
                setIsLoading(false);
            }
        },
        []
    );

    const logout = useCallback(async (): Promise<void> => {
        const refreshToken = getStoredItem("refreshToken");
        try {
            if (refreshToken) {
                await authService.logout(refreshToken);
            }
        } catch {
            // Silently ignore logout API errors — we clean up locally regardless
        } finally {
            clearSession();
            setUser(null);
        }
    }, []);

    return (
        <AuthContext.Provider value={{ user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

