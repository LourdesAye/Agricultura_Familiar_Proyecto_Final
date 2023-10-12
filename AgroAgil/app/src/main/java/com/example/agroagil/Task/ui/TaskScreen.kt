package com.example.agroagil.Task.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ChipBorder
import androidx.compose.material3.ChipColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.Task.model.AppliedFiltersForTasks
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.Task.model.TaskFilter
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

data class CardColor(val surfaceColor: String, val textColor:String)
const val CHECK_ICON_SIZE = 24

@Composable
fun TaskScreen(taskViewModel: TaskViewModel, navController: NavController) {
    var taskCardDataList = taskViewModel.taskCardDataList.observeAsState().value
    var filterTasksBy = taskViewModel.appliedFiltersForTasks.observeAsState().value

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
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (taskScreen, addTaskButton) = createRefs()

            Column(modifier = Modifier.constrainAs(taskScreen) {}) {
                LazyColumn {
                    item { FilteringBox(taskViewModel) }
                    item{ Spacer(modifier = Modifier.padding(10.dp)) }
                    items(taskCardDataList) {
                        TaskCard(taskCardData = it, taskViewModel = taskViewModel, filterTasksBy)
                    }
                    item { Spacer(modifier = Modifier.padding(40.dp))  }
                }
            }

            AddTaskButton(
                navController,
                modifier = Modifier.constrainAs(addTaskButton) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
            )
        }
    }

}

fun getCardColor(highPriority: Boolean, completed: Boolean): CardColor {
    if(completed)
        return CardColor(COMPLETED_TASK_CARD_COLOR, COMPLETED_TASK_TEXT_COLOR)
    if(highPriority)
        return CardColor(INCOMPLETE_IMPORTANT_TASK_CARD_COLOR, INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR)
    else return CardColor(INCOMPLETE_NORMAL_TASK_CARD_COLOR, INCOMPLETE_NORMAL_TASK_TEXT_COLOR)
}

@Composable
fun TaskCard(taskCardData: TaskCardData, taskViewModel: TaskViewModel?, filterTasksBy: AppliedFiltersForTasks?) {
    val roundedCornerShape = RoundedCornerShape(
    topStart = 0.dp, // 90-degree corner here
    topEnd = 14.dp,
    bottomEnd = 14.dp,
    bottomStart = 14.dp
    )

    val cardColor: CardColor = getCardColor(taskCardData.highPriority, taskCardData.completed)
    val coroutineScope = rememberCoroutineScope()

    if(filterTasksBy != null && taskCardData.passFilters(filterTasksBy)) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    //TODO: Completar con la acción luego de hacer clic en la card

                },
            shape = roundedCornerShape,
            colors = CardDefaults.cardColors(
                containerColor = Color(cardColor.surfaceColor.toColorInt())
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = taskCardData.getLimitedDescription(), fontWeight = FontWeight.Bold,
                        color = Color(cardColor.textColor.toColorInt()),
                        style = MaterialTheme.typography.titleLarge)
                    TextDate(taskCardData)
                }

                //Checkcircle
                RoundCheckbox(
                    checked = taskCardData.completed,
                    onCheckedChange = {
                        coroutineScope.launch {
                            taskViewModel?.toggleTaskCompletedStatus(taskCardData)
                        }
                   },
                    color = Color(cardColor.textColor.toColorInt())
                )
            }
        }
    }

}

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    color: Color
) {
    val sizeDp: Int = 28
    IconButton(
        onClick = { onCheckedChange() },
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
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size((CHECK_ICON_SIZE).dp)
                )
        }
    }
}


@Composable
fun TextDate(taskCardData: TaskCardData) {
    Text(text = taskCardData.getTaskFormatDate(), fontWeight = FontWeight.Light, color = Gray)
}

@Composable
fun AddTaskButton(navController: NavController?, modifier: Modifier) {
    Button(
        onClick = { navController?.navigate("task/add") },
        modifier = modifier
    ) {
        Row {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Agregar")
        }
    }
}

@Composable
fun FilteringBox(taskViewModel: TaskViewModel) {
    var filterTasksBy = taskViewModel.appliedFiltersForTasks.observeAsState().value

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(10.dp)) {
            Text(
                text = "Fecha",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Botones x3
                    FilteringButton(filterTasksBy, TaskFilter.ByOverdue, taskViewModel)
                    FilteringButton(filterTasksBy, TaskFilter.ByToday, taskViewModel)
                    FilteringButton(filterTasksBy, TaskFilter.ByNext, taskViewModel)
            }
            Divider(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
            Text(text = "Prioridad", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Botones x3
                    FilteringButton(filterTasksBy, TaskFilter.ByHigh, taskViewModel)
                    FilteringButton(filterTasksBy, TaskFilter.ByLow, taskViewModel)
                    FilteringButton(filterTasksBy, TaskFilter.ByDone, taskViewModel)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteringButton(filterTasksBy: AppliedFiltersForTasks?, taskFilter: TaskFilter, taskViewModel: TaskViewModel) {
    val color = taskViewModel.getFilterColor(taskFilter)?: MaterialTheme.colorScheme.onSurface
    val chipColor: ChipColors = AssistChipDefaults.assistChipColors(
        labelColor = color,
        leadingIconContentColor = color
    )
    val borderColor: ChipBorder = AssistChipDefaults.assistChipBorder(
        borderColor = color,
        borderWidth = 1.dp
    )

    AssistChip(
        modifier = Modifier
            .height((CHECK_ICON_SIZE * 2).dp)
            .padding(4.dp),
        onClick = { taskViewModel.onFilteringBoxChange(taskFilter) },
        label = { Text(taskFilter.name, style = MaterialTheme.typography.titleLarge) },
        colors = chipColor,
        border = borderColor,
        leadingIcon = {
            if(filterTasksBy?.getFilterValue(taskFilter) == true)
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Localized description",
                    Modifier.size(CHECK_ICON_SIZE.dp),
                )
        }
    )
}


