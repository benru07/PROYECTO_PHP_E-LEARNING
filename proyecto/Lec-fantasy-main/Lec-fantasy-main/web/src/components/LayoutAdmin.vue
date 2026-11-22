<template>
  <div class="admin">
    <aside class="lateral" :class="{ abierto: menu }">
      <Logo destino="/admin" admin />
      <nav class="enlaces" @click="menu = false">
        <RouterLink to="/admin" exact-active-class="activo">Panel de control</RouterLink>
        <RouterLink to="/admin/usuarios" active-class="activo">Gestión de usuarios</RouterLink>
        <RouterLink to="/admin/bloqueos" active-class="activo">Bloqueo de jugadores</RouterLink>
        <RouterLink to="/admin/jugadores" active-class="activo">Jugadores y precios</RouterLink>
        <RouterLink to="/admin/jornadas" active-class="activo">Jornadas y puntuación</RouterLink>
        <RouterLink to="/admin/noticias" active-class="activo">Publicar noticias</RouterLink>
        <RouterLink to="/admin/reportes" active-class="activo">Reportes y moderación</RouterLink>
        <RouterLink to="/admin/actividad" active-class="activo">Registro de actividad</RouterLink>
      </nav>
      <div class="abajo">
        <RouterLink to="/admin/configuracion" active-class="activo" @click="menu = false">Configuración</RouterLink>
        <RouterLink to="/panel" @click="menu = false">Ir al juego</RouterLink>
        <button class="cerrar" @click="salir">Cerrar sesión</button>
      </div>
    </aside>
    <div class="cuerpo">
      <div class="barra-movil">
        <Logo destino="/admin" admin />
        <button class="hamburguesa" aria-label="Menú" @click="menu = !menu">☰</button>
      </div>
      <main class="contenido"><RouterView /></main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Logo from './Logo.vue'
import { useAuth } from '../stores/auth'

const auth = useAuth()
const router = useRouter()
const menu = ref(false)

function salir() {
  auth.cerrarSesion()
  router.push('/')
}
</script>

<style scoped>
.admin { display: flex; min-height: 100vh; }
.lateral {
  width: 260px; flex-shrink: 0; background: var(--tarjeta); border-right: 1px solid var(--borde);
  padding: 28px 20px; display: flex; flex-direction: column; gap: 28px;
  position: sticky; top: 0; height: 100vh; overflow-y: auto;
}
.enlaces, .abajo { display: flex; flex-direction: column; gap: 4px; }
.abajo { margin-top: auto; }
.enlaces a, .abajo a, .cerrar {
  color: var(--texto); padding: 10px 6px; border-radius: 8px; font-weight: 500; background: none; border: 0; font: inherit; text-align: left; cursor: pointer;
}
.enlaces a:hover, .abajo a:hover { text-decoration: none; background: var(--tarjeta-2); }
.enlaces a.activo, .abajo a.activo { background: var(--acento); color: var(--acento-texto); font-weight: 700; }
.cerrar { color: var(--rojo); }
.cuerpo { flex: 1; min-width: 0; }
.contenido { padding: 40px 48px 64px; max-width: 1280px; }
.barra-movil { display: none; }
.hamburguesa { background: none; border: 1px solid var(--borde); color: var(--texto); border-radius: 8px; padding: 6px 10px; font-size: 1.1rem; cursor: pointer; }
@media (max-width: 900px) {
  .lateral { position: fixed; left: 0; top: 0; z-index: 40; transform: translateX(-100%); transition: transform 0.2s; box-shadow: var(--sombra); }
  .lateral.abierto { transform: none; }
  .barra-movil { display: flex; justify-content: space-between; align-items: center; padding: 14px 16px; border-bottom: 1px solid var(--borde); background: var(--tarjeta); }
  .contenido { padding: 24px 16px 48px; }
}
</style>
