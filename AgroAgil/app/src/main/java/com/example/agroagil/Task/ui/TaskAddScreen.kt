package com.example.agroagil.Task.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAddScreen (taskViewModel: TaskViewModel, navController: NavController) {
    Text(text = "Agregar tarea...", fontWeight = FontWeight.Bold)
    val opendatePickerDialog = remember { mutableStateOf(true) }
    var showTimePicker = remember { mutableStateOf(false) }
    // var taskCardDataList = taskViewModel.taskCardDataList.observeAsState().value
    var taskToCreate = taskViewModel.taskToCreate.observeAsState().value


    //Descripción (título)
    OutlinedTextField(
        value = taskToCreate!!.description,
        onValueChange = { taskViewModel.onDescriptionChange(it) },
        label = { Text("Descripción") }
    )
    //Fecha y hora para realizar la tarea

    OutlinedTextField(
        value = taskToCreate!!.getTaskFormatDate(),
        onValueChange = { taskViewModel.onDescriptionChange(it) },
        label = { Text("Fecha") },
        modifier = Modifier.clickable(
            onClick = {
                // Perform your action here
                opendatePickerDialog.value = true
            }
        )
    )

    DatePickerScreen(opendatePickerDialog, taskViewModel)
    OutlinedTextField(
        value = taskToCreate!!.getTaskFormatTime(),
        onValueChange = { taskViewModel.onDescriptionChange(it) },
        label = { Text("Hora") },
        modifier = Modifier.clickable(
            onClick = {
                // Perform your action here
                showTimePicker.value = true
            }
        )
    )
    TimePickerDialogScreen(showTimePicker, taskViewModel)

    //Estimación de tiempo
    OutlinedTextField(
        value = taskToCreate!!.getTaskFormatTime(),
        onValueChange = { taskViewModel.onDescriptionChange(it) },
        label = { Text("Estimación de tiempo (Hs)") }
    )
    //Ubicación en el campo
    OutlinedTextField(
        value = taskToCreate!!.getTaskFormatTime(),
        onValueChange = { taskViewModel.onDescriptionChange(it) },
        label = { Text("Ubicación en el campo") }
    )
    //Responsables de la tarea
    OutlinedTextField(
        value = taskToCreate!!.getTaskFormatTime(),
        onValueChange = { taskViewModel.onDescriptionChange(it) },
        label = { Text("Responsables de la tarea") }
    )
    //Prioridad alta

    //Instrucciones detalladas

    //TODO: recurosos necesários. Validar si es útil este campo

    //Repetición

    //Frecuencia de repetición

    //Recordatorio


}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(opendatePickerDialog: MutableState<Boolean>, taskViewModel: TaskViewModel) {
// Decoupled snackbar host state from scaffold state for demo purposes.
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackState, Modifier)

// TODO demo how to read the selected date from the state.
    if (opendatePickerDialog.value) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                opendatePickerDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        opendatePickerDialog.value = false
                        snackScope.launch {

                            snackState.showSnackbar(
                                "Selected date timestamp: ${datePickerState.selectedDateMillis}"
                            )
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        opendatePickerDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogScreen( showTimePicker: MutableState<Boolean>, taskViewModel: TaskViewModel) {

    val date = taskViewModel.dateOftaskToCreate.observeAsState().value


    val state = rememberTimePickerState(is24Hour = true)
    val snackState = remember { SnackbarHostState() }
    Box(propagateMinConstraints = false) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { showTimePicker.value = true }
        ) {
            Text("Set Time")
        }
        SnackbarHost(hostState = snackState)
    }

    if (showTimePicker.value) {
        TimePickerDialog(
            title = "Ingrese la hora",
            onCancel = { showTimePicker.value = false },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                taskViewModel.onDateTimeChange( date!!, cal)
                showTimePicker.value = false
            }
        ) {
           TimeInput (state = state)
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}
