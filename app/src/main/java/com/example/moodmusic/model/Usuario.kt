package com.example.moodmusic.model

import java.io.Serializable

data class Usuario(
    val nombre: String = "",
    val apellido: String = "",
    val username: String = "",
    val edad: String = "",
    val correo: String = "",
    val contrasena: String = ""
) : Serializable
