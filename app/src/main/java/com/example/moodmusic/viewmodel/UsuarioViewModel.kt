package com.example.moodmusic.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodmusic.data.SessionManager
import com.example.moodmusic.data.local.database.DatabaseProvider
import com.example.moodmusic.data.model.UsuarioEntity
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider
        .getDatabase(application)
        .usuarioDao()
    
    private val sessionManager = SessionManager(application)

    var mensajeError by mutableStateOf("")
        private set

    var registroExitoso by mutableStateOf(false)
        private set

    var loginExitoso by mutableStateOf(false)
        private set

    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)
        private set

    var usuarioRegistrado: UsuarioEntity? = null
        private set

    val listaUsuarios = dao.obtenerTodos()

    fun registrar(
        username: String,
        nombre: String,
        apellido: String,
        edad: String,
        correo: String,
        contrasena: String
    ) {
        viewModelScope.launch {
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
                    contrasena = contrasena,
                    avatar     = -1
                )
                dao.insertar(usuario)
                usuarioRegistrado = usuario
                usuarioActual = usuario
                
                sessionManager.saveSession(username)
                
                registroExitoso = true
                mensajeError = ""
            }
        }
    }

    fun login(nombreOUsername: String, contrasena: String) {
        viewModelScope.launch {
            val usuario = dao.buscarPorUsername(nombreOUsername)
                ?: dao.buscarPorCorreo(nombreOUsername)
                ?: dao.buscarPorNombre(nombreOUsername)

            if (usuario != null && usuario.contrasena == contrasena) {
                usuarioActual = usuario
                sessionManager.saveSession(usuario.username)
                loginExitoso = true
                mensajeError = ""
            } else {
                mensajeError = "Usuario o contraseña incorrectos."
                loginExitoso = false
            }
        }
    }

    fun actualizarAvatar(avatar: Int) {
        viewModelScope.launch {
            usuarioActual?.let { usuario ->
                val actualizado = usuario.copy(avatar = avatar)
                dao.actualizar(actualizado)
                usuarioActual = actualizado
                usuarioRegistrado = actualizado
            }
        }
    }

    fun limpiarEstados() {
        mensajeError = ""
        loginExitoso = false
        registroExitoso = false
    }

    fun cerrarSesion() {
        usuarioActual = null
        sessionManager.logout()
    }
}
