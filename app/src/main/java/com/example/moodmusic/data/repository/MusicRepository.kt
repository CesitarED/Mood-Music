package com.example.moodmusic.data.repository

import com.example.moodmusic.data.remote.MoodMapper
import com.example.moodmusic.data.remote.api.MusicApiService
import com.example.moodmusic.data.remote.model.CancionUi
import com.example.moodmusic.data.remote.model.ImageDto
import com.example.moodmusic.data.model.EstadoAnimoEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class MusicRepository(private val apiService: MusicApiService) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val apiKey = "92a80fbc46f2c10f4cdd521e9e78ea7b"

    suspend fun obtenerRecomendaciones(mood: String): List<CancionUi> {
        val tag = MoodMapper.mapMoodToTag(mood)
        return try {
            val response = apiService.getSongsByTag(tag, apiKey)
            val tracks = response.tracks.track
            val trackImages = tracks.map { obtenerMejorImagen(it.image) }
            val artistsWithoutCover = tracks
                .zip(trackImages)
                .filter { (_, imageUrl) -> imageUrl == null }
                .map { (track, _) -> track.artist.name }
                .distinct()

            val artistImages = coroutineScope {
                artistsWithoutCover.map { artistName ->
                    async { artistName to obtenerImagenArtista(artistName) }
                }
            }.awaitAll().toMap()

            val itunesImages = coroutineScope {
                tracks.mapIndexedNotNull { index, track ->
                    if (trackImages[index] == null) {
                        async {
                            val key = "${track.artist.name} - ${track.name}"
                            key to obtenerPortadaItunes(track.name, track.artist.name)
                        }
                    } else {
                        null
                    }
                }
            }.awaitAll().toMap()

            tracks.mapIndexed { index, track ->
                val key = "${track.artist.name} - ${track.name}"
                CancionUi(
                    name = track.name,
                    artistName = track.artist.name,
                    imageUrl = trackImages[index]
                        ?: itunesImages[key]
                        ?: artistImages[track.artist.name]
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun obtenerImagenArtista(artistName: String): String? {
        return try {
            val response = apiService.getArtistInfo(artistName, apiKey)
            obtenerMejorImagen(response.artist?.image.orEmpty())
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun obtenerPortadaItunes(trackName: String, artistName: String): String? {
        return try {
            val response = apiService.searchItunesSong("$artistName $trackName")
            response.results.firstOrNull()?.artworkUrl100
                ?.replace("100x100bb", "600x600bb")
                ?.takeIf { isValidImageUrl(it) }
        } catch (e: Exception) {
            null
        }
    }

    private fun obtenerMejorImagen(images: List<ImageDto>): String? {
        return images
            .sortedByDescending { imagePriority(it.size) }
            .firstOrNull { isValidImageUrl(it.url) }
            ?.url
    }

    private fun imagePriority(size: String): Int {
        return when (size.lowercase()) {
            "mega" -> 5
            "extralarge" -> 4
            "large" -> 3
            "medium" -> 2
            "small" -> 1
            else -> 0
        }
    }

    private fun isValidImageUrl(url: String): Boolean {
        val normalized = url.trim()
        return normalized.isNotEmpty() &&
            !normalized.contains("2a96cbd8b46e442fc41c2b86b821562f", ignoreCase = true) &&
            !normalized.contains("default_album", ignoreCase = true) &&
            !normalized.contains("lastfm_avatar", ignoreCase = true)
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
            db.collection("usuarios")
                .document(uid)
                .collection("historial")
                .add(registro)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun obtenerHistorialCloud(): List<EstadoAnimoEntity> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val email = auth.currentUser?.email ?: ""
        return try {
            val snapshot = db.collection("usuarios")
                .document(uid)
                .collection("historial")
                .get()
                .await()
            
            snapshot.documents.map { doc ->
                EstadoAnimoEntity(
                    id = 0,
                    username = email, // Usamos el email como identificador si no tenemos el username a mano
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
            db.collection("usuarios")
                .document(uid)
                .collection("historial")
                .document(docId)
                .update("nota", nuevaNota)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
