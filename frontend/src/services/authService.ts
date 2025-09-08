import { api } from '../utils/api';
import { AuthResponse, LoginRequest, RegisterRequest } from '../types';

export const authService = {
    login: async (credentials: LoginRequest): Promise<AuthResponse> => {
        const response = await api.post('/auth/login', credentials);
        return response.data;
    },

    register: async (userData: RegisterRequest): Promise<AuthResponse> => {
        const response = await api.post('/auth/register', userData);
        return response.data;
    },

    logout: () => {
        localStorage.removeItem('jwt_token');
        window.location.href = '/login';
    },
};