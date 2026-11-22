<template>
  <div class="ficha" :class="{ vacia: !item, seleccionada, destino, bloqueada: !editable }" :style="estilo"
       role="button" tabindex="0" :aria-label="item ? `${item.jugador.nickname}, ${hueco}` : `Hueco libre de ${hueco}`"
       @click="$emit('pulsar')" @keydown.enter.prevent="$emit('pulsar')" @keydown.space.prevent="$emit('pulsar')">
    <span class="rol">{{ hueco }}</span>
    <template v-if="item">
      <span class="avatar anillo grande">{{ iniciales(item.jugador.nickname) }}</span>
      <span class="nombre">{{ item.jugador.nickname }}</span>
      <span class="meta">
        <span class="punto" :style="{ background: colorEquipo(item.jugador.equipo) }" />
        {{ siglasEquipo(item.jugador.equipo) }} · {{ puntos(item.jugador.puntosTotales) }} pts
      </span>
      <span v-if="mejora" class="mejora" :title="`Mejora: ${mejora}`"><Icono nombre="estrella" :tam="12" /></span>
    </template>
    <template v-else>
      <span class="avatar vacio-av"><Icono nombre="mas" :tam="18" /></span>
      <RouterLink v-if="editable && rutaMercado" :to="rutaMercado" class="fichar" @click.stop>Fichar {{ hueco }}</RouterLink>
      <span v-else class="libre">Hueco libre</span>
    </template>
  </div>
</template>

<script setup>
import Icono from './Icono.vue'
import { iniciales, puntos } from '../utils/formato'
import { colorEquipo, siglasEquipo } from '../utils/equipos'

defineProps({
  hueco: { type: String, required: true },
  item: { type: Object, default: null },
  editable: Boolean,
  seleccionada: Boolean,
  destino: Boolean,
  mejora: { type: String, default: '' },
  rutaMercado: { type: [String, Object], default: null },
  estilo: { type: Object, default: () => ({}) }
})
defineEmits(['pulsar'])
</script>

<style scoped>
.ficha {
  position: absolute; transform: translate(-50%, -50%); width: 148px; padding: 16px 10px 12px;
  display: flex; flex-direction: column; align-items: center; gap: 6px; text-align: center;
  background: color-mix(in srgb, var(--tarjeta) 92%, transparent); border: 1px solid var(--borde-2); border-radius: var(--radio);
  cursor: pointer; transition: border-color 0.15s, box-shadow 0.15s, transform 0.15s; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(4px);
}
.ficha:hover { border-color: var(--texto-3); }
.ficha:focus-visible { outline: 2px solid var(--acento); outline-offset: 2px; }
.ficha.bloqueada { cursor: default; }
.ficha.vacia { border-style: dashed; background: color-mix(in srgb, var(--tarjeta) 70%, transparent); }
.ficha.seleccionada { border-color: var(--acento); box-shadow: 0 0 0 3px var(--acento-medio), 0 8px 24px rgba(0, 0, 0, 0.3); transform: translate(-50%, -50%) scale(1.04); z-index: 2; }
.ficha.destino { border-color: var(--acento-borde); border-style: dashed; }
.rol {
  position: absolute; top: -9px; left: 10px; padding: 2px 7px; border-radius: 4px;
  background: var(--acento); color: var(--acento-texto); font-size: 0.6rem; font-weight: 900; letter-spacing: 0.08em;
}
.avatar.grande { width: 52px; height: 52px; font-size: 1rem; border-width: 2px; }
.vacio-av { width: 44px; height: 44px; color: var(--texto-3); background: transparent; border: 1.5px dashed var(--borde-2); }
.nombre { font-family: var(--fuente-titulo); font-weight: 800; text-transform: uppercase; letter-spacing: 0.04em; font-size: 0.92rem; line-height: 1.1; word-break: break-word; }
.meta { display: inline-flex; align-items: center; gap: 5px; font-size: 0.68rem; color: var(--texto-3); white-space: nowrap; }
.punto { width: 7px; height: 7px; border-radius: 2px; }
.mejora {
  position: absolute; right: -8px; bottom: -8px; width: 24px; height: 24px; border-radius: 6px; display: grid; place-items: center;
  background: var(--acento); color: var(--acento-texto); box-shadow: 0 0 0 3px var(--fondo);
}
.fichar { font-size: 0.66rem; font-weight: 800; letter-spacing: 0.1em; text-transform: uppercase; }
.libre { font-size: 0.7rem; color: var(--texto-3); }
@media (max-width: 760px) {
  .ficha { width: 96px; padding: 12px 6px 8px; gap: 3px; }
  .avatar.grande { width: 36px; height: 36px; font-size: 0.74rem; }
  .vacio-av { width: 32px; height: 32px; }
  .nombre { font-size: 0.64rem; max-width: 100%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
  .meta { font-size: 0.58rem; }
  .rol { font-size: 0.52rem; left: 6px; }
  .mejora { width: 18px; height: 18px; right: -6px; bottom: -6px; }
}
</style>
