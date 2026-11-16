import { Pagamento } from './pagamento.model';

export interface Cobranca {
  id: number;
  contratoId: number;
  numeroContrato: string;
  clienteNome: string;
  motocicletaPlaca: string;
  numeroParcela: number;
  tipoCobranca: 'DIARIA' | 'SEMANAL' | 'CAUCAO';
  dataVencimento: string;
  valorTotal: number;
  valorAluguel: number;
  taxaAquisicao: number;
  valorPago: number;
  saldoRestante: number;
  status: 'PENDENTE' | 'PAGO_PARCIAL' | 'QUITADO' | 'ATRASADO' | 'ISENTO' | 'CANCELADO';
  dataPagamentoIntegral?: string;
  motivoIsencao?: string;
  isentoEm?: string;
  isentoPor?: string;
  pagamentos?: Pagamento[];
}

