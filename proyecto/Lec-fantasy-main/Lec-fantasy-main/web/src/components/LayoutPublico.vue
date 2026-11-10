<template>
  <div class="layout">
    <header class="cabecera">
      <div class="contenedor cabecera-in">
        <Logo />
        <nav class="nav" :class="{ abierta: menu }">
          <RouterLink to="/" exact-active-class="activo" @click="menu = false">Inicio</RouterLink>
          <RouterLink to="/objetivos" active-class="activo" @click="menu = false">Objetivos</RouterLink>
          <RouterLink to="/como-funciona" active-class="activo" @click="menu = false">Cómo funciona</RouterLink>
          <RouterLink to="/noticias" active-class="activo" @click="menu = false">Noticias</RouterLink>
        </nav>
        <div class="acciones">
          <template v-if="auth.autenticado">
            <RouterLink :to="rutaInicial(auth)" class="btn">Ir a mi panel</RouterLink>
          </template>
          <template v-else>
            <RouterLink to="/login" class="btn btn-borde">Iniciar sesión</RouterLink>
            <RouterLink to="/registro" class="btn ocultar-movil">Registrarse</RouterLink>
          </template>
          <button class="hamburguesa" aria-label="Menú" @click="menu = !menu">☰</button>
        </div>
      </div>
    </header>
    <main><RouterView /></main>
    <Pie />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Logo from './Logo.vue'
import Pie from './Pie.vue'
import { useAuth } from '../stores/auth'
import { rutaInicial } from '../utils/rutas'

const auth = useAuth()
const menu = ref(false)
</script>

<style scoped>
.layout { min-height: 100vh; display: flex; flex-direction: column; }
main { flex: 1; }
.cabecera { background: var(--fondo-2); border-bottom: 1px solid var(--borde); position: sticky; top: 0; z-index: 20; }
.cabecera-in { display: flex; align-items: center; gap: 24px; padding-top: 14px; padding-bottom: 14px; }
.nav { display: flex; gap: 28px; margin-left: 40px; }
.nav a { color: var(--texto-2); font-weight: 500; font-size: 1.02rem; }
.nav a:hover { color: var(--texto); text-decoration: none; }
.nav a.activo { color: var(--texto); font-weight: 700; }
.acciones { margin-left: auto; display: flex; gap: 12px; align-items: center; }
.hamburguesa { display: none; background: none; border: 1px solid var(--borde); color: var(--texto); border-radius: 8px; padding: 6px 10px; font-size: 1.1rem; cursor: pointer; }
@media (max-width: 860px) {
  .nav { display: none; position: absolute; top: 100%; left: 0; right: 0; flex-direction: column; gap: 0; margin: 0; background: var(--fondo-2); border-bottom: 1px solid var(--borde); }
  .nav.abierta { display: flex; }
  .nav a { padding: 14px 20px; border-top: 1px solid var(--borde); }
  .hamburguesa { display: block; }
  .ocultar-movil { display: none; }
}
</style>
