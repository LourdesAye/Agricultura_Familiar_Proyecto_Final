package com.example.agroagil.Summary

import Sell
import Sells
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Buys
import com.example.agroagil.core.models.EventOperation
import com.example.agroagil.core.models.EventOperationBox
import com.example.agroagil.core.models.EventOperationStock
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Stock
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SummaryViewModel: ViewModel() {
    var sells = liveData(Dispatchers.IO) {
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

    var buys = liveData(Dispatchers.IO) {
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
    var stocks = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<Stock>> { continuation ->
                Firebase.database.getReference("stock/0/").get().addOnSuccessListener { snapshot ->
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
    var events = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<EventOperationBox>> { continuation ->
                Firebase.database.getReference("events/0/boxs/events").get().addOnSuccessListener { snapshot ->
                    val genericType = object : GenericTypeIndicator<HashMap<String, EventOperationBox>>() {}
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
    var eventsStock = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<EventOperationStock>> { continuation ->
                Firebase.database.getReference("events/0/stock").get().addOnSuccessListener { snapshot ->
                    val genericType = object : GenericTypeIndicator<HashMap<String, EventOperationStock>>() {}
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
    fun getAllEvents(event:EventOperation): List<EventOperationBox> {
        var index: Int
        if(event.type == "Sell"){
            index = sells.value!!.indexOf(event.sell!!)
            return events.value!!.filter{
                it.operation.equals("Sell")
                        &&
                        it.referenceID.equals(index.toString())
            }
        }else{
            index = buys.value!!.indexOf(event.buy!!)
            return events.value!!.filter{
                it.operation.equals("Buy")
                        &&
                        it.referenceID.equals(index.toString())
            }
        }
    }

    fun getAllEventsStock(event:Stock): List<EventOperationStock> {

        return eventsStock.value!!.filter{
                        it.referenceID.equals(event.id)
            }

    }

    fun getSummaryDataStock(dateStart: String, dateEnd: String): List<Pair<String, Double>> {
        var result = HashMap<String, Double>()
        for (i in stocks.value!!){
            if (!(i.type in result.keys)){
                result[i.type] = 0.0
            }
            result[i.type] = result[i.type]!! + (i.product.price * i.product.amount)
        }
        var results = mutableListOf<Pair<String, Double>>()
        for (i in result){
            results.add(i.key to i.value)
        }
        return results
    }

    fun getSummaryData(dateStart: String, dateEnd: String): List<Pair<String, Float>> {
        var sellPaid = sells.value?.filter{it.paid}
        var buyPaid = buys.value?.filter{it.paid}
        var totalPriceSell = 0f
        var totalPriceBuy = 0f
        if (sellPaid != null) {
            for (i in sellPaid){
                totalPriceSell += i.price.toFloat()
            }
        }
        if (buyPaid != null) {
            for (i in buyPaid){
                totalPriceBuy += i.price.toFloat()
            }
        }
        return listOf(
            "Ingresos" to totalPriceSell,
            "Egresos" to totalPriceBuy,
        )
    }

    fun init(){
        var getKey = Firebase.database.getReference("events/0/stock").push().key
        val updates = HashMap<String, Any>()
        updates["/$getKey"] = EventOperationStock()
        Firebase.database.getReference("events/0/stock").updateChildren(updates)


    }
}