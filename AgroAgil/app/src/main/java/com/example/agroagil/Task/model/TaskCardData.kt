package com.example.agroagil.Task.model

import com.example.agroagil.Task.ui.AppliedFiltersForTasks
import java.text.SimpleDateFormat
import java.util.Calendar
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
    private val _maxDescriptionSize = 24
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

    fun getLimitedDescription(): String {
        if(description.length > _maxDescriptionSize)
            return description.substring(startIndex = 0, endIndex = _maxDescriptionSize) + "..."
        else return description
    }

    fun passFilters(appliedFilters : AppliedFiltersForTasks): Boolean {
        val currentDate = Calendar.getInstance().time
        if(appliedFilters.noFilterApplied()) //Se muestra la tarea si cumple por lo menos un filtro
            return true

        var passDateFilters = appliedFilters.noDateFilterApplied()
        var passPriorityFilters = appliedFilters.noPriorityFiolterApplied()

        if(appliedFilters.filterByOverdue && isDate1OverdueComparedToDate2(date, currentDate) )
            passDateFilters = true
        if(appliedFilters.filterByToday && datesHaveSameDayMonthYear(date, currentDate) )
            passDateFilters = true
        //FIXME: El filtro de tareas futuras incluye las de hoy
        if(appliedFilters.filterByNext && !isDate1OverdueComparedToDate2(date, currentDate) )
            passDateFilters = true


        if(appliedFilters.filterByLow && !highPriority && !completed)
            passPriorityFilters = true
        if(appliedFilters.filterByHigh && highPriority && !completed)
            passPriorityFilters = true
        if (appliedFilters.filterByDone && completed)
            passPriorityFilters = true


        return passDateFilters && passPriorityFilters
    }


    fun datesHaveSameDayMonthYear(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    fun isDate1OverdueComparedToDate2(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply {
            time = date1
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val cal2 = Calendar.getInstance().apply {
            time = date2
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return cal1.timeInMillis < cal2.timeInMillis
    }
}