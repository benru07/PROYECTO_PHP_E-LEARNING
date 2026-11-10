<template>
  <div>
    <div class="migas">
      <RouterLink to="/mercado">Mercado</RouterLink> / <b>{{ j?.nickname || '…' }}</b>
    </div>
    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" class="mb" />
    <template v-if="datos">
      <section class="tarjeta heroe">
        <img v-if="j.fotoUrl" :src="j.fotoUrl" alt="" class="foto" />
        <span v-else class="avatar anillo foto">{{ iniciales(j.nickname) }}</span>
        <div class="identidad">
          <span class="kicker">{{ NOMBRE_POSICION[j.posicion] || j.posicion }} · {{ j.equipo?.nombre || 'Sin equipo' }}</span>
          <h1 class="nombre">{{ j.nickname }}</h1>
          <span class="muted">{{ j.nombreReal || 'Jugador de la LEC' }} · {{ j.partidasJugadas }} partidas jugadas</span>
        </div>
        <div class="cifras">
          <div>
            <span class="micro">Valor</span>
            <span class="cifra">{{ millones(j.valorMercado) }}</span>
            <span v-if="var_" :class="var_.clase" class="var">{{ var_.texto }}</span>
          </div>
          <div><span class="micro">Puntos</span><span class="cifra">{{ puntos(j.puntosTotales) }}</span></div>
          <div><span class="micro">Media</span><span class="cifra">{{ j.partidasJugadas ? decimal(mediaJugador(j)) : '—' }}</span></div>
          <div v-if="m"><span class="micro">KDA</span><span class="cifra">{{ decimal(m.kda) }}</span></div>
        </div>
      </section>

      <Alerta v-if="j.bloqueado" :mensaje="`Retirado del mercado: ${j.motivoBloqueo || ''}`" tipo="aviso" class="mt" />

      <div class="grid grid-2 mt">
        <section class="tarjeta">
          <h2>Rendimiento</h2>
          <span class="micro">Evolución del precio</span>
          <GraficoLinea class="mt" :datos="precio" :formato="millones" descripcion="Evolución del precio"
                        vacio="El precio aún no ha cambiado: se actualiza al cerrar cada jornada." />
          <span class="micro bloque-micro">Puntos por partida · últimas {{ barras.length }}</span>
          <GraficoBarras class="mt" :datos="barras" :formato="(v) => puntos(v) + ' pts'" />
          <div v-if="m" class="grid grid-4 medias">
            <div><span class="micro">K / D / A</span><b>{{ decimal(m.kills) }} / {{ decimal(m.deaths) }} / {{ decimal(m.assists) }}</b></div>
            <div><span class="micro">CS</span><b>{{ decimal(m.cs) }}</b></div>
            <div><span class="micro">Visión</span><b>{{ decimal(m.vision) }}</b></div>
            <div><span class="micro">Victorias</span><b>{{ Math.round(m.victorias * 100) }} %</b></div>
          </div>
        </section>

        <section class="tarjeta tarjeta-acento">
          <div class="tarjeta-cabeza">
            <h2>Fichar a {{ j.nickname }}</h2>
            <span v-if="!j.bloqueado" class="chip chip-solido">Fichaje directo</span>
          </div>
          <p class="tenue">
            El mercado es libre: varios mánagers de la misma liga pueden tener al mismo jugador.
            Si su rol está libre entra de titular; si no, al banquillo.
          </p>
          <div v-if="!juego.ligas.length" class="vacio">
            Únete a una liga para poder fichar. <RouterLink to="/ligas">Ver ligas</RouterLink>
          </div>
          <PanelFichaje v-else :jugador="j" :equipo-inicial="juego.activoId" @fichado="alFichar" @vendido="alVender" />
          <Alerta :mensaje="aviso" tipo="ok" class="mt" />
        </section>
      </div>

      <section class="tarjeta mt">
        <h2>Partidas</h2>
        <div class="tabla-scroll">
          <table class="tabla">
            <thead><tr><th>Fecha</th><th>Jornada</th><th>Campeón</th><th>KDA</th><th class="num">CS</th><th class="num">Visión</th><th>Extras</th><th class="num">Puntos</th></tr></thead>
            <tbody>
              <tr v-for="e in datos.historial" :key="e.partidaId" class="clicable" @click="router.push(`/partidas/${e.partidaId}`)">
                <td class="muted">{{ fecha(e.fecha) }}</td>
                <td>{{ e.jornada }}</td>
                <td>{{ e.campeon || '—' }}</td>
                <td>{{ e.kills }}/{{ e.deaths }}/{{ e.assists }}</td>
                <td class="num">{{ e.cs }}</td>
                <td class="num">{{ e.visionScore }}</td>
                <td><Extras :e="e" /></td>
                <td class="num negrita">{{ puntos(e.puntos) }}</td>
              </tr>
              <tr v-if="!datos.historial.length"><td colspan="8" class="vacio">Todavía no ha jugado ninguna partida.</td></tr>
            </tbody>
          </table>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import Extras from '../../components/Extras.vue'
import GraficoBarras from '../../components/GraficoBarras.vue'
import GraficoLinea from '../../components/GraficoLinea.vue'
import PanelFichaje from '../../components/PanelFichaje.vue'
import { api } from '../../api/cliente'
import { useJuego } from '../../stores/juego'
import {
  decimal, fecha, iniciales, mediaJugador, millones, NOMBRE_POSICION, puntos, variacion
} from '../../utils/formato'
import { barrasPartidas, medias, seriePrecio } from '../../utils/graficos'

const route = useRoute()
const router = useRouter()
const juego = useJuego()
const datos = ref(null)
const cargando = ref(true)
const error = ref('')
const aviso = ref('')
const j = computed(() => datos.value?.jugador)
const var_ = computed(() => variacion(j.value))
const precio = computed(() => seriePrecio(datos.value))
const barras = computed(() => barrasPartidas(datos.value?.historial))
const m = computed(() => medias(datos.value?.historial))

function alFichar({ titular }) {
  aviso.value = `¡${j.value.nickname} fichado! Va a ${titular ? 'tu once titular' : 'tu banquillo'}.`
}
function alVender() {
  aviso.value = `Has vendido a ${j.value.nickname}.`
}

async function cargar() {
  cargando.value = true
  error.value = ''
  aviso.value = ''
  try {
    ;[datos.value] = await Promise.all([api.get(`/jugadores/${route.params.id}`), juego.cargar().catch(() => {})])
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)
watch(() => route.params.id, (id) => { if (id && route.name === 'jugador') cargar() })
</script>

<style scoped>
.heroe {
  display: flex; align-items: center; gap: 22px; flex-wrap: wrap; padding: 24px;
  background: linear-gradient(100deg, var(--acento-suave), var(--tarjeta) 55%);
}
.foto { width: 104px; height: 104px; border-radius: 50%; object-fit: cover; font-size: 1.9rem; border-width: 2px; flex-shrink: 0; }
.identidad { flex: 1; min-width: 220px; display: flex; flex-direction: column; gap: 2px; }
.nombre { font-size: clamp(2.2rem, 5vw, 3.4rem); margin: 0; }
.cifras { display: flex; gap: 28px; flex-wrap: wrap; }
.cifras > div { display: flex; flex-direction: column; gap: 2px; }
.cifras .cifra { font-size: 1.9rem; }
.var { font-size: 0.78rem; }
.bloque-micro { display: block; margin-top: 22px; }
.medias { margin-top: 18px; padding-top: 14px; border-top: 1px solid var(--borde); }
.medias > div { display: flex; flex-direction: column; gap: 2px; }
@media (max-width: 640px) {
  .heroe { padding: 18px; gap: 14px; }
  .foto { width: 72px; height: 72px; font-size: 1.3rem; }
  .cifras { gap: 18px; }
  .cifras .cifra { font-size: 1.4rem; }
}
</style>
