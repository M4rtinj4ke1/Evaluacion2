
package com.example.evaluacion2.data.repository

import com.example.evaluacion2.data.local.ContactoDao
import com.example.evaluacion2.data.local.ContactoEntity
import kotlinx.coroutines.flow.Flow

// Intermediario entre el ViewModel y el DAO
class ContactoRepository(private val dao: ContactoDao) {

    // Lista de contactos
    val contactos: Flow<List<ContactoEntity>> = dao.obtenerTodos()

    // Contactos pertenecientes a una organización
    fun obtenerPorOrganizacion(organizacionId: Int): Flow<List<ContactoEntity>> =
        dao.obtenerPorOrganizacion(organizacionId)

    suspend fun obtenerPorId(id: Int): ContactoEntity? =
        dao.obtenerPorId(id)

    suspend fun insertar(contacto: ContactoEntity) =
        dao.insertar(contacto)

    suspend fun actualizar(contacto: ContactoEntity) =
        dao.actualizar(contacto)

    suspend fun eliminar(contacto: ContactoEntity) =
        dao.eliminar(contacto)
}
