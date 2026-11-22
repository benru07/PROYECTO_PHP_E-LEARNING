<template>
  <section class="contenedor pagina">
    <h1>Noticias</h1>
    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" />
    <div v-if="!cargando && !noticias.length && !error" class="vacio tarjeta">Todavía no hay noticias publicadas.</div>
    <div class="pila mt">
      <RouterLink v-for="n in noticias" :key="n.id" :to="`/noticias/${n.id}`" class="tarjeta noticia">
        <img v-if="n.imagenUrl" :src="n.imagenUrl" alt="" class="imagen" />
        <div>
          <span class="tenue">{{ fecha(n.fechaPublicacion) }}<template v-if="n.autor"> · {{ n.autor }}</template></span>
          <h2>{{ n.titulo }}</h2>
          <p class="muted">{{ n.resumen || recortar(n.contenido) }}</p>
        </div>
      </RouterLink>
    </div>
    <div v-if="totalPaginas > 1" class="fila mt">
      <button class="btn btn-oscuro btn-peque" :disabled="pagina === 0" @click="cargar(pagina - 1)">Anteriores</button>
      <span class="tenue">Página {{ pagina + 1 }} de {{ totalPaginas }}</span>
      <button class="btn btn-oscuro btn-peque" :disabled="pagina + 1 >= totalPaginas" @click="cargar(pagina + 1)">Siguientes</button>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'
import { fecha } from '../../utils/formato'

const noticias = ref([])
const pagina = ref(0)
const totalPaginas = ref(0)
const cargando = ref(true)
const error = ref('')
const recortar = (t) => (t.length > 220 ? `${t.slice(0, 220)}…` : t)

async function cargar(p) {
  cargando.value = true
  try {
    const r = await api.get('/noticias', { pagina: p, tamano: 10 })
    noticias.value = r.contenido
    pagina.value = r.pagina
    totalPaginas.value = r.totalPaginas
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
}
onMounted(() => cargar(0))
</script>

<style scoped>
.noticia { color: var(--texto); display: flex; gap: 20px; align-items: flex-start; }
.noticia:hover { text-decoration: none; border-color: var(--acento); }
.noticia h2 { margin: 4px 0 6px; }
.noticia p { margin: 0; }
.imagen { width: 180px; height: 110px; object-fit: cover; border-radius: 8px; flex-shrink: 0; }
@media (max-width: 600px) { .noticia { flex-direction: column; } .imagen { width: 100%; height: 160px; } }
</style>
