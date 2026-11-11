<template>
  <div>
    <CabecalhoPagina titulo="Clientes" subtitulo="Pessoas físicas (CPF) e jurídicas (CNPJ)" icone="mdi-account-group-outline">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="novo">Novo cliente</v-btn>
    </CabecalhoPagina>

    <v-card border flat>
      <v-card-text class="pb-0">
        <v-text-field
          v-model="busca"
          label="Buscar cliente"
          prepend-inner-icon="mdi-magnify"
          density="compact"
          hide-details
          clearable
          max-width="380"
        />
      </v-card-text>
      <v-data-table
        :headers="colunas"
        :items="clientes"
        :search="busca"
        :loading="carregando"
        no-data-text="Nenhum cliente cadastrado"
        hover
      >
        <template #item.acoes="{ item }">
          <v-btn icon="mdi-pencil-outline" variant="text" size="small" title="Alterar" @click="editar(item)" />
          <v-btn icon="mdi-delete-outline" variant="text" size="small" color="error" title="Remover" @click="pedirRemocao(item)" />
        </template>
      </v-data-table>
    </v-card>

    <!-- Formulário de inclusão/alteração -->
    <v-dialog v-model="dialogo" max-width="640" persistent>
      <v-card :title="form.id ? 'Alterar cliente' : 'Novo cliente'" prepend-icon="mdi-account-outline">
        <v-form ref="formulario" @submit.prevent="salvar">
          <v-card-text>
            <v-row dense>
              <v-col cols="12">
                <v-text-field v-model="form.nome" label="Nome" :rules="[regras.obrigatorio]" maxlength="100" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  :model-value="form.cpf"
                  label="CPF / CNPJ"
                  :rules="[regras.obrigatorio, regras.cpfCnpj]"
                  @update:model-value="form.cpf = mascaraCpfCnpj($event)"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  :model-value="form.telefone"
                  label="Telefone"
                  :rules="[regras.obrigatorio, regras.telefone]"
                  @update:model-value="form.telefone = mascaraTelefone($event)"
                />
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="form.endereco" label="Endereço" :rules="[regras.obrigatorio]" maxlength="100" />
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="form.email" label="E-mail" type="email" :rules="[regras.obrigatorio, regras.email]" />
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
      :texto="`Deseja realmente excluir o cliente ${selecionado?.nome}?`"
      :carregando="salvando"
      @confirmar="remover"
    />
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import ConfirmarDialog from '../components/ConfirmarDialog.vue'
import api from '../services/api'
import { regras, mascaraCpfCnpj, mascaraTelefone } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

const VAZIO = { id: null, nome: '', cpf: '', telefone: '', endereco: '', email: '' }

export default {
  name: 'ClientesView',
  components: { CabecalhoPagina, ConfirmarDialog },
  data() {
    return {
      clientes: [],
      busca: '',
      carregando: false,
      salvando: false,
      dialogo: false,
      confirmar: false,
      selecionado: null,
      form: { ...VAZIO },
      regras,
      colunas: [
        { title: 'Nome', key: 'nome' },
        { title: 'CPF / CNPJ', key: 'cpf' },
        { title: 'Telefone', key: 'telefone' },
        { title: 'E-mail', key: 'email' },
        { title: 'Endereço', key: 'endereco' },
        { title: '', key: 'acoes', sortable: false, align: 'end', width: 110 },
      ],
    }
  },
  mounted() {
    this.carregar()
  },
  methods: {
    mascaraCpfCnpj,
    mascaraTelefone,
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get('/clientes')
        this.clientes = data
      } catch (e) {
        notificarErro(e)
      } finally {
        this.carregando = false
      }
    },
    novo() {
      this.form = { ...VAZIO }
      this.dialogo = true
    },
    editar(cliente) {
      this.form = { ...cliente }
      this.dialogo = true
    },
    async salvar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      this.salvando = true
      try {
        const { data } = this.form.id
          ? await api.put(`/clientes/${this.form.id}`, this.form)
          : await api.post('/clientes', this.form)
        notificar(data.mensagem)
        this.dialogo = false
        this.carregar()
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
      }
    },
    pedirRemocao(cliente) {
      this.selecionado = cliente
      this.confirmar = true
    },
    async remover() {
      this.salvando = true
      try {
        const { data } = await api.delete(`/clientes/${this.selecionado.id}`)
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
