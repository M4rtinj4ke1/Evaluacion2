
package com.example.evaluacion2.ui.organizacion

// Estado del formulario de organizaciones
data class OrganizacionUiState(
    val id: Int = 0,
    val nombre: String = "",
    val tipo: String = "",
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val sitioWeb: String = "",
    val observaciones: String = "",

    // Errores de validación
    val nombreError: String? = null,
    val tipoError: String? = null,
    val rubroError: String? = null,
    val comunaError: String? = null,
    val regionError: String? = null,

    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)
