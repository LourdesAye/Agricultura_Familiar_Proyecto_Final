

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.agroagil.Sell.ui.SellViewModel
import com.example.agroagil.core.models.Product
import com.example.agroagil.core.models.Loan
import java.util.Date

var currentSell = Sell()
@Composable
fun itemProduct(item: Product){
    Row() {
        Column(modifier = Modifier.padding(top = 5.dp)) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)) {
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
                        item.amount.toString()+" "+ item.units.toString(), modifier = Modifier
                            .padding(end = 10.dp)
                            .align(Alignment.CenterVertically))
                }
            }
            Divider()
        }
    }
}



@Composable
fun SellInfoScreen(navController: NavController, sellViewModel: SellViewModel, sellId: Int){
    var valuesSell = sellViewModel.farm.observeAsState().value
    if (valuesSell == null){
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp)
            )
        }

    }else {
        currentSell = valuesSell.get(sellId)
        Column(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            ) {
                Text(
                    currentSell.nameUser,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    currentSell.date,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
            Text(
                "Productos vendidos",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 50.dp, bottom = 10.dp)
            )
            for (i in 0..currentSell.items.size - 1) {
                itemProduct(currentSell.items[i])
            }

        }
    }
}