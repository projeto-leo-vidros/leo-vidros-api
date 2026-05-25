-- =============================================================
-- Leo Vidros — Dados de referência (seed baseline)
-- Data: 2026-06-01
--
-- Inserções idempotentes (NOT EXISTS) — seguras para rodar
-- contra um banco que já contenha alguns valores.
-- Idêntica em conteúdo a leo-vidros-database/seed.sql.
--
-- IDs gerados (referência para data.sql / código):
--   categoria:    1..6
--   etapa:        1..8
--   status:       AGENDAMENTO 1..4 | PEDIDO 5..7 | SOLICITACAO 8..10 | ORCAMENTO 11..16
-- =============================================================

-- -------------------------------------------------------------
-- Categorias de log
-- -------------------------------------------------------------

INSERT INTO categoria (nome)
SELECT nome FROM (VALUES
    ROW('INFO'),
    ROW('ERROR'),
    ROW('DEBUG'),
    ROW('WARNING'),
    ROW('SUCCESS'),
    ROW('FATAL')
) AS novos(nome)
WHERE NOT EXISTS (
    SELECT 1 FROM categoria c WHERE c.nome = novos.nome
);

-- -------------------------------------------------------------
-- Etapas do fluxo de pedido de serviço
-- -------------------------------------------------------------

INSERT INTO etapa (tipo, nome)
SELECT tipo, nome FROM (VALUES
    ROW('PEDIDO', 'AGUARDANDO AGENDA DE ORÇAMENTO'),
    ROW('PEDIDO', 'ORÇAMENTO AGENDADO'),
    ROW('PEDIDO', 'ANÁLISE DO ORÇAMENTO'),
    ROW('PEDIDO', 'ORÇAMENTO APROVADO'),
    ROW('PEDIDO', 'AGUARDANDO AGENDA DE SERVIÇO/INSTALAÇÃO'),
    ROW('PEDIDO', 'SERVIÇO AGENDADO'),
    ROW('PEDIDO', 'AGENDAMENTO EM EXECUÇÃO'),
    ROW('PEDIDO', 'CONCLUÍDO')
) AS novos(tipo, nome)
WHERE NOT EXISTS (
    SELECT 1 FROM etapa e WHERE e.tipo = novos.tipo AND e.nome = novos.nome
);

-- -------------------------------------------------------------
-- Status (AGENDAMENTO | PEDIDO | SOLICITACAO | ORCAMENTO)
-- -------------------------------------------------------------

INSERT INTO status (tipo, nome)
SELECT tipo, nome FROM (VALUES
    ROW('AGENDAMENTO', 'PENDENTE'),
    ROW('AGENDAMENTO', 'EM ANDAMENTO'),
    ROW('AGENDAMENTO', 'CONCLUÍDO'),
    ROW('AGENDAMENTO', 'CANCELADO'),

    ROW('PEDIDO', 'ATIVO'),
    ROW('PEDIDO', 'INATIVO'),
    ROW('PEDIDO', 'CANCELADO'),

    ROW('SOLICITACAO', 'PENDENTE'),
    ROW('SOLICITACAO', 'ACEITO'),
    ROW('SOLICITACAO', 'RECUSADO'),

    ROW('ORCAMENTO', 'RASCUNHO'),
    ROW('ORCAMENTO', 'ENVIADO'),
    ROW('ORCAMENTO', 'EM ANALISE'),
    ROW('ORCAMENTO', 'APROVADO'),
    ROW('ORCAMENTO', 'RECUSADO'),
    ROW('ORCAMENTO', 'EXPIRADO')
) AS novos(tipo, nome)
WHERE NOT EXISTS (
    SELECT 1 FROM status s WHERE s.tipo = novos.tipo AND s.nome = novos.nome
);
