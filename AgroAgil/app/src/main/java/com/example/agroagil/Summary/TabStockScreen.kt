package com.example.agroagil.Summary

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.agroagil.core.models.EventOperationStock
import com.example.agroagil.core.models.Stock
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat

var filtersStock = mutableStateListOf<Function1<List<Stock>, List<Stock>>>()
var filtersDateStock = mutableStateListOf<Function1<List<Stock>, List<Stock>>>()
var listItemDataStock = mutableStateListOf<Stock>()
var listItemDataFilterStock = mutableStateListOf<Stock>(
)
var summaryViewModelCurrent = mutableStateOf<SummaryViewModel?>(null)

fun filterHerramienta(events:List<Stock>): List<Stock> {
    return events.filter { it -> it.type=="Herramienta" }
}

fun filterFertilizante(events:List<Stock>): List<Stock> {
    return events.filter { it -> it.type=="Fertilizante" }
}

fun filterCultivo(events:List<Stock>): List<Stock> {
    return events.filter { it -> it.type=="Cultivo" }
}

fun filterSemilla(events:List<Stock>): List<Stock> {
    return events.filter { it -> it.type=="Semilla" }
}
@SuppressLint("SimpleDateFormat")
fun filterDatesStock(events:List<Stock>): List<Stock> {
    val date_format = SimpleDateFormat("yyyy/MM/dd")
    val filter_date = date_format.parse(dataDateStart.value)
    val filterDateEnd = date_format.parse(dataDateEnd.value)
    return events.filter { event ->
        var eventsStock = summaryViewModelCurrent.value!!.getAllEventsStock(event)
        var eventsDate = eventsStock.filter{
            var date_event = date_format.parse( it.date.split(" ")[0])
            (filter_date.before(date_event) or filter_date.equals(date_event)
                    )and (filterDateEnd.after(date_event) or filterDateEnd.equals(date_event)
                    )
        }
        eventsDate.size >0
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun DashboardStock(dataPoints: List<Pair<String, Double>>) {
    var color = MaterialTheme.colorScheme.secondary.toArgb()
    var colorBackground = MaterialTheme.colorScheme.background.toArgb()
    var chart = mutableStateOf(DashboardStockCreateChart(dataPoints,color))
    var bars = dataPoints.map { it.first }
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
            barchart.data.setValueTextSize(10f)
            barchart.xAxis.setLabelCount(bars.size, false);
            barchart.xAxis.setValueFormatter(IndexAxisValueFormatter(bars));
            barchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barchart.xAxis.textSize = 10f
            barchart.xAxis.setDrawAxisLine(false);
            barchart.xAxis.setDrawGridLines(false);
            barchart.setBackgroundColor(colorBackground)
            barchart.setDrawBarShadow(false)
            barchart.setDrawGridBackground(false)
            barchart.setDrawBorders(false)
            barchart.axisLeft.isEnabled = false
            //barchart.setDrawValueAboveBar(false)
            //barchart.axisLeft.valueFormatter = CurrencyValueFormatter("$")
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
fun resetFilterStock(){
    listItemDataFilterStock.clear()
    if (filtersStock.size ==0){
        listItemDataFilterStock.addAll(listItemDataStock)
    }
    for (i in 0 .. filtersStock.size-1) {
        var filtroExecute = mutableListOf<List<Stock>>()
        filtroExecute.addAll(listOf(filtersStock[i](listItemDataStock)))
        listItemDataFilterStock.addAll(filtroExecute.flatten())
    }
    for (i in 0 .. filtersDateStock.size-1) {
        var filtroExecute = mutableListOf<List<Stock>>()
        filtroExecute.addAll(listOf(filtersDateStock[i](listItemDataFilterStock)))
        listItemDataFilterStock.clear()
        listItemDataFilterStock.addAll(filtroExecute.flatten())

    }
}
private fun DashboardStockCreateChart(dataPoints: List<Pair<String, Double>>, color: Int): BarData {
    val entries = dataPoints.mapIndexed { index, pair ->
        BarEntry(index.toFloat(), pair.second.toFloat(),pair.first)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filterCategory(){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = with(LocalDensity.current) {
        screenWidth * 0.44f
    }
    var clickHerramienta  by remember{ mutableStateOf(false)}
    var colorHerramenta by remember{  mutableStateOf<Color>(Color(0))}
    var clickSemilla  by remember{ mutableStateOf(false)}
    var colorSemilla by remember{  mutableStateOf<Color>(Color(0))}
    var clickFertilizante  by remember{ mutableStateOf(false)}
    var colorFertilizante by remember{  mutableStateOf<Color>(Color(0))}
    var clickCultivo  by remember{ mutableStateOf(false)}
    var colorCultivo by remember{  mutableStateOf<Color>(Color(0))}
    Column(modifier = Modifier.padding(top = 15.dp)) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier= Modifier
            .fillMaxWidth()
            ) {
        if (clickHerramienta) {
            colorHerramenta = Color(PagadoClick.toColorInt())
        } else {
            colorHerramenta = Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickHerramienta = !clickHerramienta
                if (clickHerramienta) {
                    filtersStock.add(::filterHerramienta)
                } else {
                    filtersStock.remove(::filterHerramienta)
                }
                resetFilterStock()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorHerramenta)
        ) {
            Row() {
                Row(
                    modifier = Modifier.fillMaxSize().padding(top = 15.dp,bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround

                ) {
                    Text(
                        "Herramientas",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    if (clickHerramienta) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterVertically)
                        )
                    }

                }
            }
        }
        if (clickSinPagar.value) {
            colorSinPagar.value = Color(SinPagarClick.toColorInt())
        } else {
            colorSinPagar.value = Color(MaterialTheme.colorScheme.background.value)
        }
        if (clickFertilizante) {
            colorFertilizante = Color(PagadoClick.toColorInt())
        } else {
            colorFertilizante = Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickFertilizante = !clickFertilizante
                if (clickFertilizante) {
                    filtersStock.add(::filterFertilizante)
                } else {
                    filtersStock.remove(::filterFertilizante)
                }
                resetFilterStock()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorFertilizante)
        ) {
            Row() {
                Row(
                    modifier = Modifier.fillMaxSize().padding(top = 15.dp,bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround

                ) {
                    Text(
                        "Fertilizante",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    if (clickFertilizante) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterVertically)
                        )
                    }

                }
            }
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier= Modifier
            .fillMaxWidth()){

        if (clickSemilla) {
            colorSemilla = Color(PagadoClick.toColorInt())
        } else {
            colorSemilla = Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickSemilla = !clickSemilla
                if (clickSemilla) {
                    filtersStock.add(::filterSemilla)
                } else {
                    filtersStock.remove(::filterSemilla)
                }
                resetFilterStock()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorSemilla)
        ) {
            Row() {
                Row(
                    modifier = Modifier.fillMaxSize().padding(top = 15.dp,bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround

                ) {
                    Text(
                        "Semillas",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    if (clickSemilla) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterVertically)
                        )
                    }

                }
            }
        }

        if (clickCultivo) {
            colorCultivo = Color(PagadoClick.toColorInt())
        } else {
            colorCultivo = Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickCultivo = !clickCultivo
                if (clickCultivo) {
                    filtersStock.add(::filterCultivo)
                } else {
                    filtersStock.remove(::filterCultivo)
                }
                resetFilterStock()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorCultivo)
        ) {
            Row() {
                Row(
                    modifier = Modifier.fillMaxSize().padding(top = 15.dp,bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround

                ) {
                    Text(
                        "Cultivo",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    if (clickCultivo) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterVertically)
                        )
                    }

                }
            }
        }



    }
    }
}
@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneOperationStock(itemData: Stock, navController: NavController, summaryViewModel: SummaryViewModel ){
    var expandedEvents  by remember{ mutableStateOf(false) }
    var iconAction by remember { mutableStateOf(Icons.Filled.KeyboardArrowDown) }
    var heightCard by remember { mutableStateOf(100.dp) }
    val events by remember { mutableStateOf(mutableStateListOf<EventOperationStock>()) }
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
                Column(){
                    Row(){
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                /*Text(
                                    itemData.date,
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(5.dp)
                                )*/
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    LocalConfiguration.current.densityDpi
                                    Column(
                                        modifier = Modifier
                                            .padding(top=20.dp,bottom=5.dp, start=10.dp).fillMaxWidth(0.9f)
                                    ) {


                                        Text(itemData.product.name, fontWeight = FontWeight.Bold)
                                        Text("Actualmente tiene "+ itemData.product.amount.toString() + " "+  itemData.product.units+" a $"+itemData.product.price)
                                    }
                                    IconButton(
                                        onClick = {
                                            events.clear()
                                            events.addAll(summaryViewModel.getAllEventsStock(itemData))

                                            expandedEvents = !expandedEvents
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
                            var eventsFilter = mutableStateListOf<EventOperationStock>()
                            eventsFilter.clear()
                            eventsFilter.addAll(events)
                            if (filtersDateStock.contains(::filterDatesStock) ){
                                val date_format = SimpleDateFormat("yyyy/MM/dd")
                                val filter_date = date_format.parse(dataDateStart.value)
                                val filterDateEnd = date_format.parse(dataDateEnd.value)
                                val eventsCurrent = eventsFilter.filter{
                                    val date_event = date_format.parse( it.date.split(" ")[0])
                                    (filter_date.before(date_event) or filter_date.equals(date_event)
                                            )and (filterDateEnd.after(date_event) or filterDateEnd.equals(date_event)
                                            )
                                }
                                eventsFilter.clear()
                                eventsFilter.addAll(eventsCurrent)
                            }
                            eventsFilter.map{
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
fun StockSummary(summaryViewModel: SummaryViewModel, navController: NavController){
    summaryViewModelCurrent.value = summaryViewModel
    val valuesStocks by summaryViewModel.GetStock().observeAsState()
    val valuesEvents by summaryViewModel.eventsStock.observeAsState()
    if (valuesStocks == null || valuesEvents == null){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }

    }else {
        listItemDataStock.clear()
        listItemDataStock.addAll(valuesStocks!!)
        resetFilterStock()
        Column(modifier = Modifier.padding(start=20.dp, end= 20.dp)){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Row() {
                    DashboardStock(dataPoints = summaryViewModel.getSummaryDataStock("", ""))
                }
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    filterCategory()
                }
                Text(
                    "Total de productos: " + listItemDataFilterStock.size.toString(),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            this.items(listItemDataFilterStock) {

                OneOperationStock(it, navController, summaryViewModel)
            }

        }
        }
    }
}