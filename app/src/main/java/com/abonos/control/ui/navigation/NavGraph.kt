package com.abonos.control.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abonos.control.ui.screens.*

private object Rutas {
    const val LOGIN = "login"
    const val CLIENTES = "clientes"
    const val CLIENTE_DETALLE = "cliente_detalle/{clienteId}"
    const val AGREGAR_CLIENTE = "agregar_cliente"
    const val USUARIOS = "usuarios"
    const val REPORTES = "reportes"
}

@Composable
fun GrafoNavegacion() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

        composable(Rutas.LOGIN) {
            LoginScreen(onLoginExitoso = {
                navController.navigate(Rutas.CLIENTES) {
                    popUpTo(Rutas.LOGIN) { inclusive = true }
                }
            })
        }

        composable(Rutas.CLIENTES) {
            ClientesScreen(
                onClienteClick = { cliente -> navController.navigate("cliente_detalle/${cliente.id}") },
                onAgregarCliente = { navController.navigate(Rutas.AGREGAR_CLIENTE) },
                onVerReportes = { navController.navigate(Rutas.REPORTES) },
                onGestionarUsuarios = { navController.navigate(Rutas.USUARIOS) }
            )
        }

        composable(
            route = Rutas.CLIENTE_DETALLE,
            arguments = listOf(navArgument("clienteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getLong("clienteId") ?: 0L
            ClienteDetalleScreen(clienteId = clienteId, onVolver = { navController.popBackStack() })
        }

        composable(Rutas.AGREGAR_CLIENTE) {
            AgregarClienteScreen(onVolver = { navController.popBackStack() })
        }

        composable(Rutas.USUARIOS) {
            UsuariosScreen(onVolver = { navController.popBackStack() })
        }

        composable(Rutas.REPORTES) {
            ReportesScreen(onVolver = { navController.popBackStack() })
        }
    }
}
