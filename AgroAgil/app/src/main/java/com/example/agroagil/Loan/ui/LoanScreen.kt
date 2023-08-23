package com.example.agroagil.Loan.ui
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.agroagil.R
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.core.models.Item
import com.example.agroagil.core.models.Loan
import java.util.Date

val SinPagar = "#A93226"
val PagadoParcialmente = "#D4AC0D"
val Pagado = "#28B463"

var listItemData = mutableListOf<Loan>()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actions(navController: NavController){
    var expandedFilter by remember { mutableStateOf(false) }
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    }

    Column {
    Row(

        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 10.dp)
            .fillMaxWidth()
    ) {
    Button(onClick = {
        navController.navigate("loan/add")
                     },modifier=Modifier.padding(end=10.dp), colors=ButtonDefaults.filledTonalButtonColors()) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Agregar")
    }//ButtonDefaults.buttonColors()
    Button(onClick = { expandedFilter = !expandedFilter},colors= if (expandedFilter) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()) {
        Icon(
            ImageVector.vectorResource(R.drawable.filter),
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Filtrar")
    }
    }
    Column(modifier=Modifier.padding(end = 10.dp, start = 10.dp, bottom = 20.dp)){
    AnimatedVisibility(visible = expandedFilter) {
        Column(modifier = Modifier.fillMaxWidth()){
            Card(modifier = Modifier.fillMaxWidth()) {
                Column (modifier = Modifier.align(Alignment.CenterHorizontally).padding(30.dp)){
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Nombre de usuario")},
                        modifier=Modifier.fillMaxWidth()
                    )
                    Text("Filtrar por Fecha")
                    Text("Filtrar por Prestado")
                }
            }
        }
    }
    }
    }
}

fun SelectColorCard(percentagePaid:Int): String {
    var color: String = "white"
    if (percentagePaid==0){
        color = SinPagar
    }else{
        if (percentagePaid>=100){
            color = Pagado
        }else{
            color = PagadoParcialmente
        }
    }
    return color
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneLoan(itemData:Loan, navController: NavController){
    Column(){
    Card(
        onClick={
            navController.navigate("loan/${listItemData.indexOf(itemData)}/info")
        },
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )

    ){
        Row() {
            Column(
                modifier = Modifier
                    .background(Color(SelectColorCard(itemData.percentagePaid).toColorInt()))
                    .width(10.dp)
                    .fillMaxHeight()
            ) {

            }
            Column(modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)) {
                Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(SolidColor(Color("#00687A".toColorInt())))
                    }
                    Text(text =itemData.nameUser.substring(0,2).capitalize(),color= Color.White)
                }
            }
            Column(modifier = Modifier.padding(5.dp).fillMaxWidth()) {

                Text(itemData.date, fontSize=10.sp, modifier = Modifier.align(Alignment.End))
                Text(itemData.nameUser, fontWeight= FontWeight.Bold)
                var description = ""
                for (i in 0..itemData.items.size-1){
                    description+=itemData.items[i].amount.toString() + " "+itemData.items[i].name + ", "
                }
                if (description.length>70)
                    description = description.substring(0,69)+"..."
                else
                    description = description.substring(0,description.length-2)
                Text(description)
            }
        }

    }
    }
}
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun LoanScreen(loanViewModel: LoanViewModel, navController: NavController) {
    var valuesLoan = loanViewModel.farm.observeAsState().value
    valuesLoan?.let {
        listItemData.clear()
        listItemData.addAll(it)
    }
    if (valuesLoan == null){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp)
            )
        }

    }else {
        Column() {
            Actions(navController)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                this.items(listItemData) {
                    OneLoan(it, navController)
                }
            }
        }
    }
}