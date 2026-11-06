-- =====================================================================
-- Serralheria Garcia - Banco COMPLETO (estrutura + dados de demonstração)
--
-- Arquivo único para montar o banco em outro computador:
--   mysql -u root -p < serralheria_completo.sql
-- (ou abrir e executar no MySQL Workbench)
--
-- ATENÇÃO: apaga e recria o banco serralheria_garcia.
--
-- Conteúdo: 7 fornecedores, 4 funcionários, 10 clientes, 5 usuários,
--           20 materiais, 12 pedidos e 25 itens.
--
-- Usuários (login / senha):
--   admin   / admin123    -> ADMIN
--   carlos  / carlos123   -> ADMIN       (Carlos Garcia)
--   joao    / joao123     -> FUNCIONARIO (João Pereira)
--   marcos  / marcos123   -> FUNCIONARIO (Marcos Oliveira)
--   juliana / juliana123  -> FUNCIONARIO (Juliana Martins)
-- =====================================================================

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS serralheria_garcia;
CREATE DATABASE serralheria_garcia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE serralheria_garcia;

-- TABELAS SEM DEPENDÊNCIA

CREATE TABLE Fornecedor (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Endereco VARCHAR(100) NOT NULL,
    Telefone VARCHAR(20) NOT NULL,
    CNPJ VARCHAR(20) NOT NULL UNIQUE,
    Email VARCHAR(100) NOT NULL,
    Data_cadastro DATE NOT NULL
);

CREATE TABLE Funcionario (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Telefone VARCHAR(20),
    CPF VARCHAR(20) NOT NULL UNIQUE,
    Salario DECIMAL(10,2),
    DataCadastro DATE NOT NULL
);

CREATE TABLE Cliente (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Telefone VARCHAR(20) NOT NULL,
    CPF VARCHAR(20) NOT NULL UNIQUE,   -- CPF (pessoa física) ou CNPJ (pessoa jurídica)
    Endereco VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL
);

-- TABELAS COM DEPENDÊNCIA

-- Usuário do sistema; pode estar vinculado a um funcionário (Funcionario 1:N Usuario)
CREATE TABLE Usuario (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Id_funcionario INT NULL,
    Nome VARCHAR(100) NOT NULL,
    Login VARCHAR(50) NOT NULL UNIQUE,
    Senha VARCHAR(100) NOT NULL,        -- hash BCrypt
    Perfil VARCHAR(20) NOT NULL,        -- ADMIN ou FUNCIONARIO
    FOREIGN KEY (Id_funcionario) REFERENCES Funcionario(Id)
);

CREATE TABLE Material (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Id_fornecedor INT NOT NULL,
    Nome VARCHAR(100) NOT NULL,
    Tipo VARCHAR(100) NOT NULL,
    Quantidade INT NOT NULL DEFAULT 0,  -- quantidade em estoque
    Marca VARCHAR(100) NOT NULL,
    Custo_unidade DECIMAL(10,2) NOT NULL,
    Descritivo VARCHAR(500) NOT NULL,
    FOREIGN KEY (Id_fornecedor) REFERENCES Fornecedor(Id)
);

CREATE TABLE Pedido (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Id_Cliente INT NOT NULL,
    Id_Funcionario INT NOT NULL,
    DataSolicitacao DATE NOT NULL,
    ValorTotal DECIMAL(10,2) NOT NULL DEFAULT 0,  -- soma de (Quantidade x Valor_unidade) dos itens
    EstadoPagamento VARCHAR(20) NOT NULL,         -- Pendente, Pago ou Atrasado
    Descritivo VARCHAR(255),
    FOREIGN KEY (Id_Funcionario) REFERENCES Funcionario(Id),
    FOREIGN KEY (Id_Cliente) REFERENCES Cliente(Id)
);

CREATE TABLE Item (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Id_pedido INT NOT NULL,
    Id_material INT NOT NULL,
    Nome VARCHAR(100) NOT NULL,
    Quantidade INT NOT NULL,
    Valor_unidade DECIMAL(10,2) NOT NULL,
    Descritivo VARCHAR(500) NOT NULL,
    Quantidade_material INT NOT NULL,   -- quantidade do material consumida (baixa no estoque)
    FOREIGN KEY (Id_pedido) REFERENCES Pedido(Id),
    FOREIGN KEY (Id_material) REFERENCES Material(Id)
);

-- =====================================================================
-- DADOS DE DEMONSTRAÇÃO
-- =====================================================================

INSERT INTO Fornecedor (Nome, Endereco, Telefone, CNPJ, Email, Data_cadastro) VALUES
('Aço Forte Distribuidora', 'Av. Independência, 1500 - Piracicaba/SP', '(19) 3422-1100', '12.345.678/0001-90', 'vendas@acoforte.com.br', '2026-08-01'),
('Metalúrgica Paulista', 'Rua do Porto, 320 - Piracicaba/SP', '(19) 3433-2200', '23.456.789/0001-01', 'contato@metalpaulista.com.br', '2026-08-10'),
('Tintas & Cia', 'Rua Governador, 88 - Rio Claro/SP', '(19) 3524-3300', '34.567.890/0001-12', 'pedidos@tintasecia.com.br', '2026-09-05');

INSERT INTO Funcionario (Nome, Telefone, CPF, Salario, DataCadastro) VALUES
('Carlos Garcia', '(19) 99811-2233', '111.222.333-44', 4500.00, '2026-01-10'),
('João Pereira', '(19) 99722-3344', '222.333.444-55', 2800.00, '2026-03-15'),
('Marcos Oliveira', '(19) 99633-4455', '333.444.555-66', 2600.00, '2026-06-01'),
('Juliana Martins', '(19) 99544-5566', '666.777.888-99', 2400.00, '2026-07-20');

-- Senhas armazenadas como hash BCrypt (mesmo formato gerado pelo backend)
INSERT INTO Usuario (Id_funcionario, Nome, Login, Senha, Perfil) VALUES
(NULL, 'Administrador', 'admin', '$2a$10$BCllKBtOYvv9Ev/Xo2gZ8OrjknRJsYueCwNncaGXjG4CuslOfgjOW', 'ADMIN'),
(1, 'Carlos Garcia', 'carlos', '$2a$10$DKgfUz6OvbzqYPeytCh7veJbV7SWloIwr5DycY.Ct09DG9Py2MHxq', 'ADMIN'),
(2, 'João Pereira', 'joao', '$2a$10$3CKVWr4yMdtKX67dCgXBY.2sqG3FNTeoJ2mzyJsYEbsllS83SbFse', 'FUNCIONARIO'),
(3, 'Marcos Oliveira', 'marcos', '$2a$10$n/vdrZV2e/.W3v5FhvAbg.5d4ymTdDlczy7Jz2DdfrjkbQshp6MMO', 'FUNCIONARIO'),
(4, 'Juliana Martins', 'juliana', '$2a$10$Z5FiG0h4mOhQE0z6nZWPkuJa6gacEEwytMMXz0dJoUigTJEy1ppRu', 'FUNCIONARIO');

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

-- ---------------------------------------------------------------------
-- Clientes e pedidos adicionais para a apresentação
-- ---------------------------------------------------------------------
INSERT INTO Cliente (Id, Nome, Telefone, CPF, Endereco, Email) VALUES
(4, 'Mariana Costa', '(19) 98333-4444', '666.111.222-33', 'Rua Moraes Barros, 1020 - Piracicaba/SP', 'mariana.costa@email.com'),
(5, 'Condomínio Residencial Ipês', '(19) 3422-7788', '56.789.012/0001-34', 'Av. Rio das Pedras, 2500 - Piracicaba/SP', 'sindico@residencialipes.com.br'),
(6, 'Roberto Almeida', '(19) 98444-5555', '777.222.333-44', 'Rua Alferes José Caetano, 380 - Piracicaba/SP', 'roberto.almeida@email.com'),
(7, 'Padaria Pão Dourado', '(19) 3433-9900', '67.890.123/0001-45', 'Av. Independência, 3100 - Piracicaba/SP', 'contato@paodourado.com.br'),
(8, 'Fernanda Ribeiro', '(19) 98555-6666', '888.333.444-55', 'Rua Santa Cruz, 77 - Rio Claro/SP', 'fernanda.ribeiro@email.com'),
(9, 'Escola Estadual Monte Alegre', '(19) 3421-4455', '78.901.234/0001-56', 'Rua São João, 1500 - Piracicaba/SP', 'secretaria@eemontealegre.sp.gov.br'),
(10, 'Lucas Martins', '(19) 98666-7777', '999.444.555-66', 'Rua Benjamin Constant, 640 - Piracicaba/SP', 'lucas.martins@email.com');

INSERT INTO Pedido (Id, Id_Cliente, Id_Funcionario, DataSolicitacao, ValorTotal, EstadoPagamento, Descritivo) VALUES
(4, 4, 2, '2026-08-14', 2105.00, 'Pago', 'Portão social com fechadura'),
(5, 5, 1, '2026-08-22', 8800.00, 'Pago', 'Guarda-corpo das sacadas - torre 1'),
(6, 6, 3, '2026-09-03', 6320.00, 'Pago', 'Cobertura metálica da garagem'),
(7, 7, 2, '2026-09-18', 2680.00, 'Atrasado', 'Grade de proteção da vitrine'),
(8, 8, 3, '2026-09-29', 5740.00, 'Pago', 'Escada caracol para mezanino'),
(9, 9, 1, '2026-10-15', 10540.00, 'Pendente', 'Portões e grades da quadra poliesportiva'),
(10, 2, 2, '2026-10-22', 4850.00, 'Pendente', 'Grades para janelas do bloco B'),
(11, 10, 3, '2026-10-30', 1380.00, 'Atrasado', 'Reforma de portão basculante'),
(12, 1, 1, '2026-11-04', 1570.00, 'Pendente', 'Corrimão da área externa');

INSERT INTO Item (Id_pedido, Id_material, Nome, Quantidade, Valor_unidade, Descritivo, Quantidade_material) VALUES
(4, 1, 'Portão social 1,0x2,1 m', 1, 1650.00, 'Estrutura em tubo 30x30 com chapa', 4),
(4, 4, 'Instalação de dobradiças', 3, 35.00, 'Dobradiça reforçada', 3),
(4, 5, 'Pintura do portão', 1, 350.00, 'Esmalte preto, duas demãos', 1),
(5, 1, 'Guarda-corpo 3 m', 8, 980.00, 'Tubo 30x30 com montantes', 16),
(5, 5, 'Pintura dos guarda-corpos', 8, 120.00, 'Esmalte preto', 4),
(6, 1, 'Estrutura da cobertura 5x3 m', 1, 3800.00, 'Treliças em tubo 30x30', 18),
(6, 3, 'Fechamento em chapa galvanizada', 6, 420.00, 'Chapa 1,5 mm', 6),
(7, 2, 'Grade de enrolar 2,5x2,2 m', 1, 2400.00, 'Barra chata 1 1/4"', 8),
(7, 5, 'Pintura da grade', 1, 280.00, 'Esmalte preto', 1),
(8, 1, 'Escada caracol 2,8 m', 1, 4600.00, 'Estrutura em tubo com degraus em chapa', 12),
(8, 3, 'Degraus em chapa xadrez', 12, 95.00, 'Chapa 1,5 mm', 3),
(9, 1, 'Portão de acesso 3x2 m', 2, 2900.00, 'Tubo 30x30 com tela', 14),
(9, 2, 'Grade de proteção 2x2 m', 6, 640.00, 'Barra chata 1 1/4"', 12),
(9, 5, 'Pintura geral', 1, 900.00, 'Esmalte preto', 4),
(10, 2, 'Grade de janela 1,2x1,0 m', 10, 435.00, 'Grade em barra chata', 10),
(10, 5, 'Pintura das grades', 1, 500.00, 'Esmalte preto', 3),
(11, 1, 'Troca de quadro do portão', 1, 1200.00, 'Tubo 30x30', 4),
(11, 4, 'Troca de dobradiças', 4, 45.00, 'Dobradiça reforçada', 4),
(12, 1, 'Corrimão 6 m', 1, 1350.00, 'Tubo com fixação no piso', 3),
(12, 5, 'Pintura do corrimão', 1, 220.00, 'Esmalte preto', 1);

-- ---------------------------------------------------------------------
-- Fornecedores e materiais adicionais para a apresentação
-- ---------------------------------------------------------------------
INSERT INTO Fornecedor (Id, Nome, Endereco, Telefone, CNPJ, Email, Data_cadastro) VALUES
(4, 'Ferragens Santa Rita', 'Rua Prudente de Moraes, 1240 - Piracicaba/SP', '(19) 3434-5566', '45.123.987/0001-55', 'vendas@ferragenssantarita.com.br', '2026-09-12'),
(5, 'Solda Mais Equipamentos', 'Av. Saudade, 455 - Piracicaba/SP', '(19) 3417-8899', '56.234.098/0001-66', 'comercial@soldamais.com.br', '2026-09-20'),
(6, 'Vidraçaria Cristal', 'Rua Voluntários de Piracicaba, 610 - Piracicaba/SP', '(19) 3422-6677', '67.345.109/0001-77', 'orcamento@vidracariacristal.com.br', '2026-10-02'),
(7, 'Perfis Campinas Metais', 'Rod. Anhanguera, km 98 - Campinas/SP', '(19) 3266-4400', '78.456.210/0001-88', 'atendimento@perfiscampinas.com.br', '2026-10-15');

INSERT INTO Material (Id, Id_fornecedor, Nome, Tipo, Quantidade, Marca, Custo_unidade, Descritivo) VALUES
(6, 1, 'Tubo redondo 1 1/2"', 'Tubo de aço', 60, 'Gerdau', 92.00, 'Barra de 6 m, parede 1,5 mm'),
(7, 7, 'Cantoneira 1" x 1/8"', 'Perfil de aço', 90, 'ArcelorMittal', 48.00, 'Barra de 6 m'),
(8, 7, 'Perfil U enrijecido 75x40', 'Perfil de aço', 40, 'ArcelorMittal', 135.00, 'Barra de 6 m, chapa 2 mm, para coberturas'),
(9, 2, 'Chapa xadrez 3 mm', 'Chapa', 12, 'Usiminas', 480.00, 'Chapa 1,00 x 2,00 m antiderrapante'),
(10, 2, 'Telha galvalume trapezoidal', 'Cobertura', 50, 'Eternit', 165.00, 'Telha 0,43 mm, 1,00 x 3,00 m'),
(11, 4, 'Fechadura para portão', 'Ferragem', 15, 'Pado', 89.90, 'Fechadura de sobrepor com 3 chaves'),
(12, 4, 'Roldana para portão de correr', 'Ferragem', 5, 'Rometal', 38.50, 'Roldana canal V 3"'),
(13, 4, 'Parafuso sextavado 3/8" x 2"', 'Fixação', 400, 'Ciser', 1.20, 'Zincado, com porca e arruela'),
(14, 4, 'Bucha de ancoragem 3/8"', 'Fixação', 250, 'Fischer', 2.80, 'Parabolt para fixação em concreto'),
(15, 5, 'Eletrodo 6013 2,5 mm', 'Solda', 3, 'ESAB', 62.00, 'Pacote de 1 kg'),
(16, 5, 'Disco de corte 7"', 'Consumível', 45, 'Norton', 9.50, 'Disco para aço 180 x 1,6 mm'),
(17, 5, 'Disco flap 4 1/2"', 'Consumível', 30, 'Norton', 14.90, 'Grão 80, para acabamento de solda'),
(18, 3, 'Fundo anticorrosivo cinza', 'Pintura', 18, 'Coral', 78.00, 'Galão 3,6 L, zarcão'),
(19, 3, 'Thinner 2750', 'Pintura', 20, 'Anjo', 42.00, 'Lata 5 L'),
(20, 6, 'Vidro temperado 8 mm', 'Vidro', 10, 'Blindex', 260.00, 'Painel 1,00 x 1,00 m para guarda-corpo');
