import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agroagil.core.models.Loan
import com.example.agroagil.core.models.Loans
import com.example.agroagil.core.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.firebase.ktx.Firebase

class DashboardViewModel : ViewModel() {
    private val firebaseDatabase = Firebase.database.reference.child("loans")
    private val TAG = "DashboardViewModel"

    private val _loans = MutableLiveData<List<Loan>>()
    val loans: LiveData<List<Loan>> get() = _loans

        init {
            fetchTopLoans()
        }

    private fun fetchTopLoans() {
        Firebase.database.getReference("loan/0").get().addOnSuccessListener { snapshot ->
            val value = snapshot.getValue(Loans::class.java) as? Loans
            value?.let {
                _loans.postValue(it.loans)
            }
        }.addOnFailureListener { exception ->
        // Maneja errores si es necesario
        }
    }

    // LiveData para almacenar la respuesta de la solicitud de red
    val jsonResponseLiveData = MutableLiveData<String?>()

    init {
        // Inicializa el ViewModel y hace la solicitud de red cuando se crea
         fetchWeatherData()
    }

    // Propiedad para almacenar la ubicación (inicializada con "Buenos Aires" por defecto)
    var location: String = "Buenos Aires"

    data class WeatherData(
        val name: String,
        val coord: Coord,
        val weather: List<Weather>,
        val base: String,
        val main: Main,
        val visibility: Int,
        val wind: Wind,
        val clouds: Clouds,
        val dt: Long,
        val sys: Sys,
        val timezone: Int,
        val id: Long,
        val cod: Int
    ) {
        data class Coord(
            val lon: Double,
            val lat: Double
        )

        data class Weather(
            val id: Int,
            val main: String,
            val description: String,
            val icon: String
        )

        data class Main(
            val temp: Double,
            val feels_like: Double,
            val temp_min: Double,
            val temp_max: Double,
            val pressure: Int,
            val humidity: Int
        )

        data class Wind(
            val speed: Double,
            val deg: Int
        )

        data class Clouds(
            val all: Int
        )

        data class Sys(
            val type: Int,
            val id: Int,
            val country: String,
            val sunrise: Long,
            val sunset: Long
        )
    }

    // Función para hacer la solicitud de red y actualizar el LiveData
    fun fetchWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = "e42e9a73c4dac942fce0c6ed1e05d4d5"
            val apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=${location}&appid=${apiKey}"

            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    // Almacena el contenido de la respuesta en el LiveData
                    jsonResponseLiveData.postValue(response.toString())

                    // Imprime el JSON en el log
                    Log.d("WeatherJSON", response.toString())

                    reader.close()
                    inputStream.close()
                } else {
                    // Manejo de errores, hay que ver qué hacemos
                    jsonResponseLiveData.postValue(null)
                }

                connection.disconnect()
            } catch (e: Exception) {
                // errores de conexión
                jsonResponseLiveData.postValue(null)
            }
        }
    }
}
