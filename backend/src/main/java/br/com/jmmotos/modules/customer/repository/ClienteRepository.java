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

    @Query("SELECT c FROM Cliente c WHERE " +
           "(:busca IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR c.cpf LIKE CONCAT('%', :busca, '%') OR c.telefone LIKE CONCAT('%', :busca, '%')) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Cliente> findWithFilters(@Param("busca") String busca, @Param("status") ClienteStatus status, Pageable pageable);

    long countByStatus(ClienteStatus status);
}

