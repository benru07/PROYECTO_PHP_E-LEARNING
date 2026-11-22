import { defineStore } from 'pinia'
import { api, guardarToken, leerToken } from '../api/cliente'
import { useTema } from './tema'
import { useJuego } from './juego'

export const useAuth = defineStore('auth', {
  state: () => ({
    token: leerToken(),
    usuario: null,
    cargado: false
  }),
  getters: {
    autenticado: (s) => !!s.token,
    esAdmin: (s) => s.usuario?.rol === 'ADMIN'
  },
  actions: {
    /** Recupera el usuario al recargar la página si había token guardado. */
    async iniciar() {
      if (this.cargado) return
      if (this.token) {
        try {
          this.usuario = await api.get('/usuarios/me', undefined, { sinRedireccion: true })
          useTema().desdeUsuario(this.usuario)
        } catch {
          this.cerrarSesion()
        }
      }
      this.cargado = true
    },
    aplicar(respuesta) {
      this.token = respuesta.token
      this.usuario = respuesta.usuario
      guardarToken(respuesta.token)
      useTema().desdeUsuario(this.usuario)
      this.cargado = true
    },
    async login(identificador, password, recordarme) {
      this.aplicar(await api.post('/auth/login', { identificador, password, recordarme }))
    },
    async loginGoogle(idToken) {
      this.aplicar(await api.post('/auth/google', { idToken }))
    },
    async registrar(datos) {
      this.aplicar(await api.post('/auth/registro', datos))
    },
    async refrescar() {
      this.usuario = await api.get('/usuarios/me')
    },
    cerrarSesion() {
      this.token = null
      this.usuario = null
      guardarToken(null)
      useJuego().$reset()
    }
  }
})
