INSERT IGNORE INTO tb_usuario (
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
