<template>
  <div>
    <h1>Registro de actividad</h1>
    <Alerta :mensaje="error" class="mb" />
    <div class="tarjeta sin-relleno tabla-scroll mt">
      <table class="tabla">
        <thead><tr><th>Fecha</th><th>Administrador</th><th>Tipo</th><th>Descripción</th></tr></thead>
        <tbody>
          <tr v-for="a in filas" :key="a.id">
            <td class="muted">{{ fechaHora(a.fecha) }}</td>
            <td>{{ a.admin }}</td>
            <td><span class="chip">{{ a.tipo }}</span></td>
            <td>{{ a.descripcion }}</td>
          </tr>
          <tr v-if="!filas.length"><td colspan="4" class="vacio">Sin actividad.</td></tr>
        </tbody>
      </table>
    </div>
    <div v-if="totalPaginas > 1" class="fila mt">
      <button class="btn btn-oscuro btn-peque" :disabled="pagina === 0" @click="cargar(pagina - 1)">Anterior</button>
      <span class="tenue">Página {{ pagina + 1 }} de {{ totalPaginas }}</span>
      <button class="btn btn-oscuro btn-peque" :disabled="pagina + 1 >= totalPaginas" @click="cargar(pagina + 1)">Siguiente</button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'
import { fechaHora } from '../../utils/formato'

const filas = ref([])
const pagina = ref(0)
const totalPaginas = ref(0)
const error = ref('')

async function cargar(p = 0) {
  try {
    const r = await api.get('/admin/actividad', { pagina: p, tamano: 30 })
    filas.value = r.contenido
    pagina.value = r.pagina
    totalPaginas.value = r.totalPaginas
  } catch (e) {
    error.value = e.message
  }
}

onMounted(() => cargar(0))
</script>

<style scoped>
.sin-relleno { padding: 0; overflow: hidden; }
</style>
