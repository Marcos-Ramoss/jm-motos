package br.com.jmmotos.modules.fleet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotocicletaResponse {
    private Long id;
    private String placa;
    private String chassi;
    private String marca;
    private String modelo;
    private Integer ano;
    private String renavam;
    private Integer odometro;
    private LocalDate dataAquisicao;
    private BigDecimal custoAquisicao;
    private String municipioOperacao;
    private String statusOperacional;
    private String statusRastreador;
    private Instant criadoEm;
    private Instant atualizadoEm;
}

