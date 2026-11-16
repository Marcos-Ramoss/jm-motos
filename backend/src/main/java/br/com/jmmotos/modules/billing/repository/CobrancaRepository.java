package br.com.jmmotos.modules.billing.repository;

import br.com.jmmotos.modules.billing.model.Cobranca;
import br.com.jmmotos.modules.billing.model.CobrancaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {

    List<Cobranca> findByContratoIdOrderByNumeroParcelaAsc(Long contratoId);

    @Query("SELECT c FROM Cobranca c WHERE " +
           "(:contratoId IS NULL OR c.contrato.id = :contratoId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:dataInicio IS NULL OR c.dataVencimento >= :dataInicio) AND " +
           "(:dataFim IS NULL OR c.dataVencimento <= :dataFim) " +
           "ORDER BY c.dataVencimento ASC")
    Page<Cobranca> findWithFilters(
            @Param("contratoId") Long contratoId,
            @Param("status") CobrancaStatus status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable);

    long countByContratoIdAndStatus(Long contratoId, CobrancaStatus status);

    long countByStatus(CobrancaStatus status);

    @Query("SELECT COALESCE(SUM(c.saldoRestante), 0) FROM Cobranca c WHERE c.status IN ('PENDENTE', 'PAGO_PARCIAL', 'ATRASADO')")
    BigDecimal sumSaldoAReceber();

    @Query("SELECT COALESCE(SUM(c.saldoRestante), 0) FROM Cobranca c WHERE c.status = 'ATRASADO'")
    BigDecimal sumSaldoEmAtraso();
}

