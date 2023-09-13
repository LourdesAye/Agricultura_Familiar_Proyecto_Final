package com.example.agroagil.core.models

data class Loans(
    val loans:List<Loan> = emptyList()
)



data class Loan(
    val nameUser:String = "",
    val items: List<Product> = emptyList(),
    var paid:List<Product> = emptyList(),
    var percentagePaid: Int = 0,
    val date: String = "01/01/2023 00:00",
    val lend: Boolean = true
)