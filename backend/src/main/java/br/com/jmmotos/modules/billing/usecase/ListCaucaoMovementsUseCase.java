package br.com.jmmotos.modules.billing.usecase;

import br.com.jmmotos.modules.billing.dto.CaucaoMovimentoResponse;
import br.com.jmmotos.modules.billing.model.CaucaoMovimento;
import br.com.jmmotos.modules.billing.repository.CaucaoMovimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListCaucaoMovementsUseCase {

    private final CaucaoMovimentoRepository caucaoMovimentoRepository;

    public ListCaucaoMovementsUseCase(CaucaoMovimentoRepository caucaoMovimentoRepository) {
        this.caucaoMovimentoRepository = caucaoMovimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<CaucaoMovimentoResponse> execute(Long contratoId) {
        List<CaucaoMovimento> movimentos = contratoId != null
                ? caucaoMovimentoRepository.findByContratoIdOrderByDataMovimentoDesc(contratoId)
                : caucaoMovimentoRepository.findAll();

        return movimentos.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private CaucaoMovimentoResponse mapToResponse(CaucaoMovimento cm) {
        return CaucaoMovimentoResponse.builder()
                .id(cm.getId())
                .contratoId(cm.getContrato().getId())
                .tipoMovimento(cm.getTipoMovimento().name())
                .valor(cm.getValor())
                .dataMovimento(cm.getDataMovimento())
                .descricao(cm.getDescricao())
                .comprovanteUrl(cm.getComprovanteUrl())
                .registradoPor(cm.getRegistradoPor())
                .build();
    }
}

