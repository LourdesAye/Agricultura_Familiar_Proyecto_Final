package com.lourd.myapplication.featurePerfil.domain

data class PerfilUsuario(
    var nombreUsuario : String,
    var apellidoUsuario : String,
    var correoElectronico : String,
    var rolUsuario: String,
    var contraseña : String
)
