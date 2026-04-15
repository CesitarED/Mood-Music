package com.example.moodmusic.model

import android.content.Context
import androidx.room.Room

// PDF 3 - Room
// object = singleton, una sola instancia en toda la app
// Igual que PersonaRepository en el PDF 1
// Garantiza que no haya múltiples conexiones a la BD
object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "moodmusic_db"
            )
            .fallbackToDestructiveMigration() // Permite recrear la BD si cambia el esquema sin migraciones manuales
            .build()
            INSTANCE = instance
            instance
        }
    }
}