package br.com.jmmotos.modules.customer.repository;

import br.com.jmmotos.modules.customer.model.Cliente;
import br.com.jmmotos.modules.customer.model.ClienteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);

    Optional<Cliente> findByCpf(String cpf);

    Page<Cliente> findByStatus(ClienteStatus status, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE " +
           "(LOWER(c.nome) LIKE :busca OR c.cpf LIKE :busca OR c.telefone LIKE :busca)")
    Page<Cliente> findByBusca(@Param("busca") String busca, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE " +
           "(LOWER(c.nome) LIKE :busca OR c.cpf LIKE :busca OR c.telefone LIKE :busca) AND " +
           "c.status = :status")
    Page<Cliente> findByBuscaAndStatus(@Param("busca") String busca, @Param("status") ClienteStatus status, Pageable pageable);

    default Page<Cliente> findWithFilters(String busca, ClienteStatus status, Pageable pageable) {
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

    long countByStatus(ClienteStatus status);
}
