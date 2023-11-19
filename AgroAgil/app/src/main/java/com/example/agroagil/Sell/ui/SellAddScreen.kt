

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.Loan.ui.SubstackStock
import com.example.agroagil.R
import com.example.agroagil.Sell.ui.SellViewModel
import com.example.agroagil.Stock.ui.StockViewModel
import com.example.agroagil.Stock.ui.tiposDeElementosDeStock
import com.example.agroagil.Summary.SummaryViewModel
import com.example.agroagil.core.models.Conversion
import com.example.agroagil.core.models.EventOperationStock
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Stock
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val openDialogAddItem =  mutableStateOf(false)
val products = mutableStateListOf<Product>()
val totalPrice = mutableStateOf(0f)
val productsStock = mutableStateListOf<Stock>()

var nameProduct = mutableStateOf("")
var priceSellAdd = mutableStateOf(0.0f)
var amount = mutableStateOf("")
var nameUnidad = mutableStateOf("")
var errorNameProduct = mutableStateOf(false)
var errorPriceSellAdd = mutableStateOf(false)
var errorAmount = mutableStateOf(false)
var errorNameUnidad = mutableStateOf(false)
var isNewProduct = mutableStateOf(false)
var stockSelected = mutableStateOf<Stock?>(null)
var nameType = mutableStateOf("")
var errorNameType = mutableStateOf(false)
var isNewUnidad = mutableStateOf(false)
var nameUnidadConvert = mutableStateOf("")
val productsType = mutableMapOf<String,String>()
val productsConvert = mutableMapOf<String, Conversion>()
var user = mutableStateOf("")
var errorUser = mutableStateOf(false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextProduct(){
    var expanded by remember {mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorNameProduct.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = nameProduct.value,
                onValueChange = {
                    nameProduct.value = it
                    expanded = true
                    errorNameProduct.value=false
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
            val filteringOptions = productsStock.filter { it.product.name.contains(
                    nameProduct.value, ignoreCase = true) }
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
                            isNewProduct.value=true
                            stockSelected.value=null
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
                                nameProduct.value = selectionOption.product.name
                                expanded = false
                                isNewProduct.value=false
                                stockSelected.value = selectionOption
                                nameUnidad.value = selectionOption.product.units
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
fun TextType() {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded},
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorNameType.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = nameType.value,
                onValueChange = {
                    nameType.value = it
                    expanded = true
                    errorNameType.value = false
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
                            nameType.value = selectionOption
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
fun TextUnidad(){
    var expanded by remember {mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorNameUnidad.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = nameUnidad.value,
                onValueChange = {
                    nameUnidad.value = it.uppercase()
                    expanded = true
                    errorNameUnidad.value=false
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
            listUnits.addAll(stockSelected.value!!.conversion.map { it.name })
            listUnits.add(stockSelected.value!!.product.units)
            val filteringOptions = listUnits.filter{ it.contains(nameUnidad.value, ignoreCase = true) }.toSet().toList()
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
                            isNewUnidad.value = true
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
                                nameUnidad.value = selectionOption
                                expanded = false
                                isNewUnidad.value = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        }
    }
    if(isNewUnidad.value){
        OutlinedTextField(
            value = nameUnidadConvert.value,
            onValueChange = {
                nameUnidadConvert.value = it
                errorNameUnidad.value = false
            },
            label = {
                Text("Cuanto de "+ stockSelected.value!!.product.units+" equivale a 1 "+ nameUnidad.value+"?")
            },
            trailingIcon = {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(25.dp)
                )
            },
            isError = errorNameUnidad.value,
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
fun AddProduct(){
    if (openDialogAddItem.value){
        AlertDialog(
            title={Text("Nuevo Producto")},
            modifier= Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onDismissRequest = {
                openDialogAddItem.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nameProduct.value == ""){
                            errorNameProduct.value=true
                        }
                        if (amount.value == ""){
                            errorAmount.value=true
                        }
                        if (nameUnidad.value == "" || (isNewUnidad.value && nameUnidadConvert.value=="")){
                            errorNameUnidad.value=true
                        }
                        if (nameType.value == ""){
                            errorType.value=true
                        }
                        if (nameProduct.value != "" && amount.value != "" && nameType.value != ""  && ((nameUnidad.value != "" && !isNewUnidad.value)|| (nameUnidad.value != "" && isNewUnidad.value && nameUnidadConvert.value!=""))){
                            if (isNewUnidad.value){
                                productsConvert[nameProduct.value] = Conversion(
                                    nameUnidad.value, nameUnidadConvert.value.toFloat())
                            }
                            productsType[nameProduct.value] = nameType.value
                            var new_item = Product(nameProduct.value,amount.value.toInt(), units = nameUnidad.value, price=priceSellAdd.value)
                            products.add(new_item)
                            totalPrice.value += priceSellAdd.value*amount.value.toFloat()
                            openDialogAddItem.value=false
                            nameProduct.value = ""
                            amount.value=""
                            nameUnidad.value=""
                            priceSellAdd.value = 0.0f
                        }
                    }
                ) {

                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogAddItem.value = false
                        nameProduct.value = ""
                        amount.value=""
                        nameUnidad.value=""
                    }
                ) {
                    Text("No, Cancelar")
                }
            },

            text = {
                Column {
                    TextProduct()
                    TextType()
                    OutlinedTextField(
                        value = amount.value,
                        onValueChange = { amount.value = it
                            errorAmount.value=false
                        },
                        label = {
                            Text("Cantidad")
                        },
                        isError = errorAmount.value,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    if(isNewProduct.value==true){
                        OutlinedTextField(
                            value = nameUnidad.value,
                            onValueChange = {
                                nameUnidad.value = it.uppercase()
                                errorNameUnidad.value= false},
                            label = {
                                Text("Unidad")
                            }
                            ,
                            isError = errorNameUnidad.value,
                            modifier = Modifier.fillMaxWidth())
                    }
                    if(stockSelected.value != null){
                        TextUnidad()
                    }

                    OutlinedTextField(
                        value = priceSellAdd.value.toString(),
                        onValueChange = { priceSellAdd.value = it.toFloat()
                            errorPriceSellAdd.value=false
                        },
                        isError= errorPriceSellAdd.value,
                        label = {
                            Text("Precio del producto por unidad")
                        },
                        leadingIcon = {
                            Icon(
                                ImageVector.vectorResource(R.drawable.price),
                                contentDescription = "Localized description",
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

            })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextUser(){
    var expanded by remember {mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorUser.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = user.value,
                onValueChange = {
                    user.value = it
                    expanded = true
                    errorUser.value=false
                },
                label = { Text("Nombre de usuario") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(25.dp)
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            val filteringOptions =
                listItemData.filter { it.nameUser.contains(user.value, ignoreCase = true) }
            if (filteringOptions.isEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded=!expanded},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("Nuevo usuario") },
                        onClick = {
                            expanded = false
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
                            text = { Text(selectionOption.nameUser) },
                            onClick = {
                                user.value = selectionOption.nameUser
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
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState",
    "CoroutineCreationDuringComposition"
)
@Composable
fun SellAddScreen(sellViewModel: SellViewModel, navController: NavController,stockViewModel: StockViewModel, eventViewModel: SummaryViewModel) {
    val stockValues = stockViewModel.stockEnBaseDeDatos.observeAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    var paid by rememberSaveable { mutableStateOf(true)}
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenHeightDp.dp
    if (stockValues == null) {
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
        productsStock.clear()
        productsStock.addAll(stockValues)
        AddProduct()

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .defaultMinSize(minHeight = screenWidth),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 0.dp, height = 150.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Agrega una venta",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                    )
                }
                TextUser()
                /*
                OutlinedTextField(
                    value = user,
                    onValueChange = {
                        user = it
                        error_name = false
                    },
                    label = {
                        Text("Nombre de usuario")
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    isError = error_name
                )*/
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp)
                )
                {
                    var textPaid = ""
                    if (paid) {
                        textPaid = "Pagado"
                    } else {
                        textPaid = "No pagado"
                    }
                    Text(
                        textPaid,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Switch(
                        modifier = Modifier.semantics { contentDescription = "Demo" },
                        checked = paid,
                        onCheckedChange = { paid = it })
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(20.dp)
                        .padding(top = 30.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Lista de productos",
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Button(
                        onClick = { openDialogAddItem.value = true },
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
                var snackbarHost = SnackbarHost(hostState = snackbarHostState)

                Column(
                    modifier = Modifier
                ) {
                    if (products.size > 0) {
                        for (i in 0..products.size - 1) {
                            Row() {
                                Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                    ) {
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
                                                    text = products[i].name.substring(0, 2)
                                                        .capitalize(),
                                                    color = Color.White
                                                )
                                            }
                                            Text(
                                                products[i].name,
                                                modifier = Modifier.align(Alignment.CenterVertically)
                                            )
                                        }
                                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                            Text(
                                                products[i].amount.toString() + " " + products[i].units.toString(),
                                                modifier = Modifier
                                                    .padding(end = 10.dp)
                                                    .align(Alignment.CenterVertically)
                                            )
                                            IconButton(
                                                onClick = { products.remove(products[i])
                                                totalPrice.value = products.map{it.amount*it.price}.sum().toFloat()
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Close,
                                                    contentDescription = "Localized description",
                                                    modifier = Modifier.size(25.dp)
                                                )
                                            }


                                        }
                                    }
                                    Divider()
                                }
                            }
                        }
                    }


                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 0.dp, height = 150.dp)
                        .padding(20.dp)
                ) {
                    Text(
                        "Precio total: ",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        "$ " + totalPrice.value.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Column() {


                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                    ) {
                        Button(
                            onClick = {
                                if (products.size == 0) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Debe ingresar al menos un producto vendido"
                                        )
                                    }
                                }
                                if (user.value == "") {
                                    errorUser.value = true
                                }
                                if (products.size != 0 && user.value != "") {
                                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                    val currentDate = sdf.format(Date())
                                    for (product in products) {
                                        var stockFinds =
                                            stockValues.filter { it.product.name == product.name }
                                        if (stockFinds.size == 0) {
                                            var productStock = Product(
                                                name = product.name,
                                                amount = (SubstackStock(0f, product.amount)),
                                                units = product.units, price = product.price
                                            )
                                            var stockNew = Stock(
                                                type = productsType[product.name]!!,
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
                                                    typeEvent = "Se realizo una venta",
                                                    referenceID=stockID
                                                )
                                            )
                                        } else {
                                            var stockFind = stockFinds[0]
                                            if (product.name in productsConvert.keys) {
                                                var listConversion = mutableListOf<Conversion>()
                                                listConversion.addAll(stockFind.conversion)
                                                listConversion.add(productsConvert[product.name]!!)
                                                stockFind.conversion = listConversion.toList()
                                                stockFind.product.amount = SubstackStock(
                                                    stockFind.product.amount,
                                                    product.amount * productsConvert[product.name]!!.amount
                                                )
                                            } else {
                                                stockFind.product.amount = SubstackStock(
                                                    stockFind.product.amount,
                                                    product.amount
                                                )
                                            }
                                            var stockID = stockViewModel.addUpdateProduct(stockFind)
                                            eventViewModel.addEventOperationStock(
                                                EventOperationStock(
                                                    date= SimpleDateFormat("yyyy/MM/dd HH:mm",Locale.getDefault())
                                                        .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time),
                                                    typeEvent = "Se realizo una venta",
                                                    referenceID=stockID
                                                )
                                            )
                                        }
                                    }
                                    sellViewModel.addSell(
                                        Sell(
                                            nameUser = user.value, items = products,
                                            date = currentDate, paid = paid,
                                            price = totalPrice.value
                                        )
                                    )
                                    products.clear()
                                    navController.popBackStack()
                                }
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
    }


}