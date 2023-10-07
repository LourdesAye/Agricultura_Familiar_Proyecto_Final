package com.example.agroagil

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.*

object VariablesFuncionesGlobales {

    // esto es para ver si hubo olvido de alguna navegación usando la sealed class y no pasar el navController (buena práctica)
    var navegacionDefinida = true

    //VALIDA SI CELULAR TIENE ACCESO A INTERNET
    fun isConnected(context: Context): Boolean {
        //obtiene información sobre la conectividad de la red del dispositivo.
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return run {
            //se ejecuta el código y devuelve true o false
            //se obteniene la red activa del celular, si es null no hay internet
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            //capacidades de la red : si hay trasnporte
            val cap = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            when {
                //hay transporte de datos por wifi, hay conexion a internet
                cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                //hay trasporte de datos por datos móviles, hay conexion a internet
                cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
    }

}