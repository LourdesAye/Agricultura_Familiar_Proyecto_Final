package com.example.agroagil.Loan.ui
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.core.models.Item
import com.example.agroagil.core.models.Loan
import com.google.firebase.inappmessaging.model.Button
import java.util.Date
import kotlin.jvm.functions.FunctionN

val SinPagar = "#A93226"
val SinPagarClick = "#f4e5e4"
val PagadoParcialmente = "#D4AC0D"
val PagadoParcialmenteClick = "#faf5e1"
val Pagado = "#28B463"
val PagadoClick = "#d7f1e2"

var listItemData = mutableStateListOf<Loan>()
var listItemDataFilter = mutableStateListOf<Loan>()

var filters = mutableStateListOf<Function1<List<Loan>, List<Loan>>>()

fun filterPagado(loans:List<Loan>): List<Loan> {
    return loans.filter { it -> it.percentagePaid>=100 }
}

fun filterSinPagar(loans:List<Loan>): List<Loan> {
    return loans.filter { it -> it.percentagePaid==0 }
}
fun filterPagadoParcialmente(loans:List<Loan>): List<Loan> {
    return loans.filter { it -> 100>it.percentagePaid && it.percentagePaid > 0 }
}

fun resetFilter(){
    listItemDataFilter.clear()
    if (filters.size ==0){
        listItemDataFilter.addAll(listItemData)
    }
    for (i in 0 .. filters.size-1) {
        var filtroExecute = mutableListOf<List<Loan>>()
        filtroExecute.addAll(listOf(filters[i](listItemData)))
        listItemDataFilter.addAll(filtroExecute.flatten())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actions(navController: NavController){
    var expandedFilter by remember { mutableStateOf(false) }
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    }

    Column {
    Row(

        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(top = 5.dp, start = 0.dp, end = 0.dp)
            .fillMaxWidth()
    ) {
    Button(onClick = { expandedFilter = !expandedFilter},colors= if (expandedFilter) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(), modifier = Modifier.fillMaxWidth()) {
        Icon(
            ImageVector.vectorResource(R.drawable.filter),
            contentDescription = "Localized description",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Filtrar")
    }
    }
    Column(modifier=Modifier.padding(end = 10.dp, start = 10.dp, bottom = 5.dp)){
    AnimatedVisibility(visible = expandedFilter) {
        Column(modifier = Modifier.fillMaxWidth()){
            Card(modifier = Modifier.fillMaxWidth()) {
                Column (modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(30.dp)){
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
            Column(modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()) {

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filterStatus(){
    var clickPagado by remember {mutableStateOf(false)}
    var colorPagado by remember {mutableStateOf<Color>(Color(0))}
    var clickPagadoParcialmente by remember {mutableStateOf(false)}
    var colorPagadoParcialmente by remember {mutableStateOf<Color>(Color(0))}
    var clickSinPagar by remember {mutableStateOf(false)}
    var colorSinPagar by remember {mutableStateOf<Color>(Color(0))}
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier= Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 15.dp)){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val cardWidth =  with(LocalDensity.current) {
            screenWidth * 0.3f
        }
        if (clickPagado){
            colorPagado = Color(PagadoClick.toColorInt())
        }else{
            colorPagado=Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick={
                clickPagado = !clickPagado
                if(clickPagado){
                    filters.add(::filterPagado)
                }else{
                    filters.remove(::filterPagado)
                }
                resetFilter()
                    },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorPagado)
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(Pagado.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Pagado", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    if(clickPagado){
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterEnd)
                        )
                    }

                }
            }
        }
        if (clickPagadoParcialmente){
            colorPagadoParcialmente = Color(PagadoParcialmenteClick.toColorInt())
        }else{
            colorPagadoParcialmente=Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick={
                clickPagadoParcialmente = !clickPagadoParcialmente
                if(clickPagadoParcialmente){
                    filters.add(::filterPagadoParcialmente)
                }else{
                    filters.remove(::filterPagadoParcialmente)
                }
                resetFilter()
                    },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
        colors = CardDefaults.cardColors(colorPagadoParcialmente)
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(PagadoParcialmente.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Pagado parcialmente", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    if(clickPagadoParcialmente){
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
        if (clickSinPagar){
            colorSinPagar = Color(SinPagarClick.toColorInt())
        }else{
            colorSinPagar=Color(MaterialTheme.colorScheme.background.value)
        }
        Card(
            onClick = {
                clickSinPagar=!clickSinPagar
                if(clickSinPagar){
                    filters.add(::filterSinPagar)
                }else{
                    filters.remove(::filterSinPagar)
                }
                resetFilter()
                      },
            modifier = Modifier
                .width(cardWidth)
                .padding(2.dp)
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
            colors = CardDefaults.cardColors(colorSinPagar)
        ) {
            Row() {
                Column(
                    modifier = Modifier
                        .background(Color(SinPagar.toColorInt()))
                        .width(10.dp)
                        .fillMaxHeight()
                ) {

                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Sin pagar", textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center))
                    if(clickSinPagar){
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }



    }
}

@Composable
fun FloatingButton(
) {
    val yOffset = remember { mutableStateOf(0) }

    val offsetY = with(LocalDensity.current) { yOffset.value.dp.toPx() }

    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        Column {


           Button(
                onClick = {},
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null
                )
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
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }

    }else {
        resetFilter()
        Box(){
        Column() {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                item{
                    filterStatus()
                    Actions(navController)
                }
                this.items(listItemDataFilter) {
                    OneLoan(it, navController)
                }
            }

        }
            Button(onClick = {
                navController.navigate("loan/add")
            },modifier=Modifier.padding(end=20.dp,bottom=40.dp).align(Alignment.BottomEnd)) {
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
}