<template>
  <div>
    <RouterLink to="/jornadas">← Jornadas</RouterLink>
    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" class="mt" />
    <template v-if="datos">
      <h1 class="mt">{{ p.equipoAzul?.nombre }} <span class="muted">vs</span> {{ p.equipoRojo?.nombre }}</h1>
      <p class="muted">
        {{ fechaHora(p.fechaHora) }}<template v-if="p.ganador"> · Gana <b class="texto-acento">{{ p.ganador.nombre }}</b></template>
      </p>
      <div v-for="lado in lados" :key="lado.nombre" class="mt-2">
        <h2>{{ lado.nombre }}</h2>
        <div class="tarjeta sin-relleno tabla-scroll">
          <table class="tabla">
            <thead><tr><th>Rol</th><th>Jugador</th><th>Campeón</th><th>KDA</th><th class="num">CS</th><th class="num">Visión</th><th>Extras</th><th class="num">Puntos</th></tr></thead>
            <tbody>
              <tr v-for="e in lado.lineas" :key="e.jugadorId" class="clicable" @click="router.push(`/jugadores/${e.jugadorId}`)">
                <td><Posicion :posicion="e.posicion" /></td>
                <td class="negrita">{{ e.jugador }}</td>
                <td>{{ e.campeon || '—' }}</td>
                <td>{{ e.kills }}/{{ e.deaths }}/{{ e.assists }}</td>
                <td class="num">{{ e.cs }}</td>
                <td class="num">{{ e.visionScore }}</td>
                <td><Extras :e="e" /></td>
                <td class="num negrita">{{ puntos(e.puntos) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import Posicion from '../../components/Posicion.vue'
import Extras from '../../components/Extras.vue'
import { api } from '../../api/cliente'
import { fechaHora, puntos, POSICIONES } from '../../utils/formato'

const route = useRoute()
const router = useRouter()
const datos = ref(null)
const cargando = ref(true)
const error = ref('')
const p = computed(() => datos.value?.partida)

const lados = computed(() => {
  if (!datos.value) return []
  const orden = (a, b) => POSICIONES.indexOf(a.posicion) - POSICIONES.indexOf(b.posicion)
  const nombres = [...new Set(datos.value.estadisticas.map((e) => e.equipo))]
  return nombres.map((n) => ({ nombre: n || 'Sin equipo', lineas: datos.value.estadisticas.filter((e) => e.equipo === n).sort(orden) }))
})

onMounted(async () => {
  try {
    datos.value = await api.get(`/partidas/${route.params.id}`)
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
})
</script>

<style scoped>
.sin-relleno { padding: 0; overflow: hidden; }
</style>
