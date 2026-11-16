package br.com.jmmotos.modules.rental.repository;

import br.com.jmmotos.modules.rental.model.ContratoLocacao;
import br.com.jmmotos.modules.rental.model.ContratoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoLocacaoRepository extends JpaRepository<ContratoLocacao, Long> {

    boolean existsByMotocicletaIdAndStatusIn(Long motocicletaId, Collection<ContratoStatus> statuses);

    Optional<ContratoLocacao> findByNumeroContrato(String numeroContrato);

    List<ContratoLocacao> findByClienteId(Long clienteId);

    List<ContratoLocacao> findByMotocicletaId(Long motocicletaId);

    @Query("SELECT c FROM ContratoLocacao c WHERE " +
           "(:busca IS NULL OR LOWER(c.numeroContrato) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(c.cliente.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(c.motocicleta.placa) LIKE LOWER(CONCAT('%', :busca, '%'))) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<ContratoLocacao> findWithFilters(@Param("busca") String busca, @Param("status") ContratoStatus status, Pageable pageable);

    long countByStatus(ContratoStatus status);
}

