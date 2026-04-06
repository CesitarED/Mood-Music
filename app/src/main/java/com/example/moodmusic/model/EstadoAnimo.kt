package com.example.moodmusic.model

import java.io.Serializable

// Data class que representa un estado de ánimo
// Data Class con ViewModel
data class EstadoAnimo(
    val nombre: String = "",
    val color: Long = 0xFF000000,
    val emoji: String = ""
) : Serializable
