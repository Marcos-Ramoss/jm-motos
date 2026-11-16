package br.com.jmmotos.modules.billing.repository;

import br.com.jmmotos.modules.billing.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByIdempotencyKey(String idempotencyKey);

    List<Pagamento> findByCobrancaIdOrderByDataPagamentoDesc(Long cobrancaId);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pagamento p WHERE p.estornado = false AND p.cobranca.tipoCobranca IN ('DIARIA', 'SEMANAL')")
    BigDecimal sumTotalReceitaAluguelRealizada();
}

