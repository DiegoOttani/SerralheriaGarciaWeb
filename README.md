# Como rodar o projeto Serralheria Garcia

Guia para subir o sistema no **Linux** ou no **Windows (10 / 11)**. Escolha a
seção do seu sistema operacional:

- [🐧 Linux](#-linux)
- [🪟 Windows](#-windows)

| Parte     | Tecnologia                  | Endereço                     |
|-----------|-----------------------------|------------------------------|
| Banco     | MySQL 8                     | `localhost:3306`             |
| Back-end  | Spring Boot (Java 21+)      | http://localhost:8080/api    |
| Front-end | Vue 3 + Vuetify + Vite      | http://localhost:5173        |

**Login do sistema:** `admin` / `admin123`
**Senha do `root` do MySQL usada pelo back-end:** `1234`

## Estrutura do repositório

```text
SerralheriaGarciaWeb/
├── banco/                  # Scripts do MySQL
│   ├── ddl_serralheria.sql     # Estrutura (tabelas e chaves)
│   ├── dados_exemplo.sql       # Dados de demonstração
│   ├── serralheria_completo.sql # Estrutura + dados num único arquivo (para outro PC)
│   └── configurar_banco.sh     # Configuração automática do banco (somente Linux)
├── serralheria-api/        # Back-end Spring Boot (Maven)
├── serralheria-web/        # Front-end Vue 3 + Vuetify + Vite
└── docs/
    └── TUTORIAL_LINUX.md   # Instalação completa no Linux em máquina nova
```

---

# 🐧 Linux

Guia rápido para subir o sistema no Linux. Para instalação do zero em uma
máquina nova, veja também [`docs/TUTORIAL_LINUX.md`](docs/TUTORIAL_LINUX.md).

## Pré-requisitos (só na primeira vez)

```bash
sudo apt update && sudo apt install -y openjdk-21-jdk mysql-server nodejs npm
```

> Java 21 ou superior serve.

Conferir: `java -version`, `node -v`, `mysql --version`.

## 1. Banco de dados (só na primeira vez)

```bash
cd ~/Documents/projetos_netbeans/SerralheriaGarciaWeb
chmod +x banco/configurar_banco.sh serralheria-api/mvnw
sudo ./banco/configurar_banco.sh
```

O script define a senha do `root` do MySQL como `1234`, cria o banco
`serralheria_garcia`, as tabelas e insere dados de exemplo.

> ⚠️ Rodar o script de novo **recria as tabelas e apaga os dados**. Nas próximas
> vezes basta garantir que o MySQL está ligado:
> ```bash
> sudo systemctl start mysql
> ```

## 2. Back-end (Terminal 1)

```bash
cd ~/Documents/projetos_netbeans/SerralheriaGarciaWeb/serralheria-api
./mvnw spring-boot:run
```

Está pronto quando aparecer:
`Started SerralheriaApiApplication in X seconds`

(A primeira execução demora mais porque o Maven baixa as dependências.)

## 3. Front-end (Terminal 2)

```bash
cd ~/Documents/projetos_netbeans/SerralheriaGarciaWeb/serralheria-web
npm install      # só na primeira vez (ou quando mudar o package.json)
npm run dev
```

Abra **http://localhost:5173** no navegador e entre com `admin` / `admin123`.

O front aponta para a API pelo arquivo `serralheria-web/.env`
(`VITE_API_URL=http://localhost:8080/api`).

## Para parar

Pressione `Ctrl + C` em cada terminal.

## Problemas comuns (Linux)

| Erro | Solução |
|------|---------|
| `Port 8080 was already in use` | A API já está rodando. Libere com `sudo fuser -k 8080/tcp` |
| `Port 5173 is in use` | O front já está rodando. Libere com `sudo fuser -k 5173/tcp` |
| `Access denied for user 'root'@'localhost'` | Rode `sudo ./banco/configurar_banco.sh` (apaga os dados!) |
| `Communications link failure` | MySQL desligado: `sudo systemctl start mysql` |
| `./mvnw: Permission denied` | `chmod +x serralheria-api/mvnw` |
| Login não funciona no navegador | Confira se o back-end está rodando em http://localhost:8080 |

---

# 🪟 Windows

Guia para configurar o projeto do zero em um computador com Windows 10 ou 11.
Todos os comandos abaixo devem ser executados no **PowerShell**
(menu Iniciar → digite `PowerShell`).

> No Windows **não** se usa o `configurar_banco.sh` nem o `chmod +x`: o banco é
> importado direto pelo cliente `mysql` e o back-end roda com `.\mvnw.cmd`.

## 1. Instalar os programas (só na primeira vez)

A forma mais simples é usar o **winget** (já vem no Windows 10/11). Abra o
PowerShell **como Administrador**.

> Se preferir, os programas também podem ser baixados manualmente pelos sites
> oficiais (links em cada item).

### 1.1 Java (JDK 21)

```powershell
winget install --id Microsoft.OpenJDK.21 -e
```
> Download manual: https://learn.microsoft.com/java/openjdk/download

Feche e abra o PowerShell novamente e confira com `java -version`.

> **Dica:** se o comando `java` não for reconhecido, mas você já tiver um JDK
> instalado (por exemplo, o do Android Studio), configure o `JAVA_HOME` — o
> Maven Wrapper usa essa variável:
> ```powershell
> setx JAVA_HOME "C:\Program Files\Android\Android Studio\jbr"
> ```
> Feche e abra o PowerShell depois do `setx`.

### 1.2 MySQL Server 8

```powershell
winget install --id Oracle.MySQL -e
```
> Download manual (MySQL Installer): https://dev.mysql.com/downloads/installer/

Durante a configuração do MySQL (MySQL Installer / MySQL Configurator):
1. Escolha o tipo **Development Computer** (ou **Server only**).
2. Mantenha a porta padrão **3306**.
3. Em **Authentication Method**, mantenha a opção recomendada (*Strong Password Encryption*).
4. Defina a senha do usuário **root** como **`1234`** (é a senha usada pelo back-end).
5. Marque **Configure MySQL Server as a Windows Service** e **Start the MySQL Server at System Startup**.

Verifique se o serviço está rodando (o `Status` deve ser `Running`):
```powershell
Get-Service MySQL*
```
Caso esteja parado:
```powershell
Start-Service MySQL80
```

**Adicionar o cliente `mysql` ao PATH (recomendado)** — o instalador não coloca o
`mysql.exe` no PATH:
```powershell
setx PATH "$env:PATH;C:\Program Files\MySQL\MySQL Server 8.0\bin"
```
Feche e abra o PowerShell e confira com `mysql --version`.

### 1.3 Node.js e npm

```powershell
winget install --id OpenJS.NodeJS.LTS -e
```
> Download manual: https://nodejs.org

Feche e abra o PowerShell e confira com `node -v` e `npm -v`.

### 1.4 Permitir a execução de scripts no PowerShell

O PowerShell bloqueia scripts por padrão, o que pode impedir o `npm` de rodar
(erro *"a execução de scripts foi desabilitada neste sistema"*). Para liberar
apenas para o seu usuário:

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```
Confirme com `S` (ou `Y`) se for solicitado.

## 2. Banco de dados (só na primeira vez)

A senha do `root` já foi definida na instalação do MySQL (passo 1.2), então basta
criar o banco e importar os dados. Abra o PowerShell na pasta do projeto:

```powershell
cd C:\caminho\para\SerralheriaGarciaWeb
```

**Opção A — arquivo único (estrutura + dados de demonstração):**
```powershell
Get-Content banco\serralheria_completo.sql -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4
```

**Opção B — em duas etapas:**
```powershell
Get-Content banco\ddl_serralheria.sql -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4
Get-Content banco\dados_exemplo.sql   -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4
```

**Opção C — MySQL Workbench:** **File → Open SQL Script...**, selecione
`banco\serralheria_completo.sql` e execute com o raio (⚡).

> **Por que `Get-Content ... |` em vez de `mysql < arquivo.sql`?**
> O PowerShell não aceita o operador `<`. Os parâmetros `-Encoding UTF8` e
> `--default-character-set=utf8mb4` garantem que os acentos (ex.: "Aço",
> "Metalúrgica") sejam gravados corretamente.

> ⚠️ Os scripts começam com `DROP DATABASE IF EXISTS`: executá-los novamente
> **apaga todos os dados** e recria o banco do zero.

> Se você não adicionou o MySQL ao PATH, troque `mysql` por
> `& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"`.

Conferindo se as tabelas foram criadas:
```powershell
mysql -u root -p1234 -e "SHOW TABLES IN serralheria_garcia;"
```
Devem aparecer 7 tabelas: `Cliente`, `Fornecedor`, `Funcionario`, `Item`,
`Material`, `Pedido` e `Usuario`.

> A mensagem `mysql: [Warning] Using a password on the command line interface can be insecure.`
> é apenas um aviso e pode ser ignorada.

## 3. Back-end (PowerShell 1)

**Opção A — Maven Wrapper (não precisa ter o Maven instalado):**
```powershell
cd C:\caminho\para\SerralheriaGarciaWeb\serralheria-api
.\mvnw.cmd spring-boot:run
```

Está pronto quando aparecer:
```text
Tomcat started on port 8080 (http) with context path '/'
Started SerralheriaApiApplication in X seconds
```

(A primeira execução demora mais porque o Maven Wrapper baixa o Maven e as
dependências.)

Se preferir gerar e rodar o `.jar`:
```powershell
.\mvnw.cmd clean package
java -jar target\serralheria-api-0.0.1-SNAPSHOT.jar
```

**Opção B — Apache NetBeans:**
1. **File → Open Project...** e selecione a pasta `serralheria-api` (ícone do Maven).
2. Clique com o botão direito no projeto → **Clean and Build**.
3. Clique em **Run** (ou pressione `F6`).

**Testando a API pelo PowerShell (opcional):**
```powershell
# Login: retorna um token
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/login `
  -ContentType "application/json" -Body '{"login":"admin","senha":"admin123"}'
$login

# Listar clientes usando o token
Invoke-RestMethod -Uri http://localhost:8080/api/clientes `
  -Headers @{ Authorization = "Bearer $($login.token)" }
```

## 4. Front-end (PowerShell 2)

Abra **outro** PowerShell (a API precisa continuar rodando no primeiro):

```powershell
cd C:\caminho\para\SerralheriaGarciaWeb\serralheria-web
npm install      # só na primeira vez (ou quando mudar o package.json)
npm run dev
```

Abra **http://localhost:5173** no navegador e entre com `admin` / `admin123`.

O front aponta para a API pelo arquivo `serralheria-web\.env`
(`VITE_API_URL=http://localhost:8080/api`).

## Para parar

Pressione `Ctrl + C` em cada PowerShell.

## Comandos rápidos (máquina nova)

```powershell
# 1. Instalar programas (PowerShell como Administrador;
#    na instalação do MySQL, defina a senha do root como 1234)
winget install --id Microsoft.OpenJDK.21 -e
winget install --id Oracle.MySQL -e
winget install --id OpenJS.NodeJS.LTS -e
setx PATH "$env:PATH;C:\Program Files\MySQL\MySQL Server 8.0\bin"
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
# -> feche e abra o PowerShell

# 2. Banco de dados
cd C:\caminho\para\SerralheriaGarciaWeb
Get-Content banco\serralheria_completo.sql -Raw -Encoding UTF8 | mysql -u root -p1234 --default-character-set=utf8mb4

# 3. Rodar API (PowerShell 1)
cd C:\caminho\para\SerralheriaGarciaWeb\serralheria-api
.\mvnw.cmd spring-boot:run

# 4. Rodar Web (PowerShell 2)
cd C:\caminho\para\SerralheriaGarciaWeb\serralheria-web
npm install
npm run dev
```

## Problemas comuns (Windows)

| Erro | Solução |
|------|---------|
| `Port 8080 was already in use` | Descubra o PID com `netstat -ano \| findstr :8080` e finalize com `taskkill /PID <pid> /F` |
| `Port 5173 is in use` | Mesmo procedimento: `netstat -ano \| findstr :5173` e `taskkill /PID <pid> /F` |
| `Access denied for user 'root'@'localhost'` | A senha do `root` não é `1234`. Altere com `mysql -u root -p -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';"` ou ajuste `spring.datasource.password` em `serralheria-api\src\main\resources\application.properties` |
| `Communications link failure` | MySQL parado: `Start-Service MySQL80` (PowerShell como Administrador) |
| `JAVA_HOME not found` ou `'java' não é reconhecido` | Reinstale o JDK (passo 1.1) ou rode `setx JAVA_HOME "C:\Program Files\Microsoft\jdk-21.x.x-hotspot"` e reabra o PowerShell |
| `'mysql' não é reconhecido` | Adicione o MySQL ao PATH (passo 1.2) ou use `& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"` |
| `npm.ps1 não pode ser carregado ... execução de scripts foi desabilitada` | `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned` |
| Acentos errados (ex.: `AÃ§o` em vez de `Aço`) | Reimporte o banco usando exatamente os comandos do passo 2 (com `-Encoding UTF8` e `--default-character-set=utf8mb4`) |
| Login não funciona no navegador | Confira se o back-end está rodando em http://localhost:8080 |
