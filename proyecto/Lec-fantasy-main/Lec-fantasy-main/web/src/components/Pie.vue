<template>
  <footer class="pie">
    <div class="contenedor">
      <div class="columnas">
        <div>
          <p class="negrita">LEC Fantasy</p>
          <p class="muted">Proyecto de fin de grado · Desarrollo de Aplicaciones Multiplataforma</p>
        </div>
        <div>
          <p class="negrita">Atención al cliente</p>
          <p class="muted">Usa el formulario de reportes dentro del juego</p>
        </div>
        <div>
          <p class="negrita">Legal</p>
          <p class="muted"><RouterLink to="/privacidad">Aviso legal · Privacidad · Términos de uso</RouterLink></p>
        </div>
        <div>
          <p class="negrita">La LEC</p>
          <p class="muted"><a href="https://lolesports.com" target="_blank" rel="noopener">lolesports.com</a></p>
        </div>
      </div>
      <p class="tenue copy">© {{ anio }} LEC Fantasy. Proyecto no afiliado oficialmente a Riot Games.</p>
    </div>
  </footer>
</template>

<script setup>
const anio = new Date().getFullYear()
</script>

<style scoped>
.pie { background: var(--fondo-2); border-top: 1px solid var(--borde); padding: 32px 0 20px; margin-top: auto; }
.columnas { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 24px; }
.columnas p { margin: 0 0 4px; }
.copy { margin-top: 24px; margin-bottom: 0; }
@media (max-width: 760px) { .columnas { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 460px) { .columnas { grid-template-columns: minmax(0, 1fr); } }
</style>
