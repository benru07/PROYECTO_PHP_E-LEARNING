<template>
  <div>
    <h1>Bloqueo de jugadores</h1>
    <p class="muted">Al bloquear a un jugador sale del mercado y se retira de todos los equipos. Cada mánager recibe su valor de mercado actual.</p>
    <Alerta :mensaje="mensaje" :tipo="mensajeTipo" class="mb" />

    <h2 class="mt">Bloqueados ({{ bloqueados.length }})</h2>
    <div class="tarjeta sin-relleno tabla-scroll">
      <table class="tabla">
        <thead><tr><th>Rol</th><th>Jugador</th><th>Equipo</th><th>Motivo</th><th></th></tr></thead>
        <tbody>
          <tr v-for="j in bloqueados" :key="j.id">
            <td><Posicion :posicion="j.posicion" /></td>
            <td class="negrita">{{ j.nickname }}</td>
            <td class="muted">{{ j.equipo?.nombre || '—' }}</td>
            <td>{{ j.motivoBloqueo }}</td>
            <td class="derecha"><button class="btn btn-texto-verde btn-peque" @click="desbloquear(j)">Desbloquear</button></td>
          </tr>
          <tr v-if="!bloqueados.length"><td colspan="5" class="vacio">No hay jugadores bloqueados.</td></tr>
        </tbody>
      </table>
    </div>

    <h2 class="mt-2">Bloquear un jugador</h2>
    <input v-model.trim="texto" class="input buscar mb" placeholder="Buscar jugador…" />
    <div class="tarjeta sin-relleno tabla-scroll">
      <table class="tabla">
        <thead><tr><th>Rol</th><th>Jugador</th><th>Equipo</th><th class="num">Valor</th><th></th></tr></thead>
        <tbody>
          <tr v-for="j in disponibles" :key="j.id">
            <td><Posicion :posicion="j.posicion" /></td>
            <td class="negrita">{{ j.nickname }}</td>
            <td class="muted">{{ j.equipo?.nombre || '—' }}</td>
            <td class="num">{{ millones(j.valorMercado) }}</td>
            <td class="derecha"><button class="btn btn-texto-rojo btn-peque" @click="abrir(j)">Bloquear</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <Modal :abierto="!!elegido" :titulo="`Bloquear a ${elegido?.nickname}`" @cerrar="elegido = null">
      <form class="pila" @submit.prevent="bloquear">
        <div class="campo">
          <label for="mot">Motivo</label>
          <input id="mot" v-model.trim="motivo" class="input" maxlength="255" required placeholder="Ej: sancionado por la liga, deja el equipo…" />
        </div>
        <button class="btn btn-rojo btn-bloque">Bloquear y retirar de los equipos</button>
      </form>
    </Modal>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import Modal from '../../components/Modal.vue'
import Posicion from '../../components/Posicion.vue'
import { api } from '../../api/cliente'
import { millones } from '../../utils/formato'

const jugadores = ref([])
const texto = ref('')
const elegido = ref(null)
const motivo = ref('')
const mensaje = ref('')
const mensajeTipo = ref('ok')

const bloqueados = computed(() => jugadores.value.filter((j) => j.bloqueado))
const disponibles = computed(() => jugadores.value
  .filter((j) => !j.bloqueado && (!texto.value || j.nickname.toLowerCase().includes(texto.value.toLowerCase())))
  .slice(0, 50))

async function cargar() {
  try {
    jugadores.value = await api.get('/admin/jugadores')
  } catch (e) {
    mensajeTipo.value = 'error'
    mensaje.value = e.message
  }
}

function abrir(j) {
  motivo.value = ''
  elegido.value = j
}

async function bloquear() {
  try {
    await api.post(`/admin/jugadores/${elegido.value.id}/bloqueo`, { motivo: motivo.value })
    mensajeTipo.value = 'ok'
    mensaje.value = `${elegido.value.nickname} bloqueado y retirado de los equipos.`
    elegido.value = null
    cargar()
  } catch (e) {
    mensajeTipo.value = 'error'
    mensaje.value = e.message
  }
}

async function desbloquear(j) {
  try {
    await api.del(`/admin/jugadores/${j.id}/bloqueo`)
    mensajeTipo.value = 'ok'
    mensaje.value = `${j.nickname} vuelve a estar en el mercado.`
    cargar()
  } catch (e) {
    mensajeTipo.value = 'error'
    mensaje.value = e.message
  }
}

onMounted(cargar)
</script>

<style scoped>
.sin-relleno { padding: 0; overflow: hidden; }
.buscar { max-width: 320px; }
</style>
