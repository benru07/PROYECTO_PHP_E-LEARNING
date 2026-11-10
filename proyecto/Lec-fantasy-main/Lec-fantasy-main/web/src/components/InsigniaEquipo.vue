<template>
  <span class="insignia-equipo" :class="{ conNombre: nombre }">
    <img v-if="equipo?.logoUrl && !fallo" :src="equipo.logoUrl" alt="" class="logo-eq" @error="fallo = true" />
    <span v-else class="caja" :style="{ background: color, color: textoColor }">{{ siglas }}</span>
    <span v-if="nombre" class="nombre">{{ textoNombre }}</span>
  </span>
</template>

<script setup>
import { computed, ref } from 'vue'
import { colorEquipo, siglasEquipo } from '../utils/equipos'

const props = defineProps({
  equipo: { type: Object, default: null },
  nombre: { type: [Boolean, String], default: false }
})
const fallo = ref(false)
const siglas = computed(() => siglasEquipo(props.equipo))
const color = computed(() => (props.equipo ? colorEquipo(props.equipo) : 'var(--tarjeta-3)'))
// Amarillos y dorados con texto oscuro; el resto, blanco
const textoColor = computed(() => (/^#(F2DB00|F5D300|C9A227|D4A017)$/i.test(color.value) ? '#111' : '#fff'))
const textoNombre = computed(() => (props.nombre === 'siglas' ? siglas.value : props.equipo?.nombre || 'Sin equipo'))
</script>

<style scoped>
.insignia-equipo { display: inline-flex; align-items: center; gap: 8px; min-width: 0; vertical-align: middle; }
.caja, .logo-eq { width: 26px; height: 20px; border-radius: 4px; flex-shrink: 0; }
.caja { display: grid; place-items: center; font-size: 0.5rem; font-weight: 900; letter-spacing: 0.02em; }
.logo-eq { object-fit: contain; background: var(--tarjeta-3); }
.nombre { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
</style>
