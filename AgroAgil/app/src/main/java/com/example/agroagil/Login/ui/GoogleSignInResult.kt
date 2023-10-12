package com.example.agroagil.Login.ui

import com.google.firebase.auth.FirebaseUser

sealed class GoogleSignInResult{
    data class Success(val user: FirebaseUser) : GoogleSignInResult()
    data class Error(val exception: Exception) : GoogleSignInResult()
}
