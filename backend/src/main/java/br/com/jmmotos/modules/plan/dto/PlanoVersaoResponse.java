package br.com.jmmotos.modules.plan.dto;

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
public class PlanoVersaoResponse {
    private Long id;
    private Long planoId;
    private Integer versao;
    private String modalidade;
    private BigDecimal valorParcela;
    private BigDecimal valorAluguel;
    private BigDecimal taxaAquisicao;
    private BigDecimal valorCaucao;
    private Integer totalUnidades;
    private Integer diaVencimentoPadrao;
    private String horarioLimiteVencimento;
    private Integer kmTrocaOleo;
    private Integer trocasParaBonusOleo;
    private Integer toleranciaDiasInadimplencia;
    private Boolean ativo;
}

