<template>
  <div class="grid grid-2">
    <div class="campo">
      <label for="fecha">Fecha de nacimiento <span v-if="obligatorio">*</span></label>
      <input id="fecha" v-model="modelo.fechaNacimiento" type="date" class="input" :max="hoy"
             :class="{ invalido: errores.fechaNacimiento }" :required="obligatorio" />
      <span v-if="errores.fechaNacimiento" class="error-campo">{{ errores.fechaNacimiento }}</span>
    </div>
    <div class="campo">
      <label for="pais">País <span v-if="obligatorio">*</span></label>
      <select id="pais" v-model="modelo.pais" class="input" :class="{ invalido: errores.pais }" :required="obligatorio">
        <option value="" disabled>Selecciona un país</option>
        <option v-for="p in PAISES" :key="p.codigo" :value="p.codigo">{{ p.nombre }}</option>
      </select>
      <span v-if="errores.pais" class="error-campo">{{ errores.pais }}</span>
    </div>
    <div class="campo">
      <label for="equipo">Equipo LEC favorito</label>
      <select id="equipo" v-model="modelo.equipoFavoritoId" class="input">
        <option :value="null">Ninguno</option>
        <option v-for="e in equipos" :key="e.id" :value="e.id">{{ e.nombre }}</option>
      </select>
    </div>
    <div class="campo">
      <label for="riot">Riot ID (opcional)</label>
      <input id="riot" v-model.trim="modelo.riotId" class="input" placeholder="Nombre#EUW"
             :class="{ invalido: errores.riotId }" />
      <span v-if="errores.riotId" class="error-campo">{{ errores.riotId }}</span>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api/cliente'
import { PAISES } from '../utils/paises'

defineProps({
  modelo: { type: Object, required: true },
  errores: { type: Object, default: () => ({}) },
  obligatorio: { type: Boolean, default: true }
})

const equipos = ref([])
const hoy = new Date().toISOString().slice(0, 10)

onMounted(async () => {
  try {
    equipos.value = await api.get('/equipos-pro')
  } catch {
    equipos.value = []
  }
})
</script>
