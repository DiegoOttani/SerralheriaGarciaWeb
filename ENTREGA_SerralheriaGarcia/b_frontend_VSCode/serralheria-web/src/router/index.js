import { createRouter, createWebHistory } from 'vue-router'
import { estaLogado, ehAdmin } from '../services/auth'

import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Clientes from '../views/Clientes.vue'
import Fornecedores from '../views/Fornecedores.vue'
import Funcionarios from '../views/Funcionarios.vue'
import Materiais from '../views/Materiais.vue'
import Pedidos from '../views/Pedidos.vue'
import PedidoDetalhe from '../views/PedidoDetalhe.vue'
import Usuarios from '../views/Usuarios.vue'

// meta.publica: não exige login | meta.admin: apenas perfil ADMIN
const routes = [
  { path: '/login', name: 'login', component: Login, meta: { publica: true } },
  { path: '/', name: 'dashboard', component: Dashboard, meta: { titulo: 'Início' } },
  { path: '/clientes', name: 'clientes', component: Clientes, meta: { titulo: 'Clientes' } },
  { path: '/fornecedores', name: 'fornecedores', component: Fornecedores, meta: { titulo: 'Fornecedores' } },
  { path: '/funcionarios', name: 'funcionarios', component: Funcionarios, meta: { titulo: 'Funcionários' } },
  { path: '/materiais', name: 'materiais', component: Materiais, meta: { titulo: 'Materiais' } },
  { path: '/pedidos', name: 'pedidos', component: Pedidos, meta: { titulo: 'Pedidos' } },
  { path: '/pedidos/:id', name: 'pedido', component: PedidoDetalhe, meta: { titulo: 'Pedido' } },
  { path: '/usuarios', name: 'usuarios', component: Usuarios, meta: { titulo: 'Usuários', admin: true } },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Controle de acesso no frontend
router.beforeEach((to) => {
  if (!to.meta.publica && !estaLogado()) {
    return { name: 'login' }
  }
  if (to.name === 'login' && estaLogado()) {
    return { name: 'dashboard' }
  }
  if (to.meta.admin && !ehAdmin()) {
    return { name: 'dashboard' }
  }
})

export default router
