package com.example.moodmusic.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val username: String,
    val nombre: String,
    val apellido: String,
    val edad: String,
    val correo: String,
    val contrasena: String,
    var avatar: Int
) : Serializable
