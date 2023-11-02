

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agroagil.Cultivo.ui.CultivoViewModel
import com.example.agroagil.core.models.Crop
import com.example.agroagil.core.models.Plantation
import java.text.SimpleDateFormat
import java.util.Date


var crops = mutableStateListOf<Crop>()
var idReference = mutableStateOf("")
var isNew = mutableStateOf(false)
var units = mutableStateOf("")
var durationDay = mutableStateOf("")
var price = mutableStateOf("")
var type = mutableStateOf("")
var errorDurationDay = mutableStateOf(false)
var errorUnits =mutableStateOf(false)
var errorPrice =mutableStateOf(false)
var errorType =mutableStateOf(false)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState",
    "CoroutineCreationDuringComposition"
)
@Composable
fun FormCrop(){
    if (isNew.value){
        Row(modifier = Modifier
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()) {
        OutlinedTextField(
            value = units.value,
            onValueChange = {
                units.value = it.uppercase()
                errorUnits.value = false
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
            isError = errorUnits.value,
            modifier = Modifier.fillMaxWidth()
        )}
        Row(modifier = Modifier
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()) {
        OutlinedTextField(
            value = price.value,
            onValueChange = {
                price.value = it
                errorPrice.value = false
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
            isError = errorPrice.value,
            modifier = Modifier.fillMaxWidth()
        )}
        Row(modifier = Modifier
            .padding(top = 40.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()) {
            OutlinedTextField(
                value = durationDay.value,
                onValueChange = {
                    durationDay.value = it
                    errorDurationDay.value = false
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
                isError = errorDurationDay.value,
                modifier = Modifier
                    .fillMaxWidth()
            )}
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState",
    "CoroutineCreationDuringComposition"
)
@Composable
fun TypeCrop(){
    var expanded by remember {mutableStateOf(true)}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {  },
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                isError = errorType.value,
                modifier = Modifier
                    .menuAnchor().fillMaxWidth(),
                value = type.value,
                onValueChange = {
                    type.value = it
                    expanded = true
                },
                label = { Text("Tipo de cultivo") },
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
                    crops.filter { it.name.contains(type.value, ignoreCase = true) }
                if (filteringOptions.isEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(
                            text = { Text("Crear nuevo tipo de cultivo") },
                            onClick = {
                                expanded = false
                                isNew.value = true
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
                                text = { Text(selectionOption.name) },
                                onClick = {
                                    idReference.value = selectionOption.id
                                    type.value = selectionOption.name
                                    expanded = false
                                    isNew.value = false
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
fun CultivoAddScreen(cultivoViewModel: CultivoViewModel, navController: NavController) {
    val crop = cultivoViewModel.crop.observeAsState().value
    var namePlantation by rememberSaveable { mutableStateOf("") }
    var errorPlantation by rememberSaveable { mutableStateOf(false)}
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
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
        crops.clear()
        crops.addAll(crop)
        Column(
            modifier = Modifier
                .defaultMinSize(minHeight = screenHeight),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier
                .defaultMinSize(minHeight = screenHeight)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 0.dp, height = 150.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Agrega una plantacion",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                    )
                }

                OutlinedTextField(
                    value = namePlantation,
                    onValueChange = {
                        namePlantation = it
                        errorPlantation = false
                    },
                    label = {
                        Text("Nombre de plantacion")
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
                    isError = errorPlantation
                )
                Row(modifier = Modifier
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()) {
                    TypeCrop()
                }
                FormCrop()

                Column(modifier = Modifier.fillMaxSize(), verticalArrangement =  Arrangement.Bottom) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp)
                        ) {
                            Button(
                                onClick = {
                                    if(namePlantation!="" && type.value!="" && !isNew.value){
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())
                                        cultivoViewModel.createPlantation(Plantation(namePlantation, currentDate, idReference.value,"CREADO"))
                                        navController.popBackStack()
                                    }
                                    if(namePlantation!="" && type.value!="" && isNew.value && units.value!="" && price.value != "" && durationDay.value!=""){
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())
                                        idReference.value = cultivoViewModel.createCrop(Crop(name = type.value, units = units.value, durationDay = durationDay.value.toInt(), price=price.value.toDouble())).toString()
                                        cultivoViewModel.createPlantation(Plantation(namePlantation, currentDate, idReference.value,"CREADO"))
                                        navController.popBackStack()
                                    }
                                    if(namePlantation == ""){
                                        errorPlantation = true
                                    }
                                    if(type.value == ""){
                                        errorType.value = true
                                    }
                                    if(durationDay.value == ""){
                                        errorDurationDay.value = true
                                    }
                                    if(isNew.value && units.value==""){
                                        errorUnits.value = true
                                    }
                                    if(isNew.value && price.value==""){
                                        errorPrice.value = true
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