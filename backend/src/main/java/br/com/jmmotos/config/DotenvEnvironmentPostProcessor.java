package br.com.jmmotos.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Se estiver rodando perfil de teste, não injeta variáveis de ambiente externas/produção
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        List<File> candidates = List.of(
            new File(".env"),
            new File("backend/.env"),
            new File("jm-motos/backend/.env"),
            new File("../backend/.env"),
            new File("../../backend/.env")
        );

        File envFile = null;
        for (File candidate : candidates) {
            if (candidate.exists()) {
                envFile = candidate;
                break;
            }
        }

        if (envFile != null && envFile.exists()) {
            Map<String, Object> envProperties = new HashMap<>();
            try {
                for (String line : Files.readAllLines(envFile.toPath())) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
                        int eqIdx = trimmed.indexOf('=');
                        String key = trimmed.substring(0, eqIdx).trim();
                        String value = trimmed.substring(eqIdx + 1).trim();

                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        envProperties.put(key, value);
                    }
                }
                environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envProperties));
            } catch (IOException ignored) {
            }
        }
    }
}

