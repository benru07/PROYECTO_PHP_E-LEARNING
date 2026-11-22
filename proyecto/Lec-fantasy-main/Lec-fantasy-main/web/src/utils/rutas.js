/** Adónde va cada usuario al iniciar sesión. */
export function rutaInicial(auth) {
  if (auth.esAdmin) return { name: 'admin' }
  if (auth.usuario && !auth.usuario.perfilCompleto) return { name: 'perfil', query: { completar: '1' } }
  return { name: 'panel' }
}
