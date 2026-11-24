package com.example.evaluacion2.ui.contacto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evaluacion2.data.local.AppDatabase
import com.example.evaluacion2.data.local.ContactoEntity
import com.example.evaluacion2.data.repository.ContactoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// @JvmOverloads: Android crea el ViewModel solo con Application (usa Room real);
// en las pruebas unitarias se le pasa un repositorio simulado con MockK.
class ContactoViewModel @JvmOverloads constructor(
    application: Application,
    // Conexión con la base de datos a través del Repository
    private val repository: ContactoRepository = ContactoRepository(
        AppDatabase.getDatabase(application).contactoDao()
    )
) : AndroidViewModel(application) {

    // Lista general de contactos
    val contactos: StateFlow<List<ContactoEntity>> =
        repository.contactos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Contactos de una organización seleccionada
    private val _contactosOrganizacion =
        MutableStateFlow<List<ContactoEntity>>(emptyList())

    val contactosOrganizacion: StateFlow<List<ContactoEntity>> =
        _contactosOrganizacion.asStateFlow()

    // Estado del formulario
    private val _estado = MutableStateFlow(ContactoUiState())
    val estado: StateFlow<ContactoUiState> = _estado.asStateFlow()

    // Carga los contactos de una organización
    fun cargarContactos(organizacionId: Int) {
        viewModelScope.launch {
            repository.obtenerPorOrganizacion(organizacionId).collect {
                _contactosOrganizacion.value = it
            }
        }
    }

    // Prepara un formulario nuevo
    fun nuevoContacto(organizacionId: Int) {
        _estado.value = ContactoUiState(
            organizacionId = organizacionId
        )
    }

    // Carga un contacto existente para editar
    fun cargarContacto(id: Int) {
        viewModelScope.launch {
            val c = repository.obtenerPorId(id) ?: return@launch

            _estado.value = ContactoUiState(
                id = c.id,
                nombre = c.nombre,
                organizacionId = c.organizacionId,
                cargo = c.cargo,
                area = c.area,
                correo = c.correo,
                telefono = c.telefono,
                medioContacto = c.medioContacto,
                estado = c.estado,
                observaciones = c.observaciones
            )
        }
    }

    // Cambios en los campos del formulario
    fun onNombreChange(valor: String) =
        _estado.update { it.copy(nombre = valor, nombreError = null) }

    fun onCargoChange(valor: String) =
        _estado.update { it.copy(cargo = valor, cargoError = null) }

    fun onAreaChange(valor: String) =
        _estado.update { it.copy(area = valor, areaError = null) }

    fun onCorreoChange(valor: String) =
        _estado.update { it.copy(correo = valor, correoError = null) }

    fun onTelefonoChange(valor: String) =
        _estado.update { it.copy(telefono = valor, telefonoError = null) }

    fun onMedioContactoChange(valor: String) =
        _estado.update { it.copy(medioContacto = valor) }

    fun onEstadoChange(valor: String) =
        _estado.update { it.copy(estado = valor) }

    fun onObservacionesChange(valor: String) =
        _estado.update { it.copy(observaciones = valor) }

    // Valida los campos obligatorios
    // Valida los campos obligatorios y sus formatos
    fun validarFormulario(): Boolean {
        val e = _estado.value

        // Expresión regular que exige: caracteres + '@' + caracteres + '.' + dominio (ej: nombre@correo.com)
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        val correoError = when {
            e.correo.isBlank() -> "El correo es obligatorio"
            !e.correo.trim().matches(emailRegex) -> "El correo debe tener formato válido (ej: nombre@correo.com)"
            else -> null
        }

        val telefonoError = when {
            e.telefono.isBlank() -> "El teléfono es obligatorio"
            !e.telefono.all { it.isDigit() } -> "El teléfono solo debe contener números"
            e.telefono.trim().length < 8 -> "El teléfono debe tener al menos 8 dígitos"
            else -> null
        }

        val validado = e.copy(
            nombreError = if (e.nombre.isBlank()) "El nombre es obligatorio" else null,
            cargoError = if (e.cargo.isBlank()) "El cargo es obligatorio" else null,
            areaError = if (e.area.isBlank()) "Selecciona un área" else null,
            correoError = correoError,
            telefonoError = telefonoError
        )

        _estado.value = validado

        return listOf(
            validado.nombreError,
            validado.cargoError,
            validado.areaError,
            validado.correoError,
            validado.telefonoError
        ).all { it == null }
    }

    // Guarda un contacto nuevo o actualiza uno existente
    fun guardar() {
        if (!validarFormulario()) return

        val e = _estado.value

        val contacto = ContactoEntity(
            id = e.id,
            nombre = e.nombre.trim(),
            organizacionId = e.organizacionId,
            cargo = e.cargo.trim(),
            area = e.area,
            correo = e.correo.trim(),
            telefono = e.telefono.trim(),
            medioContacto = e.medioContacto,
            estado = e.estado,
            observaciones = e.observaciones.trim()
        )

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }

            if (e.id == 0) {
                repository.insertar(contacto)
            } else {
                repository.actualizar(contacto)
            }

            _estado.update {
                it.copy(
                    guardando = false,
                    guardadoExitosamente = true
                )
            }
        }
    }

    // Elimina un contacto
    fun eliminar() {
        val id = _estado.value.id
        if (id == 0) return

        viewModelScope.launch {
            repository.obtenerPorId(id)?.let {
                repository.eliminar(it)
            }

            _estado.update {
                it.copy(guardadoExitosamente = true)
            }
        }
    }

    // Limpia el mensaje de guardado
    fun onGuardadoConsumido() {
        _estado.update {
            it.copy(guardadoExitosamente = false)
        }
    }
}
