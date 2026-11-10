<template>
  <div>
    <div class="fila-entre">
      <h1>Publicar noticias</h1>
      <button class="btn" @click="abrir(null)">Nueva noticia</button>
    </div>
    <Alerta :mensaje="mensaje" :tipo="mensajeTipo" class="mb" />
    <div class="pila mt">
      <div v-for="n in noticias" :key="n.id" class="tarjeta fila-entre">
        <div>
          <b>{{ n.titulo }}</b>
          <span v-if="!n.publicada" class="chip chip-amarillo">borrador</span>
          <div class="tenue">{{ fechaHora(n.fechaPublicacion) }}<template v-if="n.autor"> · {{ n.autor }}</template></div>
        </div>
        <div class="fila">
          <RouterLink v-if="n.publicada" :to="`/noticias/${n.id}`" class="btn btn-oscuro btn-peque">Ver</RouterLink>
          <button class="btn btn-oscuro btn-peque" @click="abrir(n)">Editar</button>
          <button class="btn btn-texto-rojo btn-peque" @click="eliminar(n)">Eliminar</button>
        </div>
      </div>
      <div v-if="!noticias.length" class="tarjeta vacio">Aún no hay noticias.</div>
    </div>

    <Modal :abierto="modal" :titulo="form.id ? 'Editar noticia' : 'Nueva noticia'" ancho="720px" @cerrar="modal = false">
      <form class="pila" @submit.prevent="guardar">
        <div class="campo"><label for="t">Título</label><input id="t" v-model.trim="form.titulo" class="input" maxlength="200" required /></div>
        <div class="campo"><label for="r">Resumen</label><input id="r" v-model.trim="form.resumen" class="input" maxlength="500" /></div>
        <div class="campo"><label for="c">Contenido</label><textarea id="c" v-model="form.contenido" class="input contenido" maxlength="10000" required></textarea></div>
        <div class="campo"><label for="i">URL de imagen</label><input id="i" v-model.trim="form.imagenUrl" class="input" maxlength="500" /></div>
        <label class="check"><input v-model="form.publicada" type="checkbox" /> Publicada (visible para todos)</label>
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
import { fechaHora } from '../../utils/formato'

const noticias = ref([])
const modal = ref(false)
const errorModal = ref('')
const mensaje = ref('')
const mensajeTipo = ref('ok')
const form = reactive({ id: null, titulo: '', resumen: '', contenido: '', imagenUrl: '', publicada: true })

async function cargar() {
  noticias.value = (await api.get('/admin/noticias', { tamano: 50 })).contenido
}

function abrir(n) {
  errorModal.value = ''
  Object.assign(form, n
    ? { id: n.id, titulo: n.titulo, resumen: n.resumen || '', contenido: n.contenido, imagenUrl: n.imagenUrl || '', publicada: n.publicada }
    : { id: null, titulo: '', resumen: '', contenido: '', imagenUrl: '', publicada: true })
  modal.value = true
}

async function guardar() {
  const cuerpo = { titulo: form.titulo, resumen: form.resumen, contenido: form.contenido, imagenUrl: form.imagenUrl, publicada: form.publicada }
  try {
    if (form.id) await api.put(`/admin/noticias/${form.id}`, cuerpo)
    else await api.post('/admin/noticias', cuerpo)
    modal.value = false
    mensajeTipo.value = 'ok'
    mensaje.value = 'Noticia guardada.'
    cargar()
  } catch (e) {
    errorModal.value = e.message
  }
}

async function eliminar(n) {
  if (!confirm(`¿Eliminar «${n.titulo}»?`)) return
  try {
    await api.del(`/admin/noticias/${n.id}`)
    cargar()
  } catch (e) {
    mensajeTipo.value = 'error'
    mensaje.value = e.message
  }
}

onMounted(() => cargar().catch((e) => { mensajeTipo.value = 'error'; mensaje.value = e.message }))
</script>

<style scoped>
.contenido { min-height: 220px; }
</style>
