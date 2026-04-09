package com.example.moodmusic.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodmusic.model.DatabaseProvider
import com.example.moodmusic.model.EstadoAnimoEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistorialViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    
    var listaEstados by mutableStateOf<List<EstadoAnimoEntity>>(emptyList())
        private set

    fun cargarHistorial(username: String) {
        viewModelScope.launch {
            db.estadoAnimoDao().obtenerPorUsuario(username).collectLatest { lista ->
                listaEstados = lista
            }
        }
    }
}
