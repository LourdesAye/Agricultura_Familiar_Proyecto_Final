package com.example.agroagil.core.data.firebase

import com.example.agroagil.core.models.FarmModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


private const val PARENT_FARM_PATH = "granja/"
private const val CHILD_FARM_LIST_PATH = "/members/"

class FarmFirebaseService {

    suspend fun getFarmForUser(userId: Int): FarmModel {
        try {
            return suspendCancellableCoroutine<FarmModel> { continuation ->
                Firebase.database.getReference("$PARENT_FARM_PATH$userId").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(FarmModel::class.java) as FarmModel
                    continuation.resume(value)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        } catch (e: Exception) {
            // Handle exception if needed
            println("Firebase: Error from getFarmForUser.")
            e.printStackTrace()
            return FarmModel()
        }
    }


}