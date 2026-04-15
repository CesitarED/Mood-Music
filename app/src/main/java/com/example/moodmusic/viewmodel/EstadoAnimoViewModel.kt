package com.example.moodmusic.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodmusic.data.local.database.DatabaseProvider
import com.example.moodmusic.data.model.EstadoAnimo
import com.example.moodmusic.data.model.EstadoAnimoEntity
import kotlinx.coroutines.launch
import java.util.Calendar

class EstadoAnimoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)

    var estadoSeleccionado by mutableStateOf<EstadoAnimo?>(null)
        private set
        
    var registroHoyExistente by mutableStateOf<Boolean?>(null)
        private set

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

    fun seleccionarEstado(estado: EstadoAnimo) {
        estadoSeleccionado = estado
    }

    fun verificarRegistroHoy(username: String) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val dia = cal.get(Calendar.DAY_OF_MONTH).toString()
            val mes = (cal.get(Calendar.MONTH) + 1).toString()
            val anio = cal.get(Calendar.YEAR).toString()
            
            val registro = db.estadoAnimoDao().obtenerRegistroHoy(username, dia, mes, anio)
            registroHoyExistente = registro != null
        }
    }

    fun guardarEstado(username: String, nota: String, avatar: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            estadoSeleccionado?.let { estado ->
                val cal = Calendar.getInstance()
                val nuevoEstado = EstadoAnimoEntity(
                    username = username,
                    nombreEstado = estado.nombre,
                    emojiEstado = estado.emoji,
                    colorEstado = estado.color,
                    nota = nota,
                    dia = cal.get(Calendar.DAY_OF_MONTH).toString(),
                    mes = (cal.get(Calendar.MONTH) + 1).toString(),
                    anio = cal.get(Calendar.YEAR).toString(),
                    fechaCompleta = System.currentTimeMillis(),
                    avatar = avatar
                )
                db.estadoAnimoDao().insertar(nuevoEstado)
                onComplete()
            }
        }
    }
}
