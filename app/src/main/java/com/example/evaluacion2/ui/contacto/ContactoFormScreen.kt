package com.example.evaluacion2.ui.contacto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoFormScreen(
    navController: NavController,
    viewModel: ContactoViewModel,
    contactoId: Int,
    organizacionId: Int
) {

    val estado by viewModel.estado.collectAsState()

    // Prepara el formulario nuevo o carga el contacto existente
    LaunchedEffect(contactoId, organizacionId) {
        if (contactoId == -1) {
            viewModel.nuevoContacto(organizacionId)
        } else {
            viewModel.cargarContacto(contactoId)
        }
    }

    // Regresa al detalle cuando termina de guardar
    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.onGuardadoConsumido()
            navController.popBackStack()
        }
    }

    var mostrarEliminar by remember { mutableStateOf(false) }

    val areas = listOf(
        "Eventos",
        "Activaciones",
        "Marketing",
        "Comunicaciones",
        "Recursos Humanos",
        "Bienestar",
        "Beneficios",
        "Administración"
    )

    val mediosContacto = listOf(
        "Correo electrónico",
        "Teléfono",
        "Otro"
    )

    val estados = listOf(
        "Activo",
        "Inactivo"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (contactoId == -1)
                            "Nuevo contacto"
                        else
                            "Editar contacto"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del contacto") },
                isError = estado.nombreError != null,
                supportingText = {
                    Text(estado.nombreError ?: "")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.cargo,
                onValueChange = viewModel::onCargoChange,
                label = { Text("Cargo") },
                isError = estado.cargoError != null,
                supportingText = {
                    Text(estado.cargoError ?: "")
                },
                modifier = Modifier.fillMaxWidth()
            )

            SelectorContacto(
                valor = estado.area,
                opciones = areas,
                etiqueta = "Área",
                error = estado.areaError,
                onSeleccion = viewModel::onAreaChange
            )

            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo ficticio") },
                isError = estado.correoError != null,
                supportingText = {
                    Text(estado.correoError ?: "")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.telefono,
                onValueChange = { nuevoTexto ->
                    // Permite escribir únicamente dígitos (0 al 9)
                    if (nuevoTexto.all { it.isDigit() }) {
                        viewModel.onTelefonoChange(nuevoTexto)
                    }
                },
                label = { Text("Teléfono ficticio") },
                isError = estado.telefonoError != null,
                supportingText = {
                    Text(estado.telefonoError ?: "")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            SelectorContacto(
                valor = estado.medioContacto,
                opciones = mediosContacto,
                etiqueta = "Medio de contacto preferente",
                error = null,
                onSeleccion = viewModel::onMedioContactoChange
            )

            SelectorContacto(
                valor = estado.estado,
                opciones = estados,
                etiqueta = "Estado del contacto",
                error = null,
                onSeleccion = viewModel::onEstadoChange
            )

            OutlinedTextField(
                value = estado.observaciones,
                onValueChange = viewModel::onObservacionesChange,
                label = { Text("Observaciones") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.guardar() },
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (estado.guardando) "Guardando..."
                    else "Guardar contacto"
                )
            }

            if (contactoId != -1) {
                OutlinedButton(
                    onClick = { mostrarEliminar = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Eliminar contacto",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (mostrarEliminar) {
        AlertDialog(
            onDismissRequest = {
                mostrarEliminar = false
            },
            title = {
                Text("Eliminar contacto")
            },
            text = {
                Text("¿Seguro que quieres eliminar este contacto?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarEliminar = false
                        viewModel.eliminar()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarEliminar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Selector desplegable reutilizable para los campos del contacto
@Composable
private fun SelectorContacto(
    valor: String,
    opciones: List<String>,
    etiqueta: String,
    error: String?,
    onSeleccion: (String) -> Unit
) {

    var expandido by remember { mutableStateOf(false) }

    androidx.compose.foundation.layout.Box {

        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            isError = error != null,
            supportingText = {
                Text(error ?: "")
            },
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expandido = true }
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
