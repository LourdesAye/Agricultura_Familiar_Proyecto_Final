package com.example.agroagil.core.models

import Sell

data class EventOperation(
    val type: String = "Sell",
    val sell: Sell?,
    val buy: Buy?,
){
    fun getDate(): String {
        if (this.type == "Sell"){
            return this.sell!!.date
        }
        else{
            return this.buy!!.date
        }
    }

    fun getUser(): String {
        if (this.type == "Sell"){
            return this.sell!!.nameUser
        }
        else{
            return this.buy!!.nameUser
        }
    }

    fun getItems(): List<Product> {
        if (this.type == "Sell"){
            return this.sell!!.items
        }
        else{
            return this.buy!!.items
        }
    }
}
data class EventOperationBox (
    val date: String = "01/01/2023 00:00",
    var nameUser:String = "Usuario 1", //Usuario que ejecuto la accion
    val typeEvent: String = "",
    val operation:String = "Sell",
    val referenceID: String = ""
){
}