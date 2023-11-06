package com.example.agroagil.core.data

import com.example.agroagil.core.data.firebase.FarmFirebaseService
import com.example.agroagil.core.models.FarmModel
import com.example.agroagil.core.models.Member

class FarmRepository {

    private val firebaseApi = FarmFirebaseService()

    suspend fun getFarmForUser(userId: Int): FarmModel {
        return firebaseApi.getFarmForUser(userId)
    }

    suspend fun getFarmMembersForUser(userId: Int): List<Member> {
        return firebaseApi.getFarmForUser(userId).members
    }
}