package br.com.jmmotos.modules.fleet.repository;

import br.com.jmmotos.modules.fleet.model.Motocicleta;
import br.com.jmmotos.modules.fleet.model.StatusOperacional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotocicletaRepository extends JpaRepository<Motocicleta, Long> {

    boolean existsByPlaca(String placa);

    boolean existsByChassi(String chassi);

    Optional<Motocicleta> findByPlaca(String placa);

    List<Motocicleta> findByStatusOperacional(StatusOperacional statusOperacional);

    @Query("SELECT m FROM Motocicleta m WHERE " +
           "(:busca IS NULL OR LOWER(m.placa) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(m.modelo) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(m.marca) LIKE LOWER(CONCAT('%', :busca, '%'))) AND " +
           "(:status IS NULL OR m.statusOperacional = :status)")
    Page<Motocicleta> findWithFilters(@Param("busca") String busca, @Param("status") StatusOperacional status, Pageable pageable);

    long countByStatusOperacional(StatusOperacional statusOperacional);
}

