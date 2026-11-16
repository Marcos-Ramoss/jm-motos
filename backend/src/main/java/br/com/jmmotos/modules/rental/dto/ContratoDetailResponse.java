package br.com.jmmotos.modules.rental.dto;

import br.com.jmmotos.modules.billing.dto.CaucaoMovimentoResponse;
import br.com.jmmotos.modules.billing.dto.CobrancaResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratoDetailResponse {
    private Long id;
    private String numeroContrato;
    private Long clienteId;
    private String clienteNome;
    private String clienteCpf;
    private String clienteTelefone;
    private Long motocicletaId;
    private String motocicletaPlaca;
    private String motocicletaModelo;
    private String motocicletaStatus;
    private Long planoVersaoId;
    private String status;
    private LocalDate dataInicio;
    private LocalDate dataTerminoPrevista;
    private LocalDate dataEncerramentoReal;
    private String snapshotModalidade;
    private BigDecimal snapshotValorParcela;
    private BigDecimal snapshotValorAluguel;
    private BigDecimal snapshotTaxaAquisicao;
    private BigDecimal snapshotValorCaucao;
    private Integer snapshotTotalUnidades;
    private Integer snapshotDiaVencimento;
    private Integer unidadesTotais;
    private Integer unidadesQuitadas;
    private BigDecimal percentualAquisicao;
    private BigDecimal saldoCaucaoAtual;
    private String observacoes;
    private Instant criadoEm;
    private List<CobrancaResponse> cobrancas;
    private List<CaucaoMovimentoResponse> movimentosCaucao;
}

