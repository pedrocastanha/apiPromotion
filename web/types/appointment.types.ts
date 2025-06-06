// Tipos para agendamentos alinhados com o backend refatorado (IDs Long)

export enum AgendamentoStatus {
  AGENDADO = 'AGENDADO',
  CONFIRMADO = 'CONFIRMADO',
  CANCELADO_PACIENTE = 'CANCELADO_PACIENTE',
  CANCELADO_CLINICA = 'CANCELADO_CLINICA',
  REALIZADO = 'REALIZADO',
  NAO_COMPARECEU = 'NAO_COMPARECEU'
}

export enum AgendamentoRecorrencia {
  UNICO = 'UNICO',
  SEMANAL = 'SEMANAL',
  QUINZENAL = 'QUINZENAL',
  MENSAL = 'MENSAL'
}

// Matches backend AgendamentoRequest
export interface AgendamentoRequest {
  profissionalId: number; // Long
  dataHoraInicio: string; // ISO 8601 format
  procedimentoId?: number; // Optional Long
  observacoes?: string;
}

// Matches backend AgendamentoRecorrenteRequest
export interface AgendamentoRecorrenteRequest {
  profissionalId: number; // Long
  dataHoraInicial: string; // ISO 8601
  recorrencia: AgendamentoRecorrencia;
  quantidadeRecorrencias: number;
  procedimentoId?: number; // Optional Long
  observacoes?: string;
}

// Matches backend CancelamentoRequest
export interface CancelamentoRequest {
  agendamentoId: number; // Long
  motivo: string;
}

// Matches backend AgendamentoResponse
export interface AgendamentoResponse {
  id: number; // Long
  clinicaId: number; // Long
  clinicaNomeFantasia?: string;
  pacienteId: number; // Long
  pacienteNome?: string;
  profissionalId: number; // Long
  profissionalNome?: string;
  procedimentoId?: number; // Long
  procedimentoNome?: string;
  dataHoraInicio: string; // ISO 8601
  dataHoraFim: string; // ISO 8601
  status: AgendamentoStatus;
  observacoes?: string;
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
  criadoPorUsuarioId?: number; // Long
  criadoPorUsuarioNome?: string;
  dataCancelamento?: string; // ISO 8601
  motivoCancelamento?: string;
  canceladoPorUsuarioId?: number; // Long
  canceladoPorUsuarioNome?: string;
}
