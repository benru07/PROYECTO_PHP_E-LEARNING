<template>
  <div>
    <h1>Gestión de usuarios</h1>
    <div class="filtros mt">
      <input v-model.trim="q" class="input" placeholder="Buscar por nombre, email o ID…" @input="buscarConRetraso" />
      <select v-model="estado" class="input" @change="buscar(0)">
        <option value="">Estado: Todos</option>
        <option value="ACTIVO">Activos</option>
        <option value="BANEADO">Baneados</option>
      </select>
      <select v-model="pais" class="input" @change="buscar(0)">
        <option value="">País: Todos</option>
        <option v-for="p in PAISES" :key="p.codigo" :value="p.codigo">{{ p.nombre }}</option>
      </select>
    </div>
    <Alerta :mensaje="error" class="mt" />

    <div class="tarjeta sin-relleno tabla-scroll mt">
      <table class="tabla">
        <thead><tr><th>Usuario</th><th>Email</th><th>País</th><th>Registro</th><th>Estado</th><th class="derecha">Acciones</th></tr></thead>
        <tbody>
          <tr v-for="u in filas" :key="u.id" :class="{ destacada: seleccionado?.id === u.id }">
            <td class="negrita">{{ u.nombre }} <span v-if="u.rol === 'ADMIN'" class="chip chip-acento">admin</span></td>
            <td class="muted">{{ u.email }}</td>
            <td>{{ nombrePais(u.pais) }}</td>
            <td class="muted">{{ fecha(u.fechaRegistro) }}</td>
            <td>
              <span v-if="!u.baneado">Activo</span>
              <b v-else class="texto-naranja">Baneado ({{ u.banPermanente ? 'permanente' : restantes(u.baneadoHasta) }})</b>
            </td>
            <td class="derecha acciones">
              <button class="btn btn-oscuro btn-peque" @click="ver(u.id)">Ver</button>
              <button v-if="u.baneado" class="btn btn-texto-verde btn-peque" @click="desbanear(u)">Desbanear</button>
              <button v-else class="btn btn-texto-naranja btn-peque" :disabled="u.rol === 'ADMIN'" @click="ver(u.id, true)">Banear</button>
              <button class="btn btn-texto-rojo btn-peque" :disabled="u.id === auth.usuario?.id" @click="confirmarEliminar(u)">Eliminar</button>
            </td>
          </tr>
          <tr v-if="!filas.length && !cargando"><td colspan="6" class="vacio">Ningún usuario coincide.</td></tr>
        </tbody>
      </table>
    </div>
    <div v-if="totalPaginas > 1" class="fila mt">
      <button class="btn btn-oscuro btn-peque" :disabled="pagina === 0" @click="buscar(pagina - 1)">Anterior</button>
      <span class="tenue">Página {{ pagina + 1 }} de {{ totalPaginas }} · {{ total }} usuarios</span>
      <button class="btn btn-oscuro btn-peque" :disabled="pagina + 1 >= totalPaginas" @click="buscar(pagina + 1)">Siguiente</button>
    </div>

    <div v-if="seleccionado" class="grid grid-2 mt-2">
      <div class="tarjeta perfil">
        <h3>Perfil seleccionado: {{ seleccionado.nombre }}</h3>
        <p>Email: {{ seleccionado.email }}</p>
        <p>Edad: {{ seleccionado.edad ?? '—' }} · País: {{ nombrePais(seleccionado.pais) }}</p>
        <p>Riot ID: {{ seleccionado.riotId || '—' }} · Equipo favorito: {{ seleccionado.equipoFavorito || '—' }}</p>
        <p>Registro: {{ fecha(seleccionado.fechaRegistro) }}</p>
        <p>Puntos: {{ puntos(seleccionado.puntosLigaGlobal) }} · Posición #{{ seleccionado.posicionLigaGlobal ?? '—' }} (Liga Global)</p>
        <p>Reportes recibidos: {{ seleccionado.reportesRecibidos }}</p>
        <p>Último acceso: {{ seleccionado.ultimoAcceso ? fechaHora(seleccionado.ultimoAcceso) : 'nunca' }}</p>
        <div class="fila mt">
          <span class="tenue">Rol:</span>
          <select v-model="rol" class="input selector" :disabled="seleccionado.id === auth.usuario?.id">
            <option value="USER">Usuario</option>
            <option value="ADMIN">Administrador</option>
          </select>
          <button class="btn btn-oscuro btn-peque" :disabled="rol === seleccionado.rol" @click="cambiarRol">Cambiar rol</button>
        </div>
        <template v-if="seleccionado.historialBaneos.length">
          <h3 class="mt">Historial de baneos</h3>
          <ul class="historial">
            <li v-for="b in seleccionado.historialBaneos" :key="b.id">
              {{ fecha(b.inicio) }} → {{ b.permanente ? 'permanente' : fecha(b.fin) }} · {{ MOTIVOS[b.motivo] }}
              <span v-if="b.admin" class="tenue">por {{ b.admin }}</span>
              <span v-if="b.revocadoEn" class="chip">revocado</span>
              <div v-if="b.comentario" class="tenue">«{{ b.comentario }}»</div>
            </li>
          </ul>
        </template>
      </div>

      <form class="tarjeta pila" @submit.prevent="banear">
        <h3>{{ seleccionado.baneado ? 'Usuario baneado' : 'Banear usuario' }}</h3>
        <template v-if="!seleccionado.baneado">
          <div class="campo">
            <span class="etiqueta-campo">Duración</span>
            <div class="radio-grupo">
              <label class="check"><input v-model="ban.duracion" type="radio" value="H24" /> 24 horas</label>
              <label class="check"><input v-model="ban.duracion" type="radio" value="D7" /> 7 días</label>
              <label class="check"><input v-model="ban.duracion" type="radio" value="D30" /> 30 días</label>
              <label class="check"><input v-model="ban.duracion" type="radio" value="PERSONALIZADO" /> Personalizado</label>
              <label class="check"><input v-model="ban.duracion" type="radio" value="PERMANENTE" /> Permanente</label>
            </div>
            <input v-if="ban.duracion === 'PERSONALIZADO'" v-model="ban.hasta" type="date" class="input" :min="manana" required />
          </div>
          <div class="campo">
            <label for="motivo">Motivo</label>
            <select id="motivo" v-model="ban.motivo" class="input" required>
              <option value="" disabled>Seleccionar</option>
              <option v-for="(t, k) in MOTIVOS" :key="k" :value="k">{{ t }}</option>
            </select>
          </div>
          <div class="campo">
            <label for="com">Comentario interno</label>
            <input id="com" v-model.trim="ban.comentario" class="input" maxlength="500" />
          </div>
          <label class="check"><input v-model="ban.notificar" type="checkbox" /> Notificar al usuario por email</label>
        </template>
        <p v-else class="muted">Este usuario ya está baneado. Puedes levantar el baneo desde la tabla.</p>
        <Alerta :mensaje="mensaje" :tipo="mensajeTipo" />
        <div class="fila botones">
          <button v-if="!seleccionado.baneado" class="btn btn-naranja" :disabled="seleccionado.rol === 'ADMIN'">Aplicar baneo</button>
          <button type="button" class="btn btn-rojo" :disabled="seleccionado.id === auth.usuario?.id"
                  @click="confirmarEliminar(seleccionado)">Eliminar de la BD</button>
        </div>
      </form>
    </div>

    <Modal :abierto="!!aEliminar" titulo="Eliminar usuario" @cerrar="aEliminar = null">
      <p>Vas a eliminar a <b>{{ aEliminar?.nombre }}</b> y todos sus equipos, fichajes y reportes. <b>No se puede deshacer.</b></p>
      <p class="muted">Escribe su nombre para confirmar:</p>
      <input v-model="confirmacion" class="input" />
      <div class="fila mt">
        <button class="btn btn-rojo" :disabled="confirmacion !== aEliminar?.nombre" @click="eliminar">Eliminar definitivamente</button>
        <button class="btn btn-oscuro" @click="aEliminar = null">Cancelar</button>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import Modal from '../../components/Modal.vue'
import { api } from '../../api/cliente'
import { useAuth } from '../../stores/auth'
import { PAISES, nombrePais } from '../../utils/paises'
import { aFecha, fecha, fechaHora, puntos, MOTIVOS } from '../../utils/formato'

const auth = useAuth()
const q = ref('')
const estado = ref('')
const pais = ref('')
const filas = ref([])
const pagina = ref(0)
const totalPaginas = ref(0)
const total = ref(0)
const cargando = ref(false)
const error = ref('')
const seleccionado = ref(null)
const rol = ref('USER')
const mensaje = ref('')
const mensajeTipo = ref('ok')
const aEliminar = ref(null)
const confirmacion = ref('')
const ban = reactive({ duracion: 'D7', hasta: '', motivo: '', comentario: '', notificar: true })
const manana = new Date(Date.now() + 86400000).toISOString().slice(0, 10)
let temporizador

function restantes(hasta) {
  const dias = Math.ceil((aFecha(hasta).getTime() - Date.now()) / 86400000)
  return dias <= 1 ? 'menos de 1 día' : `${dias} días restantes`
}

async function buscar(p = 0) {
  cargando.value = true
  error.value = ''
  try {
    const r = await api.get('/admin/usuarios', { q: q.value, estado: estado.value, pais: pais.value, pagina: p, tamano: 20 })
    filas.value = r.contenido
    pagina.value = r.pagina
    totalPaginas.value = r.totalPaginas
    total.value = r.totalElementos
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
}

function buscarConRetraso() {
  clearTimeout(temporizador)
  temporizador = setTimeout(() => buscar(0), 300)
}

async function ver(id, enfocarBaneo = false) {
  mensaje.value = ''
  try {
    seleccionado.value = await api.get(`/admin/usuarios/${id}`)
    rol.value = seleccionado.value.rol
    Object.assign(ban, { duracion: 'D7', hasta: '', motivo: '', comentario: '', notificar: true })
    if (enfocarBaneo) setTimeout(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }), 50)
  } catch (e) {
    error.value = e.message
  }
}

async function banear() {
  mensaje.value = ''
  if (!ban.motivo) {
    mensajeTipo.value = 'error'
    mensaje.value = 'Elige un motivo'
    return
  }
  try {
    seleccionado.value = await api.post(`/admin/usuarios/${seleccionado.value.id}/baneo`, {
      duracion: ban.duracion, hasta: ban.duracion === 'PERSONALIZADO' ? ban.hasta : null,
      motivo: ban.motivo, comentario: ban.comentario, notificar: ban.notificar
    })
    mensajeTipo.value = 'ok'
    mensaje.value = 'Baneo aplicado.'
    buscar(pagina.value)
  } catch (e) {
    mensajeTipo.value = 'error'
    mensaje.value = e.message
  }
}

async function desbanear(u) {
  try {
    seleccionado.value = await api.del(`/admin/usuarios/${u.id}/baneo`)
    rol.value = seleccionado.value.rol
    mensajeTipo.value = 'ok'
    mensaje.value = `${u.nombre} ya puede volver a jugar.`
    buscar(pagina.value)
  } catch (e) {
    error.value = e.message
  }
}

async function cambiarRol() {
  try {
    seleccionado.value = await api.put(`/admin/usuarios/${seleccionado.value.id}/rol`, { rol: rol.value })
    mensajeTipo.value = 'ok'
    mensaje.value = 'Rol actualizado.'
    buscar(pagina.value)
  } catch (e) {
    mensajeTipo.value = 'error'
    mensaje.value = e.message
  }
}

function confirmarEliminar(u) {
  confirmacion.value = ''
  aEliminar.value = u
}

async function eliminar() {
  try {
    await api.del(`/admin/usuarios/${aEliminar.value.id}`)
    if (seleccionado.value?.id === aEliminar.value.id) seleccionado.value = null
    aEliminar.value = null
    buscar(pagina.value)
  } catch (e) {
    error.value = e.message
    aEliminar.value = null
  }
}

onMounted(() => {
  try {
    const pendiente = sessionStorage.getItem('admin.buscar')
    if (pendiente) {
      q.value = pendiente
      sessionStorage.removeItem('admin.buscar')
    }
  } catch {
    /* sin almacenamiento */
  }
  buscar(0)
})
</script>

<style scoped>
.filtros { display: grid; grid-template-columns: 1fr 200px 220px; gap: 12px; }
@media (max-width: 760px) { .filtros { grid-template-columns: 1fr; } }
.sin-relleno { padding: 0; overflow: hidden; }
.acciones { white-space: nowrap; }
.acciones .btn { margin-left: 6px; }
.perfil p { margin: 0 0 6px; }
.selector { width: auto; padding: 6px 10px; }
.historial { padding-left: 18px; margin: 0; display: flex; flex-direction: column; gap: 6px; font-size: 0.9rem; }
.botones { margin-top: auto; }
</style>
