package com.example.agroagil.Perfil.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agroagil.R

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VerDatosDelPerfil(onNavigationEvent: (NavigationEventPerfil) -> Unit) {
    var state:Int = 0
    //debe llegarme el nombre de la base de datos
    var nombreUsuario by remember { mutableStateOf("John") }
    //debe llegar apellido de base de datos
    var apellidoUsuario by remember { mutableStateOf("Doe") }
    // debe llegar correo electronico de la base de datos
    var correoElectronico by remember { mutableStateOf("john@example.com") }
    //debe llegar el Rol desde base de datos
    var rol by remember { mutableStateOf("Developer") }
    //debe llegar el password desde base de datos
    var password by remember { mutableStateOf("********") }

    var isEditing by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Mi Perfil") },
                navigationIcon = {
                    IconButton(
                        onClick = {  onNavigationEvent(NavigationEventPerfil.ToPantallaPrincipal) }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        //ahora entiendo porque si o si pasa un padding, es por el topBar , para que no se superponga con el contenido
            espaciado -> if (state == 1) {
        Box(
            modifier = Modifier.padding(espaciado)
        )
    }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(espaciado)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center // Centra horizontalmente
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.farmer6),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                        //.clip(CircleShape) forma de circulo se corta mucho la imagen
                    )

                }
            }


            item {
                Button(
                    onClick = {
                        onNavigationEvent(NavigationEventPerfil.ToEditPerfil)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text(text = "Editar Perfil",
                        style = TextStyle(fontSize = 18.sp))
                }
            }
        }
    }
}

@Composable
fun EditarDatosPerfil(onNavigationEvent: (NavigationEventPerfil) -> Unit) {

    var mostrarDialogoConfirmacion by rememberSaveable { mutableStateOf(false) }
    var mostrarDialogoRechazo by rememberSaveable { mutableStateOf(false) }
    var mostrarMensajeDeCambios by rememberSaveable { mutableStateOf(false) }
    var mostrarMensajeDeCancelacionDeCambios by rememberSaveable { mutableStateOf(false) }

    LazyColumn() {

        item {

            Row(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween


            ) {
                Button(
                    modifier = Modifier.weight(1f)
                        .padding(end = 8.dp) // Agrega espacio entre los botones
                        .fillMaxWidth() // Hace que el botón ocupe el ancho máximo disponible
                    ,
                    onClick = {
                        mostrarDialogoRechazo = true
                    })
                { Text(text = " Cancelar",
                    style = TextStyle(fontSize = 18.sp)) }


                Button(
                    modifier = Modifier.weight(1f)
                        .padding(end = 8.dp) // Agrega espacio entre los botones
                        .fillMaxWidth() // Hace que el botón ocupe el ancho máximo disponible
                    ,
                    onClick = {
                        mostrarDialogoConfirmacion = true
                    })
                { Text(text = " Guardar",
                    style = TextStyle(fontSize = 18.sp)) }

                // Diálogo de rechazo de cambios
                if (mostrarDialogoRechazo) {
                    AlertDialog(
                        onDismissRequest = { //mostrarDialogoRechazo = false
                        },
                        title = { Text(text="No guardar cambios",style = TextStyle(fontSize = 18.sp)) },
                        text = { Text(text="¿Está seguro de NO GUARDAR los CAMBIOS?",style = TextStyle(fontSize = 18.sp)) },
                        confirmButton = {
                            // confirma que NO VA A GUARDAR LOS CAMBIOS
                            Button(
                                modifier = Modifier.padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(), // Hace que el botón ocupe el ancho máximo disponible
                                onClick = {
                                    //cierra dialogo actual
                                    mostrarDialogoRechazo = false
                                    //abre dialogo de que no se guardaron los cambios para seguridad del usuario
                                    mostrarMensajeDeCancelacionDeCambios = true
                                }
                            ) {
                                Text(text = "NO GUARDAR CAMBIOS",
                                    style = TextStyle(fontSize = 18.sp))
                            }
                        },
                        dismissButton = {
                            //indica que desea guardar o ahcer algo con los cambios
                            Button(
                                modifier = Modifier.padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    // Cierra el diálogo
                                    mostrarDialogoRechazo = false
                                    //vuelve a pantalla de cambios
                                    //checkear esto si los cambios permanecen en pantalla luego de este click o hay que hacer algo más
                                }
                            ) {
                                Text("VOLVER A EDITAR PERFIL",
                                    style = TextStyle(fontSize = 18.sp))
                            }


                        })
                }
                //Dialogo de que efectivamente no se cambio
                if (mostrarMensajeDeCancelacionDeCambios) {
                    AlertDialog(
                        onDismissRequest = { //mostrarMensajeDeCancelacionDeCambios = false
                        },
                        title = { Text("Cambios ") },
                        text = { Text(" Sus Cambios no han sido guardados") },
                        confirmButton = {
                            Button(
                                modifier = Modifier.padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    mostrarMensajeDeCancelacionDeCambios = false
                                    //deberia quedarse en la pantalla de datos de perfil
                                    onNavigationEvent(NavigationEventPerfil.ToDatosPerfil)
                                }
                            ) {
                                Text("Cerrar",
                                    style = TextStyle(fontSize = 18.sp))
                            }
                        })
                }
                //Dialogo de raelizar cambios
                if (mostrarDialogoConfirmacion) {
                    AlertDialog(
                        onDismissRequest = {
                        },
                        title = { Text(text="Realizar Cambios",style = TextStyle(fontSize = 18.sp))},
                        text = { Text(text= "¿Está seguro GUARDAR los CAMBIOS?",style = TextStyle(fontSize = 18.sp)) },
                        confirmButton = {
                            // confirma que VA A GUARDAR LOS CAMBIOS
                            Button(
                                modifier = Modifier.padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    //cierra dialogo actual
                                    mostrarDialogoConfirmacion = false
                                    //abre dialogo de que se guardaron los cambios para seguridad del usuario
                                    mostrarMensajeDeCambios= true
                                }
                            ) {
                                Text("GUARDAR CAMBIOS",
                                    style = TextStyle(fontSize = 18.sp))
                            }
                        },
                        dismissButton = {
                            //indica que desea no quiere guaradar todavia los cambios
                            Button(
                                modifier = Modifier.padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    // Cierra el diálogo
                                    mostrarDialogoRechazo = false
                                    //vuelve a pantalla de cambios
                                    //checkear esto si los cambios permanecen en pantalla luego de este click o hay que hacer algo más
                                }
                            ) {
                                Text("VOLVER A EDITAR PERFIL",
                                    style = TextStyle(fontSize = 18.sp))
                            }


                        })
                }
                //Dialogo de que acepto cambios
                if (mostrarMensajeDeCambios) {
                    AlertDialog(
                        onDismissRequest = {
                        },
                        title = { Text(text= "Cambios Realizados ",
                            style = TextStyle(fontSize = 18.sp)) },
                        text = { Text(text = " sus CAMBIOS han sido GUARDADOS con éxito",
                            style = TextStyle(fontSize = 18.sp))},
                        confirmButton = {
                            Button(
                                modifier = Modifier.padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    mostrarMensajeDeCambios= false
                                    // y navega a la pantalla de perfil con los datos actualizados
                                    onNavigationEvent(NavigationEventPerfil.ToDatosPerfil)
                                }
                            ) {
                                Text("Cerrar",
                                    style = TextStyle(fontSize = 18.sp))
                            }
                        },
                        dismissButton = {
                        }
                    )


                }
            }



        }
    }
}

