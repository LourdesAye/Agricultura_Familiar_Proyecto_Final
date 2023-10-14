package com.example.agroagil.Task.model

sealed class TaskFilter(val name: String) {
    object ByOverdue: TaskFilter("Atrasadas")
    object ByToday: TaskFilter("Hoy")
    object ByNext: TaskFilter("Futuras")
    object ByLow: TaskFilter("Baja")
    object ByHigh: TaskFilter("Alta")
    object ByDone: TaskFilter("Hechas")
}