package com.example.agroagil.core.models

 data class Stock (
     val id: String = "",
     val type: String = "Herramienta",
     val date: String = "01/01/2023 00:00",
     val product: Product = Product()
 )
