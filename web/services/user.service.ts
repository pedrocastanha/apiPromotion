import api from './api';
import { User, ProfissionalInfo, PacienteInfo } from '../types/user.types';

const UserService = {
  // Get all users (admin only)
  getAllUsers: async () => {
    const response = await api.get('/admin/users');
    return response.data;
  },
  
  // Get user by ID
  getUserById: async (userId: number) => {
    const response = await api.get(`/users/${userId}`);
    return response.data;
  },
  
  // Create new professional
  createProfessional: async (userData: User & ProfissionalInfo) => {
    const response = await api.post('/admin/profissionais', userData);
    return response.data;
  },
  
  // Update user
  updateUser: async (userId: number, userData: Partial<User>) => {
    const response = await api.put(`/users/${userId}`, userData);
    return response.data;
  },
  
  // Get professional info
  getProfessionalInfo: async (profissionalId: number) => {
    const response = await api.get(`/profissionais/${profissionalId}`);
    return response.data;
  },
  
  // Update professional info
  updateProfessionalInfo: async (profissionalId: number, profissionalData: Partial<ProfissionalInfo>) => {
    const response = await api.put(`/profissionais/${profissionalId}`, profissionalData);
    return response.data;
  },
  
  // Get patient info
  getPatientInfo: async (pacienteId: number) => {
    const response = await api.get(`/pacientes/${pacienteId}`);
    return response.data;
  },
  
  // Update patient info
  updatePatientInfo: async (pacienteId: number, pacienteData: Partial<PacienteInfo>) => {
    const response = await api.put(`/pacientes/${pacienteId}`, pacienteData);
    return response.data;
  },
  
  // Get professional working hours
  getProfessionalWorkingHours: async (profissionalId: number) => {
    const response = await api.get(`/profissionais/${profissionalId}/horarios`);
    return response.data;
  },
  
  // Set professional working hours
  setProfessionalWorkingHours: async (profissionalId: number, horariosData: any) => {
    const response = await api.post(`/profissionais/${profissionalId}/horarios`, horariosData);
    return response.data;
  },
  
  // Get professional exceptions
  getProfessionalExceptions: async (profissionalId: number) => {
    const response = await api.get(`/profissionais/${profissionalId}/excecoes`);
    return response.data;
  },
  
  // Add professional exception
  addProfessionalException: async (profissionalId: number, excecaoData: any) => {
    const response = await api.post(`/profissionais/${profissionalId}/excecoes`, excecaoData);
    return response.data;
  },
  
  // Delete professional exception
  deleteProfessionalException: async (profissionalId: number, excecaoId: number) => {
    const response = await api.delete(`/profissionais/${profissionalId}/excecoes/${excecaoId}`);
    return response.data;
  }
};

export default UserService;
