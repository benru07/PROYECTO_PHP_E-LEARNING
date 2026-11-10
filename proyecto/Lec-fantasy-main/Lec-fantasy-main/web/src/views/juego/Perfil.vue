<template>
  <div>
    <Alerta v-if="route.query.completar && !auth.usuario?.perfilCompleto" tipo="info" class="mb"
            mensaje="Para jugar necesitamos tu fecha de nacimiento y tu país. Complétalos en «Datos personales» y pulsa Guardar." />

    <div class="cuenta">
      <!-- Columna 1: perfil -->
      <div class="pila">
        <section class="tarjeta">
          <div class="quien">
            <span class="avatar lleno enorme">{{ iniciales(auth.usuario?.nombre) }}</span>
            <div>
              <h1 class="sin-margen nombre">{{ auth.usuario?.nombre }}</h1>
              <span class="tenue">{{ auth.usuario?.email }}</span>
            </div>
          </div>
          <span class="micro">Estadísticas</span>
          <div class="grid grid-2 stats">
            <div class="mini"><span class="micro">Ligas</span><span class="cifra">{{ juego.ligas.length }}</span></div>
            <div class="mini"><span class="micro">Mejor puesto</span><span class="cifra">{{ mejorPuesto }}</span></div>
            <div class="mini"><span class="micro">Récord jornada</span><span class="cifra">{{ record ? puntos(record) : '—' }}</span></div>
            <div class="mini"><span class="micro">Mánager desde</span><span class="cifra">{{ desde }}</span></div>
          </div>
          <span class="micro">Mis ligas</span>
          <div class="ligas">
            <RouterLink v-for="l in juego.ligas" :key="l.id" :to="`/ligas/${l.id}`" class="liga">
              <span class="avatar cuadrado">{{ iniciales(l.nombre) }}</span>
              <span class="texto-liga"><b>{{ l.nombre }}</b><span class="tenue">{{ juego.equipos[l.miEquipoId]?.nombre }}</span></span>
              <span class="negrita">{{ juego.equipos[l.miEquipoId] ? ordinal(juego.equipos[l.miEquipoId].posicion) : '' }}</span>
            </RouterLink>
          </div>
        </section>
      </div>

      <!-- Columna 2: apariencia y notificaciones -->
      <div class="pila">
        <section class="tarjeta" aria-labelledby="t-apariencia">
          <div class="tarjeta-cabeza">
            <h2 id="t-apariencia">Apariencia</h2>
            <span class="estado-guardado micro" :class="estadoTema" aria-live="polite">{{ textoEstadoTema }}</span>
          </div>

          <span class="etiqueta-campo">Modo</span>
          <div class="modos" role="radiogroup" aria-label="Modo de color">
            <button v-for="m in MODOS" :key="m.id" type="button" role="radio" class="modo"
                    :class="{ elegido: tema.tema === m.id }" :aria-checked="tema.tema === m.id" @click="cambiar({ tema: m.id })">
              <span class="miniatura" :class="m.id.toLowerCase()">
                <span class="mini-barra" /><span class="mini-tarjeta"><span class="mini-acento" /></span>
              </span>
              <span class="fila nombre-modo"><Icono :nombre="m.icono" :tam="15" /> {{ m.nombre }}</span>
            </button>
          </div>

          <span class="etiqueta-campo">Color de acento</span>
          <div class="muestras" role="radiogroup" aria-label="Color de acento">
            <button v-for="c in COLORES" :key="c.hex" type="button" role="radio" class="muestra"
                    :style="{ background: c.hex, color: textoSobre(c.hex) }" :title="c.nombre" :aria-label="c.nombre"
                    :aria-checked="tema.colorAcento === c.hex" @click="cambiar({ colorAcento: c.hex })">
              <Icono v-if="tema.colorAcento === c.hex" nombre="check" :tam="16" />
            </button>
            <label class="muestra personalizada" :class="{ elegida: esPersonalizado }" title="Color personalizado"
                   :style="esPersonalizado ? { background: tema.colorAcento, color: textoSobre(tema.colorAcento) } : {}">
              <input type="color" :value="tema.colorAcento.toLowerCase()" aria-label="Elegir color personalizado"
                     @input="cambiar({ colorAcento: $event.target.value })" />
              <Icono :nombre="esPersonalizado ? 'check' : 'mas'" :tam="16" />
            </label>
          </div>
          <div class="fila hex">
            <span class="tenue">{{ nombreColor }}</span>
            <input v-model="hexTexto" class="input hex-input" maxlength="7" spellcheck="false" aria-label="Color en hexadecimal"
                   @change="aplicarHex" @keydown.enter.prevent="aplicarHex" />
          </div>

          <div class="vista-previa" aria-hidden="true">
            <div>
              <span class="micro">Cierre de alineación</span>
              <div class="cifra texto-acento">1d 14:22:08</div>
            </div>
            <div class="fila">
              <span class="chip chip-acento">MID</span>
              <span class="sube">▲ 2,4 %</span>
              <span class="btn btn-peque">Fichar</span>
            </div>
          </div>
          <div class="fila-entre pie-apariencia">
            <span class="tenue">Se guarda en tu cuenta: la web y la app usarán los mismos colores.</span>
            <button type="button" class="btn btn-fantasma btn-peque" @click="cambiar(TEMA_POR_DEFECTO)">Restablecer</button>
          </div>
        </section>

        <section class="tarjeta">
          <h2>Notificaciones</h2>
          <div class="ajuste">
            <div>
              <b>Noticias de la LEC</b>
              <span class="tenue">Novedades del juego y de la competición por correo.</span>
            </div>
            <label class="interruptor">
              <input type="checkbox" :checked="form.aceptaNoticias" aria-label="Recibir noticias de la LEC" @change="cambiarNoticias" />
              <span />
            </label>
          </div>
          <Alerta :mensaje="mensajeNoticias" :tipo="mensajeNoticiasTipo" />
        </section>

        <section class="tarjeta">
          <h2>Reglamento</h2>
          <span class="kicker">Puntuación</span>
          <div class="reglas">
            <div v-for="r in REGLAS" :key="r[0]" class="regla"><span>{{ r[0] }}</span><b>{{ r[1] }}</b></div>
          </div>
          <span class="kicker">Economía</span>
          <div class="reglas">
            <div class="regla"><span>Presupuesto inicial</span><b>{{ juego.ligaActiva ? millones(juego.ligaActiva.presupuestoInicial) : '50 M' }}</b></div>
            <div class="regla"><span>Plantilla</span><b>5 titulares + 2</b></div>
            <div class="regla"><span>Cambio de precio por jornada</span><b>± 8 %</b></div>
            <div class="regla"><span>Puntúan</span><b>Solo titulares</b></div>
          </div>
        </section>
      </div>

      <!-- Columna 3: datos y contraseña -->
      <div class="pila">
        <form id="datos" class="tarjeta pila" @submit.prevent="guardar">
          <h2>Datos personales</h2>
          <div class="campo">
            <label for="nombre">Nombre de usuario</label>
            <input id="nombre" v-model.trim="form.nombre" class="input" :class="{ invalido: errores.nombre }" />
            <span v-if="errores.nombre" class="error-campo">{{ errores.nombre }}</span>
          </div>
          <div class="campo">
            <label for="email">Correo</label>
            <input id="email" :value="auth.usuario?.email" class="input" disabled />
          </div>
          <CamposPerfil :modelo="form" :errores="errores" class="una-col" />
          <Alerta :mensaje="mensaje" :tipo="mensajeTipo" />
          <div><button class="btn" :disabled="enviando">Guardar</button></div>
        </form>

        <form class="tarjeta pila" @submit.prevent="cambiarPassword">
          <h2>{{ auth.usuario?.tienePassword ? 'Cambiar contraseña' : 'Crear contraseña' }}</h2>
          <p v-if="!auth.usuario?.tienePassword" class="tenue sin-margen">Entraste con Google. Si quieres, crea una contraseña para entrar también con tu correo.</p>
          <div v-if="auth.usuario?.tienePassword" class="campo">
            <label for="actual">Contraseña actual</label>
            <input id="actual" v-model="pass.actual" type="password" class="input" autocomplete="current-password" required />
          </div>
          <div class="campo">
            <label for="nueva">Contraseña nueva</label>
            <input id="nueva" v-model="pass.nueva" type="password" class="input" minlength="8" autocomplete="new-password" required />
          </div>
          <Alerta :mensaje="mensajePass" :tipo="mensajePassTipo" />
          <div><button class="btn btn-borde">Guardar contraseña</button></div>
        </form>

        <p class="tenue">Cuenta creada el {{ fecha(auth.usuario?.fechaRegistro) }}. Para eliminar tu cuenta, contacta con un administrador.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import CamposPerfil from '../../components/CamposPerfil.vue'
import Icono from '../../components/Icono.vue'
import { api } from '../../api/cliente'
import { useAuth } from '../../stores/auth'
import { useJuego } from '../../stores/juego'
import { useTema } from '../../stores/tema'
import { fecha, iniciales, millones, ordinal, puntos } from '../../utils/formato'
import { COLORES, TEMA_POR_DEFECTO, textoSobre } from '../../utils/tema'

const MODOS = [
  { id: 'OSCURO', nombre: 'Oscuro', icono: 'luna' },
  { id: 'CLARO', nombre: 'Claro', icono: 'sol' }
]
const REGLAS = [
  ['Kill', '+3'], ['Asistencia', '+2'], ['Muerte', '−1'], ['CS', '+1 cada 50'], ['Visión', '+1 cada 20'],
  ['Doble / triple / cuádruple / penta', '+1 / +2 / +5 / +10'], ['First blood', '+2'], ['Victoria', '+2'], ['MVP de la partida', '+3']
]

const auth = useAuth()
const juego = useJuego()
const tema = useTema()
const route = useRoute()
const router = useRouter()
const u = auth.usuario || {}
const form = reactive({
  nombre: u.nombre || '', fechaNacimiento: u.fechaNacimiento || '', pais: u.pais || '',
  equipoFavoritoId: u.equipoFavorito?.id ?? null, riotId: u.riotId || '', aceptaNoticias: !!u.aceptaNoticias
})
const errores = ref({})
const mensaje = ref('')
const mensajeTipo = ref('ok')
const enviando = ref(false)
const pass = reactive({ actual: '', nueva: '' })
const mensajePass = ref('')
const mensajePassTipo = ref('ok')
const mensajeNoticias = ref('')
const mensajeNoticiasTipo = ref('ok')
const record = ref(null)

// ---------- Estadísticas ----------
const mejorPuesto = computed(() => {
  const pos = juego.ligas.map((l) => juego.equipos[l.miEquipoId]?.posicion).filter(Boolean)
  return pos.length ? ordinal(Math.min(...pos)) : '—'
})
const desde = computed(() => (auth.usuario?.fechaRegistro ? auth.usuario.fechaRegistro.slice(0, 4) : '—'))

onMounted(async () => {
  try {
    await juego.cargar()
    const listas = await Promise.all(juego.ligas.map((l) => api.get(`/equipos/${l.miEquipoId}/puntuaciones`).catch(() => [])))
    const todos = listas.flat().map((p) => Number(p.puntosTotales))
    record.value = todos.length ? Math.max(...todos) : null
  } catch {
    record.value = null
  }
})

// ---------- Apariencia: se ve al momento y se guarda medio segundo después ----------
const estadoTema = ref('')
const hexTexto = ref(tema.colorAcento)
let espera = null
const esPersonalizado = computed(() => !COLORES.some((c) => c.hex === tema.colorAcento))
const nombreColor = computed(() => COLORES.find((c) => c.hex === tema.colorAcento)?.nombre || 'Personalizado')
const textoEstadoTema = computed(() => ({ guardando: 'Guardando…', guardado: 'Guardado ✓', error: 'No se pudo guardar' }[estadoTema.value] || ''))
watch(() => tema.colorAcento, (v) => (hexTexto.value = v))

function cambiar(cambios) {
  tema.probar(cambios)
  estadoTema.value = 'guardando'
  clearTimeout(espera)
  espera = setTimeout(guardarTema, 500)
}

async function guardarTema() {
  try {
    const usuario = await tema.guardar(auth.autenticado)
    if (usuario) auth.usuario = usuario
    estadoTema.value = 'guardado'
  } catch {
    estadoTema.value = 'error'
  }
}

function aplicarHex() {
  const v = hexTexto.value.trim()
  const hex = v.startsWith('#') ? v : `#${v}`
  if (/^#[0-9a-f]{6}$/i.test(hex)) cambiar({ colorAcento: hex })
  else hexTexto.value = tema.colorAcento
}

onBeforeUnmount(() => {
  // Si se sale antes de que venza la espera, se guarda igualmente
  if (estadoTema.value === 'guardando') {
    clearTimeout(espera)
    guardarTema()
  }
})

// ---------- Notificaciones ----------
async function cambiarNoticias(ev) {
  const valor = ev.target.checked
  mensajeNoticias.value = ''
  try {
    auth.usuario = await api.put('/usuarios/me', { aceptaNoticias: valor })
    form.aceptaNoticias = valor
  } catch (e) {
    ev.target.checked = !valor
    mensajeNoticiasTipo.value = 'error'
    mensajeNoticias.value = e.message
  }
}

// ---------- Datos y contraseña ----------
async function guardar() {
  mensaje.value = ''
  errores.value = {}
  if (!form.fechaNacimiento || !form.pais) {
    mensajeTipo.value = 'error'
    mensaje.value = 'La fecha de nacimiento y el país son obligatorios.'
    return
  }
  enviando.value = true
  const estabaIncompleto = !auth.usuario?.perfilCompleto
  try {
    auth.usuario = await api.put('/usuarios/me', { ...form, riotId: form.riotId || '' })
    mensajeTipo.value = 'ok'
    mensaje.value = 'Datos guardados.'
    if (estabaIncompleto && auth.usuario.perfilCompleto) router.push({ name: 'panel' })
  } catch (e) {
    mensajeTipo.value = 'error'
    mensaje.value = e.message
    errores.value = e.campos || {}
  } finally {
    enviando.value = false
  }
}

async function cambiarPassword() {
  mensajePass.value = ''
  try {
    await api.put('/usuarios/me/password', { passwordActual: pass.actual || null, passwordNueva: pass.nueva })
    await auth.refrescar()
    pass.actual = ''
    pass.nueva = ''
    mensajePassTipo.value = 'ok'
    mensajePass.value = 'Contraseña guardada.'
  } catch (e) {
    mensajePassTipo.value = 'error'
    mensajePass.value = e.message
  }
}
</script>

<style scoped>
.cuenta { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; align-items: start; }
@media (max-width: 1200px) { .cuenta { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 800px) { .cuenta { grid-template-columns: minmax(0, 1fr); } }
.tarjeta h2 { margin-bottom: 0.9em; }
.quien { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
.avatar.enorme { width: 64px; height: 64px; font-size: 1.3rem; }
.nombre { font-size: 1.5rem; word-break: break-word; }
.stats { gap: 10px; margin: 8px 0 20px; grid-template-columns: repeat(2, minmax(0, 1fr)); }
.mini { display: flex; flex-direction: column; gap: 4px; padding: 12px 14px; border-radius: var(--radio-peque); background: var(--tarjeta-2); border: 1px solid var(--borde); }
.mini .cifra { font-size: 1.5rem; }
.ligas { display: flex; flex-direction: column; margin-top: 6px; }
.liga { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-top: 1px solid var(--borde); color: var(--texto); }
.liga:first-child { border-top: 0; }
.liga:hover { text-decoration: none; }
.liga:hover b { color: var(--acento-tinta); }
.avatar.cuadrado { border-radius: 7px; width: 32px; height: 32px; font-size: 0.66rem; border: 1px solid var(--acento-borde); background: var(--acento-suave); color: var(--acento-tinta); }
.texto-liga { flex: 1; min-width: 0; display: flex; flex-direction: column; line-height: 1.3; font-size: 0.88rem; }
.texto-liga .tenue { font-size: 0.74rem; }

/* Apariencia */
.estado-guardado { color: var(--texto-3); }
.estado-guardado.guardado { color: var(--verde); }
.estado-guardado.error { color: var(--rojo); }
.modos { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin: 8px 0 18px; }
.modo {
  display: flex; flex-direction: column; gap: 8px; padding: 8px; border-radius: var(--radio-peque); cursor: pointer;
  background: var(--tarjeta-2); border: 1px solid var(--borde-2); color: var(--texto-2); font: inherit; font-size: 0.8rem; font-weight: 700;
}
.modo.elegido { border-color: var(--acento); box-shadow: 0 0 0 2px var(--acento-medio); color: var(--texto); }
.modo:focus-visible { outline: 2px solid var(--acento); outline-offset: 2px; }
.nombre-modo { gap: 6px; justify-content: center; }
.miniatura { display: block; height: 56px; border-radius: 6px; padding: 6px; position: relative; overflow: hidden; }
.miniatura.oscuro { background: #0a0a0b; border: 1px solid #26262b; }
.miniatura.claro { background: #f2f4f7; border: 1px solid #dfe4ea; }
.mini-barra { display: block; height: 6px; border-radius: 2px; margin-bottom: 5px; }
.oscuro .mini-barra { background: #1b1b1f; }
.claro .mini-barra { background: #ffffff; }
.mini-tarjeta { display: flex; align-items: flex-end; height: 28px; border-radius: 4px; padding: 5px; }
.oscuro .mini-tarjeta { background: #161618; }
.claro .mini-tarjeta { background: #ffffff; }
.mini-acento { display: block; width: 46%; height: 6px; border-radius: 2px; background: var(--acento); }
.muestras { display: grid; grid-template-columns: repeat(9, minmax(0, 1fr)); gap: 8px; margin: 8px 0 10px; }
.muestra {
  aspect-ratio: 1; width: 100%; max-width: 40px; border-radius: 50%; border: 0; cursor: pointer; display: grid; place-items: center;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.15); transition: transform 0.12s;
}
.muestra:hover { transform: scale(1.08); }
.muestra[aria-checked='true'], .muestra.elegida { box-shadow: 0 0 0 2px var(--tarjeta), 0 0 0 4px var(--texto); }
.muestra:focus-visible { outline: 2px solid var(--texto); outline-offset: 3px; }
.personalizada {
  position: relative; color: var(--texto-2);
  background: conic-gradient(#ff5a5f, #f5c542, #2fd27a, #1fb6ff, #9b6bff, #ff4f8b, #ff5a5f);
}
.personalizada:not(.elegida) :deep(.icono) { color: #fff; filter: drop-shadow(0 0 2px rgba(0, 0, 0, 0.6)); }
.personalizada input { position: absolute; inset: 0; opacity: 0; cursor: pointer; width: 100%; height: 100%; border: 0; padding: 0; }
.hex { justify-content: space-between; margin-bottom: 16px; }
.hex-input { width: 110px; font-family: ui-monospace, Menlo, Consolas, monospace; text-transform: uppercase; padding: 7px 10px; }
.vista-previa {
  display: flex; justify-content: space-between; align-items: flex-end; gap: 12px; flex-wrap: wrap;
  padding: 14px; border-radius: var(--radio-peque); background: var(--tarjeta-2); border: 1px solid var(--acento-borde);
}
.vista-previa .cifra { font-size: 1.5rem; }
.vista-previa .fila { gap: 8px; }
.pie-apariencia { margin-top: 12px; flex-wrap: nowrap; align-items: flex-start; }
.pie-apariencia .tenue { font-size: 0.76rem; }

/* Notificaciones y reglamento */
.ajuste { display: flex; justify-content: space-between; align-items: center; gap: 14px; padding-bottom: 6px; }
.ajuste > div { display: flex; flex-direction: column; line-height: 1.35; font-size: 0.9rem; }
.ajuste .tenue { font-size: 0.78rem; }
.reglas { display: flex; flex-direction: column; margin: 6px 0 16px; }
.reglas:last-child { margin-bottom: 0; }
.regla { display: flex; justify-content: space-between; gap: 10px; padding: 7px 0; border-top: 1px solid var(--borde); font-size: 0.84rem; }
.regla span { color: var(--texto-2); }
.regla b { text-align: right; white-space: nowrap; }
.una-col :deep(.grid-2), .una-col.grid-2 { grid-template-columns: minmax(0, 1fr); }
@media (max-width: 420px) { .muestras { grid-template-columns: repeat(5, minmax(0, 1fr)); } }
</style>
