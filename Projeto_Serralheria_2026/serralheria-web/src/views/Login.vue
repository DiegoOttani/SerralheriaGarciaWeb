<template>
  <v-main class="login-fundo">
    <v-container class="fill-height justify-center">
      <v-card class="login-card" elevation="12" rounded="xl">
        <v-row no-gutters>
          <!-- Painel de apresentação (oculto no celular) -->
          <v-col cols="12" md="6" class="login-marca d-none d-md-flex flex-column justify-space-between pa-10">
            <div class="d-flex align-center ga-3">
              <LogoSerralheria :tamanho="52" />
              <div>
                <div class="text-h6 font-weight-bold">Serralheria Garcia</div>
                <div class="text-body-2" style="opacity: 0.8">Portões, grades e estruturas metálicas</div>
              </div>
            </div>
            <div>
              <div class="text-h4 font-weight-bold mb-3">Gestão de pedidos e estoque</div>
              <div class="text-body-1" style="opacity: 0.85">
                Clientes, fornecedores, materiais e pedidos em um só lugar.
              </div>
            </div>
            <div class="text-caption" style="opacity: 0.6">Piracicaba · 2026</div>
          </v-col>

          <!-- Formulário de login -->
          <v-col cols="12" md="6" class="pa-8 pa-sm-10">
            <div class="d-flex d-md-none align-center ga-3 mb-6">
              <LogoSerralheria :tamanho="44" />
              <div class="text-h6 font-weight-bold">Serralheria Garcia</div>
            </div>

            <h1 class="text-h5 font-weight-bold mb-1">Entrar</h1>
            <p class="text-body-2 text-medium-emphasis mb-6">Informe seu login e senha para acessar o sistema.</p>

            <v-alert v-if="$route.query.expirou" type="warning" variant="tonal" density="compact" class="mb-4">
              Sua sessão expirou. Faça login novamente.
            </v-alert>
            <v-alert v-if="erro" type="error" variant="tonal" density="compact" class="mb-4">
              {{ erro }}
            </v-alert>

            <v-form ref="formulario" @submit.prevent="entrar">
              <v-text-field
                v-model="login"
                label="Login"
                prepend-inner-icon="mdi-account-outline"
                autocomplete="username"
                autofocus
                :rules="[regras.obrigatorio]"
              />
              <v-text-field
                v-model="senha"
                label="Senha"
                prepend-inner-icon="mdi-lock-outline"
                autocomplete="current-password"
                :type="mostrarSenha ? 'text' : 'password'"
                :append-inner-icon="mostrarSenha ? 'mdi-eye-off' : 'mdi-eye'"
                :rules="[regras.obrigatorio]"
                @click:append-inner="mostrarSenha = !mostrarSenha"
              />
              <v-btn type="submit" color="primary" size="large" block :loading="carregando" class="mt-2">
                Entrar
              </v-btn>
            </v-form>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </v-main>
</template>

<script>
import LogoSerralheria from '../components/LogoSerralheria.vue'
import api, { mensagemErro } from '../services/api'
import { salvarSessao } from '../services/auth'
import { regras } from '../services/formatos'

export default {
  name: 'LoginView',
  components: { LogoSerralheria },
  data() {
    return {
      login: '',
      senha: '',
      mostrarSenha: false,
      carregando: false,
      erro: '',
      regras,
    }
  },
  methods: {
    async entrar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      this.carregando = true
      this.erro = ''
      try {
        const { data } = await api.post('/login', { login: this.login, senha: this.senha })
        salvarSessao(data)
        this.$router.push({ name: 'dashboard' })
      } catch (e) {
        this.erro = mensagemErro(e)
      } finally {
        this.carregando = false
      }
    },
  },
}
</script>

<style scoped>
.login-fundo {
  background:
    radial-gradient(circle at 15% 20%, rgba(194, 87, 12, 0.35), transparent 45%),
    linear-gradient(135deg, #1f272b 0%, #2f3a40 55%, #3d4a51 100%);
  min-height: 100vh;
}

.login-card {
  width: 100%;
  max-width: 920px;
  overflow: hidden;
}

.login-marca {
  color: #fff;
  min-height: 480px;
  background:
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.04) 0 2px, transparent 2px 14px),
    linear-gradient(160deg, #2f3a40 0%, #1f272b 100%);
  border-right: 4px solid #c2570c;
}
</style>
