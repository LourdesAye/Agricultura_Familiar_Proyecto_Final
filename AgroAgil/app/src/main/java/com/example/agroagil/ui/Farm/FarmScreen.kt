package com.example.agroagil.ui.Farm
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.agroagil.R

@Composable
fun GetFarmDescription(){
    Box() {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = stringResource(id = R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
        FilledIconButton(
            onClick = { /* Do something! */ },
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.BottomEnd),
            shape = Shapes().small
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }

    Text(text = "Mi granja", fontSize = 32.sp)
    Text(text = "Salta, Argentina")
    Row(

        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
    ) {
        Text(text = "5 Trabajadores", fontSize = 28.sp)
        Button(onClick = { /* Do something! */ }) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Invitar")
        }
    }
}

@Composable
fun GetMembers(){
    for(i in 1..3){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
        ) {
            for(i in 1..2){
            Card(
                modifier = Modifier.size(width = 200.dp, height = 240.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = stringResource(id = R.string.app_name),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp)
                    )
                    Text(
                        "Nombre del usuario",
                        modifier = Modifier
                            .background(Color("#F8FAFB".toColorInt()))
                            .fillMaxWidth()
                            .height(45.dp)
                            .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                        color = Color.Black,
                        fontSize = 17.sp
                    )
                    Text(
                        "Administrador",
                        modifier = Modifier
                            .background(Color("#F8FAFB".toColorInt()))
                            .fillMaxWidth()
                            .height(45.dp)
                            .padding(start = 10.dp, end = 10.dp),
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }

            }}
            }
        }
    }






@Composable
fun Farm(){
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxSize()){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 50.dp, bottom = 30.dp)
            .fillMaxSize()
    )

    {
        GetFarmDescription()
        GetMembers()
    }
    }
}


