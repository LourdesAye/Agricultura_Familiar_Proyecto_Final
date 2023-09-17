package com.example.agroagil.Task.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class TaskCardData(
    val id: Int,
    val description: String,
    val date: Date,
    val durationHours: Int,
    var completed: Boolean,
    val highPriority: Boolean
) {
    fun getTaskFormatDate(): String {
        // Formatear el día de la semana (Domingo, Lunes, etc.)
        val formatoDiaSemana = SimpleDateFormat("EEEE", Locale("es", "ES"))
        val diaSemana = formatoDiaSemana.format(date)

        // Formatear la fecha (10/09)
        val dateFormat = SimpleDateFormat("dd/MM", Locale("es", "ES"))
        val formattedDate = dateFormat.format(date)

        // Formatear la hora (13:24)
        val hourFormat = SimpleDateFormat("HH:mm", Locale("es", "ES"))
        val fromHour = hourFormat.format(date)
        val toHour = hourFormat.format(addHoursToDate(date, durationHours))

        return "${diaSemana.replaceFirstChar { a -> a.uppercase() }} $formattedDate de $fromHour a $toHour"
    }

    fun addHoursToDate(date: Date, hours: Int): Date {
        val milliseconds = date.time
        val millisecondsToAdd = TimeUnit.HOURS.toMillis(hours.toLong())
        return Date(milliseconds + millisecondsToAdd)
    }

}