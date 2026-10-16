package com.example.evaluacion2.ui.oportunidad

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

// Pantalla base de la lista. Por ahora es solo diseño, sin datos reales
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen() {
    // Scaffold arma la estructura: barra superior, botón flotante y contenido
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Oportunidades") })
        },
        floatingActionButton = {
            // Botón "+" (todavía no hace nada)
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar oportunidad")
            }
        }
    ) { innerPadding ->
        // innerPadding evita que el contenido quede tapado por la barra de arriba
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay oportunidades registradas")
        }
    }
}

// Permite ver la pantalla en el panel Design sin ejecutar la app
@Preview(showBackground = true)
@Composable
fun ListaOportunidadesScreenPreview() {
    ListaOportunidadesScreen()
}