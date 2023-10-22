package com.example.agroagil.core.models

data class Conversion(
    val name: String = "Kg",
    val amount: Float = 10f //10 unidades son 1 Kg
)
data class Crop (
    val id: String = "0",
    val name: String = "Papa",
    val units: String = "Unidad",
    val durationDay: Int = 60,
    val price:Double = 100.0, //Valor de 100 por unidad
    val conversion: List<Conversion> = emptyList()
)

data class Plantation(
    val name: String = "Plantacion 1 de papa",
    val dateStart: String = "01/01/2023 00:00",
    val referenceId: String = "0",
    val status:String = "" //Sembrado, Cosechado
)