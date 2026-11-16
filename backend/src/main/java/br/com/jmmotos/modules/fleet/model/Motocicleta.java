package br.com.jmmotos.modules.fleet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "motocicletas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Motocicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false, unique = true, length = 30)
    private String chassi;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false)
    private Integer ano;

    @Column(length = 30)
    private String renavam;

    @Column(nullable = false)
    private Integer odometro;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;

    @Column(name = "custo_aquisicao", precision = 12, scale = 2)
    private BigDecimal custoAquisicao;

    @Column(name = "municipio_operacao", length = 100)
    private String municipioOperacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_operacional", nullable = false, length = 30)
    private StatusOperacional statusOperacional;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_rastreador", nullable = false, length = 30)
    private StatusRastreador statusRastreador;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
        if (this.odometro == null) {
            this.odometro = 0;
        }
        if (this.statusOperacional == null) {
            this.statusOperacional = StatusOperacional.DISPONIVEL;
        }
        if (this.statusRastreador == null) {
            this.statusRastreador = StatusRastreador.NAO_VINCULADO;
        }
        if (this.municipioOperacao == null) {
            this.municipioOperacao = "Manaus";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = Instant.now();
    }
}

