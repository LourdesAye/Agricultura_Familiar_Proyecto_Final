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
import com.example.agroagil.R
import com.example.agroagil.core.models.Crop
import com.example.agroagil.core.models.Member
import com.example.agroagil.core.models.Plantation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

val openDialogImageFarm = mutableStateOf(false)
val openDialogConfirmDelete = mutableStateOf(false)
val openDialogMemberDetails = mutableStateOf(false)
val openDialogMember = mutableStateOf(false)
val openDialogHome = mutableStateOf(false)
val currentMember = mutableStateOf(Member())
var farmViewModelCurrent: CultivoViewModel? = null

var crops = mutableStateListOf<Crop>()
var plantations = mutableStateListOf<Plantation>()
val nameFarm = mutableStateOf("Mi granja")
val profileImage = mutableStateOf(R.drawable.ic_launcher_background)
val profileImageTemp = mutableStateOf(R.drawable.ic_launcher_background)
val profileImages = mutableStateListOf(
    R.drawable.farm1,
    R.drawable.farm2,
    R.drawable.farm3,
    R.drawable.farm4,
    R.drawable.farm5,
    R.drawable.farm6
)

@SuppressLint("UnrememberedMutableState")
@Composable
fun GetImageFarm() {
    var currentImage = mutableStateOf(profileImage.value)
    var currentSelected = mutableStateOf(0)
    var currentColor = ButtonDefaults.textButtonColors()
    if (openDialogImageFarm.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onDismissRequest = {
                openDialogImageFarm.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileImageTemp.value = currentImage.value
                        openDialogImageFarm.value = false
                    }
                ) {

                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogImageFarm.value = false
                    }
                ) {
                    Text("No, Cancelar")
                }
            },

            text = {
                val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
                dialogWindowProvider.window.setGravity(Gravity.BOTTOM)
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    for (i in 0..Math.ceil((profileImages.size / 3).toDouble()).toInt()) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            for (item in 0..Math.min(3, profileImages.size - (i * 3)) - 1) {
                                if (currentSelected.value == (i * 3) + item + 1) {
                                    currentColor = ButtonDefaults.filledTonalButtonColors()
                                } else {
                                    currentColor = ButtonDefaults.textButtonColors()
                                }
                                TextButton(onClick = {
                                    currentImage.value = profileImages[(i * 3) + item]
                                    currentSelected.value = (i * 3) + item + 1
                                }, colors = currentColor) {


                                    Image(
                                        painter = painterResource(id = profileImages[(i * 3) + item]),
                                        contentDescription = stringResource(id = R.string.app_name),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(65.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        )
    }
}

/*
@SuppressLint("UnrememberedMutableState")
@Composable
fun GetDialogConfirmDelete() {
    if (openDialogConfirmDelete.value) {
        AlertDialog(
            onDismissRequest = {
                openDialogConfirmDelete.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        members.remove(currentMember.value)
                        farmViewModelCurrent?.updateMembers(members)
                        openDialogConfirmDelete.value = false
                    }
                ) {

                    Text("Si")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogConfirmDelete.value = false
                    }
                ) {
                    Text("No, Cancelar")
                }
            },
            title = {
                Text("Eliminar")
            },

            text = { Text("¿Desea eliminar cultivo?") }
        )
    }
}*/
/*
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetDialogMemberDetails() {
    val text_nombre = mutableStateOf(currentMember.value.name)
    val error_nombre = mutableStateOf(false)
    val text_correo = mutableStateOf(currentMember.value.correo)
    val error_correo = mutableStateOf(false)
    var expanded_role = mutableStateOf(false)
    var selected_role = mutableStateOf(currentMember.value.role)
    val error_role = mutableStateOf(false)
    if (openDialogMemberDetails.value) {
        AlertDialog(
            onDismissRequest = {
                openDialogMemberDetails.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (text_nombre.value == "") {
                            error_nombre.value = true
                        }
                        if (text_correo.value == "") {
                            error_correo.value = true
                        }
                        if (selected_role.value == "") {
                            error_role.value = true
                        }
                        if ((text_nombre.value != "") && (text_correo.value != "") && (selected_role.value != "")) {
                            members[members.indexOf(currentMember.value)] = Member(
                                text_nombre.value,
                                selected_role.value,
                                text_correo.value,
                                currentMember.value.image
                            )
                            farmViewModelCurrent?.updateMembers(members)
                            openDialogMemberDetails.value = false

                        }

                    }
                ) {

                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogMemberDetails.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            },
            title = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text("Detalles de cultivo", modifier = Modifier.align(Alignment.CenterStart))
                    TextButton(
                        onClick = {
                            openDialogConfirmDelete.value = true
                            openDialogMemberDetails.value = false
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(30.dp),
                            tint = Color("#C70707".toColorInt())
                        )
                    }
                }
            },

            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = text_nombre.value,
                        onValueChange = {
                            text_nombre.value = it
                            error_nombre.value = false
                        },
                        label = { Text("Nombre") },
                        modifier = Modifier.padding(bottom = 16.dp),
                        isError = error_nombre.value
                    )
                    OutlinedTextField(
                        value = text_correo.value,
                        onValueChange = {
                            text_correo.value = it
                            error_correo.value = false
                        },
                        label = { Text("Correo") },
                        modifier = Modifier.padding(bottom = 16.dp),
                        isError = error_correo.value

                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded_role.value,
                        onExpandedChange = { expanded_role.value = !expanded_role.value },
                        modifier = Modifier.padding(bottom = 16.dp),
                    ) {
                        TextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = selected_role.value,
                            onValueChange = {},
                            label = { Text("Rol") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded_role.value) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            isError = error_role.value
                        )
                        ExposedDropdownMenu(
                            expanded = expanded_role.value,
                            onDismissRequest = { expanded_role.value = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Administrador") },
                                onClick = {
                                    selected_role.value = "Administrador"
                                    expanded_role.value = false
                                    error_role.value = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                            DropdownMenuItem(
                                text = { Text("Trabajador") },
                                onClick = {
                                    selected_role.value = "Trabajador"
                                    expanded_role.value = false
                                    error_role.value = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }

                }
            }
        )
    }
}*/
/*
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetDialogEditHome() {
    val context = LocalContext.current
    var text_name = mutableStateOf(nameFarm.value)
    var error_name = mutableStateOf(false)
    if (openDialogHome.value) {
        AlertDialog(
            onDismissRequest = {
                openDialogHome.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (text_name.value == "") {
                            error_name.value = true
                        } else {
                            nameFarm.value = text_name.value
                            Firebase.database.getReference("title").setValue(text_name.value)
                            openDialogHome.value = false
                            error_name.value = false
                            profileImage.value = profileImageTemp.value
                            farmViewModelCurrent?.updateName(nameFarm.value)
                            val nameImage =
                                context.resources.getResourceEntryName(profileImageTemp.value)
                            farmViewModelCurrent?.updateImage(nameImage)
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogHome.value = false
                        text_name.value = ""
                        profileImageTemp.value = profileImage.value
                    }
                ) {
                    Text("Cancelar")
                }
            },

            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box() {
                        Image(
                            painter = painterResource(id = profileImageTemp.value),
                            contentDescription = stringResource(id = R.string.app_name),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                        FilledIconButton(
                            onClick = {
                                openDialogImageFarm.value = true
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.camera),
                                contentDescription = "Localized description",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = text_name.value,
                        onValueChange = {
                            text_name.value = it
                            error_name.value = false
                        },
                        label = { Text("Nombre") },
                        modifier = Modifier.padding(top = 16.dp),
                        isError = error_name.value
                    )
                }
            }
        )
    }

}*/
/*
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetDialogEditMember() {
    val text_nombre = mutableStateOf("")
    val error_nombre = mutableStateOf(false)
    val text_correo = mutableStateOf("")
    val error_correo = mutableStateOf(false)
    var expanded_role = mutableStateOf(false)
    var selected_role = mutableStateOf("")
    val error_role = mutableStateOf(false)

    if (openDialogMember.value) {
        AlertDialog(
            onDismissRequest = {
                openDialogMember.value = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (text_nombre.value == "") {
                            error_nombre.value = true
                        }
                        if (text_correo.value == "") {
                            error_correo.value = true
                        }
                        if (selected_role.value == "") {
                            error_role.value = true
                        }
                        if ((text_nombre.value != "") && (text_correo.value != "") && (selected_role.value != "")) {
                            members.add(
                                Member(
                                    text_nombre.value,
                                    selected_role.value,
                                    text_correo.value,
                                    "farmer2"
                                )
                            )
                            farmViewModelCurrent?.updateMembers(members)
                            openDialogMember.value = false
                            text_nombre.value = ""
                            text_correo.value = ""
                            selected_role.value = ""

                        }


                    }
                ) {
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Enviar invitacion")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogMember.value = false
                        text_nombre.value = ""
                        text_correo.value = ""
                    }
                ) {
                    Text("Cancelar")
                }
            },
            title = { Text("Agregar Cultivo") },

            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = text_nombre.value,
                        onValueChange = {
                            text_nombre.value = it
                            error_nombre.value = false
                        },
                        label = { Text("Nombre cultivo") },
                        modifier = Modifier.padding(bottom = 16.dp),
                        isError = error_nombre.value
                    )
                    OutlinedTextField(
                        value = text_correo.value,
                        onValueChange = {
                            text_correo.value = it
                            error_correo.value = false
                        },
                        label = { Text("tipo cultivo") },
                        modifier = Modifier.padding(bottom = 16.dp),
                        isError = error_correo.value

                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded_role.value,
                        onExpandedChange = { expanded_role.value = !expanded_role.value },
                        modifier = Modifier.padding(bottom = 16.dp),
                    ) {
                        TextField(
                            // The `menuAnchor` modifier must be passed to the text field for correctness.
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = selected_role.value,
                            onValueChange = {},
                            label = { Text("Rol") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded_role.value) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            isError = error_role.value
                        )
                        ExposedDropdownMenu(
                            expanded = expanded_role.value,
                            onDismissRequest = { expanded_role.value = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Administrador") },
                                onClick = {
                                    selected_role.value = "Administrador"
                                    expanded_role.value = false
                                    error_role.value = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                            DropdownMenuItem(
                                text = { Text("Trabajador") },
                                onClick = {
                                    selected_role.value = "Trabajador"
                                    expanded_role.value = false
                                    error_role.value = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }

                }

            })
    }
}*/

@Composable
fun GetCultivosSemillas() {
    Button(
        onClick = { },
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
fun GetPlantations() {
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
                        .size(width = 200.dp, height = 240.dp)
                        .padding(start = 5.dp, end = 5.dp, top = 10.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    onClick = {
                        //openDialogMemberDetails.value = true
                        //currentMember.value = members[(i * 2) + item]
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
                                .fillMaxWidth()
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
fun Cultivo(cultivoViewModel: CultivoViewModel) {
    //cultivoViewModel.init()
    val crop = cultivoViewModel.crop.observeAsState().value
    val plantation = cultivoViewModel.plantation.observeAsState().value
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    //farmViewModelCurrent = cultivoViewModel
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
                    GetCultivosSemillas()
                    GetPlantations()
                }
            }
            Button(onClick = { openDialogMember.value = true },
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
        /*
        members.addAll(farm.members)
        nameFarm.value = farm.name
        val context = LocalContext.current
        val drawableId = remember(farm.image) {
            context.resources.getIdentifier(
                farm.image,
                "drawable",
                context.packageName
            )
        }
        profileImage.value = drawableId
        profileImageTemp.value = drawableId
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .padding(top = 50.dp, bottom = 30.dp)
                    .fillMaxSize()
            )

            {
                GetCultivosSemillas()
                GetMembers()
                GetDialogEditMember()
                GetDialogMemberDetails()
                GetDialogEditHome()
//                GetDialogConfirmDelete()
                GetImageFarm()
            }
        }*/
}
