<template>
  <div>
    <CabecalhoPagina titulo="Materiais" subtitulo="Estoque de materiais usados nos serviços" icone="mdi-package-variant-closed">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="novo">Novo material</v-btn>
    </CabecalhoPagina>

    <v-card border flat>
      <v-card-text class="pb-0">
        <v-text-field
          v-model="busca"
          label="Buscar material"
          prepend-inner-icon="mdi-magnify"
          density="compact"
          hide-details
          clearable
          max-width="380"
        />
      </v-card-text>
      <v-data-table
        :headers="colunas"
        :items="materiais"
        :search="busca"
        :loading="carregando"
        no-data-text="Nenhum material cadastrado"
        hover
      >
        <template #item.quantidade="{ value }">
          <v-chip v-if="value <= LIMITE_ESTOQUE_BAIXO" color="warning" size="small" variant="tonal" prepend-icon="mdi-alert" label>
            {{ value }} un.
          </v-chip>
          <span v-else class="numero">{{ value }} un.</span>
        </template>
        <template #item.custoUnidade="{ value }"><span class="numero">{{ moeda(value) }}</span></template>
        <template #item.acoes="{ item }">
          <v-btn icon="mdi-pencil-outline" variant="text" size="small" title="Alterar" @click="editar(item)" />
          <v-btn icon="mdi-delete-outline" variant="text" size="small" color="error" title="Remover" @click="pedirRemocao(item)" />
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="dialogo" max-width="680" persistent>
      <v-card :title="form.id ? 'Alterar material' : 'Novo material'" prepend-icon="mdi-package-variant-closed">
        <v-form ref="formulario" @submit.prevent="salvar">
          <v-card-text>
            <v-row dense>
              <v-col cols="12">
                <v-autocomplete
                  v-model="form.fornecedorId"
                  :items="fornecedores"
                  item-title="nome"
                  item-value="id"
                  label="Fornecedor"
                  no-data-text="Cadastre um fornecedor primeiro"
                  :rules="[regras.obrigatorio]"
                />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.nome" label="Nome" :rules="[regras.obrigatorio]" maxlength="100" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-combobox v-model="form.tipo" :items="tipos" label="Tipo" variant="outlined" density="comfortable" :rules="[regras.obrigatorio]" />
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field v-model="form.marca" label="Marca" :rules="[regras.obrigatorio]" maxlength="100" />
              </v-col>
              <v-col cols="6" sm="4">
                <v-text-field
                  v-model="form.quantidade"
                  label="Qtd. em estoque"
                  type="number"
                  min="0"
                  :rules="[regras.obrigatorio, regras.naoNegativo, regras.inteiro]"
                />
              </v-col>
              <v-col cols="6" sm="4">
                <v-text-field
                  v-model="form.custoUnidade"
                  label="Custo unitário"
                  type="number"
                  prefix="R$"
                  min="0"
                  step="0.01"
                  :rules="[regras.obrigatorio, regras.naoNegativo]"
                />
              </v-col>
              <v-col cols="12">
                <v-textarea v-model="form.descritivo" label="Descrição" :rules="[regras.obrigatorio]" maxlength="500" counter />
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
      :texto="`Deseja realmente excluir o material ${selecionado?.nome}?`"
      :carregando="salvando"
      @confirmar="remover"
    />
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import ConfirmarDialog from '../components/ConfirmarDialog.vue'
import api from '../services/api'
import { regras, moeda } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

const VAZIO = { id: null, fornecedorId: null, nome: '', tipo: '', marca: '', quantidade: '', custoUnidade: '', descritivo: '' }

export default {
  name: 'MateriaisView',
  components: { CabecalhoPagina, ConfirmarDialog },
  data() {
    return {
      LIMITE_ESTOQUE_BAIXO: 10,
      materiais: [],
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
        { title: 'Tipo', key: 'tipo' },
        { title: 'Marca', key: 'marca' },
        { title: 'Fornecedor', key: 'fornecedor.nome' },
        { title: 'Estoque', key: 'quantidade', align: 'end' },
        { title: 'Custo unit.', key: 'custoUnidade', align: 'end' },
        { title: '', key: 'acoes', sortable: false, align: 'end', width: 110 },
      ],
    }
  },
  computed: {
    // Sugestões de tipo a partir dos materiais já cadastrados
    tipos() {
      return [...new Set(this.materiais.map((m) => m.tipo))].sort()
    },
  },
  mounted() {
    this.carregar()
    this.carregarFornecedores()
  },
  methods: {
    moeda,
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get('/materiais')
        this.materiais = data
      } catch (e) {
        notificarErro(e)
      } finally {
        this.carregando = false
      }
    },
    async carregarFornecedores() {
      try {
        const { data } = await api.get('/fornecedores')
        this.fornecedores = data
      } catch (e) {
        notificarErro(e)
      }
    },
    novo() {
      this.form = { ...VAZIO }
      this.dialogo = true
    },
    editar(material) {
      this.form = { ...material, fornecedorId: material.fornecedor.id }
      this.dialogo = true
    },
    async salvar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      const dados = {
        fornecedorId: this.form.fornecedorId,
        nome: this.form.nome,
        tipo: this.form.tipo,
        marca: this.form.marca,
        quantidade: Number(this.form.quantidade),
        custoUnidade: Number(this.form.custoUnidade),
        descritivo: this.form.descritivo,
      }
      this.salvando = true
      try {
        const { data } = this.form.id
          ? await api.put(`/materiais/${this.form.id}`, dados)
          : await api.post('/materiais', dados)
        notificar(data.mensagem)
        this.dialogo = false
        this.carregar()
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
      }
    },
    pedirRemocao(material) {
      this.selecionado = material
      this.confirmar = true
    },
    async remover() {
      this.salvando = true
      try {
        const { data } = await api.delete(`/materiais/${this.selecionado.id}`)
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
