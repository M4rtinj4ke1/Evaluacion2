package com.example.evaluacion2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Operaciones de la base de datos para las organizaciones
@Dao
interface OrganizacionDao {

    // READ: obtiene todas las organizaciones
    @Query("SELECT * FROM organizaciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<OrganizacionEntity>>

    // READ: busca una organización por su id
    @Query("SELECT * FROM organizaciones WHERE id = :id")
    suspend fun obtenerPorId(id: Int): OrganizacionEntity?

    // CREATE: registra una organización
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(organizacion: OrganizacionEntity)

    // UPDATE: modifica una organización
    @Update
    suspend fun actualizar(organizacion: OrganizacionEntity)

    // DELETE: elimina una organización
    @Delete
    suspend fun eliminar(organizacion: OrganizacionEntity)

    @Query("DELETE FROM contactos WHERE organizacionId = :organizacionId")
    suspend fun eliminarPorOrganizacion(organizacionId: Int)
}
