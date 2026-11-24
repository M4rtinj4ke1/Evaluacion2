package com.example.evaluacion2.ui.organizacion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evaluacion2.data.local.AppDatabase
import com.example.evaluacion2.data.local.LocalidadDao
import com.example.evaluacion2.data.local.OrganizacionEntity
import com.example.evaluacion2.data.repository.OrganizacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @JvmOverloads: Android crea el ViewModel solo con Application (usa Room real);
// en las pruebas unitarias se le pasan el repositorio y el DAO simulados con MockK.
class OrganizacionViewModel @JvmOverloads constructor(
    application: Application,
    // Conexión con la base de datos a través del Repository
    private val repository: OrganizacionRepository =
        OrganizacionRepository(AppDatabase.getDatabase(application).organizacionDao()),
    // DAO de localidades para consultar regiones y comunas
    private val localidadDao: LocalidadDao = AppDatabase.getDatabase(application).localidadDao()
) : AndroidViewModel(application) {

    // Lista de organizaciones
    val organizaciones: StateFlow<List<OrganizacionEntity>> =
        repository.organizaciones.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Flujo con todas las regiones cargadas desde la BD Room
    val regiones: StateFlow<List<String>> = localidadDao.obtenerRegiones()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Flujo con las comunas que dependen de la región seleccionada
    private val _comunas = MutableStateFlow<List<String>>(emptyList())
    val comunas: StateFlow<List<String>> = _comunas.asStateFlow()

    // Estado del formulario
    private val _estado = MutableStateFlow(OrganizacionUiState())
    val estado: StateFlow<OrganizacionUiState> = _estado.asStateFlow()

    // Prepara un formulario nuevo
    fun nuevaOrganizacion() {
        _estado.value = OrganizacionUiState()
        _comunas.value = emptyList()
    }

    // Carga una organización existente para editar
    fun cargarOrganizacion(id: Int) {
        viewModelScope.launch {
            val o = repository.obtenerPorId(id) ?: return@launch

            _estado.value = OrganizacionUiState(
                id = o.id,
                nombre = o.nombre,
                tipo = o.tipo,
                rubro = o.rubro,
                comuna = o.comuna,
                region = o.region,
                sitioWeb = o.sitioWeb,
                observaciones = o.observaciones
            )

            // Si ya tiene una región guardada, carga de inmediato sus comunas asociadas
            if (o.region.isNotBlank()) {
                localidadDao.obtenerComunasPorRegion(o.region).collect { lista ->
                    _comunas.value = lista
                }
            }
        }
    }

    // Cambios en los campos del formulario
    fun onNombreChange(valor: String) =
        _estado.update { it.copy(nombre = valor, nombreError = null) }

    fun onTipoChange(valor: String) =
        _estado.update { it.copy(tipo = valor, tipoError = null) }

    fun onRubroChange(valor: String) =
        _estado.update { it.copy(rubro = valor, rubroError = null) }

    // Al seleccionar o cambiar de región:
    // 1. Guarda la nueva región y limpia la comuna anterior
    // 2. Trae de la BD las comunas que corresponden a esta región
    fun onRegionChange(valor: String) {
        _estado.update {
            it.copy(
                region = valor,
                regionError = null,
                comuna = "",
                comunaError = null
            )
        }

        viewModelScope.launch {
            localidadDao.obtenerComunasPorRegion(valor).collect { lista ->
                _comunas.value = lista
            }
        }
    }

    fun onComunaChange(valor: String) =
        _estado.update { it.copy(comuna = valor, comunaError = null) }

    fun onSitioWebChange(valor: String) =
        _estado.update { it.copy(sitioWeb = valor) }

    fun onObservacionesChange(valor: String) =
        _estado.update { it.copy(observaciones = valor) }

    // Valida los campos obligatorios
    fun validarFormulario(): Boolean {
        val e = _estado.value

        val validado = e.copy(
            nombreError = if (e.nombre.isBlank())
                "El nombre es obligatorio" else null,

            tipoError = if (e.tipo.isBlank())
                "Selecciona el tipo de organización" else null,

            rubroError = if (e.rubro.isBlank())
                "El rubro es obligatorio" else null,

            regionError = if (e.region.isBlank())
                "Selecciona una región" else null,

            comunaError = if (e.comuna.isBlank())
                "Selecciona una comuna" else null
        )

        _estado.value = validado

        return listOf(
            validado.nombreError,
            validado.tipoError,
            validado.rubroError,
            validado.regionError,
            validado.comunaError
        ).all { it == null }
    }

    // Guarda una organización nueva o actualiza una existente
    fun guardar() {
        if (!validarFormulario()) return

        val e = _estado.value

        val organizacion = OrganizacionEntity(
            id = e.id,
            nombre = e.nombre.trim(),
            tipo = e.tipo,
            rubro = e.rubro.trim(),
            comuna = e.comuna.trim(),
            region = e.region.trim(),
            sitioWeb = e.sitioWeb.trim(),
            observaciones = e.observaciones.trim()
        )

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }

            if (e.id == 0) {
                repository.insertar(organizacion)
            } else {
                repository.actualizar(organizacion)
            }

            _estado.update {
                it.copy(
                    guardando = false,
                    guardadoExitosamente = true
                )
            }
        }
    }

    // Elimina una organización
    fun eliminar() {
        val id = _estado.value.id
        if (id == 0) return

        viewModelScope.launch {
            repository.obtenerPorId(id)?.let {
                repository.eliminar(it)
            }

            _estado.update { it.copy(guardadoExitosamente = true) }
        }
    }

    // Limpia el mensaje de guardado
    fun onGuardadoConsumido() {
        _estado.update {
            it.copy(guardadoExitosamente = false)
        }
    }
}