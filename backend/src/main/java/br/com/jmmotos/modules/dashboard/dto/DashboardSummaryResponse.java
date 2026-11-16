package br.com.jmmotos.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalMotos;
    private long motosDisponiveis;
    private long motosAlugadas;
    private long motosEmManutencao;
    private long motosTransferidas;
    private long contratosAtivos;
    private long cobrancasAtrasadasCount;
    private BigDecimal totalReceitaRealizada;
    private BigDecimal totalAReceber;
    private BigDecimal totalEmAtraso;
    private BigDecimal totalCaucaoCustodia;
    private BigDecimal progressoMedioRentToOwn;
}

