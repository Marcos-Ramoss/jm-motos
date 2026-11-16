export interface User {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'OPERADOR' | 'INVESTIDOR' | 'MECANICO' | 'LOCATARIO';
  ativo: boolean;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  usuario: User;
}

export interface Motocicleta {
  id: number;
  placa: string;
  chassi: string;
  marca: string;
  modelo: string;
  ano: number;
  renavam?: string;
  odometro: number;
  dataAquisicao?: string;
  custoAquisicao?: number;
  municipioOperacao: string;
  statusOperacional: 'DISPONIVEL' | 'ALUGADA' | 'EM_MANUTENCAO' | 'TRANSFERIDA' | 'INATIVA';
  statusRastreador: 'NAO_VINCULADO' | 'ONLINE' | 'OFFLINE' | 'DESCONHECIDO';
  criadoEm: string;
}

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

export interface DashboardSummary {
  totalMotos: number;
  motosDisponiveis: number;
  motosAlugadas: number;
  motosEmManutencao: number;
  motosTransferidas: number;
  contratosAtivos: number;
  cobrancasAtrasadasCount: number;
  totalReceitaRealizada: number;
  totalAReceber: number;
  totalEmAtraso: number;
  totalCaucaoCustodia: number;
  progressoMedioRentToOwn: number;
}

export interface TrackingStatus {
  provedorConfigurado: boolean;
  provedorCandidato: string;
  mensagem: string;
  instrucoesIntegracao: string;
  motocicletas: {
    motoId: number;
    placa: string;
    modelo: string;
    statusOperacional: string;
    statusRastreador: string;
    ultimaComunicacao: string;
    latitude?: number;
    longitude?: number;
  }[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

