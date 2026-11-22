<template>
  <div v-if="mensaje" class="alerta" :class="`alerta-${tipo}`" role="alert">{{ mensaje }}</div>
</template>

<script setup>
defineProps({
  mensaje: { type: String, default: '' },
  tipo: { type: String, default: 'error' }
})
</script>
