# Guia Completo de Instalação e Execução no Linux (Ubuntu / Debian)
## Projeto Serralheria Garcia 2026

Este tutorial foi elaborado para configurar o projeto a partir do zero em um computador com Linux Ubuntu recém-instalado (máquina limpa), contendo apenas a pasta do projeto `Projeto_Serralheria_2026`.

---

## 1. Estrutura do Projeto

```text
Projeto_Serralheria_2026/
├── banco/
│   ├── ddl_serralheria.sql      # Estrutura do banco (tabelas e chaves)
│   ├── dados_exemplo.sql        # Dados iniciais para teste
│   └── configurar_banco.sh      # Script automatizado de configuração
├── serralheria-api/             # Backend Spring Boot (Java 21+, porta 8080)
└── serralheria-web/             # Frontend Vue 3 + Vuetify + Vite (porta 5173)
```

---

## 2. Passo 1: Instalação dos Programas Necessários

Abra o terminal na máquina Linux limpa e execute:

### 2.1 Atualizar a lista de pacotes do sistema
```bash
sudo apt update && sudo apt upgrade -y
```

### 2.2 Instalar o Java (JDK 21)
O backend foi desenvolvido com suporte a Java 21+.
```bash
sudo apt install -y openjdk-21-jdk
```
> Para conferir se instalou corretamente:
> ```bash
> java -version
> ```

### 2.3 Instalar o MySQL Server
```bash
sudo apt install -y mysql-server
```
> Verifique se o serviço está rodando:
> ```bash
> sudo systemctl status mysql
> ```

### 2.4 Instalar o Node.js e npm (para o Frontend)
```bash
sudo apt install -y nodejs npm
```
> Confirme as versões:
> ```bash
> node -v
> npm -v
> ```

---

## 3. Passo 2: Dar Permissão de Execução aos Scripts

No Linux, arquivos de script precisam de permissão de execução (`+x`):

```bash
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026

# Permissão para o script de banco
chmod +x banco/configurar_banco.sh

# Permissão para o wrapper do Maven no backend
chmod +x serralheria-api/mvnw
```

---

## 4. Passo 3: Configuração do Banco de Dados MySQL

No Ubuntu, o MySQL cria o usuário `root` protegido pelo plugin `auth_socket`. Para o backend conseguir conectar usando usuário `root` e senha `1234`, criamos um script que automatiza tudo.

### 4.1 Executar a configuração automatizada:
```bash
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026/banco
sudo ./configurar_banco.sh
```
*(Digite sua senha do Ubuntu quando solicitado)*.

O script executa automaticamente:
1. Inicia o serviço MySQL.
2. Altera a autenticação do `root` para a senha `1234` (permitindo conexão local e remota).
3. Cria o banco de dados `serralheria_garcia` e todas as tabelas via `ddl_serralheria.sql`.
4. Insere os dados de demonstração via `dados_exemplo.sql`.

### 4.2 Conferindo se as tabelas foram criadas:
```bash
mysql -u root -p1234 -e "SHOW TABLES IN serralheria_garcia;"
```
Você verá as 7 tabelas:
- `Cliente`
- `Fornecedor`
- `Funcionario`
- `Item`
- `Material`
- `Pedido`
- `Usuario`

---

## 5. Passo 4: Executando o Backend (`serralheria-api`)

O backend roda em Spring Boot na porta **8080**.

### 5.1 Opção A: Executar via Terminal com Maven Wrapper (Não precisa ter Maven instalado)
```bash
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026/serralheria-api

# Compilar e empacotar (os testes unitários usam banco H2 em memória, não quebram o build)
./mvnw clean package

# Iniciar o servidor
./mvnw spring-boot:run
```

Ou executar diretamente o `.jar` gerado:
```bash
java -jar target/serralheria-api-0.0.1-SNAPSHOT.jar
```

### 5.2 Opção B: Executar pelo Apache NetBeans
1. Abra o NetBeans.
2. Vá em **File -> Open Project...**
3. Navegue até `Projeto_Serralheria_2026` e selecione a pasta `serralheria-api` (ela terá o ícone do Maven).
4. Clique com o botão direito no projeto e escolha **Clean and Build**.
5. Em seguida, clique em **Run** (ou pressione `F6`).

### 5.3 Usuário Administrador Inicial
Na primeira execução, a aplicação cadastra automaticamente o administrador padrão:
- **Login:** `admin`
- **Senha:** `admin123`

---

## 6. Passo 5: Executando o Frontend (`serralheria-web`)

O frontend é uma SPA desenvolvida em Vue 3 + Vuetify + Vite.

### 6.1 Instalar as dependências do Node:
```bash
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026/serralheria-web
npm install
```

### 6.2 Conferir o arquivo de ambiente:
O arquivo `.env` já aponta para o backend local:
```properties
VITE_API_URL=http://localhost:8080/api
```

### 6.3 Iniciar o frontend:
```bash
npm run dev
```

O terminal exibirá:
```text
  VITE v8.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

Abra o seu navegador e acesse: **`http://localhost:5173`**

---

## 7. Comandos Rápidos ("TL;DR" para máquina nova)

Se quiser configurar tudo de uma vez em um terminal de uma máquina nova:

```bash
# 1. Instalar programas
sudo apt update && sudo apt install -y openjdk-21-jdk mysql-server nodejs npm

# 2. Permissões e banco de dados
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026
chmod +x banco/configurar_banco.sh serralheria-api/mvnw
sudo ./banco/configurar_banco.sh

# 3. Rodar API (Terminal 1)
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026/serralheria-api
./mvnw spring-boot:run

# 4. Rodar Web (Terminal 2)
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026/serralheria-web
npm install
npm run dev
```

---

## 8. Solução de Problemas Comuns (Troubleshooting)

### Erro: `Port 8080 was already in use`
A API já está rodando em segundo plano ou outra aplicação está usando a porta 8080. Para liberar a porta:
```bash
sudo fuser -k 8080/tcp
```
Depois tente rodar `./mvnw spring-boot:run` novamente.

### Erro: `Access denied for user 'root'@'localhost'`
O usuário `root` do MySQL ainda está exigindo socket ou a senha não foi definida. Execute:
```bash
cd ~/Documents/projetos_netbeans/Projeto_Serralheria_2026/banco
sudo ./configurar_banco.sh
```

### Erro: `./mvnw: Permission denied`
Execute:
```bash
chmod +x ~/Documents/projetos_netbeans/Projeto_Serralheria_2026/serralheria-api/mvnw
```
