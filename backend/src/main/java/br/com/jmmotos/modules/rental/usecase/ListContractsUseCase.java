package br.com.jmmotos.modules.rental.usecase;

import br.com.jmmotos.modules.rental.dto.ContratoResponse;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.model.ContratoStatus;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListContractsUseCase {

    private final ContratoLocacaoRepository contratoLocacaoRepository;

    public ListContractsUseCase(ContratoLocacaoRepository contratoLocacaoRepository) {
        this.contratoLocacaoRepository = contratoLocacaoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ContratoResponse> execute(String busca, String statusStr, Pageable pageable) {
        ContratoStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = ContratoStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        String termoBusca = (busca != null && !busca.isBlank()) ? busca.trim() : null;
        Page<ContratoLocacao> page = contratoLocacaoRepository.findWithFilters(termoBusca, status, pageable);
        return page.map(this::mapToResponse);
    }

    private ContratoResponse mapToResponse(ContratoLocacao c) {
        return ContratoResponse.builder()
                .id(c.getId())
                .numeroContrato(c.getNumeroContrato())
                .clienteId(c.getCliente().getId())
                .clienteNome(c.getCliente().getNome())
                .clienteCpf(c.getCliente().getCpf())
                .motocicletaId(c.getMotocicleta().getId())
                .motocicletaPlaca(c.getMotocicleta().getPlaca())
                .motocicletaModelo(c.getMotocicleta().getModelo())
                .planoVersaoId(c.getPlanoVersao().getId())
                .status(c.getStatus().name())
                .dataInicio(c.getDataInicio())
                .dataTerminoPrevista(c.getDataTerminoPrevista())
                .dataEncerramentoReal(c.getDataEncerramentoReal())
                .snapshotModalidade(c.getSnapshotModalidade())
                .snapshotValorParcela(c.getSnapshotValorParcela())
                .snapshotValorAluguel(c.getSnapshotValorAluguel())
                .snapshotTaxaAquisicao(c.getSnapshotTaxaAquisicao())
                .snapshotValorCaucao(c.getSnapshotValorCaucao())
                .snapshotTotalUnidades(c.getSnapshotTotalUnidades())
                .snapshotDiaVencimento(c.getSnapshotDiaVencimento())
                .unidadesTotais(c.getUnidadesTotais())
                .unidadesQuitadas(c.getUnidadesQuitadas())
                .percentualAquisicao(c.getPercentualAquisicao())
                .observacoes(c.getObservacoes())
                .criadoEm(c.getCriadoEm())
                .build();
    }
}

