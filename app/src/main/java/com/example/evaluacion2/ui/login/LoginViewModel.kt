package com.example.evaluacion2.ui.login

import androidx.lifecycle.ViewModel
import com.example.evaluacion2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val repositorio = AuthRepository()

    // Estado de la pantalla: la pantalla lo observa
    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Al escribir se actualiza el campo y se borran los errores
    fun onUsuarioChange(valor: String) =
        _estado.update { it.copy(usuario = valor, usuarioError = null, loginError = null) }

    fun onClaveChange(valor: String) =
        _estado.update { it.copy(clave = valor, claveError = null, loginError = null) }

    // Valida los campos y revisa las credenciales. Devuelve true si el login es correcto
    fun iniciarSesion(): Boolean {
        val e = _estado.value
        val usuarioError = if (e.usuario.isBlank()) "El usuario es obligatorio" else null
        val claveError = if (e.clave.isBlank()) "La contraseña es obligatoria" else null

        if (usuarioError != null || claveError != null) {
            _estado.update { it.copy(usuarioError = usuarioError, claveError = claveError) }
            return false
        }

        val correcto = repositorio.validarCredenciales(e.usuario.trim(), e.clave)
        if (!correcto) {
            _estado.update { it.copy(loginError = "Usuario o contraseña incorrectos") }
        }
        return correcto
    }
}