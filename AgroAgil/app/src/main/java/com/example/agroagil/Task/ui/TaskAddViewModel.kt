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

class TaskAddViewModel(editMode: Boolean = false) : ViewModel() {
    val taskRepository = TaskRepository()
    val farmRepository = FarmRepository()
    val editMode = editMode

    //Pantalla para crear una nueva tarea -----------------------------------
    private val _taskToCreate = MutableLiveData<TaskForAddScreen>(TaskForAddScreen(calendarDate = null))
    val taskToCreate: LiveData<TaskForAddScreen> = _taskToCreate

    private val _taskToEdit = MutableLiveData<TaskForAddScreen?>(null)
    val taskToEdit: MutableLiveData<TaskForAddScreen?> = _taskToEdit

    private fun _updateTaskOfScreen(updatedTask: TaskForAddScreen) {
        if(editMode) //Pantalla editar
            _taskToEdit.postValue(updatedTask)
        else _taskToCreate.postValue(updatedTask) //Pantalla Agrega una tarea
    }

    private fun _getCurrentTaskToCreate(): TaskForAddScreen? {
        if(editMode) //Pantalla editar
            return _taskToEdit.value
        else return _taskToCreate.value //Pantalla Agrega una tarea
    }

    fun onDescriptionChange(description: String) {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return
        _updateTaskOfScreen(currentTaskToCreate.copy(description = description))
    }

    private val _dateSelectedString = MutableLiveData<String>("Definir fecha de la tarea")
    val dateSelectedString: LiveData<String> = _dateSelectedString

    private val _timeSelectedString = MutableLiveData<String>("Definir hora de la tarea")
    val timeSelectedString: LiveData<String> = _timeSelectedString

    private val _allFarmMembers = MutableLiveData<List<Member>>()

    private val _farmMembersToSuggest = MutableLiveData<List<Member>>()
    val farmMembersToSuggest: LiveData<List<Member>> = _farmMembersToSuggest

    private val _responsibleInputText = MutableLiveData<String>("")
    val responsibleInputText: LiveData<String> = _responsibleInputText

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

        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return
        val currentDate = currentTaskToCreate.calendarDate

        if (currentDate != null) {
            calendarFromTimestamp.set(Calendar.HOUR_OF_DAY, currentDate.get(Calendar.HOUR_OF_DAY))
            calendarFromTimestamp.set(Calendar.MINUTE, currentDate.get(Calendar.MINUTE))
        }

        val updatedTask = currentTaskToCreate.copy(calendarDate = calendarFromTimestamp)
        _updateTaskOfScreen(updatedTask)
        _dateSelectedString.postValue("El día ${updatedTask.getTaskFormatDate()}")
    }

    fun onTimeChange(hour: Int, minute: Int) {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return
        var currentDate = currentTaskToCreate.calendarDate
        if (currentDate == null)
            currentDate = Calendar.getInstance()

        currentDate!!.set(Calendar.HOUR_OF_DAY, hour)
        currentDate.set(Calendar.MINUTE, minute)
        currentDate.isLenient = false

        val updatedTask = currentTaskToCreate.copy(calendarDate = currentDate)
        _updateTaskOfScreen(updatedTask)
        _timeSelectedString.postValue("A las ${updatedTask.getTaskFormatTime()} horas")
    }

    fun onEstimationChange(estimationText: String) {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return
        if (estimationText.isDigitsOnly() && estimationText.isNotEmpty()) {
            val estimation = estimationText.toInt()
            val updatedTask = currentTaskToCreate.copy(durationHours = estimation)
            _updateTaskOfScreen(updatedTask)
        }
    }

    fun onResponsibleInputChange(partialName: String) {
        val allFarmMembers = _allFarmMembers.value ?: return
        val farmMembersWithMatchingName = allFarmMembers.filter { member -> member.name.contains(partialName, ignoreCase = true)}
        var farmMembersToSuggest: List<Member>
        if(_getCurrentTaskToCreate() != null) {
            val currentTaskToCreate = _getCurrentTaskToCreate()
            farmMembersToSuggest = farmMembersWithMatchingName.filter { member ->  !currentTaskToCreate!!.resposibles.contains(member)}
        }
        else farmMembersToSuggest = farmMembersWithMatchingName

        _farmMembersToSuggest.postValue(farmMembersToSuggest)
        _responsibleInputText.postValue(partialName)
    }

    fun onResponsibleOptionSelected(memberSelected: Member) {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return
        val updatedTask = currentTaskToCreate.copy(resposibles = currentTaskToCreate.resposibles + memberSelected)
        _updateTaskOfScreen(updatedTask)
    }

    fun onResponsibleChipClose(member: Member) {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return
        val updatedTask = currentTaskToCreate.copy(resposibles = currentTaskToCreate.resposibles - member)
        _updateTaskOfScreen(updatedTask)
    }

    fun onHighPriorityChange() {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return

        val updatedTask = currentTaskToCreate.copy(highPriority = !currentTaskToCreate.highPriority)
        _updateTaskOfScreen(updatedTask)
    }

    fun onDetailedInstructionsChange(detailedInstructions: String) {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return
        val updatedTask = currentTaskToCreate.copy(detailedInstructions = detailedInstructions)
        _updateTaskOfScreen(updatedTask)
    }

    fun onRepetitionChange() {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return

        val updatedTask = currentTaskToCreate.copy(repeatable = !currentTaskToCreate.repeatable)
        _updateTaskOfScreen(updatedTask)
    }

    fun onFrequencyOfRepetitionChange(frecuencyText: String) {
        val currentTaskToCreate = _getCurrentTaskToCreate() ?: return

        if (frecuencyText.isDigitsOnly() && frecuencyText.isNotEmpty()) {
            val frequency = frecuencyText.toInt()
            val updatedTask = currentTaskToCreate.copy(repetitionIntervalInDays = frequency)
            _updateTaskOfScreen(updatedTask)

        }
    }

    private val _taskSaved = MutableLiveData<Boolean>(false)
    val taskSaved: LiveData<Boolean> = _taskSaved

    fun onSave() {
        val currentTaskToCreate = _taskToCreate.value ?: return
        val isoDate = currentTaskToCreate.getISODateFromCalendar() //2023-10-26T09:30:00Z --> error
        //TODO: Agregar validaciones a todos los campos de la tarea
        //TODO: El userId es 0 por defecto. Cambiar luego al ID del usuario logueado
        val taskToSave = currentTaskToCreate.copy(isoDate = isoDate).taskForAddScreenToTask()

        viewModelScope.launch {
            try {
                val result = taskRepository.addNewTaskForUser(taskToSave, 0)

                if(result) {
                    taskSaved()
                    _taskToCreate.postValue(TaskForAddScreen(calendarDate = null))
                    _dateSelectedString.postValue("Definir fecha de la tarea")
                    _timeSelectedString.postValue("Definir hora de la tarea")
                }
            } catch (e: Exception) {
                println("onSave error TASK:")
                e.printStackTrace()
            }
        }
    }

    private fun taskSaved() {
        _taskSaved.postValue(true)
    }

    fun taskUnsaved() {
        _taskSaved.postValue(false)
    }


    //--------------------------------------- Funciones exclusvas para el modo edición

    fun getTaskToEdit(taskId: String) {
        viewModelScope.launch {
            try {
                val result = taskRepository.getTaskForUser(0, taskId)
                _taskToEdit.postValue(result.taskToTaskForAddScreen())
                _dateSelectedString.postValue("El día ${result.taskToTaskForAddScreen().getTaskFormatDate()}")
                _timeSelectedString.postValue("A las ${result.taskToTaskForAddScreen().getTaskFormatTime()} horas")
            } catch (e: Exception) {
                println("TaskViewModel getTaskToVisualize error:")
                e.printStackTrace()
                _taskToEdit.postValue(null)
            }
        }
    }

    fun onSave(taskId: String) {
        val currentTaskToEdit = _taskToEdit.value ?: return
        val isoDate = currentTaskToEdit.getISODateFromCalendar() //2023-10-26T09:30:00Z --> error
        //TODO: Agregar validaciones a todos los campos de la tarea
        //TODO: El userId es 0 por defecto. Cambiar luego al ID del usuario logueado
        val taskToSave = currentTaskToEdit.copy(isoDate = isoDate).taskForAddScreenToTask()
        viewModelScope.launch {
            try {
                val result = taskRepository.updateTaskForUser(taskToSave, 0, taskId)
                if(result) {
                    taskSaved()
                    _taskToEdit.postValue(null)
                }
            } catch (e: Exception) {
                println("onSave error TASK:")
                e.printStackTrace()
            }
        }
    }

}