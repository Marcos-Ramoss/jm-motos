package br.com.jmmotos.modules.tracking.usecase;

import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import br.com.jmmotos.modules.tracking.dto.TrackingDeviceStatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetTrackingStatusUseCase {

    private final MotocicletaRepository motocicletaRepository;

    public GetTrackingStatusUseCase(MotocicletaRepository motocicletaRepository) {
        this.motocicletaRepository = motocicletaRepository;
    }

    @Transactional(readOnly = true)
    public TrackingDeviceStatusResponse execute() {
        List<Motocicleta> motos = motocicletaRepository.findAll();

        List<TrackingDeviceStatusResponse.MotoTrackingItem> itens = motos.stream()
                .map(m -> TrackingDeviceStatusResponse.MotoTrackingItem.builder()
                        .motoId(m.getId())
                        .placa(m.getPlaca())
                        .modelo(m.getModelo())
                        .statusOperacional(m.getStatusOperacional().name())
                        .statusRastreador(m.getStatusRastreador().name())
                        .ultimaComunicacao("Dispositivo não vinculado")
                        .latitude(null)
                        .longitude(null)
                        .build())
                .collect(Collectors.toList());

        return TrackingDeviceStatusResponse.builder()
                .provedorConfigurado(false)
                .provedorCandidato("Traccar Open Source Tracking Server")
                .mensagem("O serviço de telemetria GPS não possui credenciais ativas ou dispositivos físicos pareados no momento.")
                .instrucoesIntegracao("Para habilitar o rastreamento em tempo real, configure as variáveis TRACCAR_URL e TRACCAR_API_TOKEN no arquivo .env e vincule os identificadores IMEI aos veículos.")
                .motocicletas(itens)
                .build();
    }
}

