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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
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
import com.example.agroagil.Buy.ui.listItemData
import com.example.agroagil.Buy.ui.listItemDataFilter
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Product
//import com.example.agroagil.Buy.ui.BuyViewModel
//import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Stock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
val tiposDeElementosDeStock = listOf(
    "Herramienta",
    "Fertilizante",
    "Cultivo",
    "Semillas",
    "Otro"
)

var tipoStockSeleccionado = mutableStateOf("")
var dialogAddOpen = mutableStateOf(false)
var dialogEditOpen = mutableStateOf(false)
var dialogResetOpen = mutableStateOf(false)
var productEditOpen = mutableStateOf<Stock>(Stock())

fun filterTieneStock(listaElementosDeStock: List<Stock>): List<Stock> {
    return listaElementosDeStock.filter { it -> it.product.amount > it.amountMinAlert }
}

fun filterSinStock(listaElementosStock: List<Stock>): List<Stock> {
    return listaElementosStock.filter { it -> it.product.amount <= it.amountMinAlert }
}

fun filterNombreProductoDelStock(listaElementosStock: List<Stock>): List<Stock> {
    //para filtrar según nombre del producto, que puede ser pera,naranja,camion,tractor
    return listaElementosStock.filter { it ->
        it.product.name.lowercase().contains(nombreElementoDeStockFilter.value.lowercase())
    }
}


fun filterTipoElementoStock(listaElementosStock: List<Stock>): List<Stock> {
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dialogEdit(stockViewModel: StockViewModel
){
    var alertAmount by remember{ mutableStateOf(productEditOpen.value.amountMinAlert.toString()) }
    var alertAmountError by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf(productEditOpen.value.type) }
    var typeError by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(productEditOpen.value.product.name) }
    var nameError by remember { mutableStateOf(false) }
    var units by remember { mutableStateOf(productEditOpen.value.product.units) }
    var unitsError by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf(productEditOpen.value.product.amount.toString()) }
    var amountError by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf(productEditOpen.value.product.price.toString()) }
    var priceError by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }
    if (dialogResetOpen.value){
        alertAmount = productEditOpen.value.amountMinAlert.toString()
        type= productEditOpen.value.type
        name = productEditOpen.value.product.name
        units = productEditOpen.value.product.units
        amount = productEditOpen.value.product.amount.toString()
        price = productEditOpen.value.product.price.toString()
        dialogResetOpen.value= false
    }
    if (dialogEditOpen.value){
        AlertDialog(
            onDismissRequest = {
                dialogEditOpen.value = false
            },
            title = {
                Text(text = "Editar un producto")
            },
            text = {
                Column(){
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        isError = nameError
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType },
                        modifier = Modifier.fillMaxWidth().padding(top=5.dp)
                    ) {
                        TextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = type,
                            onValueChange = {},
                            label = { Text("Tipo de Producto") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            isError = typeError
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tiposDeElementosDeStock.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        type = selectionOption
                                        expandedType = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = units,
                        onValueChange = { units = it },
                        label = { Text("Unidad") },
                        isError = unitsError
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Cantidad") },
                        isError = amountError
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio estimado por unidad") },
                        isError = priceError
                    )
                    OutlinedTextField(
                        value = alertAmount,
                        onValueChange = { alertAmount = it },
                        label = { Text("Cantidad minima para alertar (OPCIONAL)") },
                        isError = alertAmountError
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(name!="" && units != "" && type !="" && amount != ""){
                            if(alertAmount == ""){
                                alertAmount= "0"
                            }
                            stockViewModel.addUpdateProduct(Stock(id=productEditOpen.value.id,type=type, amountMinAlert=alertAmount.toInt(), product = Product(
                                name=name,
                                amount=amount.toInt(),
                                units = units,
                                price = price.toDouble()
                            ), date= SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)))
                            resetFilter()
                            resetFilterExclude()
                            dialogEditOpen.value = false

                        }else{
                            if(name==""){
                                nameError = true
                            }
                            if(units==""){
                                unitsError = true
                            }
                            if(type==""){
                                typeError = true
                            }
                            if(amount==""){
                                amountError = true
                            }
                        }

                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogEditOpen.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dialogAdd(stockViewModel: StockViewModel
){
    var alertAmount by remember{ mutableStateOf("") }
    var alertAmountError by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var units by remember { mutableStateOf("") }
    var unitsError by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    if (dialogAddOpen.value){
        AlertDialog(
            onDismissRequest = {
                dialogAddOpen.value = false
            },
            title = {
                Text(text = "Agregar nuevo producto")
            },
            text = {
                Column(){
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    isError = nameError
                )
                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType },
                        modifier = Modifier.fillMaxWidth().padding(top=5.dp)
                    ) {
                        TextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = type,
                            onValueChange = {},
                            label = { Text("Tipo de Producto") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            isError = typeError
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tiposDeElementosDeStock.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        type = selectionOption
                                        expandedType = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                OutlinedTextField(
                    value = units,
                    onValueChange = { units = it },
                    label = { Text("Unidad") },
                    isError = unitsError
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Cantidad") },
                    isError = amountError
                )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio estimado por unidad") },
                        isError = priceError
                    )
                OutlinedTextField(
                    value = alertAmount,
                    onValueChange = { alertAmount = it },
                    label = { Text("Cantidad minima para alertar (OPCIONAL)") },
                    isError = alertAmountError
                )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(name!="" && units != "" && type !="" && amount != ""){
                            if(alertAmount == ""){
                                alertAmount= "0"
                            }
                            stockViewModel.addUpdateProduct(Stock(type=type, amountMinAlert=alertAmount.toInt(), product = Product(
                                name=name,
                                amount=amount.toInt(),
                                units = units,
                                price = price.toDouble()
                            ), date= SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)))
                            dialogAddOpen.value = false
                        }else{
                            if(name==""){
                                nameError = true
                            }
                            if(units==""){
                                unitsError = true
                            }
                            if(type==""){
                                typeError = true
                            }
                            if(amount==""){
                                amountError = true
                            }
                        }

                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogAddOpen.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun resetFilter() {
    listStockDataFilter.clear()
    if (filters.size ==0){
        listStockDataFilter.addAll(listStockInicial)
    }
    for (i in 0 .. filters.size-1) {
        var filtroExecute = mutableListOf<List<Stock>>()
        filtroExecute.addAll(listOf(filters[i](listStockInicial)))
        listStockDataFilter.addAll(filtroExecute.flatten())
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
    var expandedType by rememberSaveable { mutableStateOf(false) }
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

                            OutlinedTextField(
                                value = nombreProductoDelStock,
                                onValueChange = { nombreProductoDelStock = it },
                                label = { Text("Producto")},
                                modifier = Modifier.fillMaxWidth()
                            )
                            var tipoDeElementoDeStockSeleccionado by rememberSaveable {
                                mutableStateOf(
                                    ""
                                )
                            }

                            ExposedDropdownMenuBox(
                                expanded = expandedType,
                                onExpandedChange = { expandedType = !expandedType },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    // The `menuAnchor` modifier must be passed to the text field for correctness.
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    readOnly = true,
                                    value = tipoDeElementoDeStockSeleccionado,
                                    onValueChange = {},
                                    label = { Text("Tipo de Producto") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedType,
                                    onDismissRequest = { expandedType = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    tiposDeElementosDeStock.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption) },
                                            onClick = {
                                                tipoProducto = TextFieldValue(selectionOption, TextRange(0, 200))
                                                tipoDeElementoDeStockSeleccionado = selectionOption
                                                expandedType = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                            modifier = Modifier.fillMaxWidth()
                                        )
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
                                if (tipoProducto.text != "") {
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

fun SelectColorCard(amount: Float, alertAmount: Int): String {
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
                        //navController.navigate("stockSummary/${listStockDataFilter[(i * 2) + item].id}/info")
                                productEditOpen.value=listStockDataFilter[(i * 2) + item]
                              dialogEditOpen.value=true
                                dialogResetOpen.value = true
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Card(
                onClick = {
                    clickConStock = !clickConStock
                    if (clickConStock) {
                        filters.add(::filterTieneStock)
                    } else {
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
                    //navController.navigate("stock/add")
                    dialogAddOpen.value=true
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
                Text("Agregar")
            }
        }
        dialogAdd(stockViewModel)
        dialogEdit(stockViewModel)
    }
}