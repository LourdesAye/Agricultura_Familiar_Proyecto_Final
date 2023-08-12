package com.example.agroagil.ui.Farm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.FarmModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FarmViewModel : ViewModel() {
    var farm = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<FarmModel> { continuation ->
                Firebase.database.getReference("granja/0").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(FarmModel::class.java) as FarmModel
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