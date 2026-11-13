USE ecolog_alimentos;

INSERT INTO tb_ponto_coleta (razao_social, cnpj, endereco, telefone, email) VALUES
    ('Supermercado Bom Preço Ltda', '11.222.333/0001-81', 'Av. Brasil, 1500 - Centro, Maringá/PR', '(44) 3025-1000', 'doacoes@bompreco.com.br'),
    ('Cooperativa Agro Vale Verde', '45.723.174/0001-10', 'Rod. PR-317, km 12 - Zona Rural, Maringá/PR', '(44) 3262-4400', 'contato@valeverde.coop.br');

INSERT INTO tb_instituicao_receptora (razao_social, cnpj, endereco, capacidade_refrigerada_kg, telefone) VALUES
    ('Associação Mesa Solidária', '19.131.243/0001-97', 'Rua das Flores, 320 - Jd. Alvorada, Maringá/PR', 350.00, '(44) 3031-2020'),
    ('Lar Esperança Infantil', '04.252.011/0001-10', 'Rua Pioneiro João Batista, 88 - Zona 7, Maringá/PR', 120.00, '(44) 3224-7788');

INSERT INTO tb_ordem_transporte (data_agendada, status, fk_id_coleta, fk_id_receptora) VALUES
    (DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PENDENTE', 1, 1),
    (DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PENDENTE', 2, 2);

INSERT INTO tb_lote_alimento
    (descricao, categoria, quantidade_kg, data_validade, tipo_lote, temperatura_minima, temperatura_maxima, tipo_embalagem, fk_id_ordem)
VALUES
    ('Iogurte natural 170g', 'Laticínios', 45.00, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'PERECIVEL', 2.00, 6.00, NULL, 1),
    ('Arroz tipo 1 5kg', 'Grãos', 200.00, DATE_ADD(CURDATE(), INTERVAL 90 DAY), 'NAO_PERECIVEL', NULL, NULL, 'Saco plástico', 1),
    ('Alface crespa', 'Hortifrúti', 30.00, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'PERECIVEL', 4.00, 10.00, NULL, 2),
    ('Feijão carioca 1kg', 'Grãos', 120.00, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'NAO_PERECIVEL', NULL, NULL, 'Saco plástico', 2);
