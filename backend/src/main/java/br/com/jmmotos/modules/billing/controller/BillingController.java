package br.com.jmmotos.modules.billing.controller;

import br.com.jmmotos.modules.billing.dto.CaucaoMovimentoResponse;
import br.com.jmmotos.modules.billing.dto.CobrancaResponse;
import br.com.jmmotos.modules.billing.dto.ExemptBillingRequest;
import br.com.jmmotos.modules.billing.dto.PagamentoResponse;
import br.com.jmmotos.modules.billing.dto.RegisterCaucaoRequest;
import br.com.jmmotos.modules.billing.dto.RegisterPaymentRequest;
import br.com.jmmotos.modules.billing.dto.ReversePaymentRequest;
import br.com.jmmotos.modules.billing.usecase.ExemptBillingUseCase;
import br.com.jmmotos.modules.billing.usecase.ListBillingsUseCase;
import br.com.jmmotos.modules.billing.usecase.ListCaucaoMovementsUseCase;
import br.com.jmmotos.modules.billing.usecase.RegisterCaucaoMovementUseCase;
import br.com.jmmotos.modules.billing.usecase.RegisterPaymentUseCase;
import br.com.jmmotos.modules.billing.usecase.ReversePaymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cobrancas")
@Tag(name = "Contas a Receber e Cobranças", description = "Endpoints para consulta de cobranças, recebimento de pagamentos, isenções, estornos e cauções")
public class BillingController {

    private final ListBillingsUseCase listBillingsUseCase;
    private final RegisterPaymentUseCase registerPaymentUseCase;
    private final ExemptBillingUseCase exemptBillingUseCase;
    private final ReversePaymentUseCase reversePaymentUseCase;
    private final RegisterCaucaoMovementUseCase registerCaucaoMovementUseCase;
    private final ListCaucaoMovementsUseCase listCaucaoMovementsUseCase;

    public BillingController(
            ListBillingsUseCase listBillingsUseCase,
            RegisterPaymentUseCase registerPaymentUseCase,
            ExemptBillingUseCase exemptBillingUseCase,
            ReversePaymentUseCase reversePaymentUseCase,
            RegisterCaucaoMovementUseCase registerCaucaoMovementUseCase,
            ListCaucaoMovementsUseCase listCaucaoMovementsUseCase) {
        this.listBillingsUseCase = listBillingsUseCase;
        this.registerPaymentUseCase = registerPaymentUseCase;
        this.exemptBillingUseCase = exemptBillingUseCase;
        this.reversePaymentUseCase = reversePaymentUseCase;
        this.registerCaucaoMovementUseCase = registerCaucaoMovementUseCase;
        this.listCaucaoMovementsUseCase = listCaucaoMovementsUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar cobranças com filtros por contrato, status e período")
    public ResponseEntity<Page<CobrancaResponse>> list(
            @RequestParam(required = false) Long contratoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CobrancaResponse> response = listBillingsUseCase.execute(contratoId, status, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pagamentos")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Registrar pagamento (parcial ou total) para uma cobrança")
    public ResponseEntity<PagamentoResponse> registerPayment(
            @Valid @RequestBody RegisterPaymentRequest request,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "SISTEMA";
        PagamentoResponse response = registerPaymentUseCase.execute(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/isentar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Conceder isenção de cobrança por motivo de oficina/manutenção")
    public ResponseEntity<CobrancaResponse> exemptBilling(
            @PathVariable Long id,
            @Valid @RequestBody ExemptBillingRequest request,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "SISTEMA";
        CobrancaResponse response = exemptBillingUseCase.execute(id, request, usuario);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pagamentos/{id}/estornar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Estornar um pagamento confirmado com justificativa auditada")
    public ResponseEntity<PagamentoResponse> reversePayment(
            @PathVariable Long id,
            @Valid @RequestBody ReversePaymentRequest request,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "SISTEMA";
        PagamentoResponse response = reversePaymentUseCase.execute(id, request, usuario);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/caucao")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(summary = "Registrar movimentação de caução em razão auxiliar (entrada, retenção ou devolução)")
    public ResponseEntity<CaucaoMovimentoResponse> registerCaucao(
            @Valid @RequestBody RegisterCaucaoRequest request,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "SISTEMA";
        CaucaoMovimentoResponse response = registerCaucaoMovementUseCase.execute(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/caucao")
    @Operation(summary = "Listar movimentações de caução (global ou por contrato)")
    public ResponseEntity<List<CaucaoMovimentoResponse>> listCaucao(@RequestParam(required = false) Long contratoId) {
        List<CaucaoMovimentoResponse> response = listCaucaoMovementsUseCase.execute(contratoId);
        return ResponseEntity.ok(response);
    }
}

