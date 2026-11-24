package com.example.evaluacion2

import com.example.evaluacion2.data.remote.FeriadoApiService
import com.example.evaluacion2.data.remote.FeriadoDto
import com.example.evaluacion2.data.remote.FeriadosResponse
import com.example.evaluacion2.data.repository.FeriadoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

// Pruebas del repositorio de la API externa. La API se reemplaza por un mock: no se usa internet.
class FeriadoRepositoryTest {

    private val api: FeriadoApiService = mockk()
    private val repository = FeriadoRepository(api)

    private val navidad = FeriadoDto("2026-12-25", "Navidad", "Civil", true, "Civil e Irrenunciable")
    private val anioNuevo = FeriadoDto("2026-01-01", "Año Nuevo", "Civil", true, null)
    private val inmaculada = FeriadoDto("2026-12-08", "Inmaculada Concepción", "Civil", false, "Civil")

    @Test
    fun `respuesta exitosa devuelve la lista de feriados`() = runTest {
        coEvery { api.obtenerFeriados() } returns
            Response.success(FeriadosResponse("success", listOf(navidad, anioNuevo)))

        val resultado = repository.obtenerFeriados()

        assertTrue(resultado.isSuccess)
        assertEquals(2, resultado.getOrThrow().size)
    }

    @Test
    fun `error HTTP devuelve failure con el codigo`() = runTest {
        coEvery { api.obtenerFeriados() } returns Response.error(500, "".toResponseBody(null))

        val resultado = repository.obtenerFeriados()

        assertTrue(resultado.isFailure)
        assertEquals("Error en API externa: 500", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `status distinto de success se trata como error`() = runTest {
        coEvery { api.obtenerFeriados() } returns
            Response.success(FeriadosResponse("error", emptyList()))

        assertTrue(repository.obtenerFeriados().isFailure)
    }

    @Test
    fun `sin internet devuelve failure con mensaje claro`() = runTest {
        coEvery { api.obtenerFeriados() } throws IOException("timeout")

        val resultado = repository.obtenerFeriados()

        assertTrue(resultado.isFailure)
        assertEquals("Sin conexión con la API externa", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `filtrarProximos deja solo fechas desde hoy y ordenadas`() {
        val lista = listOf(navidad, anioNuevo, inmaculada)

        val proximos = repository.filtrarProximos(lista, hoy = "2026-11-24")

        assertEquals(listOf("2026-12-08", "2026-12-25"), proximos.map { it.fecha })
    }

    @Test
    fun `filtrarProximos incluye el feriado de hoy`() {
        val proximos = repository.filtrarProximos(listOf(navidad), hoy = "2026-12-25")
        assertEquals(1, proximos.size)
    }
}
