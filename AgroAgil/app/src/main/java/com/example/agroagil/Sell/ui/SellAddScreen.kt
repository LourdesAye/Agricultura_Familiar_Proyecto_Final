

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.R
import com.example.agroagil.Sell.ui.SellViewModel
import com.example.agroagil.core.models.Product
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

val openDialogAddItem =  mutableStateOf(false)
val products = mutableStateListOf<Product>()
val totalPrice = mutableStateOf(0.0)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProduct(){
    var name by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf(0.0) }
    var amount by rememberSaveable { mutableStateOf("") }
    var measure by rememberSaveable { mutableStateOf("") }
    var error_name by rememberSaveable { mutableStateOf(false)}
    var error_price by rememberSaveable { mutableStateOf(false)}
    var error_amount by rememberSaveable { mutableStateOf(false)}
    var error_measure by rememberSaveable { mutableStateOf(false)}

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
                        if (name == ""){
                            error_name=true
                        }
                        if (amount == ""){
                            error_amount=true
                        }
                        if (measure == ""){
                            error_measure=true
                        }
                        if (name != "" && amount != "" && measure != ""){
                            var new_item = Product(name,amount.toInt(), units = measure, price=price)
                            products.add(new_item)
                            totalPrice.value += price*amount.toDouble()
                            openDialogAddItem.value=false
                            name = ""
                            amount=""
                            measure=""
                            price = 0.0
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
                        name = ""
                        amount=""
                        measure=""
                    }
                ) {
                    Text("No, Cancelar")
                }
            },

            text = {
                Column(){
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it
                            error_name=false
                        },
                        isError= error_name,
                        label = {
                            Text("Nombre del producto")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()){
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it
                                error_amount=false
                            },
                            label = {
                                Text("Cantidad")
                            },
                            isError = error_amount,
                            modifier = Modifier.width(130.dp)
                        )
                        OutlinedTextField(
                            value = measure,
                            onValueChange = { measure = it
                                error_measure= false},
                            label = {
                                Text("Unidad")
                            }
                            ,
                            isError = error_measure,
                            modifier = Modifier.width(130.dp)

                        )
                    }
                    OutlinedTextField(
                        value = price.toString(),
                        onValueChange = { price = it.toDouble()
                            error_price=false
                        },
                        isError= error_price,
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
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

            })
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState",
    "CoroutineCreationDuringComposition"
)
@Composable
fun SellAddScreen(sellViewModel: SellViewModel, navController: NavController) {
    var user by rememberSaveable { mutableStateOf("") }
    var error_name by rememberSaveable { mutableStateOf(false)}
    val snackbarHostState = remember { SnackbarHostState() }
    var paid by rememberSaveable { mutableStateOf(true)}
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenHeightDp.dp
    AddProduct()

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
                "Agrega una venta",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            )
        }

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
        )
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

            Button(onClick = { openDialogAddItem.value = true}, modifier = Modifier.align(Alignment.CenterVertically)) {
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
            if(products.size>0){
                for (i in 0..products.size-1) {
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
                                            text = products[i].name.substring(0, 2).capitalize(),
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
                                        products[i].amount.toString()+" "+ products[i].units.toString(), modifier = Modifier
                                            .padding(end = 10.dp)
                                            .align(Alignment.CenterVertically))
                                    IconButton(
                                        onClick = { products.remove(products[i])}){
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
            Text(
                "Precio total: ",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Text(
                "$ "+ totalPrice.value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }}
        Column(){


        Box(modifier = Modifier.fillMaxSize()){
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                    .padding(30.dp)){
                Button(onClick = {
                    if(products.size == 0){
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Debe ingresar al menos un producto vendido"
                            )
                        }
                    }
                    if(user==""){
                        error_name = true
                    }
                    if (products.size !=0 && user!=""){
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val currentDate = sdf.format(Date())
                        sellViewModel.addSell(Sell(
                            nameUser =user, items = products,
                            date = currentDate, paid =paid,
                            price =totalPrice.value))
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