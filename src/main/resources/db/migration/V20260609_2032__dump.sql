-- -------------------------------------------------------------
-- Usuário admin
-- senha: password   hash: bcrypt cost=10 (Spring Security test vector)
-- -------------------------------------------------------------


SET FOREIGN_KEY_CHECKS = 0;

INSERT IGNORE INTO usuario (nome, cpf, email, senha, telefone, first_login, endereco_id) VALUES
    ('Administrador', '00000000000', 'admin@leovidros.com.br',
     '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
     '11999990000', FALSE, 1);

SET FOREIGN_KEY_CHECKS = 1;