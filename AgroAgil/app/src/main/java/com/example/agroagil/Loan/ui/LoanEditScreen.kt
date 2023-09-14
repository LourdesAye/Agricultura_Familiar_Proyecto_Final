package com.example.agroagil.Loan.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Loan
import kotlinx.coroutines.launch
import java.util.Date

var openDialogAddItemEdit =  mutableStateOf(false)
var currentLoanEdit = Loan("Usuario1", listOf<Product>(Product("Tomate", 1, "KG")), emptyList(), 0)
var productsEdit = mutableStateListOf<Product>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductEidt(){
    var name by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var measure by rememberSaveable { mutableStateOf("") }
    var error_name by rememberSaveable { mutableStateOf(false)}
    var error_amount by rememberSaveable { mutableStateOf(false)}
    var error_measure by rememberSaveable { mutableStateOf(false)}

    if (openDialogAddItemEdit.value){
        AlertDialog(
            title={Text("Nuevo Producto")},
            modifier= Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onDismissRequest = {
                openDialogAddItemEdit.value = false
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
                            var new_item = Product(name,amount.toInt(), units = measure)
                            productsEdit.add(new_item)
                            openDialogAddItemEdit.value=false
                            name = ""
                            amount=""
                            measure=""
                        }
                    }
                ) {

                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogAddItemEdit.value = false
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
                }

            })
    }
}

@Composable
fun itemProductClose(item: Product){
    Row() {
        Column(modifier = Modifier.padding(top = 5.dp)) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)) {
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
                            text = item.name.substring(0, 2).capitalize(),
                            color = Color.White
                        )
                    }
                    Text(
                        item.name,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        item.amount.toString()+" "+ item.units.toString(), modifier = Modifier
                            .padding(end = 10.dp)
                            .align(Alignment.CenterVertically))
                    IconButton(
                        onClick = { productsEdit.remove(item)}){
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEditScreen(navController: NavController, loanViewModel: LoanViewModel, loanId: Int) {
    var valuesLoan = loanViewModel.farm.observeAsState().value
    if (valuesLoan == null){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp)
            )
        }

    }else {
        currentLoanEdit = valuesLoan.get(loanId)
        if (productsEdit.size ==0){
            productsEdit.addAll(currentLoanEdit.paid)
        }
        var percentagePaid by rememberSaveable { mutableStateOf(currentLoanEdit.percentagePaid.toString()+ " %") }
        var percentagePaidError by rememberSaveable { mutableStateOf(false)}
        AddProductEidt()
        Box(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                ) {
                    Text(
                        currentLoanEdit.nameUser,
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
                        currentLoanEdit.date,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Text(
                    "Productos pagados",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 50.dp, bottom = 10.dp)
                )

                OutlinedTextField(
                    value = percentagePaid,
                    onValueChange = {
                        percentagePaid = it
                        percentagePaidError = false
                    },
                    label = {
                        Text("Porcentaje pagado")
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = percentagePaidError
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {


                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Elementos",
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Button(
                            onClick = { openDialogAddItemEdit.value = true },
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
                    for (i in 0..productsEdit.size - 1) {
                        itemProductClose(productsEdit[i])
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(30.dp)
                    .fillMaxWidth()){
                Button(onClick = {
                    currentLoanEdit.percentagePaid = percentagePaid.replace("%", "").replace(" ", "").toInt()
                    currentLoanEdit.paid = productsEdit
                    loanViewModel.updateLoan(currentLoanEdit, loanId)
                        productsEdit.clear()
                        navController.popBackStack()

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