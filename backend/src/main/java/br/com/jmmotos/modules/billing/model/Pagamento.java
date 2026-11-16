package br.com.jmmotos.modules.billing.model;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cobranca_id", nullable = false)
    private Cobranca cobranca;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_pagamento", nullable = false)
    private Instant dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 30)
    private FormaPagamento formaPagamento;

    @Column(name = "referencia_externa", length = 100)
    private String referenciaExterna;

    @Column(name = "comprovante_url", length = 255)
    private String comprovanteUrl;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "confirmado_por", nullable = false, length = 150)
    private String confirmadoPor;

    @Column(nullable = false)
    private Boolean estornado;

    @Column(name = "estornado_em")
    private Instant estornadoEm;

    @Column(name = "estornado_por", length = 150)
    private String estornadoPor;

    @Column(name = "motivo_estorno", columnDefinition = "TEXT")
    private String motivoEstorno;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
        if (this.dataPagamento == null) {
            this.dataPagamento = Instant.now();
        }
        if (this.estornado == null) {
            this.estornado = false;
        }
    }
}

