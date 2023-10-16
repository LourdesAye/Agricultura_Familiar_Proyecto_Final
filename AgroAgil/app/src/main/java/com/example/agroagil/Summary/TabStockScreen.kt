package com.example.agroagil.Summary

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

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

@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun StockSummary(summaryViewModel: SummaryViewModel, navController: NavController){
    //summaryViewModel.init()
    val valuesStocks by summaryViewModel.stocks.observeAsState()
    if (valuesStocks == null){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }

    }else {
        DashboardStock(dataPoints = summaryViewModel.getSummaryDataStock("", ""))
    }
}