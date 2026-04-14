package com.example.moodmusic.model
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // -------------------------------------------------------
    // INSERTAR
    // -------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: UsuarioEntity)

    // -------------------------------------------------------
    //  NUEVO: ACTUALIZAR USUARIO (para avatar)
    // -------------------------------------------------------
    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    // -------------------------------------------------------
    // OBTENER TODOS
    // -------------------------------------------------------
    @Query("SELECT * FROM usuarios")
    fun obtenerTodos(): Flow<List<UsuarioEntity>>

    // -------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------
    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun buscarPorUsername(username: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): UsuarioEntity?

    // -------------------------------------------------------
    // VALIDACIÓN
    // -------------------------------------------------------
    @Query("SELECT COUNT(*) FROM usuarios WHERE username = :username OR correo = :correo")
    suspend fun existeUsuario(username: String, correo: String): Int

}