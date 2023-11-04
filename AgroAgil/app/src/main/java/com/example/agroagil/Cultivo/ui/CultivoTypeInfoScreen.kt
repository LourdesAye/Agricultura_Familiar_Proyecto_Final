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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agroagil.core.models.Crop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

var cropsInfo = mutableStateListOf<Crop>()
var selectedAll = mutableStateOf(false)
var dialogAdd = mutableStateOf(false)
var dialogInfo = mutableStateOf(false)
var selectedItems = mutableStateListOf<Boolean>()
var selectedCrop = mutableStateListOf<Crop>()

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
                if(selectedAll.value){
                selectedCrop.addAll(cropsInfo)
                }
                                                                    }, modifier = Modifier.align(Alignment.CenterVertically))
            Text("Seleccionar todas las opciones", modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}
@Composable
fun ActionsInfo(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
){
    Row(modifier= Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp), horizontalArrangement = Arrangement.End){
    Button(onClick = {
        if (selectedCrop.size ==0){
        scope.launch {
            snackbarHostState.showSnackbar(
                "Debe seleccionar al menos un tipo de plantacion"
            )
        }}else{
        navController.navigate("cultivo/type/edit")
        }
    }) {
        Icon(
            Icons.Filled.Edit,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Modificar")
    }
    Button(onClick = {navController.navigate("cultivo/type/add")}, modifier=Modifier.padding(start=10.dp)) {
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
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
                    ActionsInfo(navController, snackbarHostState,scope)
                    AllSelected()
                }
                this.items(cropsInfo) {
                    OneCrop(it)
                }
                item {
                    var snackbarHost = SnackbarHost(hostState = snackbarHostState)

                }
            }
        }


    }
}