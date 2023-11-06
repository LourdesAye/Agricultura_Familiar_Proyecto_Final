package com.example.agroagil.Task.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Person2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agroagil.R
import com.example.agroagil.core.models.Member

@Composable
fun TaskInfoScreen (taskViewModel: TaskViewModel, navController: NavController, taskId: String) {
    var taskToVisualize = taskViewModel.taskToVisualize.observeAsState().value
    val taskDeleted = taskViewModel.taskDeleted.observeAsState().value!!

    // Handle side effects such as navigation
    LaunchedEffect(taskDeleted) {
        if (taskDeleted) {
            taskViewModel.taskNotDeleted()
            navController.popBackStack()
            taskViewModel.refreshTaskCardsLiveData(0)
        }
    }

    if(taskToVisualize == null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }
    } else {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            //Botones de eliminar y editar

            DeleteOrEditButtonsRow(onDelete = {taskViewModel.onDeleteTask(taskId)},
                onEdit = {navController.navigate("task/${taskId}/edit")})

            //Contenido de la tarea
            OutlinedTextFieldReadOnly(taskToVisualize!!.description, "Descripción")

            //Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Date Picker Icon")
                Text(
                    text = "El día ${taskToVisualize.getTaskFormatDate()!!}",
                    fontWeight = FontWeight.Bold
                )
            }

            //Hora
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.clock_24), contentDescription = "Time Picker Icon")
                Text(
                    text = "A las ${taskToVisualize.getTaskFormatTime()!!} horas",
                    fontWeight = FontWeight.Bold
                )
            }


            //Estimación de tiempo
            OutlinedTextFieldReadOnly(taskToVisualize!!.durationHours.toString(),"Estimación de tiempo necesário (Hs)")

            //Ubicación en el campo
            OutlinedTextFieldReadOnly(taskToVisualize!!.locationInFarm, "Ubicación en el campo")


            //Responsables de la tarea
            Text(text = "Responsables:", style = MaterialTheme.typography.titleLarge)
            MemberChipsReadOnlyRow(members = taskToVisualize!!.resposibles)


            //Prioridad alta
            SwitchWithText("Tiene prioridad alta", taskToVisualize!!.highPriority) { }
            //Instrucciones detalladas

            OutlinedTextFieldReadOnly(taskToVisualize!!.detailedInstructions, "Instrucciones detalladas")

            //Repetición
            SwitchWithText("Se repite", taskToVisualize!!.repeatable) { }
            //Frecuencia de repetición
            if(taskToVisualize!!.repeatable) {
                OutlinedTextFieldReadOnly(taskToVisualize!!.repetitionIntervalInDays.toString(),"Días entre repetición")
            }

        }
    }

}


@Composable
fun EditButton(text: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.filledTonalButtonColors()
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text)
        }
    }
}


@Composable
fun DeleteOrEditButtonsRow(onDelete: () -> Unit, onEdit: () -> Unit) {
    Column {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextButtonWithIcon(onClick = onDelete, icon = { Icon(
                    Icons.Rounded.DeleteForever,
                    contentDescription = "Delete forever"
                ) }, "Eliminar")
                EditButton(text = "Editar", onEdit)
            }
        }
    }
}


@Composable
fun OutlinedTextFieldReadOnly(
    value: String,
    descriptionText: String
) {
    OutlinedTextField(
        trailingIcon = null,
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        value = value,
        onValueChange = {},
        label = { Text(descriptionText) },
        keyboardActions = KeyboardActions(
            onDone = null
        )
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MemberChipsReadOnlyRow(members: List<Member>) {
    FlowRow()
    {
        members.forEach { member ->
            InputReadOnlyChip(label = member.name)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputReadOnlyChip(
    label: String = ""
) {
    InputChip(
        onClick = {},
        label = { Text(label) },
        selected = false,
        avatar = {
            Icon(
                Icons.Rounded.Person2,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        }
    )
}