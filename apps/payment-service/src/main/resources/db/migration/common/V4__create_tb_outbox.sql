CREATE TABLE tb_outbox (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    evento_id       VARCHAR(36) NOT NULL,
    tipo            VARCHAR(50) NOT NULL,
    payload         TEXT        NOT NULL,
    processado      BOOLEAN     NOT NULL DEFAULT FALSE,
    criado_em       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processado_em   DATETIME    NULL,

    CONSTRAINT pk_outbox PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_processado_criado ON tb_outbox (processado, criado_em);
