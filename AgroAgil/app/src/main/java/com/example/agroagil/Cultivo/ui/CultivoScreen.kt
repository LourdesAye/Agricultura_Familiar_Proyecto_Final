package com.example.agroagil.Cultivo.ui

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.R
import com.example.agroagil.core.models.Crop
import com.example.agroagil.core.models.Member
import com.example.agroagil.core.models.Plantation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

var crops = mutableStateListOf<Crop>()
var plantations = mutableStateListOf<Plantation>()

@Composable
fun GetCultivosSemillas(navController: NavController) {
    Button(
        onClick = { navController.navigate("cultivo/type/info")},
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text = "Mis Semillas / Tipos de Cultivos", fontSize = 17.sp)
    }
    Row(

        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
    ) {
        Text(text = plantations.size.toString() + " Plantaciones", fontSize = 28.sp)

    }
}

fun getNameType(referenceID: String): String {
    val crop = crops.filter{it.id == referenceID}.first()
    return crop.name
}
fun getImage(referenceID: String, dateStart:String): Int {
    val crop = crops.filter{it.id == referenceID}.first()
    val date_format = SimpleDateFormat("dd/MM/yyyy")
    var date_class = date_format.parse(dateStart.split(" ")[0])
    var currentDateTime = Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time
    val diferenciaEnMilisegundos: Long = currentDateTime.getTime() - date_class.getTime()
    val diferenciaEnDias = diferenciaEnMilisegundos / (1000 * 60 * 60 * 24)
    val pdiferenciaEnDias = diferenciaEnDias.toFloat()/crop.durationDay.toFloat()
    if(pdiferenciaEnDias<0.25){
        return R.drawable.crop1
    }else{
        if(pdiferenciaEnDias<0.5){
            return R.drawable.crop2
        }else{
            if(pdiferenciaEnDias<0.75){
                return R.drawable.crop3
            }else{
                return R.drawable.crop4
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetPlantations(navController: NavController) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    for (i in 0..Math.ceil((plantations.size / 2).toDouble()).toInt()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            for (item in 0..Math.min(2, plantations.size - (i * 2)) - 1) {
                Card(
                    modifier = Modifier
                        .width(screenWidth*0.45f).height(240.dp)
                        .padding(start = 5.dp, end = 5.dp, top = 10.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    onClick = {
                        navController.navigate("cultivo/"+plantations[(i * 2) + item].id+"/info")
                    }

                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = getImage(plantations[(i * 2) + item].referenceId, plantations[(i * 2) + item].dateStart)),
                            contentDescription = stringResource(id = R.string.app_name),
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .fillMaxWidth().height(240.dp*0.60f)
                                .background(Color("#628665".toColorInt()))
                        )
                        plantations[(i * 2) + item].name?.let {
                            Text(
                                it,
                                modifier = Modifier
                                    .background(Color("#F8FAFB".toColorInt()))
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                                color = Color.Black,
                                fontSize = 17.sp
                            )
                        }
                        plantations[(i * 2) + item]?.let {
                            Text(
                                getNameType(it.referenceId),
                                modifier = Modifier
                                    .background(Color("#F8FAFB".toColorInt()))
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .padding(start = 10.dp, end = 10.dp),
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun Cultivo(cultivoViewModel: CultivoViewModel,  navController: NavController) {
    val crop = cultivoViewModel.crop.observeAsState().value
    val plantation = cultivoViewModel.plantation.observeAsState().value
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    if (crop == null || plantation == null) {
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
        plantations.clear()
        plantations.addAll(plantation)
        crops.clear()
        crops.addAll(crop)
        Box(modifier = Modifier.defaultMinSize(minHeight = screenHeight)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .padding(top = 30.dp, bottom = 30.dp)
                        .fillMaxSize()
                )

                {
                    GetCultivosSemillas(navController)
                    GetPlantations(navController)
                }
            }
            Button(onClick = { navController.navigate("cultivo/add")},
                modifier= Modifier
                    .padding(end = 20.dp, bottom = 40.dp)
                    .align(Alignment.BottomEnd)) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Crear Cultivo")
            }

        }
    }
}
