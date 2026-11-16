package br.com.jmmotos.modules.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingDeviceStatusResponse {
    private boolean provedorConfigurado;
    private String provedorCandidato;
    private String mensagem;
    private String instrucoesIntegracao;
    private List<MotoTrackingItem> motocicletas;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MotoTrackingItem {
        private Long motoId;
        private String placa;
        private String modelo;
        private String statusOperacional;
        private String statusRastreador;
        private String ultimaComunicacao;
        private Double latitude;
        private Double longitude;
    }
}

