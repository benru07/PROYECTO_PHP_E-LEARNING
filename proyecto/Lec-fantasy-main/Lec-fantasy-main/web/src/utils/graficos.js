import { fecha } from './formato'

/** Puntos de la gráfica de precio: historial del servidor + el valor actual. */
export function seriePrecio(detalle) {
  const puntos = (detalle?.historialValor || []).map((h) => ({
    etiqueta: h.jornada ? h.jornada : h.fecha ? `Ajuste ${fecha(h.fecha)}` : 'Inicio',
    valor: Number(h.valor)
  }))
  const actual = Number(detalle?.jugador?.valorMercado)
  if (!puntos.length || puntos[puntos.length - 1].valor !== actual) puntos.push({ etiqueta: 'Ahora', valor: actual })
  return puntos
}

/** Barras de puntos de las últimas partidas, de la más antigua a la más reciente. */
export function barrasPartidas(historial, n = 10) {
  return (historial || []).slice(0, n).reverse().map((e) => ({
    etiqueta: `${e.jornada} · ${fecha(e.fecha)}`,
    corta: e.campeon ? e.campeon.slice(0, 4) : fecha(e.fecha).slice(0, 5),
    valor: Number(e.puntos),
    detalle: `${e.kills}/${e.deaths}/${e.assists}${e.campeon ? ' · ' + e.campeon : ''}${e.victoria ? ' · Victoria' : ''}`
  }))
}

/** Medias de KDA, CS y visión de un historial. */
export function medias(historial) {
  const n = historial?.length || 0
  if (!n) return null
  const suma = (k) => historial.reduce((s, e) => s + Number(e[k] || 0), 0)
  const muertes = suma('deaths')
  return {
    kda: (suma('kills') + suma('assists')) / Math.max(1, muertes),
    kills: suma('kills') / n,
    deaths: muertes / n,
    assists: suma('assists') / n,
    cs: suma('cs') / n,
    vision: suma('visionScore') / n,
    victorias: historial.filter((e) => e.victoria).length / n
  }
}
