package com.example.agroagil.core.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

data class Loans(
    val loans:List<Loan> = emptyList()
)



data class Loan(
    val nameUser:String = "",
    val items: List<Item> = emptyList(),
    val paid:List<Item> = emptyList(),
    val percentagePaid: Double = 0.0,
    val date: String = "01/01/2023 00:00"
){
    @RequiresApi(Build.VERSION_CODES.O)
    fun datetoDate(): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("dd/mm/yyyy HH:MM")
        return LocalDate.parse(date, formatter)
    }
}

data class Item(
    val name: String = "",
    val amount: Int = 0,
    val units: String = ""
)