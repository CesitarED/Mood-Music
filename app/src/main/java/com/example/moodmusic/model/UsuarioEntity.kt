package com.example.moodmusic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// PDF 3 - Room
// @Entity indica que es una tabla en la base de datos
// @PrimaryKey define la clave primaria
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val username: String,
    val nombre: String,
    val apellido: String,
    val edad: String,
    val correo: String,
    val contrasena: String
) : Serializable