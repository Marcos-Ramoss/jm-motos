package br.com.jmmotos.modules.plan.repository;

import br.com.jmmotos.modules.plan.model.PlanoVersao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoVersaoRepository extends JpaRepository<PlanoVersao, Long> {
    List<PlanoVersao> findByPlanoIdAndAtivoTrue(Long planoId);
    Optional<PlanoVersao> findFirstByPlanoIdAndAtivoTrueOrderByVersaoDesc(Long planoId);
}

