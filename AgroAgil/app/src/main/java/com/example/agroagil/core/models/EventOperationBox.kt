package com.example.agroagil.core.models

data class EventOperationBox (
    val amount: Double = 0.0,
    val date: String = "01/01/2023 00:00",
    val typeEvent: String = "",
    val operation:String = "Sell",
    val referenceID: String = ""
){
    constructor(date: String,typeEvent: String,operation: String,reference: String,amount: Int = 0):
            this(date=date, typeEvent=typeEvent,operation=operation,referenceID=reference, amount=amount.toDouble()) {}
}