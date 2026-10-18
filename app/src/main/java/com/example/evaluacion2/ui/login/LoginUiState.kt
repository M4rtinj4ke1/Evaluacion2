package com.example.evaluacion2.ui.login

// "Foto" de la pantalla de login: lo escrito y los errores
data class LoginUiState(
    val usuario: String = "",
    val clave: String = "",
    val usuarioError: String? = null,
    val claveError: String? = null,
    val loginError: String? = null
)