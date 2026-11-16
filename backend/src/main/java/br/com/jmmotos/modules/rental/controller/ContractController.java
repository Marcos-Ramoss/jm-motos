package br.com.jmmotos.modules.rental.controller;

import br.com.jmmotos.modules.rental.dto.ContratoDetailResponse;
import br.com.jmmotos.modules.rental.dto.ContratoResponse;
import br.com.jmmotos.modules.rental.dto.CreateContractRequest;
import br.com.jmmotos.modules.rental.usecase.CancelContractUseCase;
import br.com.jmmotos.modules.rental.usecase.CreateContractUseCase;
import br.com.jmmotos.modules.rental.usecase.GetContractByIdUseCase;
import br.com.jmmotos.modules.rental.usecase.ListContractsUseCase;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contratos")
@Tag(name = "Contratos de Locação", description = "Endpoints para emissão, acompanhamento de Rent-to-Own e encerramento de contratos")
public class ContractController {

    private final CreateContractUseCase createContractUseCase;
    private final ListContractsUseCase listContractsUseCase;
    private final GetContractByIdUseCase getContractByIdUseCase;
    private final CancelContractUseCase cancelContractUseCase;

    public ContractController(
            CreateContractUseCase createContractUseCase,
            ListContractsUseCase listContractsUseCase,
            GetContractByIdUseCase getContractByIdUseCase,
            CancelContractUseCase cancelContractUseCase) {
        this.createContractUseCase = createContractUseCase;
        this.listContractsUseCase = listContractsUseCase;
        this.getContractByIdUseCase = getContractByIdUseCase;
        this.cancelContractUseCase = cancelContractUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Criar novo contrato de locação com snapshot das condições")
    public ResponseEntity<ContratoResponse> create(@Valid @RequestBody CreateContractRequest request) {
        ContratoResponse response = createContractUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar contratos com filtros e paginação")
    public ResponseEntity<Page<ContratoResponse>> list(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 15) Pageable pageable) {
        Page<ContratoResponse> response = listContractsUseCase.execute(busca, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes completos do contrato, cobranças e extrato Rent-to-Own")
    public ResponseEntity<ContratoDetailResponse> getById(@PathVariable Long id) {
        ContratoDetailResponse response = getContractByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Cancelar contrato e liberar motocicleta")
    public ResponseEntity<ContratoResponse> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        ContratoResponse response = cancelContractUseCase.execute(id, motivo);
        return ResponseEntity.ok(response);
    }
}

