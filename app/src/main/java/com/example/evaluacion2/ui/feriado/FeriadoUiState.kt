package com.example.evaluacion2.ui.feriado

import com.example.evaluacion2.data.remote.FeriadoDto

// Estado de la pantalla de feriados
data class FeriadoUiState(
    val cargando: Boolean = false,
    val feriados: List<FeriadoDto> = emptyList(),
    val soloProximos: Boolean = true,
    val error: String? = null
)
