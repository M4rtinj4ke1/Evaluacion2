package com.example.evaluacion2

import android.app.Application
import com.example.evaluacion2.data.local.ContactoEntity
import com.example.evaluacion2.data.repository.ContactoRepository
import com.example.evaluacion2.ui.contacto.ContactoViewModel
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

// Pruebas del ViewModel de contactos: formato de correo/teléfono y CRUD
@OptIn(ExperimentalCoroutinesApi::class)
class ContactoViewModelTest {

    private val app: Application = mockk(relaxed = true)
    private val repository: ContactoRepository = mockk(relaxed = true)
    private lateinit var vm: ContactoViewModel

    private val existente = ContactoEntity(
        id = 8, nombre = "Ana Pérez", organizacionId = 2, cargo = "Jefa RRHH", area = "Personas",
        correo = "ana@empresa.cl", telefono = "912345678", medioContacto = "Correo",
        estado = "Pendiente", observaciones = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repository.contactos } returns flowOf(emptyList())
        vm = ContactoViewModel(app, repository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun llenarValido() {
        vm.nuevoContacto(organizacionId = 2)
        vm.onNombreChange("Ana Pérez")
        vm.onCargoChange("Jefa RRHH")
        vm.onAreaChange("Personas")
        vm.onCorreoChange("ana@empresa.cl")
        vm.onTelefonoChange("912345678")
        vm.onMedioContactoChange("Correo")
        vm.onEstadoChange("Pendiente")
        vm.onObservacionesChange("")
    }

    @Test
    fun `contacto valido pasa la validacion`() {
        llenarValido()
        assertTrue(vm.validarFormulario())
        assertNull(vm.estado.value.correoError)
        assertNull(vm.estado.value.telefonoError)
    }

    @Test
    fun `formulario vacio marca los obligatorios`() {
        assertFalse(vm.validarFormulario())
        val e = vm.estado.value
        assertEquals("El nombre es obligatorio", e.nombreError)
        assertEquals("El cargo es obligatorio", e.cargoError)
        assertEquals("Selecciona un área", e.areaError)
        assertEquals("El correo es obligatorio", e.correoError)
        assertEquals("El teléfono es obligatorio", e.telefonoError)
    }

    @Test
    fun `correo sin formato valido es rechazado`() {
        llenarValido()
        vm.onCorreoChange("ana.empresa.cl")
        assertFalse(vm.validarFormulario())
        assertEquals("El correo debe tener formato válido (ej: nombre@correo.com)", vm.estado.value.correoError)
    }

    @Test
    fun `telefono con letras es rechazado`() {
        llenarValido()
        vm.onTelefonoChange("9123abcd")
        assertFalse(vm.validarFormulario())
        assertEquals("El teléfono solo debe contener números", vm.estado.value.telefonoError)
    }

    @Test
    fun `telefono corto es rechazado`() {
        llenarValido()
        vm.onTelefonoChange("1234")
        assertFalse(vm.validarFormulario())
        assertEquals("El teléfono debe tener al menos 8 dígitos", vm.estado.value.telefonoError)
    }

    @Test
    fun `guardar nuevo llama a insertar con la organizacion correcta`() {
        llenarValido()
        vm.guardar()

        coVerify { repository.insertar(match { it.id == 0 && it.organizacionId == 2 }) }
        assertTrue(vm.estado.value.guardadoExitosamente)
    }

    @Test
    fun `guardar invalido no llama al repositorio`() {
        vm.guardar()
        coVerify(exactly = 0) { repository.insertar(any()) }
    }

    @Test
    fun `cargar existente y guardar llama a actualizar`() {
        coEvery { repository.obtenerPorId(8) } returns existente

        vm.cargarContacto(8)
        assertEquals("Ana Pérez", vm.estado.value.nombre)

        vm.guardar()
        coVerify { repository.actualizar(match { it.id == 8 }) }
    }

    @Test
    fun `cargar id inexistente no cambia el formulario`() {
        coEvery { repository.obtenerPorId(77) } returns null
        vm.cargarContacto(77)
        assertEquals(0, vm.estado.value.id)
    }

    @Test
    fun `cargar contactos de una organizacion`() {
        every { repository.obtenerPorOrganizacion(2) } returns flowOf(listOf(existente))
        vm.cargarContactos(2)
        assertEquals(listOf(existente), vm.contactosOrganizacion.value)
    }

    @Test
    fun `eliminar contacto cargado y sin cargar`() {
        vm.eliminar()
        coVerify(exactly = 0) { repository.eliminar(any()) }

        coEvery { repository.obtenerPorId(8) } returns existente
        vm.cargarContacto(8)
        vm.eliminar()
        coVerify { repository.eliminar(existente) }

        vm.onGuardadoConsumido()
        assertFalse(vm.estado.value.guardadoExitosamente)
    }
}
