package com.example.moodmusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.moodmusic.model.EstadoAnimo

// -------------------------------------------------------
// EstadoAnimoViewModel
// StateFlow + ViewModel en Jetpack Compose
// Maneja el estado de la pantalla de selección de ánimo
// -------------------------------------------------------
class EstadoAnimoViewModel : ViewModel() {

    // La UI solo puede leerlo, el ViewModel lo modifica
    var estadoSeleccionado by mutableStateOf<EstadoAnimo?>(null)
        private set

    // Lista fija de estados de ánimo disponibles
    val listaEstados = listOf(
        EstadoAnimo(nombre = "Eufórico",  color = 0xFFFFC107, emoji = "🤩"),
        EstadoAnimo(nombre = "Feliz",     color = 0xFF26C6DA, emoji = "😊"),
        EstadoAnimo(nombre = "Motivado",  color = 0xFF42A5F5, emoji = "💪"),
        EstadoAnimo(nombre = "Inspirado", color = 0xFFAB47BC, emoji = "✨"),
        EstadoAnimo(nombre = "En paz",    color = 0xFF66BB6A, emoji = "\uD83D\uDD4A\uFE0F"),
        EstadoAnimo(nombre = "Reflexivo", color = 0xFF1565C0, emoji = "🤔"),
        EstadoAnimo(nombre = "Triste",    color = 0xFF78909C, emoji = "😢"),
        EstadoAnimo(nombre = "Enojado",   color = 0xFFE53935, emoji = "😠")
    )

    // Función para seleccionar un estado (PDF 3 - State Hoisting)
    // La UI envía el evento, el ViewModel actualiza el estado
    fun seleccionarEstado(estado: EstadoAnimo) {
        estadoSeleccionado = estado
    }

    // Función para guardar en caché (persistencia simple)
    fun guardarEstado(): Boolean {
        return estadoSeleccionado != null
    }
}