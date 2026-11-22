<template>
  <div>
    <h1>Jornadas</h1>
    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" />
    <div v-if="!cargando && !jornadas.length" class="tarjeta vacio">Todavía no hay jornadas.</div>
    <div class="pila">
      <div v-for="j in jornadas" :key="j.id" class="tarjeta">
        <div class="fila-entre clicable" @click="alternar(j)">
          <div>
            <h3 class="sin-margen">{{ j.nombre }}</h3>
            <span class="tenue">{{ fecha(j.fechaInicio) }} – {{ fecha(j.fechaFin) }}</span>
          </div>
          <div class="fila">
            <span class="chip" :class="claseEstado[j.estado]">{{ ESTADO_JORNADA[j.estado] }}</span>
            <span class="muted">{{ abierta === j.id ? '▲' : '▼' }}</span>
          </div>
        </div>
        <div v-if="abierta === j.id" class="mt">
          <div v-if="!partidas[j.id]" class="cargando">Cargando partidas…</div>
          <div v-else-if="!partidas[j.id].length" class="tenue">Sin partidas importadas todavía.</div>
          <div v-else class="pila partidas">
            <RouterLink v-for="p in partidas[j.id]" :key="p.id" :to="`/partidas/${p.id}`" class="partida">
              <span class="tenue">{{ fechaHora(p.fechaHora) }}</span>
              <span class="enfrentamiento">
                <b :class="{ 'texto-acento': p.ganador?.id === p.equipoAzul?.id }">{{ p.equipoAzul?.nombre || '?' }}</b>
                <span class="muted">vs</span>
                <b :class="{ 'texto-acento': p.ganador?.id === p.equipoRojo?.id }">{{ p.equipoRojo?.nombre || '?' }}</b>
              </span>
              <span class="tenue">{{ duracion(p.duracionSegundos) }}</span>
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'
import { ESTADO_JORNADA, fecha, fechaHora } from '../../utils/formato'

const claseEstado = { PROGRAMADA: 'chip-acento', EN_CURSO: 'chip-naranja', FINALIZADA: 'chip-verde' }
const jornadas = ref([])
const partidas = reactive({})
const abierta = ref(null)
const cargando = ref(true)
const error = ref('')

const duracion = (s) => (s ? `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}` : '')

async function alternar(j) {
  abierta.value = abierta.value === j.id ? null : j.id
  if (abierta.value && !partidas[j.id]) {
    try {
      partidas[j.id] = await api.get(`/jornadas/${j.id}/partidas`)
    } catch (e) {
      error.value = e.message
    }
  }
}

onMounted(async () => {
  try {
    jornadas.value = (await api.get('/jornadas')).slice().reverse()
    const actual = jornadas.value.find((j) => j.estado === 'EN_CURSO') || jornadas.value.find((j) => j.estado === 'FINALIZADA')
    if (actual) alternar(actual)
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
})
</script>

<style scoped>
.clicable { cursor: pointer; }
.sin-margen { margin: 0; }
.partidas { gap: 6px; }
.partida { display: grid; grid-template-columns: 140px 1fr 60px; gap: 12px; align-items: center; padding: 10px 12px; border-radius: 8px; background: var(--fondo); color: var(--texto); }
.partida:hover { text-decoration: none; outline: 1px solid var(--acento); }
.enfrentamiento { display: flex; gap: 10px; justify-content: center; flex-wrap: wrap; }
@media (max-width: 600px) { .partida { grid-template-columns: 1fr; text-align: center; } }
</style>
