package com.example.agroagil.core.models


data class Crop (
    var id: String = "0",
    val name: String = "Papa",
    var units: String = "Unidad",
    var durationDay: Int = 60,
    var price:Double = 100.0, //Valor de 100 por unidad
){
    constructor(id:String, name: String,units:String, durationDay: Int = 0, price: Int = 0, conversion: List<Conversion>  =  emptyList()):
            this(id, name,units, durationDay, price.toDouble()) {}
}

data class Plantation(
    var id: String="",
    val name: String = "Plantacion 1 de papa",
    val dateStart: String = "01/01/2023 00:00",
    val referenceId: String = "0",
    var status:String = "CREADO" //Sembrado, Cosechado, Creado
)