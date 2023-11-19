package com.example.agroagil.Summary

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.Buy.ui.Pagado
import com.example.agroagil.Buy.ui.PagadoClick
import com.example.agroagil.Buy.ui.SinPagar
import com.example.agroagil.Buy.ui.SinPagarClick
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
import java.util.Locale

var filters = mutableStateListOf<Function1<List<EventOperation>, List<EventOperation>>>()
var filtersDate = mutableStateListOf<Function1<List<EventOperation>, List<EventOperation>>>()
var listItemData = mutableStateListOf<EventOperation>()
var listItemDataFilter = mutableStateListOf<EventOperation>(
)
var clickPagado = mutableStateOf(false)
var colorPagado = mutableStateOf<Color>(Color(0))
var clickSinPagar = mutableStateOf(false)
var colorSinPagar = mutableStateOf<Color>(Color(0))

fun filterInput(events:List<EventOperation>): List<EventOperation> {
    return events.filter { it -> it.type=="Sell" }
}

fun filterOutput(events:List<EventOperation>): List<EventOperation> {
    return events.filter { it -> it.type=="Buy" }
}

class CurrencyValueFormatter(private val currencySymbol: String) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "$currencySymbol${String.format(Locale.getDefault(), "%.0f", value)}"
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
        var filtroExecute = mutableListOf<List<EventOperation>>()
        filtroExecute.addAll(listOf(filters[i](listItemData)))
        listItemDataFilter.addAll(filtroExecute.flatten())
    }
    for (i in 0 .. filtersDate.size-1) {
        var filtroExecute = mutableListOf<List<EventOperation>>()
        filtroExecute.addAll(listOf(filtersDate[i](listItemDataFilter)))
        listItemDataFilter.clear()
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
@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneOperation(itemData: EventOperation, navController: NavController, summaryViewModel: SummaryViewModel ){
    var expandedEvents  by remember{ mutableStateOf(false) }
    var iconAction by remember { mutableStateOf(Icons.Filled.KeyboardArrowDown) }
    var heightCard by remember { mutableStateOf(100.dp) }
    val events by remember { mutableStateOf(mutableStateListOf<EventOperationBox>()) }
    Column(){
        Card(
            onClick={
            },
            modifier = Modifier
                .defaultMinSize(minHeight = 100.dp)
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )

        ){
            Row(
            ) {
                Column(
                    modifier = Modifier
                        .background(Color(SelectColorCard(itemData.type).toColorInt()))
                        .width(10.dp)
                        .defaultMinSize(minHeight = heightCard)
                        .fillMaxHeight()
                ) {

                }
                Column(){
                    Row(){
                        Column(modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 5.dp)) {
                            Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(SolidColor(Color("#00687A".toColorInt())))
                                }
                                Text(text =itemData.getUser().substring(0,2).capitalize(),color= Color.White)
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    itemData.getDate(),
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(5.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {


                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp)
                                    ) {


                                        Text(itemData.getUser(), fontWeight = FontWeight.Bold)
                                        var description = ""
                                        for (i in 0..itemData.getItems().size - 1) {
                                            description += itemData.getItems()[i].amount.toString() + " " + itemData.getItems()[i].name + ", "
                                        }
                                        if (description.length > 70)
                                            description = description.substring(0, 69) + "..."
                                        else
                                            description = description.substring(0, description.length - 2)
                                        Text(description)
                                    }
                                    IconButton(
                                        onClick = {
                                            events.clear()
                                            events.addAll(summaryViewModel.getAllEvents(itemData))

                                            expandedEvents = !expandedEvents
                                            if (expandedEvents) {
                                                heightCard = 100.dp * (events.size+1)
                                            }else{
                                                heightCard = 100.dp
                                            }
                                            iconAction =  if(expandedEvents == true) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                                        },
                                        modifier = Modifier.padding(end = 5.dp)
                                    ) {
                                        Icon(
                                            iconAction,
                                            contentDescription = "Localized description",
                                            modifier = Modifier.size(50.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = expandedEvents) {
                        Column(modifier = Modifier.padding(top=20.dp,start=10.dp, end= 10.dp)) {

                            events.map{
                                Column(modifier = Modifier.defaultMinSize(minHeight = 100.dp)) {


                                    Divider(modifier = Modifier.padding(start = 20.dp, end =20.dp))

                                    Column(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 5.dp, bottom = 20.dp, top = 5.dp)) {
                                        Text(
                                            it.date,
                                            fontSize = 10.sp,
                                            modifier = Modifier
                                                .align(Alignment.End)
                                                .padding(5.dp)
                                        )


                                        Column() {
                                            Text(it.typeEvent, fontWeight = FontWeight.Bold)
                                            Text(it.nameUser+" ejecuto la accion")
                                        }
                                    }
                                }
                            }
                        }
                    }



                }
            }

        }
    }
}


@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun BoxSummary(summaryViewModel: SummaryViewModel, navController: NavController){
    val valuesSell by summaryViewModel.GetSell().observeAsState()
    val valuesBuy by summaryViewModel.GetBuy().observeAsState()
    val valuesEvents by summaryViewModel.events.observeAsState()
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
        var items = valuesBuy!!.map { EventOperation("Buy", null,it) }.toMutableList()
        items.addAll(valuesSell!!.map { EventOperation("Sell", it,null) })
        listItemData.clear()
        listItemData.addAll(items!!)
        resetFilter()

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

                OneOperation(it, navController, summaryViewModel)
            }
        }
    }
    }
}