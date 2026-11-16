package br.com.jmmotos.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ClienteRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    private String rg;

    @NotBlank(message = "O número da CNH é obrigatório")
    private String cnhNumero;

    private String cnhCategoria;

    @NotNull(message = "A validade da CNH é obrigatória")
    private LocalDate cnhValidade;

    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;

    private String endereco;

    private String municipio;
}

