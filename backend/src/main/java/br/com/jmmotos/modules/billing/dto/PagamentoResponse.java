package br.com.jmmotos.modules.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponse {
    private Long id;
    private Long cobrancaId;
    private BigDecimal valor;
    private Instant dataPagamento;
    private String formaPagamento;
    private String referenciaExterna;
    private String comprovanteUrl;
    private String observacao;
    private String confirmadoPor;
    private Boolean estornado;
    private Instant estornadoEm;
    private String estornadoPor;
    private String motivoEstorno;
}

