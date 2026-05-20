package com.example.moodmusic.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodmusic.data.SessionManager
import com.example.moodmusic.data.local.database.DatabaseProvider
import com.example.moodmusic.data.model.EstadoAnimoEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.example.moodmusic.data.remote.api.MusicApiService
import com.example.moodmusic.data.repository.MusicRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HistorialViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val sessionManager = SessionManager(application)

    private val musicRepository: MusicRepository by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MusicApiService::class.java)
        MusicRepository(api)
    }
    
    var listaEstados by mutableStateOf<List<EstadoAnimoEntity>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    init {
        val username = sessionManager.getUsername()
        if (username != null) {
            cargarHistorial(username)
        }
    }

    fun cargarHistorial(username: String) {
        viewModelScope.launch {
            cargando = true
            // Intentar cargar de Firestore primero
            val historialCloud = musicRepository.obtenerHistorialCloud()
            if (historialCloud.isNotEmpty()) {
                listaEstados = historialCloud
            } else {
                // Si falla o está vacío, usar Local
                db.estadoAnimoDao().obtenerPorUsuario(username).collectLatest { lista ->
                    listaEstados = lista
                }
            }
            cargando = false
        }
    }
}
