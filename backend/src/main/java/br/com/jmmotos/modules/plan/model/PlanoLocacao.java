package br.com.jmmotos.modules.plan.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planos_locacao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanoLocacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @OneToMany(mappedBy = "plano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PlanoVersao> versoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}

