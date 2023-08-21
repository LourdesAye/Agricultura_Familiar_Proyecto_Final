package com.example.agroagil.ui.Loan
import androidx.navigation.compose.rememberNavController
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.agroagil.R
import com.example.agroagil.ui.Farm.FarmViewModel
import com.example.agroagil.ui.Farm.openDialogMember
import androidx.navigation.compose.NavHost
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.core.models.Item
import com.example.agroagil.core.models.Loan
import java.util.Date
import java.util.Locale

val SinPagar = "#A93226"
val PagadoParcialmente = "#D4AC0D"
val Pagado = "#28B463"

val listItemData = listOf<Loan>(
    Loan("Usuario1", listOf<Item>(Item("Tomate", 1, "KG")), emptyList(), 0.0, Date(2023,1,1,15,0)),
    Loan("Usuario2", listOf<Item>(Item("Papas", 1, "KG")), emptyList(), 0.0, Date(2023,1,1,15,0)),
    Loan("Usuario3", listOf<Item>(Item("Calabaza", 1, "Unidad")), emptyList(), 0.5, Date(2023,1,1,15,0)),
    Loan("Usuario4", listOf<Item>(Item("Tomate", 1, "KG")), listOf<Item>(Item("Papas", 1, "KG")), 1.0,Date(2023,1,1,15,0)),
    Loan("Usuario5", listOf<Item>(Item("Tomate", 1, "KG")), listOf<Item>(Item("Papas", 1, "KG")), 1.0, Date(2023,1,1,15,0)),
    Loan("Usuario6", listOf<Item>(Item("Tomate", 1, "KG")), listOf<Item>(Item("Papas", 1, "KG")), 1.0, Date(2023,1,1,15,0)),
    Loan("Usuario7", listOf<Item>(Item("Tomate", 1, "KG")), listOf<Item>(Item("Papas", 1, "KG")), 0.2, Date(2023,1,1,15,0)),
    Loan("Usuario8", listOf<Item>(Item("Tomate", 1, "KG")), listOf<Item>(Item("Papas", 1, "KG")), 0.3, Date(2023,1,1,15,0)),
    Loan("Usuario10", listOf<Item>(Item("Tomate", 1, "KG")), listOf<Item>(Item("Papas", 1, "KG")),0.5,Date(2023,1,1,15,0) )
    )

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
                    Text("Filtrar por Nombre")
                    Text("Filtrar por Fecha")
                    Text("Filtrar por Prestado")
                }
            }
        }
    }
    }
    }
}

fun SelectColorCard(percentagePaid:Double): String {
    var color: String = "white"
    if (percentagePaid==0.0){
        color = SinPagar
    }else{
        if (percentagePaid==1.0){
            color = Pagado
        }else{
            color = PagadoParcialmente
        }
    }
    return color
}

@Composable
fun OneLoan(itemData:Loan){
    Column(){
    Card(
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
            Column(modifier = Modifier.padding(5.dp)) {

                Text(itemData.date.date.toString()+ "/" + itemData.date.month.toString()+"/"+itemData.date.year.toString()+" "+itemData.date.hours.toString()+":"+itemData.date.minutes.toString(), fontSize=10.sp, modifier = Modifier.align(Alignment.End))
                Text(itemData.nameUser, fontWeight= FontWeight.Bold)
                Text("5 lechugas, 1 Tomate, Mas productos, Mas productos, Mas productos, Mas...")
            }
        }

    }
    }
}
@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun LoanScreen(loanViewModel: LoanViewModel, navController: NavController) {
    Column() {
        Actions(navController)
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)) {
            this.items(listItemData) {
                OneLoan(it)
            }
        }
        //LazyColumn(content = )
        //OneLoan()
    }

}