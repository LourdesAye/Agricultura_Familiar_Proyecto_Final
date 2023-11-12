package com.example.agroagil.Cultivo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agroagil.Stock.ui.StockViewModel
import com.example.agroagil.core.models.Stock
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CultivoTypeInfoEdit(cultivoViewModel: CultivoViewModel, stockViewModel: StockViewModel, navController: NavController){
    var valuesStock = stockViewModel.stockEnBaseDeDatos.observeAsState().value
    var durationDay by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    var expandOptionsUnitsText by remember { mutableStateOf(false) }
    var durationDayText by remember { mutableStateOf("") }
    var durationUnitText by remember { mutableStateOf("") }
    var durationUnitConvertText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var errorDurationDay by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var units by remember { mutableStateOf(false) }
    var unitsError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    if (valuesStock == null) {
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

        Column(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp, top = 20.dp)
                .fillMaxSize()
        ) {


            Text(
                text = "Modificar tipo de cultivo",
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Cultivos que se modificaran",
                    fontSize = 20.sp
                )
                LazyRow(
                    content = {
                        items(selectedCrop) { index ->
                            AssistChip(
                                onClick = {},
                                enabled = false,
                                label = { Text(index.name) },
                            )

                        }
                    }, modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    "Que atributos desea modificar?",
                    fontSize = 20.sp
                )
                Divider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "Tiempo estimado de cosecha en dias", modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterVertically)
                        )
                        Switch(
                            checked = durationDay,
                            onCheckedChange = { durationDay = it }
                        )
                    }
                    if (durationDay) {
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
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            isError = errorDurationDay,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
                Divider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                ) {
                    Text("Precio", modifier = Modifier.align(Alignment.CenterVertically))
                    Switch(
                        checked = price,
                        onCheckedChange = { price = it }
                    )
                }
                if (price) {
                    ExposedDropdownMenuBox(
                        expanded = expandOptionsUnitsText,
                        onExpandedChange = { expandOptionsUnitsText = !expandOptionsUnitsText }) {
                        TextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier.menuAnchor(),
                            value = selectedOptionText,
                            onValueChange = {
                                selectedOptionText = it
                                priceError = false
                            },
                            label = { Text("Que tipo de cambio desea hacer?") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = true) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            enabled = false,
                            isError = priceError
                        )
                        ExposedDropdownMenu(
                            expanded = expandOptionsUnitsText,
                            onDismissRequest = { expandOptionsUnitsText = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Valor") },
                                onClick = {
                                    selectedOptionText = "Valor"
                                    expandOptionsUnitsText = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                            DropdownMenuItem(
                                text = { Text("Porcentaje") },
                                onClick = {
                                    selectedOptionText = "Porcentaje"
                                    expandOptionsUnitsText = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )

                        }

                    }
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = {
                            priceText = it
                            priceError = false
                        },
                        label = {
                            Text("Nuevo precio")
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        isError = priceError,
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                    )
                }

                Divider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                ) {
                    Text("Unidad", modifier = Modifier.align(Alignment.CenterVertically))
                    Switch(
                        checked = units,
                        onCheckedChange = { units = it }
                    )
                }
                if (units) {
                    OutlinedTextField(
                        value = durationUnitText,
                        onValueChange = {
                            durationUnitText = it.uppercase()
                            unitsError = false
                        },
                        label = {
                            Text("Nueva unidad de medida")
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        isError = unitsError,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = durationUnitConvertText,
                        onValueChange = {
                            durationUnitConvertText = it
                            unitsError = false
                        },
                        label = {
                            Text("Cuanto de la unidad de medida anterior equivale la actual")
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        isError = unitsError,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                }
                Divider()
            }
            var snackbarHost = SnackbarHost(hostState = snackbarHostState)
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (!durationDay && !price && !units) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Debe seleccionar el campo que desea modificar"
                                    )
                                }
                            } else {
                                if (durationDay && durationDayText == "") {
                                    errorDurationDay = true
                                } else {
                                    if (durationDay) {
                                        selectedCrop.forEach {
                                            it.durationDay = durationDayText.toFloat().toInt()
                                        }
                                    }
                                }
                                if (price && (priceText == "" || selectedOptionText == "")) {
                                    priceError = true
                                } else {
                                    if (price) {
                                        if (selectedOptionText == "Valor") {
                                            selectedCrop.forEach {
                                                it.price = priceText.toFloat()
                                            }
                                        } else {
                                            selectedCrop.forEach {
                                                it.price = it.price * priceText.toFloat()
                                            }
                                        }
                                    }
                                }
                                if (units && (durationUnitConvertText == "" || durationUnitText == "")) {
                                    unitsError = true
                                } else {
                                    if (units) {
                                        selectedCrop.forEach {
                                            it.units = durationUnitText
                                            //it.conversion.forEach { it.amount =  durationUnitConvertText.toFloat()/it.amount}
                                        }
                                    }
                                }
                                if (!errorDurationDay && !priceError && !unitsError) {
                                    if(units){
                                        selectedCrop.map {
                                            var resultado = valuesStock.find{
                                                stock ->
                                                    stock.product.name == it.name
                                            }
                                            if (resultado != null && resultado.product.units != it.units) {
                                                resultado.product.amount = resultado.product.amount/durationUnitConvertText.toFloat()
                                                resultado.product.units = it.units
                                                resultado.conversion.forEach { it.amount =  durationUnitConvertText.toFloat()/it.amount}
                                                stockViewModel.addUpdateProduct(resultado)
                                            }
                                        }
                                    }
                                    cultivoViewModel.updateCrop(selectedCrop)
                                    navController.popBackStack()
                                }
                            }
                        }
                    ) {
                        Text("Guardar")
                    }

                    TextButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Text("Cancelar")
                    }
                }
            }
        }
    }



}