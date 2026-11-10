// Colores de los equipos de la LEC para las insignias (el backend solo guarda nombre, siglas y logo).
const COLORES = {
  G2: '#E4002B',
  FNC: '#FF5900',
  KC: '#1E5BFF',
  MKOI: '#7B2FD9',
  KOI: '#7B2FD9',
  BDS: '#EE1C8E',
  TH: '#C9A227',
  VIT: '#F2DB00',
  GX: '#12A89D',
  SK: '#4A4F57',
  NAVI: '#F5D300',
  RGE: '#2AA2DC',
  MAD: '#D4A017',
  XL: '#00A651',
  AST: '#E30613'
}

const SIGLAS_POR_NOMBRE = [
  [/g2/i, 'G2'], [/fnatic/i, 'FNC'], [/karmine/i, 'KC'], [/koi|mad lions/i, 'MKOI'], [/bds/i, 'BDS'],
  [/heretics/i, 'TH'], [/vitality/i, 'VIT'], [/giantx|giants/i, 'GX'], [/sk gaming/i, 'SK'], [/navi|natus/i, 'NAVI'],
  [/rogue/i, 'RGE'], [/excel/i, 'XL'], [/astralis/i, 'AST']
]

export function siglasEquipo(equipo) {
  if (!equipo) return '—'
  if (equipo.siglas) return equipo.siglas.toUpperCase()
  const nombre = equipo.nombre || ''
  const conocida = SIGLAS_POR_NOMBRE.find(([re]) => re.test(nombre))
  if (conocida) return conocida[1]
  const palabras = nombre.split(/\s+/).filter(Boolean)
  return (palabras.length > 1 ? palabras.map((p) => p[0]).join('') : nombre.slice(0, 3)).slice(0, 4).toUpperCase()
}

export function colorEquipo(equipo) {
  const siglas = siglasEquipo(equipo)
  if (COLORES[siglas]) return COLORES[siglas]
  // Color estable a partir del nombre para los equipos desconocidos
  let h = 0
  for (const c of equipo?.nombre || siglas) h = (h * 31 + c.charCodeAt(0)) % 360
  return `hsl(${h} 55% 45%)`
}
