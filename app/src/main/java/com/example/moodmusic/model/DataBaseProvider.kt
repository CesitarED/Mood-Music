package com.example.moodmusic.model

import android.content.Context
import androidx.room.Room

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
            .fallbackToDestructiveMigration() // 🔥 Borra la BD vieja y crea la nueva si el esquema cambia
            .build()
            INSTANCE = instance
            instance
        }
    }
}
