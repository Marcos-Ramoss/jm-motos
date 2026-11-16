export interface CaucaoMovimento {
  id: number;
  contratoId: number;
  tipoMovimento: 'ENTRADA' | 'RETENCAO_DEBITO' | 'DEVOLUCAO';
  valor: number;
  dataMovimento: string;
  descricao?: string;
  comprovanteUrl?: string;
  registradoPor: string;
}

