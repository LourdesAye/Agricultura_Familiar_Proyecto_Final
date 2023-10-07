package com.example.agroagil.Login.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class LoginGoogleViewModel @Inject constructor() : ViewModel() {

    val nombre = mutableStateOf("")
    val apellido = mutableStateOf("")
    val rol = mutableStateOf("")

    // Propiedad para controlar si el diálogo de carga está visible
    val isLoadingDialogVisible = mutableStateOf(false)

    // Función para mostrar el diálogo de carga
    fun showLoadingDialog() {
        isLoadingDialogVisible.value = true
    }

    // Función para ocultar el diálogo de carga
    fun hideLoadingDialog() {
        isLoadingDialogVisible.value = false
    }


    private val auth: FirebaseAuth = Firebase.auth
    fun signInWithGoogleCredentials(credential: AuthCredential, registrateConGoogle: () -> Unit, andaAlMenu: () -> Unit) =
        viewModelScope.launch {
            // Mostrar el cuadro de diálogo de carga
            showLoadingDialog()
            try {
                // se utiliza en Firebase Authentication para autenticar a un usuario, usa credential
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            // Para obtener el usuario autenticado actual
                            val currentUser: FirebaseUser? = auth.currentUser
                            // Verificar si el usuario está autenticado
                            if (currentUser != null) {
                                // El usuario está autenticado
                                val userId = currentUser.uid // Obtener el ID del usuario autenticado
                                val userEmail = currentUser.email // Obtener el correo electrónico del usuario autenticado

                                // Verificar si el usuario ya tiene información en Firebase Realtime Database
                                val userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(currentUser.uid)

                                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            //si esta cargado usuario en base de datos realtime database va a menu directamente
                                            hideLoadingDialog() //cierra dialogo de espera
                                            andaAlMenu() //navega a la página donde esta el menú
                                        }
                                        else{
                                            // El usuario no tiene información en la base de datos, hay que mostrar pantalla para que los cargue
                                            hideLoadingDialog() // cierra dialogo de espera
                                            registrateConGoogle() // pasa a pantalla para llenar datos

                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        //se tiene que poner si o si esta función, caso contrario falla
                                        // Manejar errores si es necesario
                                    }


                                }  )
                            }

                        }
                    }
            }
            catch (ex: Exception) {
                //esto es solo por las dudas de que haya un error, hasta ahora lo probé y no había ninguno
                Log.d("Autenticacion", "El error fue : ${ex.localizedMessage}")
            }
        }



}

sealed class GoogleSignInResult {
    data class Success(val user: FirebaseUser) : GoogleSignInResult()
    data class Error(val exception: Exception) : GoogleSignInResult()
}

