package com.example.moodmusic.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadoAnimoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(estado: EstadoAnimoEntity)

    @Query("SELECT * FROM estados_animo_registrados WHERE username = :username ORDER BY fechaCompleta DESC")
    fun obtenerPorUsuario(username: String): Flow<List<EstadoAnimoEntity>>
}
