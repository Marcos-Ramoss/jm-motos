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
public class CaucaoMovimentoResponse {
    private Long id;
    private Long contratoId;
    private String tipoMovimento;
    private BigDecimal valor;
    private Instant dataMovimento;
    private String descricao;
    private String comprovanteUrl;
    private String registradoPor;
}

