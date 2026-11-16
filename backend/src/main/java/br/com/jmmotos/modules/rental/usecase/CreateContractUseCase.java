package br.com.jmmotos.modules.rental.usecase;

import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.exception.ConflictException;
import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import br.com.jmmotos.modules.billing.model.TipoCobranca;
import br.com.jmmotos.modules.billing.repository.CobrancaRepository;
import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.customer.model.ClienteStatus;
import br.com.jmmotos.modules.customer.repository.ClienteRepository;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import br.com.jmmotos.modules.plan.model.ModalidadePlano;
import br.com.jmmotos.modules.plan.model.PlanoVersao;
import br.com.jmmotos.modules.plan.repository.PlanoVersaoRepository;
import br.com.jmmotos.modules.rental.dto.ContratoResponse;
import br.com.jmmotos.modules.rental.dto.CreateContractRequest;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.model.ContratoStatus;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CreateContractUseCase {

    private final ContratoLocacaoRepository contratoLocacaoRepository;
    private final ClienteRepository clienteRepository;
    private final MotocicletaRepository motocicletaRepository;
    private final PlanoVersaoRepository planoVersaoRepository;
    private final CobrancaRepository cobrancaRepository;

    public CreateContractUseCase(
            ContratoLocacaoRepository contratoLocacaoRepository,
            ClienteRepository clienteRepository,
            MotocicletaRepository motocicletaRepository,
            PlanoVersaoRepository planoVersaoRepository,
            CobrancaRepository cobrancaRepository) {
        this.contratoLocacaoRepository = contratoLocacaoRepository;
        this.clienteRepository = clienteRepository;
        this.motocicletaRepository = motocicletaRepository;
        this.planoVersaoRepository = planoVersaoRepository;
        this.cobrancaRepository = cobrancaRepository;
    }

    @Transactional
    public ContratoResponse execute(CreateContractRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + request.getClienteId()));

        if (cliente.getStatus() != ClienteStatus.ATIVO) {
            throw new BusinessRuleException("O cliente selecionado não está ativo para novas locações.");
        }

        Motocicleta moto = motocicletaRepository.findById(request.getMotocicletaId())
                .orElseThrow(() -> new ResourceNotFoundException("Motocicleta não encontrada com ID: " + request.getMotocicletaId()));

        boolean temContratoAtivo = contratoLocacaoRepository.existsByMotocicletaIdAndStatusIn(
                moto.getId(),
                Arrays.asList(ContratoStatus.ATIVO, ContratoStatus.SUSPENSO, ContratoStatus.CONCLUIDO_AGUARDANDO_TRANSFERENCIA)
        );

        if (temContratoAtivo || moto.getStatusOperacional() == StatusOperacional.ALUGADA) {
            throw new ConflictException("A motocicleta " + moto.getPlaca() + " já possui uma locação ativa ou se encontra indisponível.");
        }

        PlanoVersao planoVersao = planoVersaoRepository.findById(request.getPlanoVersaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Versão do plano não encontrada com ID: " + request.getPlanoVersaoId()));

        if (!planoVersao.getAtivo()) {
            throw new BusinessRuleException("A versão de plano selecionada está inativa.");
        }

        String dataStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sufixo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String numeroContrato = "CTR-" + dataStr + "-" + sufixo;

        LocalDate dataTerminoPrevista;
        if (planoVersao.getModalidade() == ModalidadePlano.SEMANAL) {
            dataTerminoPrevista = request.getDataInicio().plusWeeks(planoVersao.getTotalUnidades());
        } else {
            dataTerminoPrevista = request.getDataInicio().plusDays(planoVersao.getTotalUnidades());
        }

        Integer diaVencimento = request.getDiaVencimentoPersonalizado() != null
                ? request.getDiaVencimentoPersonalizado()
                : planoVersao.getDiaVencimentoPadrao();

        ContratoLocacao contrato = ContratoLocacao.builder()
                .numeroContrato(numeroContrato)
                .cliente(cliente)
                .motocicleta(moto)
                .planoVersao(planoVersao)
                .status(ContratoStatus.ATIVO)
                .dataInicio(request.getDataInicio())
                .dataTerminoPrevista(dataTerminoPrevista)
                .snapshotModalidade(planoVersao.getModalidade().name())
                .snapshotValorParcela(planoVersao.getValorParcela())
                .snapshotValorAluguel(planoVersao.getValorAluguel())
                .snapshotTaxaAquisicao(planoVersao.getTaxaAquisicao())
                .snapshotValorCaucao(planoVersao.getValorCaucao())
                .snapshotTotalUnidades(planoVersao.getTotalUnidades())
                .snapshotDiaVencimento(diaVencimento)
                .unidadesTotais(planoVersao.getTotalUnidades())
                .unidadesQuitadas(0)
                .percentualAquisicao(BigDecimal.ZERO)
                .observacoes(request.getObservacoes())
                .build();

        ContratoLocacao salvo = contratoLocacaoRepository.save(contrato);

        // Atualiza status da motocicleta para ALUGADA
        moto.setStatusOperacional(StatusOperacional.ALUGADA);
        motocicletaRepository.save(moto);

        // Geração do lote inicial de cobranças para operação imediata
        gerarCobrancasIniciais(salvo, planoVersao, request.getDataInicio());

        return mapToResponse(salvo);
    }

    private void gerarCobrancasIniciais(ContratoLocacao contrato, PlanoVersao planoVersao, LocalDate dataInicio) {
        List<Cobranca> cobrancas = new ArrayList<>();
        boolean isSemanal = planoVersao.getModalidade() == ModalidadePlano.SEMANAL;
        int loteInicial = isSemanal ? Math.min(12, planoVersao.getTotalUnidades()) : Math.min(30, planoVersao.getTotalUnidades());
        TipoCobranca tipo = isSemanal ? TipoCobranca.SEMANAL : TipoCobranca.DIARIA;

        for (int i = 1; i <= loteInicial; i++) {
            LocalDate vencimento = isSemanal
                    ? dataInicio.plusWeeks(i - 1)
                    : dataInicio.plusDays(i - 1);

            Cobranca cobranca = Cobranca.builder()
                    .contrato(contrato)
                    .numeroParcela(i)
                    .tipoCobranca(tipo)
                    .dataVencimento(vencimento)
                    .valorTotal(planoVersao.getValorParcela())
                    .valorAluguel(planoVersao.getValorAluguel())
                    .taxaAquisicao(planoVersao.getTaxaAquisicao())
                    .valorPago(BigDecimal.ZERO)
                    .saldoRestante(planoVersao.getValorParcela())
                    .status(CobrancaStatus.PENDENTE)
                    .build();

            cobrancas.add(cobranca);
        }

        cobrancaRepository.saveAll(cobrancas);
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

