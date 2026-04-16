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

class HistorialViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val sessionManager = SessionManager(application)
    
    var listaEstados by mutableStateOf<List<EstadoAnimoEntity>>(emptyList())
        private set

    init {
        val username = sessionManager.getUsername()
        if (username != null) {
            cargarHistorial(username)
        }
    }

    fun cargarHistorial(username: String) {
        viewModelScope.launch {
            db.estadoAnimoDao().obtenerPorUsuario(username).collectLatest { lista ->
                listaEstados = lista
            }
        }
    }
}
