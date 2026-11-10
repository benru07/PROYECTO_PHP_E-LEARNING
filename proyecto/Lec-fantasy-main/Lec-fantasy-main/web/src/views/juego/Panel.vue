<template>
  <div>
    <Alerta v-if="route.query.bienvenida" tipo="ok" class="mb"
            :mensaje="`¡Bienvenido, ${auth.usuario?.nombre}! Ya estás en la Liga Global. Empieza fichando a tus cinco titulares.`" />
    <Alerta :mensaje="error" class="mb" />

    <div class="panel-grid">
        <div class="saludo area-saludo">
          <h1>Hola, {{ auth.usuario?.nombre }}</h1>
          <span class="micro">{{ subtitulo }}</span>
        </div>

        <div class="grid grid-4 area-stats">
          <div class="tarjeta estadistica">
            <span class="etiqueta">Puntos última jornada</span>
            <span class="valor">{{ ultimaPuntuacion ? puntos(ultimaPuntuacion.puntosTotales) : '—' }}</span>
            <span class="pie">{{ ultimaPuntuacion ? ultimaPuntuacion.jornada : 'Aún no has puntuado' }}</span>
          </div>
          <div class="tarjeta estadistica">
            <span class="etiqueta">Clasificación global</span>
            <span class="valor">{{ equipoGlobal ? '#' + enteroEs(equipoGlobal.posicion) : '—' }}</span>
            <span class="pie">{{ ligaGlobal ? `de ${enteroEs(ligaGlobal.participantes)} mánagers` : 'Liga Global' }}</span>
          </div>
          <div class="tarjeta estadistica">
            <span class="etiqueta">Liga privada</span>
            <span class="valor">{{ mejorPrivada ? ordinal(mejorPrivada.equipo.posicion) : '—' }}</span>
            <span class="pie">{{ mejorPrivada ? `en ${mejorPrivada.liga.nombre}` : 'Únete a una liga con amigos' }}</span>
          </div>
          <div class="tarjeta estadistica">
            <span class="etiqueta">Valor plantilla</span>
            <span class="valor">{{ activo ? millones(activo.valorPlantilla) : '—' }}</span>
            <span class="pie">{{ activo ? `${activo.plantilla.length}/7 jugadores` : '' }}</span>
          </div>
        </div>

        <!-- Partidos de la jornada -->
        <section class="tarjeta area-partidos">
          <div class="tarjeta-cabeza">
            <h2>{{ tituloPartidos }}</h2>
            <RouterLink to="/jornadas" class="enlace-acento">Ver calendario completo →</RouterLink>
          </div>

          <RouterLink v-if="activo" :to="`/equipos/${activo.id}`" class="mi-banda">
            <div>
              <span class="micro">Tu equipo · {{ activo.liga }}</span>
              <b>{{ activo.nombre }}</b>
            </div>
            <div class="derecha">
              <span class="micro">Puntos totales</span>
              <span class="cifra peque">{{ puntos(activo.puntosTotales) }} <small>pts</small></span>
            </div>
          </RouterLink>

          <div v-if="cargando" class="cargando">Cargando…</div>
          <!-- Calendario real de la LEC (PandaScore), si el servidor tiene la key -->
          <template v-else-if="calendario.length">
            <div v-for="p in calendario" :key="'c' + p.id" class="partido" :class="{ vivo: p.estado === 'running' }">
              <span class="tenue hora">
                <span v-if="p.estado === 'running'" class="chip chip-solido">En directo</span>
                <template v-else>{{ fechaHora(p.fecha) }}</template>
              </span>
              <span class="lado" :class="{ gana: p.ganadorId && p.ganadorId === p.equipoA.id }">
                <InsigniaEquipo :equipo="comoEquipo(p.equipoA)" nombre="siglas" />
              </span>
              <span class="vs">
                <template v-if="p.marcadorA !== null && p.estado !== 'not_started'">{{ p.marcadorA }} – {{ p.marcadorB }}</template>
                <template v-else>{{ p.mejorDe > 1 ? `Bo${p.mejorDe}` : 'vs' }}</template>
              </span>
              <span class="lado der" :class="{ gana: p.ganadorId && p.ganadorId === p.equipoB.id }">
                <InsigniaEquipo :equipo="comoEquipo(p.equipoB)" nombre="siglas" />
              </span>
              <span class="mios">
                <template v-if="misEnCalendario(p)">
                  <Icono nombre="estrella" :tam="13" /> {{ misEnCalendario(p) }} de tus jugadores
                </template>
              </span>
            </div>
          </template>
          <div v-else-if="!partidas.length" class="vacio">
            {{ jornadaPartidos ? 'Las partidas aparecerán cuando se importen los resultados.' : 'Todavía no hay jornadas en el calendario.' }}
          </div>
          <RouterLink v-for="p in calendario.length ? [] : partidas" :key="p.id" :to="`/partidas/${p.id}`" class="partido">
            <span class="tenue hora">{{ fechaHora(p.fechaHora) }}</span>
            <span class="lado" :class="{ gana: p.ganador?.id === p.equipoAzul?.id }">
              <InsigniaEquipo :equipo="p.equipoAzul" nombre="siglas" />
            </span>
            <span class="vs">vs</span>
            <span class="lado der" :class="{ gana: p.ganador?.id === p.equipoRojo?.id }">
              <InsigniaEquipo :equipo="p.equipoRojo" nombre="siglas" />
            </span>
            <span class="mios">
              <template v-if="misJugadoresEn(p)">
                <Icono nombre="estrella" :tam="13" /> {{ misJugadoresEn(p) }} de tus jugadores
              </template>
            </span>
          </RouterLink>
        </section>

        <section v-if="noticias.length" class="tarjeta area-noticias">
          <div class="tarjeta-cabeza">
            <h2>Noticias</h2>
            <RouterLink to="/noticias" class="enlace-acento">Ver todas →</RouterLink>
          </div>
          <div class="grid grid-3">
            <RouterLink v-for="n in noticias" :key="n.id" :to="`/noticias/${n.id}`" class="noticia">
              <span class="micro">{{ fecha(n.fechaPublicacion) }}</span>
              <b>{{ n.titulo }}</b>
            </RouterLink>
          </div>
        </section>

        <div class="tarjeta area-cierre">
          <template v-if="juego.enCurso">
            <span class="micro">Jornada en curso</span>
            <div class="cifra grande">{{ juego.enCurso.nombre }}</div>
            <p class="tenue">Mercado y alineaciones cerrados hasta el {{ fecha(juego.enCurso.fechaFin) }}. Tus titulares ya están puntuando.</p>
            <RouterLink to="/mi-equipo" class="btn btn-bloque">Ver mi alineación</RouterLink>
          </template>
          <template v-else-if="juego.proxima">
            <span class="micro">Cierre de alineación</span>
            <div class="cifra grande texto-acento"><CuentaAtras :hasta="juego.proxima.fechaInicio" /></div>
            <p class="tenue">{{ juego.proxima.nombre }} empieza el {{ fecha(juego.proxima.fechaInicio) }}. Ajusta tu once antes del inicio.</p>
            <RouterLink to="/mi-equipo" class="btn btn-bloque">Revisar mi alineación</RouterLink>
          </template>
          <template v-else>
            <span class="micro">Mercado abierto</span>
            <p class="tenue mt">No hay jornadas programadas todavía. Aprovecha para completar tu plantilla.</p>
            <RouterLink to="/mercado" class="btn btn-bloque">Ir al mercado</RouterLink>
          </template>
        </div>

        <div class="tarjeta area-ligas">
          <h2>Mis ligas</h2>
          <div class="pila ligas">
            <RouterLink v-for="(l, i) in juego.ligas" :key="l.id" :to="`/ligas/${l.id}`" class="liga">
              <span class="avatar cuadrado" :class="{ lleno: l.miEquipoId === juego.activoId }">{{ iniciales(l.nombre) }}</span>
              <span class="liga-texto">
                <b>{{ l.nombre }}</b>
                <span class="tenue">{{ enteroEs(l.participantes) }} mánagers · {{ l.global ? 'Global' : l.tipo === 'PRIVADO' ? 'Privada' : 'Pública' }}</span>
              </span>
              <span class="cifra peque" :class="{ 'texto-acento': i === 0 || l.miEquipoId === juego.activoId }">
                {{ posicionEn(l) }}
              </span>
            </RouterLink>
            <RouterLink to="/ligas" class="btn btn-discontinuo btn-bloque"><Icono nombre="mas" :tam="14" /> Crear o unirse a una liga</RouterLink>
          </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import CuentaAtras from '../../components/CuentaAtras.vue'
import Icono from '../../components/Icono.vue'
import InsigniaEquipo from '../../components/InsigniaEquipo.vue'
import { api } from '../../api/cliente'
import { useAuth } from '../../stores/auth'
import { useJuego } from '../../stores/juego'
import { enteroEs, fecha, fechaHora, iniciales, millones, ordinal, puntos } from '../../utils/formato'
import { siglasEquipo } from '../../utils/equipos'

const auth = useAuth()
const juego = useJuego()
const route = useRoute()
const noticias = ref([])
const partidas = ref([])
const calendario = ref([])
const puntuaciones = ref([])
const cargando = ref(true)
const error = ref('')

const activo = computed(() => juego.equipoActivo)
const ligaGlobal = computed(() => juego.ligas.find((l) => l.global))
const equipoGlobal = computed(() => ligaGlobal.value && juego.equipos[ligaGlobal.value.miEquipoId])
const mejorPrivada = computed(() => juego.ligas
  .filter((l) => !l.global && juego.equipos[l.miEquipoId])
  .map((l) => ({ liga: l, equipo: juego.equipos[l.miEquipoId] }))
  .sort((a, b) => a.equipo.posicion - b.equipo.posicion)[0])
const ultimaPuntuacion = computed(() => puntuaciones.value[puntuaciones.value.length - 1])
// Partidos: la jornada en juego o, si no hay, los últimos resultados
const jornadaPartidos = computed(() => juego.enCurso || juego.ultimaFinalizada)
const tituloPartidos = computed(() => {
  if (calendario.value.length) return juego.proxima ? `Próximos partidos · ${juego.proxima.nombre}` : 'Próximos partidos'
  if (!jornadaPartidos.value) return 'Partidos'
  return `${juego.enCurso ? 'En juego' : 'Últimos resultados'} · ${jornadaPartidos.value.nombre}`
})
const subtitulo = computed(() => {
  const j = juego.enCurso || juego.proxima || juego.ultimaFinalizada
  return ['LEC Fantasy', j?.nombre, juego.ligaActiva?.nombre].filter(Boolean).join(' · ')
})
const misEquiposPro = computed(() => {
  const mapa = {}
  activo.value?.plantilla.forEach((p) => {
    const id = p.jugador.equipo?.id
    if (id) mapa[id] = (mapa[id] || 0) + 1
  })
  return mapa
})
// En el calendario los equipos vienen de PandaScore: se cruzan por siglas
const misPorSiglas = computed(() => {
  const mapa = {}
  activo.value?.plantilla.forEach((p) => {
    const s = siglasEquipo(p.jugador.equipo)
    mapa[s] = (mapa[s] || 0) + 1
  })
  return mapa
})
const comoEquipo = (e) => ({ nombre: e.nombre, siglas: e.siglas, logoUrl: e.logoUrl })
const misEnCalendario = (p) => (misPorSiglas.value[siglasEquipo(comoEquipo(p.equipoA))] || 0) +
  (misPorSiglas.value[siglasEquipo(comoEquipo(p.equipoB))] || 0)
const misJugadoresEn = (p) => (misEquiposPro.value[p.equipoAzul?.id] || 0) + (misEquiposPro.value[p.equipoRojo?.id] || 0)
const posicionEn = (l) => {
  const e = juego.equipos[l.miEquipoId]
  if (!e) return '—'
  return l.global ? '#' + enteroEs(e.posicion) : ordinal(e.posicion)
}

async function cargarDependientes() {
  const peticiones = [
    jornadaPartidos.value ? api.get(`/jornadas/${jornadaPartidos.value.id}/partidas`) : Promise.resolve([]),
    activo.value ? api.get(`/equipos/${activo.value.id}/puntuaciones`) : Promise.resolve([])
  ]
  ;[partidas.value, puntuaciones.value] = await Promise.all(peticiones)
}

onMounted(async () => {
  try {
    const [ns, cal] = await Promise.all([
      api.get('/noticias', { tamano: 3 }).catch(() => ({ contenido: [] })),
      api.get('/calendario').catch(() => []),
      juego.cargar(true)
    ])
    noticias.value = ns.contenido
    // Partidos de hoy en adelante (y los que se están jugando), máximo 8
    calendario.value = cal.filter((p) => p.estado === 'running' || p.estado === 'not_started' || p.estado === 'postponed').slice(0, 8)
    await cargarDependientes()
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
})
watch(() => juego.activoId, () => cargarDependientes().catch(() => {}))
</script>

<style scoped>
.saludo { display: flex; align-items: baseline; gap: 18px; flex-wrap: wrap; }
.panel-grid {
  display: grid; gap: 16px; align-items: start; grid-template-columns: minmax(0, 1fr) 340px;
  grid-template-areas: 'saludo cierre' 'stats cierre' 'partidos ligas' 'noticias ligas';
}
.area-saludo { grid-area: saludo; align-self: end; }
.area-stats { grid-area: stats; align-self: end; }
.area-cierre { grid-area: cierre; }
.area-partidos { grid-area: partidos; }
.area-noticias { grid-area: noticias; }
.area-ligas { grid-area: ligas; }
@media (max-width: 1100px) {
  .panel-grid { grid-template-columns: minmax(0, 1fr); grid-template-areas: 'saludo' 'cierre' 'stats' 'partidos' 'ligas' 'noticias'; }
}
.saludo h1 { margin: 0; }
.enlace-acento { font-size: 0.72rem; font-weight: 700; letter-spacing: 0.06em; white-space: nowrap; }
.mi-banda {
  display: flex; justify-content: space-between; align-items: center; gap: 12px; padding: 12px 16px; margin-bottom: 8px;
  border-radius: var(--radio-peque); background: var(--acento-suave); border: 1px solid var(--acento-borde); color: var(--texto);
}
.mi-banda:hover { text-decoration: none; }
.mi-banda > div { display: flex; flex-direction: column; }
.cifra.peque { font-size: 1.3rem; }
.cifra.peque small { font-family: var(--fuente); font-size: 0.7rem; color: var(--texto-3); }
.cifra.grande { font-size: 2.3rem; margin: 8px 0 6px; }
.partido {
  display: grid; grid-template-columns: 120px 1fr 32px 1fr 170px; align-items: center; gap: 10px;
  padding: 11px 4px; border-top: 1px solid var(--borde); color: var(--texto); font-weight: 700;
}
.partido:hover { text-decoration: none; background: var(--tarjeta-2); }
.lado { color: var(--texto-2); }
.lado.der { justify-self: end; }
.lado.der :deep(.insignia-equipo) { flex-direction: row-reverse; }
.lado.gana { color: var(--texto); }
.lado.gana :deep(.nombre)::after { content: ' ✓'; color: var(--acento-tinta); }
.vs { text-align: center; color: var(--texto-3); font-size: 0.72rem; font-weight: 600; }
.mios { display: flex; align-items: center; gap: 5px; justify-content: flex-end; color: var(--acento-tinta); font-size: 0.72rem; font-weight: 700; }
.hora { font-weight: 500; }
.partido.vivo { background: var(--acento-suave); }
.noticia { display: flex; flex-direction: column; gap: 6px; padding: 14px; border-radius: var(--radio-peque); background: var(--tarjeta-2); color: var(--texto); border: 1px solid var(--borde); }
.noticia:hover { text-decoration: none; border-color: var(--acento-borde); }
.ligas { gap: 8px; }
.liga { display: flex; align-items: center; gap: 12px; padding: 10px; border-radius: var(--radio-peque); background: var(--tarjeta-2); color: var(--texto); border: 1px solid transparent; }
.liga:hover { text-decoration: none; border-color: var(--borde-2); }
.avatar.cuadrado { border-radius: 8px; width: 38px; height: 38px; font-size: 0.74rem; }
.liga-texto { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.liga-texto b { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-size: 0.9rem; }
.liga-texto .tenue { font-size: 0.74rem; }
@media (max-width: 760px) {
  .partido { grid-template-columns: 1fr 26px 1fr; row-gap: 4px; }
  .hora { grid-column: 1 / -1; font-size: 0.74rem; }
  .mios { grid-column: 1 / -1; justify-content: flex-start; }
  .mios:empty { display: none; }
  .cifra.grande { font-size: 1.9rem; }
}
</style>
