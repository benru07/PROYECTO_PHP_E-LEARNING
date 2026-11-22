<template>
  <div>
    <div class="fila-entre mb barra-ligas">
      <div class="pildoras">
        <RouterLink v-for="l in juego.ligas" :key="l.id" :to="`/ligas/${l.id}`" class="pildora"
                    :class="{ activa: String(l.id) === String(route.params.id) }">{{ l.nombre }}</RouterLink>
        <RouterLink v-if="liga && !liga.miEquipoId" :to="`/ligas/${liga.id}`" class="pildora activa">{{ liga.nombre }}</RouterLink>
      </div>
      <select v-model="jornadaId" class="input selector" aria-label="Jornada" @change="cargarClasificacion">
        <option :value="null">Clasificación general</option>
        <option v-for="j in finalizadas" :key="j.id" :value="j.id">{{ j.nombre }}</option>
      </select>
    </div>

    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" class="mb" />

    <div v-if="liga" class="con-lateral">
      <section class="tarjeta">
        <div class="tarjeta-cabeza">
          <div>
            <h2>{{ liga.nombre }}</h2>
            <span class="micro">
              {{ enteroEs(liga.participantes) }} mánagers · presupuesto {{ millones(liga.presupuestoInicial) }}
              <template v-if="liga.creador"> · creada por {{ liga.creador }}</template>
            </span>
          </div>
          <div v-if="liga.codigoInvitacion && !liga.global" class="fila invitar">
            <span class="micro ocultar-movil">Código</span>
            <span class="codigo">{{ liga.codigoInvitacion }}</span>
            <button class="btn btn-fantasma btn-peque" @click="copiar">{{ copiado ? '¡Copiado!' : 'Copiar' }}</button>
          </div>
        </div>

        <div class="tabla-scroll">
          <table class="tabla">
            <thead>
              <tr>
                <th>#</th><th>Mánager · Equipo</th>
                <th class="num" :class="{ sec: !jornadaId }">{{ jornadaId ? 'Pts jornada' : 'Últ. jornada' }}</th>
                <th v-if="!jornadaId" class="num">Pts totales</th>
                <th class="num sec">Valor plantilla</th><th class="sec">Racha</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="f in clasificacion" :key="f.equipoId" class="clicable"
                  :class="{ destacada: f.equipoId === liga.miEquipoId, seleccionada: f.equipoId === rivalId && f.equipoId !== liga.miEquipoId }"
                  @click="elegirRival(f)">
                <td class="pos-fila" :class="{ 'texto-acento': f.equipoId === liga.miEquipoId }">{{ f.posicion }}</td>
                <td>
                  <span class="manager">
                    <span class="avatar" :class="{ lleno: f.equipoId === liga.miEquipoId }">{{ iniciales(f.usuario) }}</span>
                    <span class="nombres"><b>{{ f.usuario }}</b><span class="tenue">{{ f.equipo }}<template v-if="f.equipoId === liga.miEquipoId"> (tú)</template></span></span>
                  </span>
                </td>
                <td class="num" :class="{ sec: !jornadaId }">{{ jornadaId ? puntos(f.puntos) : puntos(f.puntosUltimaJornada) }}</td>
                <td v-if="!jornadaId" class="num negrita">{{ puntos(f.puntos) }}</td>
                <td class="num sec">{{ millones(f.valorPlantilla) }}</td>
                <td class="sec">
                  <span class="racha" :title="(f.racha || []).map((r) => `${r.jornada}: ${puntos(r.puntos)}`).join('\n')">
                    <span v-for="r in f.racha || []" :key="r.jornadaId" :class="r.porEncimaDeLaMedia ? 'bien' : 'mal'" />
                    <span v-if="!f.racha?.length" class="tenue">—</span>
                  </span>
                </td>
              </tr>
              <tr v-if="!clasificacion.length"><td colspan="6" class="vacio">Sin datos para esta jornada.</td></tr>
            </tbody>
          </table>
        </div>
        <p class="tenue leyenda sec">Racha: últimas 5 jornadas. <span class="cuadro bien" /> por encima de la media de la liga · <span class="cuadro mal" /> por debajo.</p>

        <div v-if="!liga.global" class="fila mt">
          <button v-if="liga.miEquipoId" class="btn btn-texto-rojo btn-peque" @click="abandonar">Abandonar liga</button>
          <button v-if="liga.creadorId === auth.usuario?.id || auth.esAdmin" class="btn btn-rojo btn-peque" @click="eliminar">Eliminar liga</button>
        </div>
      </section>

      <aside class="tarjeta rival">
        <template v-if="rival">
          <span class="micro">{{ rival.usuarioId === auth.usuario?.id ? 'Tu equipo' : 'Detalle del rival' }}</span>
          <h2 class="sin-margen">{{ rival.nombre }}</h2>
          <span class="tenue">{{ rival.usuario }} · {{ ordinal(rival.posicion) }} · {{ puntos(rival.puntosTotales) }} pts</span>

          <span class="micro bloque">Su alineación</span>
          <div v-for="s in TITULARES" :key="s" class="linea-rival">
            <span class="pos">{{ s }}</span>
            <template v-if="slotRival[s]">
              <RouterLink :to="`/jugadores/${slotRival[s].jugador.id}`" class="nombre-rival">
                {{ slotRival[s].jugador.nickname }}
                <span v-if="enComun.has(slotRival[s].jugador.id)" class="chip chip-acento">En común</span>
              </RouterLink>
              <span class="tenue">{{ siglasEquipo(slotRival[s].jugador.equipo) }}</span>
              <b class="num">{{ puntos(slotRival[s].jugador.puntosTotales) }}</b>
            </template>
            <span v-else class="tenue">Sin titular</span>
          </div>

          <div class="comun">
            <span class="micro">Jugadores en común</span>
            <b>{{ rival.usuarioId === auth.usuario?.id ? '—' : `${enComun.size} · ${nombresComun || 'ninguno'}` }}</b>
          </div>

          <div class="fila mt">
            <RouterLink :to="`/equipos/${rival.id}`" class="btn btn-peque">Ver equipo</RouterLink>
            <button v-if="rival.usuarioId !== auth.usuario?.id" class="btn btn-fantasma btn-peque"
                    @click="reportado = { usuarioId: rival.usuarioId, usuario: rival.usuario }">Reportar</button>
          </div>
        </template>
        <div v-else-if="cargandoRival" class="cargando">Cargando equipo…</div>
        <p v-else class="tenue">Pulsa un equipo de la clasificación para ver su alineación.</p>
      </aside>
    </div>

    <ModalReporte :abierto="!!reportado" :usuario-id="reportado?.usuarioId" :usuario="reportado?.usuario || ''"
                  @cerrar="reportado = null" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import ModalReporte from '../../components/ModalReporte.vue'
import { api } from '../../api/cliente'
import { useAuth } from '../../stores/auth'
import { useJuego } from '../../stores/juego'
import { enteroEs, iniciales, millones, ordinal, puntos } from '../../utils/formato'
import { siglasEquipo } from '../../utils/equipos'

const TITULARES = ['TOP', 'JNG', 'MID', 'ADC', 'SUP']
const auth = useAuth()
const juego = useJuego()
const route = useRoute()
const router = useRouter()
const liga = ref(null)
const clasificacion = ref([])
const jornadas = ref([])
const jornadaId = ref(null)
const cargando = ref(true)
const error = ref('')
const copiado = ref(false)
const reportado = ref(null)
const rivalId = ref(null)
const rival = ref(null)
const cargandoRival = ref(false)
const finalizadas = computed(() => jornadas.value.filter((j) => j.estado === 'FINALIZADA'))

const slotRival = computed(() => Object.fromEntries((rival.value?.plantilla || []).map((p) => [p.slot, p])))
const miPlantilla = computed(() => new Set((juego.equipos[liga.value?.miEquipoId]?.plantilla || []).map((p) => p.jugador.id)))
const enComun = computed(() => new Set((rival.value?.plantilla || []).map((p) => p.jugador.id).filter((id) => miPlantilla.value.has(id))))
const nombresComun = computed(() => (rival.value?.plantilla || []).filter((p) => enComun.value.has(p.jugador.id)).map((p) => p.jugador.nickname).join(', '))

async function elegirRival(f) {
  if (rivalId.value === f.equipoId) return router.push(`/equipos/${f.equipoId}`)
  rivalId.value = f.equipoId
  cargandoRival.value = true
  try {
    rival.value = await api.get(`/equipos/${f.equipoId}`)
  } catch (e) {
    rival.value = null
    error.value = e.message
  } finally {
    cargandoRival.value = false
  }
}

async function cargarClasificacion() {
  try {
    clasificacion.value = await api.get(`/ligas/${route.params.id}/clasificacion`, { jornadaId: jornadaId.value })
  } catch (e) {
    error.value = e.message
  }
}

async function cargar() {
  cargando.value = true
  error.value = ''
  rival.value = null
  rivalId.value = null
  try {
    ;[liga.value, jornadas.value] = await Promise.all([
      api.get(`/ligas/${route.params.id}`), api.get('/jornadas'), juego.cargar().catch(() => {})
    ])
    await cargarClasificacion()
    // Se muestra de entrada el primer rival (el líder, o el segundo si el líder eres tú)
    const primero = clasificacion.value.find((f) => f.equipoId !== liga.value.miEquipoId) || clasificacion.value[0]
    if (primero) elegirRival(primero)
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)
watch(() => route.params.id, (id) => { if (id && route.name === 'liga') { jornadaId.value = null; cargar() } })

async function copiar() {
  try {
    await navigator.clipboard.writeText(liga.value.codigoInvitacion)
    copiado.value = true
    setTimeout(() => (copiado.value = false), 2000)
  } catch {
    copiado.value = false
  }
}

async function abandonar() {
  if (!confirm('¿Seguro que quieres abandonar la liga? Perderás tu equipo y tus puntos en ella.')) return
  try {
    await api.del(`/ligas/${liga.value.id}/abandonar`)
    await juego.cargar(true)
    router.push('/ligas')
  } catch (e) {
    error.value = e.message
  }
}

async function eliminar() {
  if (!confirm('¿Eliminar la liga para todos sus participantes? No se puede deshacer.')) return
  try {
    await api.del(`/ligas/${liga.value.id}`)
    await juego.cargar(true)
    router.push('/ligas')
  } catch (e) {
    error.value = e.message
  }
}
</script>

<style scoped>
.barra-ligas { flex-wrap: nowrap; }
.barra-ligas .pildoras { min-width: 0; }
.selector { width: auto; min-width: 190px; flex-shrink: 0; font-size: 0.8rem; font-weight: 700; }
.invitar { gap: 8px; }
.pos-fila { font-family: var(--fuente-titulo); font-weight: 800; font-size: 1.05rem; width: 36px; }
.manager { display: flex; align-items: center; gap: 10px; }
.manager .avatar { width: 30px; height: 30px; font-size: 0.66rem; }
.nombres { display: flex; flex-direction: column; line-height: 1.25; }
.nombres .tenue { font-size: 0.76rem; }
.racha { display: inline-flex; gap: 3px; }
.racha span, .cuadro { width: 9px; height: 14px; border-radius: 2px; display: inline-block; vertical-align: middle; }
.bien { background: var(--verde); }
.mal { background: var(--rojo); }
.leyenda { margin: 12px 0 0; font-size: 0.74rem; }
.rival { display: flex; flex-direction: column; gap: 4px; }
.bloque { margin-top: 16px; margin-bottom: 4px; }
.linea-rival { display: grid; grid-template-columns: 40px 1fr auto 48px; gap: 8px; align-items: center; padding: 8px 0; border-top: 1px solid var(--borde); font-size: 0.86rem; }
.nombre-rival { color: var(--texto); font-weight: 700; display: inline-flex; gap: 6px; align-items: center; flex-wrap: wrap; }
.comun { display: flex; flex-direction: column; gap: 2px; margin-top: 10px; padding: 12px; border-radius: var(--radio-peque); background: var(--acento-suave); border: 1px solid var(--acento-borde); }
.comun b { font-size: 0.86rem; }
@media (max-width: 760px) {
  .barra-ligas { flex-wrap: wrap; }
  .selector { width: 100%; }
  .sec { display: none; }
  .tabla :deep(td), .tabla :deep(th) { padding: 9px 6px; }
}
</style>
