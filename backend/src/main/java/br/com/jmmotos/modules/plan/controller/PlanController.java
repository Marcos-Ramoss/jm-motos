package br.com.jmmotos.modules.plan.controller;

import br.com.jmmotos.modules.plan.dto.PlanoResponse;
import br.com.jmmotos.modules.plan.usecase.GetPlanByIdUseCase;
import br.com.jmmotos.modules.plan.usecase.ListPlansUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planos")
@Tag(name = "Planos de Locação", description = "Endpoints para consulta de modelos e versões contratuais parametrizadas")
public class PlanController {

    private final ListPlansUseCase listPlansUseCase;
    private final GetPlanByIdUseCase getPlanByIdUseCase;

    public PlanController(
            ListPlansUseCase listPlansUseCase,
            GetPlanByIdUseCase getPlanByIdUseCase) {
        this.listPlansUseCase = listPlansUseCase;
        this.getPlanByIdUseCase = getPlanByIdUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar todos os modelos de planos de locação ativos")
    public ResponseEntity<List<PlanoResponse>> list() {
        List<PlanoResponse> response = listPlansUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de um modelo de plano e suas versões")
    public ResponseEntity<PlanoResponse> getById(@PathVariable Long id) {
        PlanoResponse response = getPlanByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

