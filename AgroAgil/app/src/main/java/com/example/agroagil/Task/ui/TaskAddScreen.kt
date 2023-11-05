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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.agroagil.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


@Composable
fun TaskAddScreen (taskViewModel: AddTaskViewModel, navController: NavController) {
    val showDatePickerDialog = remember { mutableStateOf(false) }
    var showTimePicker = remember { mutableStateOf(false) }
    val dateSelectedString = taskViewModel.dateSelectedString.observeAsState().value
    val timeSelectedString = taskViewModel.timeSelectedString.observeAsState().value
    var taskToCreate = taskViewModel.taskToCreate.observeAsState().value
    var responsibleTextField = taskViewModel.responsibleFieldText.observeAsState().value

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val showSnackbarForTaskSaved: Boolean = taskViewModel.showSnackbarFortaskSaved.observeAsState().value!!

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        Text(
            "Agrega una tarea",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )

        OutlinedTextFieldWithIcon(taskToCreate!!.description,
            { taskViewModel.onDescriptionChange(it) }, "Descripción")

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
        OutlinedNumericField(taskToCreate!!.durationHours.toString(),
            { taskViewModel.onEstimationChange(it) },"Estimación de tiempo necesário (Hs)")

        //Ubicación en el campo
        OutlinedTextFieldWithIcon(taskToCreate!!.locationInFarm,
            { taskViewModel.onLocationInFarmChange(it) }, "Ubicación en el campo")


        //Responsables de la tarea
        OutlinedTextFieldWithIcon(responsibleTextField!!,
            { taskViewModel.onResponsibleChange(it) }, "Responsables de la tarea")


        //Prioridad alta
        SwitchWithText("Tiene prioridad alta", taskToCreate!!.highPriority, { taskViewModel.onHighPriorityChange() })
        //Instrucciones detalladas

        OutlinedTextFieldWithIcon(taskToCreate!!.detailedInstructions,
            { taskViewModel.onDetailedInstructionsChange(it) }, "Instrucciones detalladas")

        //Repetición
        SwitchWithText("Se repite", taskToCreate!!.repeatable, { taskViewModel.onRepetitionChange() })
        //Frecuencia de repetición
        if(taskToCreate!!.repeatable) {

            OutlinedNumericField(taskToCreate!!.repetitionIntervalInDays.toString(),
                { taskViewModel.onFrequencyOfRepetitionChange(it) },"Días entre repetición")

        }

        //Recordatorio
        //Botones de guardado y cancelar
        SaveOrCancelbuttonsRow({
            coroutineScope.launch {
                taskViewModel.onSave()
            }}, navController = navController)

        //Mensajito cuando guarda exitosamente
        if(showSnackbarForTaskSaved) {
            navController.popBackStack()
        }
    }
}



@Composable
private fun OutlinedTextFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    descriptionText: String
) {

    OutlinedTextField(
        trailingIcon = {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "Localized description",
                modifier = Modifier.size(25.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(descriptionText) },
        keyboardActions = KeyboardActions(
            onDone = null
        ),
    )
}

@Composable
fun OutlinedNumericField(
    value: String,
    onValueChange: (String) -> Unit,
    descriptionText: String,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = null
        ),
        visualTransformation = VisualTransformation.None,
        label = { Text(descriptionText, style = MaterialTheme.typography.bodyLarge) }
    )
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(showDatePickerDialog: MutableState<Boolean>, taskViewModel: AddTaskViewModel) {
    if (showDatePickerDialog.value) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
        val calendar = Calendar.getInstance()

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
            DatePicker(state = datePickerState,
                dateValidator = { candidate ->
                    // Allow only dates greater than today
                    candidate >= calendar.timeInMillis
                }
                )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogScreen( showTimePicker: MutableState<Boolean>, taskViewModel: AddTaskViewModel) {
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
                modifier = Modifier
                    .fillMaxWidth()
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


// ---------------------------------------------------------- EJEMPLOS
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun InputChipExample(
    text: String = "Hola"
) {
    var enabled = remember { mutableStateOf(true) }
    if (!enabled.value) return

    InputChip(
        onClick = {
            enabled.value = !enabled.value
        },
        label = { Text(text) },
        selected = enabled.value,
        avatar = {
            Icon(
                Icons.Rounded.ShoppingCart,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
        trailingIcon = {
            Icon(
                Icons.Rounded.Close,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
    )
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownMenu() {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var gender by remember {
        mutableStateOf("")
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { newValue ->
            isExpanded = newValue
        }
    ) {
        TextField(
            value = gender,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            placeholder = {
                Text(text = "Please select your gender")
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Male")
                },
                onClick = {
                    gender = "Male"
                    isExpanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Female")
                },
                onClick = {
                    gender = "Female"
                    isExpanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Other")
                },
                onClick = {
                    gender = "Other"
                    isExpanded = false
                }
            )
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableDropdownMenu() {

    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field for correctness.
            modifier = Modifier.menuAnchor(),
            value = selectedOptionText,
            onValueChange = {
                selectedOptionText = it
                expanded = true
                            },
            label = { Text("Label") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        // filter options based on text field value
        val filteringOptions = options.filter { it.contains(selectedOptionText, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
