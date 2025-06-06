import api from './api';
import { Clinica } from '../types/clinic.types';
import { User } from '../types/user.types';

const ClinicService = {
  // Get all clinics
  getAllClinics: async () => {
    const response = await api.get('/clinicas');
    return response.data;
  },
  
  // Get clinic by ID
  getClinicById: async (clinicaId: number) => {
    const response = await api.get(`/clinicas/${clinicaId}`);
    return response.data;
  },
  
  // Create new clinic
  createClinic: async (clinicData: Partial<Clinica>) => {
    const response = await api.post('/clinicas', clinicData);
    return response.data;
  },
  
  // Update clinic
  updateClinic: async (clinicaId: number, clinicData: Partial<Clinica>) => {
    const response = await api.put(`/clinicas/${clinicaId}`, clinicData);
    return response.data;
  },
  
  // Get professionals by clinic
  getProfessionalsByClinic: async (clinicaId: number) => {
    const response = await api.get(`/clinicas/${clinicaId}/profissionais`);
    return response.data;
  },
  
  // Add professional to clinic
  addProfessionalToClinic: async (clinicaId: number, profissionalId: number) => {
    const response = await api.post(`/clinicas/${clinicaId}/profissionais/${profissionalId}`);
    return response.data;
  },
  
  // Remove professional from clinic
  removeProfessionalFromClinic: async (clinicaId: number, profissionalId: number) => {
    const response = await api.delete(`/clinicas/${clinicaId}/profissionais/${profissionalId}`);
    return response.data;
  },
  
  // Get procedures by clinic
  getProceduresByClinic: async (clinicaId: number) => {
    const response = await api.get(`/clinicas/${clinicaId}/procedimentos`);
    return response.data;
  },
  
  // Add procedure to clinic
  addProcedureToClinic: async (clinicaId: number, procedimentoData: any) => {
    const response = await api.post(`/clinicas/${clinicaId}/procedimentos`, procedimentoData);
    return response.data;
  },
  
  // Update procedure
  updateProcedure: async (clinicaId: number, procedimentoId: number, procedimentoData: any) => {
    const response = await api.put(`/clinicas/${clinicaId}/procedimentos/${procedimentoId}`, procedimentoData);
    return response.data;
  },
  
  // Delete procedure
  deleteProcedure: async (clinicaId: number, procedimentoId: number) => {
    const response = await api.delete(`/clinicas/${clinicaId}/procedimentos/${procedimentoId}`);
    return response.data;
  }
};

export default ClinicService;
