package com.lourd.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//esta clase es para configurar la inyeccion de dependencias
//lo pide como requisito dagger hilt
@HiltAndroidApp
class ConfigInyeccionDependenciaApp : Application() {
}