package com.example.evaluacion2.ui.organizacion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.evaluacion2.ui.contacto.ContactoViewModel
import com.example.evaluacion2.ui.organizacion.OrganizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOrganizacionScreen(
    navController: NavController,
    organizacionViewModel: OrganizacionViewModel,
    contactoViewModel: ContactoViewModel,
    organizacionId: Int
) {

    // Obtiene las organizaciones y los contactos
    val organizaciones by organizacionViewModel.organizaciones.collectAsState()
    val contactos by contactoViewModel.contactos.collectAsState()

    // Busca la organización seleccionada
    val organizacion = organizaciones.find {
        it.id == organizacionId
    }

    // Filtra los contactos que pertenecen a esta organización
    val contactosOrganizacion = contactos.filter {
        it.organizacionId == organizacionId
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de organización") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Permite editar la organización
                    IconButton(
                        onClick = {
                            navController.navigate(
                                "organizacionForm/$organizacionId"
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar organización"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Agrega un contacto a esta organización
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        "contactoForm/-1/$organizacionId"
                    )
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar contacto"
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                if (organizacion != null) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            organizacion.nombre,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                        )

                        Text("Tipo: ${organizacion.tipo}")
                        Text("Rubro: ${organizacion.rubro}")
                        Text("Comuna: ${organizacion.comuna}")
                        Text("Región: ${organizacion.region}")

                        if (organizacion.sitioWeb.isNotBlank()) {
                            Text("Sitio web: ${organizacion.sitioWeb}")
                        }

                        if (organizacion.observaciones.isNotBlank()) {
                            Text("Observaciones: ${organizacion.observaciones}")
                        }

                        Text(
                            "Contactos corporativos",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                } else {
                    Text("Organización no encontrada")
                }
            }

            if (contactosOrganizacion.isEmpty()) {

                item {
                    Text("Esta organización todavía no tiene contactos.")
                }

            } else {

                items(contactosOrganizacion, key = { it.id }) { contacto ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    "contactoForm/${contacto.id}/$organizacionId"
                                )
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {

                            Text(
                                contacto.nombre,
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                            )

                            Text("Cargo: ${contacto.cargo}")
                            Text("Área: ${contacto.area}")
                            Text("Correo: ${contacto.correo}")
                            Text("Teléfono: ${contacto.telefono}")
                            Text("Estado: ${contacto.estado}")
                        }
                    }
                }
            }
        }
    }
}
