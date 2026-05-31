-- Tarefa 30: soft delete em Agendamento
ALTER TABLE agendamento ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;
