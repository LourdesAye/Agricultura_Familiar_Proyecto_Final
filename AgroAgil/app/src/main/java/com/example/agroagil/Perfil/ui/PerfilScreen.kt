package com.example.agroagil.Perfil.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agroagil.R

//ok
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VerDatosDelPerfil(
    viewModelDatosPerfil: PerfilViewModel,
    onNavigationEvent: (NavigationEventPerfil) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Mi Perfil") },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigationEvent(NavigationEventPerfil.ToPantallaPrincipal) }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
            espaciado ->
        //ahora entiendo porque si o si pasa un padding, es por el topBar , para que no se superponga con el contenido
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(espaciado)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
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
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))
            }


            item {

                Spacer(modifier = Modifier.height(16.dp))

                val errorMessage = viewModelDatosPerfil.errorMessageLiveData.observeAsState().value
                val user = viewModelDatosPerfil.userLiveData.observeAsState().value

                if (!errorMessage.isNullOrBlank()) {
                    Text(text = errorMessage!!, color = Color.Red)
                } else if (user != null) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Nombre", modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = user.nombre,
                            onValueChange = { /* No es necesario cambiar el valor ya que es de solo lectura */ },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor =   if (isSystemInDarkTheme()) {Color.White} else {Color.Black}, //color cuando estoy encima de la cajita
                                unfocusedBorderColor = if (isSystemInDarkTheme()) {Color.White} else {Color.Black})

                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Apellido", modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = user.apellido,
                            onValueChange = { /* No es necesario cambiar el valor ya que es de solo lectura */ },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors =TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor =   if (isSystemInDarkTheme()) {Color.White} else {Color.Black}, //color cuando estoy encima de la cajita
                                unfocusedBorderColor = if (isSystemInDarkTheme()) {Color.White} else {Color.Black})
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Rol", modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = user.rol,
                            onValueChange = { /* No es necesario cambiar el valor ya que es de solo lectura */ },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor =   if (isSystemInDarkTheme()) {Color.White} else {Color.Black}, //color cuando estoy encima de la cajita
                                unfocusedBorderColor = if (isSystemInDarkTheme()) {Color.White} else {Color.Black})
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Correo Electrónico", modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = user.correoElectronico,
                            onValueChange = { /* No es necesario cambiar el valor ya que es de solo lectura */ },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors =TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor =   if (isSystemInDarkTheme()) {Color.White} else {Color.Black}, //color cuando estoy encima de la cajita
                                unfocusedBorderColor = if (isSystemInDarkTheme()) {Color.White} else {Color.Black})
                        )
                    }

                } else {
                    Text(text = "Ocurrió un problema, inténtelo más tarde", color=Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    Text(
                        text = "Editar Perfil",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

//pendiente
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarDatosPerfil(onNavigationEvent: (NavigationEventPerfil) -> Unit) {

    var mostrarDialogoConfirmacion by rememberSaveable { mutableStateOf(false) }
    var mostrarDialogoRechazo by rememberSaveable { mutableStateOf(false) }
    var mostrarMensajeDeCambios by rememberSaveable { mutableStateOf(false) }
    var mostrarMensajeDeCancelacionDeCambios by rememberSaveable { mutableStateOf(false) }
    var mostrarConfirmaciónHaciaDatosPerfil by rememberSaveable {mutableStateOf(false)}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Editando Mi Perfil") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            mostrarConfirmaciónHaciaDatosPerfil =true
                            //onNavigationEvent(NavigationEventPerfil.ToPantallaPrincipal)
                    }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
    LazyColumn(modifier=Modifier.padding(it)) {

        item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth() // Hace que el botón ocupe el ancho máximo disponible
                    ,
                    onClick = {
                        mostrarDialogoRechazo = true
                    })
                {
                    Text(
                        text = " Cancelar",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }


                Button(
                    modifier = Modifier
                        .padding(end = 8.dp) // Agrega espacio entre los botones
                        .fillMaxWidth() // Hace que el botón ocupe el ancho máximo disponible
                    ,
                    onClick = {
                        mostrarDialogoConfirmacion = true
                    })
                {
                    Text(
                        text = " Guardar",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }

                // Diálogo de rechazo de cambios
                if (mostrarDialogoRechazo) {
                    AlertDialog(
                        onDismissRequest = { //mostrarDialogoRechazo = false
                        },
                        title = {
                            Text(
                                text = "No guardar cambios",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        text = {
                            Text(
                                text = "¿Está seguro de NO GUARDAR los CAMBIOS?",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        confirmButton = {
                            // confirma que NO VA A GUARDAR LOS CAMBIOS
                            Button(
                                modifier = Modifier
                                    .padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(), // Hace que el botón ocupe el ancho máximo disponible
                                onClick = {
                                    //cierra dialogo actual
                                    mostrarDialogoRechazo = false
                                    //abre dialogo de que no se guardaron los cambios para seguridad del usuario
                                    mostrarMensajeDeCancelacionDeCambios = true
                                }
                            ) {
                                Text(
                                    text = "NO GUARDAR CAMBIOS",
                                    style = TextStyle(fontSize = 18.sp)
                                )
                            }
                        },
                        dismissButton = {
                            //indica que desea guardar o ahcer algo con los cambios
                            Button(
                                modifier = Modifier
                                    .padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    // Cierra el diálogo
                                    mostrarDialogoRechazo = false
                                    //vuelve a pantalla de cambios
                                    //checkear esto si los cambios permanecen en pantalla luego de este click o hay que hacer algo más
                                }
                            ) {
                                Text(
                                    "VOLVER A EDITAR PERFIL",
                                    style = TextStyle(fontSize = 18.sp)
                                )
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
                                modifier = Modifier
                                    .padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    mostrarMensajeDeCancelacionDeCambios = false
                                    //deberia quedarse en la pantalla de datos de perfil
                                    onNavigationEvent(NavigationEventPerfil.ToDatosPerfil)
                                }
                            ) {
                                Text(
                                    "Cerrar",
                                    style = TextStyle(fontSize = 18.sp)
                                )
                            }
                        })
                }
                //Dialogo de raelizar cambios
                if (mostrarDialogoConfirmacion) {
                    AlertDialog(
                        onDismissRequest = {
                        },
                        title = {
                            Text(
                                text = "Realizar Cambios",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        text = {
                            Text(
                                text = "¿Está seguro GUARDAR los CAMBIOS?",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        confirmButton = {
                            // confirma que VA A GUARDAR LOS CAMBIOS
                            Button(
                                modifier = Modifier
                                    .padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    //cierra dialogo actual
                                    mostrarDialogoConfirmacion = false
                                    //abre dialogo de que se guardaron los cambios para seguridad del usuario
                                    mostrarMensajeDeCambios = true
                                }
                            ) {
                                Text(
                                    "GUARDAR CAMBIOS",
                                    style = TextStyle(fontSize = 18.sp)
                                )
                            }
                        },
                        dismissButton = {
                            //indica que desea no quiere guaradar todavia los cambios
                            Button(
                                modifier = Modifier
                                    .padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    // Cierra el diálogo
                                    mostrarDialogoRechazo = false
                                    //vuelve a pantalla de cambios
                                    //checkear esto si los cambios permanecen en pantalla luego de este click o hay que hacer algo más
                                }
                            ) {
                                Text(
                                    "VOLVER A EDITAR PERFIL",
                                    style = TextStyle(fontSize = 18.sp)
                                )
                            }


                        })
                }
                //Dialogo de que acepto cambios
                if (mostrarMensajeDeCambios) {
                    AlertDialog(
                        onDismissRequest = {
                        },
                        title = {
                            Text(
                                text = "Cambios Realizados ",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        text = {
                            Text(
                                text = " sus CAMBIOS han sido GUARDADOS con éxito",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        confirmButton = {
                            Button(
                                modifier = Modifier
                                    .padding(end = 8.dp) // Agrega espacio entre los botones
                                    .fillMaxWidth(),
                                onClick = {
                                    mostrarMensajeDeCambios = false
                                    // y navega a la pantalla de perfil con los datos actualizados
                                    onNavigationEvent(NavigationEventPerfil.ToDatosPerfil)
                                }
                            ) {
                                Text(
                                    "Cerrar",
                                    style = TextStyle(fontSize = 18.sp)
                                )
                            }
                        },
                        dismissButton = {
                        }
                    )


                }
            }


        }

        if( mostrarConfirmaciónHaciaDatosPerfil){
            AlertDialog(
                modifier= Modifier.fillMaxSize(),
                onDismissRequest = {},
                title = {
                    Text(
                        text = "Volver a Mi Perfil",
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
                text = {
                    Text(
                        text = "¿Esta seguro que desea volver a 'Mi Perfil'? Si ha realizado cambios en sus datos, esos cambios no serán guardados",
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
                confirmButton = {
                    Button(
                        modifier = Modifier
                            .padding(end = 8.dp) // Agrega espacio entre los botones
                            .fillMaxWidth(),
                        onClick = {
                            mostrarMensajeDeCambios = false
                            // y navega a la pantalla de perfil con los datos actualizados
                            onNavigationEvent(NavigationEventPerfil.ToDatosPerfil)
                        }
                    ) {
                        Text(
                            "Cerrar",
                            style = TextStyle(fontSize = 18.sp)
                        )
                    }
                },
                dismissButton = {
                }
            )

        }
    }


}