package br.com.jmmotos.modules.rental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContractRequest {

    @NotNull(message = "O cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "A motocicleta é obrigatória")
    private Long motocicletaId;

    @NotNull(message = "A versão do plano é obrigatória")
    private Long planoVersaoId;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate dataInicio;

    private Integer diaVencimentoPersonalizado;

    private String observacoes;
}

