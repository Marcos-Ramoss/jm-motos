package br.com.jmmotos.modules.rental.usecase;

import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.billing.dto.CaucaoMovimentoResponse;
import br.com.jmmotos.modules.billing.dto.CobrancaResponse;
import br.com.jmmotos.modules.billing.dto.PagamentoResponse;
import br.com.jmmotos.modules.billing.model.CaucaoMovimento;
import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.Pagamento;
import br.com.jmmotos.modules.billing.repository.CaucaoMovimentoRepository;
import br.com.jmmotos.modules.billing.repository.CobrancaRepository;
import br.com.jmmotos.modules.rental.dto.ContratoDetailResponse;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetContractByIdUseCase {

    private final ContratoLocacaoRepository contratoLocacaoRepository;
    private final CobrancaRepository cobrancaRepository;
    private final CaucaoMovimentoRepository caucaoMovimentoRepository;

    public GetContractByIdUseCase(
            ContratoLocacaoRepository contratoLocacaoRepository,
            CobrancaRepository cobrancaRepository,
            CaucaoMovimentoRepository caucaoMovimentoRepository) {
        this.contratoLocacaoRepository = contratoLocacaoRepository;
        this.cobrancaRepository = cobrancaRepository;
        this.caucaoMovimentoRepository = caucaoMovimentoRepository;
    }

    @Transactional(readOnly = true)
    public ContratoDetailResponse execute(Long id) {
        ContratoLocacao c = contratoLocacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado com ID: " + id));

        List<Cobranca> cobrancas = cobrancaRepository.findByContratoIdOrderByNumeroParcelaAsc(c.getId());
        List<CobrancaResponse> cobrancasResponse = cobrancas.stream()
                .map(this::mapCobrancaToResponse)
                .collect(Collectors.toList());

        List<CaucaoMovimento> movimentosCaucao = caucaoMovimentoRepository.findByContratoIdOrderByDataMovimentoDesc(c.getId());
        List<CaucaoMovimentoResponse> caucaoResponse = movimentosCaucao.stream()
                .map(this::mapCaucaoToResponse)
                .collect(Collectors.toList());

        BigDecimal saldoCaucao = caucaoMovimentoRepository.getSaldoCaucaoContrato(c.getId());

        return ContratoDetailResponse.builder()
                .id(c.getId())
                .numeroContrato(c.getNumeroContrato())
                .clienteId(c.getCliente().getId())
                .clienteNome(c.getCliente().getNome())
                .clienteCpf(c.getCliente().getCpf())
                .clienteTelefone(c.getCliente().getTelefone())
                .motocicletaId(c.getMotocicleta().getId())
                .motocicletaPlaca(c.getMotocicleta().getPlaca())
                .motocicletaModelo(c.getMotocicleta().getModelo())
                .motocicletaStatus(c.getMotocicleta().getStatusOperacional().name())
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
                .saldoCaucaoAtual(saldoCaucao)
                .observacoes(c.getObservacoes())
                .criadoEm(c.getCriadoEm())
                .cobrancas(cobrancasResponse)
                .movimentosCaucao(caucaoResponse)
                .build();
    }

    private CobrancaResponse mapCobrancaToResponse(Cobranca cob) {
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

    private CaucaoMovimentoResponse mapCaucaoToResponse(CaucaoMovimento cm) {
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

