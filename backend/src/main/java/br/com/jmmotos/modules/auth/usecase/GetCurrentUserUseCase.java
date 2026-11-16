package br.com.jmmotos.modules.auth.usecase;

import br.com.jmmotos.exception.ResourceNotFoundException;
import br.com.jmmotos.modules.auth.dto.UserResponse;
import br.com.jmmotos.modules.auth.model.Usuario;
import br.com.jmmotos.modules.auth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCurrentUserUseCase {

    private final UsuarioRepository usuarioRepository;

    public GetCurrentUserUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse execute(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o e-mail: " + email));

        return UserResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil().name())
                .ativo(usuario.getAtivo())
                .build();
    }
}

