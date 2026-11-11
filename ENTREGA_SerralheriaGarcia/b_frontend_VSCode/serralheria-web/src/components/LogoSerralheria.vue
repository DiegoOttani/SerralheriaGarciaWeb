<template>
  <!-- Logo: portão em arco (produto típico de serralheria) -->
  <svg :width="tamanho" :height="tamanho" viewBox="0 0 64 64" role="img" aria-label="Serralheria Garcia">
    <rect width="64" height="64" rx="14" fill="#C2570C" />
    <g stroke="#fff" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round" fill="none">
      <path d="M14 50V26Q32 14 50 26V50" />
      <path d="M9 50H55" />
      <path d="M23 50V21.5M32 50V20M41 50V21.5" />
    </g>
  </svg>
</template>

<script>
export default {
  name: 'LogoSerralheria',
  props: {
    tamanho: { type: [Number, String], default: 40 },
  },
}
</script>
