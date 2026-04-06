package com.example.moodmusic.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodmusic.model.DatabaseProvider
import com.example.moodmusic.model.UsuarioEntity
import kotlinx.coroutines.launch

// PDF 3 - Room + ViewModel
// AndroidViewModel recibe Application para inicializar
// la base de datos con DatabaseProvider
// Igual que PersonaViewModel en el PDF 3
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    // Obtiene el DAO directo desde DatabaseProvider
    // Igual que el profesor: private val dao = DatabaseProvider.getDatabase(application).personaDao()
    private val dao = DatabaseProvider
        .getDatabase(application)
        .usuarioDao()

    // Estado observable para la UI (PDF 2 - mutableStateOf)
    var mensajeError by mutableStateOf("")
        private set

    var registroExitoso by mutableStateOf(false)
        private set

    var loginExitoso by mutableStateOf(false)
        private set

    // Usuario en sesión
    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)
        private set

    // Lista observable en tiempo real con Flow (PDF 3)
    val listaUsuarios = dao.obtenerTodos()

    // -------------------------------------------------------
    // Registrar usuario
    // viewModelScope.launch ejecuta en hilo seguro (PDF 3)
    // -------------------------------------------------------
    fun registrar(
        username: String,
        nombre: String,
        apellido: String,
        edad: String,
        correo: String,
        contrasena: String
    ) {
        viewModelScope.launch {
            // Verifica si ya existe
            val existe = dao.existeUsuario(username, correo)
            if (existe > 0) {
                mensajeError = "El usuario o correo ya está registrado."
                registroExitoso = false
            } else {
                val usuario = UsuarioEntity(
                    username   = username,
                    nombre     = nombre,
                    apellido   = apellido,
                    edad       = edad,
                    correo     = correo,
                    contrasena = contrasena
                )
                dao.insertar(usuario)
                registroExitoso = true
                mensajeError = ""
            }
        }
    }

    // -------------------------------------------------------
    // Login
    // Busca en la BD por nombre/username/correo + contraseña
    // -------------------------------------------------------
    fun login(nombreOUsername: String, contrasena: String) {
        viewModelScope.launch {
            // Busca por username, correo o nombre
            val usuario = dao.buscarPorUsername(nombreOUsername)
                ?: dao.buscarPorCorreo(nombreOUsername)
                ?: dao.buscarPorNombre(nombreOUsername)

            if (usuario != null && usuario.contrasena == contrasena) {
                usuarioActual = usuario
                loginExitoso = true
                mensajeError = ""
            } else {
                mensajeError = "Usuario o contraseña incorrectos."
                loginExitoso = false
            }
        }
    }

    // Limpiar estados después de navegar
    fun limpiarEstados() {
        mensajeError = ""
        loginExitoso = false
        registroExitoso = false
    }

    // Cerrar sesión
    fun cerrarSesion() {
        usuarioActual = null
    }
}