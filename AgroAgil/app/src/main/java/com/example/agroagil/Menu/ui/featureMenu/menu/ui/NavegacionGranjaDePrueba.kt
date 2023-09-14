package com.lourd.myapplication.featureMenu.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun GranjaDePrueba() {
    System.out.println("aqui entramos a granja de prueba")
    //aqui estoy probando la navegación
    Box(modifier=Modifier.fillMaxSize().background(Color.Black)){
        Text(
            modifier= Modifier.align(Alignment.Center),
            text="esto es solo para probar la navegación, aquí debe ir la pantalla de modificación de datos de la granja")

    }

}