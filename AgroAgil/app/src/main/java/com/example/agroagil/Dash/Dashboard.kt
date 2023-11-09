import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.toColorInt
import com.example.agroagil.Loan.ui.SelectColorCard
import com.example.agroagil.R
import com.example.agroagil.Task.ui.COMPLETED_TASK_CARD_COLOR
import com.example.agroagil.Task.ui.COMPLETED_TASK_TEXT_COLOR
import com.example.agroagil.Task.ui.INCOMPLETE_IMPORTANT_TASK_CARD_COLOR
import com.example.agroagil.Task.ui.INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR
import com.example.agroagil.Task.ui.INCOMPLETE_NORMAL_TASK_CARD_COLOR
import com.example.agroagil.Task.ui.INCOMPLETE_NORMAL_TASK_TEXT_COLOR
import com.example.agroagil.Task.ui.TextDate
import com.example.agroagil.core.models.Buy
import com.example.agroagil.core.models.Crop
import com.example.agroagil.core.models.Loan
import com.example.agroagil.core.models.Plantation
import com.example.agroagil.core.models.Product
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.TimeZone
import java.util.Calendar
import java.util.Locale


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
    "broken clouds" to "Parcialmente nublado",
    "overcast clouds" to "Totalmente nublado"
)

@Composable
fun WeatherCard(weatherJson: String?, borderColor: Color, backgroundColor: Color, textColor: Color) {
    weatherJson?.let {
        val gson = Gson()
        val weatherData = gson.fromJson(weatherJson, DashboardViewModel.WeatherData::class.java)

        val location = "${weatherData.name}, ${weatherData.sys.country}"
        val temperature = (weatherData.main.temp - 273.15).toInt()
        val description = weatherData.weather.firstOrNull()?.description ?: "N/A"
        val temperatureMin = (weatherData.main.temp_min - 273.15).toInt()
        val temperatureMax = (weatherData.main.temp_max - 273.15).toInt()
        val translatedDescription = weatherDescriptionsMap[description] ?: description

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            .format(Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")).time)

        val iconName = weatherData.weather.firstOrNull()?.icon ?: "01d"
        val iconResourceId = getWeatherIconResourceId(iconName)
        val iconPainter: Painter = painterResource(iconResourceId)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), // La altura se ajusta automáticamente al contenido
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(2.dp, borderColor),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
        ) {
            // Card principal (verde)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Clima", color = textColor, fontWeight = FontWeight.Bold)

                // Card celeste interna
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 12.dp
                    ),
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, textColor),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                ) {
                    // Contenido de la card celeste
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$location   $temperature°C\n$translatedDescription",
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Muestra el ícono del clima
                            Image(
                                painter = iconPainter,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)  // Tamaño constante del icono
                                    .clip(CircleShape)
                                    .fillMaxSize()  // Llena el espacio disponible sin estirar
                                    .align(Alignment.CenterVertically),  // Alinea el icono verticalmente al centro
                                contentScale = ContentScale.FillBounds  // Escala sin estirar
                            )

                            // Muestra la fecha, la descripción traducida y la min y max
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentDate,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Min: $temperatureMin°C",
                                    color = Color.Black
                                )
                                Text(
                                    text = "Max: $temperatureMax°C",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                // Muestra la WeatherCard hardcodeada
                ForecastWeatherCard(currentDate, temperatureMin, temperatureMax, description)
            }
        }
    }
}

@Composable
fun ForecastWeatherCard(currentDate: String, minTemp: Int, maxTemp: Int, description: String) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"))
    calendar.time = dateFormat.parse(currentDate)

    // Calcula la fecha del próximo día
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val nextDayDate = dateFormat.format(calendar.time)

    // Calcula la fecha de pasado mañana
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val nextDayDate2 = dateFormat.format(calendar.time)

    val iconResourceId = if (description != "Sin conexión") R.drawable._01d else R.drawable._01n
    val iconResourceId2 = if (description != "Sin conexión") R.drawable._10d else R.drawable._01n

    val text1 = "$nextDayDate\n${if (description != "Sin conexión") "Cielo despejado" else "Sin conexión"}\n${if (description != "Sin conexión") minTemp + 1 else minTemp}°C / ${if (description != "Sin conexión") maxTemp + 1 else maxTemp}°C"
    val text2 = "$nextDayDate2\n${if (description != "Sin conexión") "Lluvia ligera" else "Sin conexión"}\n${if (description != "Sin conexión") minTemp - 5 else minTemp}°C / ${if (description != "Sin conexión") maxTemp - 4 else maxTemp}°C"

    val textColor = Color(android.graphics.Color.parseColor("#0CBFDF"))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(IntrinsicSize.Min), // La altura se ajusta automáticamente al contenido
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp,
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(0.dp, textColor),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mañana
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(iconResourceId),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp) // Tamaño del icono
                )
                Text(
                    text = "$text1"
                )
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(IntrinsicSize.Min), // La altura se ajusta automáticamente al contenido
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(0.dp, textColor),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pasado mañana
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(iconResourceId2),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp) // Tamaño del icono
                )
                Text(
                    text = "$text2"
                )
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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCardDash(
    dashviewModel: DashboardViewModel,
    borderColor: Color,
    textColor: Color
) {
    val topTasksState by dashviewModel.getTopTasks().observeAsState(initial = emptyList())
    val roundedCornerShape = RoundedCornerShape(14.dp)

    val currentDate = LocalDate.now()

    // Calcula el número de tareas atrasadas (con fecha menor y no completadas)
    val backlogTasksCount = topTasksState?.count { task ->
        val taskDate = LocalDate.parse(task?.isoDate?.substring(0, 10) ?: "")
        taskDate.isBefore(currentDate) && !(task?.completed ?: true)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        shape = roundedCornerShape,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row para la campanita y el número de tareas atrasadas

            Text(text = "Próximas tareas", color = textColor, fontWeight = FontWeight.Bold)
            if (backlogTasksCount != null) {
                if (backlogTasksCount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color.Red
                        )
                        Text(
                            text = "Tareas atrasadas: $backlogTasksCount",
                            color = textColor
                        )
                    }
                }
            }

            topTasksState?.forEach { task ->
                val taskDate = LocalDate.parse(task?.isoDate?.substring(0, 10) ?: "")
                if (taskDate.isEqual(currentDate) || taskDate.isAfter(currentDate) || (taskDate.isBefore(currentDate) && !(task?.completed ?: true))) {
                    val cardColor = getCardColor(task?.highPriority ?: false, task?.completed ?: false)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = roundedCornerShape,
                        colors = CardDefaults.cardColors(containerColor = Color(cardColor.surfaceColor.toColorInt())),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = task?.getLimitedDescription() ?: "Descripción no disponible",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(cardColor.textColor.toColorInt()),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                TextDate(taskCardData = task)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class CardColor(val surfaceColor: String, val textColor: String)

private fun getCardColor(highPriority: Boolean, completed: Boolean): CardColor {
    if (completed)
        return CardColor(COMPLETED_TASK_CARD_COLOR, COMPLETED_TASK_TEXT_COLOR)
    if (highPriority)
        return CardColor(INCOMPLETE_IMPORTANT_TASK_CARD_COLOR, INCOMPLETE_IMPORTANT_TASK_TEXT_COLOR)
    return CardColor(INCOMPLETE_NORMAL_TASK_CARD_COLOR, INCOMPLETE_NORMAL_TASK_TEXT_COLOR)
}

@Composable
fun CashCard(ingresos: Int, egresos: Int, backgroundColor: Color, borderColor: Color, textColor: Color) {
    val totalWidth = ingresos + egresos

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Caja - Últimos 30 días", color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta celeste
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                ),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, textColor),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Dibuja la barra de ingresos
                    drawProgressBar(ingresos, totalWidth, Color(0xFF81C784), textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Ingresos: $$ingresos", color = textColor, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dibuja la barra de egresos
                    drawProgressBar(egresos, totalWidth, Color(0xFFE57373), textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Egresos: $$egresos", color = textColor, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))
                    val total = ingresos - egresos
                    // Muestra el total alineado a la derecha
                    Text(
                        text = "Total: $$total",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End) // Alinea el texto a la derecha
                    )
                }
            }
        }
    }
}


@Composable
fun drawProgressBar(value: Int, total: Int, barColor: Color, textColor: Color) {
    val fraction = if (total != 0) value.toFloat() / total.toFloat() else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth(fraction)
            .height(20.dp)
            .background(brush = Brush.horizontalGradient(listOf(barColor, Color.Black)))
    ) {
        // Texto dentro de la barra de progreso
       /*Text(
            text = "$value",
            color = textColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, end = 4.dp)
        ) */
    }
}

/*
@Composable
fun DrawBar(value: Int, total: Int, barColor: Color, modifier: Modifier = Modifier) {
    val maxWidth = 220.dp
    val maxHeight = 40.dp

    Canvas(modifier = modifier
        .fillMaxWidth()
        .padding(start = 10.dp)
        .height(maxHeight)
    ) {
        val barWidth = ((value.toFloat() / total.toFloat()) * maxWidth.toPx()).coerceAtMost(maxWidth.toPx())

        // Crea un degradado vertical desde la barra hasta negro
        val gradientShader = Brush.verticalGradient(
            colors = listOf(barColor, Color.Black),
            startY = 0f,
            endY = barWidth
        )

        // Dibuja la barra de progreso con el degradado
        drawRect(brush = gradientShader, size = Size(barWidth, maxHeight.toPx()))
    }
}
 */


@Composable
fun itemProductDash(item: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
    ) {
        Column(modifier = Modifier.padding(bottom = 5.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(SolidColor(Color("#00687A".toColorInt())))
                        }
                        Text(
                            text = item.name.substring(0, 2).capitalize(),
                            color = Color.White
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            item.name,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            "${item.amount} ${item.units}",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
            Divider()
        }
    }
}

/*
// Esto usaba un cálcuilo para modificar la altura de la card, hay una forma mejor de hacerlo
@Composable
fun BuyCard(topBuys: List<Buy>, backgroundColor: Color, borderColor: Color, textColor: Color) {
    // Calcula la altura de la tarjeta verde en función de la cantidad de compras
    val greenCardHeight = (topBuys.size * 250).dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = greenCardHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Compras", color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            for (buy in topBuys.take(5)) {
                // Cada compra se representa como una tarjeta individual con un borde
                DisplayBuyItem(buy, textColor)
            }
        }
    }
}
 */

@Composable
fun BuyCard(topBuys: List<Buy>, backgroundColor: Color, borderColor: Color, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max), // Utiliza IntrinsicSize.Max para ajustar la altura automáticamente
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Últimas Compras", color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            for (buy in topBuys.take(5)) {
                // Cada compra se representa como una tarjeta individual con un borde
                DisplayBuyItem(buy, textColor)
            }
        }
    }
}

@Composable
fun DisplayBuyItem(buy: Buy, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(IntrinsicSize.Max)
            .border(BorderStroke(1.dp, textColor), shape = MaterialTheme.shapes.small),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row {
            Column(
                modifier = Modifier
                    .background(Color(SelectColorCard(buy.paid).toColorInt()))
                    .width(10.dp)
                    .fillMaxHeight()
            ) {
                // Barra de color a la izquierda de la tarjeta
            }
        Column(modifier = Modifier.padding(16.dp)) {
            // Muestra la información de la compra
            Text(text = "${buy.nameUser}  ${buy.date}", color = Color.Black)
            // Muestra los productos de la compra utilizando la función itemProductDash
            buy.items.forEach { product ->
                itemProductDash(product)
                val totalPrice = String.format("%.2f", product.amount * buy.price)
                Text(
                    "$$totalPrice"
                )
                }
            }
        }
    }
}

@Composable
fun SellCard(topSells: List<Sell>, backgroundColor: Color, borderColor: Color, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max), // Utiliza IntrinsicSize.Max para ajustar la altura automáticamente
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Últimas Ventas", color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            for (sell in topSells.take(5)) {
                // Cada venta se representa como una tarjeta individual con un borde
                DisplaySellItem(sell, textColor, backgroundColor)
            }
        }
    }
}

@Composable
fun DisplaySellItem(sell: Sell, textColor: Color, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(IntrinsicSize.Max)
            .border(BorderStroke(1.dp, textColor), shape = MaterialTheme.shapes.small),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row {
            Column(
                modifier = Modifier
                    .background(Color(SelectColorCard(sell.paid).toColorInt()))
                    .width(10.dp)
                    .fillMaxHeight()
            ) {
                // Barra de color a la izquierda de la tarjeta
            }
        Column(modifier = Modifier.padding(16.dp)) {
            // Muestra la información de la venta
            Text(text = "${sell.nameUser}  ${sell.date}", color = Color.Black)
            // Muestra los productos de la venta utilizando la función itemProductDash
            sell.items.forEach { product ->
                itemProductDash(product)
                val totalPrice = String.format("%.2f", sell.price * product.amount)
                Text(
                    "$$totalPrice"
                )
                }
            }
        }
    }
}

@Composable
fun LoanCard(topLoans: List<Loan>, backgroundColor: Color, borderColor: Color, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max), // Utiliza IntrinsicSize.Max para ajustar la altura automáticamente
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Préstamos pendientes", color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            for (loan in topLoans.take(5)) {
                // Cada préstamo se representa como una tarjeta individual con un borde
                DisplayLoanItem(loan, textColor, backgroundColor)
            }
        }
    }
}

@Composable
fun DisplayLoanItem(loan: Loan, textColor: Color, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, textColor), shape = MaterialTheme.shapes.small)
            .height(IntrinsicSize.Max), // Establece la altura máxima de la tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Row {
            Column(
                modifier = Modifier
                    .background(Color(SelectColorCard(loan.percentagePaid).toColorInt()))
                    .width(10.dp)
                    .fillMaxHeight()
            ) {
                // Barra de color a la izquierda de la tarjeta
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
            ) {
                Text(text = "${loan.nameUser}  ${loan.date}", color = Color.Black)
                loan.items.forEach { product ->
                    itemProductDash(product)
                }
            }
        }
    }
}

/*
@Composable
fun DisplayCropItem(crop: Plantation, textColor: Color, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, textColor), shape = MaterialTheme.shapes.small)
            .height(IntrinsicSize.Max),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Row {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .width(10.dp)
                    .fillMaxHeight()
            ) {

            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
            ) {
                Text(text = "Name: ${crop.name}", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Start Date: ${crop.dateStart}", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Id: ${crop.id}", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
 */

@Composable
fun CropCard(topCrops: List<Pair<Plantation, Crop>>, backgroundColor: Color, borderColor: Color, textColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Plantaciones a cosechar", color = textColor, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            for (plantation in topCrops) {
                // mostrar cada plantación
                DisplayPlantationItem(plantation, textColor, backgroundColor)
            }
        }
    }
}

@Composable
fun DisplayPlantationItem(plantation: Pair<Plantation, Crop>, textColor: Color, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(android.graphics.Color.parseColor("#E5EFE5")))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${plantation.second.name}", color = textColor, fontWeight = FontWeight.Bold)
            Text(text = "(${plantation.first.name})", color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
            val originalDate = plantation.first.dateStart
            val formattedDate = originalDate.substring(0, 10) // Obtener los primeros 10 caracteres (la parte de la fecha)
            Text(text = formattedDate, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            // más campos...
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
            val topPlantations by viewModel.topPlantations.observeAsState(listOf())
            Log.d("TopPlantations", topPlantations.toString())
            CropCard(topPlantations, backgroundColor, borderColor, textColor)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            // Usa las tareas filtradas del ViewModel en TaskCard
            TaskCardDash(dashviewModel = viewModel, borderColor = borderColor, textColor = textColor)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val ingresos = viewModel.getTotalIncome().toInt() // Obtiene los ingresos del ViewModel
            val egresos = viewModel.getTotalExpenses().toInt() // Obtiene los egresos del ViewModel
            CashCard(ingresos, egresos, backgroundColor, borderColor, textColor)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val topBuys by viewModel.topBuys.observeAsState(listOf())
            BuyCard(topBuys, backgroundColor, borderColor, textColor)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val topSells by viewModel.topSells.observeAsState(listOf())
            SellCard(topSells, backgroundColor, borderColor, textColor)
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