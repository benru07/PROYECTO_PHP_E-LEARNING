import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from './stores/auth'
import { rutaInicial } from './utils/rutas'
import { useJuego } from './stores/juego'

import LayoutPublico from './components/LayoutPublico.vue'
import LayoutJuego from './components/LayoutJuego.vue'
import LayoutAdmin from './components/LayoutAdmin.vue'

const routes = [
  {
    path: '/',
    component: LayoutPublico,
    children: [
      { path: '', name: 'inicio', component: () => import('./views/publico/Inicio.vue') },
      { path: 'objetivos', name: 'objetivos', component: () => import('./views/publico/Objetivos.vue') },
      { path: 'como-funciona', name: 'como-funciona', component: () => import('./views/publico/ComoFunciona.vue') },
      { path: 'noticias', name: 'noticias', component: () => import('./views/publico/Noticias.vue') },
      { path: 'noticias/:id', name: 'noticia', component: () => import('./views/publico/NoticiaDetalle.vue') },
      { path: 'privacidad', name: 'legal', component: () => import('./views/publico/Legal.vue') }
    ]
  },
  { path: '/login', name: 'login', component: () => import('./views/publico/Login.vue'), meta: { invitado: true } },
  { path: '/registro', name: 'registro', component: () => import('./views/publico/Registro.vue'), meta: { invitado: true } },
  { path: '/olvido-password', name: 'olvido', component: () => import('./views/publico/OlvidoPassword.vue') },
  { path: '/restablecer-password', name: 'restablecer', component: () => import('./views/publico/RestablecerPassword.vue') },
  {
    path: '/',
    component: LayoutJuego,
    meta: { auth: true },
    children: [
      { path: 'panel', name: 'panel', component: () => import('./views/juego/Panel.vue') },
      // Accesos directos de la barra: llevan al equipo de la liga activa
      { path: 'mi-equipo', name: 'mi-equipo', component: () => import('./views/juego/Panel.vue'), beforeEnter: () => irAEquipoActivo('') },
      { path: 'mercado', name: 'mercado-activo', component: () => import('./views/juego/Panel.vue'), beforeEnter: () => irAEquipoActivo('/mercado') },
      { path: 'estadisticas', name: 'estadisticas', component: () => import('./views/juego/Estadisticas.vue') },
      { path: 'ligas', name: 'ligas', component: () => import('./views/juego/Ligas.vue') },
      { path: 'ligas/:id', name: 'liga', component: () => import('./views/juego/LigaDetalle.vue') },
      { path: 'equipos/:id', name: 'equipo', component: () => import('./views/juego/Equipo.vue') },
      { path: 'equipos/:id/mercado', name: 'mercado', component: () => import('./views/juego/Mercado.vue') },
      { path: 'jugadores/:id', name: 'jugador', component: () => import('./views/juego/Jugador.vue') },
      { path: 'jornadas', name: 'jornadas', component: () => import('./views/juego/Jornadas.vue') },
      { path: 'partidas/:id', name: 'partida', component: () => import('./views/juego/Partida.vue') },
      { path: 'perfil', name: 'perfil', component: () => import('./views/juego/Perfil.vue') }
    ]
  },
  {
    path: '/admin',
    component: LayoutAdmin,
    meta: { auth: true, admin: true },
    children: [
      { path: '', name: 'admin', component: () => import('./views/admin/AdminPanel.vue') },
      { path: 'usuarios', name: 'admin-usuarios', component: () => import('./views/admin/AdminUsuarios.vue') },
      { path: 'bloqueos', name: 'admin-bloqueos', component: () => import('./views/admin/AdminBloqueos.vue') },
      { path: 'jugadores', name: 'admin-jugadores', component: () => import('./views/admin/AdminJugadores.vue') },
      { path: 'jornadas', name: 'admin-jornadas', component: () => import('./views/admin/AdminJornadas.vue') },
      { path: 'noticias', name: 'admin-noticias', component: () => import('./views/admin/AdminNoticias.vue') },
      { path: 'reportes', name: 'admin-reportes', component: () => import('./views/admin/AdminReportes.vue') },
      { path: 'actividad', name: 'admin-actividad', component: () => import('./views/admin/AdminActividad.vue') },
      { path: 'configuracion', name: 'admin-configuracion', component: () => import('./views/admin/AdminConfiguracion.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', name: 'no-encontrado', component: () => import('./views/publico/NoEncontrado.vue') }
]

async function irAEquipoActivo(sufijo) {
  const juego = useJuego()
  try {
    await juego.cargar()
  } catch {
    return '/ligas'
  }
  return juego.activoId ? `/equipos/${juego.activoId}${sufijo}` : '/ligas'
}

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach(async (to) => {
  const auth = useAuth()
  await auth.iniciar()
  if (to.matched.some((r) => r.meta.auth) && !auth.autenticado) {
    return { name: 'login', query: { volver: to.fullPath } }
  }
  if (to.matched.some((r) => r.meta.admin) && !auth.esAdmin) {
    return { name: 'panel' }
  }
  if (to.meta.invitado && auth.autenticado) {
    return rutaInicial(auth)
  }
  // Abrir un equipo propio o su mercado lo convierte en el equipo activo
  if ((to.name === 'equipo' || to.name === 'mercado') && auth.autenticado) {
    const juego = useJuego()
    if (juego.esMio(to.params.id)) juego.elegir(to.params.id)
  }
  return true
})

export default router
