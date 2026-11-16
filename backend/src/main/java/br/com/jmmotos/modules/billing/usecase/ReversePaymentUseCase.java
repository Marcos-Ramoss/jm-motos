package br.com.jmmotos.modules.billing.usecase;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.billing.dto.PagamentoResponse;
import br.com.jmmotos.modules.billing.dto.ReversePaymentRequest;
import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import br.com.jmmotos.modules.billing.model.Pagamento;
import br.com.jmmotos.modules.billing.repository.CobrancaRepository;
import br.com.jmmotos.modules.billing.repository.PagamentoRepository;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.model.ContratoStatus;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
public class ReversePaymentUseCase {

    private final PagamentoRepository pagamentoRepository;
    private final CobrancaRepository cobrancaRepository;
    private final ContratoLocacaoRepository contratoLocacaoRepository;

    public ReversePaymentUseCase(
            PagamentoRepository pagamentoRepository,
            CobrancaRepository cobrancaRepository,
            ContratoLocacaoRepository contratoLocacaoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.cobrancaRepository = cobrancaRepository;
        this.contratoLocacaoRepository = contratoLocacaoRepository;
    }

    @Transactional
    public PagamentoResponse execute(Long pagamentoId, ReversePaymentRequest request, String usuarioAutenticado) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com ID: " + pagamentoId));

        if (Boolean.TRUE.equals(pagamento.getEstornado())) {
            throw new BusinessRuleException("Este pagamento já foi estornado anteriormente.");
        }

        pagamento.setEstornado(true);
        pagamento.setEstornadoEm(Instant.now());
        pagamento.setEstornadoPor(usuarioAutenticado != null ? usuarioAutenticado : "SISTEMA");
        pagamento.setMotivoEstorno(request.getMotivoEstorno());

        Pagamento salvo = pagamentoRepository.save(pagamento);

        Cobranca cobranca = pagamento.getCobranca();
        cobranca.recalcularSaldo();
        cobrancaRepository.save(cobranca);

        // Recalcula progresso de aquisição do contrato
        atualizarProgressoContrato(cobranca.getContrato());

        return mapToResponse(salvo);
    }

    private void atualizarProgressoContrato(ContratoLocacao contrato) {
        long unidadesQuitadas = cobrancaRepository.countByContratoIdAndStatus(contrato.getId(), CobrancaStatus.QUITADO);
        contrato.setUnidadesQuitadas((int) unidadesQuitadas);

        if (contrato.getUnidadesTotais() > 0) {
            BigDecimal percentual = BigDecimal.valueOf(unidadesQuitadas)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(contrato.getUnidadesTotais()), 2, RoundingMode.HALF_UP);
            contrato.setPercentualAquisicao(percentual);
        }

        if (contrato.getStatus() == ContratoStatus.CONCLUIDO_AGUARDANDO_TRANSFERENCIA &&
                contrato.getUnidadesQuitadas() < contrato.getUnidadesTotais()) {
            contrato.setStatus(ContratoStatus.ATIVO);
        }

        contratoLocacaoRepository.save(contrato);
    }

    private PagamentoResponse mapToResponse(Pagamento p) {
        return PagamentoResponse.builder()
                .id(p.getId())
                .cobrancaId(p.getCobranca().getId())
                .valor(p.getValor())
                .dataPagamento(p.getDataPagamento())
                .formaPagamento(p.getFormaPagamento().name())
                .referenciaExterna(p.getReferenciaExterna())
                .comprovanteUrl(p.getComprovanteUrl())
                .observacao(p.getObservacao())
                .confirmadoPor(p.getConfirmadoPor())
                .estornado(p.getEstornado())
                .estornadoEm(p.getEstornadoEm())
                .estornadoPor(p.getEstornadoPor())
                .motivoEstorno(p.getMotivoEstorno())
                .build();
    }
}

