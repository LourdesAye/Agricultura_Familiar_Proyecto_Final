package com.example.agroagil.Task.data

import com.example.agroagil.Task.data.firebase.TaskFirebaseService
import com.example.agroagil.Task.model.Task
import com.example.agroagil.Task.model.TaskForAddScreen
import com.example.agroagil.Task.model.TaskCardData


//Obtiene las tareas de firebase si hay conexión a internet
class TaskRepository {
    val firebaseApi = TaskFirebaseService()

    /**
     * GET - obtiene todas las tareas para un usuario de id userId
     * @param userId id del usuario
     */
    suspend fun getTaskCardsForUser(userId: Int): List<TaskCardData> {
        return firebaseApi.getTaskCardsForUser(userId)
    }

    /**
     * PUT - Actualiza el estado de completitud de una tarea, seteando un booleano en el campo "completed"
     * @return Un booleano que indica si la operación fue exitosa
     */
    suspend fun editCompletedFieldOfTask(newStatus: Boolean, userId: Int, taskId: String): Boolean {
        return firebaseApi.editCompletedFieldOfTask(newStatus, userId, taskId)
    }

    /**
     * GET top N tareas por fecha descendente
     */
    suspend fun getNLastTaskCardsForUser(userId: Int, numberTasks: Int): List<TaskCardData> {
        return firebaseApi.getNLastTaskCardsForUser(userId, numberTasks)
    }

    /**
     * POST - Agregar nueva tarea
     */
    suspend fun addNewTaskForUser(newTask: Task, userId: Int): Boolean {
         return firebaseApi.addNewTaskForUser(newTask, userId)
    }

    /**
     * GET tarea full con todos los datos
     */
    suspend fun getTaskForUser(userId: Int, taskId: Int): Task {
        return getTaskForUser(userId, taskId)
    }
}