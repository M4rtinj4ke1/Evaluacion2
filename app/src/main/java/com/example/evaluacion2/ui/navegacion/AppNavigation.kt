
package com.example.evaluacion2.ui.navegacion

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.evaluacion2.ui.login.LoginScreen
import com.example.evaluacion2.ui.menu.MenuScreen
import com.example.evaluacion2.ui.oportunidad.ListaOportunidadesScreen
import com.example.evaluacion2.ui.oportunidad.OportunidadFormScreen
import com.example.evaluacion2.ui.oportunidad.OportunidadViewModel
import com.example.evaluacion2.ui.oportunidad.DetalleOportunidadScreen
import com.example.evaluacion2.ui.organizacion.OrganizacionViewModel
import com.example.evaluacion2.ui.organizacion.ListaOrganizacionesScreen
import com.example.evaluacion2.ui.organizacion.OrganizacionFormScreen
import com.example.evaluacion2.ui.organizacion.DetalleOrganizacionScreen
import com.example.evaluacion2.ui.contacto.ContactoViewModel
import com.example.evaluacion2.ui.contacto.ContactoFormScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // ViewModels compartidos por las pantallas de cada módulo
    val oportunidadViewModel: OportunidadViewModel = viewModel()
    val organizacionViewModel: OrganizacionViewModel = viewModel()
    val contactoViewModel: ContactoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        // Menú principal
        composable(
            route = "menu/{usuario}",
            arguments = listOf(navArgument("usuario") {
                type = NavType.StringType
            })
        ) { backStackEntry ->

            val usuario = backStackEntry.arguments?.getString("usuario") ?: ""

            MenuScreen(navController, usuario)
        }

        // Módulo de oportunidades
        composable("lista") {
            ListaOportunidadesScreen(navController, oportunidadViewModel)
        }

        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.IntType
            })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id") ?: -1

            DetalleOportunidadScreen(
                navController,
                oportunidadViewModel,
                id
            )
        }

        composable(
            route = "form/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.IntType
            })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id") ?: -1

            OportunidadFormScreen(
                navController,
                oportunidadViewModel,
                id
            )
        }

        // Módulo de organizaciones
        composable("organizaciones") {
            ListaOrganizacionesScreen(
                navController,
                organizacionViewModel
            )
        }

        composable(
            route = "organizacionForm/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.IntType
            })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id") ?: -1

            OrganizacionFormScreen(
                navController,
                organizacionViewModel,
                id
            )
        }

        composable(
            route = "detalleOrganizacion/{id}",
            arguments = listOf(navArgument("id") {
                type = NavType.IntType
            })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id") ?: -1

            DetalleOrganizacionScreen(
                navController,
                organizacionViewModel,
                contactoViewModel,
                id
            )
        }

        // Módulo de contactos
        composable(
            route = "contactoForm/{contactoId}/{organizacionId}",
            arguments = listOf(
                navArgument("contactoId") {
                    type = NavType.IntType
                },
                navArgument("organizacionId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val contactoId =
                backStackEntry.arguments?.getInt("contactoId") ?: -1

            val organizacionId =
                backStackEntry.arguments?.getInt("organizacionId") ?: -1

            ContactoFormScreen(
                navController,
                contactoViewModel,
                contactoId,
                organizacionId
            )
        }
    }
}
