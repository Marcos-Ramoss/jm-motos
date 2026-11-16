package br.com.jmmotos.modules.customer.model;

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

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String rg;

    @Column(name = "cnh_numero", nullable = false, length = 20)
    private String cnhNumero;

    @Column(name = "cnh_categoria", nullable = false, length = 5)
    private String cnhCategoria;

    @Column(name = "cnh_validade", nullable = false)
    private LocalDate cnhValidade;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(length = 255)
    private String endereco;

    @Column(length = 100)
    private String municipio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClienteStatus status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
        if (this.status == null) {
            this.status = ClienteStatus.ATIVO;
        }
        if (this.cnhCategoria == null) {
            this.cnhCategoria = "A";
        }
        if (this.municipio == null) {
            this.municipio = "Manaus";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = Instant.now();
    }
}

