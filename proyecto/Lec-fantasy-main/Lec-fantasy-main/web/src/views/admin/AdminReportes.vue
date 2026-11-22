<template>
  <div>
    <h1>Reportes y moderación</h1>
    <div class="pestanas mt">
      <button v-for="e in estados" :key="e.id" class="pestana" :class="{ activa: estado === e.id }"
              @click="estado = e.id; cargar()">{{ e.nombre }}</button>
    </div>
    <Alerta :mensaje="error" class="mb" />
    <div class="pila">
      <div v-for="r in reportes" :key="r.id" class="tarjeta">
        <div class="fila-entre">
          <div>
            <b>{{ r.reportado }}</b> <span class="muted">reportado por {{ r.reportador }}</span>
            <span class="chip chip-naranja">{{ MOTIVOS[r.motivo] }}</span>
          </div>
          <span class="tenue">{{ fechaHora(r.fecha) }}</span>
        </div>
        <p v-if="r.descripcion" class="mt">{{ r.descripcion }}</p>
        <p v-if="r.estado !== 'PENDIENTE'" class="tenue">
          {{ r.estado === 'RESUELTO' ? 'Resuelto' : 'Descartado' }} por {{ r.admin }} el {{ fechaHora(r.fechaResolucion) }}<template v-if="r.resolucion">: {{ r.resolucion }}</template>
        </p>
        <div v-else class="fila mt">
          <input v-model.trim="notas[r.id]" class="input nota" placeholder="Nota de resolución (opcional)" />
          <button class="btn btn-peque" @click="resolver(r, 'RESUELTO')">Marcar resuelto</button>
          <button class="btn btn-oscuro btn-peque" @click="resolver(r, 'DESCARTADO')">Descartar</button>
          <RouterLink :to="{ name: 'admin-usuarios' }" class="btn btn-texto-naranja btn-peque"
                      @click="buscarUsuario(r.reportado)">Ir al usuario</RouterLink>
        </div>
      </div>
      <div v-if="!reportes.length" class="tarjeta vacio">No hay reportes en esta lista.</div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'
import { fechaHora, MOTIVOS } from '../../utils/formato'

const estados = [
  { id: 'PENDIENTE', nombre: 'Pendientes' },
  { id: 'RESUELTO', nombre: 'Resueltos' },
  { id: 'DESCARTADO', nombre: 'Descartados' },
  { id: '', nombre: 'Todos' }
]
const estado = ref('PENDIENTE')
const reportes = ref([])
const notas = reactive({})
const error = ref('')

async function cargar() {
  try {
    reportes.value = await api.get('/admin/reportes', { estado: estado.value })
  } catch (e) {
    error.value = e.message
  }
}

async function resolver(r, nuevo) {
  try {
    await api.post(`/admin/reportes/${r.id}/resolver`, { estado: nuevo, resolucion: notas[r.id] || '' })
    cargar()
  } catch (e) {
    error.value = e.message
  }
}

function buscarUsuario(nombre) {
  try {
    sessionStorage.setItem('admin.buscar', nombre)
  } catch {
    /* sin almacenamiento */
  }
}

onMounted(cargar)
</script>

<style scoped>
.nota { flex: 1; min-width: 200px; }
</style>
