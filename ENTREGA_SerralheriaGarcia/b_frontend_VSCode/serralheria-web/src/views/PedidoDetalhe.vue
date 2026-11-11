<template>
  <div>
    <v-btn variant="text" prepend-icon="mdi-arrow-left" class="mb-2 px-1" to="/pedidos">Voltar para pedidos</v-btn>

    <v-progress-linear v-if="carregando && !pedido" indeterminate color="primary" />

    <template v-if="pedido">
      <CabecalhoPagina :titulo="`Pedido nº ${pedido.id}`" :subtitulo="`Solicitado em ${data(pedido.dataSolicitacao)}`" icone="mdi-clipboard-text-outline">
        <v-btn variant="outlined" prepend-icon="mdi-pencil-outline" @click="dialogoPedido = true">Alterar pedido</v-btn>
        <v-btn variant="outlined" color="error" prepend-icon="mdi-delete-outline" @click="confirmarPedido = true">Remover</v-btn>
      </CabecalhoPagina>

      <v-row>
        <!-- Dados do pedido -->
        <v-col cols="12" md="8">
          <v-card border flat class="h-100">
            <v-card-text>
              <v-row>
                <v-col cols="12" sm="6">
                  <div class="text-caption text-medium-emphasis">Cliente</div>
                  <div class="text-body-1 font-weight-medium">{{ pedido.cliente.nome }}</div>
                  <div class="text-body-2 text-medium-emphasis">{{ pedido.cliente.telefone }} · {{ pedido.cliente.email }}</div>
                </v-col>
                <v-col cols="12" sm="6">
                  <div class="text-caption text-medium-emphasis">Funcionário responsável</div>
                  <div class="text-body-1 font-weight-medium">{{ pedido.funcionario.nome }}</div>
                </v-col>
                <v-col cols="12" sm="6">
                  <div class="text-caption text-medium-emphasis mb-1">Pagamento</div>
                  <EstadoChip :estado="pedido.estadoPagamento" />
                </v-col>
                <v-col cols="12" sm="6">
                  <div class="text-caption text-medium-emphasis">Endereço</div>
                  <div class="text-body-2">{{ pedido.cliente.endereco }}</div>
                </v-col>
                <v-col v-if="pedido.descritivo" cols="12">
                  <div class="text-caption text-medium-emphasis">Descrição</div>
                  <div class="text-body-2">{{ pedido.descritivo }}</div>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Total -->
        <v-col cols="12" md="4">
          <v-card color="grafite" class="h-100 d-flex flex-column justify-center pa-6">
            <div class="text-body-2" style="opacity: 0.8">Valor total do pedido</div>
            <div class="text-h4 font-weight-bold numero">{{ moeda(pedido.valorTotal) }}</div>
            <div class="text-body-2 mt-1" style="opacity: 0.8">
              {{ pedido.itens.length }} {{ pedido.itens.length === 1 ? 'item' : 'itens' }} · calculado automaticamente
            </div>
          </v-card>
        </v-col>
      </v-row>

      <!-- Itens do pedido (Pedido 1:N Item) -->
      <v-card border flat class="mt-6">
        <v-card-title class="d-flex flex-wrap align-center ga-2 pt-4">
          <span class="text-subtitle-1 font-weight-bold">Itens do pedido</span>
          <v-spacer />
          <v-btn color="primary" prepend-icon="mdi-plus" @click="novoItem">Adicionar item</v-btn>
        </v-card-title>
        <v-data-table
          :headers="colunas"
          :items="pedido.itens"
          no-data-text="Nenhum item adicionado. Clique em “Adicionar item”."
          hide-default-footer
          :items-per-page="-1"
          hover
        >
          <template #item.nome="{ item }">
            <div class="font-weight-medium">{{ item.nome }}</div>
            <div class="text-caption text-medium-emphasis">{{ item.descritivo }}</div>
          </template>
          <template #item.material="{ item }">
            {{ item.material.nome }}
            <span class="text-medium-emphasis numero">({{ item.quantidadeMaterial }} un.)</span>
          </template>
          <template #item.valorUnidade="{ value }"><span class="numero">{{ moeda(value) }}</span></template>
          <template #item.subtotal="{ value }"><span class="numero font-weight-medium">{{ moeda(value) }}</span></template>
          <template #item.acoes="{ item }">
            <v-btn icon="mdi-pencil-outline" variant="text" size="small" title="Alterar" @click="editarItem(item)" />
            <v-btn icon="mdi-delete-outline" variant="text" size="small" color="error" title="Remover" @click="pedirRemocaoItem(item)" />
          </template>
        </v-data-table>
      </v-card>

      <PedidoDialog v-model="dialogoPedido" :pedido="pedido" @salvo="carregar" />
      <ItemDialog v-model="dialogoItem" :pedido-id="pedido.id" :item="itemSelecionado" @salvo="carregar" />

      <ConfirmarDialog
        v-model="confirmarItem"
        :texto="`Remover o item ${itemSelecionado?.nome}? O material volta para o estoque.`"
        :carregando="removendo"
        @confirmar="removerItem"
      />
      <ConfirmarDialog
        v-model="confirmarPedido"
        :texto="`Remover o pedido nº ${pedido.id} e todos os seus itens? Os materiais voltam para o estoque.`"
        :carregando="removendo"
        @confirmar="removerPedido"
      />
    </template>
  </div>
</template>

<script>
import CabecalhoPagina from '../components/CabecalhoPagina.vue'
import ConfirmarDialog from '../components/ConfirmarDialog.vue'
import EstadoChip from '../components/EstadoChip.vue'
import ItemDialog from '../components/ItemDialog.vue'
import PedidoDialog from '../components/PedidoDialog.vue'
import api from '../services/api'
import { moeda, data } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

export default {
  name: 'PedidoDetalheView',
  components: { CabecalhoPagina, ConfirmarDialog, EstadoChip, ItemDialog, PedidoDialog },
  data() {
    return {
      pedido: null,
      carregando: false,
      removendo: false,
      dialogoPedido: false,
      dialogoItem: false,
      confirmarItem: false,
      confirmarPedido: false,
      itemSelecionado: null,
      colunas: [
        { title: 'Produto / serviço', key: 'nome' },
        { title: 'Material', key: 'material', sortable: false },
        { title: 'Qtd.', key: 'quantidade', align: 'center' },
        { title: 'Valor unit.', key: 'valorUnidade', align: 'end' },
        { title: 'Subtotal', key: 'subtotal', align: 'end' },
        { title: '', key: 'acoes', sortable: false, align: 'end', width: 110 },
      ],
    }
  },
  mounted() {
    this.carregar()
  },
  methods: {
    moeda,
    data,
    async carregar() {
      this.carregando = true
      try {
        const { data } = await api.get(`/pedidos/${this.$route.params.id}`)
        this.pedido = data
      } catch (e) {
        notificarErro(e)
        if (e.response?.status === 404) this.$router.push('/pedidos')
      } finally {
        this.carregando = false
      }
    },
    novoItem() {
      this.itemSelecionado = null
      this.dialogoItem = true
    },
    editarItem(item) {
      this.itemSelecionado = item
      this.dialogoItem = true
    },
    pedirRemocaoItem(item) {
      this.itemSelecionado = item
      this.confirmarItem = true
    },
    async removerItem() {
      this.removendo = true
      try {
        const { data } = await api.delete(`/pedidos/${this.pedido.id}/itens/${this.itemSelecionado.id}`)
        notificar(data.mensagem)
        this.carregar()
      } catch (e) {
        notificarErro(e)
      } finally {
        this.removendo = false
        this.confirmarItem = false
      }
    },
    async removerPedido() {
      this.removendo = true
      try {
        const { data } = await api.delete(`/pedidos/${this.pedido.id}`)
        notificar(data.mensagem)
        this.$router.push('/pedidos')
      } catch (e) {
        notificarErro(e)
      } finally {
        this.removendo = false
        this.confirmarPedido = false
      }
    },
  },
}
</script>
