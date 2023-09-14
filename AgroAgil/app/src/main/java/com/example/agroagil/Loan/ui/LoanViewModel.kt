package com.example.agroagil.Loan.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.agroagil.core.models.Loan
import com.example.agroagil.core.models.Loans
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LoanViewModel : ViewModel()  {
    var farm = liveData(Dispatchers.IO) {
        emit(null)

        try {
            val realValue = suspendCancellableCoroutine<List<Loan>> { continuation ->
                Firebase.database.getReference("loan/0").get().addOnSuccessListener { snapshot ->
                    val value = snapshot.getValue(Loans::class.java) as Loans
                    continuation.resume(value.loans)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
            emit(realValue)
        } catch (e: Exception) {
            // Handle exception if needed
        }
    }
    fun setFarm(){
        farm =liveData(Dispatchers.IO) {
            emit(null)

            try {
                val realValue = suspendCancellableCoroutine<List<Loan>> { continuation ->
                    Firebase.database.getReference("loan/0").get().addOnSuccessListener { snapshot ->
                        val value = snapshot.getValue(Loans::class.java) as Loans
                        continuation.resume(value.loans)
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
    fun addLoan(loan:Loan){
        val currentLoans = mutableListOf<Loan>()

        farm.value?.let {
            currentLoans.clear()
            currentLoans.addAll(it)
        currentLoans.add(loan)
        Firebase.database.getReference("loan/0").setValue(Loans(currentLoans))
            setFarm()
        }
    }

    fun updateLoan(loan:Loan, indexLoan:Int){
        val currentLoans = mutableListOf<Loan>()

        farm.value?.let {
            currentLoans.clear()
            currentLoans.addAll(it)
            currentLoans[indexLoan] = loan
            Firebase.database.getReference("loan/0").setValue(Loans(currentLoans))
            setFarm()
        }

    }
}