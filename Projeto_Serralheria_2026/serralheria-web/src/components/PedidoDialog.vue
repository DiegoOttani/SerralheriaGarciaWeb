<template>
  <!-- Inclusão/alteração dos dados do pedido (cliente, funcionário, pagamento) -->
  <v-dialog :model-value="modelValue" max-width="600" persistent @update:model-value="$emit('update:modelValue', $event)">
    <v-card :title="pedido ? `Alterar pedido nº ${pedido.id}` : 'Novo pedido'" prepend-icon="mdi-clipboard-text-outline">
      <v-form ref="formulario" @submit.prevent="salvar">
        <v-card-text>
          <v-row dense>
            <v-col cols="12">
              <v-autocomplete
                v-model="form.clienteId"
                :items="clientes"
                item-title="nome"
                item-value="id"
                label="Cliente"
                no-data-text="Nenhum cliente encontrado"
                :rules="[regras.obrigatorio]"
              />
            </v-col>
            <v-col cols="12" sm="7">
              <v-autocomplete
                v-model="form.funcionarioId"
                :items="funcionarios"
                item-title="nome"
                item-value="id"
                label="Funcionário responsável"
                no-data-text="Nenhum funcionário encontrado"
                :rules="[regras.obrigatorio]"
              />
            </v-col>
            <v-col cols="12" sm="5">
              <v-select v-model="form.estadoPagamento" :items="estados" label="Pagamento" :rules="[regras.obrigatorio]" />
            </v-col>
            <v-col cols="12">
              <v-textarea v-model="form.descritivo" label="Descrição do serviço" maxlength="255" counter />
            </v-col>
          </v-row>
          <div v-if="!pedido" class="text-body-2 text-medium-emphasis">
            <v-icon icon="mdi-information-outline" size="small" /> Após salvar, adicione os itens do pedido. O valor total é calculado automaticamente.
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="$emit('update:modelValue', false)">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" type="submit" :loading="salvando">Salvar</v-btn>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-dialog>
</template>

<script>
import api from '../services/api'
import { regras } from '../services/formatos'
import { notificar, notificarErro } from '../services/notificacao'

export default {
  name: 'PedidoDialog',
  props: {
    modelValue: { type: Boolean, default: false },
    pedido: { type: Object, default: null }, // null = novo pedido
  },
  emits: ['update:modelValue', 'salvo'],
  data() {
    return {
      clientes: [],
      funcionarios: [],
      estados: ['Pendente', 'Pago', 'Atrasado'],
      form: {},
      salvando: false,
      regras,
    }
  },
  watch: {
    // Ao abrir o diálogo, preenche o formulário e carrega as listas
    modelValue(aberto) {
      if (!aberto) return
      const p = this.pedido
      this.form = p
        ? { clienteId: p.cliente.id, funcionarioId: p.funcionario.id, estadoPagamento: p.estadoPagamento, descritivo: p.descritivo || '' }
        : { clienteId: null, funcionarioId: null, estadoPagamento: 'Pendente', descritivo: '' }
      this.carregarListas()
    },
  },
  methods: {
    async carregarListas() {
      try {
        const [clientes, funcionarios] = await Promise.all([api.get('/clientes'), api.get('/funcionarios')])
        this.clientes = clientes.data
        this.funcionarios = funcionarios.data
      } catch (e) {
        notificarErro(e)
      }
    },
    async salvar() {
      const { valid } = await this.$refs.formulario.validate()
      if (!valid) return

      this.salvando = true
      try {
        if (this.pedido) {
          const { data } = await api.put(`/pedidos/${this.pedido.id}`, this.form)
          notificar(data.mensagem)
          this.$emit('salvo', this.pedido)
        } else {
          const { data } = await api.post('/pedidos', this.form)
          notificar(`Pedido nº ${data.id} criado! Agora adicione os itens.`)
          this.$emit('salvo', data)
        }
        this.$emit('update:modelValue', false)
      } catch (e) {
        notificarErro(e)
      } finally {
        this.salvando = false
      }
    },
  },
}
</script>
