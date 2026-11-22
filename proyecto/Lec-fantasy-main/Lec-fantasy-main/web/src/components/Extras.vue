<template>
  <span class="fila extras">
    <span v-if="e.victoria" class="chip chip-verde">V</span>
    <span v-if="e.mvp" class="chip chip-amarillo">MVP</span>
    <span v-if="e.firstBlood" class="chip chip-rojo">FB</span>
    <span v-if="e.pentaKills" class="chip chip-naranja">Penta</span>
    <span v-else-if="e.quadraKills" class="chip chip-naranja">Quadra</span>
    <span v-else-if="e.tripleKills" class="chip chip-naranja">Triple</span>
  </span>
</template>

<script setup>
defineProps({ e: { type: Object, required: true } })
</script>

<style scoped>
.extras { gap: 4px; flex-wrap: nowrap; }
</style>
