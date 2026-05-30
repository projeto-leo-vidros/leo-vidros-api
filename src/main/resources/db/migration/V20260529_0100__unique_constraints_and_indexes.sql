-- Semana 1 — Auditoria: constraints UNIQUE faltantes + índices de busca frequente

-- -------------------------------------------------------
-- UNIQUE constraints (DB-03)
-- -------------------------------------------------------

ALTER TABLE usuario
    ADD CONSTRAINT uq_usuario_email UNIQUE (email);

ALTER TABLE cliente
    ADD CONSTRAINT uq_cliente_cpf UNIQUE (cpf),
    ADD CONSTRAINT uq_cliente_email UNIQUE (email);

ALTER TABLE solicitacao
    ADD CONSTRAINT uq_solicitacao_email UNIQUE (email);

-- -------------------------------------------------------
-- Índices de busca frequente (DB-01)
-- -------------------------------------------------------

-- usuario
CREATE INDEX idx_usuario_email          ON usuario(email);

-- cliente
CREATE INDEX idx_cliente_cpf            ON cliente(cpf);
CREATE INDEX idx_cliente_nome           ON cliente(nome);

-- funcionario
CREATE INDEX idx_funcionario_nome       ON funcionario(nome);

-- agendamento
CREATE INDEX idx_agendamento_data       ON agendamento(data_agendamento);

-- agendamento_funcionario (tabela de join)
CREATE INDEX idx_ag_func_funcionario    ON agendamento_funcionario(funcionario_id);

-- historico_estoque
CREATE INDEX idx_historico_data         ON historico_estoque(data_movimentacao);

-- pedido
CREATE INDEX idx_pedido_cliente         ON pedido(cliente_id);

-- item_pedido
CREATE INDEX idx_item_pedido_pedido     ON item_pedido(pedido_id);
