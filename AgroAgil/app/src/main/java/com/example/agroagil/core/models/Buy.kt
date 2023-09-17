package com.example.agroagil.core.models

class Buys (
    val buys:List<Buy> = emptyList()
)

data class Buy(
    val nameUser:String = "",
    val items: List<Product> = emptyList(),
    var price: Double = 0.0,
    val date: String = "01/01/2023 00:00",
    var paid: Boolean = true
){
    constructor(nameUser:String = "", items: List<Product> = emptyList(),price: Int = 0, date: String = "01/01/2023 00:00", paid: Boolean = true):
            this(nameUser=nameUser, items=items,price=price.toDouble(), date=date, paid=paid) {}
}