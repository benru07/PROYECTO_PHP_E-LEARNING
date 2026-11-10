<template>
  <div>
    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" class="mb" />
    <template v-if="equipo">
      <div class="fila-entre cabeza">
        <div>
          <span class="kicker">{{ equipo.nombre }} · {{ equipo.liga }}</span>
          <h1 class="sin-margen">{{ propio ? 'Mi equipo' : equipo.nombre }}</h1>
          <span v-if="!propio" class="muted">Equipo de {{ equipo.usuario }} · {{ ordinal(equipo.posicion) }} en la liga</span>
        </div>
        <div class="fila">
          <div class="pildoras">
            <button v-for="p in pestanasVisibles" :key="p.id" class="pildora" :class="{ activa: pestana === p.id }"
                    @click="pestana = p.id">{{ p.nombre }}</button>
          </div>
          <button v-if="!propio" class="btn btn-fantasma btn-peque" @click="reportando = true">Reportar</button>
        </div>
      </div>

      <Alerta v-if="!equipo.mercadoAbierto" tipo="aviso" class="mb"
              mensaje="Jornada en curso: el mercado y la alineación están bloqueados hasta que termine." />
      <Alerta :mensaje="aviso" :tipo="avisoTipo" class="mb" />

      <!-- Alineación sobre el mapa -->
      <div v-if="pestana === 'alineacion'" class="con-lateral">
        <section class="tarjeta mapa-tarjeta">
          <div class="fila-entre mapa-cabeza">
            <span class="kicker">{{ equipo.nombre }} · {{ proxima ? proxima.nombre : 'Alineación' }}</span>
            <span v-if="propio && editable && !seleccion" class="tenue ocultar-movil">Pulsa un jugador y luego otro hueco para moverlo</span>
          </div>

          <div v-if="seleccion && porSlot[seleccion]" class="barra-seleccion">
            <Icono nombre="intercambio" :tam="16" />
            <span><b>{{ porSlot[seleccion].jugador.nickname }}</b> seleccionado: pulsa otro hueco para moverlo</span>
            <span class="fila acciones-sel">
              <button class="btn btn-texto-rojo btn-peque" @click.stop="vender(porSlot[seleccion])">
                Vender · {{ millones(porSlot[seleccion].jugador.valorMercado) }}
              </button>
              <button class="btn btn-fantasma btn-peque" @click.stop="seleccion = null">Cancelar</button>
            </span>
          </div>

          <div class="mapa">
            <svg class="carriles" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true">
              <path d="M9 91 V9 H91" class="carril" />
              <path d="M9 91 H91 V9" class="carril" />
              <path d="M9 91 L91 9" class="carril medio" />
              <path d="M14 26 C 34 44, 56 58, 74 92" class="rio" />
            </svg>
            <span class="base azul" />
            <span class="base roja" />
            <FichaMapa v-for="s in TITULARES" :key="s" :class="`s-${s}`" :hueco="s" :item="porSlot[s]"
                       :editable="propio && editable" :seleccionada="seleccion === s"
                       :destino="!!seleccion && seleccion !== s && admiteMovimiento(seleccion, s)"
                       :mejora="mejoraDe(porSlot[s])" :ruta-mercado="rutaMercado(s)" @pulsar="pulsar(s)" />
          </div>
        </section>

        <aside class="pila">
          <div class="tarjeta">
            <h2>Mejoras</h2>
            <p class="tenue">
              <template v-if="proxima">Multiplican los puntos de un titular en <b>{{ proxima.nombre }}</b> si cumple el reto.</template>
              <template v-else>No hay ninguna jornada programada a la que aplicar mejoras.</template>
            </p>
            <div v-if="pendientes.length" class="pila pendientes">
              <div v-for="m in pendientes" :key="m.id" class="pendiente">
                <span class="chip chip-solido">×{{ m.mejora.multiplicadorBono }}</span>
                <span class="texto-pend"><b>{{ m.mejora.nombre }}</b><span class="tenue">{{ m.jugador }}</span></span>
                <button v-if="propio && editable" class="x" title="Cancelar y recuperar el dinero" @click="cancelarMejora(m)">
                  <Icono nombre="cerrar" :tam="14" />
                </button>
              </div>
            </div>
            <span class="micro">Disponibles</span>
            <div class="fila chips-mejora">
              <button v-for="m in catalogo" :key="m.id" class="chip-mejora" :disabled="!puedeMejorar"
                      :title="m.descripcion" @click="abrirMejora(m.id)">
                {{ m.nombre }} ×{{ m.multiplicadorBono }} <span>{{ millones(m.precio) }}</span>
              </button>
            </div>
          </div>

          <div class="tarjeta">
            <h2>Banquillo · 2 suplentes</h2>
            <div class="pila banquillo">
              <div v-for="s in BANQUILLO" :key="s" class="suplente"
                   :class="{ seleccionada: seleccion === s, destino: !!seleccion && seleccion !== s && admiteMovimiento(seleccion, s), vacia: !porSlot[s] }"
                   role="button" tabindex="0" @click="pulsar(s)" @keydown.enter.prevent="pulsar(s)">
                <template v-if="porSlot[s]">
                  <span class="avatar anillo">{{ iniciales(porSlot[s].jugador.nickname) }}</span>
                  <span class="texto-sup">
                    <b>{{ porSlot[s].jugador.nickname }}</b>
                    <span class="tenue">{{ porSlot[s].jugador.posicion }} · {{ siglasEquipo(porSlot[s].jugador.equipo) }} · {{ millones(porSlot[s].jugador.valorMercado) }}</span>
                  </span>
                  <span class="negrita">{{ puntos(porSlot[s].jugador.puntosTotales) }}</span>
                </template>
                <template v-else>
                  <span class="avatar vacio-av"><Icono nombre="mas" :tam="14" /></span>
                  <span class="texto-sup"><b class="tenue">Hueco libre</b></span>
                  <RouterLink v-if="propio && editable" :to="rutaMercado(s)" class="btn btn-borde btn-peque" @click.stop>Fichar</RouterLink>
                </template>
              </div>
            </div>
            <p class="tenue pista">Los suplentes no puntúan. Pulsa uno y luego un titular de su rol para cambiarlos.</p>
          </div>

          <div class="tarjeta resumen">
            <div class="fila-entre"><span class="micro">Puntos última jornada</span>
              <b>{{ ultima ? puntos(ultima.puntosTotales) : '—' }}</b></div>
            <div class="fila-entre"><span class="micro">Valor alineación</span><b>{{ millones(valorTitulares) }}</b></div>
            <div class="fila-entre"><span class="micro">Saldo</span><b class="texto-acento">{{ millones(equipo.dineroDisponible) }}</b></div>
          </div>
          <RouterLink v-if="propio" :to="`/equipos/${equipo.id}/mercado`" class="btn btn-grande btn-bloque">Ir al mercado</RouterLink>
        </aside>
      </div>

      <!-- Mejoras -->
      <section v-if="pestana === 'mejoras'" class="tarjeta">
        <div class="tarjeta-cabeza">
          <h2>Mejoras compradas</h2>
          <button v-if="propio" class="btn btn-peque" :disabled="!puedeMejorar" @click="abrirMejora()">Comprar mejora</button>
        </div>
        <div class="tabla-scroll">
          <table class="tabla">
            <thead><tr><th>Mejora</th><th>Jugador</th><th>Jornada</th><th>Estado</th><th class="num">Pagado</th><th class="num">Puntos extra</th><th></th></tr></thead>
            <tbody>
              <tr v-for="m in mejoras" :key="m.id">
                <td><b>{{ m.mejora.nombre }}</b> <span class="tenue">×{{ m.mejora.multiplicadorBono }}</span></td>
                <td>{{ m.jugador }}</td>
                <td class="muted">{{ m.jornada }}</td>
                <td><span class="chip" :class="claseEstadoMejora[m.estado]">{{ textoEstadoMejora[m.estado] }}</span></td>
                <td class="num">{{ millones(m.precioPagado) }}</td>
                <td class="num">{{ m.estado === 'CUMPLIDA' ? '+' + puntos(m.puntosExtra) : '—' }}</td>
                <td class="derecha">
                  <button v-if="propio && m.estado === 'PENDIENTE' && editable" class="btn btn-fantasma btn-peque"
                          @click="cancelarMejora(m)">Cancelar</button>
                </td>
              </tr>
              <tr v-if="!mejoras.length"><td colspan="7" class="vacio">Sin mejoras todavía.</td></tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Puntos por jornada -->
      <section v-if="pestana === 'puntos'" class="con-lateral">
        <div class="tarjeta">
          <h2>Puntos por jornada</h2>
          <div class="tabla-scroll">
            <table class="tabla">
              <thead><tr><th>Jornada</th><th class="num">Titulares</th><th class="num">Mejoras</th><th class="num">Total</th></tr></thead>
              <tbody>
                <tr v-for="p in puntuaciones" :key="p.jornadaId">
                  <td>{{ p.jornada }}</td>
                  <td class="num">{{ puntos(p.puntosBase) }}</td>
                  <td class="num">{{ puntos(p.puntosMejoras) }}</td>
                  <td class="num negrita">{{ puntos(p.puntosTotales) }}</td>
                </tr>
                <tr v-if="!puntuaciones.length"><td colspan="4" class="vacio">Todavía no se ha cerrado ninguna jornada.</td></tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="tarjeta">
          <span class="micro">Evolución</span>
          <GraficoBarras class="mt" :datos="barrasPuntos" :formato="(v) => puntos(v) + ' pts'"
                         descripcion="Puntos por jornada" vacio="Aún no hay jornadas puntuadas." />
        </div>
      </section>

      <!-- Movimientos -->
      <section v-if="pestana === 'movimientos'" class="tarjeta">
        <h2>Movimientos</h2>
        <div class="tabla-scroll">
          <table class="tabla">
            <thead><tr><th>Fecha</th><th>Tipo</th><th>Jugador</th><th class="num">Precio</th></tr></thead>
            <tbody>
              <tr v-for="t in transacciones" :key="t.id">
                <td class="muted">{{ fechaHora(t.fecha) }}</td>
                <td><span class="chip" :class="t.tipo === 'COMPRA' ? 'chip-acento' : 'chip-verde'">{{ t.tipo === 'COMPRA' ? 'Fichaje' : 'Venta' }}</span></td>
                <td><RouterLink :to="`/jugadores/${t.jugadorId}`">{{ t.jugador }}</RouterLink></td>
                <td class="num" :class="t.tipo === 'COMPRA' ? 'texto-rojo' : 'texto-verde'">
                  {{ t.tipo === 'COMPRA' ? '−' : '+' }}{{ millones(t.precio) }}
                </td>
              </tr>
              <tr v-if="!transacciones.length"><td colspan="4" class="vacio">Sin movimientos.</td></tr>
            </tbody>
          </table>
        </div>
      </section>
    </template>

    <Modal :abierto="modalMejora" titulo="Comprar mejora" ancho="620px" @cerrar="modalMejora = false">
      <form class="pila" @submit.prevent="comprarMejora">
        <div class="pila opciones">
          <label v-for="m in catalogo" :key="m.id" class="opcion" :class="{ elegida: nuevaMejora.mejoraId === m.id }">
            <input v-model="nuevaMejora.mejoraId" type="radio" :value="m.id" />
            <div>
              <div class="fila-entre"><b>{{ m.nombre }} ×{{ m.multiplicadorBono }}</b><span class="texto-acento negrita">{{ millones(m.precio) }}</span></div>
              <span class="tenue">{{ m.descripcion }}<template v-if="m.umbral"> (umbral {{ m.umbral }})</template></span>
            </div>
          </label>
        </div>
        <div class="campo">
          <label for="jm">Jugador</label>
          <select id="jm" v-model="nuevaMejora.jugadorId" class="input" required>
            <option :value="null" disabled>Elige un jugador de tu plantilla</option>
            <option v-for="p in equipo?.plantilla || []" :key="p.jugador.id" :value="p.jugador.id">
              {{ p.jugador.nickname }} ({{ p.jugador.posicion }}){{ p.estado === 'BANQUILLO' ? ' · suplente' : '' }}
            </option>
          </select>
          <span class="tenue">Solo cuenta si el jugador es titular al cerrarse la jornada.</span>
        </div>
        <Alerta :mensaje="errorModal" />
        <button class="btn btn-bloque" :disabled="enviando || !nuevaMejora.mejoraId || !nuevaMejora.jugadorId">Comprar</button>
      </form>
    </Modal>

    <ModalReporte v-if="equipo" :abierto="reportando" :usuario-id="equipo.usuarioId" :usuario="equipo.usuario"
                  @cerrar="reportando = false" />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import FichaMapa from '../../components/FichaMapa.vue'
import GraficoBarras from '../../components/GraficoBarras.vue'
import Icono from '../../components/Icono.vue'
import Modal from '../../components/Modal.vue'
import ModalReporte from '../../components/ModalReporte.vue'
import { api } from '../../api/cliente'
import { useAuth } from '../../stores/auth'
import { useJuego } from '../../stores/juego'
import { fechaHora, iniciales, millones, ordinal, puntos } from '../../utils/formato'
import { siglasEquipo } from '../../utils/equipos'

const TITULARES = ['TOP', 'JNG', 'MID', 'ADC', 'SUP']
const BANQUILLO = ['BANQUILLO_1', 'BANQUILLO_2']
const textoEstadoMejora = { PENDIENTE: 'Pendiente', CUMPLIDA: 'Cumplida', FALLIDA: 'Fallida' }
const claseEstadoMejora = { PENDIENTE: 'chip-amarillo', CUMPLIDA: 'chip-verde', FALLIDA: 'chip-rojo' }

const auth = useAuth()
const juego = useJuego()
const route = useRoute()
const equipo = ref(null)
const mejoras = ref([])
const puntuaciones = ref([])
const transacciones = ref([])
const jornadas = ref([])
const catalogo = ref([])
const cargando = ref(true)
const error = ref('')
const aviso = ref('')
const avisoTipo = ref('ok')
const pestana = ref('alineacion')
const seleccion = ref(null)
const modalMejora = ref(false)
const errorModal = ref('')
const enviando = ref(false)
const reportando = ref(false)
const nuevaMejora = reactive({ mejoraId: null, jugadorId: null })

const propio = computed(() => equipo.value?.usuarioId === auth.usuario?.id)
const editable = computed(() => equipo.value?.mercadoAbierto)
const proxima = computed(() => jornadas.value.find((j) => j.estado === 'PROGRAMADA'))
const puedeMejorar = computed(() => propio.value && !!proxima.value && editable.value && !!equipo.value?.plantilla.length)
const porSlot = computed(() => {
  const m = {}
  equipo.value?.plantilla.forEach((p) => (m[p.slot] = p))
  return m
})
const pendientes = computed(() => mejoras.value.filter((m) => m.estado === 'PENDIENTE'))
const ultima = computed(() => puntuaciones.value[puntuaciones.value.length - 1])
const valorTitulares = computed(() => TITULARES.reduce((s, k) => s + Number(porSlot.value[k]?.jugador.valorMercado || 0), 0))
const barrasPuntos = computed(() => puntuaciones.value.map((p, i) => ({
  etiqueta: p.jornada, corta: `J${i + 1}`, valor: Number(p.puntosTotales),
  detalle: Number(p.puntosMejoras) ? `${puntos(p.puntosMejoras)} de mejoras` : ''
})))
const pestanasVisibles = computed(() => [
  { id: 'alineacion', nombre: 'Alineación' },
  { id: 'mejoras', nombre: 'Mejoras' },
  { id: 'puntos', nombre: 'Puntos' },
  ...(propio.value ? [{ id: 'movimientos', nombre: 'Movimientos' }] : [])
])

function mejoraDe(item) {
  if (!item) return ''
  const m = pendientes.value.find((x) => x.jugadorId === item.jugador.id)
  return m ? `${m.mejora.nombre} ×${m.mejora.multiplicadorBono}` : ''
}

function rutaMercado(slot) {
  if (!equipo.value) return null
  const pos = TITULARES.includes(slot) ? slot : undefined
  return { name: 'mercado', params: { id: equipo.value.id }, query: pos ? { posicion: pos } : {} }
}

function mostrar(mensaje, tipo = 'ok') {
  aviso.value = mensaje
  avisoTipo.value = tipo
}

async function cargar() {
  const id = route.params.id
  error.value = ''
  seleccion.value = null
  try {
    equipo.value = await api.get(`/equipos/${id}`)
    if (propio.value) {
      juego.elegir(equipo.value.id)
      juego.actualizarEquipo(equipo.value)
    }
    const peticiones = [
      api.get(`/equipos/${id}/mejoras`),
      api.get(`/equipos/${id}/puntuaciones`),
      api.get('/jornadas'),
      api.get('/mejoras')
    ]
    if (propio.value) peticiones.push(api.get(`/equipos/${id}/transacciones`))
    const [m, p, j, c, t] = await Promise.all(peticiones)
    mejoras.value = m
    puntuaciones.value = p
    jornadas.value = j
    catalogo.value = c
    transacciones.value = t || []
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
}

const admite = (slot, jugador) => BANQUILLO.includes(slot) || slot === jugador.posicion
function admiteMovimiento(origen, destino) {
  const a = porSlot.value[origen]
  const b = porSlot.value[destino]
  return !!a && admite(destino, a.jugador) && (!b || admite(origen, b.jugador))
}

async function pulsar(slot) {
  if (!propio.value || !editable.value) return
  if (!seleccion.value) {
    if (porSlot.value[slot]) seleccion.value = slot
    return
  }
  const origen = seleccion.value
  seleccion.value = null
  if (origen === slot) return
  const a = porSlot.value[origen]
  const b = porSlot.value[slot]
  if (!admite(slot, a.jugador)) {
    return mostrar(`${a.jugador.nickname} es ${a.jugador.posicion} y no puede jugar en ese hueco.`, 'error')
  }
  if (b && !admite(origen, b.jugador)) {
    return mostrar(`${b.jugador.nickname} es ${b.jugador.posicion} y no puede pasar a ese hueco.`, 'error')
  }
  const slots = {}
  equipo.value.plantilla.forEach((p) => (slots[p.slot] = p.jugador.id))
  slots[slot] = a.jugador.id
  if (b) slots[origen] = b.jugador.id
  else delete slots[origen]
  try {
    equipo.value = await api.put(`/equipos/${equipo.value.id}/alineacion`, { slots })
    juego.actualizarEquipo(equipo.value)
    mostrar('Alineación guardada.')
  } catch (e) {
    mostrar(e.message, 'error')
  }
}

async function vender(item) {
  seleccion.value = null
  if (!confirm(`¿Vender a ${item.jugador.nickname} por ${millones(item.jugador.valorMercado)}?`)) return
  try {
    equipo.value = await api.del(`/equipos/${equipo.value.id}/plantilla/${item.jugador.id}`)
    juego.actualizarEquipo(equipo.value)
    mostrar(`Has vendido a ${item.jugador.nickname}.`)
  } catch (e) {
    mostrar(e.message, 'error')
  }
}

function abrirMejora(mejoraId) {
  errorModal.value = ''
  nuevaMejora.mejoraId = mejoraId ?? catalogo.value[0]?.id ?? null
  nuevaMejora.jugadorId = null
  modalMejora.value = true
}

async function comprarMejora() {
  errorModal.value = ''
  enviando.value = true
  try {
    await api.post(`/equipos/${equipo.value.id}/mejoras`, { ...nuevaMejora })
    modalMejora.value = false
    await cargar()
    mostrar('Mejora comprada.')
  } catch (e) {
    errorModal.value = e.message
  } finally {
    enviando.value = false
  }
}

async function cancelarMejora(m) {
  if (!confirm(`¿Cancelar ${m.mejora.nombre} para ${m.jugador}? Se te devolverá el dinero.`)) return
  try {
    await api.del(`/equipos/${equipo.value.id}/mejoras/${m.id}`)
    await cargar()
    mostrar('Mejora cancelada y dinero devuelto.')
  } catch (e) {
    mostrar(e.message, 'error')
  }
}

onMounted(cargar)
watch(() => route.params.id, (id) => {
  if (id && route.name === 'equipo') {
    cargando.value = true
    pestana.value = 'alineacion'
    cargar()
  }
})
</script>

<style scoped>
.cabeza { margin-bottom: 18px; align-items: flex-end; }
.mapa-tarjeta { padding: 16px; }
.mapa-cabeza { margin-bottom: 12px; }
.barra-seleccion {
  display: flex; align-items: center; gap: 10px; flex-wrap: wrap; padding: 10px 12px; margin-bottom: 12px;
  border-radius: var(--radio-peque); background: var(--acento-suave); border: 1px solid var(--acento-borde); color: var(--acento-tinta); font-size: 0.86rem;
}
.barra-seleccion b { color: var(--texto); }
.acciones-sel { margin-left: auto; gap: 8px; }
.mapa {
  position: relative; aspect-ratio: 16 / 10.5; border-radius: var(--radio-peque); overflow: hidden;
  background: var(--mapa-fondo); border: 1px solid var(--borde);
}
.carriles { position: absolute; inset: 0; width: 100%; height: 100%; }
.carril { fill: none; stroke: var(--mapa-carril); stroke-width: 26px; vector-effect: non-scaling-stroke; stroke-linejoin: round; }
.carril.medio { stroke-width: 22px; }
.rio { fill: none; stroke: var(--mapa-rio); stroke-width: 16px; vector-effect: non-scaling-stroke; stroke-linecap: round; opacity: 0.9; }
.base { position: absolute; width: 74px; height: 74px; border-radius: 50%; transform: translate(-50%, -50%); }
.base.azul { left: 7%; top: 91%; background: var(--base-azul); box-shadow: 0 0 0 1px color-mix(in srgb, #5b8fd6 40%, transparent); }
.base.roja { left: 93%; top: 9%; background: var(--base-roja); box-shadow: 0 0 0 1px color-mix(in srgb, #d65b5b 40%, transparent); }
/* Posición de cada rol sobre el mapa */
.s-TOP { left: 14%; top: 22%; }
.s-JNG { left: 30%; top: 58%; }
.s-MID { left: 50%; top: 40%; }
.s-SUP { left: 63%; top: 80%; }
.s-ADC { left: 85%; top: 70%; }

.pendientes { gap: 8px; margin-bottom: 14px; }
.pendiente { display: flex; align-items: center; gap: 10px; padding: 8px 10px; border-radius: var(--radio-peque); background: var(--tarjeta-2); }
.texto-pend { flex: 1; display: flex; flex-direction: column; font-size: 0.84rem; line-height: 1.3; }
.x { background: none; border: 0; color: var(--texto-3); cursor: pointer; padding: 4px; }
.x:hover { color: var(--rojo); }
.chips-mejora { gap: 6px; margin-top: 8px; }
.chip-mejora {
  background: var(--tarjeta-2); border: 1px solid var(--borde-2); color: var(--texto); border-radius: 6px; padding: 6px 9px;
  font: inherit; font-size: 0.72rem; font-weight: 700; cursor: pointer;
}
.chip-mejora span { color: var(--texto-3); font-weight: 600; margin-left: 4px; }
.chip-mejora:hover:not(:disabled) { border-color: var(--acento-borde); color: var(--acento-tinta); }
.chip-mejora:disabled { opacity: 0.5; cursor: not-allowed; }
.banquillo { gap: 8px; }
.suplente {
  display: flex; align-items: center; gap: 10px; padding: 10px; border-radius: var(--radio-peque);
  background: var(--tarjeta-2); border: 1px solid transparent; cursor: pointer;
}
.suplente:hover { border-color: var(--borde-2); }
.suplente.seleccionada { border-color: var(--acento); box-shadow: 0 0 0 2px var(--acento-medio); }
.suplente.destino { border-color: var(--acento-borde); border-style: dashed; }
.suplente.vacia { border: 1px dashed var(--borde-2); background: transparent; }
.vacio-av { background: transparent; border: 1.5px dashed var(--borde-2); color: var(--texto-3); }
.texto-sup { flex: 1; min-width: 0; display: flex; flex-direction: column; line-height: 1.3; }
.texto-sup .tenue { font-size: 0.74rem; }
.pista { margin: 10px 0 0; font-size: 0.76rem; }
.resumen { display: flex; flex-direction: column; gap: 10px; }
.opciones { gap: 8px; }
.opcion { display: flex; gap: 12px; align-items: flex-start; border: 1px solid var(--borde-2); border-radius: 10px; padding: 10px 12px; cursor: pointer; }
.opcion.elegida { border-color: var(--acento); background: var(--acento-suave); }
.opcion input { margin-top: 5px; accent-color: var(--acento); }
.opcion > div { flex: 1; }
@media (max-width: 760px) {
  .mapa-tarjeta { padding: 10px; }
  .mapa { aspect-ratio: 1 / 1.22; }
  .base { width: 48px; height: 48px; }
  .s-TOP { left: 20%; top: 14%; }
  .s-JNG { left: 23%; top: 50%; }
  .s-MID { left: 56%; top: 33%; }
  .s-SUP { left: 45%; top: 85%; }
  .s-ADC { left: 79%; top: 64%; }
}
</style>
