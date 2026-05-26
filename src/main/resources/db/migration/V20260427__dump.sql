-- =============================================================
-- Leo Vidros — Dados iniciais de negócio
-- Gerado em: 2026-04-25
-- Executar na ordem: schema.sql → seed.sql → data.sql
--
-- IDs assumidos do seed.sql:
--   status: AGENDAMENTO(PENDENTE=1, EM ANDAMENTO=2, CONCLUÍDO=3, CANCELADO=4)
--           PEDIDO(ATIVO=5, EM ANDAMENTO=6, FINALIZADO=7, PENDENTE=8, CANCELADO=9)
--           SOLICITACAO(PENDENTE=10, ACEITO=11, RECUSADO=12)
--           ORCAMENTO(RASCUNHO=13,...,APROVADO=16,...)
--   etapa:  PENDENTE=1, AGUARDANDO ORÇAMENTO=2, ANÁLISE DO ORÇAMENTO=3,
--           ORÇAMENTO APROVADO=4, SERVIÇO AGENDADO=5, SERVIÇO EM EXECUÇÃO=6,
--           CONCLUÍDO=7, REAGENDAR=8
--   categoria: INFO=1, ERROR=2, DEBUG=3, WARNING=4, SUCCESS=5, FATAL=6
--
-- SENHA DO ADMIN: password
-- Hash: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
-- (bcrypt cost 10, retirado dos testes do Spring Security)
-- TROQUE antes de usar em produção via BCryptPasswordEncoder.encode()
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;

-- -------------------------------------------------------------
-- Métricas de estoque (uma por produto)
-- -------------------------------------------------------------

INSERT IGNORE INTO metrica_estoque (nivel_minimo, nivel_maximo) VALUES
                                                             ( 5, 100),   --  1 Vidro Temperado 6mm
                                                             ( 5, 100),   --  2 Vidro Temperado 8mm
                                                             ( 5,  80),   --  3 Vidro Laminado 6.6mm
                                                             ( 3,  50),   --  4 Vidro Serigrafado
                                                             ( 5,  80),   --  5 Espelho Cristal 4mm
                                                             ( 2,  30),   --  6 Vidro Insulado Duplo
                                                             ( 2,  20),   --  7 Box Banheiro Inox 8mm
                                                             (10, 200),   --  8 Perfil de Alumínio 6m
                                                             (20, 500),   --  9 Silicone Estrutural 600ml
                                                             (10, 200);   -- 10 Película Proteção Solar

-- -------------------------------------------------------------
-- Produtos (vidraçaria)
-- -------------------------------------------------------------

INSERT IGNORE INTO produto (nome, descricao, unidade_medida, preco, metrica_estoque_id, ativo) VALUES
                                                                                            ('Vidro Temperado 6mm',       'Vidro temperado de 6mm — alta resistência mecânica e térmica',            'm2',    85.00,  1, TRUE),
                                                                                            ('Vidro Temperado 8mm',       'Vidro temperado de 8mm — ideal para fachadas e divisórias',               'm2',   120.00,  2, TRUE),
                                                                                            ('Vidro Laminado 6.6mm',      'Vidro laminado com película PVB — segurança e controle solar',            'm2',   110.00,  3, TRUE),
                                                                                            ('Vidro Serigrafado 6mm',     'Vidro com impressão serigráfica — acabamento decorativo e privacidade',   'm2',   180.00,  4, TRUE),
                                                                                            ('Espelho Cristal 4mm',       'Espelho de cristal 4mm com tratamento anti-umidade nas bordas',           'm2',    75.00,  5, TRUE),
                                                                                            ('Vidro Insulado Duplo',      'Vidro duplo com câmara de ar — isolamento térmico e acústico',            'm2',   250.00,  6, TRUE),
                                                                                            ('Box Banheiro Inox 8mm',     'Box de banheiro com perfis em aço inox e vidro temperado 8mm',           'm2',   850.00,  7, TRUE),
                                                                                            ('Perfil Aluminio 6m',        'Perfil de alumínio anodizado 6 metros para esquadrias e molduras',        'un',    45.00,  8, TRUE),
                                                                                            ('Silicone Estrutural 600ml', 'Silicone estrutural neutro para vedação de vidros — cartucho 600ml',      'un',    32.00,  9, TRUE),
                                                                                            ('Pelicula Protecao Solar',   'Película de controle solar para vidros — rolo 1,52m x 30m',              'rolo',  320.00, 10, TRUE);

-- -------------------------------------------------------------
-- Atributos dos produtos
-- -------------------------------------------------------------

INSERT IGNORE INTO atributo_produto (produto_id, tipo, valor) VALUES
                                                           ( 1, 'espessura', '6mm'),       ( 1, 'cor',        'incolor'),
                                                           ( 2, 'espessura', '8mm'),       ( 2, 'cor',        'incolor'),
                                                           ( 3, 'espessura', '6.6mm'),     ( 3, 'tipo',       'laminado PVB'),
                                                           ( 4, 'espessura', '6mm'),       ( 4, 'acabamento', 'serigrafado'),
                                                           ( 5, 'espessura', '4mm'),       ( 5, 'acabamento', 'polido'),
                                                           ( 6, 'configuracao', 'duplo'),  ( 6, 'camara_ar',  '12mm'),
                                                           ( 7, 'material', 'inox 304'),   ( 7, 'espessura_vidro', '8mm'),
                                                           ( 8, 'material', 'aluminio anodizado'), ( 8, 'comprimento', '6m'),
                                                           ( 9, 'tipo',     'neutro'),     ( 9, 'volume',     '600ml'),
                                                           (10, 'tipo',     'controle solar'), (10, 'largura', '1.52m');

-- -------------------------------------------------------------
-- Estoque inicial
-- As reservas serão atualizadas ao final, após os agendamentos
-- -------------------------------------------------------------

INSERT IGNORE INTO estoque (produto_id, quantidade_total, quantidade_disponivel, reservado, localizacao) VALUES
                                                                                                      ( 1,  50.00,  50.00,  0.00, 'Galpao A - Prateleira 01'),
                                                                                                      ( 2,  40.00,  40.00,  0.00, 'Galpao A - Prateleira 02'),
                                                                                                      ( 3,  35.00,  35.00,  0.00, 'Galpao A - Prateleira 03'),
                                                                                                      ( 4,  20.00,  20.00,  0.00, 'Galpao A - Prateleira 04'),
                                                                                                      ( 5,  30.00,  30.00,  0.00, 'Galpao B - Espelhos'),
                                                                                                      ( 6,  15.00,  15.00,  0.00, 'Galpao A - Prateleira 05'),
                                                                                                      ( 7,   8.00,   8.00,  0.00, 'Showroom - Exposicao'),
                                                                                                      ( 8, 120.00, 120.00,  0.00, 'Almoxarifado - Perfis'),
                                                                                                      ( 9, 200.00, 200.00,  0.00, 'Almoxarifado - Insumos'),
                                                                                                      (10,  25.00,  25.00,  0.00, 'Almoxarifado - Peliculas');

-- -------------------------------------------------------------
-- Endereços (1=admin, 2-6=clientes, 7-16=agendamentos 1-10)
-- -------------------------------------------------------------

INSERT IGNORE INTO endereco (rua, numero, complemento, bairro, cidade, uf, cep, pais) VALUES
                                                                                   ('Rua das Violetas',        100,  NULL,           'Centro',           'Sao Paulo',      'SP', '01310100', 'Brasil'),  --  1 admin
                                                                                   ('Av. Paulista',           1500,  'Sala 42',       'Bela Vista',       'Sao Paulo',      'SP', '01311200', 'Brasil'),  --  2 cliente 1
                                                                                   ('Rua XV de Novembro',      200,  'Apto 301',      'Centro',           'Campinas',       'SP', '13010100', 'Brasil'),  --  3 cliente 2
                                                                                   ('Rua Sete de Setembro',    450,  NULL,            'Jardim America',   'Ribeirao Preto', 'SP', '14020060', 'Brasil'),  --  4 cliente 3
                                                                                   ('Rua das Acacias',          80,  NULL,            'Parque Industrial', 'Santo Andre',   'SP', '09111000', 'Brasil'),  --  5 cliente 4
                                                                                   ('Rua Espirito Santo',      320,  'Casa',          'Savassi',          'Belo Horizonte', 'MG', '30160030', 'Brasil'),  --  6 cliente 5
                                                                                   ('Rua Jose Bonifacio',       55,  NULL,            'Centro',           'Sao Paulo',      'SP', '01002001', 'Brasil'),  --  7 ag 1
                                                                                   ('Av. Brasil',              900,  NULL,            'Lapa',             'Sao Paulo',      'SP', '05050001', 'Brasil'),  --  8 ag 2
                                                                                   ('Rua Augusta',             780,  'Cobertura',     'Consolacao',       'Sao Paulo',      'SP', '01305100', 'Brasil'),  --  9 ag 3
                                                                                   ('Rua da Consolacao',      1200,  'Andar 3',       'Consolacao',       'Sao Paulo',      'SP', '01301000', 'Brasil'),  -- 10 ag 4
                                                                                   ('Av. Santo Amaro',        3500,  NULL,            'Itaim Bibi',       'Sao Paulo',      'SP', '04506001', 'Brasil'),  -- 11 ag 5
                                                                                   ('Rua Vergueiro',           560,  NULL,            'Liberdade',        'Sao Paulo',      'SP', '01504000', 'Brasil'),  -- 12 ag 6
                                                                                   ('Av. Reboucas',            800,  'Loja 2',        'Pinheiros',        'Sao Paulo',      'SP', '05402100', 'Brasil'),  -- 13 ag 7
                                                                                   ('Rua Haddock Lobo',        450,  NULL,            'Cerqueira Cesar',  'Sao Paulo',      'SP', '01414001', 'Brasil'),  -- 14 ag 8
                                                                                   ('Av. Pompeia',             350,  NULL,            'Pompeia',          'Sao Paulo',      'SP', '05023001', 'Brasil'),  -- 15 ag 9
                                                                                   ('Rua Harmonia',            200,  NULL,            'Vila Madalena',    'Sao Paulo',      'SP', '05435001', 'Brasil');  -- 16 ag 10

-- -------------------------------------------------------------
-- Usuário admin
-- senha: password   hash: bcrypt cost=10 (Spring Security test vector)
-- -------------------------------------------------------------

INSERT IGNORE INTO usuario (nome, cpf, email, senha, telefone, first_login, endereco_id) VALUES
    ('Administrador', '00000000000', 'admin@leovidros.com.br',
     '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
     '11999990000', FALSE, 1);

-- -------------------------------------------------------------
-- Clientes
-- -------------------------------------------------------------

INSERT IGNORE INTO cliente (nome, cpf, email, telefone, status) VALUES
                                                             ('Construtora Alpha Ltda',  '12345678000', 'compras@alphaconstr.com.br', '11991110001', 'ATIVO'),
                                                             ('Fernanda Costa',          '23456789001', 'fernanda.costa@email.com',   '11982220002', 'ATIVO'),
                                                             ('Imobiliaria Bela Vista',  '34567890002', 'obras@bellavista.imob.br',   '21973330003', 'ATIVO'),
                                                             ('Ricardo Almeida Santos',  '45678901003', 'r.almeida@email.com',        '31964440004', 'ATIVO'),
                                                             ('Condominio Villa Verde',  '56789012004', 'sindico@villaverde.cond.br', '11955550005', 'ATIVO');

INSERT IGNORE INTO cliente_endereco (cliente_id, endereco_id) VALUES
                                                           (1, 2), (2, 3), (3, 4), (4, 5), (5, 6);

-- -------------------------------------------------------------
-- Funcionários
-- -------------------------------------------------------------

INSERT IGNORE INTO funcionario (nome, telefone, funcao, contrato, escala, ativo) VALUES
                                                                              ('Carlos Eduardo Lima',    '11988880001', 'Vidraceiro Senior',  'CLT', '5x2 Seg-Sex', TRUE),
                                                                              ('Marcos Antonio Silva',   '11977770002', 'Vidraceiro',         'CLT', '5x2 Seg-Sex', TRUE),
                                                                              ('Paulo Roberto Ferreira', '11966660003', 'Instalador',         'CLT', '5x2 Seg-Sex', TRUE),
                                                                              ('Thiago Nascimento',      '11955550004', 'Auxiliar de Vidro',  'CLT', '6x1 Seg-Sab', TRUE),
                                                                              ('Ana Paula Rocha',        '11944440005', 'Tecnica em Vidro',   'PJ',  '5x2 Seg-Sex', TRUE);

-- -------------------------------------------------------------
-- Solicitações de acesso
-- -------------------------------------------------------------

INSERT IGNORE INTO solicitacao (nome, cpf, email, telefone, status_id) VALUES
                                                                    ('Joao Pedro Oliveira',    '67890123005', 'joao.pedro@email.com',   '11933331001', 10),
                                                                    ('Mariana Santos Freitas', '78901234006', 'mariana.sf@email.com',   '11922221002', 10),
                                                                    ('Lucas Henrique Dias',    '89012345007', 'lucas.hd@empresa.com',   '11911111003', 11),
                                                                    ('Beatriz Carvalho Melo',  '90123456008', 'bea.carvalho@email.com', '11900001004', 12);

-- -------------------------------------------------------------
-- Pedidos de serviço (10 — um por agendamento)
--   Pedidos 1-5:  vinculados a agendamentos ORCAMENTO
--   Pedidos 6-10: vinculados a agendamentos SERVICO (já aprovados)
-- -------------------------------------------------------------

INSERT IGNORE INTO pedido (cliente_id, status_id, valor_total, ativo, observacao, forma_pagamento, tipo_pedido) VALUES
                                                                                                             (1, 5,    0.00, TRUE, 'Instalacao de fachada envidracada — Construtora Alpha',  NULL,          'SERVICO'),
                                                                                                             (2, 5,    0.00, TRUE, 'Espelho para banheiro — residencia Fernanda Costa',      NULL,          'SERVICO'),
                                                                                                             (3, 5,    0.00, TRUE, 'Box de banheiro e divisoria — Imobiliaria Bela Vista',   NULL,          'SERVICO'),
                                                                                                             (4, 5,    0.00, TRUE, 'Substituicao de vidros quebrados — Ricardo Almeida',     NULL,          'SERVICO'),
                                                                                                             (5, 5,    0.00, TRUE, 'Pelicula solar em janelas — Condominio Villa Verde',     NULL,          'SERVICO'),
                                                                                                             (1, 5, 1800.00, TRUE, 'Guarda-corpo de vidro — Construtora Alpha ap 201',       'A COMBINAR',  'SERVICO'),
                                                                                                             (2, 5, 2200.00, TRUE, 'Porta de vidro temperado — Fernanda Costa',              'PIX',         'SERVICO'),
                                                                                                             (3, 5, 4500.00, TRUE, 'Cobertura em vidro laminado — Imobiliaria Bela Vista',   'PARCELADO',   'SERVICO'),
                                                                                                             (4, 5,  650.00, TRUE, 'Espelho bisote sala — Ricardo Almeida Santos',           'PIX',         'SERVICO'),
                                                                                                             (5, 5, 8500.00, TRUE, 'Fachada frameless — Condominio Villa Verde bloco B',     'PARCELADO',   'SERVICO');

-- -------------------------------------------------------------
-- Serviços
--   Serviços 1-5: etapa AGUARDANDO ORÇAMENTO (id=2)
--   Serviços 6-10: etapa SERVIÇO AGENDADO (id=5) — já passaram por
--                  aprovação de orçamento e tiveram agendamento criado
-- -------------------------------------------------------------

INSERT IGNORE INTO servico (nome, codigo, descricao, preco_base, ativo, pedido_id, etapa_id) VALUES
                                                                                          ('Fachada Envidracada',         'SV-001', 'Instalacao de fachada em vidro temperado 8mm com perfis',       0.00, TRUE,  1, 2),
                                                                                          ('Espelho Banheiro',            'SV-002', 'Fornecimento e instalacao de espelho cristal 4mm anti-umidade', 0.00, TRUE,  2, 2),
                                                                                          ('Box e Divisoria',             'SV-003', 'Instalacao de box de banheiro e divisoria de vidro temperado',  0.00, TRUE,  3, 2),
                                                                                          ('Substituicao de Vidros',      'SV-004', 'Retirada de vidros danificados e reposicao com novos',          0.00, TRUE,  4, 2),
                                                                                          ('Aplicacao Pelicula Solar',    'SV-005', 'Aplicacao de pelicula de controle solar em janelas externas',   0.00, TRUE,  5, 2),
                                                                                          ('Guarda-corpo Vidro',          'SV-006', 'Guarda-corpo em vidro temperado 8mm com perfis inox',        1800.00, TRUE,  6, 5),
                                                                                          ('Porta Vidro Temperado',       'SV-007', 'Porta pivotante em vidro temperado 8mm — acabamento inox',  2200.00, TRUE,  7, 5),
                                                                                          ('Cobertura Vidro Laminado',    'SV-008', 'Cobertura em vidro laminado 6.6mm com perfis de aluminio',  4500.00, TRUE,  8, 5),
                                                                                          ('Espelho Bisote Sala',         'SV-009', 'Espelho com borda bisote 4mm para sala de estar',             650.00, TRUE,  9, 5),
                                                                                          ('Fachada Frameless',           'SV-010', 'Sistema frameless com vidro temperado 8mm — fachada bloco B', 8500.00, TRUE, 10, 5);

-- -------------------------------------------------------------
-- Agendamentos
--   1-5: tipo ORCAMENTO — visita técnica para orçar
--   6-10: tipo SERVICO  — execução dos serviços aprovados
--
-- status 1 = AGENDAMENTO/PENDENTE
-- status 2 = AGENDAMENTO/EM ANDAMENTO
-- -------------------------------------------------------------

INSERT IGNORE INTO agendamento (servico_id, endereco_id, status_id, tipo, data_agendamento, inicio_agendamento, fim_agendamento, observacao) VALUES
                                                                                                                                          (1,  7, 2, 'ORCAMENTO', '2026-05-05', '08:00:00', '09:00:00', 'Visita tecnica para medicao e orcamento de fachada'),
                                                                                                                                          (2,  8, 2, 'ORCAMENTO', '2026-05-06', '09:30:00', '10:00:00', 'Medicao de espelho — banheiro suite master'),
                                                                                                                                          (3,  9, 2, 'ORCAMENTO', '2026-05-07', '10:00:00', '11:00:00', 'Medicao de box e divisoria — apartamento 401'),
                                                                                                                                          (4, 10, 1, 'ORCAMENTO', '2026-05-08', '14:00:00', '14:30:00', 'Vistoria de vidros danificados para levantamento'),
                                                                                                                                          (5, 11, 1, 'ORCAMENTO', '2026-05-09', '15:00:00', '16:00:00', 'Levantamento de janelas para aplicacao de pelicula');

INSERT IGNORE INTO agendamento (servico_id, endereco_id, status_id, tipo, data_agendamento, inicio_agendamento, fim_agendamento, observacao) VALUES
                                                                                                                                          ( 6, 12, 2, 'SERVICO', '2026-05-12', '07:00:00', '12:00:00', 'Instalacao de guarda-corpo — ap 201'),
                                                                                                                                          ( 7, 13, 2, 'SERVICO', '2026-05-13', '08:00:00', '11:00:00', 'Instalacao de porta pivotante — entrada social'),
                                                                                                                                          ( 8, 14, 1, 'SERVICO', '2026-05-14', '07:00:00', '13:00:00', 'Instalacao de cobertura — area de lazer'),
                                                                                                                                          ( 9, 15, 1, 'SERVICO', '2026-05-15', '09:00:00', '11:00:00', 'Instalacao de espelho bisote — sala principal'),
                                                                                                                                          (10, 16, 1, 'SERVICO', '2026-05-19', '07:00:00', '17:00:00', 'Instalacao de sistema frameless — fachada bloco B');

-- -------------------------------------------------------------
-- Funcionários nos agendamentos
-- -------------------------------------------------------------

INSERT IGNORE INTO agendamento_funcionario (agendamento_id, funcionario_id) VALUES
                                                                         (1, 1),
                                                                         (2, 2),
                                                                         (3, 1),
                                                                         (4, 3),
                                                                         (5, 5),
                                                                         (6, 1), (6, 3),
                                                                         (7, 2), (7, 4),
                                                                         (8, 1), (8, 2), (8, 4),
                                                                         (9, 3),
                                                                         (10, 1), (10, 2), (10, 3), (10, 5);

-- -------------------------------------------------------------
-- Produtos reservados para os agendamentos de SERVICO (6-10)
-- (reserva — não é saída definitiva; efetivada ao concluir)
-- -------------------------------------------------------------

INSERT IGNORE INTO agendamento_produto (agendamento_id, produto_id, quantidade_utilizada, quantidade_reservada) VALUES
                                                                                                             ( 6, 2,  0.00,  8.00),   -- guarda-corpo: 8m2 vidro temperado 8mm
                                                                                                             ( 6, 8,  0.00,  6.00),   -- guarda-corpo: 6 perfis aluminio
                                                                                                             ( 7, 2,  0.00,  3.00),   -- porta: 3m2 vidro temperado 8mm
                                                                                                             ( 7, 8,  0.00,  4.00),   -- porta: 4 perfis aluminio
                                                                                                             ( 8, 3,  0.00, 10.00),   -- cobertura: 10m2 vidro laminado 6.6mm
                                                                                                             ( 8, 8,  0.00, 12.00),   -- cobertura: 12 perfis aluminio
                                                                                                             ( 9, 5,  0.00,  2.50),   -- espelho bisote: 2,5m2 espelho cristal 4mm
                                                                                                             (10, 2,  0.00, 20.00),   -- fachada frameless: 20m2 vidro temperado 8mm
                                                                                                             (10, 8,  0.00, 30.00),   -- fachada frameless: 30 perfis aluminio
                                                                                                             (10, 9,  0.00, 15.00);   -- fachada frameless: 15 cartuchos silicone estrutural

-- -------------------------------------------------------------
-- Atualizar estoque com as reservas dos agendamentos de SERVICO
--
-- Produto 2 (Vidro Temperado 8mm):  ag6=8 + ag7=3 + ag10=20 = 31 reservados
-- Produto 3 (Vidro Laminado 6.6mm): ag8=10 = 10 reservados
-- Produto 5 (Espelho Cristal 4mm):  ag9=2.5 = 2.5 reservados
-- Produto 8 (Perfil Aluminio 6m):   ag6=6 + ag7=4 + ag8=12 + ag10=30 = 52 reservados
-- Produto 9 (Silicone Estrutural):  ag10=15 = 15 reservados
-- -------------------------------------------------------------

UPDATE estoque SET reservado = 31.00, quantidade_disponivel =  9.00 WHERE produto_id = 2;
UPDATE estoque SET reservado = 10.00, quantidade_disponivel = 25.00 WHERE produto_id = 3;
UPDATE estoque SET reservado =  2.50, quantidade_disponivel = 27.50 WHERE produto_id = 5;
UPDATE estoque SET reservado = 52.00, quantidade_disponivel = 68.00 WHERE produto_id = 8;
UPDATE estoque SET reservado = 15.00, quantidade_disponivel = 185.00 WHERE produto_id = 9;

-- =============================================================
-- ITEMS DO PEDIDO (item_pedido)
-- Links entre pedidos e estoque
-- =============================================================

INSERT IGNORE INTO item_pedido (pedido_id, estoque_id, quantidade_solicitada, preco_unitario_negociado, observacao) VALUES
-- Pedido 1: Fachada Envidracada (vidro temperado 8mm + perfis)
(1, 2, 15.00, 120.00, 'Vidro temperado 8mm para fachada principal'),
(1, 8,  9.00,  45.00, 'Perfis de aluminio 6m para estrutura'),
-- Pedido 2: Espelho Banheiro
(2, 5,  2.50,  75.00, 'Espelho cristal 4mm com acabamento anti-umidade'),
-- Pedido 3: Box e Divisoria
(3, 7,  1.00, 850.00, 'Box de banheiro em vidro temperado com inox'),
(3, 2,  5.00, 120.00, 'Vidro temperado 8mm para divisoria adicional'),
-- Pedido 4: Substituicao de Vidros
(4, 1,  3.00,  95.00, 'Vidro temperado 6mm para janelas'),
-- Pedido 5: Aplicacao Pelicula Solar
(5,10,  1.50, 360.00, 'Pelicula protecao solar rolo 1,52m x 30m'),

-- Pedido 6: Guarda-corpo Vidro
(6, 2,  8.00, 130.00, 'Vidro temperado 8mm para guarda-corpo'),
(6, 8,  6.00,  50.00, 'Perfis aluminio inox para guarda-corpo'),
(6, 9,  5.00,  38.00, 'Silicone estrutural para vedacao'),
-- Pedido 7: Porta Vidro Temperado
(7, 2,  3.00, 135.00, 'Vidro temperado 8mm para porta pivotante'),
(7, 8,  4.00,  52.00, 'Perfis aluminio para esquadria'),
(7, 9,  3.00,  38.00, 'Silicone estrutural para vedacao'),
-- Pedido 8: Cobertura Vidro Laminado
(8, 3, 10.00, 125.00, 'Vidro laminado 6.6mm para cobertura'),
(8, 8, 12.00,  52.00, 'Perfis aluminio para estrutura de cobertura'),
(8, 9,  8.00,  38.00, 'Silicone estrutural para vedacao'),
-- Pedido 9: Espelho Bisote Sala
(9, 5,  2.50,  85.00, 'Espelho cristal 4mm com borda bisote'),
-- Pedido 10: Fachada Frameless
(10, 2, 20.00, 135.00, 'Vidro temperado 8mm para fachada frameless'),
(10, 8, 30.00,  52.00, 'Perfis aluminio para sistema frameless'),
(10, 9, 15.00,  38.00, 'Silicone estrutural para vedacao');

-- =============================================================
-- ORÇAMENTOS (orcamento)
-- Os 5 primeiros (pedidos 1-5) em RASCUNHO
-- Os 5 ultimos (pedidos 6-10) em APROVADO
-- =============================================================

INSERT INTO orcamento (pedido_id, cliente_id, status_id, numero_orcamento, data_orcamento, observacoes, prazo_instalacao, garantia, forma_pagamento, valor_subtotal, valor_desconto, valor_total, pdf_path, status_fila, ativo) VALUES
-- Orçamentos em RASCUNHO (aguardando aprovação do cliente)
(1, 1, 13, 'ORC-2026-00001', '2026-04-20', 'Fachada envidracada para bloco comercial. Medicoes confirmadas em visita tecnica. Inclui consultoria de cor e acabamento.', '15 dias', '5 anos vidro', 'A DEFINIR', 2295.00, 150.00, 2145.00, NULL, 'PENDENTE', TRUE),
(2, 2, 13, 'ORC-2026-00002', '2026-04-21', 'Espelho com acabamento anti-umidade para banheiro suite. Medidas: 1,50m x 1,80m. Instalacao inclusa.', '7 dias', '3 anos', 'A DEFINIR', 187.50, 0.00, 187.50, NULL, 'PENDENTE', TRUE),
(3, 3, 13, 'ORC-2026-00003', '2026-04-22', 'Box banheiro + divisoria vidro temperado. Solucao completa com perfilaria em inox e vidro 8mm. Instalacao rapida.', '10 dias', '5 anos', 'A DEFINIR', 1390.00, 100.00, 1290.00, NULL, 'PENDENTE', TRUE),
(4, 4, 13, 'ORC-2026-00004', '2026-04-23', 'Reposicao de vidros quebrados. Medicoes na vistoria indicam 3m2 de vidro temperado 6mm incolor. Urgente.', '3 dias', '3 anos', 'A DEFINIR', 285.00, 0.00, 285.00, NULL, 'PENDENTE', TRUE),
(5, 5, 13, 'ORC-2026-00005', '2026-04-24', 'Aplicacao de pelicula solar em janelas externas — bloco residencial. Economiza ate 40% em ar condicionado.', '5 dias', '10 anos', 'A DEFINIR', 540.00, 40.00, 500.00, NULL, 'PENDENTE', TRUE),

-- Orçamentos APROVADOS (vinculados aos pedidos com agendamento de SERVICO)
(6, 1, 16, 'ORC-2026-00006', '2026-04-15', 'Guarda-corpo vidro temperado 8mm — apt 201. Pé-direito duplo com vista para area comum. Design moderno.', '7 dias', '10 anos vidro', 'A COMBINAR', 1920.00, 120.00, 1800.00, '/pdfs/orc_guarda_corpo_001.pdf', 'CONCLUIDO', TRUE),
(7, 2, 16, 'ORC-2026-00007', '2026-04-16', 'Porta pivotante vidro temperado — entrada social residencia. Acabamento espelho ouro fosco, vidro fumê 8mm.', '5 dias', '5 anos', 'PIX', 2340.00, 140.00, 2200.00, '/pdfs/orc_porta_pivot_001.pdf', 'CONCLUIDO', TRUE),
(8, 3, 16, 'ORC-2026-00008', '2026-04-17', 'Cobertura vidro laminado — area de lazer condominio. Isolamento termico e acustico. Seguro contra UV e intemperies.', '10 dias', '15 anos', 'PARCELADO', 4750.00, 250.00, 4500.00, '/pdfs/orc_cobertura_laminado.pdf', 'CONCLUIDO', TRUE),
(9, 4, 16, 'ORC-2026-00009', '2026-04-18', 'Espelho bisote sala — acabamento premium com borda polida 45 graus. Reflexo perfeito e seguro para famílias.', '3 dias', '3 anos', 'PIX', 687.50, 37.50, 650.00, '/pdfs/orc_espelho_bisote.pdf', 'CONCLUIDO', TRUE),
(10, 5, 16, 'ORC-2026-00010', '2026-04-19', 'Sistema frameless fachada bloco B — solucao arquitetonica de impacto. Vidro temperado 8mm incolor com perfilaria discreta. Produto diferenciado.', '14 dias', '10 anos vidro', 'PARCELADO', 9050.00, 550.00, 8500.00, '/pdfs/orc_fachada_frameless.pdf', 'CONCLUIDO', TRUE);

-- =============================================================
-- ITEMS DO ORÇAMENTO (orcamento_item)
-- Desdobramento dos itens por orçamento
-- =============================================================

-- Orçamento 1: Fachada Envidracada
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
                                                                                                                              (1, 2, 'Vidro Temperado 8mm - Incolor', 15.00, 120.00, 0.00, 'Alta resistência mecânica e térmica. Certificado de tempera conforme NBR.', 1),
                                                                                                                              (1, 8, 'Perfil Aluminio 6m - Anodizado', 9.00, 45.00, 0.00, 'Perfis estruturais para sustentacao da fachada. Acabamento anodizado cores claras.', 2),
                                                                                                                              (1, 9, 'Silicone Estrutural 600ml', 12.00, 32.00, 150.00, 'Vedacao estrutural dos vidros — cartuchos especializados com pistola mecanica.', 3);

-- Orçamento 2: Espelho Banheiro
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
    (2, 5, 'Espelho Cristal 4mm - Banheiro', 2.50, 75.00, 0.00, 'Espelho com tratamento anti-umidade nas bordas. Corte especial conforme medicoes.', 1);

-- Orçamento 3: Box e Divisoria
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
                                                                                                                              (3, 7, 'Box Banheiro Inox 8mm - Completo', 1.00, 850.00, 0.00, 'Box completo com piso resinado, vidro temperado 8mm e perfilaria em inox 304.', 1),
                                                                                                                              (3, 2, 'Vidro Temperado 8mm - Divisoria', 5.00, 120.00, 100.00, 'Vidro para divisoria adicional no quarto — corte sob medida, pontas polidas.', 2);

-- Orçamento 4: Substituicao de Vidros
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
    (4, 1, 'Vidro Temperado 6mm - Incolor', 3.00, 95.00, 0.00, 'Reposicao conforme especificacoes originais. Entrega urgente do vidro — prazo 48h.', 1);

-- Orçamento 5: Aplicacao Pelicula Solar
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
    (5, 10, 'Pelicula Protecao Solar - Rolo 1.52m', 1.50, 360.00, 40.00, 'Controle solar 40% - reduz consumo AR em ate 40%. Protege mobiliario de UV. Aplicacao profissional inclusa.', 1);

-- Orçamento 6: Guarda-corpo Vidro
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
                                                                                                                              (6, 2, 'Vidro Temperado 8mm - Guarda-corpo', 8.00, 130.00, 0.00, 'Vidro fumê 8mm temperado - alta segurança. Amostra entregue ao cliente aprovada.', 1),
                                                                                                                              (6, 8, 'Perfil Aluminio Inox - Estrutura', 6.00, 50.00, 0.00, 'Perfis especiais para guarda-corpo em acabamento inox polido.', 2),
                                                                                                                              (6, 9, 'Silicone Estrutural - Vedacao', 5.00, 38.00, 0.00, 'Silicone cinza estrutural — estrutura resistira cargas de impacto conforme norma.', 3);

-- Orçamento 7: Porta Vidro Temperado
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
                                                                                                                              (7, 2, 'Vidro Temperado 8mm - Porta', 3.00, 135.00, 0.00, 'Vidro fumê 8mm para porta pivotante — acabamento espelho ouro fosco confirmado com arquiteto.', 1),
                                                                                                                              (7, 8, 'Perfil Aluminio Ouro Fosco', 4.00, 52.00, 0.00, 'Perfilaria completa para porta pivotante — acabamento especial conforme request cliente.', 2),
                                                                                                                              (7, 9, 'Silicone Estrutural', 3.00, 38.00, 0.00, 'Silicone cinza para estrutura de sustentacao da porta.', 3);

-- Orçamento 8: Cobertura Vidro Laminado
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
                                                                                                                              (8, 3, 'Vidro Laminado 6.6mm - Cobertura', 10.00, 125.00, 0.00, 'Vidro laminado PVB incolor 6.6mm — máxima segurança. Cortinas de aluminio em cobertura lateral.', 1),
                                                                                                                              (8, 8, 'Perfil Aluminio - Estrutura Cobertura', 12.00, 52.00, 0.00, 'Perfis estruturais reforçados para sustentacao de vidro laminado — carga uniforme.', 2),
                                                                                                                              (8, 9, 'Silicone Estrutural - Impermeabilizacao', 8.00, 38.00, 0.00, 'Silicone branco estrutural para vedacao e impermeabilizacao total da cobertura.', 3);

-- Orçamento 9: Espelho Bisote Sala
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
    (9, 5, 'Espelho Bisote 4mm - Premium', 2.50, 85.00, 0.00, 'Espelho bisote premium com borda polida 45 graus — acabamento espelhado em toda extensao.', 1);

-- Orçamento 10: Fachada Frameless
INSERT INTO orcamento_item (orcamento_id, produto_id, descricao, quantidade, preco_unitario, desconto, observacao, ordem) VALUES
                                                                                                                              (10, 2, 'Vidro Temperado 8mm - Fachada Frameless', 20.00, 135.00, 0.00, 'Vidro incolor temperado 8mm — fachada frameless arquitetonicamente diferenciada. Certificação NBR 7199 e 7210.', 1),
                                                                                                                              (10, 8, 'Perfil Aluminio Discreto - Sistema Frameless', 30.00, 52.00, 0.00, 'Sistema frameless com perfilaria discreta e inovadora — fixações ocultas para impacto visual.', 2),
                                                                                                                              (10, 9, 'Silicone Estrutural - Vedacao Fachada', 15.00, 38.00, 0.00, 'Silicone estrutural cinza de alta performance para vedacao estrutural da fachada.', 3);

-- =============================================================
-- HISTÓRICO DE ESTOQUE (historico_estoque)
-- Registra movimentações de entrada e saida de produtos
-- =============================================================

INSERT INTO historico_estoque (estoque_id, usuario_id, tipo_movimentacao, quantidade, quantidade_atual, observacao, pedido_id, origem, motivo_perda, data_movimentacao) VALUES
-- Entradas iniciais (criacao do estoque base)
(1, 1, 'ENTRADA', 50.00, 50.00, 'Entrada inicial de estoque — Vidro Temperado 6mm', NULL, 'MANUAL', NULL, '2026-04-01 08:00:00'),
(2, 1, 'ENTRADA', 40.00, 40.00, 'Entrada inicial de estoque — Vidro Temperado 8mm', NULL, 'MANUAL', NULL, '2026-04-01 08:15:00'),
(3, 1, 'ENTRADA', 35.00, 35.00, 'Entrada inicial de estoque — Vidro Laminado 6.6mm', NULL, 'MANUAL', NULL, '2026-04-01 08:30:00'),
(4, 1, 'ENTRADA', 20.00, 20.00, 'Entrada inicial de estoque — Vidro Serigrafado 6mm', NULL, 'MANUAL', NULL, '2026-04-01 08:45:00'),
(5, 1, 'ENTRADA', 30.00, 30.00, 'Entrada inicial de estoque — Espelho Cristal 4mm', NULL, 'MANUAL', NULL, '2026-04-01 09:00:00'),
(6, 1, 'ENTRADA', 15.00, 15.00, 'Entrada inicial de estoque — Vidro Insulado Duplo', NULL, 'MANUAL', NULL, '2026-04-01 09:15:00'),
(7, 1, 'ENTRADA',  8.00,  8.00, 'Entrada inicial de estoque — Box Banheiro Inox 8mm', NULL, 'MANUAL', NULL, '2026-04-01 09:30:00'),
(8, 1, 'ENTRADA', 120.00, 120.00, 'Entrada inicial de estoque — Perfil de Aluminio 6m', NULL, 'MANUAL', NULL, '2026-04-01 09:45:00'),
(9, 1, 'ENTRADA', 200.00, 200.00, 'Entrada inicial de estoque — Silicone Estrutural 600ml', NULL, 'MANUAL', NULL, '2026-04-01 10:00:00'),
(10, 1, 'ENTRADA', 25.00, 25.00, 'Entrada inicial de estoque — Pelicula Protecao Solar', NULL, 'MANUAL', NULL, '2026-04-01 10:15:00'),

-- Saidas por pedidos ORCAMENTO (1-5) — ainda nao confirmadas
(1, 1, 'SAIDA', 3.00, 47.00, 'Saida para pedido 4 — Substituicao de vidros', 4, 'PEDIDO', NULL, '2026-04-23 14:00:00'),
(2, 1, 'SAIDA', 15.00, 25.00, 'Saida para pedido 1 — Fachada Envidracada', 1, 'PEDIDO', NULL, '2026-04-20 10:00:00'),
(5, 1, 'SAIDA', 2.50, 27.50, 'Saida para pedido 2 — Espelho Banheiro', 2, 'PEDIDO', NULL, '2026-04-21 11:00:00'),
(10, 1, 'SAIDA', 1.50, 23.50, 'Saida para pedido 5 — Aplicacao Pelicula Solar', 5, 'PEDIDO', NULL, '2026-04-24 15:00:00'),

-- Saidas por pedidos SERVICO (6-10) — em execucao
(2, 1, 'SAIDA', 8.00, 17.00, 'Saida para pedido 6 — Guarda-corpo Vidro (agendamento 6)', 6, 'PEDIDO', NULL, '2026-05-12 06:30:00'),
(2, 1, 'SAIDA', 3.00, 14.00, 'Saida para pedido 7 — Porta Vidro Temperado (agendamento 7)', 7, 'PEDIDO', NULL, '2026-05-13 07:30:00'),
(3, 1, 'SAIDA', 10.00, 25.00, 'Saida para pedido 8 — Cobertura Vidro Laminado (agendamento 8)', 8, 'PEDIDO', NULL, '2026-05-14 06:30:00'),
(5, 1, 'SAIDA', 2.50, 25.00, 'Saida para pedido 9 — Espelho Bisote Sala (agendamento 9)', 9, 'PEDIDO', NULL, '2026-05-15 08:30:00'),
(8, 1, 'SAIDA', 52.00, 68.00, 'Saida para pedidos 6-10 — Perfis Aluminio (múltiplos agendamentos)', NULL, 'PEDIDO', NULL, '2026-05-12 06:30:00'),
(9, 1, 'SAIDA', 26.00, 174.00, 'Saida para pedidos 6,7,8,10 — Silicone Estrutural (múltiplos agendamentos)', NULL, 'PEDIDO', NULL, '2026-05-12 06:30:00'),

-- Saida pela perda/quebra durante movimentacao
(1, 1, 'SAIDA', 1.00, 46.00, 'Perda por quebra durante transporte — Vidro Temperado 6mm', NULL, 'PERDA', 'QUEBRA', '2026-04-18 16:00:00'),
(4, 1, 'SAIDA', 2.00, 18.00, 'Perda por quebra na vidraria durante corte — Vidro Serigrafado', NULL, 'PERDA', 'QUEBRA', '2026-04-22 10:30:00'),

-- Ajuste de contagem por inventário
(7, 1, 'SAIDA', 0.50, 7.50, 'Ajuste por inventário — Box com desgaste de expocisao em showroom', NULL, 'AJUSTE', NULL, '2026-04-25 09:00:00');

-- =============================================================
-- LOGS DO SISTEMA (log)
-- Registros de eventos e operações importantes
-- =============================================================

INSERT INTO log (data_hora, id_categoria, mensagem) VALUES
                                                        ('2026-04-01 08:00:00', 1, 'Sistema inicializado com sucesso — banco de dados e tabelas criadas.'),
                                                        ('2026-04-01 08:15:00', 1, 'Estoque inicial carregado — 10 linhas de produtos com metricas de minimo e maximo.'),
                                                        ('2026-04-01 10:00:00', 5, 'Usuario admin criado: admin@leovidros.com.br'),
                                                        ('2026-04-01 14:00:00', 1, 'Carregamento de tabelas parametricas: status (13 registros), categoria (6), etapa (8).'),
                                                        ('2026-04-10 09:00:00', 1, '5 clientes cadastrados — Construtora Alpha, Fernanda Costa, Imobiliaria Bela Vista, Ricardo Almeida, Condominio Villa Verde.'),
                                                        ('2026-04-10 10:30:00', 1, '5 funcionarios ativos registrados — vidraceiros, instaladores, auxiliar e técnica.'),
                                                        ('2026-04-15 08:00:00', 5, 'Primeira solicitacao de acesso recebida — Joao Pedro Oliveira (status: PENDENTE).'),
                                                        ('2026-04-15 14:00:00', 1, '4 solicitacoes de acesso no sistema — 2 pendentes, 1 aceita, 1 recusada.'),
                                                        ('2026-04-20 10:00:00', 1, 'Orçamento ORC-2026-00001 criado para Construtora Alpha — Fachada Envidracada (R$ 2.145,00).'),
                                                        ('2026-04-21 11:00:00', 1, 'Orçamento ORC-2026-00002 criado para Fernanda Costa — Espelho Banheiro (R$ 187,50).'),
                                                        ('2026-04-22 10:00:00', 1, 'Orçamento ORC-2026-00003 criado para Imobiliaria Bela Vista — Box e Divisoria (R$ 1.290,00).'),
                                                        ('2026-04-23 14:00:00', 1, 'Orçamento ORC-2026-00004 criado para Ricardo Almeida — Substituicao de Vidros (R$ 285,00).'),
                                                        ('2026-04-24 15:00:00', 1, 'Orçamento ORC-2026-00005 criado para Condominio Villa Verde — Pelicula Solar (R$ 500,00).'),
                                                        ('2026-04-25 08:00:00', 5, 'Orçamentos de SERVICO aprovados — ORC-2026-00006 a 00010 pronto para agendamento.'),
                                                        ('2026-04-25 09:00:00', 1, 'Inventário de estoque revisado — ajustes por desgaste e quebras registrados.'),
                                                        ('2026-05-05 08:00:00', 1, 'Agendamento 1 iniciado — Visita tecnica para Fachada Envidracada na Rua Jose Bonifacio.'),
                                                        ('2026-05-06 09:30:00', 1, 'Agendamento 2 em andamento — Medicao de espelho em banheiro na Av. Brasil.'),
                                                        ('2026-05-07 10:00:00', 1, 'Agendamento 3 em andamento — Medicao box e divisoria na Rua Augusta.'),
                                                        ('2026-05-12 06:30:00', 1, 'Agendamento 6 iniciado — Instalacao de guarda-corpo vidro temperado (Pedido 6).'),
                                                        ('2026-05-12 12:00:00', 5, 'Agendamento 6 concluído com sucesso — Guarda-corpo instalado e testado, cliente aprovado.'),
                                                        ('2026-05-13 08:00:00', 1, 'Agendamento 7 em execucao — Instalacao de porta pivotante vidro fumê (Pedido 7).'),
                                                        ('2026-05-14 07:00:00', 1, 'Agendamento 8 em execucao — Instalacao de cobertura vidro laminado (Pedido 8) — múltiplos funcionarios.'),
                                                        ('2026-05-15 09:00:00', 1, 'Agendamento 9 em execucao — Instalacao de espelho bisote (Pedido 9).'),
                                                        ('2026-05-19 07:00:00', 1, 'Agendamento 10 em execucao — Instalacao de fachada frameless (Pedido 10) — serviço critico agendado para 1 dia (10h).'),
                                                        ('2026-04-25 10:00:00', 2, 'Aviso: Box em showroom apresenta desgaste em canto superior direito — substituir proximamente.');

SET FOREIGN_KEY_CHECKS = 1;