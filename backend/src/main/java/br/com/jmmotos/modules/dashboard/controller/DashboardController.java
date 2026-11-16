package br.com.jmmotos.modules.dashboard.controller;

import br.com.jmmotos.modules.dashboard.dto.DashboardSummaryResponse;
import br.com.jmmotos.modules.dashboard.usecase.GetDashboardSummaryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Endpoints de consolidação e métricas operacionais e financeiras reais")
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    public DashboardController(GetDashboardSummaryUseCase getDashboardSummaryUseCase) {
        this.getDashboardSummaryUseCase = getDashboardSummaryUseCase;
    }

    @GetMapping("/resumo")
    @Operation(summary = "Obter resumo consolidado da frota, faturamento e inadimplência")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        DashboardSummaryResponse response = getDashboardSummaryUseCase.execute();
        return ResponseEntity.ok(response);
    }
}

