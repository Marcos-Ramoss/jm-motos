package br.com.jmmotos.modules.rental.usecase;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import br.com.jmmotos.modules.rental.dto.ContratoResponse;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.model.ContratoStatus;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CancelContractUseCase {

    private final ContratoLocacaoRepository contratoLocacaoRepository;
    private final MotocicletaRepository motocicletaRepository;

    public CancelContractUseCase(
            ContratoLocacaoRepository contratoLocacaoRepository,
            MotocicletaRepository motocicletaRepository) {
        this.contratoLocacaoRepository = contratoLocacaoRepository;
        this.motocicletaRepository = motocicletaRepository;
    }

    @Transactional
    public ContratoResponse execute(Long id, String motivo) {
        ContratoLocacao contrato = contratoLocacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com ID: " + id));

        if (contrato.getStatus() == ContratoStatus.CANCELADO || contrato.getStatus() == ContratoStatus.TRANSFERIDO) {
            throw new BusinessRuleException("O contrato já se encontra encerrado.");
        }

        contrato.setStatus(ContratoStatus.CANCELADO);
        contrato.setDataEncerramentoReal(LocalDate.now());
        if (motivo != null && !motivo.isBlank()) {
            String obsAtual = contrato.getObservacoes() != null ? contrato.getObservacoes() + " | " : "";
            contrato.setObservacoes(obsAtual + "Cancelado em " + LocalDate.now() + ". Motivo: " + motivo);
        }

        ContratoLocacao salvo = contratoLocacaoRepository.save(contrato);

        // Libera a motocicleta
        Motocicleta moto = contrato.getMotocicleta();
        moto.setStatusOperacional(StatusOperacional.DISPONIVEL);
        motocicletaRepository.save(moto);

        return mapToResponse(salvo);
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

