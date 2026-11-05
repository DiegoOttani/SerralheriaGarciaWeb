-- =====================================================================
-- Serralheria Garcia - DDL do banco de dados (MySQL 8 / MariaDB)
-- Versão web (Arquitetura de Software), evoluída a partir do banco de BDI/POO
--
-- Melhorias em relação à versão anterior:
--   * Nova tabela Usuario (controle de acesso com login e senha)
--   * Fornecedor: coluna CPF renomeada para CNPJ
--   * Material.Quantidade e Item.Quantidade passam a ser numéricas (INT)
--   * Pedido.ValorTotal passa a ser calculado pela soma dos itens
--   * Unicidade de CPF/CNPJ e login
-- =====================================================================

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
