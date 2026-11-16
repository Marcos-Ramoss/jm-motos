package br.com.jmmotos.modules.billing.repository;

import br.com.jmmotos.modules.billing.model.CaucaoMovimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CaucaoMovimentoRepository extends JpaRepository<CaucaoMovimento, Long> {

    List<CaucaoMovimento> findByContratoIdOrderByDataMovimentoDesc(Long contratoId);

    @Query("SELECT COALESCE(SUM(CASE WHEN c.tipoMovimento = 'ENTRADA' THEN c.valor ELSE -c.valor END), 0) " +
           "FROM CaucaoMovimento c WHERE c.contrato.id = :contratoId")
    BigDecimal getSaldoCaucaoContrato(@Param("contratoId") Long contratoId);

    @Query("SELECT COALESCE(SUM(CASE WHEN c.tipoMovimento = 'ENTRADA' THEN c.valor ELSE -c.valor END), 0) " +
           "FROM CaucaoMovimento c")
    BigDecimal getSaldoTotalCaucaoCustodia();
}

