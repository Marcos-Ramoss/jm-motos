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
public class RegisterPaymentRequest {

    @NotNull(message = "A cobrança é obrigatória")
    private Long cobrancaId;

    @NotNull(message = "O valor do pagamento é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotBlank(message = "A forma de pagamento é obrigatória")
    private String formaPagamento;

    private String referenciaExterna;

    private String comprovanteUrl;

    private String observacao;

    private String idempotencyKey;
}

