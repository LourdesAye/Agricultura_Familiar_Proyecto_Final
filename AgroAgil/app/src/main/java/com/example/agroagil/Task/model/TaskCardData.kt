package com.example.agroagil.Task.model

import java.util.Date

data class TaskCardData(
    val description: String,
    val date: Date,
    val durationHours: Int,
    val completed: Boolean,
    val highPriority: Boolean
)