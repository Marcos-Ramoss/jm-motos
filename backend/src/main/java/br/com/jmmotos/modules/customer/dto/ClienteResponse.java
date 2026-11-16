package br.com.jmmotos.modules.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Long id;
    private String nome;
    private String cpf;
    private String cpfMascarado;
    private String rg;
    private String cnhNumero;
    private String cnhCategoria;
    private LocalDate cnhValidade;
    private String telefone;
    private String endereco;
    private String municipio;
    private String status;
    private Instant criadoEm;
    private Instant atualizadoEm;
}

