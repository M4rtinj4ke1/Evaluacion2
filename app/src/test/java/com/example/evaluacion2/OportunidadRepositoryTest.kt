package com.example.evaluacion2

import com.example.evaluacion2.data.local.OportunidadDao
import com.example.evaluacion2.data.local.OportunidadEntity
import com.example.evaluacion2.data.remote.OportunidadApiService
import com.example.evaluacion2.data.repository.OportunidadRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Pruebas del repositorio de oportunidades (CRUD contra el microservicio Spring Boot).
 * Se mockean el ApiService (Retrofit) y el DAO (Room): se prueba solo la lógica del repositorio.
 */
class OportunidadRepositoryTest {

    private val dao: OportunidadDao = mockk(relaxed = true)
    private val api: OportunidadApiService = mockk()
    private lateinit var repository: OportunidadRepository

    private fun oportunidad(id: Int = 0, nombre: String = "Feria Costanera") = OportunidadEntity(
        id = id, nombre = nombre, tipo = "Evento", descripcion = "Activación",
        fuente = "Web", prioridad = "Alta", potencial = "Alto",
        estado = "Evento identificado", observaciones = ""
    )

    private fun <T> errorHttp(codigo: Int): Response<T> = Response.error(codigo, "".toResponseBody(null))

    @Before
    fun setUp() {
        every { dao.obtenerTodas() } returns flowOf(emptyList())
        repository = OportunidadRepository(dao, api)
    }

    // ---------- READ ----------

    @Test
    fun `refrescar reemplaza los datos locales por los de la API`() = runTest {
        val remotas = listOf(oportunidad(1), oportunidad(2, "Expo"))
        coEvery { api.obtenerTodas() } returns Response.success(remotas)

        val resultado = repository.refrescarDesdeApi()

        assertTrue(resultado.isSuccess)
        coVerify(exactly = 1) { dao.limpiarTodas() }
        coVerify(exactly = 2) { dao.insertar(any()) }
    }

    @Test
    fun `refrescar con error HTTP no borra los datos locales`() = runTest {
        coEvery { api.obtenerTodas() } returns errorHttp(500)

        val resultado = repository.refrescarDesdeApi()

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { dao.limpiarTodas() }
    }

    @Test
    fun `refrescar sin conexion devuelve failure`() = runTest {
        coEvery { api.obtenerTodas() } throws IOException("sin red")

        assertTrue(repository.refrescarDesdeApi().isFailure)
        coVerify(exactly = 0) { dao.limpiarTodas() }
    }

    @Test
    fun `obtenerPorId usa la API y guarda en Room`() = runTest {
        val remota = oportunidad(5)
        coEvery { api.obtenerPorId(5) } returns Response.success(remota)

        assertEquals(remota, repository.obtenerPorId(5))
        coVerify { dao.insertar(remota) }
    }

    @Test
    fun `obtenerPorId sin conexion usa el dato local`() = runTest {
        val local = oportunidad(5, "Local")
        coEvery { api.obtenerPorId(5) } throws IOException()
        coEvery { dao.obtenerPorId(5) } returns local

        assertEquals(local, repository.obtenerPorId(5))
    }

    // ---------- CREATE ----------

    @Test
    fun `insertar guarda en Room la oportunidad con el id que asigno la API`() = runTest {
        val nueva = oportunidad(0)
        val guardada = oportunidad(10)
        coEvery { api.insertar(nueva) } returns Response.success(guardada)

        val resultado = repository.insertar(nueva)

        assertEquals(10, resultado.getOrThrow().id)
        coVerify { dao.insertar(guardada) }
    }

    @Test
    fun `insertar sin conexion guarda localmente`() = runTest {
        val nueva = oportunidad(0)
        coEvery { api.insertar(any()) } throws IOException()

        assertTrue(repository.insertar(nueva).isSuccess)
        coVerify { dao.insertar(nueva) }
    }

    @Test
    fun `insertar con error HTTP guarda localmente`() = runTest {
        val nueva = oportunidad(0)
        coEvery { api.insertar(any()) } returns errorHttp(400)

        repository.insertar(nueva)

        coVerify { dao.insertar(nueva) }
    }

    // ---------- UPDATE ----------

    @Test
    fun `actualizar usa la respuesta de la API`() = runTest {
        val editada = oportunidad(3, "Editada")
        coEvery { api.actualizar(3, editada) } returns Response.success(editada)

        assertTrue(repository.actualizar(editada).isSuccess)
        coVerify(exactly = 1) { dao.actualizar(editada) }
    }

    @Test
    fun `actualizar con error HTTP actualiza localmente`() = runTest {
        val editada = oportunidad(3, "Editada")
        coEvery { api.actualizar(any(), any()) } returns errorHttp(404)

        repository.actualizar(editada)

        coVerify(exactly = 1) { dao.actualizar(editada) }
    }

    @Test
    fun `actualizar sin conexion actualiza localmente`() = runTest {
        val editada = oportunidad(3, "Editada")
        coEvery { api.actualizar(any(), any()) } throws IOException()

        repository.actualizar(editada)

        coVerify(exactly = 1) { dao.actualizar(editada) }
    }

    // ---------- DELETE ----------

    @Test
    fun `eliminar borra en la API y en Room`() = runTest {
        val o = oportunidad(7)
        coEvery { api.eliminar(7) } returns Response.success(Unit)
        coEvery { dao.eliminar(o) } just runs

        assertTrue(repository.eliminar(o).isSuccess)
        coVerify { api.eliminar(7) }
        coVerify { dao.eliminar(o) }
    }

    @Test
    fun `eliminar sin conexion igual borra localmente`() = runTest {
        val o = oportunidad(7)
        coEvery { api.eliminar(7) } throws IOException()

        repository.eliminar(o)

        coVerify { dao.eliminar(o) }
    }

    @Test
    fun `eliminar con error HTTP igual borra localmente`() = runTest {
        val o = oportunidad(7)
        coEvery { api.eliminar(7) } returns errorHttp(404)

        repository.eliminar(o)

        coVerify { dao.eliminar(o) }
    }
}
