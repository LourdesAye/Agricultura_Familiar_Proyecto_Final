package com.example.agroagil.Cultivo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agroagil.core.models.Crop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CultivoTypeInfoAdd(cultivoViewModel: CultivoViewModel,navController: NavController){
    var name by remember {
        mutableStateOf("")
    }
    var errorName by remember {
        mutableStateOf(false)
    }
    var units by remember {
        mutableStateOf("")
    }
    var errorUnits by remember {
        mutableStateOf(false)
    }
    var price by remember {
        mutableStateOf("")
    }
    var errorPrice by remember {
        mutableStateOf(false)
    }
    var durationDay by remember {
        mutableStateOf("")
    }
    var errorDurationDay by remember {
        mutableStateOf(false)
    }
    val screenHeight= LocalConfiguration.current.screenHeightDp.dp
    Column(modifier=Modifier.padding(start=20.dp, end=20.dp, bottom=50.dp, top=20.dp).fillMaxSize() ){
    Text(text = "Nuevo tipo de cultivo",fontSize = 28.sp, modifier = Modifier.padding(bottom = 40.dp))
    Column(modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = screenHeight*0.30f) , verticalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it.capitalize().trim()
                                errorName = false
                            },
                            label = {
                                Text("Nombre del tipo de cultivo")
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Localized description",
                                    modifier = Modifier.size(25.dp)
                                )
                            },
                            isError = errorName,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        OutlinedTextField(
                            value = units,
                            onValueChange = {
                                units = it.uppercase().trim()
                                errorUnits = false
                            },
                            label = {
                                Text("Unidades en que se cosecha")
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Localized description",
                                    modifier = Modifier.size(25.dp)
                                )
                            },
                            isError = errorUnits,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = {
                                price = it
                                errorPrice = false
                            },
                            label = {
                                Text("Precio estimado por unidad")
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Localized description",
                                    modifier = Modifier.size(25.dp)
                                )
                            },
                            isError = errorPrice,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        OutlinedTextField(
                            value = durationDay,
                            onValueChange = {
                                durationDay = it
                                errorDurationDay = false
                            },
                            label = {
                                Text("Tiempo estimado de cosecha en dias")
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Localized description",
                                    modifier = Modifier.size(25.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = errorDurationDay,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
        Column(modifier=Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
    Button( onClick = {
        if(name!="" && units != "" && durationDay != "" && price != ""){
            var crop = cropsInfo.find { it.name == name }
            if(crop!=null){
                errorName=true
            }else{
                cultivoViewModel.createCrop(Crop(name=name, units=units, durationDay = durationDay.toFloat().toInt(), price = price.toFloat().toDouble()))
                navController.popBackStack()
            }
        }else{
        if (name ==""){
            errorName=true
        }
            if (units ==""){
                errorUnits = true
            }
            if (durationDay ==""){
                errorDurationDay=true
            }
            if (price ==""){
                errorPrice=true
            }
        }

    }) {
                    Text("Guardar")
                }
    TextButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text("Cancelar")
                }

    }}}

}