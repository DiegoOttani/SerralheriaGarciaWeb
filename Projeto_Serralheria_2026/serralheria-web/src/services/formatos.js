// Formatação de valores e máscaras de digitação

const formatoMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

export function moeda(valor) {
  return formatoMoeda.format(Number(valor) || 0)
}

// '2026-10-01' -> '01/10/2026' (sem usar Date, para não sofrer com fuso horário)
export function data(valor) {
  if (!valor) return ''
  const [ano, mes, dia] = valor.split('-')
  return `${dia}/${mes}/${ano}`
}

function digitos(valor, maximo) {
  return (valor || '').replace(/\D/g, '').slice(0, maximo)
}

// (19) 99999-8888 ou (19) 3333-4444
export function mascaraTelefone(valor) {
  const d = digitos(valor, 11)
  if (d.length <= 2) return d.length ? `(${d}` : ''
  if (d.length <= 6) return `(${d.slice(0, 2)}) ${d.slice(2)}`
  if (d.length <= 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`
  return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`
}

// 000.000.000-00
export function mascaraCpf(valor) {
  const d = digitos(valor, 11)
  return d
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

// 00.000.000/0000-00
export function mascaraCnpj(valor) {
  const d = digitos(valor, 14)
  return d
    .replace(/^(\d{2})(\d)/, '$1.$2')
    .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/\.(\d{3})(\d)/, '.$1/$2')
    .replace(/(\d{4})(\d)/, '$1-$2')
}

// Até 11 dígitos usa máscara de CPF; acima disso, de CNPJ
export function mascaraCpfCnpj(valor) {
  return digitos(valor, 14).length <= 11 ? mascaraCpf(valor) : mascaraCnpj(valor)
}

// Regras de validação usadas nos formulários (Vuetify)
export const regras = {
  obrigatorio: (v) => (v !== null && v !== undefined && String(v).trim() !== '') || 'Campo obrigatório',
  email: (v) => !v || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || 'E-mail inválido',
  telefone: (v) => !v || [10, 11].includes(digitos(v, 11).length) || 'Telefone deve ter 10 ou 11 dígitos',
  cpf: (v) => !v || digitos(v, 11).length === 11 || 'CPF deve ter 11 dígitos',
  cnpj: (v) => !v || digitos(v, 14).length === 14 || 'CNPJ deve ter 14 dígitos',
  cpfCnpj: (v) => !v || [11, 14].includes(digitos(v, 14).length) || 'Informe 11 (CPF) ou 14 (CNPJ) dígitos',
  naoNegativo: (v) => v === '' || v === null || Number(v) >= 0 || 'Valor não pode ser negativo',
  positivo: (v) => Number(v) > 0 || 'Deve ser maior que zero',
  inteiro: (v) => v === '' || v === null || Number.isInteger(Number(v)) || 'Informe um número inteiro',
}
