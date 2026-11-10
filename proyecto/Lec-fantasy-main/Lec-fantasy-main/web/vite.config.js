import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    // En desarrollo, /api se redirige al backend de Spring Boot
    proxy: {
      // Si el backend usa otro puerto: $env:API_PROXY="http://localhost:8081"; npm run dev
      '/api': process.env.API_PROXY || 'http://localhost:8080'
    }
  }
})
