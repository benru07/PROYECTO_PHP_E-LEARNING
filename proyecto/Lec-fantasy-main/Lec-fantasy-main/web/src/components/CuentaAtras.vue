<template>
  <span class="cuenta-atras">{{ texto }}</span>
</template>

<script setup>
import { computed, onBeforeUnmount, ref } from 'vue'

const props = defineProps({
  /** Fecha ISO ("2026-01-24") o con hora. Las fechas sin hora cuentan hasta las 00:00 de ese día. */
  hasta: { type: String, required: true },
  corto: Boolean
})

const ahora = ref(Date.now())
const reloj = setInterval(() => (ahora.value = Date.now()), 1000)
onBeforeUnmount(() => clearInterval(reloj))

const objetivo = computed(() =>
  props.hasta.length <= 10 ? new Date(`${props.hasta}T00:00:00`).getTime() : new Date(props.hasta).getTime())

const texto = computed(() => {
  let s = Math.max(0, Math.floor((objetivo.value - ahora.value) / 1000))
  const d = Math.floor(s / 86400)
  s %= 86400
  const h = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  const seg = s % 60
  const dos = (n) => String(n).padStart(2, '0')
  if (props.corto) return d ? `${d}d ${dos(h)}:${dos(m)}` : `${dos(h)}:${dos(m)}:${dos(seg)}`
  return `${d ? d + 'd ' : ''}${dos(h)}:${dos(m)}:${dos(seg)}`
})
</script>

<style scoped>
.cuenta-atras { font-variant-numeric: tabular-nums; }
</style>
