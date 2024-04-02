package com.example.stacker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stacker.Screen

@Composable
fun TitleScreen(navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Title Screen",
        )
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.GameScreen.route)
            },
            modifier = Modifier
                .shadow(ambientColor = Color.Red, elevation = 1.dp)
        ) {
            Text(text =  "Start Game")
        }
    }
}