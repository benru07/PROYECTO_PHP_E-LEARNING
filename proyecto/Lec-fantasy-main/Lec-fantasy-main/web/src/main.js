import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { configurarNoAutorizado } from './api/cliente'
import { useAuth } from './stores/auth'
import { useTema } from './stores/tema'
// Fuentes servidas con la propia web (sin depender de Google Fonts)
import '@fontsource/noto-sans/400.css'
import '@fontsource/noto-sans/500.css'
import '@fontsource/noto-sans/600.css'
import '@fontsource/noto-sans/700.css'
import '@fontsource/noto-sans/800.css'
import '@fontsource/noto-serif/700.css'
import '@fontsource/noto-serif/800.css'
import '@fontsource/noto-serif/900.css'
import './assets/base.css'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
// El tema se pinta antes de montar para que no haya parpadeo
useTema(pinia).iniciar()
app.use(router)

// Si el servidor rechaza el token (caducado, baneo, cuenta eliminada) se cierra la sesión
configurarNoAutorizado(() => {
  const auth = useAuth(pinia)
  auth.cerrarSesion()
  router.push({ name: 'login', query: { caducada: '1' } })
})

app.mount('#app')
