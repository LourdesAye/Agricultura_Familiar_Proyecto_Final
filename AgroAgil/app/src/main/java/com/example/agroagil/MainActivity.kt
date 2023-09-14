package com.example.agroagil
import SellAddScreen
import SellInfoScreen
import SellScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.agroagil.Farm.ui.Farm
import com.example.agroagil.Farm.ui.FarmViewModel
import com.example.agroagil.Loan.ui.LoanAddScreen
import com.example.agroagil.Loan.ui.LoanEditScreen
import com.example.agroagil.Loan.ui.LoanInfoScreen
import com.example.agroagil.Loan.ui.LoanScreen
import com.example.agroagil.Loan.ui.LoanViewModel
import com.example.agroagil.Sell.ui.SellViewModel
import com.example.agroagil.ui.theme.AgroAgilTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val FarmViewModel by viewModels<FarmViewModel>()
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
                    val sellViewModel = SellViewModel()
                    NavHost(navController = navController, startDestination = "loan") {
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
                        composable("sell") {
                            SellScreen(sellViewModel = sellViewModel, navController = navController)
                        }
                        composable("sell/add") {
                            SellAddScreen(sellViewModel = sellViewModel, navController = navController)
                        }
                        composable("sell/{sellId}/info", arguments = listOf(navArgument("sellId") { type = NavType.IntType })){
                                backStackEntry ->
                            val sellId: Int? = backStackEntry.arguments?.getInt("sellId")
                            if (sellId is Int)
                                SellInfoScreen(navController = navController,sellViewModel = sellViewModel, sellId)
                        }
                    }
                    Greeting("Android", model= LoanViewModel(), navController = navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, model: LoanViewModel = LoanViewModel(), navController: NavController = rememberNavController()) {
    navController.navigate("sell")
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AgroAgilTheme {
        Greeting("Android")
    }
}