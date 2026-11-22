<template>
  <div>
    <Alerta :mensaje="error" class="mb" />
    <div class="con-lateral ancho">
      <section class="tarjeta">
        <div class="tarjeta-cabeza">
          <h2>Explorador de jugadores</h2>
          <div class="fila filtros">
            <select v-model="posicion" class="input filtro" :class="{ activo: posicion }" aria-label="Posición">
              <option value="">Rol: todos</option>
              <option v-for="p in POSICIONES" :key="p" :value="p">Rol: {{ p }}</option>
            </select>
            <select v-model="equipoPro" class="input filtro" :class="{ activo: equipoPro }" aria-label="Equipo">
              <option value="">Equipo: todos</option>
              <option v-for="e in equiposPro" :key="e.id" :value="e.id">{{ e.nombre }}</option>
            </select>
            <input v-model.trim="texto" class="input filtro buscar" placeholder="Buscar…" aria-label="Buscar jugador" />
          </div>
        </div>

        <div v-if="cargando" class="cargando">Cargando…</div>
        <div v-else class="tabla-scroll">
          <table class="tabla">
            <thead>
              <tr>
                <th v-for="c in COLUMNAS" :key="c.id" :class="[c.clase, { ordenable: c.orden, activa: orden === c.id }]"
                    :aria-sort="orden === c.id ? (asc ? 'ascending' : 'descending') : undefined" @click="ordenar(c)">
                  {{ c.texto }}<template v-if="orden === c.id"> {{ asc ? '▲' : '▼' }}</template>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="j in filtrados" :key="j.id" class="clicable" :class="{ destacada: j.id === elegidoId }" @click="elegir(j.id)">
                <td><b>{{ j.nickname }}</b></td>
                <td><span class="pos">{{ j.posicion }}</span></td>
                <td class="sec"><InsigniaEquipo :equipo="j.equipo" nombre="siglas" /></td>
                <td class="num">{{ j.partidasJugadas }}</td>
                <td class="num">{{ puntos(j.puntosTotales) }}</td>
                <td class="num negrita" :class="{ 'texto-acento': orden === 'media' }">{{ j.partidasJugadas ? decimal(mediaJugador(j)) : '—' }}</td>
                <td class="num sec">{{ millones(j.valorMercado) }}</td>
                <td class="num sec">
                  <span v-if="variacion(j)" :class="variacion(j).clase">{{ variacion(j).texto }}</span>
                  <span v-else class="tenue">—</span>
                </td>
              </tr>
              <tr v-if="!filtrados.length"><td :colspan="COLUMNAS.length" class="vacio">Ningún jugador coincide.</td></tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="tarjeta tarjeta-acento ficha">
        <div v-if="!detalle && cargandoDetalle" class="cargando">Cargando…</div>
        <p v-else-if="!detalle" class="tenue">Elige un jugador de la tabla para ver su ficha.</p>
        <template v-else>
          <div class="cabeza-ficha">
            <span class="avatar anillo xl">{{ iniciales(detalle.jugador.nickname) }}</span>
            <div>
              <h2 class="sin-margen">{{ detalle.jugador.nickname }}</h2>
              <span class="tenue">
                {{ detalle.jugador.posicion }} · {{ siglasEquipo(detalle.jugador.equipo) }} · {{ detalle.jugador.partidasJugadas }} PJ ·
                Media {{ detalle.jugador.partidasJugadas ? decimal(mediaJugador(detalle.jugador)) : '—' }}
              </span>
            </div>
          </div>

          <div class="grid grid-3 resumen">
            <div><span class="micro">Valor</span><b>{{ millones(detalle.jugador.valorMercado) }}</b></div>
            <div><span class="micro">KDA</span><b>{{ m ? decimal(m.kda) : '—' }}</b></div>
            <div><span class="micro">Victorias</span><b>{{ m ? Math.round(m.victorias * 100) + ' %' : '—' }}</b></div>
          </div>

          <span class="micro">Evolución del precio</span>
          <GraficoLinea class="mt-s" :datos="seriePrecio(detalle)" :formato="millones"
                        vacio="El precio aún no ha cambiado." />

          <span class="micro bloque">Puntos por partida</span>
          <GraficoBarras class="mt-s" :datos="barrasPartidas(detalle.historial, 8)" :formato="(v) => puntos(v) + ' pts'" />

          <span class="micro bloque">Últimas partidas</span>
          <div class="ultimas">
            <RouterLink v-for="e in detalle.historial.slice(0, 4)" :key="e.partidaId" :to="`/partidas/${e.partidaId}`"
                        class="ultima" :class="e.victoria ? 'gano' : 'perdio'" :title="e.victoria ? 'Victoria' : 'Derrota'">
              <span class="resultado">{{ e.victoria ? 'Victoria' : 'Derrota' }}</span>
              <b>{{ e.campeon || '—' }}</b>
              <span>{{ e.kills }}/{{ e.deaths }}/{{ e.assists }}</span>
              <span>{{ puntos(e.puntos) }} pts</span>
            </RouterLink>
            <span v-if="!detalle.historial.length" class="tenue">Sin partidas todavía.</span>
          </div>

          <RouterLink :to="`/jugadores/${detalle.jugador.id}`" class="btn btn-bloque mt">Ver ficha y fichar →</RouterLink>
        </template>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import GraficoBarras from '../../components/GraficoBarras.vue'
import GraficoLinea from '../../components/GraficoLinea.vue'
import InsigniaEquipo from '../../components/InsigniaEquipo.vue'
import { api } from '../../api/cliente'
import { decimal, iniciales, mediaJugador, millones, POSICIONES, puntos, variacion } from '../../utils/formato'
import { siglasEquipo } from '../../utils/equipos'
import { barrasPartidas, medias, seriePrecio } from '../../utils/graficos'

const COLUMNAS = [
  { id: 'nickname', texto: 'Jugador', orden: true },
  { id: 'posicion', texto: 'Rol', orden: true },
  { id: 'equipo', texto: 'Eq', clase: 'sec' },
  { id: 'partidas', texto: 'PJ', clase: 'num', orden: true },
  { id: 'puntos', texto: 'Pts', clase: 'num', orden: true },
  { id: 'media', texto: 'Media', clase: 'num', orden: true },
  { id: 'valor', texto: 'Valor', clase: 'num sec', orden: true },
  { id: 'variacion', texto: 'Var.', clase: 'num sec', orden: true }
]
const VALOR = {
  nickname: (j) => j.nickname.toLowerCase(),
  posicion: (j) => POSICIONES.indexOf(j.posicion),
  partidas: (j) => j.partidasJugadas,
  puntos: (j) => Number(j.puntosTotales),
  media: mediaJugador,
  valor: (j) => Number(j.valorMercado),
  variacion: (j) => variacion(j)?.pct ?? 0
}

const jugadores = ref([])
const equiposPro = ref([])
const cargando = ref(true)
const error = ref('')
const posicion = ref('')
const equipoPro = ref('')
const texto = ref('')
const orden = ref('media')
const asc = ref(false)
const elegidoId = ref(null)
const detalle = ref(null)
const cargandoDetalle = ref(false)
const m = computed(() => medias(detalle.value?.historial))

const filtrados = computed(() => {
  const t = texto.value.toLowerCase()
  const f = VALOR[orden.value]
  return jugadores.value
    .filter((j) => (!posicion.value || j.posicion === posicion.value) &&
      (!equipoPro.value || j.equipo?.id === equipoPro.value) &&
      (!t || j.nickname.toLowerCase().includes(t)))
    .sort((a, b) => {
      const va = f(a)
      const vb = f(b)
      const r = va < vb ? -1 : va > vb ? 1 : 0
      return asc.value ? r : -r
    })
})

function ordenar(c) {
  if (!c.orden) return
  if (orden.value === c.id) asc.value = !asc.value
  else {
    orden.value = c.id
    asc.value = c.id === 'nickname' || c.id === 'posicion'
  }
}

async function elegir(id) {
  elegidoId.value = id
  cargandoDetalle.value = true
  try {
    const d = await api.get(`/jugadores/${id}`)
    if (elegidoId.value === id) detalle.value = d
  } catch (e) {
    error.value = e.message
  } finally {
    cargandoDetalle.value = false
  }
}

onMounted(async () => {
  try {
    ;[jugadores.value, equiposPro.value] = await Promise.all([api.get('/jugadores'), api.get('/equipos-pro')])
    if (filtrados.value.length) elegir(filtrados.value[0].id)
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
})
</script>

<style scoped>
.filtros { gap: 6px; flex-wrap: wrap; justify-content: flex-end; }
.filtro { width: auto; font-size: 0.76rem; font-weight: 700; padding: 8px 10px; }
.filtro.activo { border-color: var(--acento-borde); color: var(--acento-tinta); background: var(--acento-suave); }
.buscar { width: 130px; }
.ficha { position: sticky; top: calc(var(--alto-cabecera) + 16px); display: flex; flex-direction: column; }
.cabeza-ficha { display: flex; align-items: center; gap: 14px; margin-bottom: 16px; }
.avatar.xl { width: 56px; height: 56px; font-size: 1rem; border-width: 2px; }
.resumen { margin-bottom: 18px; padding-bottom: 14px; border-bottom: 1px solid var(--borde); gap: 10px; }
.resumen > div { display: flex; flex-direction: column; }
.resumen b { font-family: var(--fuente-titulo); font-size: 1.2rem; }
.mt-s { margin-top: 10px; }
.bloque { display: block; margin-top: 20px; }
.ultimas { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 6px; margin-top: 10px; }
.ultima { display: flex; flex-direction: column; align-items: center; gap: 1px; padding: 8px 4px; border-radius: 6px; font-size: 0.7rem; color: var(--texto); text-align: center; }
.ultima:hover { text-decoration: none; filter: brightness(1.15); }
.ultima b { font-size: 0.74rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 100%; }
.resultado { font-size: 0.56rem; font-weight: 800; letter-spacing: 0.1em; text-transform: uppercase; opacity: 0.85; }
.gano { background: color-mix(in srgb, var(--verde) 22%, var(--tarjeta-2)); }
.perdio { background: color-mix(in srgb, var(--rojo) 22%, var(--tarjeta-2)); }
@media (max-width: 1100px) { .ficha { position: static; } }
@media (max-width: 760px) {
  .sec { display: none; }
  .filtros { justify-content: flex-start; }
  .tabla :deep(td), .tabla :deep(th) { padding: 9px 6px; }
}
</style>
