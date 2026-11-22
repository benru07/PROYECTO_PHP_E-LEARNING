<template>
  <RouterLink :to="destino" class="logo" aria-label="LEC Fantasy">
    LEC<span class="acento">FANTASY</span><span v-if="admin" class="sufijo">ADMIN</span>
  </RouterLink>
</template>

<script setup>
defineProps({
  destino: { type: [String, Object], default: '/' },
  admin: { type: Boolean, default: false }
})
</script>
