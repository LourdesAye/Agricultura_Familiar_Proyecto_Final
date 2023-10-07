package com.example.agroagil.Login.ui

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.agroagil.VariablesFuncionesGlobales
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel // habilita la inyección de dependencias para el view model
class LoginViewModel @Inject constructor() : ViewModel() {

    // Propiedad para almacenar el mensaje de error
    val mensajeError = mutableStateOf<String?>(null)

    // Función para mostrar el mensaje de error
    fun showError(message: String) {
        mensajeError.value = message
    }

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

    fun iniciarSesion(
        auth: FirebaseAuth,
        onNavigate: (NavigationInicio) -> Unit,
        context: Context
    ) {

        val isConnected = VariablesFuncionesGlobales.isConnected(context)
        if (!isConnected) {
            // Mostrar error si no hay conexión a Internet
            showError("El celular no tiene acceso a Internet")
            return
        }
        // Mostrar el cuadro de diálogo de carga
        showLoadingDialog()

        // Intentar iniciar sesión con Firebase Authentication
        auth.signInWithEmailAndPassword(
            email.value,
            password.value
        ).addOnSuccessListener {
            // Inicio de sesión exitoso
            hideLoadingDialog()
            // Navegar a la pantalla principal
            onNavigate(NavigationInicio.PantallaMenu)
        }.addOnFailureListener { e ->
            // Fallo en el inicio de sesión
            hideLoadingDialog()
            showError(e.message ?: "Error en el inicio de sesión")
        }

    }

    val email = mutableStateOf("")
    val password = mutableStateOf("")


}

