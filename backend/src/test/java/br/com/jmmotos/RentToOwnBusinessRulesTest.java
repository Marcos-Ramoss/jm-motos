package br.com.jmmotos;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ConflictException;
import br.com.jmmotos.modules.billing.dto.ExemptBillingRequest;
import br.com.jmmotos.modules.billing.dto.RegisterPaymentRequest;
import br.com.jmmotos.modules.billing.dto.ReversePaymentRequest;
import br.com.jmmotos.modules.billing.model.CaucaoMovimento;
import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import br.com.jmmotos.modules.billing.model.FormaPagamento;
import br.com.jmmotos.modules.billing.model.Pagamento;
import br.com.jmmotos.modules.billing.model.TipoCobranca;
import br.com.jmmotos.modules.billing.model.TipoMovimentoCaucao;
import br.com.jmmotos.modules.billing.repository.CaucaoMovimentoRepository;
import br.com.jmmotos.modules.billing.repository.CobrancaRepository;
import br.com.jmmotos.modules.billing.repository.PagamentoRepository;
import br.com.jmmotos.modules.billing.usecase.ExemptBillingUseCase;
import br.com.jmmotos.modules.billing.usecase.RegisterPaymentUseCase;
import br.com.jmmotos.modules.billing.usecase.ReversePaymentUseCase;
import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.customer.model.ClienteStatus;
import br.com.jmmotos.modules.customer.repository.ClienteRepository;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import br.com.jmmotos.modules.fleet.model.StatusRastreador;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import br.com.jmmotos.modules.plan.model.ModalidadePlano;
import br.com.jmmotos.modules.plan.model.PlanoLocacao;
import br.com.jmmotos.modules.plan.model.PlanoVersao;
import br.com.jmmotos.modules.plan.repository.PlanoVersaoRepository;
import br.com.jmmotos.modules.rental.dto.CreateContractRequest;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.model.ContratoStatus;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import br.com.jmmotos.modules.rental.usecase.CreateContractUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentToOwnBusinessRulesTest {

    @Mock
    private ContratoLocacaoRepository contratoLocacaoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MotocicletaRepository motocicletaRepository;

    @Mock
    private PlanoVersaoRepository planoVersaoRepository;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private CaucaoMovimentoRepository caucaoMovimentoRepository;

    @InjectMocks
    private CreateContractUseCase createContractUseCase;

    @InjectMocks
    private RegisterPaymentUseCase registerPaymentUseCase;

    @InjectMocks
    private ExemptBillingUseCase exemptBillingUseCase;

    @InjectMocks
    private ReversePaymentUseCase reversePaymentUseCase;

    private Cliente cliente;
    private Motocicleta moto;
    private PlanoVersao planoVersao;
    private ContratoLocacao contrato;
    private Cobranca cobranca;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("João da Silva")
                .cpf("12345678901")
                .cnhNumero("987654321")
                .cnhCategoria("A")
                .cnhValidade(LocalDate.now().plusYears(2))
                .telefone("(92) 99123-4567")
                .status(ClienteStatus.ATIVO)
                .build();

        moto = Motocicleta.builder()
                .id(1L)
                .placa("PHA1234")
                .chassi("9C2KC123456789012")
                .marca("Honda")
                .modelo("CG 160 Fan")
                .ano(2024)
                .odometro(5000)
                .statusOperacional(StatusOperacional.DISPONIVEL)
                .statusRastreador(StatusRastreador.NAO_VINCULADO)
                .build();

        PlanoLocacao plano = PlanoLocacao.builder()
                .id(1L)
                .nome("Plano Diário Uarini")
                .codigo("PLANO_DIARIO_UARINI")
                .ativo(true)
                .build();

        planoVersao = PlanoVersao.builder()
                .id(1L)
                .plano(plano)
                .versao(1)
                .modalidade(ModalidadePlano.DIARIO)
                .valorParcela(new BigDecimal("50.00"))
                .valorAluguel(new BigDecimal("45.00"))
                .taxaAquisicao(new BigDecimal("5.00"))
                .valorCaucao(new BigDecimal("400.00"))
                .totalUnidades(1460)
                .ativo(true)
                .build();

        contrato = ContratoLocacao.builder()
                .id(10L)
                .numeroContrato("CTR-20261115-ABC123")
                .cliente(cliente)
                .motocicleta(moto)
                .planoVersao(planoVersao)
                .status(ContratoStatus.ATIVO)
                .dataInicio(LocalDate.now())
                .unidadesTotais(1460)
                .unidadesQuitadas(0)
                .percentualAquisicao(BigDecimal.ZERO)
                .build();

        cobranca = Cobranca.builder()
                .id(100L)
                .contrato(contrato)
                .numeroParcela(1)
                .tipoCobranca(TipoCobranca.DIARIA)
                .dataVencimento(LocalDate.now())
                .valorTotal(new BigDecimal("50.00"))
                .valorAluguel(new BigDecimal("45.00"))
                .taxaAquisicao(new BigDecimal("5.00"))
                .valorPago(BigDecimal.ZERO)
                .saldoRestante(new BigDecimal("50.00"))
                .status(CobrancaStatus.PENDENTE)
                .pagamentos(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Regra 1: Deve impedir contratação simultânea da mesma motocicleta")
    void deveImpedirContratacaoSimultaneaDeMotoJaAlugada() {
        CreateContractRequest request = CreateContractRequest.builder()
                .clienteId(1L)
                .motocicletaId(1L)
                .planoVersaoId(1L)
                .dataInicio(LocalDate.now())
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(motocicletaRepository.findById(1L)).thenReturn(Optional.of(moto));
        when(contratoLocacaoRepository.existsByMotocicletaIdAndStatusIn(any(), any())).thenReturn(true);

        assertThrows(ConflictException.class, () -> createContractUseCase.execute(request));
    }

    @Test
    @DisplayName("Regra 2: Pagamento parcial marca como PAGO_PARCIAL e não avança unidade quitada de aquisição")
    void deveCalcularPagamentoParcialSemAvancarAquisicao() {
        RegisterPaymentRequest request = RegisterPaymentRequest.builder()
                .cobrancaId(100L)
                .valor(new BigDecimal("30.00"))
                .formaPagamento("PIX")
                .idempotencyKey("IDEM-001")
                .build();

        when(cobrancaRepository.findById(100L)).thenReturn(Optional.of(cobranca));
        when(pagamentoRepository.findByIdempotencyKey("IDEM-001")).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        registerPaymentUseCase.execute(request, "ADMIN");

        assertEquals(CobrancaStatus.PAGO_PARCIAL, cobranca.getStatus());
        assertEquals(new BigDecimal("30.00"), cobranca.getValorPago());
        assertEquals(new BigDecimal("20.00"), cobranca.getSaldoRestante());
    }

    @Test
    @DisplayName("Regra 3: Pagamento integral atualiza status para QUITADO e avança aquisição Rent-to-Own")
    void deveAvancarAquisicaoAposQuitacaoIntegral() {
        RegisterPaymentRequest request = RegisterPaymentRequest.builder()
                .cobrancaId(100L)
                .valor(new BigDecimal("50.00"))
                .formaPagamento("PIX")
                .idempotencyKey("IDEM-002")
                .build();

        when(cobrancaRepository.findById(100L)).thenReturn(Optional.of(cobranca));
        when(pagamentoRepository.findByIdempotencyKey("IDEM-002")).thenReturn(Optional.empty());
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cobrancaRepository.countByContratoIdAndStatus(10L, CobrancaStatus.QUITADO)).thenReturn(1L);

        registerPaymentUseCase.execute(request, "ADMIN");

        assertEquals(CobrancaStatus.QUITADO, cobranca.getStatus());
        assertEquals(new BigDecimal("50.00"), cobranca.getValorPago());
        assertEquals(BigDecimal.ZERO, cobranca.getSaldoRestante());
        assertEquals(1, contrato.getUnidadesQuitadas());
        // 1 / 1460 * 100 = 0.07%
        assertEquals(new BigDecimal("0.07"), contrato.getPercentualAquisicao());
    }

    @Test
    @DisplayName("Regra 4: Isenção de oficina suspende cobrança e NÃO conta como diária quitada")
    void deveIsentarCobrancaSemIncrementarProgressoAquisicao() {
        ExemptBillingRequest request = ExemptBillingRequest.builder()
                .motivoIsencao("Manutenção preventiva em oficina credenciada")
                .build();

        when(cobrancaRepository.findById(100L)).thenReturn(Optional.of(cobranca));
        when(cobrancaRepository.save(any(Cobranca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cobrancaRepository.countByContratoIdAndStatus(10L, CobrancaStatus.QUITADO)).thenReturn(0L);

        exemptBillingUseCase.execute(100L, request, "ADMIN");

        assertEquals(CobrancaStatus.ISENTO, cobranca.getStatus());
        assertEquals("Manutenção preventiva em oficina credenciada", cobranca.getMotivoIsencao());
        assertEquals(0, contrato.getUnidadesQuitadas());
        assertEquals(new BigDecimal("0.00"), contrato.getPercentualAquisicao());
    }

    @Test
    @DisplayName("Regra 5: Estorno auditado de pagamento recalcula saldo da cobrança e deduz aquisição")
    void deveEstornarPagamentoAuditadoERecalcularSaldoEProgresso() {
        Pagamento pagamento = Pagamento.builder()
                .id(50L)
                .cobranca(cobranca)
                .valor(new BigDecimal("50.00"))
                .dataPagamento(Instant.now())
                .formaPagamento(FormaPagamento.PIX)
                .confirmadoPor("ADMIN")
                .estornado(false)
                .build();

        cobranca.getPagamentos().add(pagamento);
        cobranca.recalcularSaldo();
        assertEquals(CobrancaStatus.QUITADO, cobranca.getStatus());

        when(pagamentoRepository.findById(50L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cobrancaRepository.countByContratoIdAndStatus(10L, CobrancaStatus.QUITADO)).thenReturn(0L);

        ReversePaymentRequest request = ReversePaymentRequest.builder()
                .motivoEstorno("Comprovante duplicado pelo banco")
                .build();

        reversePaymentUseCase.execute(50L, request, "ADMIN");

        assertTrue(pagamento.getEstornado());
        assertEquals("Comprovante duplicado pelo banco", pagamento.getMotivoEstorno());
        assertEquals(new BigDecimal("50.00"), cobranca.getSaldoRestante());
        assertEquals(CobrancaStatus.PENDENTE, cobranca.getStatus());
        assertEquals(0, contrato.getUnidadesQuitadas());
    }

    @Test
    @DisplayName("Regra 6: Pagamento com chave de idempotência duplicada deve ser rejeitado")
    void deveBloquearPagamentoDuplicadoComMesmaChaveIdempotencia() {
        RegisterPaymentRequest request = RegisterPaymentRequest.builder()
                .cobrancaId(100L)
                .valor(new BigDecimal("50.00"))
                .formaPagamento("PIX")
                .idempotencyKey("CHAVE-REPETIDA-123")
                .build();

        when(pagamentoRepository.findByIdempotencyKey("CHAVE-REPETIDA-123")).thenReturn(Optional.of(new Pagamento()));

        assertThrows(ConflictException.class, () -> registerPaymentUseCase.execute(request, "ADMIN"));
    }
}
