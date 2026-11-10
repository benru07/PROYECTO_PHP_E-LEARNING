<template>
  <div class="layout">
    <header class="cabecera">
      <div class="contenedor cabecera-in">
        <Logo destino="/panel" />
        <nav class="nav ocultar-movil" aria-label="Principal">
          <RouterLink v-for="e in enlaces" :key="e.texto" :to="e.to" :class="{ activo: e.activo }">{{ e.texto }}</RouterLink>
        </nav>

        <div class="acciones">
          <RouterLink v-if="juego.proxima && !juego.enCurso" to="/mi-equipo" class="pastilla aviso ocultar-estrecho"
                      title="Tiempo para cambiar tu alineación antes de la próxima jornada">
            <Icono nombre="reloj" :tam="14" />
            <span class="ocultar-medio">Cierre de alineación</span>
            <b><CuentaAtras :hasta="juego.proxima.fechaInicio" corto /></b>
          </RouterLink>
          <span v-else-if="juego.enCurso" class="pastilla aviso ocultar-estrecho">
            <span class="punto-vivo" /> {{ juego.enCurso.nombre }} en juego
          </span>
          <template v-if="juego.equipoActivo">
            <span class="pastilla ocultar-movil">Puntos <b>{{ puntos(juego.equipoActivo.puntosTotales) }}</b></span>
            <span class="pastilla">Saldo <b>{{ millones(juego.equipoActivo.dineroDisponible) }}</b></span>
          </template>

          <div class="menu-usuario">
            <button class="avatar lleno boton-avatar" :aria-expanded="menu" aria-label="Menú de usuario"
                    @click.stop="menu = !menu">{{ iniciales(auth.usuario?.nombre) }}</button>
            <div v-if="menu" class="desplegable" @click.stop>
              <div class="quien">
                <b>{{ auth.usuario?.nombre }}</b>
                <span class="tenue">{{ auth.usuario?.email }}</span>
              </div>
              <div v-if="juego.ligas.length > 1" class="bloque">
                <span class="micro">Jugando en</span>
                <button v-for="l in juego.ligas" :key="l.id" class="opcion-liga"
                        :class="{ activa: l.miEquipoId === juego.activoId }" @click="cambiarLiga(l)">
                  <span>{{ l.nombre }}</span>
                  <Icono v-if="l.miEquipoId === juego.activoId" nombre="check" :tam="16" />
                </button>
              </div>
              <div class="bloque">
                <RouterLink to="/perfil" @click="menu = false"><Icono nombre="cuenta" :tam="16" /> Mi cuenta y apariencia</RouterLink>
                <RouterLink to="/jornadas" @click="menu = false"><Icono nombre="calendario" :tam="16" /> Jornadas</RouterLink>
                <RouterLink to="/noticias" @click="menu = false"><Icono nombre="noticias" :tam="16" /> Noticias</RouterLink>
                <RouterLink v-if="auth.esAdmin" to="/admin" @click="menu = false"><Icono nombre="escudo" :tam="16" /> Administración</RouterLink>
                <button @click="salir"><Icono nombre="salir" :tam="16" /> Cerrar sesión</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="contenedor pagina"><RouterView /></main>
    <Pie class="ocultar-movil" />

    <!-- Navegación inferior en el móvil -->
    <nav class="nav-movil solo-movil" aria-label="Principal">
      <RouterLink v-for="e in enlacesMovil" :key="e.texto" :to="e.to" :class="{ activo: e.activo }">
        <Icono :nombre="e.icono" :tam="21" />
        <span>{{ e.corto || e.texto }}</span>
      </RouterLink>
    </nav>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Logo from './Logo.vue'
import Pie from './Pie.vue'
import Icono from './Icono.vue'
import CuentaAtras from './CuentaAtras.vue'
import { useAuth } from '../stores/auth'
import { useJuego } from '../stores/juego'
import { iniciales, millones, puntos } from '../utils/formato'

const auth = useAuth()
const juego = useJuego()
const route = useRoute()
const router = useRouter()
const menu = ref(false)

const en = (...nombres) => nombres.includes(route.name)
const enlaces = computed(() => [
  { texto: 'Inicio', to: '/panel', icono: 'inicio', activo: en('panel') },
  { texto: 'Mi equipo', corto: 'Equipo', to: '/mi-equipo', icono: 'equipo', activo: en('equipo') },
  { texto: 'Mercado', to: '/mercado', icono: 'mercado', activo: en('mercado', 'jugador') },
  { texto: 'Mis ligas', corto: 'Ligas', to: '/ligas', icono: 'ligas', activo: en('ligas', 'liga') },
  { texto: 'Estadísticas', corto: 'Stats', to: '/estadisticas', icono: 'estadisticas', activo: en('estadisticas', 'partida', 'jornadas') },
  { texto: 'Mi cuenta', corto: 'Cuenta', to: '/perfil', icono: 'cuenta', activo: en('perfil') }
])
const enlacesMovil = computed(() => enlaces.value.slice(0, 5))

function cambiarLiga(l) {
  juego.elegir(l.miEquipoId)
  menu.value = false
  // Si se está viendo el equipo o el mercado, se cambia al de la liga elegida
  if (route.name === 'equipo') router.push(`/equipos/${l.miEquipoId}`)
  else if (route.name === 'mercado') router.push(`/equipos/${l.miEquipoId}/mercado`)
}

function salir() {
  menu.value = false
  auth.cerrarSesion()
  router.push('/')
}

const cerrarMenu = () => (menu.value = false)
onMounted(() => {
  document.addEventListener('click', cerrarMenu)
  juego.cargar().catch(() => {})
})
onBeforeUnmount(() => document.removeEventListener('click', cerrarMenu))
watch(() => route.fullPath, cerrarMenu)
</script>

<style scoped>
.layout { min-height: 100vh; display: flex; flex-direction: column; }
main { flex: 1; width: 100%; }
.cabecera { background: var(--fondo-2); border-bottom: 1px solid var(--borde); position: sticky; top: 0; z-index: 30; }
.cabecera-in { display: flex; align-items: center; gap: 28px; height: var(--alto-cabecera); }
.nav { display: flex; gap: 4px; height: 100%; }
.nav a {
  display: flex; align-items: center; padding: 0 12px; height: 100%;
  color: var(--texto-3); font-size: 0.7rem; font-weight: 800; letter-spacing: 0.13em; text-transform: uppercase;
  border-bottom: 2px solid transparent; margin-bottom: -1px; white-space: nowrap;
}
.nav a:hover { color: var(--texto); text-decoration: none; }
.nav a.activo { color: var(--texto); border-bottom-color: var(--acento); }
.acciones { margin-left: auto; display: flex; gap: 8px; align-items: center; }
.pastilla {
  display: inline-flex; align-items: center; gap: 6px; height: 34px; padding: 0 12px;
  border: 1px solid var(--borde-2); border-radius: var(--radio-peque); background: var(--tarjeta);
  font-size: 0.66rem; font-weight: 700; letter-spacing: 0.1em; text-transform: uppercase; color: var(--texto-3); white-space: nowrap;
}
.pastilla b { color: var(--texto); font-size: 0.8rem; letter-spacing: 0.02em; }
.pastilla.aviso { border-color: var(--acento-borde); background: var(--acento-suave); color: var(--acento-tinta); }
.pastilla.aviso b { color: var(--acento-tinta); }
a.pastilla:hover { text-decoration: none; filter: brightness(1.1); }
.punto-vivo { width: 7px; height: 7px; border-radius: 50%; background: var(--acento); animation: latido 1.4s infinite; }
@keyframes latido { 50% { opacity: 0.3; } }
.menu-usuario { position: relative; }
.boton-avatar { width: 36px; height: 36px; border: 0; cursor: pointer; font-size: 0.78rem; }
.boton-avatar:focus-visible { outline: 2px solid var(--acento); outline-offset: 2px; }
.desplegable {
  position: absolute; right: 0; top: calc(100% + 10px); width: 270px; background: var(--tarjeta); border: 1px solid var(--borde-2);
  border-radius: var(--radio); box-shadow: var(--sombra); padding: 8px; z-index: 40;
}
.quien { display: flex; flex-direction: column; padding: 8px 10px 10px; border-bottom: 1px solid var(--borde); overflow: hidden; }
.quien .tenue { overflow: hidden; text-overflow: ellipsis; }
.bloque { display: flex; flex-direction: column; padding: 6px 0; }
.bloque + .bloque { border-top: 1px solid var(--borde); }
.bloque .micro { padding: 6px 10px 4px; }
.bloque a, .bloque button {
  display: flex; align-items: center; gap: 10px; padding: 9px 10px; border-radius: 6px; background: none; border: 0;
  color: var(--texto); font: inherit; font-size: 0.88rem; cursor: pointer; text-align: left; width: 100%;
}
.bloque a:hover, .bloque button:hover { background: var(--tarjeta-2); text-decoration: none; }
.opcion-liga { justify-content: space-between; }
.opcion-liga.activa { color: var(--acento-tinta) !important; font-weight: 700; }
.nav-movil {
  position: fixed; left: 0; right: 0; bottom: 0; z-index: 30; display: none;
  background: var(--fondo-2); border-top: 1px solid var(--borde); padding: 6px 4px calc(6px + env(safe-area-inset-bottom));
}
.nav-movil a { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 3px; padding: 4px 0; color: var(--texto-3); font-size: 0.6rem; font-weight: 800; letter-spacing: 0.08em; text-transform: uppercase; }
.nav-movil a:hover { text-decoration: none; }
.nav-movil a.activo { color: var(--acento-tinta); }
@media (max-width: 1240px) { .ocultar-medio { display: none; } }
@media (max-width: 1100px) {
  .cabecera-in { gap: 16px; }
  .nav a { padding: 0 8px; letter-spacing: 0.08em; }
}
@media (max-width: 980px) { .ocultar-estrecho { display: none; } }
@media (max-width: 760px) {
  .nav-movil { display: flex !important; }
  main { padding-bottom: 96px; }
  .cabecera-in { height: 56px; }
}
</style>
