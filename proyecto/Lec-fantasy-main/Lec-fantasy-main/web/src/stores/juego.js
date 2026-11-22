import { defineStore } from 'pinia'
import { api } from '../api/cliente'

const CLAVE_ACTIVO = 'lecfantasy.equipoActivo'

function leerActivo() {
  try {
    return Number(localStorage.getItem(CLAVE_ACTIVO)) || null
  } catch {
    return null
  }
}

/**
 * Estado compartido del juego: mis ligas, mis equipos y el "equipo activo"
 * (el de la liga con la que se está jugando ahora: Mi equipo, Mercado, saldo de la cabecera...).
 */
export const useJuego = defineStore('juego', {
  state: () => ({
    ligas: [],
    equipos: {},
    jornadas: [],
    activoId: leerActivo(),
    cargado: false,
    cargando: null
  }),
  getters: {
    equipoActivo: (s) => s.equipos[s.activoId] || null,
    ligaActiva: (s) => s.ligas.find((l) => l.miEquipoId === s.activoId) || null,
    enCurso: (s) => s.jornadas.find((j) => j.estado === 'EN_CURSO') || null,
    proxima: (s) => s.jornadas.find((j) => j.estado === 'PROGRAMADA') || null,
    ultimaFinalizada: (s) => [...s.jornadas].reverse().find((j) => j.estado === 'FINALIZADA') || null,
    esMio: (s) => (equipoId) => s.ligas.some((l) => l.miEquipoId === Number(equipoId))
  },
  actions: {
    /** Carga ligas, equipos y jornadas una vez (o de nuevo si se fuerza). */
    async cargar(forzar = false) {
      if (this.cargado && !forzar) return
      if (this.cargando) return this.cargando
      this.cargando = (async () => {
        const [ligas, jornadas] = await Promise.all([api.get('/ligas/mias'), api.get('/jornadas')])
        const equipos = await Promise.all(ligas.map((l) => api.get(`/equipos/${l.miEquipoId}`).catch(() => null)))
        this.ligas = ligas
        this.jornadas = jornadas
        this.equipos = Object.fromEntries(equipos.filter(Boolean).map((e) => [e.id, e]))
        if (!this.ligas.some((l) => l.miEquipoId === this.activoId)) {
          // Por defecto, la primera liga privada; si no hay, la Liga Global
          const elegida = ligas.find((l) => !l.global) || ligas[0]
          this.elegir(elegida?.miEquipoId ?? null)
        }
        this.cargado = true
      })()
      try {
        await this.cargando
      } finally {
        this.cargando = null
      }
    },
    elegir(equipoId) {
      this.activoId = equipoId ? Number(equipoId) : null
      try {
        if (this.activoId) localStorage.setItem(CLAVE_ACTIVO, String(this.activoId))
      } catch {
        /* sin almacenamiento: solo dura la sesión */
      }
    },
    /** Guarda la última versión de un equipo propio (tras fichar, vender o alinear). */
    actualizarEquipo(equipo) {
      if (equipo && this.esMio(equipo.id)) this.equipos = { ...this.equipos, [equipo.id]: equipo }
    }
  }
})
