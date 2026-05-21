package com.example.moodmusic.data.repository

import com.example.moodmusic.data.remote.MoodMapper
import com.example.moodmusic.data.remote.api.MusicApiService
import com.example.moodmusic.data.model.Cancion
import com.example.moodmusic.data.model.ImagenMusica
import com.example.moodmusic.data.model.EstadoAnimoEntity
import com.example.moodmusic.data.remote.model.ImageDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class MusicRepository(private val apiService: MusicApiService) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val apiKey = "92a80fbc46f2c10f4cdd521e9e78ea7b"

    suspend fun obtenerRecomendaciones(mood: String): List<Cancion> = coroutineScope {
        val tag = MoodMapper.mapMoodToTag(mood)
        try {
            val response = apiService.getSongsByTag(tag, apiKey)
            val tracks = response.tracks?.track ?: emptyList()

            // Procesamos todas las canciones en paralelo para máxima velocidad
            val deferredCanciones = tracks.map { dto ->
                async {
                    try {
                        val name = dto.name ?: "Unknown"
                        val artistName = dto.artist?.name ?: "Unknown Artist"

                        // 1. Buscamos primero la info del ARTISTA (Respaldo más seguro)
                        val artistResponse = apiService.getArtistInfo(artistName, apiKey)
                        var imagesDto = artistResponse.artist?.image ?: emptyList()

                        // 2. Si el artista no tiene foto real, buscamos el detalle de la canción (Álbum)
                        if (esListaDeImagenesInutil(imagesDto)) {
                            val trackInfo = apiService.getTrackInfo(name, artistName, apiKey).track
                            imagesDto = (trackInfo?.album?.image ?: trackInfo?.image) ?: emptyList()
                        }

                        // 3. Si sigue vacío, usamos lo que vino en el listado inicial
                        if (esListaDeImagenesInutil(imagesDto)) {
                            imagesDto = dto.image ?: emptyList()
                        }

                        Cancion(
                            nombre = name,
                            artista = artistName,
                            imagenes = imagesDto.map { ImagenMusica(it.url ?: "", it.size ?: "") }
                        )
                    } catch (e: Exception) {
                        // Fallback final en caso de error
                        Cancion(
                            nombre = dto.name ?: "Unknown",
                            artista = dto.artist?.name ?: "Unknown Artist",
                            imagenes = dto.image?.map { ImagenMusica(it.url ?: "", it.size ?: "") } ?: emptyList()
                        )
                    }
                }
            }
            deferredCanciones.map { it.await() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun esListaDeImagenesInutil(lista: List<ImageDto>?): Boolean {
        if (lista.isNullOrEmpty()) return true
        return lista.all { 
            val url = it.url ?: ""
            url.isBlank() || url.contains("2a96cbd8b46e442fc41c2b86b821562f")
        }
    }

    suspend fun guardarHistorialCloud(
        emocion: String,
        emoji: String,
        color: Long,
        nota: String,
        dia: String,
        mes: String,
        anio: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        val registro = hashMapOf(
            "nombreEstado" to emocion,
            "emojiEstado" to emoji,
            "colorEstado" to color,
            "nota" to nota,
            "dia" to dia,
            "mes" to mes,
            "anio" to anio,
            "fechaCompleta" to System.currentTimeMillis()
        )
        try {
            db.collection("usuarios").document(uid).collection("historial").add(registro).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun obtenerHistorialCloud(): List<EstadoAnimoEntity> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val email = auth.currentUser?.email ?: ""
        return try {
            val snapshot = db.collection("usuarios").document(uid).collection("historial").get().await()
            snapshot.documents.map { doc ->
                EstadoAnimoEntity(
                    id = 0,
                    username = email,
                    nombreEstado = doc.getString("nombreEstado") ?: "",
                    emojiEstado = doc.getString("emojiEstado") ?: "",
                    colorEstado = doc.getLong("colorEstado") ?: 0L,
                    nota = doc.getString("nota") ?: "",
                    dia = doc.getString("dia") ?: "",
                    mes = doc.getString("mes") ?: "",
                    anio = doc.getString("anio") ?: "",
                    fechaCompleta = doc.getLong("fechaCompleta") ?: 0L,
                    firestoreId = doc.id
                )
            }.sortedByDescending { it.fechaCompleta }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun actualizarNotaCloud(docId: String, nuevaNota: String) {
        val uid = auth.currentUser?.uid ?: return
        try {
            db.collection("usuarios").document(uid).collection("historial").document(docId).update("nota", nuevaNota).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
