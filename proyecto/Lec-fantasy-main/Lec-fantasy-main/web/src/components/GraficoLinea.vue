<template>
  <div class="grafico" @mouseleave="activo = null">
    <svg v-if="datos.length > 1" :viewBox="`0 0 ${W} ${H}`" preserveAspectRatio="none" class="lienzo"
         role="img" :aria-label="descripcion" @mousemove="mover">
      <line v-for="g in guias" :key="g.y" :x1="M.i" :x2="W - M.d" :y1="g.y" :y2="g.y" class="guia" />
      <path :d="area" class="area" />
      <path :d="linea" class="linea" vector-effect="non-scaling-stroke" />
      <line v-if="activo !== null" :x1="x(activo)" :x2="x(activo)" :y1="M.a" :y2="H - M.b" class="cruz"
            vector-effect="non-scaling-stroke" />
    </svg>
    <!-- Los puntos van en HTML para que no se deformen con el SVG estirado -->
    <template v-if="datos.length > 1">
      <span v-if="activo !== null" class="punto" :style="posPunto(activo)" />
      <span v-else class="punto final" :style="posPunto(datos.length - 1)" />
      <span v-if="activo === null" class="etiqueta-final" :style="posPunto(datos.length - 1)">
        {{ formato(datos[datos.length - 1].valor) }}
      </span>
      <div v-if="activo !== null" class="tooltip" :style="posTooltip(activo)">
        <span class="micro">{{ datos[activo].etiqueta }}</span>
        <b>{{ formato(datos[activo].valor) }}</b>
      </div>
    </template>
    <p v-else class="tenue sin-datos">{{ vacio }}</p>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  /** [{ etiqueta, valor }] en orden cronológico */
  datos: { type: Array, required: true },
  formato: { type: Function, default: (v) => v },
  descripcion: { type: String, default: 'Evolución' },
  vacio: { type: String, default: 'Sin datos suficientes todavía.' }
})

const W = 400
const H = 120
const M = { i: 4, d: 4, a: 12, b: 10 }
const activo = ref(null)

const rango = computed(() => {
  const vs = props.datos.map((d) => Number(d.valor))
  let min = Math.min(...vs)
  let max = Math.max(...vs)
  if (min === max) { min -= 1; max += 1 }
  const margen = (max - min) * 0.12
  return { min: min - margen, max: max + margen }
})
const x = (i) => M.i + (i * (W - M.i - M.d)) / Math.max(1, props.datos.length - 1)
const y = (v) => M.a + (1 - (Number(v) - rango.value.min) / (rango.value.max - rango.value.min)) * (H - M.a - M.b)
const linea = computed(() => props.datos.map((d, i) => `${i ? 'L' : 'M'}${x(i)},${y(d.valor)}`).join(' '))
const area = computed(() => `${linea.value} L${x(props.datos.length - 1)},${H - M.b} L${x(0)},${H - M.b} Z`)
const guias = computed(() => [0.25, 0.5, 0.75].map((f) => ({ y: M.a + f * (H - M.a - M.b) })))

function mover(ev) {
  const caja = ev.currentTarget.getBoundingClientRect()
  const rel = ((ev.clientX - caja.left) / caja.width) * W
  const i = Math.round(((rel - M.i) / (W - M.i - M.d)) * (props.datos.length - 1))
  activo.value = Math.min(props.datos.length - 1, Math.max(0, i))
}
const posPunto = (i) => ({ left: `${(x(i) / W) * 100}%`, top: `${(y(props.datos[i].valor) / H) * 100}%` })
function posTooltip(i) {
  const izq = (x(i) / W) * 100
  return { left: `${izq}%`, transform: `translateX(${izq > 70 ? '-100%' : izq < 30 ? '0' : '-50%'})` }
}
</script>

<style scoped>
.grafico { position: relative; height: 130px; }
.lienzo { width: 100%; height: 100%; display: block; overflow: visible; cursor: crosshair; }
.guia { stroke: var(--borde); stroke-width: 1; vector-effect: non-scaling-stroke; }
.linea { fill: none; stroke: var(--acento); stroke-width: 2; stroke-linejoin: round; stroke-linecap: round; }
.area { fill: var(--acento-suave); stroke: none; }
.cruz { stroke: var(--texto-3); stroke-width: 1; stroke-dasharray: 3 3; }
.punto { position: absolute; width: 9px; height: 9px; border-radius: 50%; background: var(--acento); border: 2px solid var(--tarjeta); transform: translate(-50%, -50%); pointer-events: none; }
.etiqueta-final { position: absolute; transform: translate(-100%, -170%); font-size: 0.74rem; font-weight: 800; color: var(--texto); pointer-events: none; white-space: nowrap; }
.tooltip { position: absolute; top: -6px; background: var(--tarjeta-3); border: 1px solid var(--borde-2); border-radius: 6px; padding: 5px 9px; display: flex; flex-direction: column; font-size: 0.8rem; pointer-events: none; white-space: nowrap; box-shadow: var(--sombra); z-index: 2; }
.sin-datos { margin: 0; padding-top: 40px; text-align: center; }
</style>
