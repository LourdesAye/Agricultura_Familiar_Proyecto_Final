package com.example.agroagil.Cultivo.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agroagil.Stock.ui.dialogEditOpen
import com.example.agroagil.core.models.Crop
import com.example.agroagil.core.models.Plantation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import com.example.agroagil.Stock.ui.StockViewModel
import com.example.agroagil.Summary.SummaryViewModel
import com.example.agroagil.core.models.EventOperationBox
import com.example.agroagil.core.models.EventOperationStock
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Stock

var currentPlantation = mutableStateOf(Plantation())
var currentCrop = mutableStateOf(Crop())
var dialogCosecharOpen = mutableStateOf(false)
var dialogCosecharCantidadError = mutableStateOf(false)
var dialogCosecharCantidad = mutableStateOf("")

@Composable
fun dialogCosechar(cultivoViewModel: CultivoViewModel, navController: NavController, stockViewModel: StockViewModel, stockEvent: SummaryViewModel){
    if(dialogCosecharOpen.value){
        AlertDialog(
            onDismissRequest = {
                dialogCosecharOpen.value = false
            },
            title = {
                Text(text = "Cosechar")
            },
            text = {
                OutlinedTextField(
                    value = dialogCosecharCantidad.value,
                    onValueChange = { dialogCosecharCantidad.value = it },
                    label = { Text("Cuantos "+ currentCrop.value.units+" fue cosechado?") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = Number
                    ),
                    isError = dialogCosecharCantidadError.value
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dialogCosecharCantidad.value==""){
                            dialogCosecharCantidadError.value = true
                        }else{
                            currentPlantation.value.status = "COSECHADO"
                            cultivoViewModel.updatePlantation(currentPlantation.value)
                            var stockID=stockViewModel.addUpdateProduct(Stock(type="Cultivo", date= SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time),
                                product= Product(
                                    name = currentCrop.value.name,
                                    amount = dialogCosecharCantidad.value.toFloat().toInt(),
                                    units = currentCrop.value.units,
                                    price = currentCrop.value.price
                                )
                            ))
                            stockEvent.addEventOperationStock(
                                EventOperationStock(
                                    date = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                        .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time),
                                    typeEvent = "Cosechado desde la seccion Cultivo",
                                    referenceID = stockID,
                                    amount=dialogCosecharCantidad.value.toFloat()
                                )
                            )
                            dialogCosecharOpen.value = false
                            navController.popBackStack()
                        }
                    }) {
                    Text("Guardar")
                }},
                dismissButton = {
                    TextButton(
                        onClick = {
                            dialogEditOpen.value = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                })}

}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CultivoInfoScreen(navController: NavController,cultivoViewModel: CultivoViewModel, plantationId: String, stockViewModel: StockViewModel, stockEvent: SummaryViewModel){
    cultivoViewModel.getPlantation(plantationId)
    var valuesPlantation = cultivoViewModel.currentPlantation?.observeAsState()?.value
    var stocks = stockViewModel.stockEnBaseDeDatos?.observeAsState()?.value
    var valuesCrop: Crop? = null
    if (valuesPlantation != null) {
        cultivoViewModel.getCrop(valuesPlantation.referenceId)
        valuesCrop = cultivoViewModel.currentCrop?.observeAsState()?.value
    }
    if (valuesPlantation == null || valuesCrop==null || stocks==null){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }

    }else {
        currentPlantation.value = valuesPlantation as Plantation
        currentCrop.value = valuesCrop
        Column(verticalArrangement = Arrangement.SpaceAround,modifier = Modifier.padding(start=20.dp, end = 20.dp)) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                ) {
                    Text(
                        currentPlantation.value.name,
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
                        currentPlantation.value.dateStart,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                val date_format = SimpleDateFormat("dd/MM/yyyy HH:mm")
                var date_event = date_format.parse(currentPlantation.value.dateStart)
                val currentDate = Calendar.getInstance()
                currentDate.setTime(date_event)
                currentDate.add(Calendar.DAY_OF_MONTH, currentCrop.value.durationDay)
                var dateEnd = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentDate.time)
                Column(modifier = Modifier.padding(top=20.dp)) {
                    ListItem(
                        headlineContent = {
                            Row(){
                            Text("Tipo de cultivo: ", fontSize=MaterialTheme.typography.bodyLarge.fontSize)
                            Text(currentCrop.value.name, fontSize=MaterialTheme.typography.bodyLarge.fontSize, fontWeight= FontWeight.Bold)
                            }
                                 },
                        leadingContent = {
                            Icon(
                                Icons.Filled.ArrowCircleRight ,
                                contentDescription = "Localized description",
                            )
                        },
                        modifier=Modifier.padding(top=10.dp)
                    )
                    Divider()
                    ListItem(
                        headlineContent = {
                            Row(){
                            Text("Unidades en que se cosecha: ", fontSize=MaterialTheme.typography.bodyLarge.fontSize)
                            Text(currentCrop.value.units, fontSize=MaterialTheme.typography.bodyLarge.fontSize, fontWeight= FontWeight.Bold)
                            }
                            },
                        leadingContent = {
                            Icon(
                                Icons.Filled.ArrowCircleRight ,
                                contentDescription = "Localized description",
                            )
                        },
                        modifier=Modifier.padding(top=10.dp)
                    )
                    Divider()
                    ListItem(
                        headlineContent = {
                            Row(){
                                Text("Precio estimado: ", fontSize=MaterialTheme.typography.bodyLarge.fontSize)
                                Text(currentCrop.value.price.toString(), fontSize=MaterialTheme.typography.bodyLarge.fontSize, fontWeight= FontWeight.Bold)
                            }
                                          },
                        leadingContent = {
                            Icon(
                                Icons.Filled.ArrowCircleRight ,
                                contentDescription = "Localized description",
                            )
                        },
                        modifier=Modifier.padding(top=10.dp)
                    )
                    Divider()
                    ListItem(
                        headlineContent = {
                            Row(){
                            Text("Fecha estimado de cosecha: ", fontSize=MaterialTheme.typography.bodyLarge.fontSize)
                            Text(dateEnd, fontSize=MaterialTheme.typography.bodyLarge.fontSize, fontWeight= FontWeight.Bold)
                        }
                                          },
                        leadingContent = {
                            Icon(
                                Icons.Filled.ArrowCircleRight ,
                                contentDescription = "Localized description",
                            )
                        },
                        modifier=Modifier.padding(top=10.dp)
                    )
                    Divider()
                }
                Button(onClick = { dialogCosecharOpen.value=true},
                        modifier= Modifier
                            .padding(end = 20.dp, bottom = 40.dp, top = 40.dp)
                            .align(Alignment.End)) {
                Icon(
                    Icons.Filled.Agriculture,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Cosechar")
                }
                dialogCosechar(cultivoViewModel, navController, stockViewModel, stockEvent)
            }
        }
    }
}