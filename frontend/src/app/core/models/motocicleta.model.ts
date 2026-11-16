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

