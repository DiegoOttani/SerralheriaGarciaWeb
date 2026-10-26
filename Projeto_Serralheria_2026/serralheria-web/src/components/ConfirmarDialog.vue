<template>
  <!-- Confirmação antes de remover um registro -->
  <v-dialog :model-value="modelValue" max-width="420" @update:model-value="$emit('update:modelValue', $event)">
    <v-card>
      <v-card-title class="d-flex align-center ga-2 pt-4">
        <v-icon icon="mdi-alert-circle-outline" color="error" />
        {{ titulo }}
      </v-card-title>
      <v-card-text>{{ texto }}</v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="$emit('update:modelValue', false)">Cancelar</v-btn>
        <v-btn color="error" variant="flat" :loading="carregando" @click="$emit('confirmar')">Remover</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: 'ConfirmarDialog',
  props: {
    modelValue: { type: Boolean, default: false },
    titulo: { type: String, default: 'Confirmar remoção' },
    texto: { type: String, default: 'Deseja realmente excluir este registro?' },
    carregando: { type: Boolean, default: false },
  },
  emits: ['update:modelValue', 'confirmar'],
}
</script>
