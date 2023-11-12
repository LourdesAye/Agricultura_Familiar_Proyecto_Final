

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
import com.example.agroagil.R
import com.example.agroagil.Buy.ui.BuyViewModel
import com.example.agroagil.Buy.ui.listItemDataBuy
import com.example.agroagil.Loan.ui.AddStock
import com.example.agroagil.Stock.ui.StockViewModel
import com.example.agroagil.Stock.ui.tiposDeElementosDeStock
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Conversion
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Stock
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val openDialogAddItemBuy =  mutableStateOf(false)
val productsBuy = mutableStateListOf<Product>()
val totalPriceBuy = mutableStateOf(0f)
var userBuy = mutableStateOf("")
var errorUserBuy = mutableStateOf(false)
var nameProductBuy = mutableStateOf("")
var amountBuy =   mutableStateOf("")
var measureBuy  = mutableStateOf("")
var errorNameProductBuy = mutableStateOf(false)
var errorAmountBuy = mutableStateOf(false)
var errorMeasureBuy = mutableStateOf(false)
val productsStockBuy = mutableStateListOf<Stock>()
val stockSelectedBuy  = mutableStateOf<Stock?>(null)
val isNewProductBuy = mutableStateOf(false)
val isNewUnidadBuy = mutableStateOf(false)
var errorNameTypeBuy = mutableStateOf(false)
var nameTypeBuy = mutableStateOf("")
var nameUnidadConvertBuy = mutableStateOf("")
val productsTypeBuy = mutableMapOf<String,String>()
val productsConvertBuy = mutableMapOf<String, Conversion>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextProductBuy(){
    var expanded by remember {mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorNameProductBuy.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = nameProductBuy.value,
                onValueChange = {
                    nameProductBuy.value = it
                    expanded = true
                    errorNameProductBuy.value=false
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
            val filteringOptions = productsStockBuy.filter { it.product.name.contains(
                nameProductBuy.value, ignoreCase = true) }
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
                            isNewProductBuy.value=true
                            stockSelectedBuy.value=null
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
                                nameProductBuy.value = selectionOption.product.name
                                expanded = false
                                isNewProductBuy.value=false
                                stockSelectedBuy.value = selectionOption
                                measureBuy.value = selectionOption.product.units
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
fun TextTypeBuy() {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded},
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorNameTypeBuy.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = nameTypeBuy.value,
                onValueChange = {
                    nameTypeBuy.value = it
                    expanded = true
                    errorNameTypeBuy.value = false
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
                            nameTypeBuy.value = selectionOption
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
fun TextUnidadBuy(){
    var expanded by remember {mutableStateOf(false)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorMeasureBuy.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = measureBuy.value,
                onValueChange = {
                    measureBuy.value = it.uppercase()
                    expanded = true
                    errorMeasureBuy.value=false
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
            listUnits.addAll(stockSelectedBuy.value!!.conversion.map { it.name })
            listUnits.add(stockSelectedBuy.value!!.product.units)
            val filteringOptions = listUnits.filter{ it.contains(measureBuy.value, ignoreCase = true) }.toSet().toList()
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
                            isNewUnidadBuy.value = true
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
                                measureBuy.value = selectionOption
                                expanded = false
                                isNewUnidadBuy.value = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        }
    }
    if(isNewUnidadBuy.value){
        OutlinedTextField(
            value = nameUnidadConvertBuy.value,
            onValueChange = {
                nameUnidadConvertBuy.value = it
                errorMeasureBuy.value = false
            },
            label = {
                Text("Cuanto de "+ stockSelectedBuy.value!!.product.units+" equivale a 1 "+ measureBuy.value+"?")
            },
            trailingIcon = {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(25.dp)
                )
            },
            isError = errorMeasureBuy.value,
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
fun AddProductBuy(){

    if (openDialogAddItemBuy.value){
        AlertDialog(
            title={Text("Nuevo Producto")},
            modifier= Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onDismissRequest = {
                openDialogAddItemBuy.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nameProductBuy.value == ""){
                            errorNameProductBuy.value=true
                        }
                        if (amountBuy.value == ""){
                            errorAmountBuy.value=true
                        }
                        if (measureBuy.value == "" || (isNewUnidadBuy.value && nameUnidadConvertBuy.value=="")){
                            errorMeasureBuy.value=true
                        }
                        if (nameTypeBuy.value == ""){
                            errorNameTypeBuy.value=true
                        }
                        if (nameProductBuy.value != "" && amountBuy.value != "" && (nameTypeBuy.value != "") &&  ((measureBuy.value != "" && !isNewUnidadBuy.value)|| (measureBuy.value != "" && isNewUnidadBuy.value && nameUnidadConvertBuy.value!=""))){
                            if (isNewUnidadBuy.value){
                                productsConvertBuy[nameProductBuy.value] = Conversion(
                                    measureBuy.value, nameUnidadConvertBuy.value.toFloat())
                            }
                            productsTypeBuy[nameProductBuy.value] = nameTypeBuy.value
                            var new_item = Product(nameProductBuy.value,amountBuy.value.toInt(), units = measureBuy.value )
                            productsBuy.add(new_item)
                            openDialogAddItemBuy.value=false
                            nameProductBuy.value = ""
                            amountBuy.value=""
                            measureBuy.value=""
                        }
                    }
                ) {

                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogAddItemBuy.value = false
                        nameProductBuy.value = ""
                        amountBuy.value=""
                        measureBuy.value=""
                    }
                ) {
                    Text("No, Cancelar")
                }
            },

            text = {
                Column {
                    TextProductBuy()
                    TextTypeBuy()

                        OutlinedTextField(
                            value = amountBuy.value,
                            onValueChange = { amountBuy.value = it
                                errorAmountBuy.value=false
                            },
                            label = {
                                Text("Cantidad")
                            },
                            isError = errorMeasureBuy.value
                        )
                    if(isNewProductBuy.value==true){
                        OutlinedTextField(
                            value = measureBuy.value,
                            onValueChange = {
                                measureBuy.value = it.uppercase()
                                errorMeasureBuy.value= false},
                            label = {
                                Text("Unidad")
                            }
                            ,
                            isError = errorMeasureBuy.value,
                            modifier = Modifier.fillMaxWidth())
                    }
                        if(stockSelectedBuy.value != null){
                            TextUnidadBuy()
                        }

                }

            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextUserBuy(){
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
                isError = errorUserBuy.value,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = userBuy.value,
                onValueChange = {
                    userBuy.value = it
                    expanded = true
                    errorUserBuy.value=false
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
                listItemDataBuy.filter { it.nameUser.contains(userBuy.value, ignoreCase = true) }
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
                                userBuy.value = selectionOption.nameUser
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
fun BuyAddScreen(buyViewModel: BuyViewModel, navController: NavController, stockViewModel: StockViewModel) {
    var error_price by rememberSaveable { mutableStateOf(false)}
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
        productsStockBuy.clear()
        productsStockBuy.addAll(stockValues)
        AddProductBuy()
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .defaultMinSize(minHeight = screenWidth),
            verticalArrangement = Arrangement.SpaceBetween) {
            Column {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 0.dp, height = 150.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Agrega una compra",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                    )
                }
                TextUserBuy()
                /*
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it
                        error_name=false},
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
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp))
                {
                    var textPaid = ""
                    if(paid){
                        textPaid= "Pagado"
                    }else{
                        textPaid = "No pagado"
                    }
                    Text(textPaid, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterVertically))
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

                    Button(onClick = { openDialogAddItemBuy.value = true}, modifier = Modifier.align(Alignment.CenterVertically)) {
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
                    if(productsBuy.size>0){
                        for (i in 0..productsBuy.size-1) {
                            Row() {
                                Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)) {
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
                                                    text = productsBuy[i].name.substring(0, 2).capitalize(),
                                                    color = Color.White
                                                )
                                            }
                                            Text(
                                                productsBuy[i].name,
                                                modifier = Modifier.align(Alignment.CenterVertically)
                                            )
                                        }
                                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                            Text(
                                                productsBuy[i].amount.toString()+" "+ productsBuy[i].units.toString(), modifier = Modifier
                                                    .padding(end = 10.dp)
                                                    .align(Alignment.CenterVertically))
                                            IconButton(
                                                onClick = { productsBuy.remove(productsBuy[i])}){
                                                Icon(
                                                    Icons.Outlined.Close,
                                                    contentDescription = "Localized description",
                                                    modifier = Modifier.size(25.dp))
                                            }


                                        }
                                    }
                                    Divider()
                                }
                            }
                        }}


                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 0.dp, height = 150.dp)
                        .padding(20.dp)
                ) {
                    OutlinedTextField(
                        value = totalPriceBuy.value.toString(),
                        onValueChange = { totalPriceBuy.value = it.toFloat()
                            error_price=false
                        },
                        isError= error_price,
                        label = {
                            Text("Precio Total")
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
                    Text(
                        "$ "+ totalPriceBuy.value.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }}
            Column(){


                Box(modifier = Modifier.fillMaxSize()){
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)){
                        Button(onClick = {
                            if(productsBuy.size == 0){
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Debe ingresar al menos un producto vendido"
                                    )
                                }
                            }
                            if(userBuy.value==""){
                                errorUserBuy.value = true
                            }
                            if(totalPriceBuy.value.toFloat()==0f){
                                error_price = true
                            }
                            if (productsBuy.size !=0 && userBuy.value!="" && totalPriceBuy.value.toFloat()!=0f){
                                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                val currentDate = sdf.format(Date())
                                for (product in productsBuy) {
                                    var stockFinds =
                                        stockValues.filter { it.product.name == product.name }
                                    if (stockFinds.size == 0) {
                                        var productStock = Product(
                                            name = product.name,
                                            amount = (AddStock(0f, product.amount)),
                                            units = product.units, price = product.price
                                        )
                                        var stockNew = Stock(
                                            type = productsTypeBuy[product.name]!!,
                                            product = productStock,
                                            amountMinAlert = 0,
                                            date = SimpleDateFormat(
                                                "yyyy/MM/dd HH:mm",
                                                Locale.getDefault()
                                            )
                                                .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)
                                        )
                                        stockViewModel.addUpdateProduct(stockNew)
                                    } else {
                                        var stockFind = stockFinds[0]
                                        if (product.name in productsConvertBuy.keys) {
                                            var listConversion = mutableListOf<Conversion>()
                                            listConversion.addAll(stockFind.conversion)
                                            listConversion.add(productsConvertBuy[product.name]!!)
                                            stockFind.conversion = listConversion.toList()
                                            stockFind.product.amount = AddStock(
                                                stockFind.product.amount,
                                                product.amount * productsConvertBuy[product.name]!!.amount
                                            )
                                        } else {
                                            stockFind.product.amount = AddStock(
                                                stockFind.product.amount,
                                                product.amount
                                            )
                                        }
                                        stockViewModel.addUpdateProduct(stockFind)
                                    }
                                }
                                buyViewModel.addBuy(
                                    Buy(
                                    nameUser =userBuy.value, items = productsBuy,
                                    date = currentDate, paid =paid,
                                    price =totalPriceBuy.value)
                                )
                                productsBuy.clear()
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