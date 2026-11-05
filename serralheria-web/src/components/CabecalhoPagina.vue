<template>
  <!-- Título da página + botões de ação (slot) -->
  <div class="d-flex flex-wrap align-center ga-3 mb-5">
    <v-avatar color="primary" variant="tonal" rounded="lg" size="48">
      <v-icon :icon="icone" size="26" />
    </v-avatar>
    <div class="flex-grow-1">
      <h1 class="text-h5 font-weight-bold">{{ titulo }}</h1>
      <div v-if="subtitulo" class="text-body-2 text-medium-emphasis">{{ subtitulo }}</div>
    </div>
    <div class="d-flex flex-wrap ga-2">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  name: 'CabecalhoPagina',
  props: {
    titulo: { type: String, required: true },
    subtitulo: { type: String, default: '' },
    icone: { type: String, default: 'mdi-file' },
  },
}
</script>
