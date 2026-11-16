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

    Page<Motocicleta> findByStatusOperacional(StatusOperacional statusOperacional, Pageable pageable);

    @Query("SELECT m FROM Motocicleta m WHERE " +
           "(LOWER(m.placa) LIKE :busca OR LOWER(m.modelo) LIKE :busca OR LOWER(m.marca) LIKE :busca)")
    Page<Motocicleta> findByBusca(@Param("busca") String busca, Pageable pageable);

    @Query("SELECT m FROM Motocicleta m WHERE " +
           "(LOWER(m.placa) LIKE :busca OR LOWER(m.modelo) LIKE :busca OR LOWER(m.marca) LIKE :busca) AND " +
           "m.statusOperacional = :status")
    Page<Motocicleta> findByBuscaAndStatus(@Param("busca") String busca, @Param("status") StatusOperacional status, Pageable pageable);

    default Page<Motocicleta> findWithFilters(String busca, StatusOperacional status, Pageable pageable) {
        if (busca != null && !busca.isBlank() && status != null) {
            return findByBuscaAndStatus("%" + busca.trim().toLowerCase() + "%", status, pageable);
        } else if (busca != null && !busca.isBlank()) {
            return findByBusca("%" + busca.trim().toLowerCase() + "%", pageable);
        } else if (status != null) {
            return findByStatusOperacional(status, pageable);
        } else {
            return findAll(pageable);
        }
    }

    long countByStatusOperacional(StatusOperacional statusOperacional);
}
