package com.example.moodmusic.data.model

import java.io.Serializable

/**
 * Modelo de dominio para una canción, optimizado para la UI.
 */
data class Cancion(
    val nombre: String,
    val artista: String,
    val imagenes: List<ImagenMusica> = emptyList()
) : Serializable {
    /**
     * Obtiene automáticamente la mejor imagen disponible.
     * Prioriza fotos reales, descarta estrellas grises y REPARA el protocolo (fuerza https).
     * Esto es VITAL para que Android no bloquee la descarga de la imagen por seguridad.
     */
    val mejorImagenUrl: String
        get() {
            // 1. Filtramos imágenes que son basura o placeholders conocidos de Last.fm
            val validas = imagenes.filter { 
                val u = it.url
                u.isNotBlank() && 
                !u.contains("2a96cbd8b46e442fc41c2b86b821562f") &&
                !u.contains("default_album")
            }
            
            if (validas.isEmpty()) return ""

            // 2. Buscamos por prioridad de calidad (extralarge es lo mejor)
            val prioridad = listOf("extralarge", "large", "medium")
            var seleccionada: String? = null
            
            for (p in prioridad) {
                seleccionada = validas.find { it.size == p }?.url
                if (seleccionada != null) break
            }

            val urlFinal = (seleccionada ?: validas.lastOrNull()?.url ?: "").trim()
            
            // 3. REPARACIÓN CRÍTICA: Android bloquea http o URLs sin protocolo (//)
            return when {
                urlFinal.startsWith("https://") -> urlFinal
                urlFinal.startsWith("http://") -> urlFinal.replace("http://", "https://")
                urlFinal.startsWith("//") -> "https:$urlFinal"
                urlFinal.isNotBlank() -> {
                    if (urlFinal.contains("last.fm") || urlFinal.contains("akamaized.net")) {
                         "https://$urlFinal"
                    } else "https://$urlFinal"
                }
                else -> ""
            }
        }
}

/**
 * Representa una imagen de la API en el dominio.
 */
data class ImagenMusica(
    val url: String,
    val size: String
) : Serializable
