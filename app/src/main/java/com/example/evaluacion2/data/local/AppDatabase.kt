package com.example.evaluacion2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

// Declara las tablas de la base de datos
@Database(
    entities = [
        OportunidadEntity::class,
        OrganizacionEntity::class,
        ContactoEntity::class,
        LocalidadEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao

    abstract fun organizacionDao(): OrganizacionDao

    abstract fun contactoDao(): ContactoDao

    abstract fun localidadDao(): LocalidadDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Versión 1 -> 2: agrega foto y ubicación
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE oportunidades ADD COLUMN fotoUri TEXT")
                db.execSQL("ALTER TABLE oportunidades ADD COLUMN latitud REAL")
                db.execSQL("ALTER TABLE oportunidades ADD COLUMN longitud REAL")
            }
        }

        // Versión 2 -> 3: agrega organizaciones y contactos
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS organizaciones (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        rubro TEXT NOT NULL,
                        comuna TEXT NOT NULL,
                        region TEXT NOT NULL,
                        sitioWeb TEXT NOT NULL,
                        observaciones TEXT NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS contactos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        organizacionId INTEGER NOT NULL,
                        cargo TEXT NOT NULL,
                        area TEXT NOT NULL,
                        correo TEXT NOT NULL,
                        telefono TEXT NOT NULL,
                        medioContacto TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        observaciones TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        // Versión 3 -> 4: agrega la tabla de localidades
        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS localidades (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        region TEXT NOT NULL,
                        comuna TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oportunidades_db"
                )
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4)
                    .addCallback(object : RoomDatabase.Callback() {
                        // Se ejecuta tanto en instalaciones limpias como en migraciones
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                poblarLocalidadesSiEstaVacio(context)
                            }
                        }
                    })
                    .build()

                INSTANCE = instancia
                instancia
            }
        }

        private suspend fun poblarLocalidadesSiEstaVacio(context: Context) {
            try {
                val dao = getDatabase(context).localidadDao()

                // Si ya contiene registros, no vuelve a insertar
                if (dao.contarLocalidades() > 0) return

                val jsonString = context.assets.open("chile_regiones_comunas.json")
                    .bufferedReader().use { it.readText() }

                val jsonArray = JSONArray(jsonString)
                val listaLocalidades = mutableListOf<LocalidadEntity>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val region = obj.getString("region")
                    val comunas = obj.getJSONArray("comunas")

                    for (j in 0 until comunas.length()) {
                        val comuna = comunas.getString(j)
                        listaLocalidades.add(
                            LocalidadEntity(region = region, comuna = comuna)
                        )
                    }
                }
                dao.insertarTodas(listaLocalidades)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}