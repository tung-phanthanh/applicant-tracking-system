import axios from 'axios';

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
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Generic response interceptor to handle common errors easily
api.interceptors.response.use(
    (response) => {
        return response.data;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Potential auto-logout logic here
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            // window.location.href = '/login';
        }
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
    }
);

export default api;
