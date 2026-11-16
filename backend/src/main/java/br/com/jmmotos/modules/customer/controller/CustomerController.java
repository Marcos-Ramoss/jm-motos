package br.com.jmmotos.modules.customer.controller;

import br.com.jmmotos.modules.customer.dto.ClienteRequest;
import br.com.jmmotos.modules.customer.dto.ClienteResponse;
import br.com.jmmotos.modules.customer.usecase.CreateCustomerUseCase;
import br.com.jmmotos.modules.customer.usecase.GetCustomerByIdUseCase;
import br.com.jmmotos.modules.customer.usecase.ListCustomersUseCase;
import br.com.jmmotos.modules.customer.usecase.UpdateCustomerUseCase;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes / Locatários", description = "Endpoints para cadastro e consulta de clientes e documentação")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            ListCustomersUseCase listCustomersUseCase,
            GetCustomerByIdUseCase getCustomerByIdUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.getCustomerByIdUseCase = getCustomerByIdUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Cadastrar novo cliente locatário")
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = createCustomerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Atualizar dados cadastrais do cliente")
    public ResponseEntity<ClienteResponse> update(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = updateCustomerUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar clientes com filtros e paginação")
    public ResponseEntity<Page<ClienteResponse>> list(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 15) Pageable pageable) {
        Page<ClienteResponse> response = listCustomersUseCase.execute(busca, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter dados detalhados do cliente por ID")
    public ResponseEntity<ClienteResponse> getById(@PathVariable Long id) {
        ClienteResponse response = getCustomerByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}

