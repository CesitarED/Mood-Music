package com.example.moodmusic.model

import androidx.room.Database
import androidx.room.RoomDatabase

// Se incrementó la versión de 1 a 2 debido al cambio en EstadoAnimoEntity (se añadió el campo avatar)
@Database(entities = [UsuarioEntity::class, EstadoAnimoEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun estadoAnimoDao(): EstadoAnimoDao
}
