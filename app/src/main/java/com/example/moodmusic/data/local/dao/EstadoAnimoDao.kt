package com.example.moodmusic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moodmusic.data.model.EstadoAnimoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadoAnimoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(estado: EstadoAnimoEntity)

    @Query("SELECT * FROM estados_animo_registrados WHERE username = :username ORDER BY fechaCompleta DESC")
    fun obtenerPorUsuario(username: String): Flow<List<EstadoAnimoEntity>>

    @Query("UPDATE estados_animo_registrados SET username = :nuevoUsername WHERE username = :viejoUsername")
    suspend fun actualizarUsernameHistorial(viejoUsername: String, nuevoUsername: String)

    @Query("SELECT * FROM estados_animo_registrados WHERE username = :username AND dia = :dia AND mes = :mes AND anio = :anio LIMIT 1")
    suspend fun obtenerRegistroHoy(username: String, dia: String, mes: String, anio: String): EstadoAnimoEntity?
}
