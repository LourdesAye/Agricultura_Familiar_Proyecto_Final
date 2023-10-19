package com.example.agroagil.Task.model

import com.example.agroagil.core.models.Member
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Data class para la creación de una tarea. se usa solo para almacenar temporalmente
 * los datos que se van cargando en la pantalla de Agregar tarea.
 * A firebase se envían estos datos en otra data class llamada "Task"
 */
data class TaskForAddScreen(
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

    @Transient
    val calendarDate: Calendar? //No guardar este campo en firebase
) {
    private val LOCALE_AR = Locale("es", "AR")

    fun getDate(): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", LOCALE_AR)
        try {
            return sdf.parse(isoDate)
        } catch (e: Exception) {
            println("Failed to parse date: ${e.message}")
        }
        return null
    }


    fun getTaskFormatDate(): String {
        if(calendarDate == null)
            return ""

        // Formatear el día de la semana (Domingo, Lunes, etc.)
        val formatoDiaSemana = SimpleDateFormat("EEEE", LOCALE_AR)
        //TODO Borrar:
        val date = calendarDate.time

        val diaSemana = formatoDiaSemana.format(calendarDate.time)

        // Formatear la fecha (10/09)
        val dateFormat = SimpleDateFormat("dd/MM", LOCALE_AR)
        val formattedDate = dateFormat.format(calendarDate.time)

        // Formatear el año (2023)
        val yearFormat = SimpleDateFormat("yyyy", LOCALE_AR)
        val formattedYear = yearFormat.format(calendarDate.time)

        return "${diaSemana.replaceFirstChar { a -> a.uppercase() }} $formattedDate de $formattedYear"
    }

    fun getTaskFormatTime(): String {
        if(calendarDate == null)
            return ""
        // Formatear la hora (13:24)
        val hourFormat = SimpleDateFormat("HH:mm", LOCALE_AR)
        val hour = hourFormat.format(calendarDate.time)

        return "$hour"
    }

    fun getISODateFromCalendar(): String {
        // Create a SimpleDateFormat object with ISO 8601 format
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

        if(calendarDate != null ) {
            // Apply the time zone of the calendar to the SimpleDateFormat
            sdf.timeZone = calendarDate.timeZone
            // Convert the Calendar object to a Date and then format it to an ISO 8601 string
            return sdf.format(calendarDate.time)
        } else return ""
    }

    /**
     * Este método retorna un objeto tipo Task para guardar la tarea en firebase.
     * Tiene los mismos campos que TaskForAddScreen, salvo el campo de tipo calendar
     */
    fun taskForAddScreenToTask(): Task {
        return Task(
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
            creator = this.creator
        )
    }

}
