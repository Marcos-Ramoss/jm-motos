package br.com.jmmotos.modules.auth.usecase;

import br.com.jmmotos.config.JwtTokenProvider;
import br.com.jmmotos.exception.BusinessRuleException;
import br.com.jmmotos.modules.auth.dto.LoginRequest;
import br.com.jmmotos.modules.auth.dto.LoginResponse;
import br.com.jmmotos.modules.auth.dto.UserResponse;
import br.com.jmmotos.modules.auth.model.Usuario;
import br.com.jmmotos.modules.auth.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticateUserUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthenticateUserUseCase(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional(readOnly = true)
    public LoginResponse execute(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if (!usuario.getAtivo()) {
            throw new BusinessRuleException("Usuário inativo. Entre em contato com o administrador.");
        }

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        String token = tokenProvider.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getPerfil().name()
        );

        UserResponse userResponse = UserResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil().name())
                .ativo(usuario.getAtivo())
                .build();

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .usuario(userResponse)
                .build();
    }
}

