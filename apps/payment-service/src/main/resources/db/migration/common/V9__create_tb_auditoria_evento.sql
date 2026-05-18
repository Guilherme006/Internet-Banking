CREATE TABLE tb_auditoria_evento (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT       NULL,
    email       VARCHAR(160) NULL,
    acao        VARCHAR(60)  NOT NULL,
    status      VARCHAR(30)  NOT NULL,
    ip          VARCHAR(45)  NULL,
    user_agent  VARCHAR(255) NULL,
    detalhes    VARCHAR(500) NULL,
    criado_em   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_auditoria_evento PRIMARY KEY (id),
    INDEX idx_auditoria_usuario_data (usuario_id, criado_em),
    INDEX idx_auditoria_acao_data (acao, criado_em)
);

DELETE rt FROM tb_refresh_token rt
INNER JOIN tb_usuario u ON u.id = rt.usuario_id
WHERE u.email = 'joao@bancopagamento.com';

DELETE FROM tb_usuario WHERE email = 'joao@bancopagamento.com';
