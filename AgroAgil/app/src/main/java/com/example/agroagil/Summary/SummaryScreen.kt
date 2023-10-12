package com.example.agroagil.Summary

import android.annotation.SuppressLint
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
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
import com.example.agroagil.Buy.ui.dataDateEnd
import com.example.agroagil.Buy.ui.dataDateStart
import com.example.agroagil.R
import com.example.agroagil.core.models.Buy
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


val titles = listOf("Caja", "Tarea", "Stock")
var filters = mutableStateListOf<Function1<List<EventOperationBox>, List<EventOperationBox>>>()
var listItemData = mutableStateListOf<EventOperationBox>()
var listItemDataFilter = mutableStateListOf<EventOperationBox>(
)
var clickPagado = mutableStateOf(false)
var colorPagado = mutableStateOf<Color>(Color(0))
var clickSinPagar = mutableStateOf(false)
var colorSinPagar = mutableStateOf<Color>(Color(0))
var dataDateStart = mutableStateOf("")
var dataDateEnd = mutableStateOf("")
var dateFilterChip = mutableStateOf(false)
fun filterInput(events:List<EventOperationBox>): List<EventOperationBox> {
    return events.filter { it -> it.operation=="Sell" }
}

fun filterOutput(events:List<EventOperationBox>): List<EventOperationBox> {
    return events.filter { it -> it.operation=="Buy" }
}

@SuppressLint("SimpleDateFormat")
fun filterDates(events:List<EventOperationBox>): List<EventOperationBox> {
    val date_format = SimpleDateFormat("yyyy/MM/dd")
    val date_format_buy = SimpleDateFormat("dd/MM/yyyy")
    val filter_date = date_format.parse(dataDateStart.value)
    val filterDateEnd = date_format.parse(dataDateEnd.value)
    return events.filter { event ->
        var date_event = date_format_buy.parse(event.date.split(" ")[0])
        (filter_date.before(date_event) or filter_date.equals(date_event)
                )and (filterDateEnd.after(date_event) or filterDateEnd.equals(date_event)
                )
    }
}

class CurrencyValueFormatter(private val currencySymbol: String) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "$currencySymbol${String.format(Locale.getDefault(), "%.2f", value)}"
    }
}
@SuppressLint("UnrememberedMutableState")
@Composable
fun BarChartDemo(dataPoints: List<Pair<String, Float>>) {
    var color = MaterialTheme.colorScheme.secondary.toArgb()
    var colorBackground = MaterialTheme.colorScheme.background.toArgb()
    var chart = mutableStateOf(createChart(dataPoints,color))
    var bars = mutableStateListOf<String>("Ingresos", "Egresos")
    var screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var dashboardHeight = with(LocalDensity.current) {
        screenHeight * 0.5f
    }
    //chart.value.setValueTextSize(50f)
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(dashboardHeight)
            .padding(top = 20.dp, bottom = 20.dp),
        factory = { context ->
            var barchart = BarChart(context).apply {
                setBackgroundColor(Color.White.toArgb())
                data = chart.value
                axisLeft.axisMinimum = 0f
                axisRight.axisMinimum = 0f
                setDrawGridBackground(false)
                setDrawValueAboveBar(true)
                legend.isEnabled = false
                description.isEnabled = false

            }
            barchart.data.setValueTextSize(25f)
            barchart.xAxis.setLabelCount(bars.size, false);
            barchart.xAxis.setValueFormatter(IndexAxisValueFormatter(bars));
            barchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barchart.xAxis.textSize = 50f
            barchart.xAxis.setDrawAxisLine(false);
            barchart.xAxis.setDrawGridLines(false);
            barchart.setBackgroundColor(colorBackground)
            barchart.setDrawBarShadow(false)
            barchart.setDrawGridBackground(false)
            barchart.setDrawBorders(false)
            barchart.axisLeft.valueFormatter = CurrencyValueFormatter("$")
            var leftAxis: YAxis = barchart.getAxisLeft()
            leftAxis.setDrawAxisLine(false);
            leftAxis.textSize = 15f
            val rightAxis: YAxis = barchart.getAxisRight()
            rightAxis.setDrawAxisLine(false)
            rightAxis.setDrawLabels(false)
            barchart
        },
        update = { chartView ->
           // chartView.data = chart.data
            chartView.notifyDataSetChanged()
            chartView.invalidate()
        }
    )
}

private fun createChart(dataPoints: List<Pair<String, Float>>, color: Int): BarData {
    val entries = dataPoints.mapIndexed { index, pair ->
        BarEntry(index.toFloat(), pair.second, pair.first)
    }

    val dataSet = BarDataSet(entries, "Data")
    dataSet.color = color
    dataSet.setDrawValues(false)
    dataSet.setDrawIcons(false)

    var bardata =  BarData(dataSet).apply {
        barWidth = 0.5f
        setValueTextColor(Color.Black.toArgb())
        setDrawValues(true)
        setValueFormatter(CurrencyValueFormatter("$"))
    }
    return bardata
}
fun resetFilter(){
    listItemDataFilter.clear()
    if (filters.size ==0){
        listItemDataFilter.addAll(listItemData)
    }
    for (i in 0 .. filters.size-1) {
        var filtroExecute = mutableListOf<List<EventOperationBox>>()
        filtroExecute.addAll(listOf(filters[i](listItemData)))
        listItemDataFilter.addAll(filtroExecute.flatten())
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filterStatus(){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier= Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardWidth =  with(LocalDensity.current) {
            screenWidth * 0.45f
        }
        if (clickPagado.value){
            colorPagado.value = Color(PagadoClick.toColorInt())
        }else{
            colorPagado.value=Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick={
                clickPagado.value = !clickPagado.value
                if(clickPagado.value){
                    filters.add(::filterInput)
                }else{
                    filters.remove(::filterInput)
                }
                resetFilter()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorPagado.value)
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(Pagado.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Ingresos", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    if(clickPagado.value){
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterEnd)
                        )
                    }

                }
            }
        }
        if (clickSinPagar.value){
            colorSinPagar.value = Color(SinPagarClick.toColorInt())
        }else{
            colorSinPagar.value=Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickSinPagar.value=!clickSinPagar.value
                if(clickSinPagar.value){
                    filters.add(::filterOutput)
                }else{
                    filters.remove(::filterOutput)
                }
                resetFilter()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorSinPagar.value)
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(SinPagar.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Egresos", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    if(clickSinPagar.value){
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }



    }
}
fun SelectColorCard(operation:String): String {
    var color: String
    if (operation=="Sell"){
        color = Pagado
    }else{
        color = SinPagar
    }
    return color
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneOperation(itemData: EventOperationBox, navController: NavController){
    Column(modifier = Modifier.padding(bottom=5.dp)){
        Card(
            onClick={
                //navController.navigate("buy/${listItemData.indexOf(itemData)}/info")
            },
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )

        ){
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(SelectColorCard(itemData.operation).toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Column(modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()) {

                    Text(itemData.date, fontSize=10.sp, modifier = Modifier.align(Alignment.End))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier= Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)) {
                        Text(itemData.typeEvent, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterVertically))
                        Text("$"+itemData.amount.toString(), modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }

        }
    }
}
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun BoxSummary(summaryViewModel: SummaryViewModel, navController: NavController){
    Column(modifier = Modifier.padding(start=20.dp, end=20.dp)){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Row(){
                    BarChartDemo(
                        dataPoints = summaryViewModel.getSummaryData("","")
                    )
                }
                Row(modifier = Modifier.padding(bottom=20.dp)){
                    filterStatus()
                }
                Text("Total de operaciones: "+listItemDataFilter.size.toString(), modifier = Modifier.padding(bottom=10.dp))
            }
            this.items(listItemDataFilter) {

                OneOperation(it, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun SummaryScreen(summaryViewModel: SummaryViewModel, navController: NavController) {
    //var valuesSell= summaryViewModel.sells.observeAsState().value
    //    var valuesBuy = summaryViewModel.buys.observeAsState().value
    //    var valuesEvents = summaryViewModel.events.observeAsState().value
    val valuesSell by summaryViewModel.sells.observeAsState()
    val valuesBuy by summaryViewModel.buys.observeAsState()
    val valuesEvents by summaryViewModel.events.observeAsState()
    var state by remember { mutableStateOf(0) }
    var expandedFilter by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxSize()) {
        TabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    text = { Text(text = title, fontSize = 20.sp, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }

        Column(){
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp)){
        Button(onClick = {expandedFilter=!expandedFilter}){
            Icon(
                ImageVector.vectorResource(R.drawable.filter),
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Fechas")
        }

            DropdownMenu(
                expanded = expandedFilter,
                onDismissRequest = { expandedFilter = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Hoy") },
                    onClick = {
                        val currentDateTime = LocalDateTime.now()
                        dataDateStart.value = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        dataDateEnd.value = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        filters.add(::filterDates)
                        dateFilterChip.value = true
                        expandedFilter = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Ultimo mes") },
                    onClick = {
                        val currentDateTime = LocalDateTime.now()
                        val firstDayOfLastMonth = currentDateTime.minusMonths(1).withDayOfMonth(1)
                        val lastDayOfLastMonth =currentDateTime.withDayOfMonth(1).minusDays(1)
                        dataDateStart.value = firstDayOfLastMonth.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        dataDateEnd.value = lastDayOfLastMonth.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        filters.add(::filterDates)
                        dateFilterChip.value = true
                        expandedFilter = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("Personalizado") },
                    onClick = { /* Handle settings! */ },
                )
            }}}
            Box( modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd)
                .padding(end = 15.dp)) {
                if (dateFilterChip.value) {
                    InputChip(
                        selected = false,
                        onClick = {
                            filters.remove(::filterDates)
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
            if (valuesSell == null || valuesBuy == null || valuesEvents == null){
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {}
                            .padding(10.dp)
                    )
                }

            }else {
                listItemData.clear()
                listItemData.addAll(valuesEvents!!)
                resetFilter()
                BoxSummary(summaryViewModel, navController)
            }
        }else{
            if (state == 1){

            }else{

            }
        }

    }
}