package com.example.agroagil.Cultivo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Buys
import com.example.agroagil.core.models.Crop
import com.example.agroagil.core.models.Plantation
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CultivoViewModel : ViewModel() {
    var currentPlantation: LiveData<Plantation?>? = null
    var currentCrop: LiveData<Crop?>? = null
    var crop = liveData(Dispatchers.IO) {
        emit(null)
        try {
            val realValue = suspendCancellableCoroutine<List<Crop>> { continuation ->
                Firebase.database.getReference("crop/0/").get().addOnSuccessListener { snapshot ->
                    val genericType = object : GenericTypeIndicator<HashMap<String, Crop>>() {}
                    val value = snapshot.getValue(genericType)
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
    var plantation = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<Plantation>> { continuation ->
                Firebase.database.getReference("plantation/0/").get().addOnSuccessListener { snapshot ->
                    val genericType = object : GenericTypeIndicator<HashMap<String, Plantation>>() {}
                    val value = snapshot.getValue(genericType)
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
    fun getCrop(cropId: String) {
        currentCrop = liveData(Dispatchers.IO) {
            try {
                val realValue = suspendCancellableCoroutine<Crop> { continuation ->
                    Firebase.database.getReference("crop/0/"+cropId).get().addOnSuccessListener { snapshot ->
                        val value = snapshot.getValue(Crop::class.java) as Crop
                        continuation.resume(value)
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
    fun setCrop(){
        crop = liveData(Dispatchers.IO) {
            emit(null)
            try {
                val realValue = suspendCancellableCoroutine<List<Crop>> { continuation ->
                    Firebase.database.getReference("crop/0/").get().addOnSuccessListener { snapshot ->
                        val genericType = object : GenericTypeIndicator<HashMap<String, Crop>>() {}
                        val value = snapshot.getValue(genericType)
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
    fun getPlantation(plantationId: String){
        currentPlantation = liveData(Dispatchers.IO) {
            try {
                val realValue = suspendCancellableCoroutine<Plantation> { continuation ->
                    Firebase.database.getReference("plantation/0/"+plantationId).get()
                        .addOnSuccessListener { snapshot ->
                            val value = snapshot.getValue(Plantation::class.java) as Plantation
                            continuation.resume(value)
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
    fun setPlantation(){
        plantation = liveData(Dispatchers.IO) {
            emit(null)

            try {
                val realValue = suspendCancellableCoroutine<List<Plantation>> { continuation ->
                    Firebase.database.getReference("plantation/0/").get()
                        .addOnSuccessListener { snapshot ->
                            val genericType =
                                object : GenericTypeIndicator<HashMap<String, Plantation>>() {}
                            val value = snapshot.getValue(genericType)
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

    fun createCrop(crop: Crop): String? {
        var getKey = Firebase.database.getReference("crop/0/").push().key
        if (getKey != null) {
            crop.id = getKey
        }
        var updates = HashMap<String, Any>()
        updates["/$getKey"] = crop
        Firebase.database.getReference("crop/0/").updateChildren(updates)
        setCrop()
        return getKey
    }
    fun createPlantation(plantation: Plantation){
        var getKey = Firebase.database.getReference("plantation/0/").push().key
        if (getKey != null) {
            plantation.id = getKey
        }
        var updates = HashMap<String, Any>()
        updates["/$getKey"] = plantation
        Firebase.database.getReference("plantation/0/").updateChildren(updates)
        setPlantation()
    }
    fun init(){
        var getKey = Firebase.database.getReference("crop/0/").push().key
        var updates = HashMap<String, Any>()
        updates["/$getKey"] = Crop()
        Firebase.database.getReference("crop/0/").updateChildren(updates)

        getKey = Firebase.database.getReference("plantation/0/").push().key
        updates = HashMap<String, Any>()
        updates["/$getKey"] = Plantation()
        Firebase.database.getReference("plantation/0/").updateChildren(updates)


    }
}