package br.com.jmmotos.modules.fleet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MotocicletaRequest {

    @NotBlank(message = "A placa é obrigatória")
    private String placa;

    @NotBlank(message = "O chassi é obrigatório")
    private String chassi;

    @NotBlank(message = "A marca é obrigatória")
    private String marca;

    @NotBlank(message = "O modelo é obrigatório")
    private String modelo;

    @NotNull(message = "O ano é obrigatório")
    @Min(value = 1990, message = "Ano inválido")
    private Integer ano;

    private String renavam;

    @NotNull(message = "O odômetro é obrigatório")
    @PositiveOrZero(message = "O odômetro não pode ser negativo")
    private Integer odometro;

    private LocalDate dataAquisicao;

    @PositiveOrZero(message = "O custo de aquisição deve ser positivo")
    private BigDecimal custoAquisicao;

    private String municipioOperacao;
}

