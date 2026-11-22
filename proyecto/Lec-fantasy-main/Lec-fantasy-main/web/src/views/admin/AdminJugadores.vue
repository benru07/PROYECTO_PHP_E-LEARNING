<template>
  <div>
    <div class="fila-entre">
      <h1>Jugadores y precios</h1>
      <button class="btn" @click="abrir(null)">Nuevo jugador</button>
    </div>
    <p class="muted">Los jugadores y equipos se crean solos al importar partidas. Aquí puedes corregir datos y ajustar precios.</p>
    <Alerta :mensaje="mensaje" :tipo="mensajeTipo" class="mb" />
    <div class="fila mb">
      <input v-model.trim="texto" class="input buscar" placeholder="Buscar…" />
      <select v-model="posicion" class="input selector">
        <option value="">Todos los roles</option>
        <option v-for="p in POSICIONES" :key="p" :value="p">{{ p }}</option>
      </select>
    </div>
    <div class="tarjeta sin-relleno tabla-scroll">
      <table class="tabla">
        <thead><tr><th>Rol</th><th>Jugador</th><th>Equipo</th><th class="num">Partidas</th><th class="num">Puntos</th><th class="num">Valor</th><th></th></tr></thead>
        <tbody>
          <tr v-for="j in filtrados" :key="j.id">
            <td><Posicion :posicion="j.posicion" /></td>
            <td class="negrita">{{ j.nickname }} <span v-if="j.bloqueado" class="chip chip-rojo">bloqueado</span></td>
            <td class="muted">{{ j.equipo?.nombre || '—' }}</td>
            <td class="num">{{ j.partidasJugadas }}</td>
            <td class="num">{{ puntos(j.puntosTotales) }}</td>
            <td class="num negrita">{{ millones(j.valorMercado) }}</td>
            <td class="derecha"><button class="btn btn-oscuro btn-peque" @click="abrir(j)">Editar</button></td>
          </tr>
          <tr v-if="!filtrados.length"><td colspan="7" class="vacio">No hay jugadores.</td></tr>
        </tbody>
      </table>
    </div>

    <Modal :abierto="modal" :titulo="form.id ? `Editar ${form.nickname}` : 'Nuevo jugador'" @cerrar="modal = false">
      <form class="pila" @submit.prevent="guardar">
        <div class="grid grid-2">
          <div class="campo"><label for="nick">Nickname</label><input id="nick" v-model.trim="form.nickname" class="input" required maxlength="40" /></div>
          <div class="campo"><label for="nr">Nombre real</label><input id="nr" v-model.trim="form.nombreReal" class="input" maxlength="100" /></div>
          <div class="campo">
            <label for="pos">Rol</label>
            <select id="pos" v-model="form.posicion" class="input" required>
              <option v-for="p in POSICIONES" :key="p" :value="p">{{ NOMBRE_POSICION[p] }}</option>
            </select>
          </div>
          <div class="campo">
            <label for="eq">Equipo</label>
            <select id="eq" v-model="form.equipoId" class="input">
              <option :value="null">Sin equipo</option>
              <option v-for="e in equipos" :key="e.id" :value="e.id">{{ e.nombre }}</option>
            </select>
          </div>
          <div class="campo">
            <label for="val">Valor de mercado (M)</label>
            <input id="val" v-model.number="form.valorM" type="number" step="0.01" min="0.1" class="input" required />
          </div>
          <div class="campo"><label for="foto">URL de la foto</label><input id="foto" v-model.trim="form.fotoUrl" class="input" maxlength="500" /></div>
        </div>
        <Alerta :mensaje="errorModal" />
        <button class="btn btn-bloque">Guardar</button>
      </form>
    </Modal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import Modal from '../../components/Modal.vue'
import Posicion from '../../components/Posicion.vue'
import { api } from '../../api/cliente'
import { millones, puntos, POSICIONES, NOMBRE_POSICION } from '../../utils/formato'

const jugadores = ref([])
const equipos = ref([])
const texto = ref('')
const posicion = ref('')
const modal = ref(false)
const errorModal = ref('')
const mensaje = ref('')
const mensajeTipo = ref('ok')
const form = reactive({ id: null, nickname: '', nombreReal: '', posicion: 'TOP', equipoId: null, valorM: 8, fotoUrl: '' })

const filtrados = computed(() => jugadores.value.filter((j) =>
  (!posicion.value || j.posicion === posicion.value) &&
  (!texto.value || j.nickname.toLowerCase().includes(texto.value.toLowerCase()))))

async function cargar() {
  ;[jugadores.value, equipos.value] = await Promise.all([api.get('/admin/jugadores'), api.get('/equipos-pro')])
}

function abrir(j) {
  errorModal.value = ''
  Object.assign(form, j
    ? { id: j.id, nickname: j.nickname, nombreReal: j.nombreReal || '', posicion: j.posicion, equipoId: j.equipo?.id ?? null,
        valorM: Number(j.valorMercado) / 1e6, fotoUrl: j.fotoUrl || '' }
    : { id: null, nickname: '', nombreReal: '', posicion: 'TOP', equipoId: null, valorM: 8, fotoUrl: '' })
  modal.value = true
}

async function guardar() {
  errorModal.value = ''
  const cuerpo = { nickname: form.nickname, nombreReal: form.nombreReal, posicion: form.posicion, equipoId: form.equipoId,
    valorMercado: Math.round(form.valorM * 1e6), fotoUrl: form.fotoUrl }
  try {
    if (form.id) await api.put(`/admin/jugadores/${form.id}`, cuerpo)
    else await api.post('/admin/jugadores', cuerpo)
    modal.value = false
    mensajeTipo.value = 'ok'
    mensaje.value = `${form.nickname} guardado.`
    cargar()
  } catch (e) {
    errorModal.value = e.message
  }
}

onMounted(() => cargar().catch((e) => { mensajeTipo.value = 'error'; mensaje.value = e.message }))
</script>

<style scoped>
.sin-relleno { padding: 0; overflow: hidden; }
.buscar { max-width: 280px; }
.selector { width: auto; }
</style>
