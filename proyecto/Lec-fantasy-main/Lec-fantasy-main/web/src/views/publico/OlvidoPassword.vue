<template>
  <MarcoAuth>
    <h1 class="centrado titulo">Recuperar contraseña</h1>
    <template v-if="!enviado">
      <p class="muted centrado">Te enviaremos un enlace para elegir una contraseña nueva.</p>
      <form class="pila" @submit.prevent="enviar">
        <div class="campo">
          <label for="email">Correo electrónico</label>
          <input id="email" v-model.trim="email" type="email" class="input" required autocomplete="email" />
        </div>
        <Alerta :mensaje="error" />
        <button class="btn btn-grande btn-bloque" :disabled="enviando">Enviar enlace</button>
      </form>
    </template>
    <Alerta v-else tipo="ok" mensaje="Si existe una cuenta con ese correo, te hemos enviado un enlace. Caduca en 1 hora." />
    <p class="centrado mt-2"><RouterLink to="/login">Volver a iniciar sesión</RouterLink></p>
  </MarcoAuth>
</template>

<script setup>
import { ref } from 'vue'
import MarcoAuth from '../../components/MarcoAuth.vue'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'

const email = ref('')
const enviado = ref(false)
const enviando = ref(false)
const error = ref('')

async function enviar() {
  error.value = ''
  enviando.value = true
  try {
    await api.post('/auth/olvido-password', { email: email.value })
    enviado.value = true
  } catch (e) {
    error.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>

<style scoped>
.titulo { margin: 18px 0 12px; font-size: 1.8rem; }
</style>
