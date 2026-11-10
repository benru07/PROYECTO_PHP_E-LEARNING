<template>
  <section class="contenedor pagina estrecho">
    <RouterLink to="/noticias">← Noticias</RouterLink>
    <div v-if="cargando" class="cargando">Cargando…</div>
    <Alerta :mensaje="error" class="mt" />
    <article v-if="noticia" class="mt">
      <span class="tenue">{{ fechaHora(noticia.fechaPublicacion) }}<template v-if="noticia.autor"> · {{ noticia.autor }}</template></span>
      <h1>{{ noticia.titulo }}</h1>
      <p v-if="noticia.resumen" class="lead muted">{{ noticia.resumen }}</p>
      <img v-if="noticia.imagenUrl" :src="noticia.imagenUrl" alt="" class="imagen" />
      <div class="contenido">{{ noticia.contenido }}</div>
    </article>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import Alerta from '../../components/Alerta.vue'
import { api } from '../../api/cliente'
import { fechaHora } from '../../utils/formato'

const route = useRoute()
const noticia = ref(null)
const cargando = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    noticia.value = await api.get(`/noticias/${route.params.id}`)
  } catch (e) {
    error.value = e.message
  } finally {
    cargando.value = false
  }
})
</script>

<style scoped>
.estrecho { max-width: 780px; }
.lead { font-size: 1.15rem; }
.imagen { width: 100%; border-radius: 12px; margin: 10px 0 20px; }
.contenido { white-space: pre-line; font-size: 1.05rem; line-height: 1.7; }
</style>
