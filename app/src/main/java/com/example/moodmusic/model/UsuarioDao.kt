package com.example.moodmusic.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// PDF 3 - Room
// El DAO define todas las operaciones sobre la tabla
@Dao
interface UsuarioDao {

    // Insertar usuario
    // IGNORE: si el username ya existe, no hace nada
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: UsuarioEntity)

    // Obtener todos como Flow
    // Flow permite que la UI observe cambios en tiempo real
    @Query("SELECT * FROM usuarios")
    fun obtenerTodos(): Flow<List<UsuarioEntity>>

    // Buscar por username para el login
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun buscarPorUsername(username: String): UsuarioEntity?

    // Buscar por correo para el login
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    // Buscar por nombre para el login
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): UsuarioEntity?

    // Verificar si ya existe el username o correo
    @Query("SELECT COUNT(*) FROM usuarios WHERE username = :username OR correo = :correo")
    suspend fun existeUsuario(username: String, correo: String): Int
}