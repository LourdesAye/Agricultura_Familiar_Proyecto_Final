package com.example.agroagil
import BuyAddScreen
import BuyInfoScreen
import SellAddScreen
import SellInfoScreen
import SellScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.agroagil.Buy.ui.BuyScreen
import com.example.agroagil.Buy.ui.BuyViewModel
import com.example.agroagil.Cultivo.ui.Cultivo
import com.example.agroagil.Farm.ui.CultivoViewModel
import com.example.agroagil.Farm.ui.Farm
import com.example.agroagil.Farm.ui.FarmViewModel
import com.example.agroagil.Loan.ui.LoanAddScreen
import com.example.agroagil.Loan.ui.LoanEditScreen
import com.example.agroagil.Loan.ui.LoanInfoScreen
import com.example.agroagil.Loan.ui.LoanScreen
import com.example.agroagil.Loan.ui.LoanViewModel
import com.example.agroagil.Menu.ui.featureMenu.menu.ui.Menu
import com.example.agroagil.Sell.ui.SellViewModel
import com.example.agroagil.ui.theme.AgroAgilTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.lourd.myapplication.featureMenu.NavigationEventMenu
import com.lourd.myapplication.featureMenu.menu.ui.MenuViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val titleCurrentPage = mutableStateOf("Inicio")
        setContent {
            AgroAgilTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FirebaseApp.initializeApp(LocalContext.current)
                    Firebase.database.setPersistenceEnabled(true)
                    val navController = rememberNavController()
                    val loanViewModel = LoanViewModel()
                    val viewModelMenu: MenuViewModel by viewModels()
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val sellViewModel = SellViewModel()
                    val buyViewModel = BuyViewModel()
                    val farmViewModel = FarmViewModel()
                    val cultivoViewModel = CultivoViewModel()
                    NavHost(navController = navController, startDestination = "loan") {
                        composable("farm"){
                            titleCurrentPage.value="Mi campo"
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),true, navController) {
                                Farm(farmViewModel)
                            }
                        }
                        composable("loan") {
                            titleCurrentPage.value="Mis prestamos"
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController), true, navController) {
                                LoanScreen(
                                    loanViewModel = loanViewModel,
                                    navController = navController
                                )
                            }
                        }
                        composable("loan/add") {
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),false, navController,
                                {LoanAddScreen(loanViewModel = loanViewModel, navController = navController)})
                        }
                        composable("loan/{loanId}/info", arguments = listOf(navArgument("loanId") { type = NavType.IntType })){
                                backStackEntry ->
                            val loanId: Int? = backStackEntry.arguments?.getInt("loanId")
                            if (loanId is Int)
                                Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),false, navController,
                                    {LoanInfoScreen(navController = navController,loanViewModel = loanViewModel, loanId)})
                        }
                        composable("loan/{loanId}/edit", arguments = listOf(navArgument("loanId") { type = NavType.IntType })){
                                backStackEntry ->
                            val loanId: Int? = backStackEntry.arguments?.getInt("loanId")
                            if (loanId is Int)
                                Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),false, navController,
                                {LoanEditScreen(navController = navController,loanViewModel = loanViewModel, loanId)})
                        }
                        composable("home") {
                            titleCurrentPage.value = "Inicio"
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),true, navController, {/*aca iria la funcion de home*/})
                        }
                        composable("sell") {
                            titleCurrentPage.value="Mis ventas"
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),true, navController) {
                                SellScreen(
                                    sellViewModel = sellViewModel,
                                    navController = navController
                                )
                            }
                        }
                        composable("sell/add") {
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),false, navController,
                                {SellAddScreen(sellViewModel = sellViewModel, navController = navController)})
                        }
                        composable("sell/{sellId}/info", arguments = listOf(navArgument("sellId") { type = NavType.IntType })){
                                backStackEntry ->
                            val sellId: Int? = backStackEntry.arguments?.getInt("sellId")
                            if (sellId is Int)
                                Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),false, navController,
                                    {SellInfoScreen(navController = navController,sellViewModel = sellViewModel, sellId)})
                        }
                        composable("buy") {
                            titleCurrentPage.value="Mis compras"
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController), true, navController) {
                                BuyScreen(buyViewModel = buyViewModel, navController = navController)
                            }
                        }
                        composable("buy/add") {
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),false, navController,
                                {BuyAddScreen(buyViewModel = buyViewModel, navController = navController)})
                        }
                        composable("buy/{buyId}/info", arguments = listOf(navArgument("buyId") { type = NavType.IntType })){
                                backStackEntry ->
                            val buyId: Int? = backStackEntry.arguments?.getInt("buyId")
                            if (buyId is Int)
                                Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),false, navController,
                                    {BuyInfoScreen(navController = navController,buyViewModel = buyViewModel, buyId)})
                        }

                        composable("cultivo") {
                            titleCurrentPage.value="Cultivos_"
//                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController), true, navController) {
//                                LoanScreen(
//                                    loanViewModel = loanViewModel,
////                                    loanViewModel = loanViewModel,
//                                    navController = navController
//                                )
//                            }
                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),true, navController) {
//                                Farm(farmViewModel)
                                Cultivo(cultivoViewModel)
                            }
                        }
//                        composable("farm"){
//                            titleCurrentPage.value="Mi campo"
//                            Menu(scope, drawerState, viewModelMenu, title=titleCurrentPage,NavigationEventFunction(navController),true, navController) {
//                                Farm(farmViewModel)
//                            }
//                        }
                    }
                    Greeting("Android", model= LoanViewModel(), navController = navController)
                }
            }
        }
    }
}

fun NavigationEventFunction(navController: NavController): (event: NavigationEventMenu) ->Unit{
    return{event ->
        // Observador de eventos de navegación
        when (event) {
            //navegar a perfil
            NavigationEventMenu.ToConfigPerfil -> {
                navController.navigate("home")
            }
            //navegar a notificaciones
            NavigationEventMenu.ToNotificaciones -> {
                navController.navigate("home")
            }
            //navegar a configurar granja
            NavigationEventMenu.ToConfigGranja -> {
                navController.navigate("farm")
            }
            //navegar a cultivos
            NavigationEventMenu.ToMisCultivos -> {
                navController.navigate("cultivo")
            }
            //navegar a misTareas
            NavigationEventMenu.ToMisTareas -> {
                navController.navigate("home")
            }
            //navegar a mi almacen
            NavigationEventMenu.ToMiAlmacen -> {
                navController.navigate("home")
            }
            //navegar a prestamos de articulos
            NavigationEventMenu.ToPrestamosArticulos -> {
                navController.navigate("loan")
            }
            //navegar a mis ventas
            NavigationEventMenu.ToMisVentas -> {
                navController.navigate("sell")
            }
            //navegar a mis compras
            NavigationEventMenu.ToMisCompras -> {
                navController.navigate("buy")
            }
            //navegar a mi resumen
            NavigationEventMenu.ToMiResumen -> {
                navController.navigate("home")
            }
            NavigationEventMenu.ToHome -> {
                navController.navigate("home")
            }
            else -> {}
    }}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, model: LoanViewModel = LoanViewModel(), navController: NavController = rememberNavController()) {
    navController.navigate("home")
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AgroAgilTheme {
        Greeting("Android")
    }
}