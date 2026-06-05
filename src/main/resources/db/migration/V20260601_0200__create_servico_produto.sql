-- =============================================================
-- Unificação de produtos do pedido de serviço (servico_produto)
-- Fonte única de verdade da lista de produtos de um serviço.
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS servico_produto (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    servico_id            INT NOT NULL,
    produto_id            INT NOT NULL,
    quantidade_planejada  DECIMAL(18,5) NOT NULL DEFAULT 0,
    quantidade_utilizada  DECIMAL(18,5) NULL,
    preco_unitario        DECIMAL(18,5) NOT NULL DEFAULT 0,
    observacao            TEXT,
    ordem                 INT DEFAULT 0,
    ativo                 BOOLEAN NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sp_servico FOREIGN KEY (servico_id) REFERENCES servico(id),
    CONSTRAINT fk_sp_produto FOREIGN KEY (produto_id) REFERENCES produto(id),
    UNIQUE KEY uk_sp_servico_produto (servico_id, produto_id)
);

SET FOREIGN_KEY_CHECKS = 1;

-- -------------------------------------------------------------
-- Migração idempotente de dados existentes
-- -------------------------------------------------------------

-- 1) Fonte primária: itens do último orçamento ativo de cada serviço.
INSERT INTO servico_produto (servico_id, produto_id, quantidade_planejada, preco_unitario, observacao, ordem, ativo)
SELECT s.id,
       oi.produto_id,
       oi.quantidade,
       oi.preco_unitario,
       oi.observacao,
       COALESCE(oi.ordem, 0),
       TRUE
FROM servico s
JOIN orcamento o
       ON o.pedido_id = s.pedido_id
      AND o.ativo = TRUE
JOIN orcamento_item oi
       ON oi.orcamento_id = o.id
      AND oi.produto_id IS NOT NULL
WHERE s.pedido_id IS NOT NULL
  AND o.id = (
        SELECT o2.id
        FROM orcamento o2
        WHERE o2.pedido_id = s.pedido_id
          AND o2.ativo = TRUE
        ORDER BY o2.created_at DESC, o2.id DESC
        LIMIT 1
  )
ON DUPLICATE KEY UPDATE
       quantidade_planejada = VALUES(quantidade_planejada),
       preco_unitario       = VALUES(preco_unitario),
       observacao           = VALUES(observacao),
       ordem                = VALUES(ordem),
       ativo                = TRUE;

-- 2) Fallback: agendamento_produto (tipo ORCAMENTO/vistoria) para serviços
--    que ainda não possuem nenhuma linha em servico_produto.
INSERT INTO servico_produto (servico_id, produto_id, quantidade_planejada, preco_unitario, ordem, ativo)
SELECT a.servico_id,
       ap.produto_id,
       COALESCE(NULLIF(ap.quantidade_reservada, 0), ap.quantidade_utilizada, 0),
       COALESCE(p.preco, 0),
       0,
       TRUE
FROM agendamento_produto ap
JOIN agendamento a
       ON a.id = ap.agendamento_id
      AND a.tipo = 'ORCAMENTO'
JOIN produto p
       ON p.id = ap.produto_id
WHERE NOT EXISTS (
        SELECT 1 FROM servico_produto sp WHERE sp.servico_id = a.servico_id
)
ON DUPLICATE KEY UPDATE
       quantidade_planejada = VALUES(quantidade_planejada);
