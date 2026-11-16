-- JM Motos: Esquema Inicial de Banco de Dados (Flyway Migration V1)
-- Compatível com PostgreSQL (Supabase) e H2 (Modo PostgreSQL)

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    rg VARCHAR(20),
    cnh_numero VARCHAR(20) NOT NULL,
    cnh_categoria VARCHAR(5) NOT NULL DEFAULT 'A',
    cnh_validade DATE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    endereco VARCHAR(255),
    municipio VARCHAR(100) DEFAULT 'Manaus',
    status VARCHAR(30) NOT NULL DEFAULT 'ATIVO',
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE motocicletas (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    chassi VARCHAR(30) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano INT NOT NULL,
    renavam VARCHAR(30),
    odometro INT NOT NULL DEFAULT 0,
    data_aquisicao DATE,
    custo_aquisicao NUMERIC(12, 2),
    municipio_operacao VARCHAR(100) DEFAULT 'Manaus',
    status_operacional VARCHAR(30) NOT NULL DEFAULT 'DISPONIVEL',
    status_rastreador VARCHAR(30) NOT NULL DEFAULT 'NAO_VINCULADO',
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE planos_locacao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descricao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE planos_versoes (
    id BIGSERIAL PRIMARY KEY,
    plano_id BIGINT NOT NULL REFERENCES planos_locacao(id),
    versao INT NOT NULL,
    modalidade VARCHAR(30) NOT NULL,
    valor_parcela NUMERIC(10, 2) NOT NULL,
    valor_aluguel NUMERIC(10, 2) NOT NULL,
    taxa_aquisicao NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    valor_caucao NUMERIC(10, 2) NOT NULL,
    total_unidades INT NOT NULL,
    dia_vencimento_padrao INT,
    horario_limite_vencimento VARCHAR(10),
    km_troca_oleo INT DEFAULT 1000,
    trocas_para_bonus_oleo INT DEFAULT 5,
    tolerancia_dias_inadimplencia INT DEFAULT 3,
    vigente_desde TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_plano_versao UNIQUE(plano_id, versao)
);

CREATE TABLE contratos_locacao (
    id BIGSERIAL PRIMARY KEY,
    numero_contrato VARCHAR(50) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    motocicleta_id BIGINT NOT NULL REFERENCES motocicletas(id),
    plano_versao_id BIGINT NOT NULL REFERENCES planos_versoes(id),
    status VARCHAR(40) NOT NULL DEFAULT 'ATIVO',
    data_inicio DATE NOT NULL,
    data_termino_prevista DATE,
    data_encerramento_real DATE,
    snapshot_modalidade VARCHAR(30) NOT NULL,
    snapshot_valor_parcela NUMERIC(10, 2) NOT NULL,
    snapshot_valor_aluguel NUMERIC(10, 2) NOT NULL,
    snapshot_taxa_aquisicao NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    snapshot_valor_caucao NUMERIC(10, 2) NOT NULL,
    snapshot_total_unidades INT NOT NULL,
    snapshot_dia_vencimento INT,
    unidades_totais INT NOT NULL,
    unidades_quitadas INT NOT NULL DEFAULT 0,
    percentual_aquisicao NUMERIC(6, 2) NOT NULL DEFAULT 0.00,
    observacoes TEXT,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cobrancas (
    id BIGSERIAL PRIMARY KEY,
    contrato_id BIGINT NOT NULL REFERENCES contratos_locacao(id),
    numero_parcela INT NOT NULL,
    tipo_cobranca VARCHAR(30) NOT NULL,
    data_vencimento DATE NOT NULL,
    valor_total NUMERIC(10, 2) NOT NULL,
    valor_aluguel NUMERIC(10, 2) NOT NULL,
    taxa_aquisicao NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    valor_pago NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    saldo_restante NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    data_pagamento_integral TIMESTAMP WITH TIME ZONE,
    motivo_isencao TEXT,
    isento_em TIMESTAMP WITH TIME ZONE,
    isento_por VARCHAR(150),
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pagamentos (
    id BIGSERIAL PRIMARY KEY,
    cobranca_id BIGINT NOT NULL REFERENCES cobrancas(id),
    valor NUMERIC(10, 2) NOT NULL,
    data_pagamento TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    forma_pagamento VARCHAR(30) NOT NULL,
    referencia_externa VARCHAR(100),
    comprovante_url VARCHAR(255),
    observacao TEXT,
    confirmado_por VARCHAR(150) NOT NULL,
    estornado BOOLEAN NOT NULL DEFAULT FALSE,
    estornado_em TIMESTAMP WITH TIME ZONE,
    estornado_por VARCHAR(150),
    motivo_estorno TEXT,
    idempotency_key VARCHAR(100) UNIQUE,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE caucao_movimentos (
    id BIGSERIAL PRIMARY KEY,
    contrato_id BIGINT NOT NULL REFERENCES contratos_locacao(id),
    tipo_movimento VARCHAR(30) NOT NULL,
    valor NUMERIC(10, 2) NOT NULL,
    data_movimento TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descricao TEXT,
    comprovante_url VARCHAR(255),
    registrado_por VARCHAR(150) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auditoria_eventos (
    id BIGSERIAL PRIMARY KEY,
    usuario VARCHAR(150) NOT NULL,
    acao VARCHAR(100) NOT NULL,
    entidade VARCHAR(50) NOT NULL,
    entidade_id VARCHAR(50),
    detalhes TEXT,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Índices essenciais para consultas frequentes e integridade
CREATE INDEX idx_clientes_cpf ON clientes(cpf);
CREATE INDEX idx_motos_placa ON motocicletas(placa);
CREATE INDEX idx_motos_status ON motocicletas(status_operacional);
CREATE INDEX idx_contratos_cliente ON contratos_locacao(cliente_id);
CREATE INDEX idx_contratos_moto ON contratos_locacao(motocicleta_id);
CREATE INDEX idx_contratos_status ON contratos_locacao(status);
CREATE INDEX idx_cobrancas_contrato ON cobrancas(contrato_id);
CREATE INDEX idx_cobrancas_status ON cobrancas(status);
CREATE INDEX idx_cobrancas_vencimento ON cobrancas(data_vencimento);
CREATE INDEX idx_pagamentos_cobranca ON pagamentos(cobranca_id);

