CREATE TABLE tb_usuario (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nome           VARCHAR(120) NOT NULL,
    email          VARCHAR(160) NOT NULL,
    cpf            VARCHAR(11)  NOT NULL,
    senha_hash     VARCHAR(120) NOT NULL,
    numero_conta   VARCHAR(10)  NOT NULL,
    cep            VARCHAR(8)   NOT NULL,
    logradouro     VARCHAR(140) NOT NULL,
    numero         VARCHAR(20)  NOT NULL,
    complemento    VARCHAR(80)  NULL,
    bairro         VARCHAR(80)  NOT NULL,
    cidade         VARCHAR(80)  NOT NULL,
    uf             CHAR(2)      NOT NULL,
    criado_em      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uq_usuario_email UNIQUE (email),
    CONSTRAINT uq_usuario_cpf UNIQUE (cpf),
    CONSTRAINT uq_usuario_conta UNIQUE (numero_conta),
    CONSTRAINT fk_usuario_conta FOREIGN KEY (numero_conta) REFERENCES tb_conta (numero_conta)
);

INSERT INTO tb_usuario (
    nome, email, cpf, senha_hash, numero_conta,
    cep, logradouro, numero, complemento, bairro, cidade, uf
) VALUES (
    'João da Silva',
    'joao@bancopagamento.com',
    '12345678901',
    '$2a$10$KJjqP8.a6aoF09mBvwGOYevHge2ALAWUvDXmLt9b1yqXrjVpvdi02',
    '12345-6',
    '01001000',
    'Praça da Sé',
    '100',
    'Conjunto 12',
    'Sé',
    'São Paulo',
    'SP'
);
