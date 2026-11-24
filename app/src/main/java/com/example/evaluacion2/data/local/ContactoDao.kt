package com.example.evaluacion2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Operaciones de la base de datos para los contactos
@Dao
interface ContactoDao {

    // READ: obtiene todos los contactos
    @Query("SELECT * FROM contactos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ContactoEntity>>

    // READ: obtiene los contactos de una organización
    @Query("SELECT * FROM contactos WHERE organizacionId = :organizacionId ORDER BY id DESC")
    fun obtenerPorOrganizacion(organizacionId: Int): Flow<List<ContactoEntity>>

    // READ: busca un contacto por su id
    @Query("SELECT * FROM contactos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ContactoEntity?

    // CREATE: registra un contacto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(contacto: ContactoEntity)

    // UPDATE: modifica un contacto
    @Update
    suspend fun actualizar(contacto: ContactoEntity)

    // DELETE: elimina un contacto
    @Delete
    suspend fun eliminar(contacto: ContactoEntity)

    // DELETE: elimina los contactos de una organización
    @Query("DELETE FROM contactos WHERE organizacionId = :organizacionId")
    suspend fun eliminarPorOrganizacion(organizacionId: Int)
}
