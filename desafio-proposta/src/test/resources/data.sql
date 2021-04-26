INSERT INTO PROPOSTA (documento, email, endereco, nome, salario, status) VALUES ('55078573003', 'mary.jane@gmail.com', 'Queens', 'Mary Jane', '1950.00', 'ELEGIVEL');
INSERT INTO PROPOSTA (documento, email, endereco, nome, salario, status) VALUES ('39262458088', 'parker.aranha@gmail.com', 'Queens', 'Peter Parker', '1950.00', 'INELEGIVEL');
INSERT INTO PROPOSTA (documento, email, endereco, nome, salario, status) VALUES ('84785596040', 'may.parker@gmail.com', 'Queens', 'Tia May', '1950.00', 'ELEGIVEL');

INSERT INTO CARTAO (contas_id_cartao, emitido_em, status, proposta_id) VALUES ('2545-1400-4183-6148', '2021-04-23 23:18:25.626396', 'ATIVO', '1');
INSERT INTO CARTAO (contas_id_cartao, emitido_em, status, proposta_id) VALUES ('8866-2159-7683-4969', '2021-04-23 23:05:51.691743', 'BLOQUEADO', '3');

INSERT INTO BLOQUEIO (ip_cliente, user_agent_cliente, cartao_id, bloqueado_em, status_cartao_no_legado) VALUES ('0:0:0:0:0:0:0:1', 'PostmanRuntime/7.26.10', '2', '2021-04-23 20:22:37.789672', 'BLOQUEADO');
