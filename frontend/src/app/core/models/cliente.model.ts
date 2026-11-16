export interface Cliente {
  id: number;
  nome: string;
  cpf: string;
  cpfMascarado: string;
  rg?: string;
  cnhNumero: string;
  cnhCategoria: string;
  cnhValidade: string;
  telefone: string;
  endereco?: string;
  municipio?: string;
  status: 'ATIVO' | 'INATIVO' | 'BLOQUEADO';
  criadoEm: string;
}

