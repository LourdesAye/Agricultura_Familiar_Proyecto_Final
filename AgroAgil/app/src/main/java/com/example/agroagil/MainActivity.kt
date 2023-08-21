package com.example.agroagil
import MainScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agroagil.ui.Farm.Farm
import com.example.agroagil.ui.Farm.FarmViewModel
import com.example.agroagil.ui.Loan.LoanAddScreen
import com.example.agroagil.ui.Loan.LoanScreen
import com.example.agroagil.ui.Loan.LoanViewModel
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
                    NavHost(navController = navController, startDestination = "loan") {
                        composable("loan") {
                            LoanScreen(loanViewModel = LoanViewModel(), navController = navController)
                        }
                        composable("loan/add") {
                            LoanAddScreen(loanViewModel = LoanViewModel(), navController = navController)
                        }
                    }
                    Greeting("Android", model=LoanViewModel(), navController = navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, model: LoanViewModel = LoanViewModel(), navController: NavController = rememberNavController()) {
    navController.navigate("loan")
    //LoanScreen(loanViewModel = model, navController = navController)
    //Farm(model)
    //DateRangeSelector()
    //MainScreen()
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AgroAgilTheme {
        Greeting("Android")
    }
}