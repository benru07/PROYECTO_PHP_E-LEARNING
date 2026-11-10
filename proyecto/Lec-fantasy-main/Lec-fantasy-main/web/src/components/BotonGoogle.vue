<template>
  <div v-if="clientId" class="google">
    <div ref="contenedor" class="boton"></div>
  </div>
</template>

<script setup>
// "Continuar con Google" con Google Identity Services. Devuelve un ID token que valida el backend.
import { onMounted, ref } from 'vue'

const emit = defineEmits(['credencial', 'error'])
const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
const contenedor = ref(null)

function cargarScript() {
  return new Promise((resolve, reject) => {
    if (window.google?.accounts?.id) return resolve()
    const existente = document.getElementById('gsi-script')
    if (existente) {
      existente.addEventListener('load', resolve)
      return
    }
    const s = document.createElement('script')
    s.id = 'gsi-script'
    s.src = 'https://accounts.google.com/gsi/client'
    s.async = true
    s.onload = resolve
    s.onerror = reject
    document.head.appendChild(s)
  })
}

onMounted(async () => {
  if (!clientId) return
  try {
    await cargarScript()
    window.google.accounts.id.initialize({
      client_id: clientId,
      callback: (r) => emit('credencial', r.credential)
    })
    window.google.accounts.id.renderButton(contenedor.value, {
      theme: 'filled_black', size: 'large', text: 'continue_with', shape: 'rectangular', locale: 'es',
      width: Math.min(contenedor.value.offsetWidth || 360, 400)
    })
  } catch {
    emit('error', 'No se pudo cargar el inicio de sesión con Google')
  }
})
</script>

<style scoped>
.google { width: 100%; }
.boton { width: 100%; display: flex; justify-content: center; min-height: 44px; }
</style>
