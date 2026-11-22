<template>
  <div>
    <h1>Panel de administración</h1>
    <Alerta :mensaje="error" class="mb" />
    <div class="grid grid-4 mt">
      <div class="tarjeta estadistica"><div class="etiqueta">Usuarios</div><div class="valor">{{ n(d?.usuarios) }}</div></div>
      <div class="tarjeta estadistica"><div class="etiqueta">Activos hoy</div><div class="valor">{{ n(d?.activosHoy) }}</div></div>
      <div class="tarjeta estadistica"><div class="etiqueta">Baneados</div><div class="valor">{{ n(d?.baneados) }}</div></div>
      <div class="tarjeta estadistica"><div class="etiqueta">Jugadores bloqueados</div><div class="valor">{{ n(d?.jugadoresBloqueados) }}</div></div>
    </div>

    <h2 class="mt-2">Herramientas</h2>
    <div class="grid grid-2">
      <RouterLink to="/admin/usuarios" class="tarjeta herramienta">
        <b>Gestión de usuarios</b><span class="muted">Ver perfiles, banear temporalmente, eliminar</span>
      </RouterLink>
      <RouterLink to="/admin/bloqueos" class="tarjeta herramienta">
        <b>Bloqueo de jugadores LEC</b><span class="muted">Retirar del mercado y de los equipos</span>
      </RouterLink>
      <RouterLink to="/admin/jornadas" class="tarjeta herramienta">
        <b>Jornadas y puntuación</b><span class="muted">Importar partidas, cerrar jornadas y validar puntos</span>
      </RouterLink>
      <RouterLink to="/admin/reportes" class="tarjeta herramienta">
        <b>Reportes pendientes</b>
        <span class="muted">{{ d ? `${d.reportesPendientes} reporte${d.reportesPendientes === 1 ? '' : 's'} de usuarios por revisar` : '…' }}</span>
      </RouterLink>
    </div>

    <div class="tarjeta mt-2">
      <div class="fila-entre"><h3>Actividad reciente</h3><RouterLink to="/admin/actividad">Ver todo</RouterLink></div>
      <ul class="actividad">
        <li v-for="a in d?.actividadReciente || []" :key="a.id">{{ a.descripcion }} · <span class="tenue">{{ hace(a.fecha) }}</span></li>
        <li v-if="d && !d.actividadReciente.length" class="tenue">Sin actividad todavía.</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'
import { hace } from '../../utils/formato'

const d = ref(null)
const error = ref('')
const n = (v) => (v === undefined || v === null ? '…' : Number(v).toLocaleString('es-ES'))

onMounted(async () => {
  try {
    d.value = await api.get('/admin/dashboard')
  } catch (e) {
    error.value = e.message
  }
})
</script>

<style scoped>
.herramienta { color: var(--texto); display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 4px; text-align: center; min-height: 140px; }
.herramienta:hover { text-decoration: none; border-color: var(--acento); }
.actividad { list-style: none; padding: 0; margin: 8px 0 0; display: flex; flex-direction: column; gap: 6px; color: var(--texto-2); }
</style>
