import api from './api';
import { AgendamentoRequest, AgendamentoRecorrenteRequest, CancelamentoRequest, AgendamentoResponse, AgendamentoStatus } from '../types/appointment.types';
import AuthService from './auth.service';

const AgendamentoService = {

  // --- Generic or Role-Based Endpoints --- 

  // Get a specific appointment by ID (requires appropriate permissions)
  getAgendamentoById: async (agendamentoId: number): Promise<AgendamentoResponse> => {
    // Backend needs to handle authorization based on user role/ownership
    const response = await api.get<AgendamentoResponse>(`/agendamentos/${agendamentoId}`);
    return response.data;
  },

  // --- Patient-Specific Services --- 

  // Get appointments for the currently logged-in patient
  getMeusAgendamentos: async (): Promise<AgendamentoResponse[]> => {
    // Backend uses the authenticated user context
    const response = await api.get<AgendamentoResponse[]>("/agendamentos/meus/paciente");
    return response.data;
  },

  // Patient creates an appointment
  criarMeuAgendamento: async (appointmentData: AgendamentoRequest): Promise<AgendamentoResponse> => {
    // Backend infers pacienteId and clinicaId (if applicable) from context
    const response = await api.post<AgendamentoResponse>("/agendamentos", appointmentData);
    return response.data;
  },

  // Patient creates a recurrent appointment
  criarMeuAgendamentoRecorrente: async (appointmentData: AgendamentoRecorrenteRequest): Promise<AgendamentoResponse[]> => {
    // Backend infers pacienteId and clinicaId (if applicable) from context
    const response = await api.post<AgendamentoResponse[]>("/agendamentos/recorrentes", appointmentData);
    return response.data;
  },

  // Patient cancels their own appointment
  cancelarMeuAgendamento: async (agendamentoId: number, cancelData: CancelamentoRequest): Promise<void> => {
    // Backend uses authenticated user context to verify ownership and apply rules
    await api.post(`/agendamentos/${agendamentoId}/cancelar/paciente`, cancelData);
  },

  // --- Professional-Specific Services --- 

  // Get appointments for the currently logged-in professional
  getMinhaAgendaProfissional: async (startDate: string, endDate: string): Promise<AgendamentoResponse[]> => {
    // Backend uses authenticated user context
    const response = await api.get<AgendamentoResponse[]>("/agendamentos/meus/profissional", {
      params: { inicio: startDate, fim: endDate }
    });
    return response.data;
  },

  // Get today's appointments for the currently logged-in professional
  getMinhaAgendaHoje: async (): Promise<AgendamentoResponse[]> => {
    const today = new Date().toISOString().split('T')[0];
    const response = await api.get<AgendamentoResponse[]>("/agendamentos/meus/profissional/hoje", {
      params: { data: today }
    });
    return response.data;
  },

  // Professional confirms an appointment
  confirmarAgendamento: async (agendamentoId: number): Promise<AgendamentoResponse> => {
    const response = await api.post<AgendamentoResponse>(`/agendamentos/${agendamentoId}/confirmar`, {});
    return response.data;
  },

  // Professional marks appointment as completed
  marcarComoRealizado: async (agendamentoId: number): Promise<AgendamentoResponse> => {
    const response = await api.post<AgendamentoResponse>(`/agendamentos/${agendamentoId}/realizado`, {});
    return response.data;
  },

  // Professional marks patient as no-show
  marcarComoNaoCompareceu: async (agendamentoId: number): Promise<AgendamentoResponse> => {
    const response = await api.post<AgendamentoResponse>(`/agendamentos/${agendamentoId}/nao-compareceu`, {});
    return response.data;
  },

  // Professional cancels appointment
  cancelarAgendamentoProfissional: async (agendamentoId: number, cancelData: CancelamentoRequest): Promise<void> => {
    await api.post(`/agendamentos/${agendamentoId}/cancelar/profissional`, cancelData);
  },

  // --- Admin/Staff Services --- 

  // Get all appointments for a specific clinic (requires ADMIN role)
  getAgendamentosDaClinica: async (clinicaId: number, startDate?: string, endDate?: string): Promise<AgendamentoResponse[]> => {
    const response = await api.get<AgendamentoResponse[]>(`/clinicas/${clinicaId}/agendamentos`, {
      params: { inicio: startDate, fim: endDate } // Optional date range
    });
    return response.data;
  },

  // Get all appointments across all clinics (requires ADMIN role)
  getAllAgendamentos: async (startDate?: string, endDate?: string): Promise<AgendamentoResponse[]> => {
    const response = await api.get<AgendamentoResponse[]>(`/admin/agendamentos`, {
      params: { inicio: startDate, fim: endDate } // Optional date range
    });
    return response.data;
  },

  // Get appointments for a specific professional within a clinic
  getAgendaProfissionalDaClinica: async (clinicaId: number, profissionalId: number, startDate: string, endDate: string): Promise<AgendamentoResponse[]> => {
    const response = await api.get<AgendamentoResponse[]>(`/clinicas/${clinicaId}/profissionais/${profissionalId}/agenda`, {
      params: { inicio: startDate, fim: endDate }
    });
    return response.data;
  },

  // Staff creates an appointment for a patient
  criarAgendamentoParaPaciente: async (clinicaId: number, pacienteId: number, appointmentData: AgendamentoRequest): Promise<AgendamentoResponse> => {
    const response = await api.post<AgendamentoResponse>(`/clinicas/${clinicaId}/agendamentos`, {
      ...appointmentData,
      pacienteId: pacienteId // Explicitly pass pacienteId if needed by backend endpoint
    });
    return response.data;
  },

  // Staff cancels any appointment within their clinic
  cancelarAgendamentoPelaClinica: async (clinicaId: number, agendamentoId: number, cancelData: CancelamentoRequest): Promise<void> => {
    // Backend needs clinicaId context for authorization
    await api.post(`/clinicas/${clinicaId}/agendamentos/${agendamentoId}/cancelar`, cancelData);
  },
};

export default AgendamentoService;
