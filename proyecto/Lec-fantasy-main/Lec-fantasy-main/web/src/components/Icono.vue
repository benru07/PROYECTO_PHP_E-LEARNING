<template>
  <svg class="icono" :width="tam" :height="tam" viewBox="0 0 24 24" fill="none" stroke="currentColor"
       stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
    <path v-for="(d, i) in ICONOS[nombre] || []" :key="i" :d="d" />
  </svg>
</template>

<script setup>
defineProps({
  nombre: { type: String, required: true },
  tam: { type: [Number, String], default: 20 }
})

const ICONOS = {
  inicio: ['M3 10.5 12 3l9 7.5', 'M5 9.5V21h14V9.5', 'M9.5 21v-6h5v6'],
  equipo: ['M12 3 4 6v6c0 4.5 3.4 8 8 9 4.6-1 8-4.5 8-9V6l-8-3Z', 'M9 12l2 2 4-4'],
  mercado: ['M4 7h16l-1.5 12.5a1 1 0 0 1-1 .5h-11a1 1 0 0 1-1-.5L4 7Z', 'M8.5 7V5.5a3.5 3.5 0 0 1 7 0V7'],
  ligas: ['M8 21h8', 'M12 17v4', 'M7 4h10v5a5 5 0 0 1-10 0V4Z', 'M17 6h3v2a3 3 0 0 1-3 3', 'M7 6H4v2a3 3 0 0 0 3 3'],
  estadisticas: ['M4 20V10', 'M10 20V4', 'M16 20v-7', 'M22 20H2'],
  cuenta: ['M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z', 'M4 21a8 8 0 0 1 16 0'],
  buscar: ['M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14Z', 'm20 20-3.5-3.5'],
  reloj: ['M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Z', 'M12 7v5l3 2'],
  flecha: ['M5 12h14', 'm13 6 6 6-6 6'],
  cerrar: ['M6 6l12 12', 'M18 6 6 18'],
  mas: ['M12 5v14', 'M5 12h14'],
  estrella: ['m12 3 2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9L12 3Z'],
  calendario: ['M4 6h16v15H4z', 'M4 10h16', 'M8 3v4', 'M16 3v4'],
  salir: ['M15 4h4v16h-4', 'M10 16l-4-4 4-4', 'M6 12h11'],
  escudo: ['M12 3 4 6v6c0 4.5 3.4 8 8 9 4.6-1 8-4.5 8-9V6l-8-3Z'],
  noticias: ['M5 4h11v16H6a1 1 0 0 1-1-1V4Z', 'M16 8h3v11a1 1 0 0 1-1 1h-2', 'M8 8h5', 'M8 12h5', 'M8 16h3'],
  sol: ['M12 17a5 5 0 1 0 0-10 5 5 0 0 0 0 10Z', 'M12 1v2', 'M12 21v2', 'M4.2 4.2l1.4 1.4', 'M18.4 18.4l1.4 1.4', 'M1 12h2', 'M21 12h2', 'M4.2 19.8l1.4-1.4', 'M18.4 5.6l1.4-1.4'],
  luna: ['M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z'],
  check: ['m5 12 5 5L20 7'],
  intercambio: ['M7 4 3 8l4 4', 'M3 8h14', 'm17 20 4-4-4-4', 'M21 16H7']
}
</script>

<style scoped>
.icono { flex-shrink: 0; display: block; }
</style>
