<template>
  <section class="hero contenedor">
    <div class="texto">
      <h1>Crea tu equipo soñado de la LEC</h1>
      <p class="lead">
        Ficha a los mejores jugadores de la LEC, gestiona tu presupuesto y suma puntos con cada partida real.
        Compite contra tus amigos y contra fans de toda Europa.
      </p>
      <div class="fila">
        <RouterLink to="/registro" class="btn btn-grande">Empieza gratis</RouterLink>
        <RouterLink to="/login" class="btn btn-grande btn-borde">Iniciar sesión</RouterLink>
      </div>
      <p class="reto">10 equipos. 50 jugadores. Una sola corona. ¿Te atreves?</p>
    </div>
    <div class="escenario" aria-hidden="true">
      <div class="alineacion">
        <div v-for="p in ['TOP', 'JNG', 'MID', 'ADC', 'SUP']" :key="p" class="hueco">
          <span class="pos" :class="`pos-${p}`">{{ p }}</span>
          <span class="silueta"></span>
        </div>
      </div>
      <p class="tenue centrado">Tu alineación: un jugador por rol</p>
    </div>
  </section>

  <section class="contenedor ventajas grid grid-3">
    <div class="tarjeta tarjeta-clara centrado">
      <h3>Ficha jugadores</h3>
      <p>Compra y vende a las estrellas de la LEC en el mercado</p>
    </div>
    <div class="tarjeta tarjeta-clara centrado">
      <h3>Suma puntos en directo</h3>
      <p>Cada kill, asistencia y objetivo cuenta</p>
    </div>
    <div class="tarjeta tarjeta-clara centrado">
      <h3>Compite en ligas</h3>
      <p>Crea ligas privadas con tus amigos o entra en la global</p>
    </div>
  </section>

  <section v-if="noticias.length" class="contenedor noticias">
    <div class="fila-entre mb">
      <h2>Últimas noticias</h2>
      <RouterLink to="/noticias">Ver todas</RouterLink>
    </div>
    <div class="grid grid-3">
      <RouterLink v-for="n in noticias" :key="n.id" :to="`/noticias/${n.id}`" class="tarjeta noticia">
        <span class="tenue">{{ fecha(n.fechaPublicacion) }}</span>
        <h3>{{ n.titulo }}</h3>
        <p class="muted">{{ n.resumen || recortar(n.contenido) }}</p>
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../../api/cliente'
import { fecha } from '../../utils/formato'

const noticias = ref([])
const recortar = (t) => (t.length > 140 ? `${t.slice(0, 140)}…` : t)

onMounted(async () => {
  try {
    noticias.value = (await api.get('/noticias', { tamano: 3 })).contenido
  } catch {
    noticias.value = []
  }
})
</script>

<style scoped>
.hero { display: grid; grid-template-columns: 1.15fr 1fr; gap: 48px; align-items: center; padding-top: 64px; padding-bottom: 56px; }
h1 { font-size: clamp(2.2rem, 5vw, 3.6rem); }
.lead { font-size: 1.2rem; color: var(--texto-2); max-width: 560px; margin: 18px 0 30px; }
.reto { color: var(--naranja); font-weight: 700; margin-top: 26px; }
.escenario {
  background: radial-gradient(circle at 50% 30%, var(--acento-medio), transparent 60%), var(--tarjeta);
  border: 1px solid var(--borde); border-radius: 14px; min-height: 340px; padding: 32px 24px;
  display: flex; flex-direction: column; justify-content: center; gap: 18px;
}
.alineacion { display: grid; grid-template-columns: repeat(5, 1fr); gap: 10px; }
.hueco { display: flex; flex-direction: column; align-items: center; gap: 10px; }
.silueta { width: 100%; aspect-ratio: 3 / 4; border-radius: 10px; background: linear-gradient(180deg, var(--tarjeta-2), var(--fondo)); border: 1px solid var(--borde); }
.ventajas { padding-bottom: 56px; }
.ventajas .tarjeta { padding: 40px 20px; }
.ventajas p { margin: 0; }
.noticias { padding-bottom: 56px; }
.noticia { color: var(--texto); display: flex; flex-direction: column; gap: 6px; }
.noticia:hover { text-decoration: none; border-color: var(--acento); }
.noticia h3 { margin: 0; }
@media (max-width: 860px) { .hero { grid-template-columns: 1fr; padding-top: 36px; } }
</style>
