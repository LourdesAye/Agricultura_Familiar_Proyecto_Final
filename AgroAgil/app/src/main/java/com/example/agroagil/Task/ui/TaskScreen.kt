package com.example.agroagil.Task.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.R
import com.example.agroagil.Task.model.TaskCardData
import java.util.Calendar


val completedTaskCardColor = "#E2F2F2"
val incompleteImportantTaskCardColor = "#FAE9E8"
val incompleteNormalTaskCardColor = "#E9F0F8"

val completedTaskTextColor = "#38B9BC"
val incompleteImportantTaskTextColor = "#E73226"
val incompleteNormalTaskTextColor = "#5B92E3"
data class CardColor(val surfaceColor: String, val textColor:String)

@Composable
fun TaskScreen(taskViewModel: TaskViewModel, navController: NavController) {
    var taskCardDataList = taskViewModel.taskCardDataList.observeAsState().value

    if(taskCardDataList == null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }
    } else {
        LazyColumn{
            items(taskCardDataList) {
                TaskCard(taskCardData = it, taskViewModel = taskViewModel)
            }
        }
    }

}

fun getCardColor(highPriority: Boolean, completed: Boolean): CardColor {
    if(completed)
        return CardColor(completedTaskCardColor, completedTaskTextColor)
    if(highPriority)
        return CardColor(incompleteImportantTaskCardColor, incompleteImportantTaskTextColor)
    else return CardColor(incompleteNormalTaskCardColor, incompleteNormalTaskTextColor)
}

@Composable
fun TaskCard(taskCardData: TaskCardData, taskViewModel: TaskViewModel?) {
    val roundedCornerShape = RoundedCornerShape(
    topStart = 0.dp, // 90-degree corner here
    topEnd = 12.dp,
    bottomEnd = 12.dp,
    bottomStart = 12.dp
    )

    val cardColor: CardColor = getCardColor(taskCardData.highPriority, taskCardData.completed)

    val cardColors = CardColors(
        containerColor = Color(cardColor.surfaceColor.toColorInt()),
        contentColor = Color(cardColor.surfaceColor.toColorInt()),
        disabledContainerColor = Color(cardColor.surfaceColor.toColorInt()),
        disabledContentColor = Color(cardColor.surfaceColor.toColorInt())
    )

    Card(
        shape = roundedCornerShape,
        colors = cardColors, // Setting color directly
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(color = Color(cardColor.surfaceColor.toColorInt()))
                .clip(roundedCornerShape)
                .clickable {
                    //TODO: Completar con la acción luego de hacer clic
                    taskViewModel?.toggleTaskCompletedStatus(taskCardData.id)
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
                Text(text = taskCardData.description, fontWeight = FontWeight.Bold, color = Color(cardColor.textColor.toColorInt()))
                TextDate(taskCardData)
            }

            //Checkcircle
            RoundCheckbox(
                checked = taskCardData.completed,
                onCheckedChange = { taskCardData.completed = !taskCardData.completed },
                color = Color(cardColor.textColor.toColorInt())
                )
        }
    }
}

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color
) {
    val sizeDp: Int = 28
    IconButton(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.size(sizeDp.dp)
    ) {
        Box(
            modifier = Modifier
                .border(2.dp, color, CircleShape)
                .clip(CircleShape)
                .size(sizeDp.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            if (checked)
                Icon(
                    painter = painterResource(id = R.drawable.check_completed_task),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size((sizeDp - 4).dp)
                )
        }
    }
}


@Composable
fun TextDate(taskCardData: TaskCardData) {
    Text(text = taskCardData.getTaskFormatDate(), fontWeight = FontWeight.Light, color = LightGray)
}


@Preview
@Composable
fun TaskCardPrevie() {
    var taskCardData =  TaskCardData(11, "Cosechar tomates", Calendar.getInstance().time, 3, true, false)

    TaskCard(taskCardData = taskCardData, null)
}