package com.example.evaluacion2.ui.oportunidad

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evaluacion2.data.local.AppDatabase
import com.example.evaluacion2.data.local.OportunidadEntity
import com.example.evaluacion2.data.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadViewModel(application: Application) : AndroidViewModel(application) {

    // Conexión con la base de datos a través del Repository
    private val repository = OportunidadRepository(
        AppDatabase.getDatabase(application).oportunidadDao()
    )

    // Lista de oportunidades para la pantalla de la lista (se actualiza sola)
    val oportunidades: StateFlow<List<OportunidadEntity>> = repository.oportunidades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario: la pantalla lo observa
    private val _estado = MutableStateFlow(OportunidadUiState())
    val estado: StateFlow<OportunidadUiState> = _estado.asStateFlow()

    // Deja el formulario vacío (para crear una oportunidad nueva)
    fun nuevaOportunidad() {
        _estado.value = OportunidadUiState()
    }

    // Carga una oportunidad existente en el formulario (para editarla)
    fun cargarOportunidad(id: Int) {
        viewModelScope.launch {
            val o = repository.obtenerPorId(id) ?: return@launch
            _estado.value = OportunidadUiState(
                id = o.id,
                nombre = o.nombre,
                tipo = o.tipo,
                descripcion = o.descripcion,
                fuente = o.fuente,
                prioridad = o.prioridad,
                potencial = o.potencial,
                estado = o.estado,
                observaciones = o.observaciones
            )
        }
    }

    // Cada vez que el usuario escribe, se actualiza el campo y se borra su error
    fun onNombreChange(valor: String) = _estado.update { it.copy(nombre = valor, nombreError = null) }
    fun onTipoChange(valor: String) = _estado.update { it.copy(tipo = valor, tipoError = null) }
    fun onDescripcionChange(valor: String) = _estado.update { it.copy(descripcion = valor, descripcionError = null) }
    fun onFuenteChange(valor: String) = _estado.update { it.copy(fuente = valor, fuenteError = null) }
    fun onPrioridadChange(valor: String) = _estado.update { it.copy(prioridad = valor, prioridadError = null) }
    fun onPotencialChange(valor: String) = _estado.update { it.copy(potencial = valor, potencialError = null) }
    fun onEstadoChange(valor: String) = _estado.update { it.copy(estado = valor) }
    fun onObservacionesChange(valor: String) = _estado.update { it.copy(observaciones = valor) }

    // Revisa los campos obligatorios. Devuelve true si todo está bien
    fun validarFormulario(): Boolean {
        val e = _estado.value
        val validado = e.copy(
            nombreError = if (e.nombre.isBlank()) "El nombre es obligatorio" else null,
            tipoError = if (e.tipo.isBlank()) "Selecciona un tipo de oportunidad" else null,
            descripcionError = if (e.descripcion.isBlank()) "La descripción es obligatoria" else null,
            fuenteError = if (e.fuente.isBlank()) "La fuente de información es obligatoria" else null,
            prioridadError = if (e.prioridad.isBlank()) "Selecciona una prioridad" else null,
            potencialError = when {
                e.potencial.isBlank() -> "Selecciona el potencial comercial"
                e.potencial !in OportunidadOpciones.potenciales -> "Potencial no válido"
                else -> null
            }
        )
        _estado.value = validado
        return listOf(
            validado.nombreError, validado.tipoError, validado.descripcionError,
            validado.fuenteError, validado.prioridadError, validado.potencialError
        ).all { it == null }
    }

    // Valida y guarda: si el id es 0 crea una nueva, y si no, modifica la existente
    fun guardar() {
        if (!validarFormulario()) return
        val e = _estado.value
        val oportunidad = OportunidadEntity(
            id = e.id,
            nombre = e.nombre.trim(),
            tipo = e.tipo,
            descripcion = e.descripcion.trim(),
            fuente = e.fuente.trim(),
            prioridad = e.prioridad,
            potencial = e.potencial,
            estado = e.estado,
            observaciones = e.observaciones.trim()
        )
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            if (e.id == 0) repository.insertar(oportunidad) else repository.actualizar(oportunidad)
            _estado.update { it.copy(guardando = false, guardadoExitosamente = true) }
        }
    }

    // La pantalla llama esto después de volver a la lista
    fun onGuardadoConsumido() {
        _estado.update { it.copy(guardadoExitosamente = false) }
    }
}