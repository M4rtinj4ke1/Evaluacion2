package com.example.evaluacion2.ui.oportunidad

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import com.example.evaluacion2.ui.components.SeccionUbicacion
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.evaluacion2.ui.components.SeccionFoto
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Formulario para crear (oportunidadId = -1) o editar una oportunidad
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    navController: NavController,
    viewModel: OportunidadViewModel,
    oportunidadId: Int
) {
    // La pantalla "mira" el estado del ViewModel y se redibuja sola
    val estado by viewModel.estado.collectAsState()
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    // Al entrar: formulario vacío (nueva) o cargar los datos (editar)
    LaunchedEffect(oportunidadId) {
        if (oportunidadId == -1) viewModel.nuevaOportunidad()
        else viewModel.cargarOportunidad(oportunidadId)
    }

    // Cuando se guarda bien, vuelve a la lista
    LaunchedEffect(estado.guardadoExitosamente) {
        if (estado.guardadoExitosamente) {
            viewModel.onGuardadoConsumido()
            navController.popBackStack()
        }
    }

    // Confirmación antes de eliminar
    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Eliminar oportunidad") },
            text = { Text("¿Seguro que quieres eliminarla? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    viewModel.eliminar()
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (oportunidadId == -1) "Nueva oportunidad" else "Editar oportunidad")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cada campo muestra su error en rojo si el ViewModel lo marcó
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre de la oportunidad") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                isError = estado.nombreError != null,
                supportingText = { Text(estado.nombreError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            CampoSelector(
                etiqueta = "Tipo de oportunidad",
                icono = Icons.Default.Menu,
                valor = estado.tipo,
                opciones = OportunidadOpciones.tipos,
                error = estado.tipoError,
                onSeleccion = viewModel::onTipoChange
            )

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                minLines = 3,
                isError = estado.descripcionError != null,
                supportingText = { Text(estado.descripcionError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.fuente,
                onValueChange = viewModel::onFuenteChange,
                label = { Text("Fuente de información") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                isError = estado.fuenteError != null,
                supportingText = { Text(estado.fuenteError ?: "") },
                modifier = Modifier.fillMaxWidth()
            )

            CampoSelector(
                etiqueta = "Prioridad",
                icono = Icons.Default.Warning,
                valor = estado.prioridad,
                opciones = OportunidadOpciones.prioridades,
                error = estado.prioridadError,
                onSeleccion = viewModel::onPrioridadChange
            )

            CampoSelector(
                etiqueta = "Potencial comercial",
                icono = Icons.Default.Star,
                valor = estado.potencial,
                opciones = OportunidadOpciones.potenciales,
                error = estado.potencialError,
                onSeleccion = viewModel::onPotencialChange
            )

            CampoSelector(
                etiqueta = "Estado de gestión",
                icono = Icons.Default.CheckCircle,
                valor = estado.estado,
                opciones = OportunidadOpciones.estados,
                error = null,
                onSeleccion = viewModel::onEstadoChange
            )

            OutlinedTextField(
                value = estado.observaciones,
                onValueChange = viewModel::onObservacionesChange,
                label = { Text("Observaciones (opcional)") },
                leadingIcon = { Icon(Icons.Default.Create, contentDescription = null) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // Recurso nativo 1: cámara (foto de evidencia)
            SeccionFoto(
                uri = estado.fotoUri,
                onFotoCapturada = viewModel::onFotoChange
            )

            // Recurso nativo 2: ubicación (lugar de la oportunidad)
            SeccionUbicacion(
                latitud = estado.latitud,
                longitud = estado.longitud,
                onUbicacionObtenida = viewModel::onUbicacionChange
            )

            Button(
                onClick = { viewModel.guardar() },
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                // El texto del botón cambia con un fundido suave al guardar
                Crossfade(targetState = estado.guardando, label = "textoGuardar") { guardando ->
                    Text(if (guardando) "Guardando..." else "Guardar")
                }
            }

            // Eliminar: solo aparece al editar una oportunidad que ya existe
            if (oportunidadId != -1) {
                OutlinedButton(
                    onClick = { mostrarConfirmacion = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar oportunidad", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// Campo con lista desplegable para elegir una opción (se reutiliza 4 veces)
@Composable
private fun CampoSelector(
    etiqueta: String,
    icono: ImageVector,
    valor: String,
    opciones: List<String>,
    error: String?,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = {
                Icon(
                    if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            },
            isError = error != null,
            supportingText = { Text(error ?: "") },
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente que recibe el clic para abrir la lista
        Box(modifier = Modifier.matchParentSize().clickable { expandido = true })

        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
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