<template>
  <MarcoAuth ancho="760px">
    <h1 class="centrado titulo">Crea tu cuenta</h1>
    <p class="centrado muted">Únete a la liga y ficha a tu primer equipo</p>
    <form class="pila mt" novalidate @submit.prevent="enviar">
      <div class="grid grid-2">
        <div class="campo">
          <label for="nombre">Nombre de usuario *</label>
          <input id="nombre" v-model.trim="form.nombre" class="input" placeholder="Ej: CapsFan99" autocomplete="username"
                 :class="{ invalido: errores.nombre }" />
          <span v-if="errores.nombre" class="error-campo">{{ errores.nombre }}</span>
        </div>
        <div class="campo">
          <label for="email">Correo electrónico *</label>
          <input id="email" v-model.trim="form.email" type="email" class="input" placeholder="tu@email.com"
                 autocomplete="email" :class="{ invalido: errores.email }" />
          <span v-if="errores.email" class="error-campo">{{ errores.email }}</span>
        </div>
        <div class="campo">
          <label for="password">Contraseña *</label>
          <input id="password" v-model="form.password" type="password" class="input" placeholder="Mín. 8 caracteres"
                 autocomplete="new-password" :class="{ invalido: errores.password }" />
          <span v-if="errores.password" class="error-campo">{{ errores.password }}</span>
        </div>
        <div class="campo">
          <label for="repetir">Repetir contraseña *</label>
          <input id="repetir" v-model="form.repetirPassword" type="password" class="input" autocomplete="new-password"
                 :class="{ invalido: errores.repetirPassword }" />
          <span v-if="errores.repetirPassword" class="error-campo">{{ errores.repetirPassword }}</span>
        </div>
      </div>
      <CamposPerfil :modelo="form" :errores="errores" />
      <div class="pila checks">
        <label class="check">
          <input v-model="form.aceptaTerminos" type="checkbox" />
          <span>Acepto los <RouterLink to="/privacidad" target="_blank">Términos de uso y la Política de privacidad</RouterLink> *</span>
        </label>
        <span v-if="errores.aceptaTerminos" class="error-campo">{{ errores.aceptaTerminos }}</span>
        <label class="check"><input v-model="form.aceptaNoticias" type="checkbox" /> Quiero recibir noticias y novedades de la LEC</label>
      </div>
      <Alerta :mensaje="error" />
      <button class="btn btn-grande btn-bloque" :disabled="enviando">{{ enviando ? 'Creando cuenta…' : 'Crear cuenta' }}</button>
    </form>
    <p class="centrado mt-2 mb-0">¿Ya tienes cuenta? <RouterLink to="/login" class="negrita">Inicia sesión</RouterLink></p>
  </MarcoAuth>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import MarcoAuth from '../../components/MarcoAuth.vue'
import Alerta from '../../components/Alerta.vue'
import CamposPerfil from '../../components/CamposPerfil.vue'
import { useAuth } from '../../stores/auth'

const auth = useAuth()
const router = useRouter()
const form = reactive({
  nombre: '', email: '', password: '', repetirPassword: '', fechaNacimiento: '', pais: '',
  equipoFavoritoId: null, riotId: '', aceptaTerminos: false, aceptaNoticias: false
})
const errores = ref({})
const error = ref('')
const enviando = ref(false)

function validar() {
  const e = {}
  if (!/^[A-Za-z0-9_]{3,20}$/.test(form.nombre)) e.nombre = 'Entre 3 y 20 caracteres: letras, números o _'
  if (!/^\S+@\S+\.\S+$/.test(form.email)) e.email = 'Correo no válido'
  if (form.password.length < 8) e.password = 'Mínimo 8 caracteres'
  if (form.password !== form.repetirPassword) e.repetirPassword = 'Las contraseñas no coinciden'
  if (!form.fechaNacimiento) e.fechaNacimiento = 'La fecha de nacimiento es obligatoria'
  if (!form.pais) e.pais = 'Elige tu país'
  if (form.riotId && !/^.{3,16}#[A-Za-z0-9]{2,5}$/.test(form.riotId)) e.riotId = 'Formato Nombre#TAG'
  if (!form.aceptaTerminos) e.aceptaTerminos = 'Debes aceptar los términos para continuar'
  errores.value = e
  return Object.keys(e).length === 0
}

async function enviar() {
  error.value = ''
  if (!validar()) return
  enviando.value = true
  try {
    await auth.registrar({ ...form, fechaNacimiento: form.fechaNacimiento || null, riotId: form.riotId || null })
    router.push({ name: 'panel', query: { bienvenida: '1' } })
  } catch (e) {
    error.value = e.message
    errores.value = e.campos || {}
  } finally {
    enviando.value = false
  }
}
</script>

<style scoped>
.titulo { margin: 18px 0 8px; font-size: 2rem; }
.checks { gap: 8px; }
.mb-0 { margin-bottom: 0; }
</style>
