package com.example.agroagil.core.models

import java.util.AbstractQueue

class Stocks (
    val stocks:List<Stock> = emptyList()
)
data class Conversion(
 val name: String = "Kg",
 var amount: Float = 10f //10 unidades son 1 Kg
)
 data class Stock (
     var id: String = "",
     val type: String = "Herramienta", //Cultivo
     val date: String = "01/01/2023 00:00",
     val product: Product = Product("",0,"KG",0.0),
     val amountMinAlert: Int = 0,
     val withAlert:Boolean = false,
     var conversion: List<Conversion> = emptyList()
 )
