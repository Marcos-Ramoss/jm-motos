package br.com.jmmotos.modules.billing.usecase;

import br.com.jmmotos.modules.billing.dto.CobrancaResponse;
import br.com.jmmotos.modules.billing.dto.PagamentoResponse;
import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import br.com.jmmotos.modules.billing.model.Pagamento;
import br.com.jmmotos.modules.billing.repository.CobrancaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListBillingsUseCase {

    private final CobrancaRepository cobrancaRepository;

    public ListBillingsUseCase(CobrancaRepository cobrancaRepository) {
        this.cobrancaRepository = cobrancaRepository;
    }

    @Transactional(readOnly = true)
    public Page<CobrancaResponse> execute(Long contratoId, String statusStr, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        CobrancaStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = CobrancaStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Page<Cobranca> page = cobrancaRepository.findWithFilters(contratoId, status, dataInicio, dataFim, pageable);
        return page.map(this::mapToResponse);
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

