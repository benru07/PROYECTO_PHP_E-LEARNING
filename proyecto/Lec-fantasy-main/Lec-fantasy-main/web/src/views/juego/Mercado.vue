<template>
  <div>
    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" class="mb" />
    <template v-if="equipo">
      <Alerta v-if="!equipo.mercadoAbierto" tipo="aviso" class="mb" mensaje="El mercado está cerrado mientras se juega la jornada." />
      <Alerta :mensaje="aviso" :tipo="avisoTipo" class="mb" />

      <div class="con-lateral">
        <section class="tarjeta">
          <div class="tarjeta-cabeza">
            <div>
              <h2>Jugadores</h2>
              <span class="micro">Transferibles ahora · {{ equipo.liga }}</span>
            </div>
            <span class="micro derecha">{{ filtrados.length }} jugadores · mercado {{ equipo.mercadoAbierto ? 'abierto' : 'cerrado' }}</span>
          </div>

          <div class="filtros">
            <label class="buscar">
              <Icono nombre="buscar" :tam="16" />
              <input v-model.trim="texto" class="input" placeholder="Buscar jugador…" aria-label="Buscar jugador" />
            </label>
            <select v-model="posicion" class="input filtro" :class="{ activo: posicion }" aria-label="Posición">
              <option value="">Posición: todas</option>
              <option v-for="p in POSICIONES" :key="p" :value="p">Posición: {{ p }}</option>
            </select>
            <select v-model="equipoPro" class="input filtro" :class="{ activo: equipoPro }" aria-label="Equipo">
              <option value="">Equipo: todos</option>
              <option v-for="e in equiposPro" :key="e.id" :value="e.id">{{ e.nombre }}</option>
            </select>
            <select v-model="orden" class="input filtro" aria-label="Ordenar">
              <option value="valor">Más caros</option>
              <option value="barato">Más baratos</option>
              <option value="puntos">Más puntos</option>
              <option value="media">Mejor media</option>
              <option value="variacion">Mayor subida</option>
            </select>
          </div>

          <div class="tabla-scroll">
            <table class="tabla">
              <thead>
                <tr>
                  <th>Jugador</th><th>Pos</th><th class="sec">Equipo</th><th class="num">Precio</th>
                  <th class="num sec">Variación</th><th class="num sec">Puntos</th><th class="num sec">Media</th><th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="j in filtrados" :key="j.id" class="clicable" @click="router.push(`/jugadores/${j.id}`)">
                  <td>
                    <span class="jugador">
                      <span class="avatar anillo">{{ iniciales(j.nickname) }}</span>
                      <b>{{ j.nickname }}</b>
                    </span>
                  </td>
                  <td><span class="pos">{{ j.posicion }}</span></td>
                  <td class="sec"><InsigniaEquipo :equipo="j.equipo" nombre /></td>
                  <td class="num negrita">{{ millones(j.valorMercado) }}</td>
                  <td class="num sec">
                    <span v-if="variacion(j)" :class="variacion(j).clase">{{ variacion(j).texto }}</span>
                    <span v-else class="tenue">—</span>
                  </td>
                  <td class="num sec">{{ puntos(j.puntosTotales) }}</td>
                  <td class="num sec">{{ j.partidasJugadas ? decimal(mediaJugador(j)) : '—' }}</td>
                  <td class="derecha">
                    <span v-if="enPlantilla.has(j.id)" class="chip chip-acento">Tuyo</span>
                    <button v-else class="btn btn-peque" :disabled="!puedeFichar(j)" :title="motivoNoFichar(equipo, j)"
                            @click.stop="abrir(j)">Fichar</button>
                  </td>
                </tr>
                <tr v-if="!filtrados.length"><td colspan="8" class="vacio">
                  {{ jugadores.length ? 'Ningún jugador coincide con los filtros.' : 'Todavía no hay jugadores. Aparecerán cuando se importe la primera jornada.' }}
                </td></tr>
              </tbody>
            </table>
          </div>
        </section>

        <aside class="pila">
          <div class="tarjeta">
            <h2>Mi plantilla</h2>
            <div class="fila-entre saldo">
              <div><span class="micro">Saldo disponible</span><div class="cifra texto-acento">{{ millones(equipo.dineroDisponible) }}</div></div>
              <div class="derecha"><span class="micro">Jugadores</span><div class="cifra">{{ equipo.plantilla.length }}/7</div></div>
            </div>
            <div class="pila mios">
              <div v-for="s in SLOTS" :key="s" class="mio" :class="{ vacio: !porSlot[s] }">
                <span class="pos">{{ s.startsWith('BANQUILLO') ? 'BAN' : s }}</span>
                <template v-if="porSlot[s]">
                  <RouterLink :to="`/jugadores/${porSlot[s].jugador.id}`" class="nombre-mio">{{ porSlot[s].jugador.nickname }}</RouterLink>
                  <span class="tenue">{{ millones(porSlot[s].jugador.valorMercado) }}</span>
                  <button class="btn btn-fantasma btn-peque" :disabled="!equipo.mercadoAbierto" @click="vender(porSlot[s])">Vender</button>
                </template>
                <template v-else>
                  <button class="hueco-libre" @click="filtrarPor(s)">Hueco libre · buscar {{ s.startsWith('BANQUILLO') ? 'suplente' : s }}</button>
                </template>
              </div>
            </div>
          </div>

          <div class="tarjeta">
            <h2>Últimos movimientos</h2>
            <div v-if="!transacciones.length" class="tenue">Aún no has fichado ni vendido a nadie.</div>
            <div v-for="t in transacciones.slice(0, 5)" :key="t.id" class="movimiento">
              <div>
                <b>{{ t.jugador }}</b>
                <span class="tenue">{{ hace(t.fecha) }}</span>
              </div>
              <span class="chip" :class="t.tipo === 'COMPRA' ? 'chip-acento' : 'chip-verde'">
                {{ t.tipo === 'COMPRA' ? '−' : '+' }}{{ millones(t.precio) }}
              </span>
            </div>
            <RouterLink :to="`/equipos/${equipo.id}`" class="btn btn-fantasma btn-bloque mt">Ver mi alineación</RouterLink>
          </div>
        </aside>
      </div>
    </template>

    <Modal :abierto="!!elegido" titulo="" ancho="480px" @cerrar="elegido = null">
      <template v-if="elegido">
        <span class="micro">Fichaje directo</span>
        <div class="cabeza-modal">
          <span class="avatar anillo xl">{{ iniciales(elegido.nickname) }}</span>
          <div>
            <h2 class="sin-margen nombre-modal">{{ elegido.nickname }}</h2>
            <span class="tenue">{{ elegido.posicion }} · {{ elegido.equipo?.nombre || 'Sin equipo' }} · Valor de mercado {{ millones(elegido.valorMercado) }}</span>
          </div>
        </div>
        <PanelFichaje :jugador="elegido" :equipo-inicial="equipo.id" cancelable
                      @cancelar="elegido = null" @fichado="alFichar" />
      </template>
    </Modal>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import Icono from '../../components/Icono.vue'
import InsigniaEquipo from '../../components/InsigniaEquipo.vue'
import Modal from '../../components/Modal.vue'
import PanelFichaje from '../../components/PanelFichaje.vue'
import { api } from '../../api/cliente'
import { useJuego } from '../../stores/juego'
import {
  decimal, hace, iniciales, mediaJugador, millones, motivoNoFichar, puntos, POSICIONES, variacion
} from '../../utils/formato'

const SLOTS = ['TOP', 'JNG', 'MID', 'ADC', 'SUP', 'BANQUILLO_1', 'BANQUILLO_2']
const route = useRoute()
const router = useRouter()
const juego = useJuego()
const equipo = ref(null)
const jugadores = ref([])
const equiposPro = ref([])
const transacciones = ref([])
const cargando = ref(true)
const error = ref('')
const aviso = ref('')
const avisoTipo = ref('ok')
const texto = ref('')
const posicion = ref(typeof route.query.posicion === 'string' ? route.query.posicion : '')
const equipoPro = ref('')
const orden = ref('valor')
const elegido = ref(null)

const enPlantilla = computed(() => new Set(equipo.value?.plantilla.map((p) => p.jugador.id) || []))
const porSlot = computed(() => Object.fromEntries((equipo.value?.plantilla || []).map((p) => [p.slot, p])))
const puedeFichar = (j) => !motivoNoFichar(equipo.value, j)

const filtrados = computed(() => {
  const t = texto.value.toLowerCase()
  const lista = jugadores.value.filter((j) =>
    (!posicion.value || j.posicion === posicion.value) &&
    (!equipoPro.value || j.equipo?.id === equipoPro.value) &&
    (!t || j.nickname.toLowerCase().includes(t)))
  const pct = (j) => variacion(j)?.pct ?? 0
  const comparadores = {
    valor: (a, b) => b.valorMercado - a.valorMercado,
    barato: (a, b) => a.valorMercado - b.valorMercado,
    puntos: (a, b) => b.puntosTotales - a.puntosTotales,
    media: (a, b) => mediaJugador(b) - mediaJugador(a),
    variacion: (a, b) => pct(b) - pct(a)
  }
  return [...lista].sort(comparadores[orden.value])
})

function filtrarPor(slot) {
  posicion.value = slot.startsWith('BANQUILLO') ? '' : slot
  orden.value = 'media'
}

function abrir(j) {
  aviso.value = ''
  elegido.value = j
}

function alFichar({ equipo: actualizado, titular }) {
  const nombre = elegido.value.nickname
  equipo.value = actualizado
  elegido.value = null
  avisoTipo.value = 'ok'
  aviso.value = `¡${nombre} fichado! Va a ${titular ? 'tu once titular' : 'tu banquillo'}.`
  cargarMovimientos()
}

async function vender(item) {
  if (!confirm(`¿Vender a ${item.jugador.nickname} por ${millones(item.jugador.valorMercado)}?`)) return
  try {
    equipo.value = await api.del(`/equipos/${equipo.value.id}/plantilla/${item.jugador.id}`)
    juego.actualizarEquipo(equipo.value)
    avisoTipo.value = 'ok'
    aviso.value = `Has vendido a ${item.jugador.nickname}.`
    cargarMovimientos()
  } catch (e) {
    avisoTipo.value = 'error'
    aviso.value = e.message
  }
}

async function cargarMovimientos() {
  transacciones.value = await api.get(`/equipos/${equipo.value.id}/transacciones`).catch(() => [])
}

async function cargar() {
  cargando.value = true
  error.value = ''
  try {
    ;[equipo.value, jugadores.value, equiposPro.value] = await Promise.all([
      api.get(`/equipos/${route.params.id}`), api.get('/jugadores'), api.get('/equipos-pro'), juego.cargar()
    ])
    juego.actualizarEquipo(equipo.value)
    await cargarMovimientos()
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
}

onMounted(cargar)
watch(() => route.params.id, (id) => { if (id && route.name === 'mercado') cargar() })
</script>

<style scoped>
.filtros { display: flex; gap: 8px; margin-bottom: 10px; flex-wrap: wrap; }
.buscar { position: relative; flex: 1 1 220px; display: flex; align-items: center; }
.buscar .icono { position: absolute; left: 11px; color: var(--texto-3); }
.buscar .input { padding-left: 34px; }
.filtro { width: auto; flex: 0 1 auto; font-size: 0.78rem; font-weight: 700; }
.filtro.activo { border-color: var(--acento-borde); color: var(--acento-tinta); background: var(--acento-suave); }
.jugador { display: inline-flex; align-items: center; gap: 10px; white-space: nowrap; }
.jugador .avatar { width: 30px; height: 30px; font-size: 0.68rem; }
.saldo { margin-bottom: 14px; align-items: flex-end; }
.mios { gap: 0; }
.mio { display: grid; grid-template-columns: 40px 1fr auto auto; align-items: center; gap: 10px; padding: 8px 0; border-top: 1px solid var(--borde); font-size: 0.86rem; }
.mio.vacio { grid-template-columns: 40px 1fr; }
.nombre-mio { color: var(--texto); font-weight: 700; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.hueco-libre { background: none; border: 1px dashed var(--borde-2); border-radius: 6px; color: var(--texto-3); font: inherit; font-size: 0.76rem; padding: 6px 10px; cursor: pointer; text-align: left; }
.hueco-libre:hover { color: var(--acento-tinta); border-color: var(--acento-borde); }
.movimiento { display: flex; justify-content: space-between; align-items: center; gap: 10px; padding: 9px 0; border-top: 1px solid var(--borde); }
.movimiento > div { display: flex; flex-direction: column; line-height: 1.3; font-size: 0.86rem; }
.movimiento .tenue { font-size: 0.74rem; }
.cabeza-modal { display: flex; align-items: center; gap: 14px; margin: 8px 0 18px; }
.avatar.xl { width: 58px; height: 58px; font-size: 1.05rem; border-width: 2px; }
.nombre-modal { font-size: 1.6rem; }
@media (max-width: 760px) {
  .sec { display: none; }
  .tabla :deep(td), .tabla :deep(th) { padding: 9px 6px; }
  .filtro { flex: 1 1 30%; }
  .jugador .avatar { display: none; }
}
</style>
