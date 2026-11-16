package br.com.jmmotos.modules.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanoResponse {
    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private Boolean ativo;
    private List<PlanoVersaoResponse> versoes;
}

