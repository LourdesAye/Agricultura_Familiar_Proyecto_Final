package com.example.agroagil.Perfil.ui

sealed class NavigationEventPerfil{
    object ToEditPerfil: NavigationEventPerfil()
    object ToPantallaPrincipal: NavigationEventPerfil()
    object ToDatosPerfil : NavigationEventPerfil()
}
