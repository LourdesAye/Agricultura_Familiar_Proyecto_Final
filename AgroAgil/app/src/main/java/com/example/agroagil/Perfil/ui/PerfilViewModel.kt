package com.example.agroagil.Perfil.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@HiltViewModel
class PerfilViewModel @Inject constructor() : ViewModel()  {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val userReference: DatabaseReference? = auth.uid?.let { database.reference.child("usuarios").child(it)}

    val userLiveData = MutableLiveData<DatosUsuario>()
    val errorMessageLiveData = MutableLiveData<String>()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(DatosUsuario::class.java)
                        user?.let {
                            userLiveData.value = it
                        }
                    }
                } catch (e: Exception) {
                    val errorMessage = "Error al cargar el perfil del usuario: ${e.message}"
                    errorMessageLiveData.value = errorMessage
                }

            }

            override fun onCancelled(error: DatabaseError) {
                val errorMessage = "Error al cargar el perfil del usuario: ${error.message}"
                errorMessageLiveData.value = errorMessage
            }
        })
    }
}
