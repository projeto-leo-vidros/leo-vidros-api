-- Tarefa 27: tabela de refresh tokens
CREATE TABLE IF NOT EXISTS refresh_token (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    token       VARCHAR(255) NOT NULL UNIQUE,
    usuario_id  INT NOT NULL,
    expiracao   TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_token ON refresh_token(token);
