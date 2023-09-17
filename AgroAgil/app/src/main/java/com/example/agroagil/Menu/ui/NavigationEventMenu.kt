package com.lourd.myapplication.featureMenu

sealed class NavigationEventMenu{
    object ToConfigGranja : NavigationEventMenu()
    object ToMisCultivos : NavigationEventMenu()
    object ToMisTareas : NavigationEventMenu()
    object ToMiAlmacen : NavigationEventMenu()
    object ToPrestamosArticulos : NavigationEventMenu()
    object ToMisVentas: NavigationEventMenu()
    object ToMisCompras : NavigationEventMenu()
    object ToMiResumen : NavigationEventMenu()
    object ToNotificaciones : NavigationEventMenu()
    object ToConfigPerfil : NavigationEventMenu()

    object ToHome : NavigationEventMenu()
}
