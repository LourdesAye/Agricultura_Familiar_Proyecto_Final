package com.example.agroagil.core.models

data class Product (
    val name: String = "",
    var amount: Float = 0f,
    var units: String = "",
    val price: Float = 0f
){
    constructor(name: String= "", amount: Int = 0, units: String= "", price: Float=0f):
            this(name, amount.toFloat(), units, price)

}