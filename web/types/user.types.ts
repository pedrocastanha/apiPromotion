export interface User {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  cpf?: string;
  role: 'ADMIN' | 'PSICOLOGA' | 'PACIENTE';
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ProfissionalInfo {
  id: number;
  userId: number;
  registroProfissional: string;
  especialidade: string;
  biografia?: string;
  fotoPerfil?: string;
}

export interface PacienteInfo {
  id: number;
  userId: number;
  dataNascimento?: string;
  genero?: string;
  endereco?: string;
  informacoesAdicionais?: string;
}

export interface HorarioTrabalho {
  id: number;
  profissionalId: number;
  diaSemana: number; // 0-6 (Domingo-Sábado)
  horaInicio: string; // HH:MM
  horaFim: string; // HH:MM
}

export interface ExcecaoHorario {
  id: number;
  profissionalId: number;
  data: string; // YYYY-MM-DD
  horaInicio?: string; // HH:MM (se null, dia todo)
  horaFim?: string; // HH:MM (se null, dia todo)
  motivo?: string;
  tipo: 'FOLGA' | 'HORARIO_ESPECIAL';
}
