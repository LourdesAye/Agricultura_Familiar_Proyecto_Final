package com.example.agroagil.Buy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Buys
import com.example.agroagil.core.models.EventOperationBox
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BuyViewModel : ViewModel()  {
    var farm = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<Buy>> { continuation ->
                Firebase.database.getReference("buy/0").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Buys::class.java) as Buys
                    continuation.resume(value.buys)
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
                val realValue = suspendCancellableCoroutine<List<Buy>> { continuation ->
                    Firebase.database.getReference("buy/0").get().addOnSuccessListener { snapshot ->
                        val value = snapshot.getValue(Buys::class.java) as Buys
                        continuation.resume(value.buys)
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
    fun addBuy(buy: Buy){
        val currentBuys = mutableListOf<Buy>()

        farm.value?.let {
            currentBuys.clear()
            currentBuys.addAll(it)
            currentBuys.add(buy)
            Firebase.database.getReference("buy/0").setValue(Buys(currentBuys))
            var getKey = Firebase.database.getReference("events/0/boxs/events").push().key
            val updates = HashMap<String, Any>()
            updates["/$getKey"] = EventOperationBox(buy.price, buy.date, "Registro de la compra", "Buy",   (currentBuys.size-1).toString())
            Firebase.database.getReference("events/0/boxs/events").updateChildren(updates)
            setFarm()
        }
    }

    fun updateBuy(buy: Buy, indexLoan:Int){
        val currentBuy = mutableListOf<Buy>()

        farm.value?.let {
            currentBuy.clear()
            currentBuy.addAll(it)
            currentBuy[indexLoan] = buy
            Firebase.database.getReference("buy/0").setValue(Buys(currentBuy))
            setFarm()
        }

    }
}