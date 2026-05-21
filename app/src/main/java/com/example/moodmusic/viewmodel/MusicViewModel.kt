package com.example.moodmusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moodmusic.data.model.Cancion
import com.example.moodmusic.data.remote.api.MusicApiService
import com.example.moodmusic.data.repository.MusicRepository
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MusicViewModel(private val repository: MusicRepository) : ViewModel() {

    var canciones by mutableStateOf<List<Cancion>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    fun cargarCanciones(mood: String) {
        viewModelScope.launch {
            cargando = true
            try {
                canciones = repository.obtenerRecomendaciones(mood)
            } catch (e: Exception) {
                canciones = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    fun guardarRegistroCloud(
        emocion: String,
        emoji: String,
        color: Long,
        nota: String,
        dia: String,
        mes: String,
        anio: String
    ) {
        viewModelScope.launch {
            repository.guardarHistorialCloud(emocion, emoji, color, nota, dia, mes, anio)
        }
    }
}

class MusicViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MusicApiService::class.java)
        val repo = MusicRepository(api)
        return MusicViewModel(repo) as T
    }
}
