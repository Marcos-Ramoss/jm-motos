import { Cobranca } from './cobranca.model';
import { CaucaoMovimento } from './caucao-movimento.model';

export interface Contrato {
  id: number;
  numeroContrato: string;
  clienteId: number;
  clienteNome: string;
  clienteCpf: string;
  clienteTelefone?: string;
  motocicletaId: number;
  motocicletaPlaca: string;
  motocicletaModelo: string;
  motocicletaStatus?: string;
  planoVersaoId: number;
  status: 'RASCUNHO' | 'ATIVO' | 'SUSPENSO' | 'CONCLUIDO_AGUARDANDO_TRANSFERENCIA' | 'TRANSFERIDO' | 'RESCINDIDO' | 'CANCELADO';
  dataInicio: string;
  dataTerminoPrevista?: string;
  dataEncerramentoReal?: string;
  snapshotModalidade: string;
  snapshotValorParcela: number;
  snapshotValorAluguel: number;
  snapshotTaxaAquisicao: number;
  snapshotValorCaucao: number;
  snapshotTotalUnidades: number;
  snapshotDiaVencimento?: number;
  unidadesTotais: number;
  unidadesQuitadas: number;
  percentualAquisicao: number;
  saldoCaucaoAtual?: number;
  observacoes?: string;
  criadoEm: string;
  cobrancas?: Cobranca[];
  movimentosCaucao?: CaucaoMovimento[];
}

