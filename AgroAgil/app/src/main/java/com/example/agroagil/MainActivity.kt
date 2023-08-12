package com.example.agroagil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

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
import com.example.agroagil.ui.Farm.Farm
import com.example.agroagil.ui.Farm.FarmViewModel
import com.example.agroagil.ui.theme.AgroAgilTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
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
                    Greeting("Android", model=FarmViewModel())
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, model: FarmViewModel= FarmViewModel()) {
    Farm(model)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AgroAgilTheme {
        Greeting("Android")
    }
}