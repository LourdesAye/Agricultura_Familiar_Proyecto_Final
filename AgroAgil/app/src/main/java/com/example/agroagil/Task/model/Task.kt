package com.example.agroagil.Task.model

import com.example.agroagil.core.models.Member
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class para la creación de una tarea
 */
data class Task(
    val id: Int = 0,
    val description: String = "",
    val isoDate: String = "", //Ej 2001-01-01T00:00:00-03:00
    val durationHours: Int = 0,
    var completed: Boolean = false,
    val highPriority: Boolean = false,

    val completionIsoDate: String = "", //Ej 2001-01-01T00:00:00-03:00
    val locationInFarm: String = "",
    val resposibles: List<Member> = emptyList(),
    val detailedInstructions: String = "",
    val repeatable: Boolean = false,
    val repetitionIntervalInDays: Int = 0,
    val creator: Member = Member()
    //TODO validar si este campo va: val resultExpected: String
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
        val date = getDate() ?: return ""

        // Formatear el día de la semana (Domingo, Lunes, etc.)
        val formatoDiaSemana = SimpleDateFormat("EEEE", LOCALE_AR)
        val diaSemana = formatoDiaSemana.format(date)

        // Formatear la fecha (10/09)
        val dateFormat = SimpleDateFormat("dd/MM", LOCALE_AR)
        val formattedDate = dateFormat.format(date)

        return "${diaSemana.replaceFirstChar { a -> a.uppercase() }} $formattedDate"
    }

    fun getTaskFormatTime(): String {
        val date = getDate() ?: return ""

        // Formatear la hora (13:24)
        val hourFormat = SimpleDateFormat("HH:mm", LOCALE_AR)
        val hour = hourFormat.format(date)

        return "$hour"
    }
}
