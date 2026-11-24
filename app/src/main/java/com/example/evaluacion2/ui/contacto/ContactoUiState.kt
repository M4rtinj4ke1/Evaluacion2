package com.example.evaluacion2.ui.contacto

// Estado del formulario de contactos
data class ContactoUiState(
    val id: Int = 0,
    val nombre: String = "",
    val organizacionId: Int = 0,
    val cargo: String = "",
    val area: String = "",
    val correo: String = "",
    val telefono: String = "",
    val medioContacto: String = "",
    val estado: String = "Activo",
    val observaciones: String = "",

    // Errores de validación
    val nombreError: String? = null,
    val cargoError: String? = null,
    val areaError: String? = null,
    val correoError: String? = null,
    val telefonoError: String? = null,

    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)
