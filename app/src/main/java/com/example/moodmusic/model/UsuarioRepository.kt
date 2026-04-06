package com.example.moodmusic.model

// -------------------------------------------------------
// UsuarioRepository
// Singleton que actúa como caché compartida entre Activities
// En el futuro se reemplaza por Room Database o SharedPreferences
// -------------------------------------------------------
object UsuarioRepository {

    // Lista de usuarios registrados en memoria
    private val usuarios = mutableListOf<Usuario>()

    // Usuario actualmente en sesión
    var usuarioActual: Usuario? = null
        private set

    // -------------------------------------------------------
    // Registrar usuario
    // Retorna true si fue exitoso, false si ya existe
    // -------------------------------------------------------
    fun registrar(usuario: Usuario): Boolean {
        val yaExiste = usuarios.any {
            it.username == usuario.username || it.correo == usuario.correo
        }
        return if (yaExiste) {
            false
        } else {
            usuarios.add(usuario)
            true
        }
    }

    // -------------------------------------------------------
    // Iniciar sesión
    // Busca por nombre, username o correo + contraseña
    // -------------------------------------------------------
    fun login(nombreOUsername: String, contrasena: String): Boolean {
        val usuario = usuarios.find {
            (it.username == nombreOUsername ||
                    it.correo   == nombreOUsername ||
                    it.nombre   == nombreOUsername) &&
                    it.contrasena == contrasena
        }
        return if (usuario != null) {
            usuarioActual = usuario
            true
        } else {
            false
        }
    }

    // Cerrar sesión
    fun cerrarSesion() {
        usuarioActual = null
    }
}