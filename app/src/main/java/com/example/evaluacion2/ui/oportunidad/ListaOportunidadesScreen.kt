package com.example.evaluacion2.ui.oportunidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Lista de oportunidades guardadas en la base de datos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen(
    navController: NavController,
    viewModel: OportunidadViewModel
) {
    // Lista que viene de Room y se actualiza sola
    val oportunidades by viewModel.oportunidades.collectAsState()

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
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(oportunidades, key = { it.id }) { oportunidad ->
                    // Al tocar una oportunidad se abre el formulario para editarla
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("form/${oportunidad.id}") }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(oportunidad.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(oportunidad.tipo, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Prioridad: ${oportunidad.prioridad}  |  Estado: ${oportunidad.estado}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}