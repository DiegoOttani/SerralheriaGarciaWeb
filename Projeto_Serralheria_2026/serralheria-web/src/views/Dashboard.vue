<template>
  <div>
    <CabecalhoPagina
      :titulo="`Olá, ${sessao.nome}!`"
      subtitulo="Resumo da serralheria"
      icone="mdi-view-dashboard-outline"
    >
      <v-btn color="primary" prepend-icon="mdi-plus" to="/pedidos?novo=1">Novo pedido</v-btn>
    </CabecalhoPagina>

    <v-progress-linear v-if="carregando" indeterminate color="primary" class="mb-4" />

    <template v-if="resumo">
      <!-- Indicadores principais -->
      <v-row>
        <v-col v-for="card in indicadores" :key="card.titulo" cols="12" sm="6" lg="3">
          <v-card border flat class="h-100">
            <v-card-text class="d-flex align-center ga-4">
              <v-avatar :color="card.cor" variant="tonal" rounded="lg" size="52">
                <v-icon :icon="card.icone" size="28" />
              </v-avatar>
              <div>
                <div class="text-body-2 text-medium-emphasis">{{ card.titulo }}</div>
                <div class="text-h5 font-weight-bold numero">{{ card.valor }}</div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="mt-2">
        <!-- Pedidos por estado de pagamento -->
        <v-col cols="12" md="5">
          <v-card border flat class="h-100">
            <v-card-title class="text-subtitle-1 font-weight-bold">Pedidos por pagamento</v-card-title>
            <v-card-text>
              <div v-for="e in estados" :key="e.estado" class="mb-4">
                <div class="d-flex justify-space-between align-center mb-1">
                  <EstadoChip :estado="e.estado" />
                  <span class="text-body-2 numero">
                    <strong>{{ e.quantidade }}</strong>
                    <span class="text-medium-emphasis"> de {{ resumo.totalPedidos }}</span>
                  </span>
                </div>
                <v-progress-linear
                  :model-value="percentual(e.quantidade)"
                  :color="e.cor"
                  bg-color="on-surface"
                  bg-opacity="0.08"
                  height="8"
                  rounded
                />
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Alerta de estoque baixo -->
        <v-col cols="12" md="7">
          <v-card border flat class="h-100">
            <v-card-title class="d-flex align-center text-subtitle-1 font-weight-bold">
              Estoque baixo
              <v-chip size="small" class="ml-2" variant="tonal">até 10 unidades</v-chip>
            </v-card-title>
            <v-card-text>
              <div v-if="!resumo.estoqueBaixo.length" class="d-flex align-center ga-2 text-medium-emphasis">
                <v-icon icon="mdi-check-circle" color="success" /> Todos os materiais com estoque adequado.
              </div>
              <v-list v-else density="compact" class="pa-0">
                <v-list-item
                  v-for="m in resumo.estoqueBaixo"
                  :key="m.id"
                  :title="m.nome"
                  :subtitle="`${m.tipo} · ${m.fornecedor.nome}`"
                  to="/materiais"
                >
                  <template #prepend>
                    <v-icon icon="mdi-alert" color="warning" />
                  </template>
                  <template #append>
                    <span class="text-body-1 font-weight-bold numero text-high-emphasis">{{ m.quantidade }} un.</span>
                  </template>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Últimos pedidos -->
      <v-card border flat class="mt-6">
        <v-card-title class="d-flex align-center text-subtitle-1 font-weight-bold">
          Últimos pedidos
          <v-spacer />
          <v-btn variant="text" size="small" color="primary" to="/pedidos" append-icon="mdi-arrow-right">Ver todos</v-btn>
        </v-card-title>
        <v-data-table
          :headers="colunasPedidos"
          :items="resumo.ultimosPedidos"
          class="linhas-clicaveis"
          hide-default-footer
          hover
          @click:row="(_, { item }) => $router.push(`/pedidos/${item.id}`)"
        >
          <template #item.dataSolicitacao="{ value }">{{ data(value) }}</template>
          <template #item.estadoPagamento="{ value }"><EstadoChip :estado="value" /></template>
          <template #item.valorTotal="{ value }"><span class="numero">{{ moeda(value) }}</span></template>
        </v-data-table>
      </v-card>
    </template>
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import EstadoChip from '../components/EstadoChip.vue'
import api from '../services/api'
import { sessao } from '../services/auth'
import { moeda, data } from '../services/formatos'
import { notificarErro } from '../services/notificacao'

export default {
  name: 'DashboardView',
  components: { CabecalhoPagina, EstadoChip },
  data() {
    return {
      sessao,
      resumo: null,
      carregando: false,
      colunasPedidos: [
        { title: 'Nº', key: 'id', width: 70 },
        { title: 'Data', key: 'dataSolicitacao' },
        { title: 'Cliente', key: 'cliente.nome' },
        { title: 'Pagamento', key: 'estadoPagamento' },
        { title: 'Total', key: 'valorTotal', align: 'end' },
      ],
    }
  },
  computed: {
    indicadores() {
      const r = this.resumo
      return [
        { titulo: 'Clientes', valor: r.totalClientes, icone: 'mdi-account-group-outline', cor: 'secondary' },
        { titulo: 'Pedidos', valor: r.totalPedidos, icone: 'mdi-clipboard-text-outline', cor: 'primary' },
        { titulo: 'Valor recebido', valor: moeda(r.valorRecebido), icone: 'mdi-cash-check', cor: 'success' },
        { titulo: 'A receber', valor: moeda(r.valorEmAberto), icone: 'mdi-cash-clock', cor: 'warning' },
      ]
    },
    estados() {
      const r = this.resumo
      return [
        { estado: 'Pago', quantidade: r.pedidosPagos, cor: 'success' },
        { estado: 'Pendente', quantidade: r.pedidosPendentes, cor: 'warning' },
        { estado: 'Atrasado', quantidade: r.pedidosAtrasados, cor: 'error' },
      ]
    },
  },
  mounted() {
    this.carregar()
  },
  methods: {
    moeda,
    data,
    percentual(qtd) {
      return this.resumo.totalPedidos ? (qtd / this.resumo.totalPedidos) * 100 : 0
    },
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get('/dashboard')
        this.resumo = data
      } catch (e) {
        notificarErro(e)
      } finally {
        this.carregando = false
      }
    },
  },
}
</script>
