package com.example.agroagil.Summary

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.Buy.ui.Pagado
import com.example.agroagil.Buy.ui.PagadoClick
import com.example.agroagil.Buy.ui.SinPagar
import com.example.agroagil.Buy.ui.SinPagarClick
import com.example.agroagil.R
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.EventOperation
import com.example.agroagil.core.models.EventOperationBox
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

var expandedFilter = mutableStateOf(false)
val titles = listOf("Caja", "Almacén")
var dataDateStart = mutableStateOf("")
var dataDateEnd = mutableStateOf("")
var dateFilterChip = mutableStateOf(false)
var dateCustomDate= mutableStateOf(false)
var dataDateStartCustom = mutableStateOf("")
var dataDateEndCustom = mutableStateOf("")

@SuppressLint("SimpleDateFormat")
fun filterDates(events:List<EventOperation>): List<EventOperation> {
    val date_format = SimpleDateFormat("yyyy/MM/dd")
    val date_format_buy = SimpleDateFormat("dd/MM/yyyy")
    val filter_date = date_format.parse(dataDateStart.value)
    val filterDateEnd = date_format.parse(dataDateEnd.value)
    return events.filter { event ->
        var date_event = date_format_buy.parse(event.getDate().split(" ")[0])
        (filter_date.before(date_event) or filter_date.equals(date_event)
                )and (filterDateEnd.after(date_event) or filterDateEnd.equals(date_event)
                )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormattedDateInputField(
) {
    val cursor = remember { mutableStateOf(0) }
    val formatter: (String) -> String = { value ->
        val digits = value.filter { it.isDigit() }
        var text =""
        buildString {
            if (digits.length >= 4) {
                text +="${digits.substring(0..3)}"
            } else{
                text +=digits

            }
            if (digits.length >= 6) {
                text +="/${digits.substring(4..5)}"
            }else{
                if (digits.length > 4) {
                    text +="/${digits.substring(4..(digits.length-1))}"
                }
            }
            if (digits.length >= 8) {
                text +="/${digits.substring(6..7)}"
            }else{
                if (digits.length > 6) {
                    text +="/${digits.substring(6..(digits.length-1))}"
                }
            }
            append(text)
            cursor.value = text.length
            append("YYYY/MM/DD".substring(text.length,"YYYY/MM/DD".length ))
        }

    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            label={Text("Filtrar por fecha de inicio")},
            value = TextFieldValue(dataDateStartCustom.value, TextRange(cursor.value)),
            onValueChange = {
                // Remove any non-digit characters
                val formatted = formatter(it.text)
                dataDateStartCustom.value = formatted

            },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            placeholder = { Text(text = "YYYY/MM/DD") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormattedDateInputFieldEnd(
) {
    val cursor = remember { mutableStateOf(0) }
    val formatter: (String) -> String = { value ->
        val digits = value.filter { it.isDigit() }
        var text =""
        buildString {
            if (digits.length >= 4) {
                text +="${digits.substring(0..3)}"
            } else{
                text +=digits

            }
            if (digits.length >= 6) {
                text +="/${digits.substring(4..5)}"
            }else{
                if (digits.length > 4) {
                    text +="/${digits.substring(4..(digits.length-1))}"
                }
            }
            if (digits.length >= 8) {
                text +="/${digits.substring(6..7)}"
            }else{
                if (digits.length > 6) {
                    text +="/${digits.substring(6..(digits.length-1))}"
                }
            }
            append(text)
            cursor.value = text.length
            append("YYYY/MM/DD".substring(text.length,"YYYY/MM/DD".length ))
        }

    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            label={Text("Filtrar por fecha de fin")},
            value = TextFieldValue(dataDateEndCustom.value, TextRange(cursor.value)),
            onValueChange = {
                // Remove any non-digit characters
                val formatted = formatter(it.text)
                dataDateEndCustom.value = formatted

            },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            placeholder = { Text(text = "YYYY/MM/DD") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun DialogCustomDate(){
    if (dateCustomDate.value) {
        AlertDialog(
            modifier= Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onDismissRequest = {
                dateCustomDate.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dataDateStart.value = dataDateStartCustom.value
                        dataDateEnd.value = dataDateEndCustom.value
                        var checkStartDate = !dataDateStart.value.equals("") and !dataDateStart.value.contains("D")
                        var checkEndDate = !dataDateEnd.value.equals("") and !dataDateEnd.value.contains("D")
                        if (checkStartDate || checkEndDate){
                            if(!checkStartDate){
                                dataDateStart.value = dataDateEnd.value
                            }
                            if(!checkEndDate){
                                dataDateEnd.value = dataDateStart.value
                            }
                            filtersDate.add(::filterDates)
                            filtersDateStock.add(::filterDatesStock)
                            dateFilterChip.value = true
                        }
                        expandedFilter.value = false
                        dateCustomDate.value = false
                    }
                ) {

                    Text("Buscar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dateCustomDate.value = false
                    }
                ) {
                    Text("No, Cancelar")
                }
            },

            text = {
                Column {
                    FormattedDateInputField()
                    FormattedDateInputFieldEnd()
                }
            }
        )

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun SummaryScreen(summaryViewModel: SummaryViewModel, navController: NavController) {
    var state by remember { mutableStateOf(0) }
    Column(modifier = Modifier
        .fillMaxSize()) {
        TabRow(selectedTabIndex = state,
            ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = {
                        state = index
                        if (state == 0 && dateFilterChip.value){
                            filtersDate.clear()
                            filtersDate.add(::filterDates)
                        }
                        if (state == 1 && dateFilterChip.value){
                            filtersDateStock.clear()
                            filtersDateStock.add(::filterDatesStock)
                        }
                              },
                    text = { Text(text = title, fontSize = 20.sp, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                Button(onClick = { expandedFilter.value = !expandedFilter.value },) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.filter),
                        contentDescription = "Localized description",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Fechas")
                }

                DropdownMenu(
                    expanded = expandedFilter.value,
                    onDismissRequest = { expandedFilter.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Hoy") },
                        onClick = {
                            val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                                .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)
                            dataDateStart.value = currentDate
                            dataDateEnd.value = currentDate
                            filtersDate.add(::filterDates)
                            filtersDateStock.add(::filterDatesStock)
                            dateFilterChip.value = true
                            expandedFilter.value = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Ultimo mes") },
                        onClick = {
                            var currentDateTime = Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"))
                            currentDateTime.set(Calendar.DAY_OF_MONTH, 1);
                            currentDateTime.add(Calendar.MONTH, -1);
                            dataDateStart.value = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                                .format(currentDateTime.time)
                            currentDateTime = Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"))
                            currentDateTime.add(Calendar.MONTH, -1);
                            currentDateTime.set(Calendar.DAY_OF_MONTH, currentDateTime.getActualMaximum(Calendar.DAY_OF_MONTH))
                            dataDateEnd.value = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                                .format(currentDateTime.time)
                            filtersDate.add(::filterDates)
                            filtersDateStock.add(::filterDatesStock)
                            dateFilterChip.value = true
                            expandedFilter.value = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Personalizado") },
                        onClick = { dateCustomDate.value=true},
                    )
                }
            }
        }
        DialogCustomDate()
            Box( modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd)
                .padding(end = 15.dp)) {
                if (dateFilterChip.value) {
                    InputChip(
                        selected = false,
                        onClick = {
                            filtersDate.remove(::filterDates)
                            filtersDateStock.remove(::filterDatesStock)
                            resetFilter()
                            resetFilterStock()
                            dateFilterChip.value = false
                        },
                        label = { Text(dataDateStart.value + " - " + dataDateEnd.value) },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Localized description"
                            )
                        }
                    )
                }
            }
        if (state == 0){
            BoxSummary(summaryViewModel,navController)
        }else{
                StockSummary(summaryViewModel,navController)

        }

    }
}