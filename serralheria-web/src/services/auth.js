import { reactive } from 'vue'

// Sessão do usuário logado (token + dados), guardada também no navegador
const CHAVE = 'serralheria.sessao'

const VAZIA = { token: null, id: null, nome: null, login: null, perfil: null }

function carregar() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE)) || {}
  } catch {
    return {}
  }
}

export const sessao = reactive({ ...VAZIA, ...carregar() })

export function salvarSessao(dados) {
  Object.assign(sessao, dados)
  try {
    localStorage.setItem(CHAVE, JSON.stringify(dados))
  } catch {
    // sem localStorage a sessão vale só enquanto a aba estiver aberta
  }
}

export function limparSessao() {
  Object.assign(sessao, VAZIA)
  try {
    localStorage.removeItem(CHAVE)
  } catch {
    // ignora
  }
}

export function estaLogado() {
  return !!sessao.token
}

export function ehAdmin() {
  return sessao.perfil === 'ADMIN'
}
