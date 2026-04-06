package com.example.moodmusic.model

import androidx.room.Database
import androidx.room.RoomDatabase

// PDF 3 - Room
// Define la base de datos y conecta Entity + DAO
// version = 1 es la versión inicial
@Database(entities = [UsuarioEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}