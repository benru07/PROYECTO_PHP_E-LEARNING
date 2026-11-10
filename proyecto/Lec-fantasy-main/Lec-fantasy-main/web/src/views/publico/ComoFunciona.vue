<template>
  <section class="contenedor pagina">
    <h1>Cómo funciona</h1>
    <p class="muted lead">Todo lo que necesitas saber para ganar tu liga.</p>

    <div class="grid grid-3 mt-2">
      <div class="tarjeta paso" v-for="(p, i) in pasos" :key="p.t">
        <span class="numero">{{ i + 1 }}</span>
        <h3>{{ p.t }}</h3>
        <p class="muted">{{ p.d }}</p>
      </div>
    </div>

    <div class="grid grid-2 mt-2">
      <div class="tarjeta">
        <h2>Puntos por partida</h2>
        <table class="tabla">
          <tbody>
            <tr v-for="r in puntos" :key="r[0]"><td>{{ r[0] }}</td><td class="num texto-acento negrita">{{ r[1] }}</td></tr>
          </tbody>
        </table>
        <p class="tenue mt">Ejemplo: un ADC que acaba 6/2/8 con 300 de CS, 30 de visión y gana suma 41,5 puntos.</p>
      </div>
      <div class="pila">
        <div class="tarjeta">
          <h2>Tu plantilla</h2>
          <ul class="muted lista">
            <li>7 jugadores: 5 titulares (uno por rol) y 2 suplentes.</li>
            <li>Solo puntúan los titulares. Un jugador solo puede ser titular en su rol.</li>
            <li>Cada liga tiene su presupuesto. En la Liga Global empiezas con 50 M.</li>
            <li>El mercado se cierra mientras se juega la jornada.</li>
            <li>Los precios suben o bajan hasta un 8 % por jornada según el rendimiento.</li>
          </ul>
        </div>
        <div class="tarjeta">
          <h2>Mejoras</h2>
          <p class="muted">
            Compra una mejora para un jugador antes de la jornada (por ejemplo, «Triple Kill»). Si la consigue y es
            titular, sus puntos se multiplican. Si no, pierdes lo que pagaste.
          </p>
        </div>
      </div>
    </div>

    <div class="centrado mt-2">
      <RouterLink to="/registro" class="btn btn-grande">Crear mi equipo</RouterLink>
    </div>
  </section>
</template>

<script setup>
const pasos = [
  { t: 'Regístrate', d: 'Crea tu cuenta en un minuto. Entrarás automáticamente en la Liga Global.' },
  { t: 'Forma tu equipo', d: 'Ficha a un jugador por rol sin pasarte de presupuesto y elige a tus suplentes.' },
  { t: 'Compite', d: 'Cada jornada sumas los puntos de tus titulares según las partidas reales de la LEC.' }
]
const puntos = [
  ['Kill', '+3'], ['Asistencia', '+2'], ['Muerte', '−1'], ['Cada 50 de CS', '+1'], ['Cada 20 de visión', '+1'],
  ['Doble kill', '+1'], ['Triple kill', '+2'], ['Quadra kill', '+5'], ['Pentakill', '+10'],
  ['First blood', '+2'], ['Victoria', '+2'], ['MVP de la partida', '+3']
]
</script>

<style scoped>
.lead { font-size: 1.15rem; }
.paso .numero { display: inline-grid; place-items: center; width: 36px; height: 36px; border-radius: 50%; background: var(--acento); color: var(--acento-texto); font-weight: 800; margin-bottom: 10px; }
.lista { padding-left: 20px; margin: 0; display: flex; flex-direction: column; gap: 6px; }
</style>
