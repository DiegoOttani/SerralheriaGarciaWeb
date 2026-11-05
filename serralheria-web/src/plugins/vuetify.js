import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { pt } from 'vuetify/locale'

// Tema preferido salvo no navegador (claro/escuro)
let temaSalvo = 'claro'
try {
  temaSalvo = localStorage.getItem('serralheria.tema') || 'claro'
} catch {
  // navegador sem acesso ao localStorage: usa o tema claro
}

// Identidade visual: grafite (metal) + laranja (brasa da solda)
export default createVuetify({
  components,
  directives,
  locale: {
    locale: 'pt',
    messages: { pt },
  },
  theme: {
    defaultTheme: temaSalvo,
    themes: {
      claro: {
        dark: false,
        colors: {
          primary: '#C2570C',
          secondary: '#2F3A40',
          grafite: '#2F3A40', // menu lateral e destaques escuros
          background: '#F3F4F6',
          surface: '#FFFFFF',
          success: '#2E7D32',
          warning: '#A15C00',
          error: '#C62828',
          info: '#1565C0',
        },
      },
      escuro: {
        dark: true,
        colors: {
          primary: '#F08A3E',
          secondary: '#B0BEC5',
          grafite: '#22282C',
          background: '#121417',
          surface: '#1C1F23',
          success: '#66BB6A',
          warning: '#FFB74D',
          error: '#EF5350',
          info: '#64B5F6',
        },
      },
    },
  },
  defaults: {
    VTextField: { variant: 'outlined', density: 'comfortable', color: 'primary' },
    VTextarea: { variant: 'outlined', density: 'comfortable', color: 'primary', rows: 3 },
    VSelect: { variant: 'outlined', density: 'comfortable', color: 'primary' },
    VAutocomplete: { variant: 'outlined', density: 'comfortable', color: 'primary' },
    VCard: { rounded: 'lg' },
    VBtn: { style: 'text-transform: none; letter-spacing: 0;' },
  },
})
