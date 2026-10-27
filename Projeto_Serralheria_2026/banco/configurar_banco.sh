#!/bin/bash
# Script de configuração do banco MySQL para o projeto Serralheria Garcia no Linux Ubuntu

set -e

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do
  DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
done
DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"

echo "=========================================================="
echo "  Configuração do Banco de Dados - Serralheria Garcia"
echo "=========================================================="

# Se não estiver rodando como root, eleva com sudo
if [ "$EUID" -ne 0 ]; then
    echo ">> Solicitando permissão de administrador para configurar o MySQL..."
    exec sudo "$0" "$@"
fi

# 1. Garantir que o serviço MySQL está ativo
echo "[1/4] Verificando serviço MySQL..."
systemctl start mysql || true
echo "MySQL está ativo."

# 2. Configurar a senha do usuário root para '1234' e permitir conexões com senha
echo "[2/4] Configurando autenticação por senha '1234' para o usuário 'root'..."
SQL="ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY '1234'; CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED WITH caching_sha2_password BY '1234'; CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED WITH caching_sha2_password BY '1234'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION; GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION; GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; FLUSH PRIVILEGES;"

mysql -e "$SQL" 2>/dev/null || mysql -u root -p1234 -e "$SQL"
echo "Senha do root configurada com sucesso."

# 3. Importar a estrutura (DDL)
echo "[3/4] Criando banco 'serralheria_garcia' e tabelas (ddl_serralheria.sql)..."
mysql -u root -p1234 < "$DIR/ddl_serralheria.sql"
echo "Tabelas criadas com sucesso."

# 4. Importar dados de demonstração
echo "[4/4] Inserindo dados iniciais de demonstração (dados_exemplo.sql)..."
mysql -u root -p1234 < "$DIR/dados_exemplo.sql"
echo "Dados de exemplo inseridos com sucesso."

echo "=========================================================="
echo "  BANCO CONFIGURADO COM SUCESSO!"
echo "  - Banco: serralheria_garcia"
echo "  - Usuário: root | Senha: 1234"
echo "  - Tabelas criadas: Cliente, Fornecedor, Funcionario, Item, Material, Pedido, Usuario"
echo "=========================================================="
