import axios from 'axios';

// Helper: read token from localStorage or sessionStorage (AuthContext supports both)
function getAccessToken(): string | null {
    return localStorage.getItem('accessToken') || sessionStorage.getItem('accessToken');
}

// Create a configured axios instance
const api = axios.create({
    baseURL: 'http://localhost:8386/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to inject JWT tokens
api.interceptors.request.use(
    (config) => {
        const token = getAccessToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor: auto-logout on 401
api.interceptors.response.use(
    (response) => {
        return response.data;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Clear all token keys used by AuthContext
            ['accessToken', 'refreshToken', 'user'].forEach((key) => {
                localStorage.removeItem(key);
                sessionStorage.removeItem(key);
            });
            window.location.href = '/login';
        }
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
    }
);

export default api;
