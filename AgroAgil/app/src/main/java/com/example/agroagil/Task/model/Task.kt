package com.example.agroagil.Task.model

import com.example.agroagil.core.models.Member

/**
 * Data class para la creación de una tarea.
 * Este data class se usa para mandar los datos de una nueva tarea a firebase.
 * En firebase se guarda en BD con estos atributos
 */
data class Task(
    val id: String = "",
    val description: String = "",
    val isoDate: String = "", //Ej 2001-01-01T00:00:00-03:00
    val durationHours: Int? = 0,
    var completed: Boolean = false,
    val highPriority: Boolean = false,

    val completionIsoDate: String = "", //Ej 2001-01-01T00:00:00-03:00
    val locationInFarm: String = "",
    val resposibles: List<Member> = emptyList(),
    val detailedInstructions: String = "",
    val repeatable: Boolean = false,
    val repetitionIntervalInDays: Int? = 0,
    val creator: Member = Member(),
    //TODO validar si este campo va: val resultExpected: String
) {
    /**
     * Este método retorna un objeto tipo Task para guardar la tarea en firebase.
     * Tiene los mismos campos que TaskForAddScreen, salvo el campo de tipo calendar
     */
    fun taskToTaskForAddScreen(): TaskForAddScreen {
        return TaskForAddScreen(
            id = this.id,
            description = this.description,
            isoDate = this.isoDate,
            durationHours = this.durationHours,
            completed = this.completed,
            highPriority = this.highPriority,
            completionIsoDate = this.completionIsoDate,
            locationInFarm = this.locationInFarm,
            resposibles = this.resposibles,
            detailedInstructions = this.detailedInstructions,
            repeatable = this.repeatable,
            repetitionIntervalInDays = this.repetitionIntervalInDays,
            creator = this.creator,
            calendarDate = isoDate?.stringIsoDateToDate()?.toCalendar()
        )
    }
}