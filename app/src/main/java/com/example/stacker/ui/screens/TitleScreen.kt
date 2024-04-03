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
import androidx.compose.material3.Button


@Composable
fun TitleScreen(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.stacktheblocks)
    val backgroundColor = colorResource(
        id = R.color.background
    )

    Box(
        modifier = Modifier.fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize().offset(y = (-50).dp),
            contentScale = ContentScale.Fit // Ensures the image covers the available space
        )


        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(480.dp))

            Button(
                onClick = { navController.navigate(Screen.GameScreen.route) }
            ) {
                Text(text = "Start Game")
            }
        }
    }
}