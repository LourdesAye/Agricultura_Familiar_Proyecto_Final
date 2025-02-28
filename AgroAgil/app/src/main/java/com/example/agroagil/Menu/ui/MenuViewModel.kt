package com.lourd.myapplication.featureMenu.menu.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.agroagil.Menu.ui.NavigationEventMenu
import com.example.agroagil.R
import com.lourd.myapplication.featureMenu.menu.domain.ItemMenuPrincipal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltViewModel // habilita la inyección de dependencias para el view model
class MenuViewModel @Inject constructor() : ViewModel() {


    private val _itemGranjaNombre =
        MutableLiveData<String>() //variable que solo se accede desde el view model
    val nombreGranja: LiveData<String> =
        _itemGranjaNombre // no private y sin guion bajo para que pueda llamarse pero no modificarse

    private val _itemGranjaImagen = MutableLiveData<String>()
    val nombreImagenGranja: LiveData<String> = _itemGranjaImagen

    val defaultOptionSelected: ItemMenuPrincipal = ItemMenuPrincipal(NavigationEventMenu.ToMisCultivos, "Mis Cultivos", R.drawable.mis_cultivos)
    private val _currentOptionSelected = MutableLiveData<ItemMenuPrincipal>(defaultOptionSelected)

    val currentOptionSelected: LiveData<ItemMenuPrincipal> = _currentOptionSelected


    // private val urlImagen : String = "R.drawable."

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("granja/0")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            //realizar operaciones de lectura en segundo plano
            // Configurar el listener para actualizar los datos desde Firebase
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Obtener el nombre de la granja desde Firebase
                    val nombreDataSnapshot = dataSnapshot.child("name")
                    // Obtener el nombre de la imagen desde Firebase
                    val imagenDataSnapshot = dataSnapshot.child("image")
                    // Actualizar LiveData en el hilo principal
                    viewModelScope.launch(Dispatchers.Main) {
                        _itemGranjaImagen.value = imagenDataSnapshot.value as String
                        _itemGranjaNombre.value = nombreDataSnapshot.value as String

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores si es necesario
                }
            }
            )

        }
    }

    fun onChangeOptionSelected(menuOption: ItemMenuPrincipal) {
        _currentOptionSelected.postValue(menuOption)

    }

    fun setItemsDeMenu(): List<ItemMenuPrincipal>{
        return listOf(
            defaultOptionSelected,
            ItemMenuPrincipal(NavigationEventMenu.ToMisTareas, "Mis Tareas", R.drawable.mis_tareas),
            ItemMenuPrincipal(NavigationEventMenu.ToMiAlmacen, "Mi Almacén", R.drawable.almacen_deposito),
            ItemMenuPrincipal(NavigationEventMenu.ToPrestamosArticulos,  "Mis Préstamos de Artículos",
                R.drawable.mis_prestamos),
            ItemMenuPrincipal(NavigationEventMenu.ToMisVentas, "Mis Ventas", R.drawable.ventas),
            ItemMenuPrincipal(NavigationEventMenu.ToMisCompras, "Mis Compras", R.drawable.mis_compras),
            ItemMenuPrincipal(NavigationEventMenu.ToMiResumen, "Mi Resumen", R.drawable.mi_resumen)
        )
    }

}
