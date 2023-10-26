package com.example.agroagil.Stock.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Stock


var stockEnEsteMomento = mutableStateOf(Stock())

@Composable
fun itemProductBuy(item: Product) {
    Row() {
        Column(modifier = Modifier.padding(top = 5.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            ) {
                Row() {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterVertically)
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
                    Text(
                        item.name,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        item.amount.toString() + " " + item.units.toString(), modifier = Modifier
                            .padding(end = 10.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Divider()
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInfoScreen(navController: NavController, stockViewModel: StockViewModel, stockId: String?) {
    var stockActual = stockViewModel.stockEnBaseDeDatos.observeAsState().value
    if (stockActual == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(10.dp)
            )
        }
    } else {

        stockEnEsteMomento.value = stockActual.find { it.id == stockId } ?: Stock()
        val screenWidth = LocalConfiguration.current.screenHeightDp.dp
        Column(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .defaultMinSize(minHeight = screenWidth)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                var textChipStatus: String
                var colorChipStatus: Color
                if (stockEnEsteMomento.value.product.amount > 0) {
                    textChipStatus = "En Almacén"
                    colorChipStatus = Color(ConStock.toColorInt())
                } else {
                    textChipStatus = "No Disponible en Almacén"
                    colorChipStatus = Color(SinStock.toColorInt())
                }
                SuggestionChip(
                    onClick = { /* Do something! */ },
                    label = { Text(textChipStatus) },
                    enabled = false,
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        disabledLabelColor =
                        colorChipStatus
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        disabledBorderColor = colorChipStatus
                    )
                )
            }
            Column(
                modifier = Modifier.defaultMinSize(minHeight = screenWidth - 100.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp)
                    ) {
                        Text(
                            "Producto: " +
                                    stockEnEsteMomento.value.product.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Spacer(Modifier.padding(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp)
                    ) {
                        Text(
                            "Tipo de Producto: " +
                                    stockEnEsteMomento.value.type,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Spacer(Modifier.padding(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Cantidad: " +
                                    stockEnEsteMomento.value.product.amount.toString() + " " + stockEnEsteMomento.value.product.units,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }

                    Spacer(Modifier.padding(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(width = 0.dp, height = 150.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Precio por unidad: $ " + stockEnEsteMomento.value.product.price,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )

                    }
                }
            }
        }
    }
}
