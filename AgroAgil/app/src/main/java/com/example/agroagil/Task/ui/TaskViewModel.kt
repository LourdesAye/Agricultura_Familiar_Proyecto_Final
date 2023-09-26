package com.example.agroagil.Task.ui

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agroagil.Task.model.TaskCardData
import java.util.Calendar

const val COMPLETED_TASK_CARD_COLOR = "#E2F2F2"
const val INCOMPLETE_IMPORTANT_TASK_CARD_COLOR = "#FAE9E8"
const val INCOMPLETE_NORMAL_TASK_CARD_COLOR = "#E9F0F8"

const val COMPLETED_TASK_TEXT_COLOR = "#38B9BC"
const val INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR= "#E73226"
const val INCOMPLETE_NORMAL_TASK_TEXT_COLOR = "#5B92E3"

val TASKS_FILTERS_DEFAULT_VALUES = AppliedFiltersForTasks(
    false, false,
    false, false,
    false, false
)

val TASK_CARD_DATA_LIST_MOCK = listOf(
    TaskCardData(111,"Sembrar choclo", Calendar.getInstance().time, 3, true, false),
    TaskCardData(112,"Fertilizar campo", Calendar.getInstance().time, 4, false, false),
    TaskCardData(113,"Poda de árboles", Calendar.getInstance().time, 5, true, false),
    TaskCardData(114,"Riego semanal", Calendar.getInstance().time, 6, false, true),
    TaskCardData(115,"Revisar plagas", Calendar.getInstance().time, 7, true, true),
    TaskCardData(116,"Rotar cultivos", Calendar.getInstance().time, 8, false, true),
    TaskCardData(117,"Cosechar tomate", Calendar.getInstance().time, 9, true, true),
    TaskCardData(118,"Venta de productos", Calendar.getInstance().time, 4, false, false),
    TaskCardData(119,"Limpiar equipo", Calendar.getInstance().time, 5, true, false),
    TaskCardData(120,"Actualizar inventario", Calendar.getInstance().time, 6, false, true),
    TaskCardData(121,"Reparar tractor", Calendar.getInstance().time, 7, true, true),
    TaskCardData(122,"Vacunar ganado", Calendar.getInstance().time, 8, false, true),
    TaskCardData(123,"Comprar semillas", Calendar.getInstance().time, 9, true, true)
)

class TaskViewModel: ViewModel() {
    private val _taskCardData = MutableLiveData<TaskCardData>(TaskCardData(111,"Cosechar tomates", Calendar.getInstance().time, 3, true, false))
    val taskCardData : LiveData<TaskCardData> = _taskCardData

    //Mock
    private val _taskCardDataList = MutableLiveData<List<TaskCardData>>(
        TASK_CARD_DATA_LIST_MOCK
    )

    val taskCardDataList: LiveData<List<TaskCardData>> = _taskCardDataList

    //Filter state by date


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


    private val _filterTasksBy = MutableLiveData<AppliedFiltersForTasks>(
        TASKS_FILTERS_DEFAULT_VALUES
    )
    val filterTasksBy : LiveData<AppliedFiltersForTasks> = _filterTasksBy



    fun onFilteringBoxChange(taskFilter: TaskFilter) {
        val currentTaskFilters = _filterTasksBy.value ?: return
        // Create a copy of the AppliedFiltersForTasks object and updated the corresponding status
        var updatedTaskFilters: AppliedFiltersForTasks = currentTaskFilters.copy()
        when(taskFilter) {
            is TaskFilter.ByOverdue -> updatedTaskFilters.filterByOverdue = !currentTaskFilters.filterByOverdue
            is TaskFilter.ByToday -> updatedTaskFilters.filterByToday = !currentTaskFilters.filterByToday
            is TaskFilter.ByNext -> updatedTaskFilters.filterByNext = !currentTaskFilters.filterByNext
            is TaskFilter.ByLow -> updatedTaskFilters.filterByLow = !currentTaskFilters.filterByLow
            is TaskFilter.ByHigh -> updatedTaskFilters.filterByHigh = !currentTaskFilters.filterByHigh
            is TaskFilter.ByDone -> updatedTaskFilters.filterByDone = !currentTaskFilters.filterByDone
        }
        _filterTasksBy.postValue(updatedTaskFilters)
    }
}

sealed class TaskFilter(val name: String) {
    object ByOverdue: TaskFilter("Atrasadas")
    object ByToday: TaskFilter("Hoy")
    object ByNext: TaskFilter("Futuras")
    object ByLow: TaskFilter("Baja")
    object ByHigh: TaskFilter("Alta")
    object ByDone: TaskFilter("Hechas")
}

data class AppliedFiltersForTasks(
    var filterByOverdue: Boolean,
    var filterByToday: Boolean,
    var filterByNext: Boolean,
    var filterByLow: Boolean,
    var filterByHigh: Boolean,
    var filterByDone: Boolean,
) {
    fun getFilterValue(taskFilter: TaskFilter): Boolean {
        when(taskFilter) {
            is TaskFilter.ByOverdue -> return filterByOverdue
            is TaskFilter.ByToday -> return filterByToday
            is TaskFilter.ByNext -> return filterByNext
            is TaskFilter.ByLow -> return filterByLow
            is TaskFilter.ByHigh -> return filterByHigh
            is TaskFilter.ByDone -> return filterByDone
        }
    }

    fun getFilterColor(taskFilter: TaskFilter): Color? {
        when(taskFilter) {
            is TaskFilter.ByOverdue, TaskFilter.ByToday, TaskFilter.ByNext -> return  null
            is TaskFilter.ByLow -> return  Color(INCOMPLETE_NORMAL_TASK_TEXT_COLOR.toColorInt())
            is TaskFilter.ByHigh -> return  Color(INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR.toColorInt())
            is TaskFilter.ByDone -> return  Color(COMPLETED_TASK_TEXT_COLOR.toColorInt())
        }
    }

    fun noFilterApplied(): Boolean {
        return noDateFilterApplied() && noPriorityFiolterApplied()
    }

    fun noDateFilterApplied(): Boolean {
        return !(filterByOverdue || filterByToday || filterByNext)
    }

    fun noPriorityFiolterApplied(): Boolean {
        return !(filterByLow || filterByHigh || filterByDone)
    }

}
