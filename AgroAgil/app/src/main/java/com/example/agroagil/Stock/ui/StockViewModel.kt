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
    fun addUpdateProduct(stockNew: Stock): String {
        var getKey: String?
        var stock = stockNew
        if(stock.id ==""){
            var result = stockEnBaseDeDatos.value!!.find {it.product.name == stock.product.name}
            if(result != null){
                getKey = result.id
                result.product.amount = result.product.amount+stock.product.amount
                stock = result
            }else{
            getKey = Firebase.database.getReference("stockSummary/0/").push().key
            if (getKey != null) {
                stock.id = getKey
            }}
        }else{
            getKey = stock.id
        }
        var updates = HashMap<String, Any>()
        updates["/$getKey"] = stock
        Firebase.database.getReference("stockSummary/0/").updateChildren(updates)
        setFarm()
        return stock.id
    }
}