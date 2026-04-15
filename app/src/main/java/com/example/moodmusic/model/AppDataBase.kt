package com.example.moodmusic.model

import androidx.room.Database
import androidx.room.RoomDatabase

// Se incrementó la versión a 3 para forzar la recreación de la base de datos tras cambios en las entidades
@Database(entities = [UsuarioEntity::class, EstadoAnimoEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun estadoAnimoDao(): EstadoAnimoDao
}
