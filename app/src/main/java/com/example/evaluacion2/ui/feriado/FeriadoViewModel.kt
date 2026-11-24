package com.example.evaluacion2.ui.feriado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.evaluacion2.data.remote.FeriadoDto
import com.example.evaluacion2.data.repository.FeriadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel de la pantalla de feriados (API externa).
 * Recibe el repositorio y la fecha de hoy por constructor para poder probarlo con MockK.
 */
class FeriadoViewModel(
    private val repository: FeriadoRepository = FeriadoRepository(),
    private val hoy: () -> String = {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
) : ViewModel() {

    private val _estado = MutableStateFlow(FeriadoUiState())
    val estado: StateFlow<FeriadoUiState> = _estado.asStateFlow()

    // Lista completa que llegó de la API (sin filtrar)
    private var todos: List<FeriadoDto> = emptyList()

    init {
        cargarFeriados()
    }

    fun cargarFeriados() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            repository.obtenerFeriados()
                .onSuccess { lista ->
                    todos = lista
                    _estado.update { it.copy(cargando = false, feriados = aplicarFiltro(it.soloProximos)) }
                }
                .onFailure { e ->
                    _estado.update { it.copy(cargando = false, error = e.message ?: "Error desconocido") }
                }
        }
    }

    // Cambia entre "solo próximos" y "todo el año"
    fun onSoloProximosChange(valor: Boolean) {
        _estado.update { it.copy(soloProximos = valor, feriados = aplicarFiltro(valor)) }
    }

    private fun aplicarFiltro(soloProximos: Boolean): List<FeriadoDto> =
        if (soloProximos) repository.filtrarProximos(todos, hoy()) else todos
}
