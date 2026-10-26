<template>
  <!-- Inclusão/alteração de um item do pedido -->
  <v-dialog :model-value="modelValue" max-width="680" persistent @update:model-value="$emit('update:modelValue', $event)">
    <v-card :title="item ? 'Alterar item' : 'Novo item'" prepend-icon="mdi-format-list-bulleted">
      <v-form ref="formulario" @submit.prevent="salvar">
        <v-card-text>
          <v-row dense>
            <v-col cols="12">
              <v-text-field v-model="form.nome" label="Produto / serviço" placeholder="Ex.: Portão basculante 3x2,5 m" :rules="[regras.obrigatorio]" maxlength="100" />
            </v-col>
            <v-col cols="6" sm="4">
              <v-text-field v-model="form.quantidade" label="Quantidade" type="number" min="1" :rules="[regras.obrigatorio, regras.positivo, regras.inteiro]" />
            </v-col>
            <v-col cols="6" sm="4">
              <v-text-field
                v-model="form.valorUnidade"
                label="Valor unitário"
                type="number"
                prefix="R$"
                min="0"
                step="0.01"
                :rules="[regras.obrigatorio, regras.naoNegativo]"
              />
            </v-col>
            <v-col cols="12" sm="4">
              <v-text-field :model-value="moeda(subtotal)" label="Subtotal" readonly variant="solo-filled" flat />
            </v-col>

            <v-col cols="12">
              <v-divider class="my-2" />
              <div class="text-subtitle-2 mb-2">Material utilizado</div>
            </v-col>
            <v-col cols="12" sm="8">
              <v-autocomplete
                v-model="form.materialId"
                :items="materiais"
                item-title="nome"
                item-value="id"
                label="Material"
                no-data-text="Nenhum material cadastrado"
                :rules="[regras.obrigatorio]"
              >
                <template #item="{ props, item: opcao }">
                  <v-list-item v-bind="props" :subtitle="`${opcao.raw.tipo} · estoque: ${disponivel(opcao.raw)} un.`" />
                </template>
              </v-autocomplete>
            </v-col>
            <v-col cols="12" sm="4">
              <v-text-field
                v-model="form.quantidadeMaterial"
                label="Qtd. de material"
                type="number"
                min="0"
                :hint="materialSelecionado ? `Disponível: ${disponivel(materialSelecionado)} un.` : ''"
                persistent-hint
                :rules="[regras.obrigatorio, regras.naoNegativo, regras.inteiro, regraEstoque]"
              />
            </v-col>
            <v-col cols="12">
              <v-textarea v-model="form.descritivo" label="Descrição" :rules="[regras.obrigatorio]" maxlength="500" counter />
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="$emit('update:modelValue', false)">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" type="submit" :loading="salvando">Salvar</v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script>
import api from '../services/api'
import { regras, moeda } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

export default {
  name: 'ItemDialog',
  props: {
    modelValue: { type: Boolean, default: false },
    pedidoId: { type: [Number, String], required: true },
    item: { type: Object, default: null }, // null = novo item
  },
  emits: ['update:modelValue', 'salvo'],
  data() {
    return {
      materiais: [],
      form: {},
      salvando: false,
      regras,
    }
  },
  computed: {
    subtotal() {
      return (Number(this.form.quantidade) || 0) * (Number(this.form.valorUnidade) || 0)
    },
    materialSelecionado() {
      return this.materiais.find((m) => m.id === this.form.materialId) || null
    },
  },
  watch: {
    modelValue(aberto) {
      if (!aberto) return
      const i = this.item
      this.form = i
        ? {
            nome: i.nome,
            quantidade: i.quantidade,
            valorUnidade: i.valorUnidade,
            materialId: i.material.id,
            quantidadeMaterial: i.quantidadeMaterial,
            descritivo: i.descritivo,
          }
        : { nome: '', quantidade: 1, valorUnidade: '', materialId: null, quantidadeMaterial: '', descritivo: '' }
      this.carregarMateriais()
    },
  },
  methods: {
    moeda,
    async carregarMateriais() {
      try {
        const { data } = await api.get('/materiais')
        this.materiais = data
      } catch (e) {
        notificarErro(e)
      }
    },
    // Na alteração, o que o próprio item já consumiu volta a ficar disponível
    disponivel(material) {
      const consumoAtual = this.item && this.item.material.id === material.id ? this.item.quantidadeMaterial : 0
      return material.quantidade + consumoAtual
    },
    regraEstoque(v) {
      if (!this.materialSelecionado) return true
      return Number(v) <= this.disponivel(this.materialSelecionado) || 'Estoque insuficiente'
    },
    async salvar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      const dados = {
        ...this.form,
        quantidade: Number(this.form.quantidade),
        valorUnidade: Number(this.form.valorUnidade),
        quantidadeMaterial: Number(this.form.quantidadeMaterial),
      }
      this.salvando = true
      try {
        const { data } = this.item
          ? await api.put(`/pedidos/${this.pedidoId}/itens/${this.item.id}`, dados)
          : await api.post(`/pedidos/${this.pedidoId}/itens`, dados)
        notificar(data.mensagem)
        this.$emit('salvo')
        this.$emit('update:modelValue', false)
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
      }
    },
  },
}
</script>
