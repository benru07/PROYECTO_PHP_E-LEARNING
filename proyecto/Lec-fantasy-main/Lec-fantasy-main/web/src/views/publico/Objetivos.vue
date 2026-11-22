<template>
  <section class="contenedor pagina">
    <h1>Nuestros objetivos</h1>
    <p class="lead muted">
      LEC Fantasy acerca la competición europea de League of Legends a los fans, convirtiendo cada partida en una
      experiencia en la que tú también juegas.
    </p>
    <div class="grid grid-2 mt-2">
      <div class="tarjeta">
        <h3>1. Acercar la LEC a los fans</h3>
        <p class="muted">Que seguir la competición europea sea más emocionante y participativo.</p>
      </div>
      <div class="tarjeta">
        <h3>2. Estrategia y conocimiento</h3>
        <p class="muted">Premiar a quien mejor entiende el juego: roles, meta, rendimiento y presupuesto.</p>
      </div>
      <div class="tarjeta">
        <h3>3. Comunidad</h3>
        <p class="muted">Ligas privadas, clasificaciones y noticias para competir con amigos.</p>
      </div>
      <div class="tarjeta">
        <h3>Cómo funciona</h3>
        <p class="muted">
          Regístrate · Forma tu equipo de 5 (Top, Jungla, Mid, ADC, Support) con un presupuesto · Suma puntos según el
          rendimiento real en cada jornada.
        </p>
        <RouterLink to="/como-funciona">Ver las reglas completas →</RouterLink>
      </div>
    </div>
  </section>
</template>

<style scoped>
.lead { font-size: 1.15rem; max-width: 820px; }
</style>
