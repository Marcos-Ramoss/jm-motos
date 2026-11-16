export interface PlanoVersao {
  id: number;
  planoId: number;
  versao: number;
  modalidade: 'SEMANAL' | 'DIARIO';
  valorParcela: number;
  valorAluguel: number;
  taxaAquisicao: number;
  valorCaucao: number;
  totalUnidades: number;
  diaVencimentoPadrao?: number;
  horarioLimiteVencimento?: string;
  kmTrocaOleo: number;
  trocasParaBonusOleo: number;
  toleranciaDiasInadimplencia: number;
  ativo: boolean;
}

export interface Plano {
  id: number;
  nome: string;
  codigo: string;
  descricao: string;
  ativo: boolean;
  versoes: PlanoVersao[];
}

