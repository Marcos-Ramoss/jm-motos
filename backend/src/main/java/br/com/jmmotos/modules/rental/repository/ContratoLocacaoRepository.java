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

    Page<ContratoLocacao> findByStatus(ContratoStatus status, Pageable pageable);

    @Query("SELECT c FROM ContratoLocacao c WHERE " +
           "(LOWER(c.numeroContrato) LIKE :busca OR LOWER(c.cliente.nome) LIKE :busca OR LOWER(c.motocicleta.placa) LIKE :busca)")
    Page<ContratoLocacao> findByBusca(@Param("busca") String busca, Pageable pageable);

    @Query("SELECT c FROM ContratoLocacao c WHERE " +
           "(LOWER(c.numeroContrato) LIKE :busca OR LOWER(c.cliente.nome) LIKE :busca OR LOWER(c.motocicleta.placa) LIKE :busca) AND " +
           "c.status = :status")
    Page<ContratoLocacao> findByBuscaAndStatus(@Param("busca") String busca, @Param("status") ContratoStatus status, Pageable pageable);

    default Page<ContratoLocacao> findWithFilters(String busca, ContratoStatus status, Pageable pageable) {
        if (busca != null && !busca.isBlank() && status != null) {
            return findByBuscaAndStatus("%" + busca.trim().toLowerCase() + "%", status, pageable);
        } else if (busca != null && !busca.isBlank()) {
            return findByBusca("%" + busca.trim().toLowerCase() + "%", pageable);
        } else if (status != null) {
            return findByStatus(status, pageable);
        } else {
            return findAll(pageable);
        }
    }

    long countByStatus(ContratoStatus status);
}
