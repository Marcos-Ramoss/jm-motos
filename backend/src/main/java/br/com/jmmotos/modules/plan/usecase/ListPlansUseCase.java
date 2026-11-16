package br.com.jmmotos.modules.plan.usecase;

import br.com.jmmotos.modules.plan.dto.PlanoResponse;
import br.com.jmmotos.modules.plan.dto.PlanoVersaoResponse;
import br.com.jmmotos.modules.plan.model.PlanoLocacao;
import br.com.jmmotos.modules.plan.model.PlanoVersao;
import br.com.jmmotos.modules.plan.repository.PlanoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListPlansUseCase {

    private final PlanoLocacaoRepository planoLocacaoRepository;

    public ListPlansUseCase(PlanoLocacaoRepository planoLocacaoRepository) {
        this.planoLocacaoRepository = planoLocacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanoResponse> execute() {
        List<PlanoLocacao> planos = planoLocacaoRepository.findByAtivoTrue();
        return planos.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private PlanoResponse mapToResponse(PlanoLocacao plano) {
        List<PlanoVersaoResponse> versoes = plano.getVersoes().stream()
                .filter(PlanoVersao::getAtivo)
                .map(this::mapVersaoToResponse)
                .collect(Collectors.toList());

        return PlanoResponse.builder()
                .id(plano.getId())
                .nome(plano.getNome())
                .codigo(plano.getCodigo())
                .descricao(plano.getDescricao())
                .ativo(plano.getAtivo())
                .versoes(versoes)
                .build();
    }

    private PlanoVersaoResponse mapVersaoToResponse(PlanoVersao v) {
        return PlanoVersaoResponse.builder()
                .id(v.getId())
                .planoId(v.getPlano().getId())
                .versao(v.getVersao())
                .modalidade(v.getModalidade().name())
                .valorParcela(v.getValorParcela())
                .valorAluguel(v.getValorAluguel())
                .taxaAquisicao(v.getTaxaAquisicao())
                .valorCaucao(v.getValorCaucao())
                .totalUnidades(v.getTotalUnidades())
                .diaVencimentoPadrao(v.getDiaVencimentoPadrao())
                .horarioLimiteVencimento(v.getHorarioLimiteVencimento())
                .kmTrocaOleo(v.getKmTrocaOleo())
                .trocasParaBonusOleo(v.getTrocasParaBonusOleo())
                .toleranciaDiasInadimplencia(v.getToleranciaDiasInadimplencia())
                .ativo(v.getAtivo())
                .build();
    }
}

