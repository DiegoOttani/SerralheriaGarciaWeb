<template>
  <div>
    <CabecalhoPagina titulo="Fornecedores" subtitulo="Empresas que fornecem os materiais" icone="mdi-truck-outline">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="novo">Novo fornecedor</v-btn>
    </CabecalhoPagina>

    <v-card border flat>
      <v-card-text class="pb-0">
        <v-text-field
          v-model="busca"
          label="Buscar fornecedor"
          prepend-inner-icon="mdi-magnify"
          density="compact"
          hide-details
          clearable
          max-width="380"
        />
      </v-card-text>
      <v-data-table
        :headers="colunas"
        :items="fornecedores"
        :search="busca"
        :loading="carregando"
        no-data-text="Nenhum fornecedor cadastrado"
        hover
      >
        <template #item.dataCadastro="{ value }">{{ data(value) }}</template>
        <template #item.acoes="{ item }">
          <v-btn icon="mdi-pencil-outline" variant="text" size="small" title="Alterar" @click="editar(item)" />
          <v-btn icon="mdi-delete-outline" variant="text" size="small" color="error" title="Remover" @click="pedirRemocao(item)" />
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="dialogo" max-width="640" persistent>
      <v-card :title="form.id ? 'Alterar fornecedor' : 'Novo fornecedor'" prepend-icon="mdi-truck-outline">
        <v-form ref="formulario" @submit.prevent="salvar">
          <v-card-text>
            <v-row dense>
              <v-col cols="12">
                <v-text-field v-model="form.nome" label="Nome / Razão social" :rules="[regras.obrigatorio]" maxlength="100" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  :model-value="form.cnpj"
                  label="CNPJ"
                  :rules="[regras.obrigatorio, regras.cnpj]"
                  @update:model-value="form.cnpj = mascaraCnpj($event)"
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
      :texto="`Deseja realmente excluir o fornecedor ${selecionado?.nome}?`"
      :carregando="salvando"
      @confirmar="remover"
    />
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import ConfirmarDialog from '../components/ConfirmarDialog.vue'
import api from '../services/api'
import { regras, data, mascaraCnpj, mascaraTelefone } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

const VAZIO = { id: null, nome: '', cnpj: '', telefone: '', endereco: '', email: '' }

export default {
  name: 'FornecedoresView',
  components: { CabecalhoPagina, ConfirmarDialog },
  data() {
    return {
      fornecedores: [],
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
        { title: 'CNPJ', key: 'cnpj' },
        { title: 'Telefone', key: 'telefone' },
        { title: 'E-mail', key: 'email' },
        { title: 'Cadastro', key: 'dataCadastro' },
        { title: '', key: 'acoes', sortable: false, align: 'end', width: 110 },
      ],
    }
  },
  mounted() {
    this.carregar()
  },
  methods: {
    data,
    mascaraCnpj,
    mascaraTelefone,
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get('/fornecedores')
        this.fornecedores = data
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
    editar(fornecedor) {
      this.form = { ...fornecedor }
      this.dialogo = true
    },
    async salvar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      this.salvando = true
      try {
        const { data } = this.form.id
          ? await api.put(`/fornecedores/${this.form.id}`, this.form)
          : await api.post('/fornecedores', this.form)
        notificar(data.mensagem)
        this.dialogo = false
        this.carregar()
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
      }
    },
    pedirRemocao(fornecedor) {
      this.selecionado = fornecedor
      this.confirmar = true
    },
    async remover() {
      this.salvando = true
      try {
        const { data } = await api.delete(`/fornecedores/${this.selecionado.id}`)
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
