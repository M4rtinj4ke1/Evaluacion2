package com.example.evaluacion2.ui.oportunidad

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.evaluacion2.ui.components.ImagenInteligente

import androidx.compose.material3.FilterChip
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox

import androidx.compose.material3.ExposedDropdownMenuDefaults


// Color de cada prioridad (rojo = alta, amarillo = media, verde = baja)
private fun colorPrioridad(prioridad: String): Color = when (prioridad) {
    "Alta" -> Color(0xFFD32F2F)
    "Media" -> Color(0xFFF9A825)
    else -> Color(0xFF388E3C)
}

// Lista de oportunidades guardadas en la base de datos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen(
    navController: NavController,
    viewModel: OportunidadViewModel
) {
    // Lista que viene de Room y se actualiza sola
    val oportunidades by viewModel.oportunidades.collectAsState()

    var filtroEstado by remember { mutableStateOf("Todos los estados") }
    var filtroPrioridad by remember { mutableStateOf("Todas") }
    var estadoMenuExpandido by remember { mutableStateOf(false) }

    val oportunidadesFiltradas = oportunidades.filter { oportunidad ->
        (filtroEstado == "Todos los estados" || oportunidad.estado == filtroEstado) &&
                (filtroPrioridad == "Todas" || oportunidad.prioridad == filtroPrioridad)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Oportunidades") })
        },
        floatingActionButton = {
            // Botón "+": abre el formulario vacío (-1 significa "nueva")
            FloatingActionButton(onClick = { navController.navigate("form/-1") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar oportunidad")
            }
        }
    ) { innerPadding ->
        if (oportunidades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay oportunidades registradas")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {



                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "Filtrar por estado",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = estadoMenuExpandido,
                            onExpandedChange = {
                                estadoMenuExpandido = !estadoMenuExpandido
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.OutlinedTextField(
                                value = filtroEstado,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Estado de gestión") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = estadoMenuExpandido
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = estadoMenuExpandido,
                                onDismissRequest = {
                                    estadoMenuExpandido = false
                                }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Todos los estados") },
                                    onClick = {
                                        filtroEstado = "Todos los estados"
                                        estadoMenuExpandido = false
                                    }
                                )

                                OportunidadOpciones.estados.forEach { estado ->
                                    DropdownMenuItem(
                                        text = { Text(estado) },
                                        onClick = {
                                            filtroEstado = estado
                                            estadoMenuExpandido = false
                                        }
                                    )
                                }
                            }
                        }


                        Text(
                            text = "Filtrar por prioridad",
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Todas", "Alta", "Media", "Baja")
                                .forEach { prioridad ->
                                    FilterChip(
                                        selected = filtroPrioridad == prioridad,
                                        onClick = { filtroPrioridad = prioridad },
                                        label = { Text(prioridad) }
                                    )
                                }
                        }
                    }
                }



                item {
                    if (oportunidadesFiltradas.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No se encontraron oportunidades")
                            Text(
                                "Prueba cambiando los filtros seleccionados.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            onClick = {
                                filtroEstado = "Todos los estados"
                                filtroPrioridad = "Todas"
                            },
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Limpiar filtros")
                        }

                    }
                }

                items(oportunidadesFiltradas, key = { it.id }) { oportunidad ->
                    // El color de la prioridad cambia con una transición suave al editarla
                    val colorIndicador by animateColorAsState(
                        targetValue = colorPrioridad(oportunidad.prioridad),
                        label = "colorPrioridad"
                    )

                    // Al tocar una oportunidad se abre su pantalla de detalle
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .clickable { navController.navigate("detalle/${oportunidad.id}") }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Foto de evidencia (o ícono de cámara si no tiene)
                            ImagenInteligente(
                                uri = oportunidad.fotoUri?.toUri(),
                                modifier = Modifier.size(64.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {
                                Text(oportunidad.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(oportunidad.tipo, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Prioridad: ${oportunidad.prioridad}  |  Estado: ${oportunidad.estado}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (oportunidad.latitud != null && oportunidad.longitud != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = "Con ubicación",
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            "%.4f, %.4f".format(oportunidad.latitud, oportunidad.longitud),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }

                            // Punto de color según la prioridad
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(colorIndicador, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}
