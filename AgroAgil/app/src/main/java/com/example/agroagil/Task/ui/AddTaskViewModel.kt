package com.example.agroagil.Task.ui

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agroagil.Task.data.TaskRepository
import com.example.agroagil.Task.model.TaskForAddScreen
import com.example.agroagil.core.data.FarmRepository
import com.example.agroagil.core.models.Member
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTaskViewModel : ViewModel() {
    val taskRepository = TaskRepository()
    val farmRepository = FarmRepository()

    //Pantalla para crear una nueva tarea -----------------------------------
    private val _taskToCreate = MutableLiveData<TaskForAddScreen>(TaskForAddScreen(calendarDate = null))
    val taskToCreate: LiveData<TaskForAddScreen> = _taskToCreate

    fun onDescriptionChange(description: String) {
        val currentTaskToCreate = _taskToCreate.value ?: return
        _taskToCreate.postValue(currentTaskToCreate.copy(description = description))
    }

    private val _dateSelectedString = MutableLiveData<String>("Definir fecha de la tarea")
    val dateSelectedString: LiveData<String> = _dateSelectedString

    private val _timeSelectedString = MutableLiveData<String>("Definir hora de la tarea")
    val timeSelectedString: LiveData<String> = _timeSelectedString

    private val _allFarmMembers = MutableLiveData<List<Member>>()
    private val _farmMembersToSuggest = MutableLiveData<List<Member>>()

    private val _responsibleFieldText = MutableLiveData<String>("")
    val responsibleFieldText: LiveData<String> = _responsibleFieldText

    init {
        refreshFarmMembersList(0)
    }

    private fun refreshFarmMembersList(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val realValue = farmRepository.getFarmMembersForUser(userId)
                _allFarmMembers.postValue(realValue)
            } catch (e: Exception) {
                // Handle exception, e.g., log it
                _allFarmMembers.postValue(emptyList())
            }
        }
    }


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
        if (estimationText.isDigitsOnly() && estimationText.isNotEmpty()) {
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

    fun onResponsibleChange(partialName: String) {
        val allFarmMembers = _allFarmMembers.value ?: return
        val farmMembersWithMatchingName = allFarmMembers.filter { member -> member.name.contains(partialName, ignoreCase = true)}

        _farmMembersToSuggest.postValue(farmMembersWithMatchingName)
        _responsibleFieldText.postValue(partialName)
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

        if (frecuencyText.isDigitsOnly() && frecuencyText.isNotEmpty()) {
            val frequency = frecuencyText.toInt()
            val updatedTask = currentTaskToCreate.copy(repetitionIntervalInDays = frequency)
            _taskToCreate.postValue(updatedTask)

        }
    }

    private val _showSnackbarFortaskSaved = MutableLiveData<Boolean>(false)
    val showSnackbarFortaskSaved: LiveData<Boolean> = _showSnackbarFortaskSaved

    suspend fun onSave() {
        val currentTaskToCreate = _taskToCreate.value ?: return
        val isoDate = currentTaskToCreate.getISODateFromCalendar() //2023-10-26T09:30:00Z --> error
        //TODO: Agregar validaciones a todos los campos de la tarea
        //TODO: El userId es 0 por defecto. Cambiar luego al ID del usuario logueado
        val taskToSave = currentTaskToCreate.copy(isoDate = isoDate).taskForAddScreenToTask()
        val result = taskRepository.addNewTaskForUser(taskToSave, 0)

        if(result) {
            taskSaved()
            _taskToCreate.postValue(TaskForAddScreen(calendarDate = null))
        }
    }

    private fun taskSaved() {
        _showSnackbarFortaskSaved.postValue(true)
    }

    public fun taskUnsaved() {
        _showSnackbarFortaskSaved.postValue(false)
    }

}