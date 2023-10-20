import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agroagil.Buy.ui.BuyViewModel
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.Task.ui.TaskViewModel
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Buys
import com.example.agroagil.core.models.Loan
import com.example.agroagil.core.models.Loans
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardViewModel : ViewModel() {

    // -------------- Ingresos y egresos
    private val _allSells = MutableLiveData<List<Sell>>()
    val allSells: LiveData<List<Sell>> get() = _allSells

    init {
        fetchAllSells()
    }

    private val _allBuys = MutableLiveData<List<Buy>>()
    val allBuys: LiveData<List<Buy>> get() = _allBuys

    init {
        fetchAllBuys()
    }

    private fun fetchAllSells() {
        Firebase.database.getReference("sell/0").get().addOnSuccessListener { snapshot ->
            val value = snapshot.getValue(Sells::class.java) as? Sells
            value?.let {
                // Ordena los elementos por fecha descendente
                val allSells = it.sells.sortedByDescending { it.date }
                _allSells.postValue(allSells)

                // Imprime la lista de ventas en los logs
                Log.d("FetchAllSells", "Lista de ventas: $allSells")
            }
        }.addOnFailureListener { exception ->
            // Maneja errores si es necesario
            Log.e("FetchAllSells", "Error al obtener las ventas: ${exception.message}")
        }
    }

    private fun fetchAllBuys() {
        Firebase.database.getReference("buy/0").get().addOnSuccessListener { snapshot ->
            val value = snapshot.getValue(Buys::class.java) as? Buys
            value?.let {
                // Ordena los elementos por fecha descendente
                val allBuys = it.buys.sortedByDescending { it.date }
                _allBuys.postValue(allBuys)

                // Imprime la lista de compras en los logs
                Log.d("FetchAllBuys", "Lista de compras: $allBuys")
            }
        }.addOnFailureListener { exception ->
            // Maneja errores si es necesario
            Log.e("FetchAllBuys", "Error al obtener las compras: ${exception.message}")
        }
    }

    fun getTotalIncome(): Double {
        return _allSells.value?.flatMap { sell ->
            sell.items.map { product -> product.amount * sell.price }
        }?.sum() ?: 0.0
    }

    fun getTotalExpenses(): Double {
        return _allBuys.value?.flatMap { buy ->
            buy.items.map { product -> product.amount * buy.price }
        }?.sum() ?: 0.0
    }

    // ----------------------- Ventas

    private val _topSells = MutableLiveData<List<Sell>>()

    init {
        fetchTopSells()
    }

    val topSells: LiveData<List<Sell>> get() = _topSells

    private fun fetchTopSells() {
        Firebase.database.getReference("sell/0").get().addOnSuccessListener { snapshot ->
            val value = snapshot.getValue(Sells::class.java) as? Sells
            value?.let {
                // Ordena los elementos por fecha descendente y luego selecciona los primeros n elementos
                val topSells = it.sells.sortedByDescending { it.date }
                    .take(5)
                _topSells.postValue(topSells)
            }
        }.addOnFailureListener { exception ->
            // Maneja errores si es necesario
        }
    }


    // ----------------------- Compras
    private val _topBuys = MutableLiveData<List<Buy>>()
    val topBuys: LiveData<List<Buy>> get() = _topBuys

    init {
        fetchTopBuys()
    }

    private fun fetchTopBuys() {
        Firebase.database.getReference("buy/0").get().addOnSuccessListener { snapshot ->
            val value = snapshot.getValue(Buys::class.java) as? Buys
            value?.let {
                // Ordena los elementos por fecha descendente y luego selecciona los primeros n elementos
                val topBuys = it.buys.sortedByDescending { it.date }
                    .take(5)
                _topBuys.postValue(topBuys)
            }
        }.addOnFailureListener { exception ->
            // Maneja errores si es necesario
        }
    }

    // --------------------------
    // -- Loans
    private val _loans = MutableLiveData<List<Loan>>()

    private fun fetchTopLoans() {
        Firebase.database.getReference("loan/0").get().addOnSuccessListener { snapshot ->
            val value = snapshot.getValue(Loans::class.java) as? Loans
            value?.let {
                val sortedLoans = it.loans.sortedByDescending { loan -> loan.date }
                _loans.postValue(sortedLoans)
            }
        }.addOnFailureListener { exception ->
            // Maneja errores si es necesario
        }
    }

    val loans: LiveData<List<Loan>> get() = _loans

        init {
            fetchTopLoans()
        }
    // --------------------------
    // Tasks

    private val taskViewModel = TaskViewModel()
    private val _taskCardDataList = MutableLiveData<List<TaskCardData>?>()
    val taskCardDataList: LiveData<List<TaskCardData>?> get() = _taskCardDataList

    private suspend fun fetchTopTasks(userId: Int) {
        try {
            val tasks = taskViewModel.taskRepository.getTaskCardsForUser(userId).take(5) // Solo toma las primeras 5 tareas

            val sortedTasks = tasks.sortedByDescending { task ->
                task.isoDate
            }

            _taskCardDataList.postValue(sortedTasks)
        } catch (e: Exception) {
            // Maneja errores si es necesario
            _taskCardDataList.postValue(null)
        }
    }

    fun getTopTasks(): LiveData<List<TaskCardData>?> {
        return taskCardDataList
    }

    init {
        viewModelScope.launch {
            fetchTopTasks(0)
        }
    }

    // --------------------------
    // -- Clima
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
                    jsonResponseLiveData.postValue(  "{\"coord\":{\"lon\":0,\"lat\":0},\"weather\":[{\"id\":800,\"main\":\"???\",\"description\":\"Sin conexión\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":273,\"feels_like\":0,\"temp_min\":273,\"temp_max\":273,\"pressure\":0,\"humidity\":0},\"visibility\":0,\"wind\":{\"speed\":0,\"deg\":0,\"gust\":2.24},\"clouds\":{\"all\":7},\"dt\":1697144825,\"sys\":{\"type\":2,\"id\":2031595,\"country\":\"AR\",\"sunrise\":1697102124,\"sunset\":1697148263},\"timezone\":-10800,\"id\":3435910,\"name\":\"Buenos Aires\",\"cod\":200}\n"
                    )
                }

                connection.disconnect()
            } catch (e: Exception) {
                // errores de conexión
                jsonResponseLiveData.postValue( "{\"coord\":{\"lon\":0,\"lat\":0},\"weather\":[{\"id\":800,\"main\":\"???\",\"description\":\"Sin conexión\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":273,\"feels_like\":0,\"temp_min\":273,\"temp_max\":273,\"pressure\":0,\"humidity\":0},\"visibility\":0,\"wind\":{\"speed\":0,\"deg\":0,\"gust\":2.24},\"clouds\":{\"all\":7},\"dt\":1697144825,\"sys\":{\"type\":2,\"id\":2031595,\"country\":\"AR\",\"sunrise\":1697102124,\"sunset\":1697148263},\"timezone\":-10800,\"id\":3435910,\"name\":\"Buenos Aires\",\"cod\":200}\n"
                )
            }
        }
    }
}