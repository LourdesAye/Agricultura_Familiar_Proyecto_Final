package com.example.agroagil.Menu.ui.featurePerfil.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agroagil.R


@Preview(showSystemUi = true, showBackground = true)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VerDatosDelPerfil(param: (Any) -> Unit) {
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
                        onClick = {  }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
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
                Button(onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text(text = "Editar Perfil")
                }
            }
        }
    }
}

