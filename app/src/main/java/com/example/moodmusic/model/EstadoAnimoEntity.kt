package com.example.moodmusic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "estados_animo_registrados")
data class EstadoAnimoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String, // Relación con el usuario
    val nombreEstado: String,
    val emojiEstado: String,
    val colorEstado: Long,
    val nota: String,
    val dia: String,
    val mes: String,
    val anio: String,
    val fechaCompleta: Long, // Timestamp para ordenar
    val avatar: Int = -1 // Nuevo campo para guardar el avatar del usuario en ese momento
) : Serializable
