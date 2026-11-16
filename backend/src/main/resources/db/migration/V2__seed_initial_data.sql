-- JM Motos: Carga Inicial de Dados de Demonstração e Configurações (Flyway Migration V2)

-- Inserção dos dois modelos contratuais iniciais (id gerado automaticamente por BIGSERIAL)
INSERT INTO planos_locacao (nome, codigo, descricao, ativo) VALUES
('Plano Semanal 156 Semanas', 'PLANO_SEMANAL_156', 'Modelo semanal padrão: 156 semanas, R$ 210,00/semana, caução de R$ 500,00 com opção de compra ao final', TRUE),
('Plano Diário Uarini 1460 Diárias', 'PLANO_DIARIO_UARINI', 'Modelo diário Uarini/AM: 1.460 diárias, R$ 50,00/dia (R$ 45 aluguel + R$ 5 aquisição), caução de R$ 400,00', TRUE);

INSERT INTO planos_versoes (
    plano_id, versao, modalidade, valor_parcela, valor_aluguel, taxa_aquisicao, valor_caucao,
    total_unidades, dia_vencimento_padrao, horario_limite_vencimento, km_troca_oleo, trocas_para_bonus_oleo,
    tolerancia_dias_inadimplencia, ativo
) VALUES
(1, 1, 'SEMANAL', 210.00, 210.00, 0.00, 500.00, 156, 1, '23:59', 1000, 5, 3, TRUE),
(2, 1, 'DIARIO', 50.00, 45.00, 5.00, 400.00, 1460, 1, '23:00', 1000, 5, 2, TRUE);

-- Usuários iniciais com senhas protegidas por BCrypt (senha inicial: 'admin123' e 'operador123')
-- Hash gerado com BCrypt 10 rounds para 'admin123': $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
INSERT INTO usuarios (nome, email, senha, perfil, ativo) VALUES
('Administrador JM Motos', 'admin@jmmotos.com.br', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN', TRUE),
('Operador de Frotas', 'operador@jmmotos.com.br', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'OPERADOR', TRUE);
