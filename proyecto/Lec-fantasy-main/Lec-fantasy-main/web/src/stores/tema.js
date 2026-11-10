import { defineStore } from 'pinia'
import { api } from '../api/cliente'
import { aplicarTema, guardarLocal, leerLocal, normalizar } from '../utils/tema'

/** Tema activo. Se sincroniza con el usuario (PUT /usuarios/me/apariencia) cuando hay sesión. */
export const useTema = defineStore('tema', {
  state: () => ({ ...leerLocal() }),
  getters: {
    claro: (s) => s.tema === 'CLARO'
  },
  actions: {
    /** Pinta el tema guardado en el navegador (antes de montar la app). */
    iniciar() {
      aplicarTema(this.$state)
    },
    /** Al iniciar sesión manda el tema guardado en la cuenta. */
    desdeUsuario(usuario) {
      if (!usuario?.tema) return
      this.$patch(normalizar(usuario))
      aplicarTema(this.$state)
      guardarLocal(this.$state)
    },
    /** Cambia el tema en pantalla al momento (vista previa), sin guardarlo. */
    probar(cambios) {
      this.$patch(normalizar({ ...this.$state, ...cambios }))
      aplicarTema(this.$state)
    },
    /** Guarda el tema en el navegador y, si hay sesión, en la cuenta. Devuelve el usuario actualizado. */
    async guardar(conSesion) {
      guardarLocal(this.$state)
      if (!conSesion) return null
      return api.put('/usuarios/me/apariencia', { tema: this.tema, colorAcento: this.colorAcento })
    }
  }
})
