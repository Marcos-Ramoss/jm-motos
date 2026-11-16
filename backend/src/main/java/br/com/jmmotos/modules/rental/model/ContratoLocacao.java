package br.com.jmmotos.modules.rental.model;

import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.plan.model.PlanoVersao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "contratos_locacao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratoLocacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_contrato", nullable = false, unique = true, length = 50)
    private String numeroContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motocicleta_id", nullable = false)
    private Motocicleta motocicleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_versao_id", nullable = false)
    private PlanoVersao planoVersao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ContratoStatus status;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_termino_prevista")
    private LocalDate dataTerminoPrevista;

    @Column(name = "data_encerramento_real")
    private LocalDate dataEncerramentoReal;

    // Snapshot Imutável das Condições Acordadas
    @Column(name = "snapshot_modalidade", nullable = false, length = 30)
    private String snapshotModalidade;

    @Column(name = "snapshot_valor_parcela", nullable = false, precision = 10, scale = 2)
    private BigDecimal snapshotValorParcela;

    @Column(name = "snapshot_valor_aluguel", nullable = false, precision = 10, scale = 2)
    private BigDecimal snapshotValorAluguel;

    @Column(name = "snapshot_taxa_aquisicao", nullable = false, precision = 10, scale = 2)
    private BigDecimal snapshotTaxaAquisicao;

    @Column(name = "snapshot_valor_caucao", nullable = false, precision = 10, scale = 2)
    private BigDecimal snapshotValorCaucao;

    @Column(name = "snapshot_total_unidades", nullable = false)
    private Integer snapshotTotalUnidades;

    @Column(name = "snapshot_dia_vencimento")
    private Integer snapshotDiaVencimento;

    // Acompanhamento do Rent-to-Own
    @Column(name = "unidades_totais", nullable = false)
    private Integer unidadesTotais;

    @Column(name = "unidades_quitadas", nullable = false)
    private Integer unidadesQuitadas;

    @Column(name = "percentual_aquisicao", nullable = false, precision = 6, scale = 2)
    private BigDecimal percentualAquisicao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
        if (this.unidadesQuitadas == null) {
            this.unidadesQuitadas = 0;
        }
        if (this.percentualAquisicao == null) {
            this.percentualAquisicao = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = ContratoStatus.ATIVO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = Instant.now();
    }
}

