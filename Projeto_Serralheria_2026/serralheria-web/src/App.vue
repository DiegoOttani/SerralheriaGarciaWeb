<template>
  <v-app>
    <!-- Tela de login: sem menu -->
    <router-view v-if="$route.meta.publica" />

    <!-- Demais telas: menu lateral + barra superior -->
    <template v-else>
      <v-navigation-drawer v-model="menuAberto" color="grafite" width="260">
        <div class="d-flex align-center ga-3 px-4 py-5">
          <LogoSerralheria :tamanho="42" />
          <div>
            <div class="text-subtitle-1 font-weight-bold">Serralheria Garcia</div>
            <div class="text-caption" style="opacity: 0.75">Gestão de pedidos</div>
          </div>
        </div>
        <v-divider class="mb-2" />

        <v-list nav density="comfortable" color="primary">
          <v-list-item to="/" exact prepend-icon="mdi-view-dashboard-outline" title="Início" />

          <v-list-subheader>CADASTROS</v-list-subheader>
          <v-list-item to="/clientes" prepend-icon="mdi-account-group-outline" title="Clientes" />
          <v-list-item to="/fornecedores" prepend-icon="mdi-truck-outline" title="Fornecedores" />
          <v-list-item to="/funcionarios" prepend-icon="mdi-account-hard-hat-outline" title="Funcionários" />

          <v-list-subheader>SOLICITAÇÃO</v-list-subheader>
          <v-list-item to="/materiais" prepend-icon="mdi-package-variant-closed" title="Materiais" />
          <v-list-item to="/pedidos" prepend-icon="mdi-clipboard-text-outline" title="Pedidos" />

          <template v-if="admin">
            <v-list-subheader>ADMINISTRAÇÃO</v-list-subheader>
            <v-list-item to="/usuarios" prepend-icon="mdi-shield-account-outline" title="Usuários" />
          </template>
        </v-list>
      </v-navigation-drawer>

      <v-app-bar flat border="b">
        <v-app-bar-nav-icon @click="menuAberto = !menuAberto" />
        <v-app-bar-title>{{ $route.meta.titulo }}</v-app-bar-title>

        <v-btn
          :icon="temaEscuro ? 'mdi-weather-sunny' : 'mdi-weather-night'"
          :title="temaEscuro ? 'Tema claro' : 'Tema escuro'"
          @click="alternarTema"
        />

        <v-menu>
          <template #activator="{ props }">
            <v-btn v-bind="props" class="mr-2" prepend-icon="mdi-account-circle">
              <span class="d-none d-sm-inline">{{ sessao.nome }}</span>
            </v-btn>
          </template>
          <v-list density="compact" min-width="220">
            <v-list-item :title="sessao.nome" :subtitle="admin ? 'Administrador' : 'Funcionário'" />
            <v-divider />
            <v-list-item prepend-icon="mdi-logout" title="Sair" @click="sair" />
          </v-list>
        </v-menu>
      </v-app-bar>

      <v-main>
        <v-container fluid class="pa-4 pa-md-6" style="max-width: 1400px">
          <router-view />
        </v-container>
      </v-main>
    </template>

    <!-- Avisos globais de sucesso/erro -->
    <v-snackbar v-model="notificacao.visivel" :color="notificacao.cor" location="top right" timeout="3500">
      {{ notificacao.texto }}
      <template #actions>
        <v-btn icon="mdi-close" variant="text" @click="notificacao.visivel = false" />
      </template>
    </v-snackbar>
  </v-app>
</template>

<script>
import LogoSerralheria from './components/LogoSerralheria.vue'
import api from './services/api'
import { sessao, limparSessao, ehAdmin } from './services/auth'
import { notificacao } from './services/notificacao'

export default {
  name: 'App',
  components: { LogoSerralheria },
  data() {
    return {
      menuAberto: null, // null: aberto em telas grandes, fechado no celular
      sessao,
      notificacao,
    }
  },
  computed: {
    admin() {
      return ehAdmin()
    },
    temaEscuro() {
      return this.$vuetify.theme.global.current.dark
    },
  },
  methods: {
    alternarTema() {
      const novo = this.temaEscuro ? 'claro' : 'escuro'
      const tema = this.$vuetify.theme
      if (typeof tema.change === 'function') {
        tema.change(novo)
      } else {
        tema.global.name = novo
      }
      try {
        localStorage.setItem('serralheria.tema', novo)
      } catch {
        // preferência não será lembrada
      }
    },
    async sair() {
      try {
        await api.post('/logout')
      } catch {
        // mesmo com erro no servidor, encerra a sessão local
      }
      limparSessao()
      this.$router.push({ name: 'login' })
    },
  },
}
</script>
