package com.example.agroagil.Task.data.firebase

import com.example.agroagil.Task.model.Task
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.Task.model.Tasks
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TaskFirebaseService {
private val TASK_PATH = "task/"

    /**
     * GET - obtiene todas las tareas para un usuario de id userId
     * @param userId id del usuario
     */
    suspend fun getTaskCardsForUser(userId: Int) {
        try {
            suspendCancellableCoroutine<List<TaskCardData>> { continuation ->
                Firebase.database.getReference("$TASK_PATH$userId").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Tasks::class.java) as Tasks
                    continuation.resume(value.tasks)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        } catch (e: Exception) {
            // Handle exception if needed
            println("Firebase: Error from getTaskCardsForUser.")
            e.printStackTrace()
        }
    }

    //PUT check a tarea
    fun editCompletedFieldOfTask(newStatus: Boolean, userId: Int, taskId: Int) {
        // Reference to the 'completed' field of the specific task
        val taskCompletionStatusRef = Firebase.database.getReference("$TASK_PATH$userId/$taskId/completed")

        // Update the value
        taskCompletionStatusRef.setValue(newStatus)
            .addOnSuccessListener {
                // Successfully updated
            }
            .addOnFailureListener { exception ->
                // Handle failure
                // For example, you could log the exception
            }
    }

    //GET tareas filtradas


    //GET top 5 tareas por fecha descendente

    //POST - Agregar nueva tarea
    fun addNewTaskForUser(newTask: Task, userId: Int) {
        val databaseReference = Firebase.database.getReference("$TASK_PATH$userId")

        // Generate a unique key for the new task and set its value
        val newTaskReference = databaseReference.push()
        newTaskReference.setValue(newTask)
    }


}