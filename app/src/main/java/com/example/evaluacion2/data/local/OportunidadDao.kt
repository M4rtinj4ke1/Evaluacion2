package com.example.evaluacion2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// @Dao le dice a Room que esta interfaz tiene las operaciones de la base de datos
@Dao
interface OportunidadDao {

    // READ: trae todas las oportunidades (las más nuevas primero).
    // Devuelve un Flow, así la lista se actualiza sola al guardar o modificar
    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<OportunidadEntity>>

    // READ: busca una oportunidad por su id (se usa al editar).
    // Devuelve null si no existe
    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun obtenerPorId(id: Int): OportunidadEntity?

    // CREATE: guarda una oportunidad nueva
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(oportunidad: OportunidadEntity)

    // UPDATE: modifica una oportunidad que ya existe
    @Update
    suspend fun actualizar(oportunidad: OportunidadEntity)

    // DELETE: elimina una oportunidad
    @Delete
    suspend fun eliminar(oportunidad: OportunidadEntity)
}