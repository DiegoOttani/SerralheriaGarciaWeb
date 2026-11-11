<template>
  <!-- Campo de valor em reais: aceita só dígitos e mostra sempre 2 casas com vírgula (ex.: 1.234,56) -->
  <v-text-field
    v-bind="$attrs"
    :model-value="texto"
    prefix="R$"
    inputmode="numeric"
    @update:model-value="atualizar"
  />
</template>

<script>
import { mascaraMoeda } from '../services/formatos'

export default {
  name: 'CampoMoeda',
  inheritAttrs: false,
  props: {
    modelValue: { type: String, default: '' },
  },
  emits: ['update:modelValue'],
  data() {
    return { texto: this.modelValue }
  },
  watch: {
    modelValue(valor) {
      this.texto = valor
    },
  },
  methods: {
    atualizar(valor) {
      const formatado = mascaraMoeda(valor)
      // atribui o texto bruto e depois o formatado para o campo sempre ser redesenhado
      this.texto = valor
      this.$nextTick(() => {
        this.texto = formatado
      })
      this.$emit('update:modelValue', formatado)
    },
  },
}
</script>
