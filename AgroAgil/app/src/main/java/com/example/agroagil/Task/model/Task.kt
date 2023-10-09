package com.example.agroagil.Task.model

/**
 * Data class para la creación de una tarea
 */
data class Task(
    val id: Int,
    val description: String,
    val isoDate: String,
    val durationHours: Int,
    var completed: Boolean,
    val highPriority: Boolean
)
