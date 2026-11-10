<template>
  <div class="fondo">
    <div class="tarjeta marco" :style="{ maxWidth: ancho }">
      <div class="centrado"><Logo /></div>
      <slot />
    </div>
  </div>
</template>

<script setup>
import Logo from './Logo.vue'

defineProps({ ancho: { type: String, default: '460px' } })
</script>

<style scoped>
.fondo { min-height: 100vh; display: grid; place-items: center; padding: 40px 16px; }
.marco { width: 100%; padding: 36px 40px; border-radius: 14px; }
@media (max-width: 520px) { .marco { padding: 28px 20px; } }
</style>
