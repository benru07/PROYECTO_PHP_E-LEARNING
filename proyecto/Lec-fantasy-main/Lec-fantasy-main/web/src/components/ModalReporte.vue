<template>
  <Modal :abierto="abierto" :titulo="`Reportar a ${usuario}`" @cerrar="$emit('cerrar')">
    <Alerta v-if="hecho" tipo="ok" mensaje="Reporte enviado. Un administrador lo revisará." />
    <form v-else class="pila" @submit.prevent="enviar">
      <div class="campo">
        <label for="motivo">Motivo</label>
        <select id="motivo" v-model="motivo" class="input" required>
          <option value="TOXICIDAD">Toxicidad</option>
          <option value="NOMBRE_INAPROPIADO">Nombre inapropiado</option>
          <option value="TRAMPAS">Trampas</option>
          <option value="SPAM">Spam</option>
          <option value="OTRO">Otro</option>
        </select>
      </div>
      <div class="campo">
        <label for="desc">Descripción (opcional)</label>
        <textarea id="desc" v-model.trim="descripcion" class="input" maxlength="1000"></textarea>
      </div>
      <Alerta :mensaje="error" />
      <button class="btn btn-naranja btn-bloque" :disabled="enviando">Enviar reporte</button>
    </form>
  </Modal>
</template>

<script setup>
import { ref, watch } from 'vue'
import Modal from './Modal.vue'
import Alerta from './Alerta.vue'
import { api } from '../api/cliente'

const props = defineProps({ abierto: Boolean, usuarioId: { type: Number, default: null }, usuario: { type: String, default: '' } })
defineEmits(['cerrar'])
const motivo = ref('TOXICIDAD')
const descripcion = ref('')
const error = ref('')
const enviando = ref(false)
const hecho = ref(false)

watch(() => props.abierto, (a) => {
  if (a) {
    hecho.value = false
    error.value = ''
    descripcion.value = ''
  }
})

async function enviar() {
  enviando.value = true
  error.value = ''
  try {
    await api.post('/reportes', { usuarioReportadoId: props.usuarioId, motivo: motivo.value, descripcion: descripcion.value })
    hecho.value = true
  } catch (e) {
    error.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>
