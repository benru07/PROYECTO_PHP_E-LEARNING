<template>
  <MarcoAuth>
    <h1 class="centrado titulo">Iniciar sesión</h1>
    <Alerta v-if="route.query.caducada" tipo="aviso" mensaje="Tu sesión ha terminado. Vuelve a iniciar sesión." class="mb" />
    <Alerta v-if="route.query.restablecida" tipo="ok" mensaje="Contraseña cambiada. Ya puedes entrar." class="mb" />
    <form class="pila" @submit.prevent="entrar">
      <div class="campo">
        <label for="identificador">Nombre de usuario o email</label>
        <input id="identificador" v-model.trim="identificador" class="input" autocomplete="username"
               placeholder="usuario@email.com" required />
      </div>
      <div class="campo">
        <label for="password">Contraseña</label>
        <input id="password" v-model="password" type="password" class="input" autocomplete="current-password" required />
      </div>
      <div class="fila-entre">
        <label class="check"><input v-model="recordarme" type="checkbox" /> Recordarme</label>
        <RouterLink to="/olvido-password" class="peque">¿Olvidaste tu contraseña?</RouterLink>
      </div>
      <Alerta :mensaje="error" />
      <button class="btn btn-grande btn-bloque" :disabled="enviando">{{ enviando ? 'Entrando…' : 'Entrar' }}</button>
    </form>

    <template v-if="hayGoogle">
      <div class="separador-texto"><span>o continúa con</span></div>
      <BotonGoogle @credencial="entrarConGoogle" @error="error = $event" />
    </template>

    <p class="centrado mt-2 mb-0">
      ¿No tienes cuenta? <RouterLink to="/registro" class="negrita">Regístrate aquí</RouterLink>
    </p>
  </MarcoAuth>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MarcoAuth from '../../components/MarcoAuth.vue'
import Alerta from '../../components/Alerta.vue'
import BotonGoogle from '../../components/BotonGoogle.vue'
import { useAuth } from '../../stores/auth'
import { rutaInicial } from '../../utils/rutas'

const auth = useAuth()
const route = useRoute()
const router = useRouter()
const identificador = ref('')
const password = ref('')
const recordarme = ref(false)
const error = ref('')
const enviando = ref(false)
const hayGoogle = !!import.meta.env.VITE_GOOGLE_CLIENT_ID

function continuar() {
  const volver = route.query.volver
  const valida = typeof volver === 'string' && volver.startsWith('/') && !volver.startsWith('//')
  router.push(valida && auth.usuario?.perfilCompleto ? volver : rutaInicial(auth))
}

async function entrar() {
  error.value = ''
  enviando.value = true
  try {
    await auth.login(identificador.value, password.value, recordarme.value)
    continuar()
  } catch (e) {
    error.value = e.message
  } finally {
    enviando.value = false
  }
}

async function entrarConGoogle(credencial) {
  error.value = ''
  try {
    await auth.loginGoogle(credencial)
    continuar()
  } catch (e) {
    error.value = e.message
  }
}
</script>

<style scoped>
.titulo { margin: 18px 0 24px; font-size: 2rem; }
.peque { font-size: 0.9rem; }
.mb-0 { margin-bottom: 0; }
.separador-texto { display: flex; align-items: center; gap: 12px; color: var(--texto-2); font-size: 0.9rem; margin: 24px 0 16px; }
.separador-texto::before, .separador-texto::after { content: ''; flex: 1; border-top: 1px solid var(--borde); }
</style>
