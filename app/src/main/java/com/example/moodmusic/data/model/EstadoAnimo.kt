package com.example.moodmusic.data.model

import java.io.Serializable

data class EstadoAnimo(
    val nombre: String = "",
    val color: Long = 0xFF000000,
    val emoji: String = ""
) : Serializable
