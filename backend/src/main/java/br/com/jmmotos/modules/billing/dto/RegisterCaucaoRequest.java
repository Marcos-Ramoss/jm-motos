package br.com.jmmotos.modules.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class RegisterCaucaoRequest {

    @NotNull(message = "O contrato é obrigatório")
    private Long contratoId;

    @NotBlank(message = "O tipo de movimento é obrigatório (ENTRADA, RETENCAO_DEBITO, DEVOLUCAO)")
    private String tipoMovimento;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    private String descricao;

    private String comprovanteUrl;
}

