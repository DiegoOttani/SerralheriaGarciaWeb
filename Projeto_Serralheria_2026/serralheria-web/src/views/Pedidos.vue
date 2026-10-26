<template>
  <div>
    <CabecalhoPagina titulo="Pedidos" subtitulo="Solicitações de serviço dos clientes" icone="mdi-clipboard-text-outline">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="dialogo = true">Novo pedido</v-btn>
    </CabecalhoPagina>

    <v-card border flat>
      <v-card-text class="d-flex flex-wrap align-center ga-4 pb-0">
        <v-text-field
          v-model="busca"
          label="Buscar por cliente, funcionário ou nº"
          prepend-inner-icon="mdi-magnify"
          density="compact"
          hide-details
          clearable
          max-width="380"
          min-width="240"
        />
        <v-chip-group v-model="filtroEstado" selected-class="text-primary" column>
          <v-chip value="Todos" variant="outlined" filter>Todos</v-chip>
          <v-chip v-for="e in estados" :key="e" :value="e" variant="outlined" filter>{{ e }}</v-chip>
        </v-chip-group>
      </v-card-text>
      <v-data-table
        :headers="colunas"
        :items="pedidosFiltrados"
        :search="busca"
        :loading="carregando"
        :sort-by="[{ key: 'id', order: 'desc' }]"
        no-data-text="Nenhum pedido encontrado"
        class="linhas-clicaveis"
        hover
        @click:row="(_, { item }) => abrir(item)"
      >
        <template #item.id="{ value }"><strong>#{{ value }}</strong></template>
        <template #item.dataSolicitacao="{ value }">{{ data(value) }}</template>
        <template #item.estadoPagamento="{ value }"><EstadoChip :estado="value" /></template>
        <template #item.itens="{ value }">{{ value.length }}</template>
        <template #item.valorTotal="{ value }"><span class="numero font-weight-medium">{{ moeda(value) }}</span></template>
        <template #item.acoes="{ item }">
          <v-btn icon="mdi-chevron-right" variant="text" size="small" title="Abrir pedido" @click.stop="abrir(item)" />
        </template>
      </v-data-table>
    </v-card>

    <PedidoDialog v-model="dialogo" @salvo="abrir" />
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import EstadoChip from '../components/EstadoChip.vue'
import PedidoDialog from '../components/PedidoDialog.vue'
import api from '../services/api'
import { moeda, data } from '../services/formatos'
import { notificarErro } from '../services/notificacao'

export default {
  name: 'PedidosView',
  components: { CabecalhoPagina, EstadoChip, PedidoDialog },
  data() {
    return {
      pedidos: [],
      busca: '',
      filtroEstado: 'Todos',
      estados: ['Pendente', 'Pago', 'Atrasado'],
      carregando: false,
      dialogo: false,
      colunas: [
        { title: 'Nº', key: 'id', width: 80 },
        { title: 'Data', key: 'dataSolicitacao' },
        { title: 'Cliente', key: 'cliente.nome' },
        { title: 'Funcionário', key: 'funcionario.nome' },
        { title: 'Pagamento', key: 'estadoPagamento' },
        { title: 'Itens', key: 'itens', align: 'center', sortable: false },
        { title: 'Total', key: 'valorTotal', align: 'end' },
        { title: '', key: 'acoes', sortable: false, align: 'end', width: 60 },
      ],
    }
  },
  computed: {
    pedidosFiltrados() {
      if (!this.filtroEstado || this.filtroEstado === 'Todos') return this.pedidos
      return this.pedidos.filter((p) => p.estadoPagamento === this.filtroEstado)
    },
  },
  mounted() {
    this.carregar()
    // Atalho "Novo pedido" vindo da tela inicial
    if (this.$route.query.novo) {
      this.dialogo = true
      this.$router.replace({ query: {} })
    }
  },
  methods: {
    moeda,
    data,
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get('/pedidos')
        this.pedidos = data
      } catch (e) {
        notificarErro(e)
      } finally {
        this.carregando = false
      }
    },
    abrir(pedido) {
      this.$router.push(`/pedidos/${pedido.id}`)
    },
  },
}
</script>
