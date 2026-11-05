# Serralheria Garcia — Frontend (Vue.js)

Interface web do sistema da Serralheria Garcia, feita com Vue 3 + Vuetify, consumindo a API REST
do projeto `serralheria-api` (Spring Boot).

## Como executar

1. Inicie o backend (`serralheria-api`) no NetBeans — ele deve estar rodando em `http://localhost:8080`.
2. Nesta pasta, instale as dependências e inicie o servidor de desenvolvimento:

   ```
   npm install
   npm run dev
   ```

3. Acesse `http://localhost:5173` e entre com `admin` / `admin123`.

Para acessar de outra máquina da rede, altere `VITE_API_URL` no arquivo `.env` para o IP do
computador que roda o backend.

## Estrutura

```
src/
  main.js          configuração inicial (Vue, rotas, Vuetify)
  App.vue          layout: menu lateral, barra superior e avisos
  router/index.js  rotas e controle de acesso (login / perfil admin)
  views/           telas (Login, Início, Clientes, Pedidos...)
  components/      componentes reutilizados pelas telas
  services/        acesso à API (axios), sessão, formatação e máscaras
  plugins/         tema do Vuetify
  assets/css/      estilos globais
```
