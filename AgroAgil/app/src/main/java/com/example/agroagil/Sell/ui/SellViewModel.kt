package com.example.agroagil.Sell.ui

import Sell
import Sells
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.EventOperationBox
import com.example.agroagil.core.models.Loan
import com.example.agroagil.core.models.Loans
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SellViewModel : ViewModel()  {
    var farm = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<Sell>> { continuation ->
                Firebase.database.getReference("sell/0").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Sells::class.java) as Sells
                    continuation.resume(value.sells)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
            emit(realValue)
        } catch (e: Exception) {
            // Handle exception if needed
        }
    }
    fun setFarm(){
        farm = liveData(Dispatchers.IO) {
            emit(null)

            try {
                val realValue = suspendCancellableCoroutine<List<Sell>> { continuation ->
                    Firebase.database.getReference("sell/0").get().addOnSuccessListener { snapshot ->
                        val value = snapshot.getValue(Sells::class.java) as Sells
                        continuation.resume(value.sells)
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
                }
                emit(realValue)
            } catch (e: Exception) {
                // Handle exception if needed
            }
        }
    }
    fun addSell(sell: Sell){
        val currentSells = mutableListOf<Sell>()

        farm.value?.let {
            currentSells.clear()
            currentSells.addAll(it)
            currentSells.add(sell)
            Firebase.database.getReference("sell/0").setValue(Sells(currentSells))
            var getKey = Firebase.database.getReference("events/0/boxs/events").push().key
            val updates = HashMap<String, Any>()
            updates["/$getKey"] = EventOperationBox(date = SimpleDateFormat("yyyy/MM/dd HH:mm",
                Locale.getDefault())
                .format(
                    Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time), typeEvent= "Registro de la venta", operation = "Sell",   referenceID= (currentSells.size-1).toString()
                )
            Firebase.database.getReference("events/0/boxs/events").updateChildren(updates)
            if(sell.paid){
                var getKey = Firebase.database.getReference("events/0/boxs/events").push().key
                val updates = HashMap<String, Any>()
                updates["/$getKey"] = EventOperationBox(date = SimpleDateFormat("yyyy/MM/dd HH:mm",
                    Locale.getDefault())
                    .format(
                        Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time), typeEvent= "Pago de la venta", operation = "Sell",   referenceID= (currentSells.size-1).toString()
                )
                Firebase.database.getReference("events/0/boxs/events").updateChildren(updates)
            }
            setFarm()
        }
    }

    fun updateSell(sell: Sell, indexLoan:Int){
        val currentSell = mutableListOf<Sell>()

        farm.value?.let {
            currentSell.clear()
            currentSell.addAll(it)
            currentSell[indexLoan] = sell
            Firebase.database.getReference("sell/0").setValue(Sells(currentSell))
            var getKey = Firebase.database.getReference("events/0/boxs/events").push().key
            val updates = HashMap<String, Any>()
            updates["/$getKey"] = EventOperationBox(date = SimpleDateFormat("yyyy/MM/dd HH:mm",
                Locale.getDefault())
                .format(
                    Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time), typeEvent= "Pago de la venta", operation = "Sell",   referenceID= indexLoan.toString()
            )
            Firebase.database.getReference("events/0/boxs/events").updateChildren(updates)
            setFarm()
        }

    }
}