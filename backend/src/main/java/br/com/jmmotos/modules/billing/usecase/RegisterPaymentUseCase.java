package br.com.jmmotos.modules.billing.usecase;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ConflictException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.billing.dto.PagamentoResponse;
import br.com.jmmotos.modules.billing.dto.RegisterPaymentRequest;
import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import br.com.jmmotos.modules.billing.model.FormaPagamento;
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
public class RegisterPaymentUseCase {

    private final CobrancaRepository cobrancaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ContratoLocacaoRepository contratoLocacaoRepository;

    public RegisterPaymentUseCase(
            CobrancaRepository cobrancaRepository,
            PagamentoRepository pagamentoRepository,
            ContratoLocacaoRepository contratoLocacaoRepository) {
        this.cobrancaRepository = cobrancaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.contratoLocacaoRepository = contratoLocacaoRepository;
    }

    @Transactional
    public PagamentoResponse execute(RegisterPaymentRequest request, String usuarioAutenticado) {
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            if (pagamentoRepository.findByIdempotencyKey(request.getIdempotencyKey()).isPresent()) {
                throw new ConflictException("Pagamento com esta chave de idempotência já foi processado.");
            }
        }

        Cobranca cobranca = cobrancaRepository.findById(request.getCobrancaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cobrança não encontrada com ID: " + request.getCobrancaId()));

        if (cobranca.getStatus() == CobrancaStatus.QUITADO) {
            throw new BusinessRuleException("Esta cobrança já se encontra integralmente quitada.");
        }
        if (cobranca.getStatus() == CobrancaStatus.ISENTO) {
            throw new BusinessRuleException("Esta cobrança está marcada como ISENTA e não pode receber pagamentos.");
        }

        if (request.getValor().compareTo(cobranca.getSaldoRestante()) > 0) {
            throw new BusinessRuleException("O valor do pagamento (R$ " + request.getValor() +
                    ") não pode ser maior que o saldo restante da cobrança (R$ " + cobranca.getSaldoRestante() + ").");
        }

        FormaPagamento forma;
        try {
            forma = FormaPagamento.valueOf(request.getFormaPagamento().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Forma de pagamento inválida: " + request.getFormaPagamento());
        }

        Pagamento pagamento = Pagamento.builder()
                .cobranca(cobranca)
                .valor(request.getValor())
                .dataPagamento(Instant.now())
                .formaPagamento(forma)
                .referenciaExterna(request.getReferenciaExterna())
                .comprovanteUrl(request.getComprovanteUrl())
                .observacao(request.getObservacao())
                .confirmadoPor(usuarioAutenticado != null ? usuarioAutenticado : "SISTEMA")
                .estornado(false)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        Pagamento salvo = pagamentoRepository.save(pagamento);

        cobranca.getPagamentos().add(salvo);
        cobranca.recalcularSaldo();
        cobrancaRepository.save(cobranca);

        // Atualização do Rent-to-Own no contrato
        atualizarProgressoAquisicaoContrato(cobranca.getContrato());

        return mapToResponse(salvo);
    }

    private void atualizarProgressoAquisicaoContrato(ContratoLocacao contrato) {
        long unidadesQuitadas = cobrancaRepository.countByContratoIdAndStatus(contrato.getId(), CobrancaStatus.QUITADO);
        contrato.setUnidadesQuitadas((int) unidadesQuitadas);

        if (contrato.getUnidadesTotais() > 0) {
            BigDecimal percentual = BigDecimal.valueOf(unidadesQuitadas)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(contrato.getUnidadesTotais()), 2, RoundingMode.HALF_UP);
            contrato.setPercentualAquisicao(percentual);
        }

        if (contrato.getUnidadesQuitadas() >= contrato.getUnidadesTotais()) {
            contrato.setStatus(ContratoStatus.CONCLUIDO_AGUARDANDO_TRANSFERENCIA);
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

