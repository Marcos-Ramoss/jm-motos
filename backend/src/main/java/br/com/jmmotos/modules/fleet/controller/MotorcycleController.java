package br.com.jmmotos.modules.fleet.controller;

import br.com.jmmotos.modules.fleet.dto.MotocicletaRequest;
import br.com.jmmotos.modules.fleet.dto.MotocicletaResponse;
import br.com.jmmotos.modules.fleet.dto.MotocicletaStatusUpdateRequest;
import br.com.jmmotos.modules.fleet.usecase.CreateMotorcycleUseCase;
import br.com.jmmotos.modules.fleet.usecase.GetMotorcycleByIdUseCase;
import br.com.jmmotos.modules.fleet.usecase.ListMotorcyclesUseCase;
import br.com.jmmotos.modules.fleet.usecase.UpdateMotorcycleStatusUseCase;
import br.com.jmmotos.modules.fleet.usecase.UpdateMotorcycleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/motos")
@Tag(name = "Frota de Motocicletas", description = "Endpoints para gerenciamento do ciclo de vida das motocicletas")
public class MotorcycleController {

    private final CreateMotorcycleUseCase createMotorcycleUseCase;
    private final UpdateMotorcycleUseCase updateMotorcycleUseCase;
    private final ListMotorcyclesUseCase listMotorcyclesUseCase;
    private final GetMotorcycleByIdUseCase getMotorcycleByIdUseCase;
    private final UpdateMotorcycleStatusUseCase updateMotorcycleStatusUseCase;

    public MotorcycleController(
            CreateMotorcycleUseCase createMotorcycleUseCase,
            UpdateMotorcycleUseCase updateMotorcycleUseCase,
            ListMotorcyclesUseCase listMotorcyclesUseCase,
            GetMotorcycleByIdUseCase getMotorcycleByIdUseCase,
            UpdateMotorcycleStatusUseCase updateMotorcycleStatusUseCase) {
        this.createMotorcycleUseCase = createMotorcycleUseCase;
        this.updateMotorcycleUseCase = updateMotorcycleUseCase;
        this.listMotorcyclesUseCase = listMotorcyclesUseCase;
        this.getMotorcycleByIdUseCase = getMotorcycleByIdUseCase;
        this.updateMotorcycleStatusUseCase = updateMotorcycleStatusUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Cadastrar nova motocicleta na frota")
    public ResponseEntity<MotocicletaResponse> create(@Valid @RequestBody MotocicletaRequest request) {
        MotocicletaResponse response = createMotorcycleUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Atualizar dados de uma motocicleta existente")
    public ResponseEntity<MotocicletaResponse> update(@PathVariable Long id, @Valid @RequestBody MotocicletaRequest request) {
        MotocicletaResponse response = updateMotorcycleUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar motocicletas com filtros e paginação")
    public ResponseEntity<Page<MotocicletaResponse>> list(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 15) Pageable pageable) {
        Page<MotocicletaResponse> response = listMotorcyclesUseCase.execute(busca, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de uma motocicleta por ID")
    public ResponseEntity<MotocicletaResponse> getById(@PathVariable Long id) {
        MotocicletaResponse response = getMotorcycleByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Atualizar status operacional da motocicleta")
    public ResponseEntity<MotocicletaResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody MotocicletaStatusUpdateRequest request) {
        MotocicletaResponse response = updateMotorcycleStatusUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }
}

