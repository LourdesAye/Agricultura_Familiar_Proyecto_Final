package com.example.agroagil.Task.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

/**
 * La pantalla de editar es la misma que la de agregar tarea, con la misma funcionalidad,
 * salvo que los campos empiezan con datos existentes y el guardado se hace en una tarea existente.
 * Por esta razón el viewModel TaskAddViewModel es el mismo en ambas pantallas
 */
@Composable
fun TaskEditScreen (taskEditViewModel: TaskAddViewModel, navController: NavController, taskId: String) {
    taskEditViewModel.getTaskToEdit(taskId)
    TaskAddScreen(taskViewModel = taskEditViewModel, navController = navController, editMode = true, taskToEditId = taskId)
}