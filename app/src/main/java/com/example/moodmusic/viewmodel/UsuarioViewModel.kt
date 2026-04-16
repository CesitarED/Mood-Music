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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider
        .getDatabase(application)
        .usuarioDao()
    
    private val sessionManager = SessionManager(application)

    private val _usernameSession = MutableStateFlow(sessionManager.getUsername())

    var mensajeError by mutableStateOf("")
        private set

    var registroExitoso by mutableStateOf(false)
        private set

    var loginExitoso by mutableStateOf(false)
        private set

    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)
        private set

    init {
        viewModelScope.launch {
            // Cada vez que el username en sesión cambie, reiniciamos el Flow de Room
            _usernameSession.flatMapLatest { username ->
                if (username != null) dao.buscarPorUsernameFlow(username)
                else flowOf(null)
            }.collect { usuario ->
                usuarioActual = usuario
            }
        }
    }

    var usuarioRegistrado: UsuarioEntity? = null
        private set

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
                
                sessionManager.saveSession(username)
                _usernameSession.value = username
                
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
                sessionManager.saveSession(usuario.username)
                _usernameSession.value = usuario.username
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
            val userToUpdate = usuarioActual ?: sessionManager.getUsername()?.let { 
                dao.buscarPorUsername(it) 
            }

            userToUpdate?.let { usuario ->
                val actualizado = usuario.copy(avatar = avatar)
                dao.actualizar(actualizado)
            }
        }
    }

    private val estadoAnimoDao = DatabaseProvider
        .getDatabase(application)
        .estadoAnimoDao()

    fun actualizarPerfil(
        nombre: String,
        apellido: String,
        nuevoUsername: String,
        edad: String,
        correo: String
    ) {
        viewModelScope.launch {
            usuarioActual?.let { usuario ->
                val usernameAntiguo = usuario.username
                val actualizado = usuario.copy(
                    nombre = nombre,
                    apellido = apellido,
                    username = nuevoUsername,
                    edad = edad,
                    correo = correo
                )

                if (usernameAntiguo != nuevoUsername) {
                    // 1. Actualizamos masivamente el historial para que no se pierda
                    estadoAnimoDao.actualizarUsernameHistorial(usernameAntiguo, nuevoUsername)
                    
                    // 2. Borramos el usuario viejo e insertamos el nuevo (cambio de PK)
                    dao.eliminarPorUsername(usernameAntiguo)
                    dao.insertar(actualizado)
                    
                    sessionManager.saveSession(nuevoUsername)
                    _usernameSession.value = nuevoUsername
                } else {
                    dao.actualizar(actualizado)
                }
            }
        }
    }

    fun actualizarContrasena(nuevaContrasena: String) {
        viewModelScope.launch {
            usuarioActual?.let { usuario ->
                val actualizado = usuario.copy(contrasena = nuevaContrasena)
                dao.actualizar(actualizado)
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
