package com.example.evaluacion2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Declara la base de datos y sus tablas (por ahora solo "oportunidades")
@Database(entities = [OportunidadEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Da acceso al DAO para guardar y leer oportunidades
    abstract fun oportunidadDao(): OportunidadDao

    companion object {
        // Guarda la base de datos para que exista una sola en toda la app
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Versión 1 -> 2: agrega las columnas de foto y ubicación sin borrar los datos existentes
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE oportunidades ADD COLUMN fotoUri TEXT")
                db.execSQL("ALTER TABLE oportunidades ADD COLUMN latitud REAL")
                db.execSQL("ALTER TABLE oportunidades ADD COLUMN longitud REAL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            // Si ya existe la devuelve, y si no, la crea
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oportunidades_db" // nombre del archivo de la base de datos
                ).addMigrations(MIGRACION_1_2)
                    .build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}
