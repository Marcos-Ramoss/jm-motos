package br.com.jmmotos.modules.plan.model;

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
@Table(name = "planos_versoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanoVersao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoLocacao plano;

    @Column(nullable = false)
    private Integer versao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModalidadePlano modalidade;

    @Column(name = "valor_parcela", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorParcela;

    @Column(name = "valor_aluguel", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorAluguel;

    @Column(name = "taxa_aquisicao", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxaAquisicao;

    @Column(name = "valor_caucao", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCaucao;

    @Column(name = "total_unidades", nullable = false)
    private Integer totalUnidades;

    @Column(name = "dia_vencimento_padrao")
    private Integer diaVencimentoPadrao;

    @Column(name = "horario_limite_vencimento", length = 10)
    private String horarioLimiteVencimento;

    @Column(name = "km_troca_oleo")
    private Integer kmTrocaOleo;

    @Column(name = "trocas_para_bonus_oleo")
    private Integer trocasParaBonusOleo;

    @Column(name = "tolerancia_dias_inadimplencia")
    private Integer toleranciaDiasInadimplencia;

    @Column(name = "vigente_desde")
    private Instant vigenteDesde;

    @Column(nullable = false)
    private Boolean ativo;

    @PrePersist
    protected void onCreate() {
        if (this.vigenteDesde == null) {
            this.vigenteDesde = Instant.now();
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}

