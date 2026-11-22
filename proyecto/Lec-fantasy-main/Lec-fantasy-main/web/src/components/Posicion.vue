<template>
  <span class="pos" :class="`pos-${posicion}`">{{ posicion }}</span>
</template>

<script setup>
defineProps({ posicion: { type: String, required: true } })
</script>
