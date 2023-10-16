package com.example.agroagil.Task.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.agroagil.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAddScreen (taskViewModel: TaskViewModel, navController: NavController) {
    val showDatePickerDialog = remember { mutableStateOf(false) }
    var showTimePicker = remember { mutableStateOf(false) }
    val dateSelectedString = taskViewModel.dateSelectedString.observeAsState().value
    val timeSelectedString = taskViewModel.timeSelectedString.observeAsState().value
    var taskToCreate = taskViewModel.taskToCreate.observeAsState().value
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ){
        val paddingBottom = 4
        Text(modifier = Modifier.padding(bottom = paddingBottom.dp),text = "Agregar nueva", style = MaterialTheme.typography.headlineLarge)
        //Descripción (título)
        OutlinedTextField(
            modifier = Modifier.padding(bottom = paddingBottom.dp),
            value = taskToCreate!!.description,
            onValueChange = { taskViewModel.onDescriptionChange(it) },
            label = { Text("Descripción") }
        )
        //Fecha
        TextButtonWithIcon(
            onClick = { showDatePickerDialog.value = true },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Date Picker Icon") },
            text = dateSelectedString!!
        )
        DatePickerScreen(showDatePickerDialog, taskViewModel)

        //Hora
        TextButtonWithIcon(
            onClick = { showTimePicker.value = true },
            icon = { Icon(
                painter = painterResource(id = R.drawable.clock_24),
                contentDescription = "Time Picker Icon"
            )
            },
            text = timeSelectedString!!
        )
        TimePickerDialogScreen(showTimePicker, taskViewModel)

        //Estimación de tiempo
        OutlinedTextField(
            modifier = Modifier.padding(bottom = paddingBottom.dp),
            value = taskToCreate!!.durationHours.toString(),
            onValueChange = { taskViewModel.onEstimationChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Handle done event, if necessary */ }
            ),
            visualTransformation = VisualTransformation.None,
            label = { Text("Estimación de tiempo necesário (Hs)") }
        )

        //Ubicación en el campo
        OutlinedTextField(
            modifier = Modifier.padding(bottom = paddingBottom.dp),
            value = taskToCreate!!.locationInFarm,
            onValueChange = { taskViewModel.onLocationInFarmChange(it) },
            label = { Text("Ubicación en el campo") }
        )
        //Responsables de la tarea
        OutlinedTextField(
            modifier = Modifier.padding(bottom = paddingBottom.dp),
            value = taskToCreate!!.getTaskFormatTime(),
            onValueChange = { taskViewModel.onResponsiblesChange() },
            label = { Text("Responsables de la tarea") }
        )
        //Prioridad alta
        SwitchWithText("Tiene prioridad alta", taskToCreate!!.highPriority, { taskViewModel.onHighPriorityChange() })
        //Instrucciones detalladas
        OutlinedTextField(
            value = taskToCreate!!.detailedInstructions,
            onValueChange = { taskViewModel.onDetailedInstructionsChange(it) },
            label = { Text("Instrucciones detalladas") }
        )
        //TODO: recursos necesários. Validar si es útil este campo

        //Repetición
        SwitchWithText("Se repite", taskToCreate!!.repeatable, { taskViewModel.onRepetitionChange() })
        //Frecuencia de repetición
        if(taskToCreate!!.repeatable) {
            OutlinedTextField(
                modifier = Modifier.padding(bottom = paddingBottom.dp),
                value = taskToCreate!!.durationHours.toString(),
                onValueChange = { taskViewModel.onFrequencyOfRepetitionChange(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { /* Handle done event, if necessary */ }
                ),
                visualTransformation = VisualTransformation.None,
                label = { Text("Días entre repetición", style = MaterialTheme.typography.bodyLarge) }
            )
        }

        //Recordatorio
        //Botones de guardado y cancelar
        SaveOrCancelbuttonsRow({ taskViewModel.onSave() }, navController = navController)
    }
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(showDatePickerDialog: MutableState<Boolean>, taskViewModel: TaskViewModel) {
    if (showDatePickerDialog.value) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                showDatePickerDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog.value = false
                        taskViewModel.onDateChange(datePickerState.selectedDateMillis)
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ){
            DatePicker(state = datePickerState)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogScreen( showTimePicker: MutableState<Boolean>, taskViewModel: TaskViewModel) {
    val state = rememberTimePickerState(is24Hour = true)

    if (showTimePicker.value) {
        PickerDialog(
            title = "Ingrese la hora",
            onCancel = { showTimePicker.value = false },
            onConfirm = {
                taskViewModel.onTimeChange(state.hour, state.minute)
                showTimePicker.value = false
            }
        ) {
           TimeInput (state = state)
        }
    }
}

@Composable
fun PickerDialog(
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
                    ) { Text("Cancelar") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("Aceptar") }
                }
            }
        }
    }
}

@Composable
fun TextButtonWithIcon(onClick: () -> Unit, icon: @Composable () -> Unit = {}, text:String) {
    TextButton(onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp), // Space between the Text and the Icon
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            icon()
            Text(text, style = MaterialTheme.typography.titleLarge)
        }
    }
}


@Composable
fun SwitchWithText(description: String, checked: Boolean, onCheckedChange: () -> Unit) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 30.dp))
    {
        Text(text = description, style = MaterialTheme.typography.titleLarge)
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckedChange()
            }
        )
    }
}

@Composable
fun SaveOrCancelbuttonsRow(onSave: () -> Unit, navController: NavController) {
    Column(){
        Box(modifier = Modifier.fillMaxSize()){
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(30.dp)){
                Button(onClick = onSave, modifier = Modifier.align(Alignment.CenterVertically)

                ) {

                    Text("Guardar")
                }
                TextButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}