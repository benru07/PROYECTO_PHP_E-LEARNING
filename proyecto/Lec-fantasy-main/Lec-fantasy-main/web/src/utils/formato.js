const numero = new Intl.NumberFormat('es-ES', { maximumFractionDigits: 2 })
const entero = new Intl.NumberFormat('es-ES')
const unDecimal = new Intl.NumberFormat('es-ES', { minimumFractionDigits: 1, maximumFractionDigits: 1 })

/** 8000000 → "8 M", 8250000 → "8,25 M" */
export function millones(valor) {
  if (valor === null || valor === undefined) return '—'
  return `${numero.format(Number(valor) / 1_000_000)} M`
}

export function euros(valor) {
  if (valor === null || valor === undefined) return '—'
  return entero.format(Math.round(Number(valor)))
}

export function puntos(valor) {
  if (valor === null || valor === undefined) return '—'
  return numero.format(Number(valor))
}

/** Media con un decimal: 39,3 */
export function decimal(valor) {
  if (valor === null || valor === undefined || Number.isNaN(Number(valor))) return '—'
  return unDecimal.format(Number(valor))
}

/** El backend devuelve fechas UTC sin zona ("2026-01-17T17:05:00"). */
export function aFecha(iso) {
  if (!iso) return null
  const conZona = /[zZ]|[+-]\d\d:?\d\d$/.test(iso) || iso.length <= 10 ? iso : `${iso}Z`
  return new Date(conZona)
}

export function fecha(iso) {
  if (!iso) return '—'
  if (iso.length <= 10) {
    const [a, m, d] = iso.split('-')
    return `${d}/${m}/${a}`
  }
  return aFecha(iso).toLocaleDateString('es-ES')
}

export function fechaHora(iso) {
  if (!iso) return '—'
  return aFecha(iso).toLocaleString('es-ES', { dateStyle: 'short', timeStyle: 'short' })
}

export function hace(iso) {
  if (!iso) return ''
  const seg = Math.round((Date.now() - aFecha(iso).getTime()) / 1000)
  if (seg < 60) return 'ahora mismo'
  if (seg < 3600) return `hace ${Math.floor(seg / 60)} min`
  if (seg < 86400) return `hace ${Math.floor(seg / 3600)} h`
  if (seg < 172800) return 'ayer'
  return `hace ${Math.floor(seg / 86400)} días`
}

export const POSICIONES = ['TOP', 'JNG', 'MID', 'ADC', 'SUP']
export const NOMBRE_POSICION = { TOP: 'Top', JNG: 'Jungla', MID: 'Mid', ADC: 'ADC', SUP: 'Support' }
export const NOMBRE_SLOT = {
  TOP: 'Top', JNG: 'Jungla', MID: 'Mid', ADC: 'ADC', SUP: 'Support',
  BANQUILLO_1: 'Banquillo 1', BANQUILLO_2: 'Banquillo 2'
}
export const ESTADO_JORNADA = { PROGRAMADA: 'Programada', EN_CURSO: 'En curso', FINALIZADA: 'Finalizada' }
export const MOTIVOS = {
  TOXICIDAD: 'Toxicidad', NOMBRE_INAPROPIADO: 'Nombre inapropiado', TRAMPAS: 'Trampas',
  SPAM: 'Spam', MULTICUENTA: 'Multicuenta', OTRO: 'Otro'
}

/** "Caps" → "CA", "Rubén Gordillo" → "RG", "los_pibes" → "LP" */
export function iniciales(texto) {
  if (!texto) return '?'
  const partes = String(texto).trim().split(/[\s_.-]+/).filter(Boolean)
  if (partes.length > 1) return (partes[0][0] + partes[1][0]).toUpperCase()
  return partes[0].slice(0, 2).toUpperCase()
}

/** 2 → "2º" */
export function ordinal(n) {
  return n === null || n === undefined ? '—' : `${entero.format(n)}º`
}

export function enteroEs(n) {
  return n === null || n === undefined ? '—' : entero.format(n)
}

/** Variación de precio respecto al valor anterior: { pct, texto, clase } (null si nunca ha cambiado). */
export function variacion(jugador) {
  const antes = Number(jugador?.valorAnterior)
  const ahora = Number(jugador?.valorMercado)
  if (!antes || !ahora || antes === ahora) return null
  const pct = ((ahora - antes) / antes) * 100
  return {
    pct,
    texto: `${pct > 0 ? '▲' : '▼'} ${unDecimal.format(Math.abs(pct))} %`,
    clase: pct > 0 ? 'sube' : 'baja'
  }
}

export function mediaJugador(j) {
  return j?.partidasJugadas ? Number(j.puntosTotales) / j.partidasJugadas : 0
}

const SLOTS_TITULARES = ['TOP', 'JNG', 'MID', 'ADC', 'SUP']

/** Dónde iría un fichaje: 'TITULAR', 'BANQUILLO' o null si no cabe. */
export function destinoFichaje(equipo, jugador) {
  if (!equipo || !jugador) return null
  const ocupados = new Set(equipo.plantilla.map((p) => p.slot))
  if (SLOTS_TITULARES.includes(jugador.posicion) && !ocupados.has(jugador.posicion)) return 'TITULAR'
  if (!ocupados.has('BANQUILLO_1') || !ocupados.has('BANQUILLO_2')) return 'BANQUILLO'
  return null
}

/** Motivo por el que no se puede fichar a un jugador ('' si se puede). */
export function motivoNoFichar(equipo, jugador) {
  if (!equipo || !jugador) return 'Elige un equipo'
  if (jugador.bloqueado) return 'Retirado del mercado'
  if (!equipo.mercadoAbierto) return 'Mercado cerrado: hay una jornada en curso'
  if (equipo.plantilla.some((p) => p.jugador.id === jugador.id)) return 'Ya está en tu plantilla'
  if (equipo.plantilla.length >= 7) return 'Plantilla completa (7/7)'
  if (!destinoFichaje(equipo, jugador)) return 'No hay hueco libre para su rol ni en el banquillo'
  if (Number(equipo.dineroDisponible) < Number(jugador.valorMercado)) return 'No tienes saldo suficiente'
  return ''
}
