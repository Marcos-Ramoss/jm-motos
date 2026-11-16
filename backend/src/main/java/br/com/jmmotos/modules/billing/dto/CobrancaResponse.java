package br.com.jmmotos.modules.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CobrancaResponse {
    private Long id;
    private Long contratoId;
    private String numeroContrato;
    private String clienteNome;
    private String motocicletaPlaca;
    private Integer numeroParcela;
    private String tipoCobranca;
    private LocalDate dataVencimento;
    private BigDecimal valorTotal;
    private BigDecimal valorAluguel;
    private BigDecimal taxaAquisicao;
    private BigDecimal valorPago;
    private BigDecimal saldoRestante;
    private String status;
    private Instant dataPagamentoIntegral;
    private String motivoIsencao;
    private Instant isentoEm;
    private String isentoPor;
    private List<PagamentoResponse> pagamentos;
}

