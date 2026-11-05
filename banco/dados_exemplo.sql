-- =====================================================================
-- Serralheria Garcia - Dados de exemplo para demonstração (opcional)
-- Executar APÓS o ddl_serralheria.sql.
-- O usuário administrador (admin / admin123) é criado automaticamente pelo
-- backend na primeira execução.
-- =====================================================================

USE serralheria_garcia;

INSERT INTO Fornecedor (Nome, Endereco, Telefone, CNPJ, Email, Data_cadastro) VALUES
('Aço Forte Distribuidora', 'Av. Independência, 1500 - Piracicaba/SP', '(19) 3422-1100', '12.345.678/0001-90', 'vendas@acoforte.com.br', '2026-08-01'),
('Metalúrgica Paulista', 'Rua do Porto, 320 - Piracicaba/SP', '(19) 3433-2200', '23.456.789/0001-01', 'contato@metalpaulista.com.br', '2026-08-10'),
('Tintas & Cia', 'Rua Governador, 88 - Rio Claro/SP', '(19) 3524-3300', '34.567.890/0001-12', 'pedidos@tintasecia.com.br', '2026-09-05');

INSERT INTO Funcionario (Nome, Telefone, CPF, Salario, DataCadastro) VALUES
('Carlos Garcia', '(19) 99811-2233', '111.222.333-44', 4500.00, '2026-01-10'),
('João Pereira', '(19) 99722-3344', '222.333.444-55', 2800.00, '2026-03-15'),
('Marcos Oliveira', '(19) 99633-4455', '333.444.555-66', 2600.00, '2026-06-01');

INSERT INTO Cliente (Nome, Telefone, CPF, Endereco, Email) VALUES
('Ana Souza', '(19) 98111-2222', '444.555.666-77', 'Rua das Flores, 45 - Piracicaba/SP', 'ana.souza@email.com'),
('Construtora Horizonte', '(19) 3411-5566', '45.678.901/0001-23', 'Av. Carlos Botelho, 900 - Piracicaba/SP', 'obras@horizonte.com.br'),
('Pedro Lima', '(19) 98222-3333', '555.666.777-88', 'Rua XV de Novembro, 210 - Piracicaba/SP', 'pedro.lima@email.com');

INSERT INTO Material (Id_fornecedor, Nome, Tipo, Quantidade, Marca, Custo_unidade, Descritivo) VALUES
(1, 'Tubo quadrado 30x30', 'Tubo de aço', 120, 'Gerdau', 85.00, 'Barra de 6 m, chapa 18'),
(1, 'Barra chata 1 1/4"', 'Barra de aço', 80, 'Gerdau', 42.50, 'Barra de 6 m'),
(2, 'Chapa galvanizada 1,5 mm', 'Chapa', 30, 'Usiminas', 310.00, 'Chapa 1,20 x 3,00 m'),
(2, 'Dobradiça reforçada', 'Ferragem', 8, 'Metalpar', 18.90, 'Dobradiça para portão'),
(3, 'Tinta esmalte preto', 'Pintura', 25, 'Suvinil', 95.00, 'Galão 3,6 L');

INSERT INTO Pedido (Id_Cliente, Id_Funcionario, DataSolicitacao, ValorTotal, EstadoPagamento, Descritivo) VALUES
(1, 1, '2026-10-01', 3200.00, 'Pago', 'Portão basculante para garagem'),
(2, 2, '2026-10-08', 4850.00, 'Pendente', 'Grades para janelas do bloco A'),
(3, 3, '2026-10-12', 900.00, 'Atrasado', 'Corrimão de escada');

-- Valor do pedido = soma de (Quantidade x Valor_unidade) dos itens
INSERT INTO Item (Id_pedido, Id_material, Nome, Quantidade, Valor_unidade, Descritivo, Quantidade_material) VALUES
(1, 1, 'Portão basculante 3x2,5 m', 1, 2800.00, 'Estrutura em tubo 30x30', 10),
(1, 5, 'Pintura do portão', 1, 400.00, 'Duas demãos de esmalte preto', 2),
(2, 2, 'Grade de janela 1,2x1,0 m', 10, 435.00, 'Grade em barra chata', 3),
(2, 5, 'Pintura das grades', 1, 500.00, 'Esmalte preto', 3),
(3, 1, 'Corrimão 4 m', 1, 900.00, 'Corrimão em tubo com fixação', 2);
