package br.com.jmmotos.modules.billing.model;

import br.com.jmmotos.modules.rental.model.ContratoLocacao;
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
@Table(name = "caucao_movimentos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaucaoMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoLocacao contrato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 30)
    private TipoMovimentoCaucao tipoMovimento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_movimento", nullable = false)
    private Instant dataMovimento;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "comprovante_url", length = 255)
    private String comprovanteUrl;

    @Column(name = "registrado_por", nullable = false, length = 150)
    private String registradoPor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
        if (this.dataMovimento == null) {
            this.dataMovimento = Instant.now();
        }
    }
}

