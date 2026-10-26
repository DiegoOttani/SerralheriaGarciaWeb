<template>
  <div>
    <CabecalhoPagina titulo="Usuários" subtitulo="Acesso ao sistema (somente administradores)" icone="mdi-shield-account-outline">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="novo">Novo usuário</v-btn>
    </CabecalhoPagina>

    <v-card border flat>
      <v-data-table :headers="colunas" :items="usuarios" :loading="carregando" no-data-text="Nenhum usuário cadastrado" hover>
        <template #item.nome="{ item }">
          {{ item.nome }}
          <v-chip v-if="item.id === sessao.id" size="x-small" class="ml-1" variant="tonal">você</v-chip>
        </template>
        <template #item.perfil="{ value }">
          <v-chip
            :color="value === 'ADMIN' ? 'primary' : 'secondary'"
            :prepend-icon="value === 'ADMIN' ? 'mdi-shield-crown-outline' : 'mdi-account-outline'"
            size="small"
            variant="tonal"
            label
          >
            {{ value === 'ADMIN' ? 'Administrador' : 'Funcionário' }}
          </v-chip>
        </template>
        <template #item.funcionario="{ value }">{{ value ? value.nome : '—' }}</template>
        <template #item.acoes="{ item }">
          <v-btn icon="mdi-pencil-outline" variant="text" size="small" title="Alterar" @click="editar(item)" />
          <v-btn
            icon="mdi-delete-outline"
            variant="text"
            size="small"
            color="error"
            title="Remover"
            :disabled="item.id === sessao.id"
            @click="pedirRemocao(item)"
          />
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="dialogo" max-width="560" persistent>
      <v-card :title="form.id ? 'Alterar usuário' : 'Novo usuário'" prepend-icon="mdi-shield-account-outline">
        <v-form ref="formulario" @submit.prevent="salvar">
          <v-card-text>
            <v-row dense>
              <v-col cols="12">
                <v-text-field v-model="form.nome" label="Nome" :rules="[regras.obrigatorio]" maxlength="100" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.login" label="Login" autocomplete="off" :rules="[regras.obrigatorio]" maxlength="50" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.senha"
                  label="Senha"
                  type="password"
                  autocomplete="new-password"
                  :hint="form.id ? 'Deixe em branco para manter a senha atual' : 'Mínimo de 4 caracteres'"
                  persistent-hint
                  :rules="form.id ? [] : [regras.obrigatorio]"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-select v-model="form.perfil" :items="perfis" label="Perfil" :rules="[regras.obrigatorio]" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-autocomplete
                  v-model="form.funcionarioId"
                  :items="funcionarios"
                  item-title="nome"
                  item-value="id"
                  label="Funcionário vinculado (opcional)"
                  clearable
                />
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" @click="dialogo = false">Cancelar</v-btn>
            <v-btn color="primary" variant="flat" type="submit" :loading="salvando">Salvar</v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>

    <ConfirmarDialog
      v-model="confirmar"
      :texto="`Deseja realmente excluir o usuário ${selecionado?.login}?`"
      :carregando="salvando"
      @confirmar="remover"
    />
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import ConfirmarDialog from '../components/ConfirmarDialog.vue'
import api from '../services/api'
import { sessao } from '../services/auth'
import { regras } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

const VAZIO = { id: null, nome: '', login: '', senha: '', perfil: 'FUNCIONARIO', funcionarioId: null }

export default {
  name: 'UsuariosView',
  components: { CabecalhoPagina, ConfirmarDialog },
  data() {
    return {
      sessao,
      usuarios: [],
      funcionarios: [],
      carregando: false,
      salvando: false,
      dialogo: false,
      confirmar: false,
      selecionado: null,
      form: { ...VAZIO },
      regras,
      perfis: [
        { title: 'Administrador', value: 'ADMIN' },
        { title: 'Funcionário', value: 'FUNCIONARIO' },
      ],
      colunas: [
        { title: 'Nome', key: 'nome' },
        { title: 'Login', key: 'login' },
        { title: 'Perfil', key: 'perfil' },
        { title: 'Funcionário vinculado', key: 'funcionario', sortable: false },
        { title: '', key: 'acoes', sortable: false, align: 'end', width: 110 },
      ],
    }
  },
  mounted() {
    this.carregar()
    this.carregarFuncionarios()
  },
  methods: {
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get('/usuarios')
        this.usuarios = data
      } catch (e) {
        notificarErro(e)
      } finally {
        this.carregando = false
      }
    },
    async carregarFuncionarios() {
      try {
        const { data } = await api.get('/funcionarios')
        this.funcionarios = data
      } catch (e) {
        notificarErro(e)
      }
    },
    novo() {
      this.form = { ...VAZIO }
      this.dialogo = true
    },
    editar(usuario) {
      this.form = {
        id: usuario.id,
        nome: usuario.nome,
        login: usuario.login,
        senha: '',
        perfil: usuario.perfil,
        funcionarioId: usuario.funcionario ? usuario.funcionario.id : null,
      }
      this.dialogo = true
    },
    async salvar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      this.salvando = true
      try {
        const { data } = this.form.id
          ? await api.put(`/usuarios/${this.form.id}`, this.form)
          : await api.post('/usuarios', this.form)
        notificar(data.mensagem)
        this.dialogo = false
        this.carregar()
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
      }
    },
    pedirRemocao(usuario) {
      this.selecionado = usuario
      this.confirmar = true
    },
    async remover() {
      this.salvando = true
      try {
        const { data } = await api.delete(`/usuarios/${this.selecionado.id}`)
        notificar(data.mensagem)
        this.carregar()
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
        this.confirmar = false
      }
    },
  },
}
</script>
