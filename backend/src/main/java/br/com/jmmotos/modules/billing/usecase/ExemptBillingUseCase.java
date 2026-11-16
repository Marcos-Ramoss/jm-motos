package br.com.jmmotos.modules.billing.usecase;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.billing.dto.CobrancaResponse;
import br.com.jmmotos.modules.billing.dto.ExemptBillingRequest;
import br.com.jmmotos.modules.billing.dto.PagamentoResponse;
import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import br.com.jmmotos.modules.billing.model.Pagamento;
import br.com.jmmotos.modules.billing.repository.CobrancaRepository;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExemptBillingUseCase {

    private final CobrancaRepository cobrancaRepository;
    private final ContratoLocacaoRepository contratoLocacaoRepository;

    public ExemptBillingUseCase(
            CobrancaRepository cobrancaRepository,
            ContratoLocacaoRepository contratoLocacaoRepository) {
        this.cobrancaRepository = cobrancaRepository;
        this.contratoLocacaoRepository = contratoLocacaoRepository;
    }

    @Transactional
    public CobrancaResponse execute(Long cobrancaId, ExemptBillingRequest request, String usuarioAutenticado) {
        Cobranca cobranca = cobrancaRepository.findById(cobrancaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cobrança não encontrada com ID: " + cobrancaId));

        if (cobranca.getStatus() == CobrancaStatus.QUITADO) {
            throw new BusinessRuleException("Não é possível isentar uma cobrança que já foi integralmente quitada.");
        }
        if (cobranca.getValorPago().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("Cobrança possui pagamentos parciais ativos. Estorne os pagamentos antes de conceder isenção.");
        }

        cobranca.setStatus(CobrancaStatus.ISENTO);
        cobranca.setMotivoIsencao(request.getMotivoIsencao());
        cobranca.setIsentoEm(Instant.now());
        cobranca.setIsentoPor(usuarioAutenticado != null ? usuarioAutenticado : "SISTEMA");
        cobranca.setSaldoRestante(BigDecimal.ZERO);

        Cobranca salva = cobrancaRepository.save(cobranca);

        // Recalcula progresso do contrato (isenções não contam como unidades quitadas)
        atualizarProgressoContrato(salva.getContrato());

        return mapToResponse(salva);
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
        contratoLocacaoRepository.save(contrato);
    }

    private CobrancaResponse mapToResponse(Cobranca cob) {
        List<PagamentoResponse> pags = cob.getPagamentos().stream()
                .map(this::mapPagamentoToResponse)
                .collect(Collectors.toList());

        return CobrancaResponse.builder()
                .id(cob.getId())
                .contratoId(cob.getContrato().getId())
                .numeroContrato(cob.getContrato().getNumeroContrato())
                .clienteNome(cob.getContrato().getCliente().getNome())
                .motocicletaPlaca(cob.getContrato().getMotocicleta().getPlaca())
                .numeroParcela(cob.getNumeroParcela())
                .tipoCobranca(cob.getTipoCobranca().name())
                .dataVencimento(cob.getDataVencimento())
                .valorTotal(cob.getValorTotal())
                .valorAluguel(cob.getValorAluguel())
                .taxaAquisicao(cob.getTaxaAquisicao())
                .valorPago(cob.getValorPago())
                .saldoRestante(cob.getSaldoRestante())
                .status(cob.getStatus().name())
                .dataPagamentoIntegral(cob.getDataPagamentoIntegral())
                .motivoIsencao(cob.getMotivoIsencao())
                .isentoEm(cob.getIsentoEm())
                .isentoPor(cob.getIsentoPor())
                .pagamentos(pags)
                .build();
    }

    private PagamentoResponse mapPagamentoToResponse(Pagamento p) {
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

