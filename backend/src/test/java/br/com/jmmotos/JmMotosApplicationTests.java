package br.com.jmmotos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class JmMotosApplicationTests {

    @Test
    @DisplayName("Contexto Spring Boot e Flyway devem carregar com sucesso no perfil de teste")
    void contextLoads() {
        assertTrue(true);
    }
}

