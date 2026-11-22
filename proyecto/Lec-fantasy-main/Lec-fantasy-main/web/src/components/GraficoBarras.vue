<template>
  <div class="barras" :style="{ height: alto + 'px' }">
    <div v-if="datos.length" class="columnas" role="img" :aria-label="descripcion">
      <div v-for="(d, i) in datos" :key="i" class="columna" @mouseenter="activo = i" @mouseleave="activo = null"
           @click="$emit('pulsar', d)">
        <div class="pista">
          <div class="barra" :class="{ tenue: Number(d.valor) <= 0, activa: activo === i }"
               :style="{ height: altura(d.valor) }" />
        </div>
        <span class="eje">{{ d.corta || i + 1 }}</span>
        <div v-if="activo === i" class="tooltip" :class="{ izq: i > datos.length / 2 }">
          <span class="micro">{{ d.etiqueta }}</span>
          <b>{{ formato(d.valor) }}</b>
          <span v-if="d.detalle" class="tenue">{{ d.detalle }}</span>
        </div>
      </div>
    </div>
    <p v-else class="tenue vacio-barras">{{ vacio }}</p>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  /** [{ etiqueta, corta, valor, detalle }] */
  datos: { type: Array, required: true },
  formato: { type: Function, default: (v) => v },
  alto: { type: Number, default: 110 },
  descripcion: { type: String, default: 'Puntos por partida' },
  vacio: { type: String, default: 'Todavía no hay partidas.' }
})
defineEmits(['pulsar'])
const activo = ref(null)
const max = computed(() => Math.max(1, ...props.datos.map((d) => Number(d.valor))))
// Las partidas con puntos negativos o cero se ven como una barra mínima apagada
const altura = (v) => `${Math.max(4, (Math.max(0, Number(v)) / max.value) * 100)}%`
</script>

<style scoped>
.barras { position: relative; }
.columnas { display: flex; gap: 2px; height: 100%; align-items: stretch; }
.columna { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 6px; position: relative; cursor: default; }
.pista { flex: 1; display: flex; align-items: flex-end; }
.barra { width: 100%; background: var(--acento); border-radius: 4px 4px 0 0; transition: filter 0.12s; }
.barra.tenue { background: var(--acento-medio); }
.barra.activa { filter: brightness(1.15); }
.eje { font-size: 0.62rem; color: var(--texto-3); text-align: center; white-space: nowrap; overflow: hidden; }
.tooltip { position: absolute; bottom: calc(100% - 10px); left: 50%; transform: translateX(-50%); background: var(--tarjeta-3); border: 1px solid var(--borde-2); border-radius: 6px; padding: 5px 9px; display: flex; flex-direction: column; font-size: 0.8rem; pointer-events: none; white-space: nowrap; box-shadow: var(--sombra); z-index: 2; }
.tooltip.izq { left: auto; right: 0; transform: none; }
.vacio-barras { margin: 0; padding-top: 30px; text-align: center; }
</style>
