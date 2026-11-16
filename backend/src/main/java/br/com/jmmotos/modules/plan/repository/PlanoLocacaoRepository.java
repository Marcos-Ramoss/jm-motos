package br.com.jmmotos.modules.plan.repository;

import br.com.jmmotos.modules.plan.model.PlanoLocacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoLocacaoRepository extends JpaRepository<PlanoLocacao, Long> {
    Optional<PlanoLocacao> findByCodigo(String codigo);
    List<PlanoLocacao> findByAtivoTrue();
}

