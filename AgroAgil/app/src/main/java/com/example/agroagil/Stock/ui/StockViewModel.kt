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
        //valor que se pasa inicialmente
        emit(null)
        /*suspendCancellableCoroutine para iniciar una suspension del codigo.
         Esto permite que el codigo se ejecute de forma asincronica y se suspenda hasta que se complete.
         También se especifica que el tipo de resultado que se espera cuando se reanuda
        el codigo suspendido es una lista de objetos Stock */

        try {
            val realValue = suspendCancellableCoroutine<List<Stock>> {
                //se utiliza para controlar la suspensión
                    continuation ->
                /* obtener datos de Firebase, si es con éxito, se llama al oyente onSuccess, y dentro de él
                 se obtiene el valor de los datos y se almacena en value */
                Firebase.database.getReference("stockSummary/0/").get().addOnSuccessListener { snapshot ->
                    //En esta línea, se crea una instancia de GenericTypeIndicator
                    // para indicar el tipo de datos que se espera obtener de Firebase.
                    // En este caso, se espera obtener un mapa (HashMap) donde las claves
                    // son cadenas (String) y los valores son objetos de tipo Stock.
                    // Esto se utiliza para ayudar a Firebase a deserializar los datos
                    // correctamente.
                    val genericType = object : GenericTypeIndicator<HashMap<String, Stock>>() {}
                    val value = snapshot.getValue(genericType)
                    /*
                    se accede a los valores (los objetos de tipo Stock) dentro del mapa value
                     y se convierten en una lista utilizando .toList().
                     Si value es nulo, se utiliza una lista vacía en su lugar, evitando un valor nulo.
                    * */
                    val result = value?.values?.toList() ?: emptyList()
                    continuation.resume(result)
                    /*
                    es la lista de objetos de tipo Stock, se pasa como resultado y
                    se almacena en una variable o se utiliza de alguna manera fuera
                    de este bloque de código.
                    */

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