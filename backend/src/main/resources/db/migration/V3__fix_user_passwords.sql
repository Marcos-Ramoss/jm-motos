-- JM Motos: Ajuste das senhas iniciais com hash BCrypt válido para 'admin123' e 'operador123'
-- Hash gerado com BCryptPasswordEncoder do Spring Security para 'admin123'
UPDATE usuarios 
SET senha = '$2a$10$rxCVxVMULmjPOBAjzc5OAOGkr62TcNa8L6/NJ5OBG9pQtNki32UXm'
WHERE email IN ('admin@jmmotos.com.br', 'operador@jmmotos.com.br');

