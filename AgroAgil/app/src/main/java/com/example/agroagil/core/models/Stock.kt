package com.example.agroagil.core.models

import java.util.AbstractQueue

class Stocks (
    val stocks:List<Stock> = emptyList()
)
 data class Stock (
     val id: String = "",
     val type: String = "Herramienta",
     val date: String = "01/01/2023 00:00",
     val product: Product = Product(),
     val items: List<Product> = emptyList(),
     val nameUser:String = "",
     val withAlert:Boolean = false,
     val recolectedQuantity: Number = 0,
 )
 {
//     constructor(
//         id: String = "",
//         type: String = "Herramienta",
//         date: String = "01/01/2023 00:00",
////         product: Product = Product(),
//         items: List<Product> = emptyList()
////         ,
////         nameUser: String = ""
//     )
//             :
//             this(id = id, type = type, date = date, items = items) {
//     }
 }
