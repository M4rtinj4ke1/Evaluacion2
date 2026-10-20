package com.example.evaluacion2.ui.oportunidad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.net.toUri
import com.example.evaluacion2.ui.components.ImagenInteligente

import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOportunidadScreen(
    navController: NavController,
    viewModel: OportunidadViewModel,
    oportunidadId: Int
) {
    val oportunidades by viewModel.oportunidades.collectAsState()
    val oportunidad = oportunidades.find { it.id == oportunidadId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de oportunidad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (oportunidad == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text("No se encontró esta oportunidad.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = oportunidad.nombre,
                    style = MaterialTheme.typography.headlineSmall
                )

                DetalleCampo("Tipo", oportunidad.tipo)
                DetalleCampo("Descripción", oportunidad.descripcion)
                DetalleCampo("Fuente de información", oportunidad.fuente)
                DetalleCampo("Prioridad", oportunidad.prioridad)
                DetalleCampo("Potencial comercial", oportunidad.potencial)
                DetalleCampo("Estado", oportunidad.estado)
                DetalleCampo("Observaciones", oportunidad.observaciones)

                Text(
                    text = "Foto de evidencia",
                    style = MaterialTheme.typography.titleMedium
                )

                ImagenInteligente(
                    uri = oportunidad.fotoUri?.toUri(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Text(
                    text = "Ubicación",
                    style = MaterialTheme.typography.titleMedium
                )

                if (oportunidad.latitud != null &&
                    oportunidad.longitud != null
                ) {
                    Text("Latitud: ${oportunidad.latitud}")
                    Text("Longitud: ${oportunidad.longitud}")
                } else {
                    Text("No se registró una ubicación.")
                }

                Button(
                    onClick = {
                        navController.navigate("form/${oportunidad.id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar oportunidad")
                }

                Button(
                    onClick = {
                        viewModel.eliminarOportunidad(oportunidad)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Eliminar oportunidad")
                }

            }
        }
    }
}

@Composable
private fun DetalleCampo(
    etiqueta: String,
    valor: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = valor.ifBlank { "Sin información" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
