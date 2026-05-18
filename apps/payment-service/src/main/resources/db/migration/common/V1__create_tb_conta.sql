CREATE TABLE tb_conta (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    numero_conta VARCHAR(10)    NOT NULL,
    agencia     VARCHAR(10)     NOT NULL,
    titular     VARCHAR(100)    NOT NULL,
    saldo       DECIMAL(19, 2)  NOT NULL DEFAULT 0.00,
    versao      BIGINT          NOT NULL DEFAULT 0,
    criado_em   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_conta PRIMARY KEY (id),
    CONSTRAINT uq_conta_numero UNIQUE (numero_conta)
);

INSERT INTO tb_conta (numero_conta, agencia, titular, saldo) VALUES
    ('12345-6', '0001', 'João da Silva',  5000.00),
    ('78901-2', '0001', 'Maria Souza',   10000.00),
    ('11111-1', '0002', 'Carlos Ferreira', 500.00);
