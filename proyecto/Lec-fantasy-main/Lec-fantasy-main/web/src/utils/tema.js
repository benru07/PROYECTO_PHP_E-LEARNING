// Apariencia de la interfaz: modo (OSCURO/CLARO) + color de acento.
// Se guarda en el servidor (usuario.tema / usuario.color_acento) y también en el navegador,
// para pintar la página con el tema correcto antes de saber quién es el usuario.

export const TEMA_POR_DEFECTO = { tema: 'OSCURO', colorAcento: '#FF6B1A' }

export const COLORES = [
  { nombre: 'Naranja LEC', hex: '#FF6B1A' },
  { nombre: 'Celeste', hex: '#1FB6FF' },
  { nombre: 'Azul', hex: '#2F6BFF' },
  { nombre: 'Cian', hex: '#00D6D6' },
  { nombre: 'Verde', hex: '#2FD27A' },
  { nombre: 'Violeta', hex: '#9B6BFF' },
  { nombre: 'Rosa', hex: '#FF4F8B' },
  { nombre: 'Oro', hex: '#E3B341' }
]

const CLAVE = 'lecfantasy.tema'
const HEX = /^#[0-9a-f]{6}$/i

export function normalizar(t) {
  return {
    tema: t?.tema === 'CLARO' ? 'CLARO' : 'OSCURO',
    colorAcento: HEX.test(t?.colorAcento || '') ? t.colorAcento.toUpperCase() : TEMA_POR_DEFECTO.colorAcento
  }
}

export function leerLocal() {
  try {
    const guardado = localStorage.getItem(CLAVE)
    return guardado ? normalizar(JSON.parse(guardado)) : { ...TEMA_POR_DEFECTO }
  } catch {
    return { ...TEMA_POR_DEFECTO }
  }
}

export function guardarLocal(t) {
  try {
    localStorage.setItem(CLAVE, JSON.stringify(normalizar(t)))
  } catch {
    /* modo privado: el tema dura lo que la pestaña */
  }
}

// ---------- Color ----------

function aRgb(hex) {
  const n = parseInt(hex.slice(1), 16)
  return [(n >> 16) & 255, (n >> 8) & 255, n & 255]
}

function aHex([r, g, b]) {
  return '#' + [r, g, b].map((v) => Math.round(v).toString(16).padStart(2, '0')).join('').toUpperCase()
}

/** Luminancia relativa WCAG. */
function luminancia(rgb) {
  const [r, g, b] = rgb.map((v) => {
    const c = v / 255
    return c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4
  })
  return 0.2126 * r + 0.7152 * g + 0.0722 * b
}

export function contraste(a, b) {
  const [l1, l2] = [luminancia(aRgb(a)), luminancia(aRgb(b))].sort((x, y) => y - x)
  return (l1 + 0.05) / (l2 + 0.05)
}

function mezclar(hex, con, peso) {
  const a = aRgb(hex)
  const b = aRgb(con)
  return aHex(a.map((v, i) => v + (b[i] - v) * peso))
}

/** Texto que se lee bien encima del acento (botones, chips sólidos). */
export function textoSobre(hex) {
  return contraste(hex, '#FFFFFF') >= contraste(hex, '#120A04') ? '#FFFFFF' : '#120A04'
}

/**
 * Versión del acento para texto sobre el fondo: si el color elegido no tiene contraste
 * suficiente (p. ej. un amarillo en modo claro), se oscurece o aclara hasta 4.5:1.
 */
export function tinta(hex, fondo) {
  const destino = luminancia(aRgb(fondo)) > 0.5 ? '#000000' : '#FFFFFF'
  let color = hex
  for (let i = 1; i <= 20 && contraste(color, fondo) < 4.5; i++) {
    color = mezclar(hex, destino, i * 0.05)
  }
  return color
}

/** Aplica el tema al documento. */
export function aplicarTema(t) {
  const { tema, colorAcento } = normalizar(t)
  const raiz = document.documentElement
  const claro = tema === 'CLARO'
  raiz.dataset.tema = claro ? 'claro' : 'oscuro'
  raiz.style.setProperty('--acento', colorAcento)
  raiz.style.setProperty('--acento-tinta', tinta(colorAcento, claro ? '#FFFFFF' : '#141416'))
  raiz.style.setProperty('--acento-texto', textoSobre(colorAcento))
  const meta = document.querySelector('meta[name="theme-color"]')
  if (meta) meta.setAttribute('content', claro ? '#ffffff' : '#0f0f11')
}
