CREATE TABLE tb_boleto (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    codigo_barra    VARCHAR(48)     NOT NULL,
    beneficiario    VARCHAR(100)    NOT NULL,
    pagador         VARCHAR(100)    NOT NULL,
    valor           DECIMAL(19, 2)  NOT NULL,
    data_vencimento DATE            NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDENTE',
    criado_em       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_boleto PRIMARY KEY (id),
    CONSTRAINT uq_boleto_codigo UNIQUE (codigo_barra),
    CONSTRAINT chk_boleto_status CHECK (status IN ('PENDENTE', 'PAGO', 'VENCIDO', 'CANCELADO'))
);

INSERT INTO tb_boleto (codigo_barra, beneficiario, pagador, valor, data_vencimento, status) VALUES
    ('23793380896012340900901613951001291070001500000',
     'Empresa ABC Ltda', 'João da Silva', 250.00, '2027-12-31', 'PENDENTE'),
    ('34191090008001212904606855851177300000100000000',
     'Empresa XYZ S.A.', 'Maria Souza', 1500.00, '2027-12-31', 'PENDENTE'),
    ('75691122300001500001092310800004510935003795000',
     'Condomínio Central', 'Carlos Ferreira', 450.00, '2027-12-31', 'PENDENTE');
