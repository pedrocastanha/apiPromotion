export interface Clinica {
  id: number;
  nomeFantasia: string;
  razaoSocial: string;
  cnpj: string;
  telefone: string;
  email?: string;
  endereco: string;
  cidade: string;
  estado: string;
  cep: string;
  logo?: string;
  ativa: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Procedimento {
  id: number;
  clinicaId: number;
  nome: string;
  descricao?: string;
  duracao: number; // em minutos
  valor: number;
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}
