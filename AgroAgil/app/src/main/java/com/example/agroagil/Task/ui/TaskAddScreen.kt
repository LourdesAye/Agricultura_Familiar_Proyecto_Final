package com.example.agroagil.Task.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@Composable
fun TaskAddScreen (taskViewModel: TaskViewModel, navController: NavController) {
    Text(text = "Agregar tarea...", fontWeight = FontWeight.Bold)
}