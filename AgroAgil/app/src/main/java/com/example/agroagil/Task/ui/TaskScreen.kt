package com.example.agroagil.Task.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agroagil.R
import com.example.agroagil.Task.model.TaskCardData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun TaskCard(taskCardData: TaskCardData) {
    val roundedCornerShape: RoundedCornerShape = RoundedCornerShape(
    topStart = 0.dp, // 90-degree corner here
    topEnd = 12.dp,
    bottomEnd = 12.dp,
    bottomStart = 12.dp
    )

    Card(
        shape = roundedCornerShape,
            modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .clip(roundedCornerShape)
            .clickable {
                //TODO: Completar con la acción luego de hacer clic
            }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = taskCardData.description, fontWeight = FontWeight.Bold)
                TextDate(taskCardData.date, taskCardData.durationHours)
            }

            //Checkcircle
            RoundCheckbox(
                checked = taskCardData.completed,
                onCheckedChange = {  })
        }
    }
}

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    sizeDp: Int = 24, // Size in dp
) {
    IconButton(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.size(sizeDp.dp)
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, Color.Black, CircleShape)
                .clip(CircleShape)
                .size(sizeDp.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            if (checked)
                Icon(
                    painter = painterResource(id = R.drawable.check_completed_task),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size((sizeDp - 4).dp)
                )
        }
    }
}

@Preview
@Composable
fun RoundCheckboxDemo() {
    val checkedState = remember { mutableStateOf(false) }

    RoundCheckbox(
        checked = checkedState.value,
        onCheckedChange = { checkedState.value = it })
}

@Composable
fun TextDate(date: Date, duration: Int) {
    Text(text = getTaskFormatDate(date, duration), fontWeight = FontWeight.Light, color = LightGray)
}

fun getTaskFormatDate(date: Date, duration: Int): String {
    // Formatear el día de la semana (Domingo, Lunes, etc.)
    val formatoDiaSemana = SimpleDateFormat("EEEE", Locale("es", "ES"))
    val diaSemana = formatoDiaSemana.format(date)

    // Formatear la fecha (10/09)
    val dateFormat = SimpleDateFormat("dd/MM", Locale("es", "ES"))
    val formattedDate = dateFormat.format(date)

    // Formatear la hora (13:24)
    val hourFormat = SimpleDateFormat("HH:mm", Locale("es", "ES"))
    val fromHour = hourFormat.format(date)
    val toHour = hourFormat.format(addHoursToDate(date, duration))

    return "${diaSemana.replaceFirstChar { a -> a.uppercase() }} $formattedDate de $fromHour a ${toHour}"
}

fun addHoursToDate(date: Date, hours: Int): Date {
    val milliseconds = date.time
    val millisecondsToAdd = TimeUnit.HOURS.toMillis(hours.toLong())
    return Date(milliseconds + millisecondsToAdd)
}



@Preview
@Composable
fun TaskCardPrevie() {
    val taskCardData =  TaskCardData("Cosechar tomates", Calendar.getInstance().time, 3, true, false)
    TaskCard(taskCardData = taskCardData)
}