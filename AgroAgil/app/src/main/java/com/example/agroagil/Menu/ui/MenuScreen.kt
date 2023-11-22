package com.example.agroagil.Menu.ui.featureMenu.menu.ui

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import com.lourd.myapplication.featureMenu.menu.domain.ItemMenuPrincipal
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.imageResource

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import com.example.agroagil.R
import com.example.agroagil.Menu.ui.NavigationEventMenu
import com.lourd.myapplication.featureMenu.menu.ui.MenuViewModel
import kotlinx.coroutines.delay
import java.time.format.TextStyle

//import androidx.navigation.NavHost


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenedorDeOpciones(
    scope: CoroutineScope,
    drawerState: DrawerState,
    onNavigationEvent: (NavigationEventMenu) -> Unit,
    title: MutableState<String>,
    isMenu: Boolean,
    navController: NavController
) {
    TopAppBar(
        title = { Text(title.value, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if(isMenu){
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Acceso a Opciones del Menú"
                )
            }}else{
                IconButton(onClick = {scope.launch { navController.popBackStack()}}) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (title.value!="Inicio") {
                IconButton(onClick = {
                    onNavigationEvent(NavigationEventMenu.ToHome)

                }) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = " Home "
                    )
                }
            }
            IconButton(onClick = { onNavigationEvent(NavigationEventMenu.ToNotificaciones)

            }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = " Notificaciones "
                )
            }
            IconButton(onClick = { onNavigationEvent(NavigationEventMenu.ToConfigPerfil) }) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Perfil del Usuario"
                )
            }

        })
}
fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(
    scope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: MenuViewModel,
    title: MutableState<String>,
    onNavigationEvent: (NavigationEventMenu) -> Unit, // Evento de navegación único,
    isMenu: Boolean,
    navController: NavController,
    contentFrame:  @Composable () ->Unit
){
    var statusNetwork by remember { mutableStateOf(true) }
    var lastConnection by remember { mutableStateOf(SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)) }
        // Observa cambios en nombreGranja y nombreImagenGranja
    val nombreGranja: String by viewModel.nombreGranja.observeAsState("Mi Campo")
    val nombreImagenGranja:String by viewModel.nombreImagenGranja.observeAsState(initial ="farm3" )
    val selectedItem = viewModel.currentOptionSelected.observeAsState().value
    val connectivityManager = getSystemService(
        LocalContext.current,
        ConnectivityManager::class.java
    ) as ConnectivityManager?

    // Obtiene la clase R
    val drawableClass = R.drawable::class.java
    // Obtiene el campo del recurso drawable utilizando el nombre de la cadena
    val field = drawableClass.getDeclaredField(nombreImagenGranja)
    // Obtiene el valor del campo, que es la referencia R.drawable.farm5
    val resourceId = field.getInt(null)

    //items de menú sin Granja
    var items: List<ItemMenuPrincipal> = viewModel.setItemsDeMenu()
    //creo el item de la granja
    var itemGranja = ItemMenuPrincipal(NavigationEventMenu.ToConfigGranja, nombreGranja, resourceId)
    //armo lista con item de la granja
    val listaConNuevoItem = mutableListOf(itemGranja)
    //concateno a la lista con item de la granja con la lista del resto del menú
    listaConNuevoItem.addAll(items)
    items =listaConNuevoItem

    //item seleccionado del menú
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    //construcción del menú
    ModalNavigationDrawer(
        //estado del menú: cerrado inicialmente, viene desde el mainActivity
        drawerState = drawerState,
        // contenido del menú
        drawerContent = {
            //Column() {
            //    Text(text = "Hola")
                ModalDrawerSheet(
                    content = {
                        //Spacer(Modifier.height(22.dp))
                        items.forEachIndexed { posicion, item ->
                            if (item.itemMenuName == "Mis Cultivos"){
                                Text("Campo", modifier= Modifier.padding(start=20.dp, top=10.dp))
                            }
                            if  (item.itemMenuName == "Mis Ventas"){
                                Text("Dinero", modifier= Modifier.padding(start=20.dp, top=10.dp))
                            }
                            if (item.itemMenuName == "Mi Almacén"){
                                Text("Artículos", modifier= Modifier.padding(start=20.dp, top=10.dp))
                            }

                            if (posicion == 0) {
                                NavigationDrawerItem(
                                    icon = {
                                        Image(
                                            painter = painterResource(id = resourceId),
                                            contentDescription = "Imagen del campo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape)
                                        )
                                    },
                                    label = {
                                        TextButton(onClick = {
                                            scope.launch { drawerState.close() }
                                            //esto funcionara cuando se le asocie el composable en el mainActivity
                                            onNavigationEvent(NavigationEventMenu.ToConfigGranja)
                                        }) {
                                            Text(
                                                text = item.itemMenuName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center // Alinea el texto al centro
                                            )
                                        }
                                    },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        //esto funcionara cuando se le asocie el composable en el mainActivity
                                        onNavigationEvent(NavigationEventMenu.ToConfigGranja)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                )
                            } else {
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            ImageBitmap.imageResource(item.drawableIconId),
                                            modifier = Modifier.size(44.dp, 44.dp),
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(item.itemMenuName) },
                                    selected = item == selectedItem,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        viewModel.onChangeOptionSelected(item)
                                        onNavigationEvent(item.typeOfNavigationEventMenu)
                                    },
                                    modifier = Modifier.padding(10.dp)
                                )


                            }
                            if (item.itemMenuName in listOf("Mis Tareas","Mis Préstamos de Artículos","Mi Resumen")){
                                Divider(modifier = Modifier.padding(start=20.dp, end=20.dp))
                            }

                        }
                    }, modifier = Modifier
                        .verticalScroll(
                            state = rememberScrollState()
                        )
                        .defaultMinSize(minHeight = screenHeight)
                )
            //}

            }
        ,
        content = {
            // lo que contiene al menú : los iconos que permiten que se abra
            // recibe scope y drawerState
            Column() {
                ContenedorDeOpciones(scope, drawerState, onNavigationEvent, title,
                    isMenu,
                    navController)
                LaunchedEffect(true) {
                    val updateInterval = 5000L // 5 segundos
                    while (true) {
                        if (connectivityManager != null){
                            statusNetwork = isInternetAvailable(connectivityManager)
                            if (statusNetwork){
                                lastConnection = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                    .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)
                            }
                        }
                        delay(updateInterval)
                    }
                }

                AnimatedVisibility(visible = !statusNetwork) {
                    Card(Modifier.fillMaxWidth()) {
                        Row(modifier=Modifier.padding(start=20.dp, top=15.dp)){
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.wifi_off),
                                contentDescription = "Localized description",
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Sin acceso a internet", style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ))
                        }
                        Text(text = "Ultima actualizacion: "+ lastConnection, modifier=Modifier.padding(start=20.dp,bottom=15.dp))
                    }

                }

                contentFrame()
            }
        }

    )
}


