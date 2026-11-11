import axios from 'axios'
import router from '../router'
import { sessao, limparSessao } from './auth'

// Cliente HTTP para o backend REST (Spring Boot)
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
})

// Envia o token em todas as requisições
api.interceptors.request.use((config) => {
  if (sessao.token) {
    config.headers.Authorization = `Bearer ${sessao.token}`
  }
  return config
})

// Sessão expirada (401): volta para a tela de login
api.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    if (erro.response?.status === 401 && sessao.token) {
      limparSessao()
      router.push({ name: 'login', query: { expirou: '1' } })
    }
    return Promise.reject(erro)
  },
)

// Extrai a mensagem enviada pelo backend ({"mensagem": "..."})
export function mensagemErro(erro) {
  if (erro.response?.data?.mensagem) {
    return erro.response.data.mensagem
  }
  if (!erro.response) {
    return 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.'
  }
  return 'Ocorreu um erro inesperado.'
}

export default api
