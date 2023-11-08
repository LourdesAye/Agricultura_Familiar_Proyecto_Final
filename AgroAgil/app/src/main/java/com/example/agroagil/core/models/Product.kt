package com.example.agroagil.core.models

data class Product (
    val name: String = "",
    var amount: Float = 0f,
    var units: String = "",
    val price: Double = 0.0
){
    constructor(name: String= "", amount: Int = 0, units: String= "", price: Double=0.0):
            this(name, amount.toFloat(), units, price)

}