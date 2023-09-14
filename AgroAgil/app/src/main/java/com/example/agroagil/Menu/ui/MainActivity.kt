package com.example.agroagil.Menu.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.agroagil.Menu.ui.featureMenu.menu.ui.Menu
import androidx.lifecycle.viewmodel.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agroagil.Farm.ui.Farm
import com.example.agroagil.Farm.ui.FarmViewModel
import com.example.agroagil.Loan.ui.LoanAddScreen
import com.example.agroagil.Loan.ui.LoanScreen
import com.lourd.myapplication.featureMenu.NavigationEventMenu
import com.lourd.myapplication.featureMenu.menu.ui.GranjaDePrueba
import com.lourd.myapplication.featureMenu.menu.ui.MenuViewModel
import com.lourd.myapplication.featurePerfil.NavigationEventPerfil
import com.example.agroagil.Menu.ui.featurePerfil.ui.VerDatosDelPerfil
import com.example.agroagil.ui.theme.AgroAgilTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint //habilita la inyección de dependecias para el main activity
class MainActivity : ComponentActivity() {

    //estoy inyectando el view model en el main activity y no necesito crear isntancia a mano
    private val viewModelMenu: MenuViewModel by viewModels() //prepara viewmodel y se lo inyecta

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContent {
            AgroAgilTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //se inicia el menú cerrado
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    // se crea una corrutina
                    val scope = rememberCoroutineScope()
                    //esto es para probar navegacion
                    val navControllerPrueba: NavHostController = rememberNavController()
                    val destinoPrincipal: String = "miMenu"
                    //para manejar navegación
                    NavHost(
                        navController = navControllerPrueba, //aca se pone estado
                        startDestination = destinoPrincipal // indicar dónde empieza la actividad
                    ) {
                        //se le pasa el evento de navegación como parametro al menú para que se pueda acceder
                        // a una pantalla u otra según el botón que se va a apretar en el composable
                        composable("miMenu") {
                            Menu(scope, drawerState, viewModelMenu) { event ->
                                // Observador de eventos de navegación
                                when (event) {
                                    //navegar a perfil
                                    NavigationEventMenu.ToConfigPerfil-> {
                                        navControllerPrueba.navigate("miPerfil")
                                    }
                                    //navegar a notificaciones
                                    NavigationEventMenu.ToNotificaciones-> {
                                        navControllerPrueba.navigate("misNotificaciones")
                                    }
                                    //navegar a configurar granja
                                    NavigationEventMenu.ToConfigGranja-> {
                                        navControllerPrueba.navigate("farm")
                                    }
                                    //navegar a cultivos
                                    NavigationEventMenu.ToMisCultivos-> {
                                        navControllerPrueba.navigate("misCultivos")
                                    }
                                    //navegar a misTareas
                                    NavigationEventMenu.ToMisTareas-> {
                                        navControllerPrueba.navigate("misTareas")
                                    }
                                    //navegar a mi almacen
                                    NavigationEventMenu.ToMiAlmacen-> {
                                        navControllerPrueba.navigate("miAlmacen")
                                    }
                                    //navegar a prestamos de articulos
                                    NavigationEventMenu.ToPrestamosArticulos -> {
                                        navControllerPrueba.navigate("prestamosArticulos")
                                    }
                                    //navegar a mis ventas
                                    NavigationEventMenu.ToMisVentas-> {
                                        navControllerPrueba.navigate("misVentas")
                                    }
                                    //navegar a mis compras
                                    NavigationEventMenu.ToMisCompras-> {
                                        navControllerPrueba.navigate("misCompras")
                                    }
                                    //navegar a mi resumen
                                    NavigationEventMenu.ToMiResumen-> {
                                        navControllerPrueba.navigate("miResumen")
                                    }
                                }
                            }
                        }

                        composable("miPerfil") {
                            VerDatosDelPerfil() { evento ->
                                when (evento) {
                                    NavigationEventPerfil.ToEditPerfil ->
                                        navControllerPrueba.navigate("editarPerfil")

                                    NavigationEventPerfil.ToPantallaPrincipal ->
                                        navControllerPrueba.navigate("miMenu")
                                }
                            }
                        }

                        composable("farm"){
                            Farm(FarmViewModel())
                        }
                        composable("loan") {
                            LoanScreen(loanViewModel = loanViewModel, navController = navController)
                        }
                        composable("loan/add") {
                            LoanAddScreen(loanViewModel = loanViewModel, navController = navController)
                        }
                        composable("loan/{loanId}/info", arguments = listOf(navArgument("loanId") { type = NavType.IntType })){
                                backStackEntry ->
                            val loanId: Int? = backStackEntry.arguments?.getInt("loanId")
                            if (loanId is Int)
                                LoanInfoScreen(navController = navController,loanViewModel = loanViewModel, loanId)
                        }
                        composable("loan/{loanId}/edit", arguments = listOf(navArgument("loanId") { type = NavType.IntType })){
                                backStackEntry ->
                            val loanId: Int? = backStackEntry.arguments?.getInt("loanId")
                            if (loanId is Int)
                                LoanEditScreen(navController = navController,loanViewModel = loanViewModel, loanId)
                        }




                        composable("editarPerfil"){/*composable, pantalla, contenido de la pantalla perfil*/}
                        composable("misNotificaciones") { /* composable, pantalla o Contenido de la pantalla mis notificaciones */ }
                        composable("miGranja") { GranjaDePrueba() }
                        composable("misCultivos") { /* composable, pantalla o Contenido de mis cultivos */ }
                        composable("misTareas") { /* composable, pantalla o  Contenido de mis tareas*/ }
                        composable("miAlmacen") { /* composable, pantalla o Contenido de mi almacen  */ }
                        composable("prestamosArticulos"){ /* composable, pantalla o Contenido de la pantalla de prestamos de articulos*/ }
                        composable("misVentas"){ /* composable, pantalla o Contenido de la pantalla mis ventas */ }
                        composable("misCompras"){ /* composable, pantalla o Contenido de la pantalla mis compras */ }
                        composable("miResumen"){ /* composable, pantalla o coentenido de la pantalla mi resumen */}


                    }




                    }
            }
        }
    }
}

