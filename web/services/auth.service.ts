import api from './api';
import { User } from '../types/user.types';

const AuthService = {
  // Login user
  login: async (credentials: { email: string; senha: string }) => {
    const response = await api.post('/auth/login', credentials);
    const { token, user } = response.data;
    
    // Store token and user data in localStorage
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    
    return response.data;
  },
  
  // Register new user
  register: async (userData: any) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },
  
  // Check if user is authenticated
  isAuthenticated: () => {
    const token = localStorage.getItem('token');
    return !!token;
  },
  
  // Get current user data
  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  },
  
  // Get user role
  getUserRole: () => {
    const user = AuthService.getCurrentUser();
    return user?.role || null;
  },
  
  // Logout user
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  
  // Request password reset
  requestPasswordReset: async (email: string) => {
    const response = await api.post('/auth/request-reset', { email });
    return response.data;
  },
  
  // Reset password with token
  resetPassword: async (token: string, newPassword: string) => {
    const response = await api.post('/auth/reset-password', { token, newPassword });
    return response.data;
  },
  
  // Update user profile
  updateProfile: async (userId: number, userData: Partial<User>) => {
    const response = await api.put(`/users/${userId}`, userData);
    
    // Update stored user data
    const currentUser = AuthService.getCurrentUser();
    if (currentUser && currentUser.id === userId) {
      localStorage.setItem('user', JSON.stringify({
        ...currentUser,
        ...response.data
      }));
    }
    
    return response.data;
  }
};

export default AuthService;
