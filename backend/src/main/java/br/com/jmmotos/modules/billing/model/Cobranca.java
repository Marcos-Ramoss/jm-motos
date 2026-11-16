package br.com.jmmotos.modules.billing.model;

import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cobrancas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cobranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoLocacao contrato;

    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cobranca", nullable = false, length = 30)
    private TipoCobranca tipoCobranca;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_aluguel", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorAluguel;

    @Column(name = "taxa_aquisicao", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxaAquisicao;

    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "saldo_restante", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoRestante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CobrancaStatus status;

    @Column(name = "data_pagamento_integral")
    private Instant dataPagamentoIntegral;

    @Column(name = "motivo_isencao", columnDefinition = "TEXT")
    private String motivoIsencao;

    @Column(name = "isento_em")
    private Instant isentoEm;

    @Column(name = "isento_por", length = 150)
    private String isentoPor;

    @OneToMany(mappedBy = "cobranca", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pagamento> pagamentos = new ArrayList<>();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
        if (this.valorPago == null) {
            this.valorPago = BigDecimal.ZERO;
        }
        if (this.saldoRestante == null) {
            this.saldoRestante = this.valorTotal;
        }
        if (this.taxaAquisicao == null) {
            this.taxaAquisicao = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = CobrancaStatus.PENDENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = Instant.now();
    }

    public void recalcularSaldo() {
        if (this.status == CobrancaStatus.ISENTO) {
            return;
        }

        BigDecimal totalPagoValido = this.pagamentos.stream()
                .filter(p -> !Boolean.TRUE.equals(p.getEstornado()))
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.valorPago = totalPagoValido;
        this.saldoRestante = this.valorTotal.subtract(totalPagoValido);

        if (this.saldoRestante.compareTo(BigDecimal.ZERO) <= 0) {
            this.saldoRestante = BigDecimal.ZERO;
            this.status = CobrancaStatus.QUITADO;
            if (this.dataPagamentoIntegral == null) {
                this.dataPagamentoIntegral = Instant.now();
            }
        } else if (this.valorPago.compareTo(BigDecimal.ZERO) > 0) {
            this.status = CobrancaStatus.PAGO_PARCIAL;
            this.dataPagamentoIntegral = null;
        } else {
            this.status = LocalDate.now().isAfter(this.dataVencimento) ? CobrancaStatus.ATRASADO : CobrancaStatus.PENDENTE;
            this.dataPagamentoIntegral = null;
        }
    }
}

