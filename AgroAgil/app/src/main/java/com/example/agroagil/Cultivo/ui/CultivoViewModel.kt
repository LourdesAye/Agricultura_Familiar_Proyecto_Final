package com.example.agroagil.Cultivo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.FarmModel
import com.example.agroagil.core.models.Member
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CultivoViewModel : ViewModel() {
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
    fun updateMembers(members:List<Member>){
        Firebase.database.getReference("granja/0/members").setValue(members)
    }
    fun updateName(name:String){
        Firebase.database.getReference("granja/0/name").setValue(name)
    }
    fun updateImage(image:String){
        Firebase.database.getReference("granja/0/image").setValue(image)
    }
}