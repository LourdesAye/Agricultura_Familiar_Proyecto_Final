package com.example.agroagil.Task.data.firebase

import com.example.agroagil.Task.model.Task
import com.example.agroagil.Task.model.TaskForAddScreen
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
        return hashMapOfTasks.map { entry: Map.Entry<String, TaskCardData> ->  entry.value.copy(id = entry.key) }
    }


    /**
     * PUT - Actualiza el estado de completitud de una tarea, seteando un booleano en el campo "completed"
     * @return Un booleano que indica si la operación fue exitosa
     */
    suspend fun editCompletedFieldOfTask(newStatus: Boolean, userId: Int, taskId: String): Boolean {
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
    suspend fun addNewTaskForUser(newTask: Task, userId: Int): Boolean  {

        return suspendCancellableCoroutine { continuation ->
            val databaseReference = Firebase.database.getReference("$PARENT_TASK_PATH$userId$CHILD_TASK_LIST_PATH")

            // Generate a unique key for the new task and set its value
            val newTaskReference = databaseReference.push()
            newTaskReference.setValue(newTask)
                .addOnSuccessListener {
                    // The write was successful, invoke the callback with true
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    continuation.resume(false)
                }
        }
    }


    /**
     * GET tarea full con todos los datos
     */
    suspend fun getTaskForUser(userId: Int, taskId: String): Task {
        try {
            return suspendCancellableCoroutine { continuation ->
                Firebase.database.getReference("$PARENT_TASK_PATH$userId$CHILD_TASK_LIST_PATH$taskId").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Task::class.java) as Task
                    continuation.resume(value.copy(id = taskId))
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        } catch (e: Exception) {
            // Handle exception if needed
            println("Firebase: Error from getTaskForUser.")
            e.printStackTrace()
            return Task()
        }
    }

    /**
     * DELETE - Elimina una tarea específica dado un ID de tarea
     * @param userId id del usuario
     * @param taskId id de la tarea a eliminar
     * @return Un booleano que indica si la operación fue exitosa
     */
    suspend fun deleteTaskForUser(userId: Int, taskId: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val taskReference = Firebase.database.getReference("$PARENT_TASK_PATH$userId$CHILD_TASK_LIST_PATH$taskId")

            taskReference.removeValue()
                .addOnSuccessListener {
                    // The delete was successful, invoke the callback with true
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    // The delete failed, print the exception and resume the coroutine with false
                    exception.printStackTrace()
                    continuation.resume(false)
                }
        }
    }

    suspend fun updateTaskForUser(task: Task, userId: Int, taskId: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val taskReference = Firebase.database.getReference("$PARENT_TASK_PATH$userId$CHILD_TASK_LIST_PATH$taskId")

            taskReference.setValue(task)
                .addOnSuccessListener {
                    // The update was successful, invoke the callback with true
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    // The update failed, print the exception and resume the coroutine with false
                    exception.printStackTrace()
                    continuation.resume(false)
                }
        }
    }


}

