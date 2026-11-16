package br.com.jmmotos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class JmMotosApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(JmMotosApplication.class, args);
        System.out.println("Swagger: http://localhost:8080/swagger-ui.html");
    }
}
