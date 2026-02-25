export type UserRole = "recruiter" | "admin";

export interface User {
    id: string;
    name: string;
    email: string;
    role: UserRole;
    department: string;
    phone: string;
    initials: string;
}

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface AuthContextType {
    user: User | null;
    isLoading: boolean;
    login: (credentials: LoginCredentials) => Promise<void>;
    logout: () => void;
}
