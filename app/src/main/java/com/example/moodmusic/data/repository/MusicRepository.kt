package com.example.moodmusic.data.repository

import com.example.moodmusic.data.remote.MoodMapper
import com.example.moodmusic.data.remote.api.MusicApiService
import com.example.moodmusic.data.remote.model.TrackDto
import com.example.moodmusic.data.model.EstadoAnimoEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MusicRepository(private val apiService: MusicApiService) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val apiKey = "92a80fbc46f2c10f4cdd521e9e78ea7b"

    suspend fun obtenerRecomendaciones(mood: String): List<TrackDto> {
        val tag = MoodMapper.mapMoodToTag(mood)
        return try {
            val response = apiService.getSongsByTag(tag, apiKey)
            response.tracks.track
        } catch (e: Exception) {
            emptyList()
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
