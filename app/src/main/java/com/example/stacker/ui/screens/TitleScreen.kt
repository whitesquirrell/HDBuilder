package com.example.stacker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stacker.Screen
import com.example.stacker.R
import androidx.compose.foundation.background
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight


@Composable
fun TitleScreen(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.stacktheblocks3)
    val backgroundColor = colorResource(
        id = R.color.background
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-50).dp),
            contentScale = ContentScale.Fit // Ensures the image covers the available space
        )


        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(480.dp))

            FloatingActionButton(
                onClick = { navController.navigate(Screen.GameScreen.route) },
            ) {
                Text(text = "Start",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FloatingActionButton(
                onClick = { navController.navigate(Screen.ScoreScreen.route) },
            ) {
                Text(text = "Scoreboard",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
                )
            }
        }
    }
}