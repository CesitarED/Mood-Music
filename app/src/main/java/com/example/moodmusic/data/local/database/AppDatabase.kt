package com.example.moodmusic.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moodmusic.data.local.dao.EstadoAnimoDao
import com.example.moodmusic.data.local.dao.UsuarioDao
import com.example.moodmusic.data.model.EstadoAnimoEntity
import com.example.moodmusic.data.model.UsuarioEntity

@Database(entities = [UsuarioEntity::class, EstadoAnimoEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun estadoAnimoDao(): EstadoAnimoDao
}
