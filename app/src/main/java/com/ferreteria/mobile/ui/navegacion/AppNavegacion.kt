package com.ferreteria.mobile.ui.navegacion

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ferreteria.mobile.data.AvisosEnVivo
import com.ferreteria.mobile.data.Carrito
import com.ferreteria.mobile.data.RetrofitClient
import com.ferreteria.mobile.ui.carrito.CarritoScreen
import com.ferreteria.mobile.ui.catalogo.CatalogoScreen
import com.ferreteria.mobile.ui.cotizaciones.CotizacionesScreen
import com.ferreteria.mobile.ui.cotizaciones.DetalleCotizacionScreen
import com.ferreteria.mobile.ui.login.LoginScreen
import com.ferreteria.mobile.ui.notificaciones.NotificacionesScreen
import com.ferreteria.mobile.ui.pedidos.DetallePedidoScreen
import com.ferreteria.mobile.ui.pedidos.PedidosScreen
import com.ferreteria.mobile.ui.perfil.PerfilScreen
import com.ferreteria.mobile.ui.registro.RegistroScreen
import com.ferreteria.mobile.util.SessionManager

data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

val pestanas = listOf(
    Pestana("catalogo", "Catálogo", Icons.Filled.Build),
    Pestana("carrito", "Carrito", Icons.Filled.ShoppingCart),
    Pestana("cotizaciones", "Cotizaciones", Icons.AutoMirrored.Filled.List),
    Pestana("pedidos", "Pedidos", Icons.Filled.CheckCircle),
    Pestana("perfil", "Perfil", Icons.Filled.Person)
)

fun irAPestana(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo("catalogo") { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun irAlLogin(navController: NavHostController) {
    navController.navigate("login") {
        popUpTo(navController.graph.id) { inclusive = true }
    }
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val inicio = if (SessionManager.haySesion()) "catalogo" else "login"
    val enSesion = rutaActual != null && rutaActual != "login" && rutaActual != "registro"
    val conBarra = pestanas.any { it.ruta == rutaActual }
    val snackbarHostState = remember { SnackbarHostState() }
    val abrirNotificaciones = { navController.navigate("notificaciones") }

    LaunchedEffect(SessionManager.sesionExpirada) {
        if (SessionManager.sesionExpirada && rutaActual != "login") {
            AvisosEnVivo.desconectar()
            Carrito.vaciar()
            irAlLogin(navController)
        }
    }

    LaunchedEffect(enSesion) {
        if (enSesion) {
            try {
                AvisosEnVivo.noLeidas = RetrofitClient.api.listarNotificaciones(true).size
            } catch (e: Exception) {
            }
        }
    }

    LaunchedEffect(rutaActual) {
        if (enSesion) {
            AvisosEnVivo.conectar()
        }
    }

    LaunchedEffect(AvisosEnVivo.ultimoAviso) {
        val aviso = AvisosEnVivo.ultimoAviso
        if (aviso != null) {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar("${aviso.titulo}: ${aviso.mensaje}")
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = if (conBarra) Modifier else Modifier.navigationBarsPadding()
            )
        },
        bottomBar = {
            if (conBarra) {
                NavigationBar {
                    for (pestana in pestanas) {
                        NavigationBarItem(
                            selected = rutaActual == pestana.ruta,
                            onClick = { irAPestana(navController, pestana.ruta) },
                            icon = {
                                if (pestana.ruta == "carrito" && Carrito.items.isNotEmpty()) {
                                    BadgedBox(badge = { Badge { Text("${Carrito.items.size}") } }) {
                                        Icon(pestana.icono, contentDescription = pestana.titulo)
                                    }
                                } else {
                                    Icon(pestana.icono, contentDescription = pestana.titulo)
                                }
                            },
                            label = { Text(pestana.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = inicio,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            composable("login") {
                LoginScreen(
                    alIngresar = {
                        navController.navigate("catalogo") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    alIrARegistro = { navController.navigate("registro") }
                )
            }
            composable("registro") {
                RegistroScreen(alVolver = { navController.popBackStack() })
            }
            composable("catalogo") {
                CatalogoScreen(alAbrirNotificaciones = abrirNotificaciones)
            }
            composable("carrito") {
                CarritoScreen(
                    alVerCotizaciones = { irAPestana(navController, "cotizaciones") },
                    alAbrirNotificaciones = abrirNotificaciones
                )
            }
            composable("cotizaciones") {
                CotizacionesScreen(
                    alAbrirCotizacion = { id -> navController.navigate("cotizacion/$id") },
                    alAbrirNotificaciones = abrirNotificaciones
                )
            }
            composable(
                route = "cotizacion/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entrada ->
                DetalleCotizacionScreen(
                    idCotizacion = entrada.arguments?.getLong("id") ?: 0,
                    alVolver = { navController.popBackStack() },
                    alVerPedidos = {
                        navController.popBackStack()
                        irAPestana(navController, "pedidos")
                    }
                )
            }
            composable("pedidos") {
                PedidosScreen(
                    alAbrirPedido = { id -> navController.navigate("pedido/$id") },
                    alAbrirNotificaciones = abrirNotificaciones
                )
            }
            composable(
                route = "pedido/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entrada ->
                DetallePedidoScreen(
                    idPedido = entrada.arguments?.getLong("id") ?: 0,
                    alVolver = { navController.popBackStack() }
                )
            }
            composable("perfil") {
                PerfilScreen(
                    alCerrarSesion = { irAlLogin(navController) },
                    alAbrirNotificaciones = abrirNotificaciones
                )
            }
            composable("notificaciones") {
                NotificacionesScreen(
                    alVolver = { navController.popBackStack() },
                    alAbrirCotizacion = { id -> navController.navigate("cotizacion/$id") },
                    alAbrirPedido = { id -> navController.navigate("pedido/$id") }
                )
            }
        }
    }
}
