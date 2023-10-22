package com.example.agroagil.Stock.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.agroagil.R
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
//import com.example.agroagil.Buy.ui.BuyViewModel
//import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Stock
import java.text.SimpleDateFormat

val SinStock = "#A93226"
val SinPagarClick = "#f4e5e4"
val ConStock = "#28B463"
val PagadoClick = "#d7f1e2"

var listStockDataFilter = mutableStateListOf<Stock>()

var filters = mutableStateListOf<Function1<List<Stock>, List<Stock>>>()
var filtersExclude = mutableStateListOf<Function1<List<Stock>, List<Stock>>>()
var chipsFilter = mutableStateListOf<Map<String,Function1<List<Stock>, List<Stock>>>>()

var nombreElementoDeStockFilter = mutableStateOf("")
var dataDateStart = mutableStateOf("")
var dataDateEnd = mutableStateOf("")


fun filterTieneStock(buys:List<Stock>): List<Stock> {
    return buys.filter { it -> it.product.amount>0 }
}

fun filterSinStock(buys:List<Stock>): List<Stock> {
    return buys.filter { it -> it.product.amount<=0 }
}
fun filterNombreProductoDelStock(buys:List<Stock>): List<Stock> {
    //para filtrar según nombre del producto, que puede ser pera,naranja,camion,tractor
    return buys.filter { it -> it.product.name.lowercase().contains(nombreElementoDeStockFilter.value.lowercase()) }
}
fun filterAllBuys(buys:List<Stock>): List<Stock> {
    return buys
}

fun filterDateStart(buys:List<Stock>): List<Stock> {
    var date_format = SimpleDateFormat("yyyy/MM/dd")
    var date_format_buy = SimpleDateFormat("dd/MM/yyyy")
    var filter_date = date_format.parse(dataDateStart.value)
    return buys.filter { buy ->
        var date_buy = date_format_buy.parse(buy.date.split(" ")[0])
        filter_date.before(date_buy) or filter_date.equals(date_buy)
    }
}

fun filterDateEnd(buys:List<Stock>): List<Stock> {
    var date_format = SimpleDateFormat("yyyy/MM/dd")
    var date_format_buy = SimpleDateFormat("dd/MM/yyyy")
    var filter_date = date_format.parse(dataDateEnd.value)
    return buys.filter { buy ->
        var date_buy = date_format_buy.parse(buy.date.split(" ")[0])
        filter_date.after(date_buy) or filter_date.equals(date_buy) }
}

fun filterDateRange(buys:List<Stock>): List<Stock> {
    var date_format = SimpleDateFormat("yyyy/MM/dd")
    var date_format_buy = SimpleDateFormat("dd/MM/yyyy")
    var filter_date_end = date_format.parse(dataDateEnd.value)
    var filter_date_start = date_format.parse(dataDateStart.value)
    return buys.filter { buy ->
        var date_buy = date_format_buy.parse(buy.date.split(" ")[0])
        (filter_date_start.before(date_buy) or filter_date_start.equals(date_buy)) and
                ( filter_date_end.after(date_buy) or filter_date_end.equals(date_buy))
    }
}

fun resetFilter(){
    listStockDataFilter.clear()
    if (filters.size ==0){
        listStockDataFilter.addAll(listStockDataFilter)
    }
    for (i in 0 .. filters.size-1) {
        var filtroExecute = mutableListOf<List<Stock>>()
        filtroExecute.addAll(listOf(filters[i](listStockDataFilter)))
        listStockDataFilter.addAll(filtroExecute.flatten())
    }
}

fun resetFilterExclude(){
    for (i in 0 .. filtersExclude.size-1) {
        var filtroExecute = mutableListOf<List<Stock>>()
        filtroExecute.addAll(listOf(filtersExclude[i](listStockDataFilter)))
        listStockDataFilter.clear()
        listStockDataFilter.addAll(filtroExecute.flatten())
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
            value = TextFieldValue(dataDateStart.value, TextRange(cursor.value)),
            onValueChange = {
                // Remove any non-digit characters
                val formatted = formatter(it.text)
                dataDateStart.value = formatted

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
            value = TextFieldValue(dataDateEnd.value, TextRange(cursor.value)),
            onValueChange = {
                // Remove any non-digit characters
                val formatted = formatter(it.text)
                dataDateEnd.value = formatted

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
fun Actions(navController: NavController){
    var expandedFilter by remember { mutableStateOf(false) }
    var userName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    }
    var selectedLent by remember { mutableStateOf(false) }
    var selectedWasLent by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(false) }

    Column {
        Row(

            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 5.dp, start = 0.dp, end = 0.dp)
                .fillMaxWidth()
        ) {
            Button(onClick = { expandedFilter = !expandedFilter},colors= if (expandedFilter) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(), modifier = Modifier.fillMaxWidth()) {
                Icon(
                    ImageVector.vectorResource(R.drawable.filter),
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Filtrar")
            }
        }
        Column(modifier=Modifier.padding(end = 10.dp, start = 10.dp, bottom = 5.dp)){
            AnimatedVisibility(visible = expandedFilter) {
                Column(modifier = Modifier.fillMaxWidth()){
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column (modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(30.dp)){
                            OutlinedTextField(
                                value = userName,
                                onValueChange = { userName = it },
                                label = { Text("Producto")},
                                modifier=Modifier.fillMaxWidth()
                            )
                            FormattedDateInputField()
                            FormattedDateInputFieldEnd()
                            ExtendedFloatingActionButton(onClick = {
                                filtersExclude.removeIf { it.equals(::filterNombreProductoDelStock) or
                                        it.equals(::filterDateRange) or it.equals(::filterDateStart) or it.equals(::filterDateEnd)
                                }
                                chipsFilter.clear()
                                if (userName.text!=""){
                                    chipsFilter.add(mapOf(("Usuario: "+userName.text) to ::filterNombreProductoDelStock))
                                    filtersExclude.add(::filterNombreProductoDelStock)
                                    nombreElementoDeStockFilter.value = userName.text
                                }
                                var checkStartDate = !dataDateStart.value.equals("") and !dataDateStart.value.contains("D")
                                var checkEndDate = !dataDateEnd.value.equals("")  and !dataDateEnd.value.contains("D")
                                if  (checkStartDate or checkEndDate){
                                    if  (checkStartDate and checkEndDate){
//                                        chipsFilter.add(mapOf(("Fecha: "+ dataDateStart.value + " - "+ dataDateEnd.value) to ::filterDateRange))
//                                        filtersExclude.add(::filterDateRange)
                                    }else{
                                        if(checkStartDate) {
//                                            chipsFilter.add(mapOf(("Fecha inicio: " + dataDateStart.value ) to ::filterDateStart))
//                                            filtersExclude.add(::filterDateStart)
                                        }else{
//                                            chipsFilter.add(mapOf(("Fecha fin: " + dataDateEnd.value ) to ::filterDateEnd))
//                                            filtersExclude.add(::filterDateEnd)
                                        }
                                    }
                                }
                                expandedFilter=false
                            }, modifier = Modifier.align(Alignment.End)) { Text("Buscar") }
                        }
                    }
                }
            }
            for (i in 0..chipsFilter.size-1) {
                InputChip(
                    selected = false,
                    onClick = {
                        filtersExclude.removeIf { it.equals(chipsFilter[i][chipsFilter[i].keys.first()])}
                        chipsFilter.remove(chipsFilter[i])
                    },
                    label = { Text(chipsFilter[i].keys.first()) },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Localized description"
                        )
                    }
                )
            }
        }
    }
}

fun SelectColorCard(paid:Boolean): String {
    var color: String
    if (paid==true){
        color = ConStock
    }else{
        color = SinStock
    }
    return color
}

// TODO: esto esta para compras, se debe adaptar para stock, esto es lo que posiblemente est haciendo romper
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneBuy(stock:Stock, navController: NavController){
    Column(){
        Card(
            onClick={
                    //TODO verificar esto
               // navController.navigate("stockSummary/${listItemData.indexOf(itemData)}/info")
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
                        .background(Color(SelectColorCard(stock.withAlert).toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
              /*  Column(modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp)) {
                    Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(SolidColor(Color("#00687A".toColorInt())))
                        }
                        Text(text ="estoy probando",color= Color.Red)
                        //Text(text =itemData.nameUser.substring(0,2).capitalize(),color= Color.White)
                    }
                }
                */
                Column(modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()) {

                    Text("${stock.type}")
                    Text("${stock.product.name}")
                    Text("cantidad: ${stock.product.amount}+${stock.product.units}", fontWeight= FontWeight.Bold)

                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filterStatus(){
    var clickConStock by remember {mutableStateOf(false)}
    var colorConStock by remember {mutableStateOf<Color>(Color(0))}
    var clickSinStock by remember {mutableStateOf(false)}
    var colorSinStock by remember {mutableStateOf<Color>(Color(0))}
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier= Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardWidth =  with(LocalDensity.current) {
            screenWidth * 0.45f
        }
        if (clickConStock){
            colorConStock = Color(PagadoClick.toColorInt())
        }else{
            colorConStock=Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick={
                clickConStock = !clickConStock
                if(clickConStock){
                    filters.add(::filterTieneStock)
                }else{
                    filters.remove(::filterTieneStock)
                }
                resetFilter()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorConStock)
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(ConStock.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("En Almacén", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    if(clickConStock){
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
        if (clickSinStock){
            colorSinStock = Color(SinPagarClick.toColorInt())
        }else{
            colorSinStock=Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickSinStock=!clickSinStock
                if(clickSinStock){
                    filters.add(::filterSinStock)
                }else{
                    filters.remove(::filterSinStock)
                }
                resetFilter()
            },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorSinStock)
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(SinStock.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("No Disponible En Almacén", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    if(clickSinStock){
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


@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun StockScreen(stockViewModel: StockViewModel, navController: NavController) {
    var valuesStock = stockViewModel.stockEnBaseDeDatos.observeAsState().value
    valuesStock?.let {
       listStockDataFilter.clear()
        listStockDataFilter.addAll(it)
    }
    if (valuesStock == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }

    } else {
        resetFilter()
        resetFilterExclude()
        Box() {
            Column() {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    item {
                        //todo chequera filter
                        filterStatus()
                        //todo chequear actions
                        Actions(navController)
                    }
                    this.items(listStockDataFilter) {
                        OneBuy(it, navController)
                    }
                }

            }
            Button(
                onClick = {
                    //todo chequear esto
                    navController.navigate("stock/add")
                }, modifier = Modifier
                    .padding(end = 20.dp, bottom = 40.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Agregar elementos al almacén")
            }
        }
    }
}