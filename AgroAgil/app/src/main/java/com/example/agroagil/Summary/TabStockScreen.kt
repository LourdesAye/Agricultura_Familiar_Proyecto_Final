package com.example.agroagil.Summary

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.core.models.EventOperation
import com.example.agroagil.core.models.EventOperationBox
import com.example.agroagil.core.models.EventOperationStock
import com.example.agroagil.core.models.Stock
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

var listItemDataStock = mutableStateListOf<Stock>()
var listItemDataFilterStock = mutableStateListOf<Stock>(
)

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
            .padding(top=20.dp,bottom=20.dp),
        factory = { context ->
            var barchart = HorizontalBarChart(context).apply {
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
            barchart.xAxis.textSize = 20f
            barchart.xAxis.setDrawAxisLine(false);
            barchart.xAxis.setDrawGridLines(false);
            barchart.setBackgroundColor(colorBackground)
            barchart.setDrawBarShadow(false)
            barchart.setDrawGridBackground(false)
            barchart.setDrawBorders(false)
            barchart.axisLeft.isEnabled = false
            barchart.setDrawValueAboveBar(false)
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
        setValueTextColor(Color.White.toArgb())
        setDrawValues(true)

        setValueFormatter(CurrencyValueFormatter("$"))
    }
    return bardata
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
                /*
                Column(
                    modifier = Modifier
                        .background(Color(SelectColorCard(itemData.type).toColorInt()))
                        .width(10.dp)
                        .defaultMinSize(minHeight = heightCard)
                        .fillMaxHeight()
                ) {

                }*/
                Column(){
                    Row(){
                        /*
                        Column(modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 5.dp)) {
                            Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(SolidColor(Color("#00687A".toColorInt())))
                                }
                                Text(text =itemData.getUser().substring(0,2).capitalize(),color= Color.White)
                            }
                        }*/
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    itemData.date,
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
                                            .padding(top=5.dp,bottom=5.dp, start=10.dp)
                                    ) {


                                        Text(itemData.product.name, fontWeight = FontWeight.Bold)
                                        Text("Actualmente tiene "+ itemData.product.amount.toString() + " "+  itemData.product.units+" a $"+itemData.product.price)
                                    }
                                    IconButton(
                                        onClick = {
                                            events.clear()
                                            events.addAll(summaryViewModel.getAllEventsStock(itemData))

                                            expandedEvents = !expandedEvents
                                            /*if (expandedEvents) {
                                                heightCard = 100.dp * (events.size+1)
                                            }else{
                                                heightCard = 100.dp
                                            }*/
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
fun StockSummary(summaryViewModel: SummaryViewModel, navController: NavController){
    //summaryViewModel.init()
    val valuesStocks by summaryViewModel.stocks.observeAsState()
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
        listItemDataFilterStock.clear()
        listItemDataFilterStock.addAll(valuesStocks!!)
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
                    //filterStatus()
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