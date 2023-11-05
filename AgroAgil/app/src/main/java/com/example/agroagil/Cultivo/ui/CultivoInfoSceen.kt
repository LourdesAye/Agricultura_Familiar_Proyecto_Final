package com.example.agroagil.Cultivo.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agroagil.Buy.ui.BuyViewModel
import com.example.agroagil.Summary.dataDateStart
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Crop
import com.example.agroagil.core.models.Plantation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

var currentPlantation = mutableStateOf(Plantation())
var currentCrop = mutableStateOf(Crop())

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CultivoInfoScreen(navController: NavController,cultivoViewModel: CultivoViewModel, plantationId: String){
    cultivoViewModel.getPlantation(plantationId)
    var valuesPlantation = cultivoViewModel.currentPlantation?.observeAsState()?.value
    var valuesCrop: Crop? = null
    if (valuesPlantation != null) {
        cultivoViewModel.getCrop(valuesPlantation.referenceId)
        valuesCrop = cultivoViewModel.currentCrop?.observeAsState()?.value
    }
    if (valuesPlantation == null || valuesCrop==null){
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
            }
        }
    }
}