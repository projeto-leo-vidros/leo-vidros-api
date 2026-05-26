-- =============================================================
-- Leo Vidros — Schema baseline (criação do zero)
-- Data: 2026-06-01
-- Banco: MySQL 8.0
--
-- Esta migration substitui o histórico anterior (V20260425..V20260514)
-- por um baseline consolidado que reflete o estado canônico do schema.
-- Idêntica a leo-vidros-database/schema.sql.
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------------------------------
-- Tabelas base (sem dependências)
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS endereco (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    rua         VARCHAR(200),
    numero      INT,
    complemento VARCHAR(100),
    bairro      VARCHAR(100),
    cidade      VARCHAR(100),
    uf          CHAR(2),
    cep         VARCHAR(8),
    pais        VARCHAR(100),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS status (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    tipo       VARCHAR(100) COMMENT 'PEDIDO | AGENDAMENTO | SOLICITACAO | ORCAMENTO',
    nome       VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categoria (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nome       VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS etapa (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    tipo       VARCHAR(100),
    nome       VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS metrica_estoque (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    nivel_minimo INT DEFAULT 0,
    nivel_maximo INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------
-- Usuário e cliente
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS usuario (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    nome        VARCHAR(100) NOT NULL,
    cpf         CHAR(11) UNIQUE,
    email       VARCHAR(155) NOT NULL,
    senha       VARCHAR(255),
    telefone    VARCHAR(20),
    first_login BOOLEAN DEFAULT TRUE,
    endereco_id INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id)
);

CREATE TABLE IF NOT EXISTS cliente (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nome       VARCHAR(150) NOT NULL,
    cpf        CHAR(11),
    email      VARCHAR(150),
    telefone   VARCHAR(20),
    status     VARCHAR(10) DEFAULT 'ATIVO' COMMENT 'ATIVO | INATIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cliente_endereco (
    cliente_id  INT NOT NULL,
    endereco_id INT NOT NULL,
    PRIMARY KEY (cliente_id, endereco_id),
    CONSTRAINT fk_cli_end_cliente  FOREIGN KEY (cliente_id)  REFERENCES cliente(id),
    CONSTRAINT fk_cli_end_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id)
);

CREATE TABLE IF NOT EXISTS funcionario (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nome       VARCHAR(100),
    telefone   VARCHAR(100) UNIQUE,
    funcao     VARCHAR(100),
    contrato   VARCHAR(100),
    escala     VARCHAR(255),
    ativo      BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------
-- Logs e solicitações
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS log (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    data_hora    DATETIME NOT NULL,
    id_categoria INT NOT NULL,
    mensagem     TEXT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id)
);

CREATE TABLE IF NOT EXISTS solicitacao (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    cpf        CHAR(11) NOT NULL UNIQUE,
    email      VARCHAR(155) NOT NULL,
    telefone   VARCHAR(20),
    status_id  INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_solicitacao_status FOREIGN KEY (status_id) REFERENCES status(id)
);

-- -------------------------------------------------------------
-- Catálogo de produtos e estoque
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS produto (
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    nome                VARCHAR(100) NOT NULL,
    descricao           VARCHAR(255),
    unidade_medida      VARCHAR(255),
    preco               DECIMAL(16,5),
    metrica_estoque_id  INT,
    ativo               BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_produto_metrica FOREIGN KEY (metrica_estoque_id) REFERENCES metrica_estoque(id)
);

CREATE TABLE IF NOT EXISTS atributo_produto (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    produto_id INT NOT NULL,
    tipo       VARCHAR(100) COMMENT 'Ex: cor, espessura, material',
    valor      VARCHAR(100) COMMENT 'Ex: verde, 8mm, alumínio',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_atributo_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE IF NOT EXISTS estoque (
    id                    INT PRIMARY KEY AUTO_INCREMENT,
    produto_id            INT NOT NULL,
    quantidade_total      DECIMAL(18,2) DEFAULT 0 CHECK (quantidade_total >= 0),
    quantidade_disponivel DECIMAL(18,2) DEFAULT 0,
    reservado             DECIMAL(18,2) DEFAULT 0,
    localizacao           VARCHAR(100),
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_estoque_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);

-- -------------------------------------------------------------
-- Pedidos e serviços
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS pedido (
    id              INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id      INT,
    status_id       INT,
    valor_total     DECIMAL(18,2),
    ativo           BOOLEAN DEFAULT TRUE,
    observacao      TEXT,
    forma_pagamento VARCHAR(255),
    tipo_pedido     VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_status  FOREIGN KEY (status_id)  REFERENCES status(id),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id                       INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id                INT NOT NULL,
    estoque_id               INT NOT NULL,
    quantidade_solicitada    DECIMAL(18,5) NOT NULL,
    preco_unitario_negociado DECIMAL(18,5) NOT NULL,
    subtotal                 DECIMAL(18,2) AS (quantidade_solicitada * preco_unitario_negociado) STORED,
    observacao               TEXT,
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_pedido_pedido  FOREIGN KEY (pedido_id)  REFERENCES pedido(id),
    CONSTRAINT fk_item_pedido_estoque FOREIGN KEY (estoque_id) REFERENCES estoque(id)
);

CREATE TABLE IF NOT EXISTS servico (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nome       VARCHAR(150) NOT NULL,
    codigo     VARCHAR(255),
    descricao  TEXT,
    preco_base DECIMAL(18,2),
    ativo      BOOLEAN DEFAULT TRUE,
    pedido_id  INT UNIQUE,
    etapa_id   INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_servico_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_servico_etapa  FOREIGN KEY (etapa_id)  REFERENCES etapa(id)
);

CREATE TABLE IF NOT EXISTS historico_estoque (
    id                INT PRIMARY KEY AUTO_INCREMENT,
    estoque_id        INT NOT NULL,
    usuario_id        INT,
    tipo_movimentacao ENUM('ENTRADA','SAIDA') NOT NULL,
    quantidade        DECIMAL(18,2) NOT NULL,
    quantidade_atual  DECIMAL(18,2),
    observacao        VARCHAR(255),
    pedido_id         INT,
    origem            ENUM('PEDIDO','SERVICO','AGENDAMENTO','PERDA','AJUSTE','MANUAL') DEFAULT 'MANUAL',
    motivo_perda      ENUM('QUEBRA','FURTO','VENCIMENTO','OUTRO'),
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historico_estoque  FOREIGN KEY (estoque_id)  REFERENCES estoque(id),
    CONSTRAINT fk_historico_usuario  FOREIGN KEY (usuario_id)  REFERENCES usuario(id),
    CONSTRAINT fk_historico_pedido   FOREIGN KEY (pedido_id)   REFERENCES pedido(id)
);

-- -------------------------------------------------------------
-- Agendamentos
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS agendamento (
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    servico_id          INT NOT NULL,
    endereco_id         INT,
    status_id           INT,
    tipo                ENUM('ORCAMENTO','SERVICO') NOT NULL,
    data_agendamento    DATE NOT NULL,
    inicio_agendamento  TIME NOT NULL,
    fim_agendamento     TIME NOT NULL,
    observacao          TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_agendamento_servico  FOREIGN KEY (servico_id)  REFERENCES servico(id),
    CONSTRAINT fk_agendamento_status   FOREIGN KEY (status_id)   REFERENCES status(id),
    CONSTRAINT fk_agendamento_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id)
);

CREATE TABLE IF NOT EXISTS agendamento_funcionario (
    agendamento_id  INT NOT NULL,
    funcionario_id  INT NOT NULL,
    PRIMARY KEY (agendamento_id, funcionario_id),
    CONSTRAINT fk_ag_func_agendamento FOREIGN KEY (agendamento_id) REFERENCES agendamento(id),
    CONSTRAINT fk_ag_func_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

CREATE TABLE IF NOT EXISTS agendamento_produto (
    id                   INT PRIMARY KEY AUTO_INCREMENT,
    agendamento_id       INT NOT NULL,
    produto_id           INT NOT NULL,
    quantidade_utilizada DECIMAL(18,2) DEFAULT 0 CHECK (quantidade_utilizada >= 0),
    quantidade_reservada DECIMAL(18,2) DEFAULT 0 CHECK (quantidade_reservada >= 0),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ag_prod_agendamento FOREIGN KEY (agendamento_id) REFERENCES agendamento(id),
    CONSTRAINT fk_ag_prod_produto     FOREIGN KEY (produto_id)     REFERENCES produto(id)
);

-- -------------------------------------------------------------
-- Orçamentos
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS orcamento (
    id               INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id        INT NOT NULL,
    cliente_id       INT NOT NULL,
    status_id        INT,
    numero_orcamento VARCHAR(50),
    data_orcamento   DATE NOT NULL,
    observacoes      TEXT,
    prazo_instalacao VARCHAR(100),
    garantia         VARCHAR(100),
    forma_pagamento  VARCHAR(255),
    valor_subtotal   DECIMAL(18,2) DEFAULT 0,
    valor_desconto   DECIMAL(18,2) DEFAULT 0,
    valor_total      DECIMAL(18,2) DEFAULT 0,
    pdf_path         VARCHAR(500),
    status_fila      VARCHAR(20) NOT NULL DEFAULT 'PENDENTE' COMMENT 'PENDENTE | PROCESSANDO | CONCLUIDO | ERRO',
    ativo            BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orcamento_pedido  FOREIGN KEY (pedido_id)  REFERENCES pedido(id),
    CONSTRAINT fk_orcamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_orcamento_status  FOREIGN KEY (status_id)  REFERENCES status(id)
);

CREATE TABLE IF NOT EXISTS orcamento_item (
    id             INT PRIMARY KEY AUTO_INCREMENT,
    orcamento_id   INT NOT NULL,
    produto_id     INT,
    descricao      TEXT NOT NULL,
    quantidade     DECIMAL(18,5) NOT NULL,
    preco_unitario DECIMAL(18,5) NOT NULL,
    desconto       DECIMAL(18,2) DEFAULT 0,
    subtotal       DECIMAL(18,2) AS (quantidade * preco_unitario - desconto) STORED,
    observacao     TEXT,
    ordem          INT DEFAULT 0,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orcamento_item_orcamento FOREIGN KEY (orcamento_id) REFERENCES orcamento(id),
    CONSTRAINT fk_orcamento_item_produto   FOREIGN KEY (produto_id)   REFERENCES produto(id)
);

SET FOREIGN_KEY_CHECKS = 1;
