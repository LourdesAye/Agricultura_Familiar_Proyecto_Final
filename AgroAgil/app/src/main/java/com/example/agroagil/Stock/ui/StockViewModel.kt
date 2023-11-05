package com.example.agroagil.Stock.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
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
    var stockEnBaseDeDatos = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<Stock>> {
                    continuation ->
                Firebase.database.getReference("stockSummary/0/").get().addOnSuccessListener { snapshot ->
                    val genericType = object : GenericTypeIndicator<HashMap<String, Stock>>() {}
                    val value = snapshot.getValue(genericType)
                    value?.forEach{(key,value) -> value.id = key}
                    val result = value?.values?.toList() ?: emptyList()

                    continuation.resume(result)

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
        stockEnBaseDeDatos = liveData(Dispatchers.IO) {
            emit(null)

            try {
                val realValue = suspendCancellableCoroutine<List<Stock>> {
                        continuation ->
                    Firebase.database.getReference("stockSummary/0/").get().addOnSuccessListener { snapshot ->
                        val genericType = object : GenericTypeIndicator<HashMap<String, Stock>>() {}
                        val value = snapshot.getValue(genericType)
                        value?.forEach{(key,value) -> value.id = key}
                        val result = value?.values?.toList() ?: emptyList()
                        continuation.resume(result)

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
    fun addUpdateProduct(stock: Stock) {
        var getKey: String?
        if(stock.id ==""){
        getKey = Firebase.database.getReference("stockSummary/0/").push().key
        if (getKey != null) {
            stock.id = getKey
        }
        }else{
            getKey = stock.id
        }
        var updates = HashMap<String, Any>()
        updates["/$getKey"] = stock
        Firebase.database.getReference("stockSummary/0/").updateChildren(updates)
        setFarm()
    }

    fun addBuy(buy: Stock) {
        val currentBuys = mutableListOf<Stock>()
        stockEnBaseDeDatos.value?.let {
            currentBuys.clear()
            currentBuys.addAll(it)
            currentBuys.add(buy)
            Firebase.database.getReference("stockSummary/0").setValue(Stocks(currentBuys))
            setFarm()
        }
    }

    fun updateBuy(buy: Stock, indexLoan: Int) {
        val currentBuy = mutableListOf<Stock>()

        stockEnBaseDeDatos.value?.let {
            currentBuy.clear()
            currentBuy.addAll(it)
            currentBuy[indexLoan] = buy
            Firebase.database.getReference("stockSummary/0").setValue(Stocks(currentBuy))
            setFarm()
        }

    }
}