package br.com.jmmotos.modules.dashboard.usecase;

import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import br.com.jmmotos.modules.billing.repository.CaucaoMovimentoRepository;
import br.com.jmmotos.modules.billing.repository.CobrancaRepository;
import br.com.jmmotos.modules.billing.repository.PagamentoRepository;
import br.com.jmmotos.modules.dashboard.dto.DashboardSummaryResponse;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import br.com.jmmotos.modules.fleet.repository.MotocicletaRepository;
import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.model.ContratoStatus;
import br.com.jmmotos.modules.rental.repository.ContratoLocacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GetDashboardSummaryUseCase {

    private final MotocicletaRepository motocicletaRepository;
    private final ContratoLocacaoRepository contratoLocacaoRepository;
    private final CobrancaRepository cobrancaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final CaucaoMovimentoRepository caucaoMovimentoRepository;

    public GetDashboardSummaryUseCase(
            MotocicletaRepository motocicletaRepository,
            ContratoLocacaoRepository contratoLocacaoRepository,
            CobrancaRepository cobrancaRepository,
            PagamentoRepository pagamentoRepository,
            CaucaoMovimentoRepository caucaoMovimentoRepository) {
        this.motocicletaRepository = motocicletaRepository;
        this.contratoLocacaoRepository = contratoLocacaoRepository;
        this.cobrancaRepository = cobrancaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.caucaoMovimentoRepository = caucaoMovimentoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse execute() {
        long totalMotos = motocicletaRepository.count();
        long motosDisponiveis = motocicletaRepository.countByStatusOperacional(StatusOperacional.DISPONIVEL);
        long motosAlugadas = motocicletaRepository.countByStatusOperacional(StatusOperacional.ALUGADA);
        long motosEmManutencao = motocicletaRepository.countByStatusOperacional(StatusOperacional.EM_MANUTENCAO);
        long motosTransferidas = motocicletaRepository.countByStatusOperacional(StatusOperacional.TRANSFERIDA);

        long contratosAtivos = contratoLocacaoRepository.countByStatus(ContratoStatus.ATIVO);
        long cobrancasAtrasadasCount = cobrancaRepository.countByStatus(CobrancaStatus.ATRASADO);

        BigDecimal totalReceitaRealizada = pagamentoRepository.sumTotalReceitaAluguelRealizada();
        BigDecimal totalAReceber = cobrancaRepository.sumSaldoAReceber();
        BigDecimal totalEmAtraso = cobrancaRepository.sumSaldoEmAtraso();
        BigDecimal totalCaucaoCustodia = caucaoMovimentoRepository.getSaldoTotalCaucaoCustodia();

        // Média de progresso Rent-to-Own nos contratos ativos
        List<ContratoLocacao> ativos = contratoLocacaoRepository.findAll().stream()
                .filter(c -> c.getStatus() == ContratoStatus.ATIVO)
                .toList();

        BigDecimal progressoMedio = BigDecimal.ZERO;
        if (!ativos.isEmpty()) {
            BigDecimal soma = ativos.stream()
                    .map(ContratoLocacao::getPercentualAquisicao)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            progressoMedio = soma.divide(BigDecimal.valueOf(ativos.size()), 2, RoundingMode.HALF_UP);
        }

        return DashboardSummaryResponse.builder()
                .totalMotos(totalMotos)
                .motosDisponiveis(motosDisponiveis)
                .motosAlugadas(motosAlugadas)
                .motosEmManutencao(motosEmManutencao)
                .motosTransferidas(motosTransferidas)
                .contratosAtivos(contratosAtivos)
                .cobrancasAtrasadasCount(cobrancasAtrasadasCount)
                .totalReceitaRealizada(totalReceitaRealizada != null ? totalReceitaRealizada : BigDecimal.ZERO)
                .totalAReceber(totalAReceber != null ? totalAReceber : BigDecimal.ZERO)
                .totalEmAtraso(totalEmAtraso != null ? totalEmAtraso : BigDecimal.ZERO)
                .totalCaucaoCustodia(totalCaucaoCustodia != null ? totalCaucaoCustodia : BigDecimal.ZERO)
                .progressoMedioRentToOwn(progressoMedio)
                .build();
    }
}

