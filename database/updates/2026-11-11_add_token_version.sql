-- Actualización de la tabla usuario para gestionar la revocación de JWT.
-- Esta instrucción ya fue ejecutada en la base de datos lovepaws_v2.
-- Ejecutar una sola vez en instalaciones nuevas.

ALTER TABLE usuario
ADD COLUMN token_version BIGINT NOT NULL DEFAULT 0;