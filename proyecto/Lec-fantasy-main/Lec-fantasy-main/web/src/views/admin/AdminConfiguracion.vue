<template>
  <div>
    <h1>Configuración</h1>
    <Alerta :mensaje="mensaje" :tipo="mensajeTipo" class="mb" />

    <div class="fila-entre mt">
      <h2>Mejoras</h2>
      <button class="btn btn-peque" @click="abrirMejora(null)">Nueva mejora</button>
    </div>
    <div class="tarjeta sin-relleno tabla-scroll">
      <table class="tabla">
        <thead><tr><th>Nombre</th><th>Condición</th><th class="num">Multiplicador</th><th class="num">Precio</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          <tr v-for="m in mejoras" :key="m.id">
            <td><b>{{ m.nombre }}</b><div class="tenue">{{ m.descripcion }}</div></td>
            <td>{{ CONDICIONES[m.condicion] }}<template v-if="m.umbral"> ≥ {{ m.umbral }}</template></td>
            <td class="num">×{{ m.multiplicadorBono }}</td>
            <td class="num">{{ millones(m.precio) }}</td>
            <td><span class="chip" :class="m.activa ? 'chip-verde' : ''">{{ m.activa ? 'Activa' : 'Inactiva' }}</span></td>
            <td class="derecha"><button class="btn btn-oscuro btn-peque" @click="abrirMejora(m)">Editar</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="fila-entre mt-2">
      <h2>Equipos de la LEC</h2>
      <button class="btn btn-peque" @click="abrirEquipo(null)">Nuevo equipo</button>
    </div>
    <div class="grid grid-3">
      <div v-for="e in equipos" :key="e.id" class="tarjeta fila-entre">
        <div class="fila">
          <img v-if="e.logoUrl" :src="e.logoUrl" alt="" class="logo-eq" />
          <div><b>{{ e.nombre }}</b><div class="tenue">{{ e.siglas || 'sin siglas' }}</div></div>
        </div>
        <button class="btn btn-oscuro btn-peque" @click="abrirEquipo(e)">Editar</button>
      </div>
      <div v-if="!equipos.length" class="tarjeta vacio">Se crean solos al importar la primera jornada.</div>
    </div>

    <Modal :abierto="modalMejora" :titulo="fm.id ? 'Editar mejora' : 'Nueva mejora'" @cerrar="modalMejora = false">
      <form class="pila" @submit.prevent="guardarMejora">
        <div class="campo"><label for="mn">Nombre</label><input id="mn" v-model.trim="fm.nombre" class="input" maxlength="60" required /></div>
        <div class="campo"><label for="md">Descripción</label><input id="md" v-model.trim="fm.descripcion" class="input" maxlength="255" /></div>
        <div class="grid grid-2">
          <div class="campo">
            <label for="mc">Condición</label>
            <select id="mc" v-model="fm.condicion" class="input">
              <option v-for="(t, k) in CONDICIONES" :key="k" :value="k">{{ t }}</option>
            </select>
          </div>
          <div class="campo">
            <label for="mu">Umbral</label>
            <input id="mu" v-model.number="fm.umbral" type="number" step="0.1" class="input" :disabled="fm.condicion !== 'KDA_MINIMO'" />
          </div>
          <div class="campo"><label for="mm">Multiplicador</label><input id="mm" v-model.number="fm.multiplicadorBono" type="number" min="2" max="10" class="input" required /></div>
          <div class="campo"><label for="mp">Precio (M)</label><input id="mp" v-model.number="fm.precioM" type="number" step="0.1" min="0" class="input" required /></div>
        </div>
        <label class="check"><input v-model="fm.activa" type="checkbox" /> Activa (se puede comprar)</label>
        <Alerta :mensaje="errorModal" />
        <button class="btn btn-bloque">Guardar</button>
      </form>
    </Modal>

    <Modal :abierto="modalEquipo" :titulo="fe.id ? 'Editar equipo' : 'Nuevo equipo'" @cerrar="modalEquipo = false">
      <form class="pila" @submit.prevent="guardarEquipo">
        <div class="campo"><label for="en">Nombre</label><input id="en" v-model.trim="fe.nombre" class="input" maxlength="100" required /></div>
        <div class="campo"><label for="es">Siglas</label><input id="es" v-model.trim="fe.siglas" class="input" maxlength="10" /></div>
        <div class="campo"><label for="el">URL del logo</label><input id="el" v-model.trim="fe.logoUrl" class="input" maxlength="500" /></div>
        <Alerta :mensaje="errorModal" />
        <button class="btn btn-bloque">Guardar</button>
      </form>
    </Modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import Modal from '../../components/Modal.vue'
import { api } from '../../api/cliente'
import { millones } from '../../utils/formato'

const CONDICIONES = {
  TRIPLE_KILL: 'Triple kill o más', QUADRA_KILL: 'Quadra kill o más', PENTA_KILL: 'Pentakill',
  FIRST_BLOOD: 'First blood', MVP: 'MVP de una partida', CERO_MUERTES: 'Sin muertes',
  KDA_MINIMO: 'KDA mínimo', VICTORIAS: 'Gana todas sus partidas'
}
const mejoras = ref([])
const equipos = ref([])
const mensaje = ref('')
const mensajeTipo = ref('ok')
const errorModal = ref('')
const modalMejora = ref(false)
const modalEquipo = ref(false)
const fm = reactive({ id: null, nombre: '', descripcion: '', condicion: 'TRIPLE_KILL', umbral: null, multiplicadorBono: 2, precioM: 1, activa: true })
const fe = reactive({ id: null, nombre: '', siglas: '', logoUrl: '' })

async function cargar() {
  ;[mejoras.value, equipos.value] = await Promise.all([api.get('/admin/mejoras'), api.get('/equipos-pro')])
}

function abrirMejora(m) {
  errorModal.value = ''
  Object.assign(fm, m
    ? { ...m, descripcion: m.descripcion || '', precioM: Number(m.precio) / 1e6 }
    : { id: null, nombre: '', descripcion: '', condicion: 'TRIPLE_KILL', umbral: null, multiplicadorBono: 2, precioM: 1, activa: true })
  modalMejora.value = true
}

async function guardarMejora() {
  const cuerpo = { nombre: fm.nombre, descripcion: fm.descripcion, condicion: fm.condicion,
    umbral: fm.condicion === 'KDA_MINIMO' ? fm.umbral : null, multiplicadorBono: fm.multiplicadorBono,
    precio: Math.round(fm.precioM * 1e6), activa: fm.activa }
  try {
    if (fm.id) await api.put(`/admin/mejoras/${fm.id}`, cuerpo)
    else await api.post('/admin/mejoras', cuerpo)
    modalMejora.value = false
    mensajeTipo.value = 'ok'
    mensaje.value = 'Mejora guardada.'
    cargar()
  } catch (e) {
    errorModal.value = e.message
  }
}

function abrirEquipo(e) {
  errorModal.value = ''
  Object.assign(fe, e ? { id: e.id, nombre: e.nombre, siglas: e.siglas || '', logoUrl: e.logoUrl || '' } : { id: null, nombre: '', siglas: '', logoUrl: '' })
  modalEquipo.value = true
}

async function guardarEquipo() {
  const cuerpo = { nombre: fe.nombre, siglas: fe.siglas, logoUrl: fe.logoUrl }
  try {
    if (fe.id) await api.put(`/admin/equipos-pro/${fe.id}`, cuerpo)
    else await api.post('/admin/equipos-pro', cuerpo)
    modalEquipo.value = false
    mensajeTipo.value = 'ok'
    mensaje.value = 'Equipo guardado.'
    cargar()
  } catch (e) {
    errorModal.value = e.message
  }
}

onMounted(() => cargar().catch((e) => { mensajeTipo.value = 'error'; mensaje.value = e.message }))
</script>

<style scoped>
.sin-relleno { padding: 0; overflow: hidden; }
.logo-eq { width: 36px; height: 36px; object-fit: contain; }
</style>
