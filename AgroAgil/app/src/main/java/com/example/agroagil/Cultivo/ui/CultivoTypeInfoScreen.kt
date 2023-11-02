package com.example.agroagil.Cultivo.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agroagil.Buy.ui.Actions
import com.example.agroagil.Buy.ui.OneBuy
import com.example.agroagil.Buy.ui.filterStatus
import com.example.agroagil.Buy.ui.listItemDataFilter
import com.example.agroagil.R
import com.example.agroagil.core.models.Crop
import kotlinx.coroutines.selects.select

var cropsInfo = mutableStateListOf<Crop>()
var selectedAll = mutableStateOf(false)
var dialogAdd = mutableStateOf(false)
var dialogInfo = mutableStateOf(false)
var selectedItems = mutableStateListOf<Boolean>()
var selectedCrop = mutableStateListOf<Crop>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogInfo(){
    var durationDay by remember {mutableStateOf(false) }
    var durationDayText by remember {mutableStateOf("") }
    var errorDurationDay by remember { mutableStateOf(false) }
    var price by remember {mutableStateOf(false) }
    var units by remember {mutableStateOf(false) }

    if (dialogInfo.value){
        AlertDialog(
            onDismissRequest = {
                dialogInfo.value = false
            },
            title = {
                Text(text = "Modificar tipo de cultivo")
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxWidth()) {
                    Text("Cultivos que se modificaran")
                    LazyRow(
                        content = {
                            items(selectedCrop) { index ->
                                        AssistChip(
                                            onClick = {},
                                            enabled=false,
                                            label = { Text(index.name) },
                                        )

                            }
                        }, modifier = Modifier.padding(bottom=5.dp))
                    Divider()
                    Column(modifier= Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)){
                    Row(horizontalArrangement = Arrangement.SpaceBetween){
                       Text("Tiempo estimado de cosecha en dias", modifier= Modifier
                           .fillMaxWidth(0.8f)
                           .align(Alignment.CenterVertically))
                        Switch(
                            checked = durationDay,
                            onCheckedChange = { durationDay = it }
                        )
                    }
                        if(durationDay){
                            OutlinedTextField(
                                value = durationDayText,
                                onValueChange = {
                                    durationDayText = it
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
                                isError = errorDurationDay,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                    Divider()
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier= Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)){
                        Text("Precio", modifier = Modifier.align(Alignment.CenterVertically))
                        Switch(
                            checked = price,
                            onCheckedChange = { price = it }
                        )
                    }
                    Divider()
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier= Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)){
                        Text("Unidad", modifier = Modifier.align(Alignment.CenterVertically))
                        Switch(
                            checked = units,
                            onCheckedChange = { units = it }
                        )
                    }
                    Divider()
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogInfo.value = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogInfo.value = false
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
fun DialogAdd(){
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
    if (dialogAdd.value){
        AlertDialog(
            onDismissRequest = {
                dialogAdd.value = false
            },
            title = {
                Text(text = "Nuevo tipo de cultivo")
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxWidth()) {
                    Row(modifier = Modifier
                        .padding(top = 5.dp, start = 20.dp)
                        .fillMaxWidth()) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it.uppercase()
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
                    .padding(top = 5.dp, start = 20.dp)
                    .fillMaxWidth()) {
                    OutlinedTextField(
                        value = units,
                        onValueChange = {
                            units = it.uppercase()
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
                    .padding(top = 5.dp, start = 20.dp)
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
                    .padding(top = 5.dp, start = 20.dp)
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
                        isError = errorDurationDay,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogAdd.value = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogAdd.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
@Composable
fun OneCrop(crop: Crop){
    var index = cropsInfo.indexOf(crop)
    var selected =selectedItems[index]
    OutlinedCard(content={
    Row() {
        Checkbox(checked = selected, onCheckedChange = {
            selectedItems[index] = !selected
            if (!selected){
            selectedCrop.add(crop)
            }else{
                selectedCrop.remove(crop)
            }
            if (selectedAll.value){
                selectedAll.value = false
            }
            selectedAll.value = selectedItems.all { it }
                                                       }, modifier = Modifier.align(Alignment.CenterVertically))
        Text(crop.name, modifier = Modifier.align(Alignment.CenterVertically))
    }},
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
        )

}
@Composable
fun AllSelected(){
    Row(){
        Row() {
            Checkbox(checked = selectedAll.value, onCheckedChange = {
                selectedAll.value = !selectedAll.value
                selectedItems.replaceAll { selectedAll.value }
                selectedCrop.clear()
                selectedCrop.addAll(cropsInfo)
                                                                    }, modifier = Modifier.align(Alignment.CenterVertically))
            Text("Seleccionar todas las opciones", modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}
@Composable
fun ActionsInfo(navController: NavController){
    Row(modifier= Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp), horizontalArrangement = Arrangement.End){
    Button(onClick = { dialogInfo.value=true}) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Modificar")
    }
    Button(onClick = { dialogAdd.value=true}, modifier=Modifier.padding(start=10.dp)) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Agregar")
    }
    }
}
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun CultivoTypeInfoScreen(cultivoViewModel: CultivoViewModel,  navController: NavController) {
    val crop = cultivoViewModel.crop.observeAsState().value
    if (crop == null) {
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
        cropsInfo.clear()
        cropsInfo.addAll(crop)
        if (selectedItems.size != cropsInfo.size) {
            selectedItems.clear()
            selectedItems.addAll(cropsInfo.map { false })
        }
        Column() {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                item {
                    ActionsInfo(navController)
                    AllSelected()
                }
                this.items(cropsInfo) {
                    OneCrop(it)
                }
            }
        }
        DialogAdd()
        DialogInfo()

    }
}