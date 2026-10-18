package com.example.evaluacion2.data.repository

// Revisa si el usuario y la contraseña son correctos (cuenta ficticia)
class AuthRepository {

    private val usuarioValido = "admin"
    private val claveValida = "123"

    fun validarCredenciales(usuario: String, clave: String): Boolean {
        return usuario == usuarioValido && clave == claveValida
    }
}