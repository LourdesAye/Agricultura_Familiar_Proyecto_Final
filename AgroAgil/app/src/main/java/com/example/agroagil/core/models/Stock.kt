package com.example.agroagil.core.models

import java.util.AbstractQueue

class Stocks (
    val stocks:List<Stock> = emptyList()
)
 data class Stock (
     var id: String = "",
     val type: String = "Herramienta", //Cultivo
     val date: String = "01/01/2023 00:00",
     val product: Product = Product(),
     val amountMinAlert: Int = 0,
     val withAlert:Boolean = false,
 )
