<template>
  <!-- Estado do pagamento: cor + ícone + texto (nunca só a cor) -->
  <v-chip :color="info.cor" :prepend-icon="info.icone" size="small" variant="tonal" label>
    {{ estado }}
  </v-chip>
</template>

<script>
const ESTADOS = {
  Pago: { cor: 'success', icone: 'mdi-check-circle' },
  Pendente: { cor: 'warning', icone: 'mdi-clock-outline' },
  Atrasado: { cor: 'error', icone: 'mdi-alert-circle' },
}

export default {
  name: 'EstadoChip',
  props: {
    estado: { type: String, required: true },
  },
  computed: {
    info() {
      return ESTADOS[this.estado] || { cor: 'grey', icone: 'mdi-help-circle' }
    },
  },
}
</script>
