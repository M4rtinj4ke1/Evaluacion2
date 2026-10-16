package com.example.evaluacion2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Declara la base de datos y sus tablas (por ahora solo "oportunidades")
@Database(entities = [OportunidadEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Da acceso al DAO para guardar y leer oportunidades
    abstract fun oportunidadDao(): OportunidadDao

    companion object {
        // Guarda la base de datos para que exista una sola en toda la app
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si ya existe la devuelve, y si no, la crea
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oportunidades_db" // nombre del archivo de la base de datos
                ).build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}