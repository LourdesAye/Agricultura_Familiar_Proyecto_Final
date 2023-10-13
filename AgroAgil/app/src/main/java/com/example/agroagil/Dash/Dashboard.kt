import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import com.example.agroagil.Task.model.TaskCardData
import com.example.agroagil.Task.ui.CardColor
import com.example.agroagil.Task.ui.RoundCheckbox
import com.example.agroagil.Task.ui.TASK_CARD_DATA_LIST_MOCK
import com.example.agroagil.Task.ui.TextDate
import com.example.agroagil.Task.ui.getCardColor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.MutableLiveData
import com.example.agroagil.R
import com.example.agroagil.core.models.Loan

val weatherDescriptionsMap = mapOf(
    "thunderstorm with light rain" to "Tormenta con lluvia ligera",
    "thunderstorm with rain" to "Tormenta con lluvia",
    "thunderstorm with heavy rain" to "Tormenta con lluvia intensa",
    "light thunderstorm" to "Tormenta ligera",
    "thunderstorm" to "Tormenta",
    "heavy thunderstorm" to "Tormenta intensa",
    "ragged thunderstorm" to "Tormenta desordenada",
    "thunderstorm with light drizzle" to "Tormenta con llovizna ligera",
    "thunderstorm with drizzle" to "Tormenta con llovizna",
    "thunderstorm with heavy drizzle" to "Tormenta con llovizna intensa",
    "light intensity drizzle" to "Llovizna ligera",
    "drizzle" to "Llovizna",
    "heavy intensity drizzle" to "Llovizna intensa",
    "light intensity drizzle rain" to "Llovizna ligera con lluvia",
    "drizzle rain" to "Llovizna con lluvia",
    "heavy intensity drizzle rain" to "Llovizna intensa con lluvia",
    "shower rain and drizzle" to "Chubascos de lluvia y llovizna",
    "heavy shower rain and drizzle" to "Chubascos intensos de lluvia y llovizna",
    "shower drizzle" to "Chubascos de llovizna",
    "light rain" to "Lluvia ligera",
    "moderate rain" to "Lluvia moderada",
    "heavy intensity rain" to "Lluvia de intensidad fuerte",
    "very heavy rain" to "Lluvia muy intensa",
    "extreme rain" to "Lluvia extrema",
    "freezing rain" to "Lluvia congelante",
    "light intensity shower rain" to "Chubascos de lluvia de intensidad ligera",
    "shower rain" to "Chubascos de lluvia",
    "heavy intensity shower rain" to "Chubascos de lluvia de intensidad fuerte",
    "ragged shower rain" to "Chubascos de lluvia desordenados",
    "light snow" to "Nieve ligera",
    "snow" to "Nieve",
    "heavy snow" to "Nieve intensa",
    "sleet" to "Aguanieve",
    "light shower sleet" to "Aguanieve ligera",
    "shower sleet" to "Aguanieve",
    "light rain and snow" to "Lluvia ligera y nieve",
    "rain and snow" to "Lluvia y nieve",
    "light shower snow" to "Chubascos de nieve ligeros",
    "shower snow" to "Chubascos de nieve",
    "heavy shower snow" to "Chubascos de nieve intensos",
    "mist" to "Niebla",
    "smoke" to "Humo",
    "haze" to "Neblina",
    "sand/dust whirls" to "Remolinos de arena/polvo",
    "fog" to "Niebla",
    "sand" to "Arena",
    "dust" to "Polvo",
    "volcanic ash" to "Ceniza volcánica",
    "squalls" to "Rachas de viento",
    "tornado" to "Tornado",
    "clear sky" to "Cielo despejado",
    "few clouds" to "Pocas nubes",
    "scattered clouds" to "Nubes dispersas",
    "broken clouds" to "Parcialmente nublado"
)

@Composable
fun WeatherCard(weatherJson: String?, borderColor: Color, backgroundColor: Color, azul: Color) {
    weatherJson?.let {
        val gson = Gson()
        val weatherData = gson.fromJson(weatherJson, DashboardViewModel.WeatherData::class.java)

        val location = "${weatherData.name}, ${weatherData.sys.country}"
        val temperature = (weatherData.main.temp - 273.15).toInt()
        val description = weatherData.weather.firstOrNull()?.description ?: "N/A"
        val temperatureMin = (weatherData.main.temp_min - 273.15).toInt()
        val temperatureMax = (weatherData.main.temp_max - 273.15).toInt()
        val translatedDescription = weatherDescriptionsMap[description] ?: description

        val iconName = weatherData.weather.firstOrNull()?.icon ?: "01d"
        val iconResourceId = getWeatherIconResourceId(iconName)
        val iconPainter: Painter = painterResource(iconResourceId)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(5.dp, borderColor),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Muestra el ícono del clima
                Image(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)  // tamaño icono
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds  // escala sin estirar
                )

                // Muestra la ubicación, la temperatura y la descripción en una Columna
                Column {
                    Text(
                        text = "$location   $temperature°C",
                        color = azul,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Min: $temperatureMin°C  -  Max: $temperatureMax°C",
                        color = Color.Black
                    )
                    Text(
                        text = translatedDescription,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private fun getWeatherIconResourceId(iconName: String): Int {
    return when (iconName) {
        "01d" -> R.drawable._01d
        "01n" -> R.drawable._01n
        "02d" -> R.drawable._02d
        "02n" -> R.drawable._02n
        "03d" -> R.drawable._03d
        "03n" -> R.drawable._03n
        "04d" -> R.drawable._04d
        "04n" -> R.drawable._04n
        "09d" -> R.drawable._09d
        "09n" -> R.drawable._09n
        "10d" -> R.drawable._10d
        "10n" -> R.drawable._10n
        "11d" -> R.drawable._11d
        "11n" -> R.drawable._11n
        "13d" -> R.drawable._13d
        "13n" -> R.drawable._13n
        "50d" -> R.drawable._50d
        "50n" -> R.drawable._50n
        else -> R.drawable._01n // Un ícono por defecto para casos no reconocidos
    }
}



@Composable
fun TaskCard(backgroundColor: Color, borderColor: Color, azul: Color, taskList: List<TaskCardData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(5.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Próximas tareas", color = azul, fontWeight = FontWeight.Bold)

            // Muestra solo las primeras 5 tareas
            val tasksToShow = taskList.take(5)

            tasksToShow.forEach { task ->
                TaskItem(task)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TaskItem(taskCardData: TaskCardData) {
    val roundedCornerShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 14.dp,
        bottomEnd = 14.dp,
        bottomStart = 14.dp
    )

    val cardColor: CardColor = getCardColor(taskCardData.highPriority, taskCardData.completed)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                // TODO: Agrega la acción después de hacer click en la card
            },
        shape = roundedCornerShape,
        colors = CardDefaults.cardColors(
            containerColor = Color(cardColor.surfaceColor.toColorInt())
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = taskCardData.getLimitedDescription(),
                    fontWeight = FontWeight.Bold,
                    color = Color(cardColor.textColor.toColorInt()),
                    style = MaterialTheme.typography.titleLarge
                )
                TextDate(taskCardData)
            }

            RoundCheckbox(
                checked = taskCardData.completed,
                onCheckedChange = { /* acá hay que tocar para la acción */ },
                color = Color(cardColor.textColor.toColorInt())
            )
        }
    }
}

@Composable
fun CashCard(ingresos: Int, egresos: Int, backgroundColor: Color, borderColor: Color, azul: Color) {
    var totalwidth = ingresos + egresos
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(5.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Caja", color = azul, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Dibuja la barra de ingresos con el número al lado
            DrawBar(ingresos, totalwidth, Color(0xFFE57373), azul)

            Spacer(modifier = Modifier.height(8.dp))

            // Dibuja la barra de egresos con el número al lado
            DrawBar(egresos, totalwidth, Color(0xFF81C784), azul)

            Spacer(modifier = Modifier.height(16.dp))
            var total = ingresos - egresos
            // Muestra el total con el borde suave
            Text(
                text = "Total: $total",
                color = azul,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun LoanCard(topLoans: List<Loan>, backgroundColor: Color, borderColor: Color, azul: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(540.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(5.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Préstamos", color = azul, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            for (loan in topLoans.take(5)) {
                // Muestra la información del préstamo
                Text(text = "${loan.nameUser}  ${loan.date}", color = Color.Black)
                // Muestra los productos del préstamo utilizando la función itemProduct
                loan.items.forEach { product ->
                    itemProduct(product)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}



@Composable
fun DrawBar(value: Int, total: Int, color: Color, azul: Color) {
    Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
        val width = (value.toFloat() / total.toFloat()) * size.width

        val gradientShader = Brush.verticalGradient(
            colors = listOf(color, Color.Black)
        )

        drawRect(brush = gradientShader, size = Size(width, 50f))
        drawContext.canvas.nativeCanvas.drawText(
            "$value",
            width + 50f,
            size.height / 2,
            android.graphics.Paint().apply {
                // color = azul
                textSize = 40f
                isAntiAlias = true
                typeface = android.graphics.Typeface.defaultFromStyle(android.graphics.Typeface.BOLD)
            }
        )
    }
}

@Composable
fun dash(viewModel: DashboardViewModel) {
    val backgroundColor = Color(android.graphics.Color.parseColor("#F0FFFF"))
    val borderColor = Color(android.graphics.Color.parseColor("#A6DB68"))
    val textColor = Color(android.graphics.Color.parseColor("#0CBFDF"))

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            // crea tarjeta y luego busca datos, para que no tarde en crearla no la inicializo en null
            val jsonResponse by viewModel.jsonResponseLiveData.observeAsState(
                initial = "{\"coord\":{\"lon\":0,\"lat\":0},\"weather\":[{\"id\":800,\"main\":\"???\",\"description\":\"Sin conexión\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":273,\"feels_like\":0,\"temp_min\":273,\"temp_max\":273,\"pressure\":0,\"humidity\":0},\"visibility\":0,\"wind\":{\"speed\":0,\"deg\":0,\"gust\":2.24},\"clouds\":{\"all\":7},\"dt\":1697144825,\"sys\":{\"type\":2,\"id\":2031595,\"country\":\"AR\",\"sunrise\":1697102124,\"sunset\":1697148263},\"timezone\":-10800,\"id\":3435910,\"name\":\"Buenos Aires\",\"cod\":200}\n")

            WeatherCard(jsonResponse, borderColor, backgroundColor, textColor)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val taskList = TASK_CARD_DATA_LIST_MOCK.sortedByDescending { it.date }
            val limitedTaskList = taskList.take(5)
            TaskCard(backgroundColor, borderColor, textColor, limitedTaskList)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val ingresos = 200000
            val egresos = 150000
            CashCard(ingresos, egresos, backgroundColor, borderColor, textColor)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val topLoans by viewModel.loans.observeAsState(listOf())
            LoanCard(topLoans, backgroundColor, borderColor, textColor)
        }
    }
}