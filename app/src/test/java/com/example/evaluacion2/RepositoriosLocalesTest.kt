package com.example.evaluacion2

import com.example.evaluacion2.data.local.ContactoDao
import com.example.evaluacion2.data.local.ContactoEntity
import com.example.evaluacion2.data.local.OrganizacionDao
import com.example.evaluacion2.data.local.OrganizacionEntity
import com.example.evaluacion2.data.repository.ContactoRepository
import com.example.evaluacion2.data.repository.OrganizacionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

// Pruebas de los repositorios que trabajan solo con Room (organizaciones y contactos)
class RepositoriosLocalesTest {

    private val org = OrganizacionEntity(3, "Duoc UC", "Institución", "Educación", "Santiago", "Metropolitana", "", "")
    private val contacto = ContactoEntity(1, "Ana", 3, "Jefa", "RRHH", "a@b.cl", "12345678", "Correo", "Pendiente", "")

    @Test
    fun `eliminar organizacion borra primero sus contactos`() = runTest {
        val dao: OrganizacionDao = mockk(relaxed = true)
        every { dao.obtenerTodas() } returns flowOf(emptyList())
        val repo = OrganizacionRepository(dao)

        repo.eliminar(org)

        // Orden importante: primero los contactos asociados, después la organización
        coVerifyOrder {
            dao.eliminarPorOrganizacion(3)
            dao.eliminar(org)
        }
    }

    @Test
    fun `repositorio de organizaciones delega en el DAO`() = runTest {
        val dao: OrganizacionDao = mockk(relaxed = true)
        every { dao.obtenerTodas() } returns flowOf(listOf(org))
        coEvery { dao.obtenerPorId(3) } returns org
        val repo = OrganizacionRepository(dao)

        assertEquals(org, repo.obtenerPorId(3))
        repo.insertar(org)
        repo.actualizar(org)

        coVerify { dao.insertar(org) }
        coVerify { dao.actualizar(org) }
        verify { dao.obtenerTodas() }
    }

    @Test
    fun `repositorio de contactos delega en el DAO`() = runTest {
        val dao: ContactoDao = mockk(relaxed = true)
        every { dao.obtenerTodos() } returns flowOf(listOf(contacto))
        every { dao.obtenerPorOrganizacion(3) } returns flowOf(listOf(contacto))
        coEvery { dao.obtenerPorId(1) } returns contacto
        val repo = ContactoRepository(dao)

        assertEquals(contacto, repo.obtenerPorId(1))
        repo.obtenerPorOrganizacion(3)
        repo.insertar(contacto)
        repo.actualizar(contacto)
        repo.eliminar(contacto)

        verify { dao.obtenerPorOrganizacion(3) }
        coVerify { dao.insertar(contacto) }
        coVerify { dao.actualizar(contacto) }
        coVerify { dao.eliminar(contacto) }
    }
}
