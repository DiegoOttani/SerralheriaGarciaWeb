# Guia Completo de Instalação e Execução no Windows (10 / 11)
## Projeto Serralheria Garcia 2026

Este tutorial foi elaborado para configurar o projeto a partir do zero em um computador com Windows 10 ou 11, contendo apenas a pasta do projeto `Projeto_Serralheria_2026`.

Todos os comandos abaixo devem ser executados no **PowerShell** (menu Iniciar → digite `PowerShell`).

---

## 1. Estrutura do Projeto

```text
Projeto_Serralheria_2026/
├── banco/
│   ├── ddl_serralheria.sql      # Estrutura do banco (tabelas e chaves)
│   ├── dados_exemplo.sql        # Dados iniciais para teste
│   └── configurar_banco.sh      # Script de configuração (somente Linux)
├── serralheria-api/             # Backend Spring Boot (Java 21+, porta 8080)
└── serralheria-web/             # Frontend Vue 3 + Vuetify + Vite (porta 5173)
```

---

## 2. Passo 1: Instalação dos Programas Necessários

A forma mais simples é usar o **winget** (gerenciador de pacotes que já vem no Windows 10/11). Abra o PowerShell **como Administrador** e execute os comandos abaixo.

> Se preferir, todos os programas também podem ser baixados e instalados manualmente pelos sites oficiais (links em cada item).

### 2.1 Instalar o Java (JDK 21)
O backend foi desenvolvido com suporte a Java 21+.
```powershell
winget install --id Microsoft.OpenJDK.21 -e
```
> Download manual: https://learn.microsoft.com/java/openjdk/download

Feche e abra o PowerShell novamente e confira:
```powershell
java -version
```

> **Dica:** se o comando `java` não for reconhecido, mas você já tiver um JDK instalado (por exemplo, o que vem com o Android Studio em `C:\Program Files\Android\Android Studio\jbr`), basta configurar o `JAVA_HOME` apontando para ele — o Maven Wrapper usa essa variável:
> ```powershell
> setx JAVA_HOME "C:\Program Files\Android\Android Studio\jbr"
> ```
> Feche e abra o PowerShell depois de executar o `setx`.

### 2.2 Instalar o MySQL Server 8
```powershell
winget install --id Oracle.MySQL -e
```
> Download manual (MySQL Installer): https://dev.mysql.com/downloads/installer/

Durante a configuração do MySQL (MySQL Installer / MySQL Configurator):
1. Escolha o tipo **Development Computer** (ou **Server only**).
2. Mantenha a porta padrão **3306**.
3. Em **Authentication Method**, mantenha a opção recomendada (*Strong Password Encryption*).
4. Defina a senha do usuário **root** como **`1234`** (é a senha usada pelo backend).
5. Marque **Configure MySQL Server as a Windows Service** e **Start the MySQL Server at System Startup**.

Verifique se o serviço está rodando:
```powershell
Get-Service MySQL*
```
O `Status` deve aparecer como `Running`. Caso esteja parado:
```powershell
Start-Service MySQL80
```

#### Adicionar o cliente `mysql` ao PATH (recomendado)
O instalador não coloca o `mysql.exe` no PATH. Para poder usar o comando `mysql` em qualquer pasta:
```powershell
setx PATH "$env:PATH;C:\Program Files\MySQL\MySQL Server 8.0\bin"
```
Feche e abra o PowerShell e confira:
```powershell
mysql --version
```

### 2.3 Instalar o Node.js e npm (para o Frontend)
```powershell
winget install --id OpenJS.NodeJS.LTS -e
```
> Download manual: https://nodejs.org

Feche e abra o PowerShell e confirme as versões:
```powershell
node -v
npm -v
```

---

## 3. Passo 2: Permitir a Execução de Scripts no PowerShell

No Windows não é preciso dar permissão de execução (`chmod +x`) aos arquivos como no Linux. Porém, o PowerShell bloqueia scripts por padrão, o que pode impedir o `npm` de rodar (erro *"a execução de scripts foi desabilitada neste sistema"*). Para liberar apenas para o seu usuário:

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```
Confirme com `S` (ou `Y`) se for solicitado.

---

## 4. Passo 3: Configuração do Banco de Dados MySQL

No Windows, a senha do `root` já é definida durante a instalação do MySQL (Passo 2.2), então basta criar o banco e importar os dados.

### 4.1 Criar o banco e as tabelas
Abra o PowerShell na pasta do projeto:
```powershell
cd C:\caminho\para\Projeto_Serralheria_2026
```

Execute o DDL (cria o banco `serralheria_garcia` e todas as tabelas):
```powershell
Get-Content banco\ddl_serralheria.sql -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4
```

### 4.2 Inserir os dados de demonstração
```powershell
Get-Content banco\dados_exemplo.sql -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4
```

> **Por que `Get-Content ... |` em vez de `mysql < arquivo.sql`?**
> O PowerShell não aceita o operador `<` para redirecionar arquivos. Os parâmetros `-Encoding UTF8` e `--default-character-set=utf8mb4` garantem que os acentos (ex.: "Aço", "Metalúrgica") sejam gravados corretamente.

> **Atenção:** o `ddl_serralheria.sql` começa com `DROP DATABASE IF EXISTS`, ou seja, executá-lo novamente **apaga todos os dados** e recria o banco do zero.

> Se você não adicionou o MySQL ao PATH, troque `mysql` por `& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"`.

### 4.3 Conferindo se as tabelas foram criadas:
```powershell
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

> A mensagem `mysql: [Warning] Using a password on the command line interface can be insecure.` é apenas um aviso e pode ser ignorada.

---

## 5. Passo 4: Executando o Backend (`serralheria-api`)

O backend roda em Spring Boot na porta **8080**.

### 5.1 Opção A: Executar via PowerShell com Maven Wrapper (Não precisa ter Maven instalado)
```powershell
cd C:\caminho\para\Projeto_Serralheria_2026\serralheria-api

# Compilar e empacotar (os testes unitários usam banco H2 em memória, não quebram o build)
.\mvnw.cmd clean package

# Iniciar o servidor
.\mvnw.cmd spring-boot:run
```
> Na primeira execução o Maven Wrapper baixa o Maven e todas as dependências, por isso pode demorar alguns minutos.

O servidor está pronto quando aparecer no terminal:
```text
Tomcat started on port 8080 (http) with context path '/'
Started SerralheriaApiApplication in X seconds
```

Ou executar diretamente o `.jar` gerado:
```powershell
java -jar target\serralheria-api-0.0.1-SNAPSHOT.jar
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

### 5.4 Testando a API pelo PowerShell (opcional)
```powershell
# Login: retorna um token
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/login `
  -ContentType "application/json" -Body '{"login":"admin","senha":"admin123"}'
$login

# Listar clientes usando o token
Invoke-RestMethod -Uri http://localhost:8080/api/clientes `
  -Headers @{ Authorization = "Bearer $($login.token)" }
```

---

## 6. Passo 5: Executando o Frontend (`serralheria-web`)

O frontend é uma SPA desenvolvida em Vue 3 + Vuetify + Vite.

### 6.1 Instalar as dependências do Node:
Abra **outro** PowerShell (a API precisa continuar rodando no primeiro):
```powershell
cd C:\caminho\para\Projeto_Serralheria_2026\serralheria-web
npm install
```

### 6.2 Conferir o arquivo de ambiente:
O arquivo `.env` já aponta para o backend local:
```properties
VITE_API_URL=http://localhost:8080/api
```

### 6.3 Iniciar o frontend:
```powershell
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

Se quiser configurar tudo de uma vez (PowerShell como Administrador para o passo 1):

```powershell
# 1. Instalar programas (na instalação do MySQL, defina a senha do root como 1234)
winget install --id Microsoft.OpenJDK.21 -e
winget install --id Oracle.MySQL -e
winget install --id OpenJS.NodeJS.LTS -e
setx PATH "$env:PATH;C:\Program Files\MySQL\MySQL Server 8.0\bin"
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
# -> feche e abra o PowerShell

# 2. Banco de dados
cd C:\caminho\para\Projeto_Serralheria_2026
Get-Content banco\ddl_serralheria.sql -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4
Get-Content banco\dados_exemplo.sql   -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4

# 3. Rodar API (PowerShell 1)
cd C:\caminho\para\Projeto_Serralheria_2026\serralheria-api
.\mvnw.cmd spring-boot:run

# 4. Rodar Web (PowerShell 2)
cd C:\caminho\para\Projeto_Serralheria_2026\serralheria-web
npm install
npm run dev
```

---

## 8. Solução de Problemas Comuns (Troubleshooting)

### Erro: `Port 8080 was already in use`
A API já está rodando em segundo plano ou outra aplicação está usando a porta 8080. Descubra o processo (PID, última coluna) e finalize-o:
```powershell
netstat -ano | findstr :8080
taskkill /PID <numero_do_pid> /F
```
Depois tente rodar `.\mvnw.cmd spring-boot:run` novamente.

### Erro: `Access denied for user 'root'@'localhost'`
A senha do `root` do MySQL não é `1234`. Altere a senha (será pedida a senha atual):
```powershell
mysql -u root -p -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';"
```
Ou, como alternativa, ajuste a senha no backend em `serralheria-api\src\main\resources\application.properties`:
```properties
spring.datasource.password=SUA_SENHA
```

### Erro: `Communications link failure` ao iniciar a API
O serviço do MySQL não está rodando. Inicie-o (PowerShell como Administrador):
```powershell
Start-Service MySQL80
```

### Erro: `JAVA_HOME not found in your environment` ou `'java' não é reconhecido`
O Java não foi encontrado. Reinstale o JDK (Passo 2.1) ou configure o `JAVA_HOME` apontando para a pasta do JDK, e depois feche e abra o PowerShell:
```powershell
setx JAVA_HOME "C:\Program Files\Microsoft\jdk-21.x.x-hotspot"
```

### Erro: `'mysql' não é reconhecido como nome de cmdlet`
O MySQL não está no PATH. Veja o item *"Adicionar o cliente mysql ao PATH"* no Passo 2.2, ou use o caminho completo:
```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p1234
```

### Erro: `npm : O arquivo ... npm.ps1 não pode ser carregado porque a execução de scripts foi desabilitada`
Libere a execução de scripts para o seu usuário (Passo 2):
```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

### Acentos aparecendo errados (ex.: `AÃ§o` em vez de `Aço`)
Os scripts SQL foram importados sem UTF-8. Reimporte usando exatamente os comandos do Passo 4 (com `-Encoding UTF8` e `--default-character-set=utf8mb4`).
