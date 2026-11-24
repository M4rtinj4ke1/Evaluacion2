package com.example.evaluacion2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalidadDao {

    @Query("SELECT DISTINCT region FROM localidades ORDER BY region ASC")
    fun obtenerRegiones(): Flow<List<String>>

    @Query("SELECT comuna FROM localidades WHERE region = :region ORDER BY comuna ASC")
    fun obtenerComunasPorRegion(region: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(localidades: List<LocalidadEntity>)

    // Agrega esta función aquí:
    @Query("SELECT COUNT(*) FROM localidades")
    suspend fun contarLocalidades(): Int
}