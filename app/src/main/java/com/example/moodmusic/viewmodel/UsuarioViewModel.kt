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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider.getDatabase(application).usuarioDao()
    private val estadoAnimoDao = DatabaseProvider.getDatabase(application).estadoAnimoDao()
    private val sessionManager = SessionManager(application)
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
            _usernameSession.flatMapLatest { username ->
                if (username != null) dao.buscarPorUsernameFlow(username)
                else flowOf(null)
            }.collect { usuario ->
                usuarioActual = usuario
            }
        }
    }

    fun registrar(
        username: String,
        nombre: String,
        apellido: String,
        edad: String,
        correo: String,
        contrasena: String
    ) {
        viewModelScope.launch {
            try {
                // 1. Verificar disponibilidad local (Username es Primary Key)
                val existeLocal = dao.buscarPorUsername(username)
                if (existeLocal != null) {
                    mensajeError = "El nombre de usuario ya existe."
                    return@launch
                }

                // 2. Crear en Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(correo, contrasena).await()
                val uid = authResult.user?.uid ?: return@launch

                // 3. Guardar en Firestore
                val datosFirestore = hashMapOf(
                    "username" to username,
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "edad" to edad,
                    "correo" to correo,
                    "avatar" to -1
                )
                firestore.collection("usuarios").document(uid).set(datosFirestore).await()

                // 4. Guardar en Room local
                val usuario = UsuarioEntity(
                    username = username,
                    nombre = nombre,
                    apellido = apellido,
                    edad = edad,
                    correo = correo,
                    contrasena = contrasena,
                    avatar = -1
                )
                dao.insertar(usuario)
                
                sessionManager.saveSession(username)
                _usernameSession.value = username
                registroExitoso = true
                mensajeError = ""
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: ""
                mensajeError = when {
                    errorMsg.contains("SHA-1") || errorMsg.contains("invalid-app-credential") -> 
                        "Error de configuración: Agrega el SHA-1 en Firebase Console."
                    errorMsg.contains("collision") -> "El correo ya está registrado."
                    else -> "Error: $errorMsg"
                }
                registroExitoso = false
            }
        }
    }

    fun login(nombreOUsername: String, contrasena: String) {
        viewModelScope.launch {
            try {
                val correoFinal = if (nombreOUsername.contains("@")) {
                    nombreOUsername
                } else {
                    dao.buscarPorUsername(nombreOUsername)?.correo 
                        ?: firestore.collection("usuarios")
                            .whereEqualTo("username", nombreOUsername)
                            .get().await().documents.firstOrNull()?.getString("correo")
                }

                if (correoFinal == null) {
                    mensajeError = "Usuario no encontrado"
                    return@launch
                }

                auth.signInWithEmailAndPassword(correoFinal, contrasena).await()
                
                val userRoom = dao.buscarPorCorreo(correoFinal)
                if (userRoom != null) {
                    sessionManager.saveSession(userRoom.username)
                    _usernameSession.value = userRoom.username
                } else {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val doc = firestore.collection("usuarios").document(uid).get().await()
                        val username = doc.getString("username") ?: "User"
                        val nombre = doc.getString("nombre") ?: ""
                        val apellido = doc.getString("apellido") ?: ""
                        val edad = doc.getString("edad") ?: ""
                        val avatar = doc.getLong("avatar")?.toInt() ?: -1
                        
                        val nuevoUsuario = UsuarioEntity(username, nombre, apellido, edad, correoFinal, contrasena, avatar)
                        dao.insertar(nuevoUsuario)
                        sessionManager.saveSession(username)
                        _usernameSession.value = username
                    }
                }
                
                loginExitoso = true
                mensajeError = ""
            } catch (e: Exception) {
                mensajeError = "Credenciales incorrectas o error de conexión"
                loginExitoso = false
            }
        }
    }

    fun actualizarAvatar(avatar: Int) {
        viewModelScope.launch {
            usuarioActual?.let { usuario ->
                val actualizado = usuario.copy(avatar = avatar)
                dao.actualizar(actualizado)
                
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    firestore.collection("usuarios").document(uid).update("avatar", avatar)
                }
            }
        }
    }

    fun actualizarContrasena(nuevaContrasena: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.updatePassword(nuevaContrasena)?.await()
                usuarioActual?.let { usuario ->
                    val actualizado = usuario.copy(contrasena = nuevaContrasena)
                    dao.actualizar(actualizado)
                }
                mensajeError = ""
            } catch (e: Exception) {
                mensajeError = e.localizedMessage ?: "Error al actualizar contraseña"
            }
        }
    }

    fun actualizarPerfil(
        nombre: String,
        apellido: String,
        username: String,
        edad: String,
        correo: String
    ) {
        viewModelScope.launch {
            try {
                usuarioActual?.let { usuario ->
                    // 1. Actualizar en Firestore
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val updates = hashMapOf<String, Any>(
                            "nombre" to nombre,
                            "apellido" to apellido,
                            "username" to username,
                            "edad" to edad,
                            "correo" to correo
                        )
                        firestore.collection("usuarios").document(uid).update(updates).await()
                    }

                    // 2. Actualizar localmente en Room
                    val actualizado = usuario.copy(
                        nombre = nombre,
                        apellido = apellido,
                        username = username,
                        edad = edad,
                        correo = correo
                    )
                    
                    // Si el username cambió, necesitamos actualizar la sesión
                    if (usuario.username != username) {
                        sessionManager.saveSession(username)
                        _usernameSession.value = username
                    }
                    
                    dao.actualizar(actualizado)
                }
                mensajeError = ""
            } catch (e: Exception) {
                mensajeError = e.localizedMessage ?: "Error al actualizar perfil"
            }
        }
    }

    fun cerrarSesion() {
        auth.signOut()
        usuarioActual = null
        sessionManager.logout()
        _usernameSession.value = null
    }

    fun limpiarEstados() {
        mensajeError = ""
        loginExitoso = false
        registroExitoso = false
    }
}
