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
)