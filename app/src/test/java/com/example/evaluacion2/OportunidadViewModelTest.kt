package com.example.evaluacion2

import android.app.Application
import com.example.evaluacion2.data.local.OportunidadEntity
import com.example.evaluacion2.data.repository.OportunidadRepository
import com.example.evaluacion2.ui.oportunidad.OportunidadViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Pruebas del ViewModel de oportunidades: validaciones del formulario y llamadas al repositorio
@OptIn(ExperimentalCoroutinesApi::class)
class OportunidadViewModelTest {

    private val app: Application = mockk(relaxed = true)
    private val repository: OportunidadRepository = mockk(relaxed = true)
    private lateinit var vm: OportunidadViewModel

    private val existente = OportunidadEntity(
        id = 4, nombre = "Feria Costanera", tipo = "Feria empresarial", descripcion = "Stand",
        fuente = "Web", prioridad = "Alta", potencial = "Alto",
        estado = "Contacto pendiente", observaciones = "Llamar"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repository.oportunidades } returns flowOf(emptyList())
        coEvery { repository.refrescarDesdeApi() } returns Result.success(Unit)
        vm = OportunidadViewModel(app, repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun llenarFormularioValido() {
        vm.onNombreChange("  Expo Pyme  ")
        vm.onTipoChange("Feria empresarial")
        vm.onDescripcionChange("Stand de beneficios")
        vm.onFuenteChange("Municipalidad")
        vm.onPrioridadChange("Media")
        vm.onPotencialChange("Medio")
        vm.onEstadoChange("Contacto pendiente")
        vm.onObservacionesChange("Sin obs")
    }

    @Test
    fun `al crearse sincroniza con la API`() {
        coVerify(exactly = 1) { repository.refrescarDesdeApi() }
    }

    @Test
    fun `refrescar vuelve a llamar a la API`() {
        vm.refrescar()
        coVerify(exactly = 2) { repository.refrescarDesdeApi() }
    }

    @Test
    fun `formulario vacio marca todos los campos obligatorios`() {
        assertFalse(vm.validarFormulario())
        val e = vm.estado.value
        assertEquals("El nombre es obligatorio", e.nombreError)
        assertEquals("Selecciona un tipo de oportunidad", e.tipoError)
        assertEquals("La descripción es obligatoria", e.descripcionError)
        assertEquals("La fuente de información es obligatoria", e.fuenteError)
        assertEquals("Selecciona una prioridad", e.prioridadError)
        assertEquals("Selecciona el potencial comercial", e.potencialError)
    }

    @Test
    fun `potencial fuera de las opciones es invalido`() {
        llenarFormularioValido()
        vm.onPotencialChange("Altísimo")

        assertFalse(vm.validarFormulario())
        assertEquals("Potencial no válido", vm.estado.value.potencialError)
    }

    @Test
    fun `escribir en un campo limpia su error`() {
        vm.validarFormulario()
        llenarFormularioValido()
        val e = vm.estado.value
        assertNull(e.nombreError); assertNull(e.tipoError); assertNull(e.descripcionError)
        assertNull(e.fuenteError); assertNull(e.prioridadError); assertNull(e.potencialError)
    }

    @Test
    fun `guardar una nueva llama a insertar con los textos recortados`() {
        llenarFormularioValido()
        vm.onUbicacionChange(-33.41, -70.60)
        vm.onFotoChange(null)

        vm.guardar()

        coVerify {
            repository.insertar(match {
                it.id == 0 && it.nombre == "Expo Pyme" && it.latitud == -33.41 && it.longitud == -70.60
            })
        }
        assertTrue(vm.estado.value.guardadoExitosamente)
        assertFalse(vm.estado.value.guardando)
    }

    @Test
    fun `guardar con formulario invalido no llama al repositorio`() {
        vm.guardar()
        coVerify(exactly = 0) { repository.insertar(any()) }
        coVerify(exactly = 0) { repository.actualizar(any()) }
    }

    @Test
    fun `cargar una existente llena el formulario y guardar la actualiza`() {
        coEvery { repository.obtenerPorId(4) } returns existente

        vm.cargarOportunidad(4)
        assertEquals("Feria Costanera", vm.estado.value.nombre)
        assertEquals(4, vm.estado.value.id)

        vm.onNombreChange("Feria Costanera 2026")
        vm.guardar()

        coVerify { repository.actualizar(match { it.id == 4 && it.nombre == "Feria Costanera 2026" }) }
    }

    @Test
    fun `cargar un id inexistente no cambia el formulario`() {
        coEvery { repository.obtenerPorId(99) } returns null
        vm.cargarOportunidad(99)
        assertEquals(0, vm.estado.value.id)
    }

    @Test
    fun `eliminar la oportunidad cargada`() {
        coEvery { repository.obtenerPorId(4) } returns existente
        vm.cargarOportunidad(4)

        vm.eliminar()

        coVerify { repository.eliminar(existente) }
        assertTrue(vm.estado.value.guardadoExitosamente)
    }

    @Test
    fun `eliminar sin oportunidad cargada no hace nada`() {
        vm.eliminar()
        coVerify(exactly = 0) { repository.eliminar(any()) }
    }

    @Test
    fun `eliminar desde la lista llama al repositorio`() {
        vm.eliminarOportunidad(existente)
        coVerify { repository.eliminar(existente) }
    }

    @Test
    fun `nueva oportunidad y guardado consumido reinician el estado`() {
        llenarFormularioValido()
        vm.guardar()
        vm.onGuardadoConsumido()
        assertFalse(vm.estado.value.guardadoExitosamente)

        vm.nuevaOportunidad()
        assertEquals("", vm.estado.value.nombre)
    }
}
