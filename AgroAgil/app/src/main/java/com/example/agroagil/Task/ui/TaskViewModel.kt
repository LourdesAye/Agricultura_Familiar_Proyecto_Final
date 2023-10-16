package com.example.agroagil.Task.ui

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agroagil.Task.data.TaskRepository
import com.example.agroagil.Task.model.AppliedFiltersForTasks
import com.example.agroagil.Task.model.Task
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.Task.model.TaskFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

const val COMPLETED_TASK_CARD_COLOR = "#E2F2F2"
const val INCOMPLETE_IMPORTANT_TASK_CARD_COLOR = "#FAE9E8"
const val INCOMPLETE_NORMAL_TASK_CARD_COLOR = "#E9F0F8"

const val COMPLETED_TASK_TEXT_COLOR = "#38B9BC"
const val INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR = "#E73226"
const val INCOMPLETE_NORMAL_TASK_TEXT_COLOR = "#5B92E3"


class TaskViewModel : ViewModel() {
    val taskRepository = TaskRepository()

    private val TASKS_FILTERS_DEFAULT_VALUES = AppliedFiltersForTasks(
        false, false,
        false, false,
        false, false
    )

    private val _appliedFiltersForTasks = MutableLiveData<AppliedFiltersForTasks>(
        TASKS_FILTERS_DEFAULT_VALUES
    )

    val appliedFiltersForTasks: LiveData<AppliedFiltersForTasks> = _appliedFiltersForTasks

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
        val updated = taskRepository.editCompletedFieldOfTask(
            newStatus = !taskCard.completed,
            userId = 0,
            taskId = taskCard.id
        )
        if (updated)
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
        when (taskFilter) {
            is TaskFilter.ByOverdue, TaskFilter.ByToday, TaskFilter.ByNext -> return null
            is TaskFilter.ByLow -> return Color(INCOMPLETE_NORMAL_TASK_TEXT_COLOR.toColorInt())
            is TaskFilter.ByHigh -> return Color(INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR.toColorInt())
            is TaskFilter.ByDone -> return Color(COMPLETED_TASK_TEXT_COLOR.toColorInt())
        }
    }

    //Pantalla para crear una nueva tarea -----------------------------------
    private val _taskToCreate = MutableLiveData<Task>(Task(calendarDate = null))
    val taskToCreate: LiveData<Task> = _taskToCreate

    fun onDescriptionChange(description: String) {
        val currentTaskToCreate = _taskToCreate.value ?: return
        _taskToCreate.postValue(currentTaskToCreate.copy(description = description))
    }

    private val _dateSelectedString = MutableLiveData<String>("Definir fecha de la tarea")
    val dateSelectedString: LiveData<String> = _dateSelectedString

    private val _timeSelectedString = MutableLiveData<String>("Definir hora de la tarea")
    val timeSelectedString: LiveData<String> = _timeSelectedString


    fun onDateChange(timestamp: Long?) {
        if (timestamp == null)
            return

        var calendarFromTimestamp = Calendar.getInstance()
        calendarFromTimestamp.timeInMillis = timestamp // Convert time to Calendar object

        val currentTaskToCreate = _taskToCreate.value ?: return
        val currentDate = currentTaskToCreate.calendarDate

        if (currentDate != null) {
            calendarFromTimestamp.set(Calendar.HOUR_OF_DAY, currentDate.get(Calendar.HOUR_OF_DAY))
            calendarFromTimestamp.set(Calendar.MINUTE, currentDate.get(Calendar.MINUTE))
        }

        val updatedTask = currentTaskToCreate.copy(calendarDate = calendarFromTimestamp)
        _taskToCreate.postValue(updatedTask)
        _dateSelectedString.postValue("El día ${updatedTask.getTaskFormatDate()}")
    }

    fun onTimeChange(hour: Int, minute: Int) {
        val currentTaskToCreate = _taskToCreate.value ?: return
        var currentDate = currentTaskToCreate.calendarDate
        if (currentDate == null)
            currentDate = Calendar.getInstance()

        currentDate!!.set(Calendar.HOUR_OF_DAY, hour)
        currentDate.set(Calendar.MINUTE, minute)
        currentDate.isLenient = false

        val updatedTask = currentTaskToCreate.copy(calendarDate = currentDate)
        _taskToCreate.postValue(updatedTask)
        _timeSelectedString.postValue("A las ${updatedTask.getTaskFormatTime()} horas")
    }

    fun onEstimationChange(estimationText: String) {
        val currentTaskToCreate = _taskToCreate.value ?: return
        if (estimationText.isDigitsOnly()) {
            val estimation = estimationText.toInt()
            val updatedTask = currentTaskToCreate.copy(durationHours = estimation)
            _taskToCreate.postValue(updatedTask)
        }
    }

    fun onLocationInFarmChange(location: String) {
        val currentTaskToCreate = _taskToCreate.value ?: return

        val updatedTask = currentTaskToCreate.copy(locationInFarm = location)
        _taskToCreate.postValue(updatedTask)
    }

    fun onResponsiblesChange() {
        val currentTaskToCreate = _taskToCreate.value ?: return

        val updatedTask = currentTaskToCreate.copy()
        _taskToCreate.postValue(updatedTask)
    }

    fun onHighPriorityChange() {
        val currentTaskToCreate = _taskToCreate.value ?: return

        val updatedTask = currentTaskToCreate.copy(highPriority = !currentTaskToCreate.highPriority)
        _taskToCreate.postValue(updatedTask)
    }

    fun onDetailedInstructionsChange(detailedInstructions: String) {
        val currentTaskToCreate = _taskToCreate.value ?: return
        val updatedTask = currentTaskToCreate.copy(detailedInstructions = detailedInstructions)
        _taskToCreate.postValue(updatedTask)
    }

    fun onRepetitionChange() {
        val currentTaskToCreate = _taskToCreate.value ?: return

        val updatedTask = currentTaskToCreate.copy(repeatable = !currentTaskToCreate.repeatable)
        _taskToCreate.postValue(updatedTask)
    }

    fun onFrequencyOfRepetitionChange(frecuencyText: String) {
        val currentTaskToCreate = _taskToCreate.value ?: return

        if (frecuencyText.isDigitsOnly()) {
            val frequency = frecuencyText.toInt()
            val updatedTask = currentTaskToCreate.copy(repetitionIntervalInDays = frequency)
            _taskToCreate.postValue(updatedTask)

        }
    }

    fun onSave() {
        val currentTaskToCreate = _taskToCreate.value ?: return
        val isoDate = currentTaskToCreate.getISODateFromCalendar()
        //TODO: Agregar validaciones a todos los campos de la tarea
        //TODO: El userId es 0 por defecto. Cambiar luego al ID del usuario logueado
        taskRepository.addNewTaskForUser(currentTaskToCreate.copy(isoDate = isoDate), 0)
    }


}
