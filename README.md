# Como rodar o projeto Serralheria Garcia

Guia rápido para subir o sistema nesta máquina (Linux). Para instalação do zero
em uma máquina nova, veja também [`docs/TUTORIAL_LINUX.md`](docs/TUTORIAL_LINUX.md).

| Parte     | Tecnologia                  | Endereço                     |
|-----------|-----------------------------|------------------------------|
| Banco     | MySQL 8                     | `localhost:3306`             |
| Back-end  | Spring Boot (Java 21+)      | http://localhost:8080/api    |
| Front-end | Vue 3 + Vuetify + Vite      | http://localhost:5173        |

**Login do sistema:** `admin` / `admin123`

## Estrutura do repositório

```text
SerralheriaGarciaWeb/
├── banco/                  # Scripts do MySQL
│   ├── ddl_serralheria.sql     # Estrutura (tabelas e chaves)
│   ├── dados_exemplo.sql       # Dados de demonstração
│   ├── serralheria_completo.sql # Estrutura + dados num único arquivo (para outro PC)
│   └── configurar_banco.sh     # Configuração automática do banco
├── serralheria-api/        # Back-end Spring Boot (Maven)
├── serralheria-web/        # Front-end Vue 3 + Vuetify + Vite
└── docs/
    └── TUTORIAL_LINUX.md   # Instalação completa em máquina nova
```

---

## Pré-requisitos (só na primeira vez)

```bash
sudo apt update && sudo apt install -y openjdk-21-jdk mysql-server nodejs npm
```

> Java 21 ou superior serve (nesta máquina roda com Java 25).

Conferir: `java -version`, `node -v`, `mysql --version`.

---

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

---

## 2. Back-end (Terminal 1)

```bash
cd ~/Documents/projetos_netbeans/SerralheriaGarciaWeb/serralheria-api
./mvnw spring-boot:run
```

Está pronto quando aparecer:
`Started SerralheriaApiApplication in X seconds`

(A primeira execução demora mais porque o Maven baixa as dependências.)

---

## 3. Front-end (Terminal 2)

```bash
cd ~/Documents/projetos_netbeans/SerralheriaGarciaWeb/serralheria-web
npm install      # só na primeira vez (ou quando mudar o package.json)
npm run dev
```

Abra **http://localhost:5173** no navegador e entre com `admin` / `admin123`.

O front aponta para a API pelo arquivo `serralheria-web/.env`
(`VITE_API_URL=http://localhost:8080/api`).

---

## Para parar

Pressione `Ctrl + C` em cada terminal.

---

## Problemas comuns

| Erro | Solução |
|------|---------|
| `Port 8080 was already in use` | A API já está rodando. Libere com `sudo fuser -k 8080/tcp` |
| `Port 5173 is in use` | O front já está rodando. Libere com `sudo fuser -k 5173/tcp` |
| `Access denied for user 'root'@'localhost'` | Rode `sudo ./banco/configurar_banco.sh` (apaga os dados!) |
| `Communications link failure` | MySQL desligado: `sudo systemctl start mysql` |
| `./mvnw: Permission denied` | `chmod +x serralheria-api/mvnw` |
| Login não funciona no navegador | Confira se o back-end está rodando em http://localhost:8080 |
