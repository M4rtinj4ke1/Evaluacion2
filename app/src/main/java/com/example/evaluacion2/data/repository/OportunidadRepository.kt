package com.example.evaluacion2.data.repository

import com.example.evaluacion2.data.local.OportunidadDao
import com.example.evaluacion2.data.local.OportunidadEntity
import kotlinx.coroutines.flow.Flow

// Intermediario entre el ViewModel y el DAO
class OportunidadRepository(private val dao: OportunidadDao) {

    // Lista de oportunidades que se actualiza sola
    val oportunidades: Flow<List<OportunidadEntity>> = dao.obtenerTodas()

    suspend fun obtenerPorId(id: Int): OportunidadEntity? = dao.obtenerPorId(id)

    suspend fun insertar(oportunidad: OportunidadEntity) = dao.insertar(oportunidad)

    suspend fun actualizar(oportunidad: OportunidadEntity) = dao.actualizar(oportunidad)

    suspend fun eliminar(oportunidad: OportunidadEntity) = dao.eliminar(oportunidad)
}