package br.com.jmmotos.modules.auth.controller;

import br.com.jmmotos.modules.auth.dto.LoginRequest;
import br.com.jmmotos.modules.auth.dto.LoginResponse;
import br.com.jmmotos.modules.auth.dto.UserResponse;
import br.com.jmmotos.modules.auth.usecase.AuthenticateUserUseCase;
import br.com.jmmotos.modules.auth.usecase.GetCurrentUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints de login e verificação de credenciais")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login e obter token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticateUserUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário autenticado atual")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        UserResponse response = getCurrentUserUseCase.execute(authentication.getName());
        return ResponseEntity.ok(response);
    }
}

