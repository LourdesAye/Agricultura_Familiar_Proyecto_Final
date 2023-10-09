package com.example.agroagil.Task.data

import com.example.agroagil.Task.data.firebase.TaskFirebaseService


//Obtiene las tareas de firebase si hay conexión a internet o de la base de datos si la app está en modo offline,
class TaskRepository {
    val firebaseApi = TaskFirebaseService()
    val OFFLINE = false //TODO: Obtener estado de conexión del dispositivo

    suspend fun getTaskCardsForUser(userId: Int) {
        if(!OFFLINE)
            return firebaseApi.getTaskCardsForUser(userId)
        //TODO: Else obtiene tareas de la base de datos
    }




}