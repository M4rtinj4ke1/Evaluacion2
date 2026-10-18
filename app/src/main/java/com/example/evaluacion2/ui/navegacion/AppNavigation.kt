package com.example.evaluacion2.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.evaluacion2.ui.oportunidad.ListaOportunidadesScreen
import com.example.evaluacion2.ui.oportunidad.OportunidadFormScreen
import com.example.evaluacion2.ui.oportunidad.OportunidadViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Un solo ViewModel compartido por las dos pantallas
    val viewModel: OportunidadViewModel = viewModel()

    NavHost(navController = navController, startDestination = "lista") {

        composable("lista") {
            ListaOportunidadesScreen(navController, viewModel)
        }

        // "form/{id}": id = -1 es una oportunidad nueva, y otro número es editar
        composable(
            route = "form/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            OportunidadFormScreen(navController, viewModel, id)
        }
    }
}