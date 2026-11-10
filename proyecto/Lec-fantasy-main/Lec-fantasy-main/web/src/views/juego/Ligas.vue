<template>
  <div>
    <div class="fila-entre mb">
      <div>
        <span class="kicker">Compite con tus amigos</span>
        <h1 class="sin-margen">Mis ligas</h1>
      </div>
      <div class="fila">
        <button class="btn btn-borde" @click="abrirCodigo">Unirme con código</button>
        <button class="btn" @click="abrirCrear"><Icono nombre="mas" :tam="14" /> Crear liga</button>
      </div>
    </div>
    <Alerta :mensaje="error" class="mb" />

    <div class="pildoras mb">
      <button class="pildora" :class="{ activa: pestana === 'mias' }" @click="pestana = 'mias'">Mis ligas ({{ mias.length }})</button>
      <button class="pildora" :class="{ activa: pestana === 'publicas' }" @click="pestana = 'publicas'">Ligas públicas</button>
    </div>

    <div v-if="cargando" class="cargando">Cargando…</div>
    <div v-else class="grid grid-3">
      <div v-for="l in lista" :key="l.id" class="tarjeta liga" :class="{ 'tarjeta-acento': l.miEquipoId && l.miEquipoId === juego.activoId }">
        <div class="fila-entre cabeza-liga">
          <span class="avatar cuadrado" :class="{ lleno: l.miEquipoId && l.miEquipoId === juego.activoId }">{{ iniciales(l.nombre) }}</span>
          <span class="chip" :class="l.global ? 'chip-acento' : l.tipo === 'PRIVADO' ? '' : 'chip-verde'">
            {{ l.global ? 'Global' : l.tipo === 'PRIVADO' ? 'Privada' : 'Pública' }}
          </span>
        </div>
        <h3>{{ l.nombre }}</h3>
        <p class="tenue">
          {{ enteroEs(l.participantes) }}<template v-if="!l.global"> / {{ l.maxParticipantes }}</template> mánagers ·
          Presupuesto {{ millones(l.presupuestoInicial) }}
          <template v-if="l.creador"> · creada por {{ l.creador }}</template>
        </p>
        <div v-if="l.miEquipoId && juego.equipos[l.miEquipoId]" class="fila-entre mi-pos">
          <span class="micro">Tu posición</span>
          <span class="cifra">{{ l.global ? '#' + enteroEs(juego.equipos[l.miEquipoId].posicion) : ordinal(juego.equipos[l.miEquipoId].posicion) }}</span>
        </div>
        <div class="fila acciones-liga">
          <template v-if="l.miEquipoId">
            <RouterLink :to="`/ligas/${l.id}`" class="btn btn-peque">Clasificación</RouterLink>
            <RouterLink :to="`/equipos/${l.miEquipoId}`" class="btn btn-fantasma btn-peque">Mi equipo</RouterLink>
          </template>
          <button v-else class="btn btn-peque" :disabled="l.participantes >= l.maxParticipantes" @click="abrirUnirse(l)">
            {{ l.participantes >= l.maxParticipantes ? 'Completa' : 'Unirme' }}
          </button>
        </div>
      </div>
      <div v-if="!lista.length" class="tarjeta vacio">
        {{ pestana === 'mias' ? 'Todavía no estás en ninguna liga.' : 'No hay ligas públicas ahora mismo.' }}
      </div>
    </div>

    <Modal :abierto="modal === 'crear'" titulo="Crear liga" @cerrar="modal = ''">
      <form class="pila" @submit.prevent="crear">
        <div class="campo">
          <label for="nl">Nombre de la liga</label>
          <input id="nl" v-model.trim="nueva.nombre" class="input" maxlength="60" required />
        </div>
        <div class="campo">
          <span class="etiqueta-campo">Tipo</span>
          <div class="radio-grupo">
            <label class="check"><input v-model="nueva.tipo" type="radio" value="PRIVADO" /> Privada (solo con código)</label>
            <label class="check"><input v-model="nueva.tipo" type="radio" value="PUBLICO" /> Pública</label>
          </div>
        </div>
        <div class="grid grid-2">
          <div class="campo">
            <label for="pres">Presupuesto (M)</label>
            <input id="pres" v-model.number="nueva.presupuestoM" type="number" min="10" max="500" class="input" required />
          </div>
          <div class="campo">
            <label for="max">Máx. participantes</label>
            <input id="max" v-model.number="nueva.maxParticipantes" type="number" min="2" max="50" class="input" required />
          </div>
        </div>
        <div class="campo">
          <label for="ne">Nombre de tu equipo</label>
          <input id="ne" v-model.trim="nueva.nombreEquipo" class="input" maxlength="40" required />
        </div>
        <Alerta :mensaje="errorModal" />
        <button class="btn btn-bloque" :disabled="enviando">Crear liga</button>
      </form>
    </Modal>

    <Modal :abierto="modal === 'codigo'" titulo="Unirme con código" @cerrar="modal = ''">
      <form class="pila" @submit.prevent="unirseCodigo">
        <div class="campo">
          <label for="cod">Código de invitación</label>
          <input id="cod" v-model.trim="codigo" class="input codigo-input" maxlength="12" required placeholder="ABCD2345" />
        </div>
        <div class="campo">
          <label for="ne2">Nombre de tu equipo</label>
          <input id="ne2" v-model.trim="nombreEquipo" class="input" maxlength="40" required />
        </div>
        <Alerta :mensaje="errorModal" />
        <button class="btn btn-bloque" :disabled="enviando">Unirme</button>
      </form>
    </Modal>

    <Modal :abierto="modal === 'unirse'" :titulo="`Unirme a ${ligaElegida?.nombre}`" @cerrar="modal = ''">
      <form class="pila" @submit.prevent="unirse">
        <div class="campo">
          <label for="ne3">Nombre de tu equipo</label>
          <input id="ne3" v-model.trim="nombreEquipo" class="input" maxlength="40" required />
        </div>
        <Alerta :mensaje="errorModal" />
        <button class="btn btn-bloque" :disabled="enviando">Unirme</button>
      </form>
    </Modal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import Modal from '../../components/Modal.vue'
import Icono from '../../components/Icono.vue'
import { api } from '../../api/cliente'
import { useAuth } from '../../stores/auth'
import { useJuego } from '../../stores/juego'
import { enteroEs, iniciales, millones, ordinal } from '../../utils/formato'

const auth = useAuth()
const juego = useJuego()
const router = useRouter()
const pestana = ref('mias')
const mias = ref([])
const publicas = ref([])
const cargando = ref(true)
const error = ref('')
const modal = ref('')
const errorModal = ref('')
const enviando = ref(false)
const codigo = ref('')
const nombreEquipo = ref('')
const ligaElegida = ref(null)
const nueva = reactive({ nombre: '', tipo: 'PRIVADO', presupuestoM: 50, maxParticipantes: 10, nombreEquipo: '' })

const lista = computed(() => (pestana.value === 'mias' ? mias.value : publicas.value))
const nombrePorDefecto = () => `${auth.usuario?.nombre || 'Mi'} FC`.slice(0, 40)

async function cargar() {
  cargando.value = true
  try {
    ;[mias.value, publicas.value] = await Promise.all([api.get('/ligas/mias'), api.get('/ligas/publicas'), juego.cargar(true)])
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
}

function abrirCrear() {
  errorModal.value = ''
  nueva.nombreEquipo = nueva.nombreEquipo || nombrePorDefecto()
  modal.value = 'crear'
}
function abrirCodigo() {
  errorModal.value = ''
  nombreEquipo.value = nombreEquipo.value || nombrePorDefecto()
  modal.value = 'codigo'
}
function abrirUnirse(l) {
  errorModal.value = ''
  ligaElegida.value = l
  nombreEquipo.value = nombreEquipo.value || nombrePorDefecto()
  modal.value = 'unirse'
}

async function ejecutar(fn) {
  errorModal.value = ''
  enviando.value = true
  try {
    const liga = await fn()
    modal.value = ''
    await juego.cargar(true)
    juego.elegir(liga.miEquipoId)
    router.push(`/equipos/${liga.miEquipoId}/mercado`)
  } catch (e) {
    errorModal.value = e.message
  } finally {
    enviando.value = false
  }
}

const crear = () => ejecutar(() => api.post('/ligas', {
  nombre: nueva.nombre, tipo: nueva.tipo, presupuestoInicial: nueva.presupuestoM * 1_000_000,
  maxParticipantes: nueva.maxParticipantes, nombreEquipo: nueva.nombreEquipo
}))
const unirseCodigo = () => ejecutar(() => api.post('/ligas/unirse', { codigo: codigo.value, nombreEquipo: nombreEquipo.value }))
const unirse = () => ejecutar(() => api.post(`/ligas/${ligaElegida.value.id}/unirse`, { nombreEquipo: nombreEquipo.value }))

onMounted(cargar)
</script>

<style scoped>
.liga { display: flex; flex-direction: column; gap: 8px; }
.liga h3 { margin: 4px 0 0; font-size: 1.05rem; }
.liga p { margin: 0; }
.avatar.cuadrado { border-radius: 8px; width: 40px; height: 40px; }
.mi-pos { padding: 10px 0; border-top: 1px solid var(--borde); border-bottom: 1px solid var(--borde); }
.mi-pos .cifra { font-size: 1.4rem; }
.acciones-liga { margin-top: auto; padding-top: 4px; }
.codigo-input { text-transform: uppercase; letter-spacing: 0.15em; font-weight: 700; }
</style>
