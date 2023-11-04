package com.example.agroagil.Stock.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
//import com.example.agroagil.Buy.ui.BuyViewModel
//import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Stock

val SinStock = "#A93226"
val SinStockClick = "#f4e5e4"
val ConStock = "#28B463"
val ConStockClick = "#d7f1e2"

var listStockInicial =
    mutableStateListOf<Stock>() //lista mutable que contendrá los elementos de stock que se mostrarán en la interfaz de usuario.
var listStockDataFilter =
    mutableStateListOf<Stock>() //lista mutable que contendrá los elementos de stock que se mostrarán en la interfaz de usuario.
var filters =
    mutableStateListOf<Function1<List<Stock>, List<Stock>>>() //lista mutable de funciones que representan los filtros que se pueden aplicar a listStockDataFilter.

var filtersExclude = mutableStateListOf<Function1<List<Stock>, List<Stock>>>()
var chipsFilter = mutableStateListOf<Map<String, Function1<List<Stock>, List<Stock>>>>()
var nombreElementoDeStockFilter = mutableStateOf("")


var tipoStockSeleccionado = mutableStateOf("")

fun filterTieneStock(listaElementosDeStock: List<Stock>): List<Stock> {
    return listaElementosDeStock.filter { it -> it.product.amount > 0 }
}

fun filterSinStock(listaElementosStock: List<Stock>): List<Stock> {
    return listaElementosStock.filter { it -> it.product.amount <= 0 }
}

fun filterNombreProductoDelStock(listaElementosStock: List<Stock>): List<Stock> {
    //para filtrar según nombre del producto, que puede ser pera,naranja,camion,tractor
    return listaElementosStock.filter { it ->
        it.product.name.lowercase().contains(nombreElementoDeStockFilter.value.lowercase())
    }
}

fun filterAllStocks(listaElementosStock: List<Stock>): List<Stock> {
    return listaElementosStock
}

fun filterTipoElementoStock(listaElementosStock: List<Stock>): List<Stock> {
    Log.d("probando Filter tipo","${tipoStockSeleccionado.value}")
    for (i in 0..listaElementosStock.size - 1){
        Log.d("probando Filter tipo"," el valor seleccionado es : $tipoStockSeleccionado")
        Log.d("probando Filter tipo"," el tipo de este producto es  : ${listaElementosStock[i].type}")
        Log.d("probando Filter tipo","${tipoStockSeleccionado.value.equals( listaElementosStock[i].type)}")

    }

    return listaElementosStock.filter { it ->
        if(tipoStockSeleccionado.value.isNullOrEmpty()){
            Log.d("probando filtro", "el filtro fue nulo o vacio " +
                    if (tipoStockSeleccionado.value.isEmpty()) {"vacio"} else {"nulo"}
            )
            Log.d("probando filtro", "le voy asignar por defecto Semilla")
            tipoStockSeleccionado.value= "Semillas"
        }
        it.type.equals(tipoStockSeleccionado.value)
    }
}

fun resetFilter() {
    if (filters.size != 0) {
        for (i in 0..filters.size - 1) {
            if(listStockDataFilter.size==0){
            var filtroExecute = mutableListOf<List<Stock>>()
            filtroExecute.addAll(listOf(filters[i](listStockInicial)))
            listStockDataFilter.addAll(filtroExecute.flatten())
        }
            else{
                listStockDataFilter.clear()
                var filtroExecute = mutableListOf<List<Stock>>()
                filtroExecute.addAll(listOf(filters[i](listStockInicial)))
                listStockDataFilter.addAll(filtroExecute.flatten())

            }
    }
}
}
//}

fun resetFilterExclude() {
    if (filtersExclude.size != 0) {
        for (i in 0..filtersExclude.size - 1) {
            var filtroExecute = mutableListOf<List<Stock>>()
            if (listStockDataFilter.size != 0) {
                filtroExecute.addAll(listOf(filtersExclude[i](listStockDataFilter)))
                listStockDataFilter.clear()
                listStockDataFilter.addAll(filtroExecute.flatten())
            }
            else{
                filtroExecute.addAll(listOf(filtersExclude[i](listStockInicial)))
                listStockDataFilter.clear()
                listStockDataFilter.addAll(filtroExecute.flatten())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actions(navController: NavController) {
    var expandedFilter by rememberSaveable { mutableStateOf(false) }
    var nombreProductoDelStock by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    } //valor inicial una cadena vacía, y los valores que se coloquen se mantienen a pesar de rotar la pantalla
    var tipoProducto by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    }

    Column {
        Row(

            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 5.dp, start = 0.dp, end = 0.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { expandedFilter = !expandedFilter },
                colors = if (expandedFilter) ButtonDefaults.buttonColors()
                else ButtonDefaults.filledTonalButtonColors(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.filter),
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Filtrar")
            }
        }
        Column(modifier = Modifier.padding(end = 10.dp, start = 10.dp, bottom = 5.dp)) {
            AnimatedVisibility(visible = expandedFilter) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(30.dp)
                        ) {
                            Text("Producto")
                            OutlinedTextField(
                                value = nombreProductoDelStock,
                                onValueChange = { nombreProductoDelStock = it },
                                label = { },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(text="Tipo de Producto")
                            var tipoDeElementoDeStockSeleccionado by rememberSaveable { mutableStateOf("") }
                            var estaOpcionPorDefecto by rememberSaveable { mutableStateOf(true) }
                            var expandirSelector by rememberSaveable { mutableStateOf(false) }
                            val tiposDeElementosDeStock = listOf(" Herramienta", "Fertilizante","Cultivo","Semillas","Otros","Elegir tipo de producto")
                            var sinTipoStockSeleccionado by rememberSaveable { mutableStateOf(true) }

                                // en este componente se a ver la opción que se elige
                                OutlinedTextField(
                                    trailingIcon = {
                                        //flechita para arriba o para abajo según corresponda en el selector
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandirSelector) },
                                    value = tipoProducto,
                                    //la opcion seleccionada
                                    onValueChange = { nuevoTipoStockSeleccionado ->
                                       tipoProducto = nuevoTipoStockSeleccionado
                                    },
                                    enabled = false, // con esto no podes escribir un rol
                                    readOnly = true, // solo permite su lectura
                                    label = {
                                        if (estaOpcionPorDefecto) {
                                            //valida que se seleccione un rol y que no sea el por defecto
                                            Text("Elegir tipo de producto")
                                            sinTipoStockSeleccionado = true
                                        }
                                    },
                                    modifier = Modifier
                                        .clickable {
                                            estaOpcionPorDefecto = false
                                            //se expande el selector
                                            expandirSelector = true
                                        }
                                        .fillMaxWidth()
                                )

                                if (!estaOpcionPorDefecto) {
                                    DropdownMenu(
                                        expanded = expandirSelector,
                                        onDismissRequest = {
                                            expandirSelector = false
                                            if (tipoProducto.text.equals("Elegir tipo de producto")) {//si no selecciono un tipo esta la opcion por defecto
                                                estaOpcionPorDefecto = true
                                            } else {
                                                sinTipoStockSeleccionado = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        tiposDeElementosDeStock.forEach { tipoStk ->
                                            if (!tipoStk.equals("Elegir tipo de producto")) {
                                                DropdownMenuItem(
                                                    text = { Text(text = tipoStk) },
                                                    onClick = {
                                                        tipoProducto = TextFieldValue(tipoStk, TextRange(0, 200))
                                                        expandirSelector = false
                                                        tipoDeElementoDeStockSeleccionado = tipoStk
                                                        sinTipoStockSeleccionado =
                                                            tipoStk == "" || tipoStk.isEmpty()
                                                    })
                                            }
                                        }
                                    }

                                }
                            }


                            //botón circular
                            ExtendedFloatingActionButton(onClick = {
                                //borro todos los filtros que ya existian previamente
                                filtersExclude.removeIf {
                                    it.equals(::filterNombreProductoDelStock) or
                                            it.equals(::filterTipoElementoStock)
                                    /* ver si sirve depues, probablemnete sirva para agregar otro filtro or it.equals(::filterDateRange) or it.equals(::filterDateStart) or it.equals(::filterDateEnd)
                                } */
                                }
                                //limpio los chips, o sea donde coloco el filtro
                                chipsFilter.clear()
                                if (nombreProductoDelStock.text != "") {
                                    chipsFilter.add(mapOf(("Producto: " + nombreProductoDelStock.text) to ::filterNombreProductoDelStock))
                                    filtersExclude.add(::filterNombreProductoDelStock)
                                    nombreElementoDeStockFilter.value = nombreProductoDelStock.text
                                }
                                if (tipoProducto.text != "" && tipoProducto.text != "Elegir tipo de producto") {
                                    chipsFilter.add(mapOf(("Tipo de Producto: ${tipoProducto.text}") to ::filterTipoElementoStock))
                                    filtersExclude.add(::filterTipoElementoStock)
                                    tipoStockSeleccionado.value = tipoProducto.text
                                }
                                expandedFilter = false
                            }, modifier = Modifier.align(Alignment.End)) { Text("Buscar") }
                        }
                    }
                }
            }
            for (i in 0..chipsFilter.size - 1) {
                //componente de Jetpack Compose que se utilizan para mostrar opciones seleccionables en una interfaz de usuario.
                InputChip(
                    selected = false,
                    onClick = {
                        filtersExclude.removeIf { it.equals(chipsFilter[i][chipsFilter[i].keys.first()]) }
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

fun SelectColorCard(amount: Int, alertAmount: Int): String {
    var color: String
    if (amount > alertAmount) {
        color = ConStockClick
    } else {
        color = SinStockClick
    }
    return color
}
fun getImage(typeStock: String): Int {
    if (typeStock == "Herramienta"){
        return R.drawable.herramientas
    }
    if(typeStock=="Cultivo"){
        return R.drawable.crop4
    }
    if (typeStock=="Fertilizante"){
        return R.drawable.fertilizante
    }
    if (typeStock=="Semillas"){
        return R.drawable.semillas
    }
    return R.drawable.logo_aa
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun unProductoDelStock(navController: NavController){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    for (i in 0..Math.ceil((listStockDataFilter.size / 2).toDouble()).toInt()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            for (item in 0..Math.min(2, listStockDataFilter.size - (i * 2)) - 1) {
                Card(
                    modifier = Modifier
                        .width(screenWidth * 0.42f)
                        .height(240.dp)
                        .padding(start = 5.dp, end = 5.dp, top = 10.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    onClick = {
                        //navController.navigate("cultivo/"+ plantations[(i * 2) + item].id+"/info")
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color("#F8FAFB".toColorInt()),
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()

                    ) {
                        Box() {


                            Image(
                                painter = painterResource(id = getImage(listStockDataFilter[(i * 2) + item].type)),
                                contentDescription = stringResource(id = R.string.app_name),
                                contentScale = ContentScale.Inside,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp * 0.7f)
                                    .background(Color("#628665".toColorInt()))
                            )
                                ElevatedFilterChip(
                                    selected = true,
                                    enabled = false,
                                    colors= FilterChipDefaults.filterChipColors(disabledSelectedContainerColor =Color(SelectColorCard(listStockDataFilter[(i * 2) + item].product.amount,listStockDataFilter[(i * 2) + item].amountMinAlert).toColorInt()),
                                        disabledLabelColor = Color.Black
                                    ),
                                    onClick = {},
                                    label = { Text(listStockDataFilter[(i * 2) + item].product.amount.toString() + " "+listStockDataFilter[(i * 2) + item].product.units)},
                                    modifier = Modifier.align(Alignment.TopEnd).padding(end=5.dp)
                                )
                        }
                        Text(
                            listStockDataFilter[(i * 2) + item].product.name,
                            modifier = Modifier
                                .background(Color("#F8FAFB".toColorInt()))
                                .fillMaxWidth()
                                .height(45.dp)
                                .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                            color = Color.Black,
                            fontSize = 17.sp
                        )


                    }

                }
            }
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filterStatus() {
    var clickConStock by rememberSaveable { mutableStateOf(false) }
    var colorConStock by remember { mutableStateOf<Color>(Color(0)) }
    var clickSinStock by rememberSaveable { mutableStateOf(false) }
    var colorSinStock by remember { mutableStateOf<Color>(Color(0)) }
    var todosLosElementosStock by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardWidth = with(LocalDensity.current) {
            screenWidth * 0.45f
        }

        //se marca con stock
        if (clickConStock) {
            colorConStock = Color(ConStockClick.toColorInt())
        } else {
            colorConStock = Color(MaterialTheme.colorScheme.background.value)
        }

        Card(
            onClick = {
                clickConStock = !clickConStock
                    if (clickConStock) {
                        filters.add(::filterTieneStock)
                    } else {
                        filters.remove(::filterTieneStock)
                    }

                if( clickConStock && todosLosElementosStock){
                    todosLosElementosStock = false
                }
                if( clickConStock && clickSinStock){
                    clickSinStock = false
                    filters.remove(::filterSinStock)
                }
                resetFilter()
            },
            modifier = Modifier
                //.width(cardWidth)
                .fillMaxWidth()
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorConStock)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .background(Color(ConStock.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "En Almacén",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (clickConStock) {
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

        //se marca sin stock
        if (clickSinStock) {
            colorSinStock = Color(SinStockClick.toColorInt())
        } else {
            colorSinStock = Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickSinStock = !clickSinStock
                    if (clickSinStock) {
                        filters.add(::filterSinStock)
                    } else {
                        filters.remove(::filterSinStock)
                    }

                if( clickSinStock && todosLosElementosStock){
                    todosLosElementosStock = false
                }
                if( clickConStock && clickSinStock){
                    clickConStock = false
                    filters.remove(::filterTieneStock)
                }

                resetFilter()
            },
            modifier = Modifier
                //.width(cardWidth)
                .fillMaxWidth()
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
                    Text(
                        "No Disponible En Almacén",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (clickSinStock) {
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
        // todos los elementos del stock

        Card(
            onClick = {
                todosLosElementosStock = !todosLosElementosStock

                if(todosLosElementosStock){
                    //filterAllStocks
                        filters.add(::filterAllStocks)
                    } else {
                        filters.remove(::filterAllStocks)
                    }

                    if( clickSinStock && todosLosElementosStock){
                        clickSinStock = false
                        filters.remove(::filterSinStock)
                    }
                    if( clickConStock && todosLosElementosStock){
                        clickConStock = false
                        filters.remove(::filterTieneStock)
                    }

                    resetFilter()

            },
            modifier = Modifier
                //.width(cardWidth)
                .fillMaxWidth()
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .width(10.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "Todos",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (todosLosElementosStock) {
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
    //se cargarían los distintos elementos con su stock en el view model y se traen a la variable valueStock
    var valuesStock = stockViewModel.stockEnBaseDeDatos.observeAsState().value
    valuesStock?.let {
        //si es nulo valueStock no se ejecutan
        listStockInicial.clear()
        listStockInicial.addAll(it)
    }
    if (valuesStock == null) {
        //aca pregunta si es nulo valueStock
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            //simbolo de espera hasta que cargue
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }

    } else {
        listStockDataFilter.clear()
        listStockDataFilter.addAll(listStockInicial)
       //resetFilter()
        //resetFilterExclude()
        Box() {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp).verticalScroll(rememberScrollState()))
            {
                        filterStatus()
                        Actions(navController)
                        resetFilterExclude()
                        unProductoDelStock(navController)
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