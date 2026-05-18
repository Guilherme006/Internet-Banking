CREATE TABLE tb_transacao (
    id           VARCHAR(36)     NOT NULL,
    numero_conta VARCHAR(20)     NOT NULL,
    tipo         VARCHAR(20)     NOT NULL,
    descricao    VARCHAR(160)    NOT NULL,
    valor        DECIMAL(19, 2)  NOT NULL,
    data_hora    DATETIME        NOT NULL,
    saldo_apos   DECIMAL(19, 2)  NOT NULL,
    categoria    VARCHAR(60)     NOT NULL,
    criado_em    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transacao PRIMARY KEY (id),
    CONSTRAINT chk_transacao_tipo CHECK (tipo IN ('CREDITO', 'DEBITO')),
    INDEX idx_transacao_conta_data (numero_conta, data_hora),
    INDEX idx_transacao_conta_tipo (numero_conta, tipo)
);

INSERT INTO tb_transacao (id, numero_conta, tipo, descricao, valor, data_hora, saldo_apos, categoria) VALUES
    ('t-001', '12345-6', 'DEBITO',  'Pagamento Boleto - Empresa ABC',       250.00,  '2026-05-15 10:30:00', 4750.00,  'Boleto'),
    ('t-002', '12345-6', 'CREDITO', 'TED Recebida - Maria Souza',          1200.00,  '2026-05-14 16:45:00', 5000.00,  'Transferencia'),
    ('t-003', '12345-6', 'DEBITO',  'Debito Automatico - Energia Eletrica', 187.50,  '2026-05-13 08:00:00', 3800.00,  'Debito Automatico'),
    ('t-004', '12345-6', 'DEBITO',  'PIX - Carlos Ferreira',                500.00,  '2026-05-12 14:22:00', 3987.50,  'PIX'),
    ('t-005', '12345-6', 'CREDITO', 'Salario',                             8500.00,  '2026-05-10 07:00:00', 4487.50,  'Credito'),
    ('t-006', '12345-6', 'DEBITO',  'Pagamento Boleto - Condominio',        850.00,  '2026-05-09 11:15:00', 3637.50,  'Boleto'),
    ('t-007', '12345-6', 'DEBITO',  'Tarifa Manutencao de Conta',            29.90,  '2026-05-07 00:00:00', 3607.60,  'Tarifa'),
    ('t-008', '12345-6', 'CREDITO', 'Rendimento Poupanca',                   45.30,  '2026-05-05 00:01:00', 3652.90,  'Rendimento'),
    ('t-009', '12345-6', 'DEBITO',  'Compra Debito - Supermercado XYZ',     312.75,  '2026-05-03 19:40:00', 3340.15,  'Compra'),
    ('t-010', '12345-6', 'DEBITO',  'PIX - Restaurante Bom Sabor',           89.00,  '2026-05-02 13:05:00', 3251.15,  'PIX'),
    ('t-011', '12345-6', 'CREDITO', 'Transferencia Recebida',               300.00,  '2026-05-01 09:30:00', 3551.15,  'Transferencia'),
    ('t-012', '12345-6', 'DEBITO',  'Debito Automatico - Internet',         129.90,  '2026-04-30 06:00:00', 3421.25,  'Debito Automatico');
