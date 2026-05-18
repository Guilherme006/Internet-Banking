CREATE TABLE tb_refresh_token (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    jti            VARCHAR(80)  NOT NULL,
    usuario_id     BIGINT       NOT NULL,
    expira_em      DATETIME     NOT NULL,
    revogado       BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revogado_em    DATETIME     NULL,

    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token_jti UNIQUE (jti),
    CONSTRAINT fk_refresh_token_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario (id)
);

CREATE INDEX idx_refresh_token_usuario ON tb_refresh_token (usuario_id);
CREATE INDEX idx_refresh_token_expira ON tb_refresh_token (expira_em);
