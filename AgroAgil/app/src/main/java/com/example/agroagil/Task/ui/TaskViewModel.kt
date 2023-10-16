package com.example.agroagil.Task.ui

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agroagil.Task.data.TaskRepository
import com.example.agroagil.Task.model.AppliedFiltersForTasks
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.Task.model.TaskFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    TaskCardData(111,"Sembrar choclo", "2023-10-07T15:30:00-03:00", 3, true, false),
    TaskCardData(112,"Fertilizar campo", "2023-10-07T15:30:00-03:00",4, false, false),
    TaskCardData(113,"Poda de árboles", "2023-10-07T15:30:00-03:00",5, true, false),
    TaskCardData(114,"Riego semanal", "2023-10-07T15:30:00-03:00",6, false, true),
    TaskCardData(115,"Revisar plagas", "2023-10-07T15:30:00-03:00",7, true, true),
    TaskCardData(116,"Rotar cultivos", "2023-12-07T15:30:00-03:00",8, false, true),
    TaskCardData(117,"Cosechar tomate", "2023-10-07T15:30:00-03:00",9, true, true),
    TaskCardData(118,"Venta de productos", "2023-12-07T15:30:00-03:00",4, false, false),
    TaskCardData(119,"Limpiar equipo", "2023-10-07T15:30:00-03:00",5, true, false),
    TaskCardData(120,"Actualizar inventario", "2023-12-07T15:30:00-03:00",6, false, true),
    TaskCardData(121,"Reparar tractor", "2023-10-07T15:30:00-03:00",7, true, true),
    TaskCardData(122,"Vacunar ganado", "2023-12-07T15:30:00-03:00",8, false, true),
    TaskCardData(123,"Comprar semillas", "2023-10-07T15:30:00-03:00",9, true, true)
)

class TaskViewModel: ViewModel() {
    val taskRepository = TaskRepository()

    private val _appliedFiltersForTasks = MutableLiveData<AppliedFiltersForTasks>(
        TASKS_FILTERS_DEFAULT_VALUES
    )

    val appliedFiltersForTasks : LiveData<AppliedFiltersForTasks> = _appliedFiltersForTasks

    private val _taskCardDataList = MutableLiveData<List<TaskCardData>?>()
    val taskCardDataList: LiveData<List<TaskCardData>?> = _taskCardDataList

    init {
        refreshTaskCardsLiveData(0)
    }

    private fun refreshTaskCardsLiveData(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realValue = taskRepository.getTaskCardsForUser(userId)
                _taskCardDataList.postValue(realValue)
            } catch (e: Exception) {
                // Handle exception, e.g., log it
                _taskCardDataList.postValue(null)
            }
        }
    }

    /**
     * Actualiza el atributo "completed" de la tarea en firebase
     */
    suspend fun toggleTaskCompletedStatus(taskCard: TaskCardData) {
        val updated = taskRepository.editCompletedFieldOfTask(newStatus = !taskCard.completed, userId = 0, taskId = taskCard.id)
        if(updated)
            refreshTaskCardsLiveData(0)
    }

    /**
     * En el filtering box hay varios botones para aplicar filtros en la lista de tareas.
     * TaskFilter representa una opción de filtro seleccionada. AppliedFiltersForTasks tiene todos
     * los valores de filtros elegidos, como booleanos
     */
    fun onFilteringBoxChange(taskFilter: TaskFilter) {
        val currentTaskFilters = _appliedFiltersForTasks.value ?: return
        // Create a copy of the AppliedFiltersForTasks object and updated the corresponding status
        _appliedFiltersForTasks.postValue(currentTaskFilters.copyWithUpdatedFilter(taskFilter))
    }

    /**
     * Obtiene el color para un botón de filtrado
     */
    fun getFilterColor(taskFilter: TaskFilter): Color? {
        when(taskFilter) {
            is TaskFilter.ByOverdue, TaskFilter.ByToday, TaskFilter.ByNext -> return  null
            is TaskFilter.ByLow -> return Color(INCOMPLETE_NORMAL_TASK_TEXT_COLOR.toColorInt())
            is TaskFilter.ByHigh -> return Color(INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR.toColorInt())
            is TaskFilter.ByDone -> return Color(COMPLETED_TASK_TEXT_COLOR.toColorInt())
        }
    }
}
