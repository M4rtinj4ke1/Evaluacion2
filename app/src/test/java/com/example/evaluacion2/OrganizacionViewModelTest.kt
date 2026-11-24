package com.example.evaluacion2

import android.app.Application
import com.example.evaluacion2.data.local.LocalidadDao
import com.example.evaluacion2.data.local.OrganizacionEntity
import com.example.evaluacion2.data.repository.OrganizacionRepository
import com.example.evaluacion2.ui.organizacion.OrganizacionViewModel
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

// Pruebas del ViewModel de organizaciones: validación, región/comuna y CRUD en Room
@OptIn(ExperimentalCoroutinesApi::class)
class OrganizacionViewModelTest {

    private val app: Application = mockk(relaxed = true)
    private val repository: OrganizacionRepository = mockk(relaxed = true)
    private val localidadDao: LocalidadDao = mockk(relaxed = true)
    private lateinit var vm: OrganizacionViewModel

    private val existente = OrganizacionEntity(
        id = 2, nombre = "Mall Costanera", tipo = "Empresa", rubro = "Retail",
        comuna = "Providencia", region = "Metropolitana", sitioWeb = "", observaciones = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repository.organizaciones } returns flowOf(emptyList())
        every { localidadDao.obtenerRegiones() } returns flowOf(listOf("Metropolitana", "Valparaíso"))
        every { localidadDao.obtenerComunasPorRegion("Metropolitana") } returns
            flowOf(listOf("Providencia", "Santiago"))
        vm = OrganizacionViewModel(app, repository, localidadDao)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun llenarValido() {
        vm.onNombreChange(" Duoc UC ")
        vm.onTipoChange("Institución")
        vm.onRubroChange("Educación")
        vm.onRegionChange("Metropolitana")
        vm.onComunaChange("Santiago")
        vm.onSitioWebChange("duoc.cl")
        vm.onObservacionesChange("")
    }

    @Test
    fun `formulario vacio marca los obligatorios`() {
        assertFalse(vm.validarFormulario())
        val e = vm.estado.value
        assertEquals("El nombre es obligatorio", e.nombreError)
        assertEquals("Selecciona el tipo de organización", e.tipoError)
        assertEquals("El rubro es obligatorio", e.rubroError)
        assertEquals("Selecciona una región", e.regionError)
        assertEquals("Selecciona una comuna", e.comunaError)
    }

    @Test
    fun `cambiar region carga sus comunas y borra la comuna anterior`() {
        vm.onComunaChange("Viña del Mar")
        vm.onRegionChange("Metropolitana")

        assertEquals("", vm.estado.value.comuna)
        assertEquals(listOf("Providencia", "Santiago"), vm.comunas.value)
    }

    @Test
    fun `guardar nueva llama a insertar`() {
        llenarValido()
        vm.guardar()

        coVerify { repository.insertar(match { it.id == 0 && it.nombre == "Duoc UC" }) }
        assertTrue(vm.estado.value.guardadoExitosamente)
        assertNull(vm.estado.value.nombreError)
    }

    @Test
    fun `guardar invalido no llama al repositorio`() {
        vm.guardar()
        coVerify(exactly = 0) { repository.insertar(any()) }
    }

    @Test
    fun `cargar existente llena formulario y comunas, y guardar actualiza`() {
        coEvery { repository.obtenerPorId(2) } returns existente

        vm.cargarOrganizacion(2)

        assertEquals("Mall Costanera", vm.estado.value.nombre)
        assertEquals(listOf("Providencia", "Santiago"), vm.comunas.value)

        vm.guardar()
        coVerify { repository.actualizar(match { it.id == 2 }) }
    }

    @Test
    fun `cargar id inexistente no cambia nada`() {
        coEvery { repository.obtenerPorId(50) } returns null
        vm.cargarOrganizacion(50)
        assertEquals(0, vm.estado.value.id)
    }

    @Test
    fun `eliminar la organizacion cargada`() {
        coEvery { repository.obtenerPorId(2) } returns existente
        vm.cargarOrganizacion(2)

        vm.eliminar()

        coVerify { repository.eliminar(existente) }
        assertTrue(vm.estado.value.guardadoExitosamente)
    }

    @Test
    fun `eliminar sin organizacion cargada no hace nada`() {
        vm.eliminar()
        coVerify(exactly = 0) { repository.eliminar(any()) }
    }

    @Test
    fun `nueva organizacion limpia formulario y comunas`() {
        llenarValido()
        vm.guardar()
        vm.onGuardadoConsumido()
        assertFalse(vm.estado.value.guardadoExitosamente)

        vm.nuevaOrganizacion()
        assertEquals("", vm.estado.value.nombre)
        assertTrue(vm.comunas.value.isEmpty())
    }
}
