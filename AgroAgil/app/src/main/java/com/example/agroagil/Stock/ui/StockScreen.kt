package com.example.agroagil.Stock.ui

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
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

/*
es una lista mutable que almacena funciones (Function1) que aceptan una lista de elementos de tipo List<Stock>
y devuelven una lista de elementos del mismo tipo (List<Stock>).
Cada función en filtersExclude representa un filtro específico que excluye ciertos elementos de la lista.
filterNombreProductoDelStock, que acepta una lista de elementos de stock  y devuelve una lista que excluye aquellos
elementos que no coinciden con un nombre de producto específico. En resumen, filtersExclude almacena funciones de filtro que se aplican para excluir elementos específicos de la lista de stock.
* */
var filtersExclude = mutableStateListOf<Function1<List<Stock>, List<Stock>>>()
var chipsFilter = mutableStateListOf<Map<String, Function1<List<Stock>, List<Stock>>>>()

/* chipsFilter se utiliza para almacenar etiquetas (generalmente descripciones de filtros) y las funciones de filtro asociadas a esas etiquetas.
Esto permite que la interfaz de usuario muestre los filtros aplicados en forma de etiquetas, y cuando el usuario interactúa con estas etiquetas,
se pueden eliminar los filtros correspondientes.
Por ejemplo, un elemento en chipsFilter podría tener una clave (una etiqueta) como "Filtrar por Nombre de Producto"
y un valor que es la función de filtro filterNombreProductoDelStock.
Cuando el usuario hace clic en esta etiqueta, se puede usar la función de filtro asociada para realizar la acción correspondiente, c
omo eliminar ese filtro particular.
En resumen, chipsFilter es una lista que relaciona etiquetas de filtro con las funciones de filtro que se aplican a la lista de elementos de stock.
Esto se utiliza para representar y gestionar los filtros aplicados en la interfaz de usuario.
*/
var nombreElementoDeStockFilter = mutableStateOf("")

//estos filtros no los usamos pero probablemnete nos irvan para ver alguna cosa más adelante
//var dataDateStart = mutableStateOf("")
//var dataDateEnd = mutableStateOf("")


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


fun resetFilterFix2() {
    // Asegurarse de que tempFilteredList esté inicializada con los datos originales
    val tempFilteredList = ArrayList(listStockInicial)

    if (filters.isNotEmpty()) {
        for (i in 0 until filters.size) {
            val filtroExecute = filters[i](tempFilteredList)
            tempFilteredList.clear()
            tempFilteredList.addAll(filtroExecute)
        }
    }

    // Copiar los resultados de los filtros a listStockDataFilter
    listStockDataFilter.clear()
    listStockDataFilter.addAll(tempFilteredList)
}

fun resetFilter() {
    //borra todos los elementos de la lista listStockDataFilter
    // listStockDataFilter.clear()
    /* if (filters.size == 0) {
        listStockDataFilter.addAll(listStockDataFilter)
    } else {*/
    /*Este bucle for recorre la lista de filtros, y i toma valores desde 0 hasta filters.size - 1,
     que son los índices válidos en la lista.
    */
    if (filters.size != 0) {
        for (i in 0..filters.size - 1) {
            /*Dentro del bucle, se crea una nueva lista llamada filtroExecute que es inicialmente una lista vacía y se utiliza para almacenar
        los resultados de aplicar un filtro.
        se aplica el filtro en la posición i a listStockDataFilter y se almacena el resultado en filtroExecute.
        se agrega el resultado de filtroExecute a la lista listStockDataFilter.
        Esto actualiza listStockDataFilter con los resultados de aplicar todos los filtros en la lista de filtros.
        */
            if(listStockDataFilter.size==0){
            var filtroExecute = mutableListOf<List<Stock>>()
            filtroExecute.addAll(listOf(filters[i](listStockInicial)))
            listStockDataFilter.addAll(filtroExecute.flatten())
            /*Luego, el resultado de filtroExecute se aplana (convierte en una lista plana)
         y se agrega a la lista listStockDataFilter.
         Esto significa que se están aplicando filtros sucesivamente a listStockDataFilter
         y actualizando la lista con los resultados de los filtros.*/
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

fun resetFilterFix() {
    // Crea una copia de la lista original para aplicar los filtros
    listStockDataFilter.clear()
    listStockDataFilter.addAll(listStockInicial)

    // Aplica cada filtro seleccionado
    for (filter in filters) {
        listStockDataFilter.retainAll(filter(listStockInicial))
    }
}

fun resetFilterExclude() {
    /*
    Se itera sobre la lista de filtros , se crea una nueva lista llamada filtroExecute que está inicialmente vacía
    y se utiliza para almacenar los resultados de aplicar un filtro.
Se agrega a filtroExecute el resultado de aplicar el filtro de exclusión en la posición i de la lista de filtros de exclusión a listStockDataFilter.
Esto se hace utilizando listOf(filtersExclude[i](listStockDataFilter)).
Se borran todos los elementos de la lista listStockDataFilter con listStockDataFilter.clear().
Luego, se agrega el resultado de filtroExecute a la lista listStockDataFilter. Esto actualiza listStockDataFilter con los resultados de aplicar los filtros de exclusión en la lista de filtros de exclusión.
    * */
    if (filtersExclude.size != 0) {
        for (i in 0..filtersExclude.size - 1) {
            var filtroExecute = mutableListOf<List<Stock>>()
            filtroExecute.addAll(listOf(filtersExclude[i](listStockInicial)))
            listStockDataFilter.clear()
            listStockDataFilter.addAll(filtroExecute.flatten())
        }
    }
}

//FILTRAR POR TIPO DE HERRAMIENTA

// boton de flitrado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actions(navController: NavController) {
    var expandedFilter by remember { mutableStateOf(false) }
    var nombreProductoDelStock by rememberSaveable(stateSaver = TextFieldValue.Saver) {
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
                colors = if (expandedFilter) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
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
                            OutlinedTextField(
                                value = nombreProductoDelStock,
                                onValueChange = { nombreProductoDelStock = it },
                                label = { Text("Producto") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            //botón circular
                            ExtendedFloatingActionButton(onClick = {
                                filtersExclude.removeIf {
                                    it.equals(::filterNombreProductoDelStock)
                                    /* ver si sirve depues, probablemnete sirva para agregar otro filtro or it.equals(::filterDateRange) or it.equals(::filterDateStart) or it.equals(::filterDateEnd)
                                } */
                                }
                                chipsFilter.clear()
                                if (nombreProductoDelStock.text != "") {
                                    chipsFilter.add(mapOf(("Producto: " + nombreProductoDelStock.text) to ::filterNombreProductoDelStock))
                                    filtersExclude.add(::filterNombreProductoDelStock)
                                    nombreElementoDeStockFilter.value = nombreProductoDelStock.text
                                }
                                /*  var checkStartDate = !dataDateStart.value.equals("") and !dataDateStart.value.contains("D")
                                  var checkEndDate = !dataDateEnd.value.equals("")  and !dataDateEnd.value.contains("D")
                                  */


                                /*if  (checkStartDate or checkEndDate){
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
                                }*/
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
}

fun SelectColorCard(conStock: Boolean): String {
    var color: String
    if (conStock == true) {
        color = ConStock
    } else {
        color = SinStock
    }
    return color
}

// TODO: esto esta para compras, se debe adaptar para stock, esto es lo que posiblemente est haciendo romper
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun unProductoDelStock(stock: Stock, navController: NavController) {
    Column() {
        var hayErrorIdStock by rememberSaveable { mutableStateOf("") }
        Card(
            onClick = {

                if (stock.id.isNullOrEmpty()) {
                    hayErrorIdStock = "hubo un error de Id Stock"
                } else {
                    hayErrorIdStock = ""
                    navController.navigate("stockSummary/${stock.id}/info")
                }
            },
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )

        ) {
            if(hayErrorIdStock!=""){
               Text(text = hayErrorIdStock)
            }

            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(SelectColorCard(stock.product.amount > 0).toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                ) {
                    if (stock.product.name.isNullOrEmpty()) {
                        Text(text = stock.type)
                    } else {
                        Text(stock.product.name, Modifier.fillMaxWidth())

                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filterStatus() {
    var clickConStock by rememberSaveable { mutableStateOf(false) }
    var colorConStock by remember { mutableStateOf<Color>(Color(0)) }
    var clickSinStock by rememberSaveable { mutableStateOf(false) }
    var colorSinStock by remember { mutableStateOf<Color>(Color(0)) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardWidth = with(LocalDensity.current) {
            screenWidth * 0.45f
        }
        if (clickConStock) {
            colorConStock = Color(ConStockClick.toColorInt())
        } else {
            colorConStock = Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickConStock = !clickConStock

                if (clickSinStock && clickConStock) {
                    filters.clear()
                    filters.add(::filterTieneStock)
                    filters.add(::filterTieneStock)
                }
                else{
                    if (clickConStock) {
                        filters.add(::filterTieneStock)
                    } else {
                        filters.remove(::filterTieneStock)
                    }

                }

               // resetFilterFix2()
                resetFilter()
                //resetFilterFix()
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

                // Si ambos filtros están seleccionados, quita el filtro de "Con Stock"
                if (clickConStock) {
                    filters.add(::filterTieneStock)
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
       resetFilter()
        Box() {
            Column() {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    item {
                        filterStatus()
                        Actions(navController)
                    }
                    /*this.items(listStockDataFilter) { ... }:
                    Aquí, se utiliza items para mostrar una lista de elementos de listStockDataFilter.
                    Cada elemento de esta lista se representa utilizando el composable OneBuy(it, navController).
                    Esto implica que se está mostrando una lista de elementos de listStockDataFilter en la vista,
                    donde navController se utiliza para la navegación o interacción relacionada con cada elemento.*/
                    if (listStockDataFilter.size == 0) {
                        this.items(listStockInicial) {
                            unProductoDelStock(it, navController)
                        }
                    } else {
                        this.items(listStockDataFilter) {
                            unProductoDelStock(it, navController)
                        }
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