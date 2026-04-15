package com.example.moodmusic.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "estados_animo_registrados")
data class EstadoAnimoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val nombreEstado: String,
    val emojiEstado: String,
    val colorEstado: Long,
    val nota: String,
    val dia: String,
    val mes: String,
    val anio: String,
    val fechaCompleta: Long,
    val avatar: Int = -1
) : Serializable
