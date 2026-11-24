package com.example.evaluacion2.data.repository

import com.example.evaluacion2.data.local.OrganizacionDao
import com.example.evaluacion2.data.local.OrganizacionEntity
import kotlinx.coroutines.flow.Flow

// Intermediario entre el ViewModel y el DAO
class OrganizacionRepository(private val dao: OrganizacionDao) {

    // Lista de organizaciones
    val organizaciones: Flow<List<OrganizacionEntity>> = dao.obtenerTodas()

    suspend fun obtenerPorId(id: Int): OrganizacionEntity? =
        dao.obtenerPorId(id)

    suspend fun insertar(organizacion: OrganizacionEntity) =
        dao.insertar(organizacion)

    suspend fun actualizar(organizacion: OrganizacionEntity) =
        dao.actualizar(organizacion)

    suspend fun eliminar(organizacion: OrganizacionEntity) {
        dao.eliminarPorOrganizacion(organizacion.id)
        dao.eliminar(organizacion)
    }
}
