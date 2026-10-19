package com.example.evaluacion2.ui.oportunidad
import android.net.Uri

// "Foto" del formulario: lo que hay escrito y los errores de cada campo
data class OportunidadUiState(
    val id: Int = 0,
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fuente: String = "",
    val prioridad: String = "",
    val potencial: String = "",
    val estado: String = "Evento identificado",
    val observaciones: String = "",
    val fotoUri: Uri? = null,
    // Un error en null significa que el campo está bien
    val nombreError: String? = null,
    val tipoError: String? = null,
    val descripcionError: String? = null,
    val fuenteError: String? = null,
    val prioridadError: String? = null,
    val potencialError: String? = null,
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)