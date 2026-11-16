export interface Pagamento {
  id: number;
  cobrancaId: number;
  valor: number;
  dataPagamento: string;
  formaPagamento: 'PIX' | 'DINHEIRO' | 'TRANSFERENCIA' | 'CARTAO' | 'OUTRO';
  referenciaExterna?: string;
  comprovanteUrl?: string;
  observacao?: string;
  confirmadoPor: string;
  estornado: boolean;
  estornadoEm?: string;
  estornadoPor?: string;
  motivoEstorno?: string;
}

