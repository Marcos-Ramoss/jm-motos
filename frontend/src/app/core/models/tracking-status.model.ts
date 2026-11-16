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

