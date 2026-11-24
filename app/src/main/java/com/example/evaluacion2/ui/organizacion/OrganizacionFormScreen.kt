package com.example.evaluacion2.ui.organizacion

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
import androidx.compose.material.icons.filled.Business
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionFormScreen(
    navController: NavController,
    viewModel: OrganizacionViewModel,
    organizacionId: Int
) {

    // Observa los datos del formulario
    val estado by viewModel.estado.collectAsState()

    // Observa las regiones y comunas dinámicas desde la BD Room
    val regiones by viewModel.regiones.collectAsState()
    val comunas by viewModel.comunas.collectAsState()

    // Si es nueva, limpia el formulario.
    // Si existe, carga sus datos.
    LaunchedEffect(organizacionId) {
        if (organizacionId == -1) {
            viewModel.nuevaOrganizacion()
        } else {
            viewModel.cargarOrganizacion(organizacionId)
        }
    }

    // Cuando termina de guardar, vuelve a la lista
    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.onGuardadoConsumido()
            navController.popBackStack()
        }
    }

    var mostrarEliminar by remember { mutableStateOf(false) }

    // Tipos de organización disponibles
    val tipos = listOf(
        "Empresa",
        "Municipalidad",
        "Institución pública",
        "Institución privada",
        "Organizador de eventos",
        "Centro comercial",
        "Asociación",
        "Otra entidad"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (organizacionId == -1)
                            "Nueva organización"
                        else
                            "Editar organización"
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
                label = { Text("Nombre de la organización") },
                leadingIcon = {
                    Icon(Icons.Default.Business, contentDescription = null)
                },
                isError = estado.nombreError != null,
                supportingText = {
                    Text(estado.nombreError ?: "")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector del tipo de organización
            SelectorOrganizacion(
                valor = estado.tipo,
                opciones = tipos,
                etiqueta = "Tipo de organización",
                error = estado.tipoError,
                onSeleccion = viewModel::onTipoChange
            )

            OutlinedTextField(
                value = estado.rubro,
                onValueChange = viewModel::onRubroChange,
                label = { Text("Rubro") },
                isError = estado.rubroError != null,
                supportingText = {
                    Text(estado.rubroError ?: "")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 1. Selector de Región (se selecciona primero)
            SelectorOrganizacion(
                valor = estado.region,
                opciones = regiones,
                etiqueta = "Región",
                error = estado.regionError,
                onSeleccion = { regionSeleccionada ->
                    viewModel.onRegionChange(regionSeleccionada)
                }
            )

            // 2. Selector de Comuna (se habilita/rellena según la región elegida)
            SelectorOrganizacion(
                valor = estado.comuna,
                opciones = if (estado.region.isBlank()) emptyList() else comunas,
                etiqueta = if (estado.region.isBlank()) "Selecciona primero una región" else "Comuna",
                error = estado.comunaError,
                onSeleccion = { comunaSeleccionada ->
                    if (estado.region.isNotBlank()) {
                        viewModel.onComunaChange(comunaSeleccionada)
                    }
                }
            )

            OutlinedTextField(
                value = estado.sitioWeb,
                onValueChange = viewModel::onSitioWebChange,
                label = { Text("Sitio web (opcional)") },
                modifier = Modifier.fillMaxWidth()
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
                    else "Guardar organización"
                )
            }

            // Solo aparece cuando estamos editando
            if (organizacionId != -1) {
                OutlinedButton(
                    onClick = { mostrarEliminar = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Eliminar organización",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Confirmación antes de eliminar
    if (mostrarEliminar) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                mostrarEliminar = false
            },
            title = {
                Text("Eliminar organización")
            },
            text = {
                Text(
                    "¿Seguro que quieres eliminar esta organización y sus contactos?"
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        mostrarEliminar = false
                        viewModel.eliminar()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
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

// Selector desplegable reutilizable
@Composable
private fun SelectorOrganizacion(
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

        // Recibe el clic para abrir las opciones solo si hay elementos disponibles
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = opciones.isNotEmpty()) { expandido = true }
        )

        if (opciones.isNotEmpty()) {
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
}