<template>
  <Teleport to="body">
    <div v-if="abierto" class="velo" @mousedown.self="$emit('cerrar')">
      <div class="modal" role="dialog" aria-modal="true" :style="{ maxWidth: ancho }">
        <div class="fila-entre cabeza" :class="{ 'sin-titulo': !titulo }">
          <h2 v-if="titulo">{{ titulo }}</h2>
          <button class="x" aria-label="Cerrar" @click="$emit('cerrar')">×</button>
        </div>
        <slot />
      </div>
    </div>
  </Teleport>
</template>

<script setup>
defineProps({
  abierto: Boolean,
  titulo: { type: String, default: '' },
  ancho: { type: String, default: '520px' }
})
defineEmits(['cerrar'])
</script>

<style scoped>
.velo { position: fixed; inset: 0; background: var(--velo); backdrop-filter: blur(2px); display: grid; place-items: center; padding: 16px; z-index: 100; }
.modal { width: 100%; background: var(--tarjeta); border: 1px solid var(--acento-borde); border-radius: 14px; padding: 24px; box-shadow: var(--sombra); max-height: 90vh; overflow-y: auto; }
.cabeza { margin-bottom: 14px; flex-wrap: nowrap; }
.cabeza h2 { margin: 0; }
.modal { position: relative; }
.cabeza.sin-titulo { position: absolute; top: 14px; right: 16px; margin: 0; }
.x { background: none; border: 0; color: var(--texto-2); font-size: 1.8rem; line-height: 1; cursor: pointer; }
</style>
