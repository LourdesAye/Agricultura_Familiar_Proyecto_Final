package com.example.agroagil.Menu.ui.featureMenu.menu.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import com.lourd.myapplication.featureMenu.menu.domain.ItemMenuPrincipal
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.imageResource

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.agroagil.R
import com.lourd.myapplication.featureMenu.NavigationEventMenu
import com.lourd.myapplication.featureMenu.menu.ui.MenuViewModel

//import androidx.navigation.NavHost

@Composable

fun setItemsDeMenu(): List<ItemMenuPrincipal>{
    return listOf(
        ItemMenuPrincipal("Mis Cultivos", ImageBitmap.imageResource(R.drawable.mis_cultivos)),
        ItemMenuPrincipal("Mis Tareas", ImageBitmap.imageResource(R.drawable.mis_tareas)),
        ItemMenuPrincipal("Mi Almacén", ImageBitmap.imageResource(R.drawable.almacen_deposito)),
        ItemMenuPrincipal("Mis Préstamos de Artículos",ImageBitmap.imageResource(R.drawable.mis_prestamos)),
        ItemMenuPrincipal("Mis Ventas",ImageBitmap.imageResource( R.drawable.ventas)),
        ItemMenuPrincipal("Mis Compras", ImageBitmap.imageResource(R.drawable.mis_compras)),
        ItemMenuPrincipal("Mi Resumen", ImageBitmap.imageResource(R.drawable.mi_resumen))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenedorDeOpciones(
    scope: CoroutineScope,
    drawerState: DrawerState,
    onNavigationEvent: (NavigationEventMenu) -> Unit,
) {
    // estado del scaffold
    var state=0
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inicio", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Acceso a Opciones del Menú"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigationEvent(NavigationEventMenu.ToNotificaciones)

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = " Notificaciones "
                        )
                    }
                    IconButton(onClick = { onNavigationEvent(NavigationEventMenu.ToConfigPerfil) }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil del Usuario"
                        )
                    }

                })
        },
        content = {
                padding -> if (state == 1) {
            Box(
                modifier = Modifier.padding(padding)
            )
        }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(
    scope: CoroutineScope,
    drawerState: DrawerState,
    viewModel: MenuViewModel,
    onNavigationEvent: (NavigationEventMenu) -> Unit // Evento de navegación único
){
        // Observa cambios en nombreGranja y nombreImagenGranja
    val nombreGranja: String by viewModel.nombreGranja.observeAsState("Mi Campo")
    val nombreImagenGranja:String by viewModel.nombreImagenGranja.observeAsState(initial ="farm3" )

    // Obtiene la clase R
    val drawableClass = R.drawable::class.java
    // Obtiene el campo del recurso drawable utilizando el nombre de la cadena
    val field = drawableClass.getDeclaredField(nombreImagenGranja)
    // Obtiene el valor del campo, que es la referencia R.drawable.farm5
    val resourceId = field.getInt(null)

    //items de menú sin Granja
    var items: List<ItemMenuPrincipal> = setItemsDeMenu()
    //creo el item de la granja
    var itemGranja = ItemMenuPrincipal(nombreGranja, ImageBitmap.imageResource(resourceId))
    //armo lista con item de la granja
    val listaConNuevoItem = mutableListOf(itemGranja)
    //concateno a la lista con item de la granja con la lista del resto del menú
    listaConNuevoItem.addAll(items)
    items =listaConNuevoItem

    //item seleccionado del menú
    val selectedItem = remember { mutableStateOf(items[1]) }

    //construcción del menú
    ModalNavigationDrawer(
        //estado del menú: cerrado inicialmente, viene desde el mainActivity
        drawerState = drawerState,
        // contenido del menú
        drawerContent = {
                ModalDrawerSheet(
                    content = {
                        //Spacer(Modifier.height(22.dp))
                        items.forEachIndexed { posicion, item ->
                            if (posicion == 0) {
                                NavigationDrawerItem(
                                    icon = {
                                        Column(
                                            verticalArrangement = Arrangement.Center, // Alinea el contenido en el centro vertical
                                            horizontalAlignment = Alignment.CenterHorizontally, // Alinea el contenido en el centro horizontal
                                            modifier = Modifier
                                                .size(200.dp)
                                                .clip(RoundedCornerShape(16.dp)) // Ajusta el radio de las esquinas
                                        ) {
                                            Image(
                                                painter = painterResource(id = resourceId),
                                                contentDescription = "Imagen del campo",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(200.dp).clickable {
                                                    scope.launch { drawerState.close() }
                                                    //esto funcionara cuando se le asocie el composable en el mainActivity
                                                    onNavigationEvent(NavigationEventMenu.ToConfigGranja)
                                                }

                                            )
                                            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre la imagen y el texto

                                        }


                                    },
                                    label = { TextButton(onClick = {
                                        scope.launch { drawerState.close() }
                                        //esto funcionara cuando se le asocie el composable en el mainActivity
                                        onNavigationEvent(NavigationEventMenu.ToConfigGranja)
                                    }) {
                                        Text(
                                            text = item.nombreItemMenu,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center // Alinea el texto al centro
                                        )
                                    }},
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        //esto funcionara cuando se le asocie el composable en el mainActivity
                                        onNavigationEvent(NavigationEventMenu.ToConfigGranja)
                                        },
                                    modifier = Modifier.fillMaxWidth().height(250.dp)
                                )
                            } else {
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            item.iconoItemMenu,
                                            modifier = Modifier.size(44.dp, 44.dp),
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(item.nombreItemMenu) },
                                    selected = item == selectedItem.value,
                                    onClick = {
                                            scope.launch { drawerState.close() }
                                            selectedItem.value = item
                                        onClick(posicion,item,onNavigationEvent)
                                    },
                                    modifier = Modifier.padding(10.dp)
                                )


                            }
                        }
                    }
                    , modifier =Modifier.verticalScroll(
                        state= rememberScrollState()
                    )
                )
            }
        ,
        content = {
            // lo que contiene al menú : los iconos que permiten que se abra
            // recibe scope y drawerState
            ContenedorDeOpciones(scope,drawerState,onNavigationEvent)

        }

    )
}

fun onClick(posicion: Int, item: ItemMenuPrincipal, onNavigationEvent: (NavigationEventMenu) -> Unit) {

    //pos 0 es el nombre de la granja y su iamgen
    //pos 1 "Mis Cultivos"
    if(posicion==1){onNavigationEvent(NavigationEventMenu.ToMisCultivos)}
    //pos 2 "Mis Tareas"
    if(posicion==2){onNavigationEvent(NavigationEventMenu.ToMisTareas)}
    //pos 3 "Mi Almacén"
    if(posicion==3){onNavigationEvent(NavigationEventMenu.ToMiAlmacen)}
    //pos 4 "Mis Préstamos de Artículos"
    if(posicion==4){onNavigationEvent(NavigationEventMenu.ToPrestamosArticulos)}
    //pos 5 "Mis Ventas"
    if(posicion==5){onNavigationEvent(NavigationEventMenu.ToMisVentas)}
    //pos 6 "Mis Compras"
    if(posicion==6){onNavigationEvent(NavigationEventMenu.ToMisCompras)}
    //pos 7 "Mi Resumen"
    if(posicion==7){onNavigationEvent(NavigationEventMenu.ToMiResumen)}

}
