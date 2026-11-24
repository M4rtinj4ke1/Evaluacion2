package com.example.evaluacion2.ui.organizacion

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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOrganizacionesScreen(
    navController: NavController,
    viewModel: OrganizacionViewModel
) {

    // Lista de organizaciones que viene de Room
    val organizaciones by viewModel.organizaciones.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organizaciones") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al menú"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Abre el formulario para registrar una organización
            FloatingActionButton(
                onClick = {
                    navController.navigate("organizacionForm/-1")
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar organización"
                )
            }
        }
    ) { innerPadding ->

        if (organizaciones.isEmpty()) {

            // Mensaje cuando todavía no existen organizaciones
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay organizaciones registradas")
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(organizaciones, key = { it.id }) { organizacion ->

                    // Tarjeta de cada organización
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    "detalleOrganizacion/${organizacion.id}"
                                )
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            androidx.compose.foundation.layout.Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null
                                )

                                Text(
                                    text = organizacion.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            Text(
                                "Tipo: ${organizacion.tipo}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                "Rubro: ${organizacion.rubro}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                "${organizacion.comuna}, ${organizacion.region}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
