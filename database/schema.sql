CREATE DATABASE IF NOT EXISTS ecolog_alimentos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ecolog_alimentos;

CREATE TABLE IF NOT EXISTS tb_ponto_coleta (
    id_coleta     BIGINT       NOT NULL AUTO_INCREMENT,
    razao_social  VARCHAR(150) NOT NULL,
    cnpj          VARCHAR(18)  NOT NULL,
    endereco      VARCHAR(255) NOT NULL,
    telefone      VARCHAR(20)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    CONSTRAINT pk_ponto_coleta PRIMARY KEY (id_coleta),
    CONSTRAINT uk_ponto_coleta_cnpj UNIQUE (cnpj)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tb_instituicao_receptora (
    id_receptora              BIGINT        NOT NULL AUTO_INCREMENT,
    razao_social              VARCHAR(150)  NOT NULL,
    cnpj                      VARCHAR(18)   NOT NULL,
    endereco                  VARCHAR(255)  NOT NULL,
    capacidade_refrigerada_kg DECIMAL(10,2) NOT NULL DEFAULT 0,
    telefone                  VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_instituicao_receptora PRIMARY KEY (id_receptora),
    CONSTRAINT uk_instituicao_receptora_cnpj UNIQUE (cnpj),
    CONSTRAINT ck_instituicao_capacidade CHECK (capacidade_refrigerada_kg >= 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tb_ordem_transporte (
    id_ordem        BIGINT      NOT NULL AUTO_INCREMENT,
    data_agendada   DATE        NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    fk_id_coleta    BIGINT      NOT NULL,
    fk_id_receptora BIGINT      NOT NULL,
    CONSTRAINT pk_ordem_transporte PRIMARY KEY (id_ordem),
    CONSTRAINT fk_ordem_ponto_coleta FOREIGN KEY (fk_id_coleta)
        REFERENCES tb_ponto_coleta (id_coleta),
    CONSTRAINT fk_ordem_instituicao FOREIGN KEY (fk_id_receptora)
        REFERENCES tb_instituicao_receptora (id_receptora),
    CONSTRAINT ck_ordem_status
        CHECK (status IN ('PENDENTE', 'EM_TRANSITO', 'CONCLUIDA', 'CANCELADA'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tb_lote_alimento (
    id_lote            BIGINT        NOT NULL AUTO_INCREMENT,
    descricao          VARCHAR(120)  NOT NULL,
    categoria          VARCHAR(50)   NOT NULL,
    quantidade_kg      DECIMAL(10,2) NOT NULL,
    data_validade      DATE          NOT NULL,
    tipo_lote          VARCHAR(20)   NOT NULL,
    temperatura_minima DECIMAL(5,2)  NULL,
    temperatura_maxima DECIMAL(5,2)  NULL,
    tipo_embalagem     VARCHAR(50)   NULL,
    fk_id_ordem        BIGINT        NOT NULL,
    CONSTRAINT pk_lote_alimento PRIMARY KEY (id_lote),
    CONSTRAINT fk_lote_ordem FOREIGN KEY (fk_id_ordem)
        REFERENCES tb_ordem_transporte (id_ordem)
        ON DELETE CASCADE,
    CONSTRAINT ck_lote_tipo CHECK (tipo_lote IN ('PERECIVEL', 'NAO_PERECIVEL')),
    CONSTRAINT ck_lote_quantidade CHECK (quantidade_kg > 0)
) ENGINE = InnoDB;
