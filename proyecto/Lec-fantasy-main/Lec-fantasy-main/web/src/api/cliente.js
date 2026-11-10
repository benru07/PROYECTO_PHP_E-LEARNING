// Cliente HTTP de la API REST. Añade el token JWT y convierte los errores en ApiError.
const BASE = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '')
const CLAVE_TOKEN = 'lecfantasy.token'

export class ApiError extends Error {
  constructor(status, mensaje, campos) {
    super(mensaje)
    this.status = status
    this.campos = campos || {}
  }
}

let alNoAutorizado = () => {}
export function configurarNoAutorizado(fn) {
  alNoAutorizado = fn
}

export function leerToken() {
  try {
    return localStorage.getItem(CLAVE_TOKEN)
  } catch {
    return null
  }
}

export function guardarToken(token) {
  try {
    if (token) localStorage.setItem(CLAVE_TOKEN, token)
    else localStorage.removeItem(CLAVE_TOKEN)
  } catch {
    /* modo privado: la sesión dura lo que la pestaña */
  }
}

async function peticion(metodo, ruta, cuerpo, opciones = {}) {
  const cabeceras = {}
  const token = leerToken()
  if (token) cabeceras.Authorization = `Bearer ${token}`
  let body
  if (cuerpo instanceof FormData) {
    body = cuerpo
  } else if (cuerpo !== undefined) {
    cabeceras['Content-Type'] = 'application/json'
    body = JSON.stringify(cuerpo)
  }

  let respuesta
  try {
    respuesta = await fetch(`${BASE}/api${ruta}`, { method: metodo, headers: cabeceras, body })
  } catch {
    throw new ApiError(0, 'No se puede conectar con el servidor. Inténtalo de nuevo en unos segundos.')
  }

  if (respuesta.status === 204) return null
  const texto = await respuesta.text()
  let datos = null
  if (texto) {
    try {
      datos = JSON.parse(texto)
    } catch {
      datos = null
    }
  }
  if (!respuesta.ok) {
    if (respuesta.status === 401 && token && !opciones.sinRedireccion) {
      alNoAutorizado()
    }
    const mensaje = datos?.message || mensajePorDefecto(respuesta.status)
    throw new ApiError(respuesta.status, mensaje, datos?.campos)
  }
  return datos
}

function mensajePorDefecto(status) {
  if (status === 401) return 'Tu sesión ha caducado. Vuelve a iniciar sesión.'
  if (status === 403) return 'No tienes permiso para hacer esto.'
  if (status === 404) return 'No se ha encontrado.'
  return 'Ha ocurrido un error inesperado.'
}

function query(params = {}) {
  const q = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') q.append(k, v)
  })
  const s = q.toString()
  return s ? `?${s}` : ''
}

export const api = {
  get: (ruta, params, opciones) => peticion('GET', ruta + query(params), undefined, opciones),
  post: (ruta, cuerpo, opciones) => peticion('POST', ruta, cuerpo, opciones),
  put: (ruta, cuerpo) => peticion('PUT', ruta, cuerpo),
  del: (ruta) => peticion('DELETE', ruta)
}
