<template>
  <div class="panel-fichaje pila">
    <div v-if="opciones.length > 1" class="campo">
      <label :for="`liga-${uid}`">Fichar en</label>
      <select :id="`liga-${uid}`" v-model="equipoId" class="input">
        <option v-for="o in opciones" :key="o.equipo.id" :value="o.equipo.id">{{ o.liga.nombre }} · {{ o.equipo.nombre }}</option>
      </select>
    </div>

    <div class="importe">
      <span class="micro">{{ yaEsMio ? 'Valor de mercado' : 'Precio del fichaje' }}</span>
      <span class="cifra">{{ euros(jugador.valorMercado) }} €</span>
      <span v-if="var_" :class="var_.clase" class="var">{{ var_.texto }}</span>
    </div>

    <div v-if="equipo && yaEsMio" class="filas">
      <div class="fila-entre"><span class="tenue">Estado</span><b class="texto-acento">En tu plantilla</b></div>
      <div class="fila-entre"><span class="tenue">Tu saldo</span><b>{{ euros(equipo.dineroDisponible) }} €</b></div>
      <div class="fila-entre"><span class="tenue">Saldo si lo vendes</span><b>{{ euros(Number(equipo.dineroDisponible) + Number(jugador.valorMercado)) }} €</b></div>
    </div>
    <div v-else-if="equipo" class="filas">
      <div class="fila-entre"><span class="tenue">Tu saldo</span><b>{{ euros(equipo.dineroDisponible) }} €</b></div>
      <div class="fila-entre">
        <span class="tenue">Saldo tras el fichaje</span>
        <b :class="{ 'texto-rojo': saldoTras < 0 }">{{ euros(saldoTras) }} €</b>
      </div>
      <div class="fila-entre">
        <span class="tenue">Destino</span>
        <b>{{ destino === 'TITULAR' ? `Titular (${jugador.posicion})` : destino === 'BANQUILLO' ? 'Banquillo' : '—' }}</b>
      </div>
      <div class="fila-entre"><span class="tenue">Plantilla</span><b>{{ equipo.plantilla.length }}/7</b></div>
    </div>

    <Alerta v-if="motivo && !yaEsMio" :mensaje="motivo" tipo="aviso" />
    <Alerta :mensaje="error" />

    <div class="fila botones">
      <button v-if="cancelable" type="button" class="btn btn-fantasma" @click="$emit('cancelar')">Cancelar</button>
      <button v-if="yaEsMio" type="button" class="btn btn-texto-rojo crece" :disabled="enviando || !equipo.mercadoAbierto"
              @click="vender">Vender por {{ millones(jugador.valorMercado) }}</button>
      <button v-else type="button" class="btn crece" :disabled="enviando || !!motivo" @click="fichar">
        {{ enviando ? 'Fichando…' : `Confirmar fichaje · ${millones(jugador.valorMercado)}` }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import Alerta from './Alerta.vue'
import { api } from '../api/cliente'
import { useJuego } from '../stores/juego'
import { destinoFichaje, euros, millones, motivoNoFichar, variacion } from '../utils/formato'

const props = defineProps({
  jugador: { type: Object, required: true },
  /** Equipo con el que fichar; si no se indica, el activo */
  equipoInicial: { type: Number, default: null },
  cancelable: Boolean
})
const emit = defineEmits(['fichado', 'vendido', 'cancelar'])

const juego = useJuego()
const uid = Math.random().toString(36).slice(2, 8)
const equipoId = ref(props.equipoInicial || juego.activoId)
const enviando = ref(false)
const error = ref('')

const opciones = computed(() => juego.ligas
  .filter((l) => juego.equipos[l.miEquipoId])
  .map((l) => ({ liga: l, equipo: juego.equipos[l.miEquipoId] })))
const equipo = computed(() => juego.equipos[equipoId.value] || null)
const yaEsMio = computed(() => !!equipo.value?.plantilla.some((p) => p.jugador.id === props.jugador.id))
const motivo = computed(() => motivoNoFichar(equipo.value, props.jugador))
const destino = computed(() => destinoFichaje(equipo.value, props.jugador))
const saldoTras = computed(() => Number(equipo.value?.dineroDisponible || 0) - Number(props.jugador.valorMercado))
const var_ = computed(() => variacion(props.jugador))

watch(() => props.equipoInicial, (v) => { if (v) equipoId.value = v })
watch(equipoId, () => (error.value = ''))

async function fichar() {
  error.value = ''
  enviando.value = true
  try {
    const actualizado = await api.post(`/equipos/${equipo.value.id}/fichajes`, { jugadorId: props.jugador.id })
    juego.actualizarEquipo(actualizado)
    const p = actualizado.plantilla.find((x) => x.jugador.id === props.jugador.id)
    emit('fichado', { equipo: actualizado, titular: p?.estado === 'TITULAR' })
  } catch (e) {
    error.value = e.message
  } finally {
    enviando.value = false
  }
}

async function vender() {
  if (!confirm(`¿Vender a ${props.jugador.nickname} por ${millones(props.jugador.valorMercado)}?`)) return
  error.value = ''
  enviando.value = true
  try {
    const actualizado = await api.del(`/equipos/${equipo.value.id}/plantilla/${props.jugador.id}`)
    juego.actualizarEquipo(actualizado)
    emit('vendido', { equipo: actualizado })
  } catch (e) {
    error.value = e.message
  } finally {
    enviando.value = false
  }
}
</script>

<style scoped>
.importe {
  display: flex; flex-direction: column; gap: 4px; padding: 14px 16px; border-radius: var(--radio-peque);
  background: var(--tarjeta-2); border: 1px solid var(--borde-2); border-left: 3px solid var(--acento);
}
.importe .cifra { font-size: 1.7rem; }
.var { font-size: 0.78rem; }
.filas { display: flex; flex-direction: column; }
.filas .fila-entre { padding: 8px 0; border-bottom: 1px solid var(--borde); font-size: 0.88rem; flex-wrap: nowrap; }
.filas .fila-entre:last-child { border-bottom: 0; }
.botones { flex-wrap: nowrap; }
.crece { flex: 1; }
</style>
