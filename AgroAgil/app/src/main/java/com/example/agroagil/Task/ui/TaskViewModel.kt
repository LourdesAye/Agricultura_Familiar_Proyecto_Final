package com.example.agroagil.Task.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.core.models.Loan
import com.example.agroagil.core.models.Loans
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TaskViewModel: ViewModel() {
    private val _taskCardData = MutableLiveData<TaskCardData>(TaskCardData(111,"Cosechar tomates", Calendar.getInstance().time, 3, true, false))
    val taskCardData : LiveData<TaskCardData> = _taskCardData

    //Mock
    private val _taskCardDataList = MutableLiveData<List<TaskCardData>>(
        listOf(
            TaskCardData(111,"Cosechar tomates", Calendar.getInstance().time, 3, true, false),
            TaskCardData(112,"Plantar tomates", Calendar.getInstance().time, 4, false, false),
            TaskCardData(113,"Regar tomates", Calendar.getInstance().time, 5, true, false),
            TaskCardData(114,"Comprar tomates", Calendar.getInstance().time, 6, false, true),
            TaskCardData(115,"Vender tomates", Calendar.getInstance().time, 7, true, true),
            TaskCardData(116,"Prestar tomates", Calendar.getInstance().time, 8, false, true),
            TaskCardData(117,"Tomates", Calendar.getInstance().time, 9, true, true)
        )
    )

    val taskCardDataList: LiveData<List<TaskCardData>> = _taskCardDataList


    //Setea el check de completo en una card de tarea. Encuentra la tarea por Id
    fun toggleTaskCompletedStatus(taskId: Int) {
        val currentList = _taskCardDataList.value ?: return // Get the current list; return if it's null

        // Find the index of the task with the matching id
        val taskIndex = currentList.indexOfFirst { it.id == taskId }
        if (taskIndex == -1) return  // Return if no matching task found

        // Create a copy of the TaskCardData object with the "completed" status toggled
        val updatedTask = currentList[taskIndex].copy(completed = !currentList[taskIndex].completed)

        // Create a new list with the updated TaskCardData object
        val updatedList = currentList.toMutableList().apply {
            this[taskIndex] = updatedTask
        }

        // Post the new list back to LiveData
        _taskCardDataList.postValue(updatedList)
    }


/*
    var taskCardDataList = liveData(Dispatchers.IO) {
        emit(null)

        try {
//            val realValue = suspendCancellableCoroutine<List<TaskCardData>> { continuation ->
//                Firebase.database.getReference("task/0").get().addOnSuccessListener { snapshot ->
//                    val value = snapshot.getValue(Loans::class.java) as Loans
//                    continuation.resume(value.tasks)
//                }.addOnFailureListener { exception ->
//                    continuation.resumeWithException(exception)
//                }
//            }
//            emit(realValue)
        } catch (e: Exception) {
            // Handle exception if needed
        }
    }*/

    // var _taskCardData =  TaskCardData("Cosechar tomates", Calendar.getInstance().time, 3, true, false)



}