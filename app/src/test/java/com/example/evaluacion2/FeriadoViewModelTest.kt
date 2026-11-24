package com.example.evaluacion2

import com.example.evaluacion2.data.remote.FeriadoDto
import com.example.evaluacion2.data.repository.FeriadoRepository
import com.example.evaluacion2.ui.feriado.FeriadoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

// Pruebas del ViewModel de feriados. El repositorio se mockea y la fecha de "hoy" es fija.
@OptIn(ExperimentalCoroutinesApi::class)
class FeriadoViewModelTest {

    private val repository: FeriadoRepository = mockk()

    private val navidad = FeriadoDto("2026-12-25", "Navidad", "Civil", true, null)
    private val anioNuevo = FeriadoDto("2026-01-01", "Año Nuevo", "Civil", true, null)

    @Before
    fun setUp() {
        // viewModelScope usa Dispatchers.Main, que no existe en pruebas: lo reemplazamos
        Dispatchers.setMain(UnconfinedTestDispatcher())
        // filtrarProximos es lógica real (no llama a la red), así que delegamos al código verdadero
        every { repository.filtrarProximos(any(), any()) } answers {
            FeriadoRepository(mockk()).filtrarProximos(firstArg(), secondArg())
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar carga solo los feriados proximos`() {
        coEvery { repository.obtenerFeriados() } returns Result.success(listOf(anioNuevo, navidad))

        val vm = FeriadoViewModel(repository, hoy = { "2026-11-24" })

        val estado = vm.estado.value
        assertFalse(estado.cargando)
        assertNull(estado.error)
        assertEquals(listOf(navidad), estado.feriados)
    }

    @Test
    fun `cambiar a todo el anio muestra la lista completa`() {
        coEvery { repository.obtenerFeriados() } returns Result.success(listOf(anioNuevo, navidad))
        val vm = FeriadoViewModel(repository, hoy = { "2026-11-24" })

        vm.onSoloProximosChange(false)

        assertEquals(2, vm.estado.value.feriados.size)
        assertFalse(vm.estado.value.soloProximos)
    }

    @Test
    fun `si la API falla se muestra el error`() {
        coEvery { repository.obtenerFeriados() } returns
            Result.failure(Exception("Sin conexión con la API externa"))

        val vm = FeriadoViewModel(repository, hoy = { "2026-11-24" })

        assertEquals("Sin conexión con la API externa", vm.estado.value.error)
        assertFalse(vm.estado.value.cargando)
    }

    @Test
    fun `reintentar vuelve a llamar a la API`() {
        coEvery { repository.obtenerFeriados() } returns Result.success(emptyList())
        val vm = FeriadoViewModel(repository, hoy = { "2026-11-24" })

        vm.cargarFeriados()

        coVerify(exactly = 2) { repository.obtenerFeriados() }
    }
}
