package com.example.agroagil.Login.ui

sealed class NavigationInicio {
    object PantallaLogin : NavigationInicio()
    object PantallaRegistro: NavigationInicio()
    object PantallaMenu: NavigationInicio()
    object PantallaRegistroGoogle : NavigationInicio()
}
