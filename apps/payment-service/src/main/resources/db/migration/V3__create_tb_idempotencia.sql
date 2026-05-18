CREATE TABLE tb_idempotencia (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    chave       VARCHAR(36) NOT NULL,
    resposta    TEXT        NOT NULL,
    criado_em   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_em   DATETIME    NOT NULL,

    CONSTRAINT pk_idempotencia PRIMARY KEY (id),
    CONSTRAINT uq_idempotencia_chave UNIQUE (chave)
);

CREATE INDEX idx_idempotencia_chave_expira ON tb_idempotencia (chave, expira_em);
