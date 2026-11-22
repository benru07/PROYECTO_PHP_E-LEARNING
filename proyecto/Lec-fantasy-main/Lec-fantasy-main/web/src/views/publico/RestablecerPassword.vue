<template>
  <MarcoAuth>
    <h1 class="centrado titulo">Nueva contraseña</h1>
    <Alerta v-if="!token" mensaje="El enlace no es válido. Pide uno nuevo." />
    <form v-else class="pila" @submit.prevent="enviar">
      <div class="campo">
        <label for="p1">Contraseña nueva</label>
        <input id="p1" v-model="password" type="password" class="input" minlength="8" required autocomplete="new-password" />
      </div>
      <div class="campo">
        <label for="p2">Repítela</label>
        <input id="p2" v-model="repetir" type="password" class="input" required autocomplete="new-password" />
      </div>
      <Alerta :mensaje="error" />
      <button class="btn btn-grande btn-bloque" :disabled="enviando">Guardar contraseña</button>
    </form>
    <p class="centrado mt-2"><RouterLink to="/olvido-password">Pedir otro enlace</RouterLink></p>
  </MarcoAuth>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MarcoAuth from '../../components/MarcoAuth.vue'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'

const route = useRoute()
const router = useRouter()
const token = typeof route.query.token === 'string' ? route.query.token : ''
const password = ref('')
const repetir = ref('')
const error = ref('')
const enviando = ref(false)

async function enviar() {
  error.value = ''
  if (password.value.length < 8) return (error.value = 'Mínimo 8 caracteres')
  if (password.value !== repetir.value) return (error.value = 'Las contraseñas no coinciden')
  enviando.value = true
  try {
    await api.post('/auth/restablecer-password', { token, password: password.value })
    router.push({ name: 'login', query: { restablecida: '1' } })
  } catch (e) {
    error.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>

<style scoped>
.titulo { margin: 18px 0 16px; font-size: 1.8rem; }
</style>
