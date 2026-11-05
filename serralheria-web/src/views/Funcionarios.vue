<template>
  <div>
    <CabecalhoPagina titulo="Funcionários" subtitulo="Equipe responsável pelos pedidos" icone="mdi-account-hard-hat-outline">
      <v-btn v-if="admin" color="primary" prepend-icon="mdi-plus" @click="novo">Novo funcionário</v-btn>
    </CabecalhoPagina>

    <v-alert v-if="!admin" type="info" variant="tonal" density="compact" class="mb-4">
      Somente administradores podem cadastrar, alterar ou remover funcionários.
    </v-alert>

    <v-card border flat>
      <v-card-text class="pb-0">
        <v-text-field
          v-model="busca"
          label="Buscar funcionário"
          prepend-inner-icon="mdi-magnify"
          density="compact"
          hide-details
          clearable
          max-width="380"
        />
      </v-card-text>
      <v-data-table
        :headers="colunas"
        :items="funcionarios"
        :search="busca"
        :loading="carregando"
        no-data-text="Nenhum funcionário cadastrado"
        hover
      >
        <template #item.telefone="{ value }">{{ value || '—' }}</template>
        <template #item.salario="{ value }">
          <span class="numero">{{ value != null ? moeda(value) : '—' }}</span>
        </template>
        <template #item.dataCadastro="{ value }">{{ data(value) }}</template>
        <template #item.acoes="{ item }">
          <template v-if="admin">
            <v-btn icon="mdi-pencil-outline" variant="text" size="small" title="Alterar" @click="editar(item)" />
            <v-btn icon="mdi-delete-outline" variant="text" size="small" color="error" title="Remover" @click="pedirRemocao(item)" />
          </template>
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="dialogo" max-width="560" persistent>
      <v-card :title="form.id ? 'Alterar funcionário' : 'Novo funcionário'" prepend-icon="mdi-account-hard-hat-outline">
        <v-form ref="formulario" @submit.prevent="salvar">
          <v-card-text>
            <v-row dense>
              <v-col cols="12">
                <v-text-field v-model="form.nome" label="Nome" :rules="[regras.obrigatorio]" maxlength="100" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  :model-value="form.cpf"
                  label="CPF"
                  :rules="[regras.obrigatorio, regras.cpf]"
                  @update:model-value="form.cpf = mascaraCpf($event)"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  :model-value="form.telefone"
                  label="Telefone (opcional)"
                  :rules="[regras.telefone]"
                  @update:model-value="form.telefone = mascaraTelefone($event)"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="form.salario"
                  label="Salário (opcional)"
                  type="number"
                  prefix="R$"
                  min="0"
                  step="0.01"
                  :rules="[regras.naoNegativo]"
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
      :texto="`Deseja realmente excluir o funcionário ${selecionado?.nome}?`"
      :carregando="salvando"
      @confirmar="remover"
    />
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import ConfirmarDialog from '../components/ConfirmarDialog.vue'
import api from '../services/api'
import { ehAdmin } from '../services/auth'
import { regras, data, moeda, mascaraCpf, mascaraTelefone } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

const VAZIO = { id: null, nome: '', cpf: '', telefone: '', salario: '' }

export default {
  name: 'FuncionariosView',
  components: { CabecalhoPagina, ConfirmarDialog },
  data() {
    return {
      funcionarios: [],
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
        { title: 'CPF', key: 'cpf' },
        { title: 'Telefone', key: 'telefone' },
        { title: 'Salário', key: 'salario', align: 'end' },
        { title: 'Cadastro', key: 'dataCadastro' },
        { title: '', key: 'acoes', sortable: false, align: 'end', width: 110 },
      ],
    }
  },
  computed: {
    admin() {
      return ehAdmin()
    },
  },
  mounted() {
    this.carregar()
  },
  methods: {
    data,
    moeda,
    mascaraCpf,
    mascaraTelefone,
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get('/funcionarios')
        this.funcionarios = data
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
    editar(funcionario) {
      this.form = { ...funcionario, telefone: funcionario.telefone || '', salario: funcionario.salario ?? '' }
      this.dialogo = true
    },
    async salvar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      const dados = {
        ...this.form,
        salario: this.form.salario === '' || this.form.salario === null ? null : Number(this.form.salario),
      }
      this.salvando = true
      try {
        const { data } = dados.id
          ? await api.put(`/funcionarios/${dados.id}`, dados)
          : await api.post('/funcionarios', dados)
        notificar(data.mensagem)
        this.dialogo = false
        this.carregar()
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
      }
    },
    pedirRemocao(funcionario) {
      this.selecionado = funcionario
      this.confirmar = true
    },
    async remover() {
      this.salvando = true
      try {
        const { data } = await api.delete(`/funcionarios/${this.selecionado.id}`)
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
