package com.example.agroagil.Task.model

/**
 * Data class para la creación de una tarea
 */
data class Task(
    val id: Int = 0,
    val description: String = "",
    val isoDate: String = "2001-01-01T00:00:00-03:00",
    val durationHours: Int = 0,
    var completed: Boolean = false,
    val highPriority: Boolean = false
)
