package com.example.agroagil.Task.data.firebase

import androidx.compose.runtime.snapshots.Snapshot
import com.example.agroagil.Task.model.Task
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.Task.model.Tasks
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val _COMPLETED = "/completed"
private const val PARENT_TASK_PATH = "task/"
private const val CHILD_TASK_LIST_PATH = "/tasks/"

class TaskFirebaseService {

    /**
     * GET - obtiene todas las tareas para un usuario de id userId
     * @param userId id del usuario
     */
    suspend fun getTaskCardsForUser(userId: Int): List<TaskCardData> {
        try {
            return suspendCancellableCoroutine { continuation ->
                Firebase.database.getReference("$PARENT_TASK_PATH$userId")
                    .orderByChild("isoDate")
                    .get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Tasks::class.java) as Tasks
                    continuation.resume(hashMapToListofTasks(value.tasks))
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        } catch (e: Exception) {
            // Handle exception if needed
            println("Firebase: Error from getTaskCardsForUser.")
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * Este método se creó porque las tareas llegaban de firebase como un hashmap con key idTarea y
     * value la tarea en sí.
     * @return Una liesta de TaskCardData con id igual a la key del hashmap
     */
    private fun hashMapToListofTasks(hashMapOfTasks: HashMap<String, TaskCardData>): List<TaskCardData> {
        return hashMapOfTasks.map { entry: Map.Entry<String, TaskCardData> ->  entry.value.copy(id = entry.key.toInt()) }
    }


    /**
     * PUT - Actualiza el estado de completitud de una tarea, seteando un booleano en el campo "completed"
     * @return Un booleano que indica si la operación fue exitosa
     */
    suspend fun editCompletedFieldOfTask(newStatus: Boolean, userId: Int, taskId: Int): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val taskCompletionStatusRef = Firebase.database.getReference("$PARENT_TASK_PATH$userId$CHILD_TASK_LIST_PATH$taskId$_COMPLETED")

            taskCompletionStatusRef.setValue(newStatus)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    continuation.resumeWithException(exception)
                }
        }
    }

    /**
     * GET top N tareas por fecha descendente
     */
    suspend fun getNLastTaskCardsForUser(userId: Int, numberTasks: Int): List<TaskCardData> {
        try {
            return suspendCancellableCoroutine<List<TaskCardData>> { continuation ->
                Firebase.database.getReference("$PARENT_TASK_PATH$userId")
                    .orderByChild("isoDate")
                    .limitToLast(numberTasks)
                    .get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Tasks::class.java) as Tasks
                    continuation.resume(hashMapToListofTasks(value.tasks))
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        } catch (e: Exception) {
            // Handle exception if needed
            println("Firebase: Error from getNLastTaskCardsForUser.")
            e.printStackTrace()
            return emptyList()
        }
    }

    /**
     * POST - Agregar nueva tarea
     */
    fun addNewTaskForUser(newTask: Task, userId: Int) {
        val databaseReference = Firebase.database.getReference("$PARENT_TASK_PATH$userId")

        // Generate a unique key for the new task and set its value
        val newTaskReference = databaseReference.push()
        newTaskReference.setValue(newTask)
    }

    /**
     * GET tarea full con todos los datos
     */
    suspend fun getTaskForUser(userId: Int, taskId: Int): Task {
        try {
            return suspendCancellableCoroutine { continuation ->
                Firebase.database.getReference("$PARENT_TASK_PATH$userId$CHILD_TASK_LIST_PATH$taskId").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Task::class.java) as Task
                    continuation.resume(value)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        } catch (e: Exception) {
            // Handle exception if needed
            println("Firebase: Error from getTaskForUser.")
            e.printStackTrace()
            return Task(calendarDate = null)
        }
    }
}

