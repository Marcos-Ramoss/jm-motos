package br.com.jmmotos.modules.billing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExemptBillingRequest {

    @NotBlank(message = "O motivo da isenção por oficina/manutenção é obrigatório")
    private String motivoIsencao;
}

