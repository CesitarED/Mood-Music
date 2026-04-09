package com.example.moodmusic.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UsuarioEntity::class, EstadoAnimoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun estadoAnimoDao(): EstadoAnimoDao
}
