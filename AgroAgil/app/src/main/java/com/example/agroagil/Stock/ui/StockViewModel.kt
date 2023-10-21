package com.example.agroagil.Stock.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Buys
import com.example.agroagil.core.models.Stock
import com.example.agroagil.core.models.Stocks
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StockViewModel : ViewModel() {
    var farm = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<Stock>> { continuation ->
                Firebase.database.getReference("stockSummary/0/").get().addOnSuccessListener { snapshot ->
                    val genericType = object : GenericTypeIndicator<HashMap<String, Stock>>() {}
                    val value = snapshot.getValue(genericType)
                    val result = value?.values?.toList() ?: emptyList()
                    continuation.resume(result)
                    /*
                    val value = snapshot.getValue(HashMap<String, EventOperationBox>()::class.java) as HashMap<String, EventOperationBox>
                    var result = mutableListOf<EventOperationBox>()
                    result = value.values.toMutableList()
                    continuation.resume(result)*/
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
            emit(realValue)
        } catch (e: Exception) {
            // Handle exception if needed
        }
    }

    fun setFarm() {
        farm = liveData(Dispatchers.IO) {
            emit(null)
            try {
                val realValue = suspendCancellableCoroutine<List<Stock>> { continuation ->
                    Firebase.database.getReference("stockSummary/0").get().addOnSuccessListener { snapshot ->
                        val value = snapshot.getValue(Stocks::class.java) as Stocks
                        continuation.resume(value.stocks)
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

    fun addBuy(buy: Stock) {
        val currentBuys = mutableListOf<Stock>()
        farm.value?.let {
            currentBuys.clear()
            currentBuys.addAll(it)
            currentBuys.add(buy)
            Firebase.database.getReference("stockSummary/0").setValue(Stocks(currentBuys))
            setFarm()
        }
    }

    fun updateBuy(buy: Stock, indexLoan: Int) {
        val currentBuy = mutableListOf<Stock>()

        farm.value?.let {
            currentBuy.clear()
            currentBuy.addAll(it)
            currentBuy[indexLoan] = buy
            Firebase.database.getReference("stockSummary/0").setValue(Stocks(currentBuy))
            setFarm()
        }

    }
}