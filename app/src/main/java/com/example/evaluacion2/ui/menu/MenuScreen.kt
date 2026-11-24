
package com.example.evaluacion2.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Menú principal de la aplicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController, usuario: String) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú") }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Bienvenido, $usuario",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Acceso al módulo de oportunidades
            Button(
                onClick = {
                    navController.navigate("lista")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver oportunidades")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Acceso al módulo de organizaciones
            Button(
                onClick = {
                    navController.navigate("organizaciones")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver organizaciones")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Acceso a la API externa de feriados
            Button(
                onClick = {
                    navController.navigate("feriados")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Feriados de Chile (API externa)")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("menu/{usuario}") {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
