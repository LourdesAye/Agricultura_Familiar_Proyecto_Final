package com.example.agroagil.Loan.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.Stock.ui.StockViewModel
import com.example.agroagil.Stock.ui.tiposDeElementosDeStock
import com.example.agroagil.Summary.SummaryViewModel
import com.example.agroagil.core.models.Conversion
import com.example.agroagil.core.models.EventOperationStock
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Loan
import com.example.agroagil.core.models.Stock
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

var openDialogAddItemEdit =  mutableStateOf(false)
var currentLoanEdit = Loan("Usuario1", listOf<Product>(Product("Tomate", 1, "KG")), emptyList(), 0)
var currentLoanEditId = mutableStateOf(-1)
var productsEdit = mutableStateListOf<Product>()
val productsTypeEdit = mutableMapOf<String,String>()
val productsConvertEdit = mutableMapOf<String, Conversion>()
var nameEdit = mutableStateOf("")
var amountEdit = mutableStateOf("")
var measureEdit = mutableStateOf("")
var errorNameEdit = mutableStateOf(false)
var errorAmountEdit = mutableStateOf(false)
var errorMeasureEdit = mutableStateOf(false)
var typeEdit = mutableStateOf("")
var errorTypeEdit =mutableStateOf(false)
var isNewUnidadEdit = mutableStateOf(false)
var isNewProductEdit = mutableStateOf(false)
var nameUnidadConvertEdit = mutableStateOf("")
var stockSelectedEdit = mutableStateOf<Stock?>(null)
var productsStockEdit = mutableStateListOf<Stock>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextProductEdit(){
    var expanded by remember {mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorNameEdit.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = nameEdit.value,
                onValueChange = {
                    nameEdit.value = it
                    expanded = true
                    errorNameEdit.value=false
                },
                label = { Text("Nombre del producto") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(25.dp)
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            val filteringOptions =
                productsStockEdit.filter { it.product.name.contains(nameEdit.value, ignoreCase = true) }
            if (filteringOptions.isEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded=!expanded},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("Nuevo producto") },
                        onClick = {
                            expanded = false
                            isNewProductEdit.value=true
                            stockSelectedEdit.value=null
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                }

            } else {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteringOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.product.name) },
                            onClick = {
                                nameEdit.value = selectionOption.product.name
                                expanded = false
                                isNewProductEdit.value=false
                                stockSelectedEdit.value = selectionOption
                                measureEdit.value = selectionOption.product.units
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTypeEdit() {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded},
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorTypeEdit.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = typeEdit.value,
                onValueChange = {
                    typeEdit.value = it
                    expanded = true
                    errorTypeEdit.value = false
                },
                label = { Text("Tipo de producto") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(25.dp)
                    )
                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                tiposDeElementosDeStock.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            typeEdit.value = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        modifier = Modifier.fillMaxWidth()
                    )
                }


            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextUnidadEdit(){
    var expanded by remember {mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorMeasureEdit.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = measureEdit.value,
                onValueChange = {
                    measureEdit.value = it.uppercase()
                    expanded = true
                    errorMeasureEdit.value=false
                },
                label = { Text("Unidad") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(25.dp)
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            var listUnits = mutableListOf<String>()
            listUnits.addAll(stockSelectedEdit.value!!.conversion.map { it.name })
            listUnits.add(stockSelectedEdit.value!!.product.units)
            val filteringOptions = listUnits.filter{ it.contains(measureEdit.value, ignoreCase = true) }.toSet().toList()
            if (filteringOptions.isEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded=!expanded},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("Nueva unidad") },
                        onClick = {
                            expanded = false
                            isNewUnidadEdit.value = true
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                }

            } else {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteringOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                measureEdit.value = selectionOption
                                expanded = false
                                isNewUnidadEdit.value = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        }
    }
    if(isNewUnidadEdit.value){
        OutlinedTextField(
            value = nameUnidadConvertEdit.value,
            onValueChange = {
                nameUnidadConvertEdit.value = it
                errorMeasureEdit.value = false
            },
            label = {
                Text("Cuanto de "+ stockSelectedEdit.value!!.product.units+" equivale a 1 "+ measureEdit.value+"?")
            },
            trailingIcon = {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(25.dp)
                )
            },
            isError = errorMeasureEdit.value,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductEidt(){
    if (openDialogAddItemEdit.value){
        AlertDialog(
            title={Text("Nuevo Producto")},
            modifier= Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onDismissRequest = {
                openDialogAddItemEdit.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nameEdit.value == ""){
                            errorNameEdit.value=true
                        }
                        if (amountEdit.value == ""){
                            errorAmountEdit.value=true
                        }
                        if (typeEdit.value == ""){
                            errorType.value=true
                        }
                        if (measureEdit.value == "" || (isNewUnidadEdit.value && nameUnidadConvertEdit.value=="")){
                            errorMeasureEdit.value=true
                        }
                        if (nameEdit.value != "" && (typeEdit.value != "") && amountEdit.value != "" && ((measureEdit.value != "" && !isNewUnidadEdit.value)|| (measureEdit.value != "" && isNewUnidadEdit.value && nameUnidadConvertEdit.value!=""))){
                            if (isNewUnidadEdit.value){
                                productsConvertEdit[nameEdit.value] = Conversion(measureEdit.value, nameUnidadConvertEdit.value.toFloat())
                            }
                            var new_item = Product(nameEdit.value, amountEdit.value.toInt(), units = measureEdit.value)
                            productsEdit.add(new_item)
                            productsTypeEdit[nameEdit.value] = typeEdit.value
                            openDialogAddItemEdit.value=false
                            nameEdit.value = ""
                            amountEdit.value=""
                            nameEdit.value=""
                        }
                    }
                ) {

                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogAddItemEdit.value = false
                        nameEdit.value = ""
                        amountEdit.value=""
                    }
                ) {
                    Text("No, Cancelar")
                }
            },

            text = {
                Column {
                    TextProductEdit()
                    TextTypeEdit()
                    OutlinedTextField(
                        value = amountEdit.value,
                        onValueChange = { amountEdit.value = it
                            errorAmountEdit.value=false
                        },
                        label = {
                            Text("Cantidad")
                        },
                        isError = errorAmountEdit.value,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    if(isNewProductEdit.value==true){
                        OutlinedTextField(
                            value = measureEdit.value,
                            onValueChange = {
                                measureEdit.value = it.uppercase()
                                errorMeasureEdit.value = false},
                            label = {
                                Text("Unidad")
                            }
                            ,
                            isError = errorMeasureEdit.value,
                            modifier = Modifier.fillMaxWidth())
                    }
                    if(stockSelectedEdit.value != null){
                        TextUnidadEdit()
                    }
                }

            })
    }
}

@Composable
fun itemProductClose(item: Product){
    Row() {
        Column(modifier = Modifier.padding(top = 5.dp)) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)) {
                Row() {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterVertically)
                            .padding(end = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(SolidColor(Color("#00687A".toColorInt())))
                        }
                        Text(
                            text = item.name.substring(0, 2).capitalize(),
                            color = Color.White
                        )
                    }
                    Text(
                        item.name,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        item.amount.toString()+" "+ item.units.toString(), modifier = Modifier
                            .padding(end = 10.dp)
                            .align(Alignment.CenterVertically))
                    /*IconButton(
                        onClick = { productsEdit.remove(item)}){
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(25.dp))
                    }*/
                }
            }
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEditScreen(navController: NavController, loanViewModel: LoanViewModel, loanId: Int, stockViewModel: StockViewModel, eventViewModel: SummaryViewModel) {
    var valuesLoan = loanViewModel.farm.observeAsState().value
    val stockValues = stockViewModel.stockEnBaseDeDatos.observeAsState().value
    if (valuesLoan == null || stockValues == null){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp)
            )
        }

    }else {
        productsStockEdit.clear()
        productsStockEdit.addAll(stockValues)
        currentLoanEdit = valuesLoan.get(loanId)
        if (productsEdit.size ==0 || currentLoanEditId.value != loanId ){
            productsEdit.clear()
            productsEdit.addAll(currentLoanEdit.paid)
            currentLoanEditId.value = loanId
        }
        var percentagePaid by rememberSaveable { mutableStateOf(currentLoanEdit.percentagePaid.toString()+ " %") }
        var percentagePaidError by rememberSaveable { mutableStateOf(false)}
        AddProductEidt()
        Box(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                ) {
                    Text(
                        currentLoanEdit.nameUser,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        currentLoanEdit.date,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Text(
                    "Productos pagados",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 50.dp, bottom = 10.dp)
                )

                OutlinedTextField(
                    value = percentagePaid,
                    onValueChange = {
                        percentagePaid = it
                        percentagePaidError = false
                    },
                    label = {
                        Text("Porcentaje pagado")
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = percentagePaidError
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {


                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Elementos",
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Button(
                            onClick = { openDialogAddItemEdit.value = true },
                            modifier = Modifier.align(Alignment.CenterVertically)
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
                    for (i in 0..productsEdit.size - 1) {
                        itemProductClose(productsEdit[i])
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(30.dp)
                    .fillMaxWidth()){
                Button(onClick = {
                    var stockAcction: Function2<Float, Float, Float>
                    if (currentLoanEdit.lend) {
                        stockAcction = ::SubstackStock
                    } else {
                        stockAcction = ::AddStock
                    }
                    //var initLoan = valuesLoan.get(loanId)
                    for (product in productsEdit) {
                        //var stockFinds =
                        //    stockValues.filter { it.product.name == product.name }
                        var loanInitFind = currentLoanEdit.paid.filter { it.name == product.name }
                        if (loanInitFind.size ==0){
                            var stockFinds =
                                stockValues.filter { it.product.name == product.name }
                            if (stockFinds.size == 0) {
                                var productStock = Product(
                                    name = product.name,
                                    amount = (stockAcction(0f, product.amount)),
                                    units = product.units, price = product.price
                                )
                                var stockNew = Stock(
                                    type = productsTypeEdit[product.name]!!,
                                    product = productStock,
                                    amountMinAlert = 0,
                                    date = SimpleDateFormat(
                                        "yyyy/MM/dd HH:mm",
                                        Locale.getDefault()
                                    )
                                        .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)
                                )
                                var stockID = stockViewModel.addUpdateProduct(stockNew)
                                eventViewModel.addEventOperationStock(
                                    EventOperationStock(
                                        date= SimpleDateFormat("yyyy/MM/dd HH:mm",Locale.getDefault())
                                            .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time),
                                        typeEvent = "Se realizo la devolucion de un prestamo",
                                        referenceID=stockID
                                    )
                                )
                            } else {
                                var stockFind = stockFinds[0]
                                if (product.name in productsConvertEdit.keys) {
                                    var listConversion = mutableListOf<Conversion>()
                                    listConversion.addAll(stockFind.conversion)
                                    listConversion.add(productsConvertEdit[product.name]!!)
                                    stockFind.conversion = listConversion.toList()
                                    stockFind.product.amount = stockAcction(
                                        stockFind.product.amount,
                                        product.amount * productsConvertEdit[product.name]!!.amount
                                    )
                                } else {
                                    stockFind.product.amount = stockAcction(
                                        stockFind.product.amount,
                                        product.amount
                                    )
                                }
                                var stockID = stockViewModel.addUpdateProduct(stockFind)
                                eventViewModel.addEventOperationStock(
                                    EventOperationStock(
                                        date= SimpleDateFormat("yyyy/MM/dd HH:mm",Locale.getDefault())
                                            .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time),
                                        typeEvent = "Se realizo la devolucion de un prestamo",
                                        referenceID=stockID
                                    )
                                )
                            }
                        }

                    }
                    currentLoanEdit.percentagePaid = percentagePaid.replace("%", "").replace(" ", "").toInt()
                    currentLoanEdit.paid = productsEdit
                    loanViewModel.updateLoan(currentLoanEdit, loanId)
                    productsEdit.clear()
                    navController.popBackStack()

                }, modifier = Modifier.align(Alignment.CenterVertically)

                ) {

                    Text("Guardar")
                }
                TextButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Cancelar")
                }
            }
    }

    }
}