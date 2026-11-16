package br.com.jmmotos.modules.tracking.controller;

import br.com.jmmotos.modules.tracking.dto.TrackingDeviceStatusResponse;
import br.com.jmmotos.modules.tracking.usecase.GetTrackingStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rastreamento")
@Tag(name = "Rastreamento e Telemetria", description = "Endpoints para monitoramento de dispositivos GPS e integrador Traccar")
public class TrackingController {

    private final GetTrackingStatusUseCase getTrackingStatusUseCase;

    public TrackingController(GetTrackingStatusUseCase getTrackingStatusUseCase) {
        this.getTrackingStatusUseCase = getTrackingStatusUseCase;
    }

    @GetMapping("/status")
    @Operation(summary = "Obter status da integração de telemetria e situação dos dispositivos da frota")
    public ResponseEntity<TrackingDeviceStatusResponse> getStatus() {
        TrackingDeviceStatusResponse response = getTrackingStatusUseCase.execute();
        return ResponseEntity.ok(response);
    }
}

