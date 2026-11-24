package com.example.evaluacion2

import com.example.evaluacion2.data.repository.AuthRepository
import com.example.evaluacion2.ui.login.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas del login: validación de campos y credenciales
class LoginViewModelTest {

    @Test
    fun `credenciales correctas permiten iniciar sesion`() {
        val vm = LoginViewModel()
        vm.onUsuarioChange("admin")
        vm.onClaveChange("123")

        assertTrue(vm.iniciarSesion())
        assertNull(vm.estado.value.loginError)
    }

    @Test
    fun `campos vacios muestran error en cada campo`() {
        val vm = LoginViewModel()

        assertFalse(vm.iniciarSesion())
        assertEquals("El usuario es obligatorio", vm.estado.value.usuarioError)
        assertEquals("La contraseña es obligatoria", vm.estado.value.claveError)
    }

    @Test
    fun `clave incorrecta muestra error de login`() {
        val vm = LoginViewModel()
        vm.onUsuarioChange("admin")
        vm.onClaveChange("999")

        assertFalse(vm.iniciarSesion())
        assertEquals("Usuario o contraseña incorrectos", vm.estado.value.loginError)
    }

    @Test
    fun `escribir en un campo limpia su error`() {
        val vm = LoginViewModel()
        vm.iniciarSesion() // genera errores
        vm.onUsuarioChange("a")
        vm.onClaveChange("b")

        assertNull(vm.estado.value.usuarioError)
        assertNull(vm.estado.value.claveError)
    }

    @Test
    fun `el usuario se recorta antes de validar`() {
        assertTrue(AuthRepository().validarCredenciales("admin", "123"))
        val vm = LoginViewModel()
        vm.onUsuarioChange("  admin  ")
        vm.onClaveChange("123")
        assertTrue(vm.iniciarSesion())
    }
}
