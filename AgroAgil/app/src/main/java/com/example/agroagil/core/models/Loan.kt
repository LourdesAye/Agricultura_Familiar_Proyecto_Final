package com.example.agroagil.core.models

import java.util.Date

data class Loan(
    val nameUser:String = "",
    val items: List<Item> = emptyList(),
    val paid:List<Item> = emptyList(),
    val percentagePaid: Double = 0.0,
    val date: Date = Date()
)

data class Item(
    val name: String = "",
    val amount: Int = 0,
    val units: String = ""
)