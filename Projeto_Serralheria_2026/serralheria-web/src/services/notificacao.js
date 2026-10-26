import { reactive } from 'vue'
import { mensagemErro } from './api'

// Aviso (snackbar) global exibido pelo App.vue
export const notificacao = reactive({
  visivel: false,
  texto: '',
  cor: 'success',
})

export function notificar(texto, cor = 'success') {
  notificacao.texto = texto
  notificacao.cor = cor
  notificacao.visivel = true
}

export function notificarErro(erro) {
  notificar(mensagemErro(erro), 'error')
}
