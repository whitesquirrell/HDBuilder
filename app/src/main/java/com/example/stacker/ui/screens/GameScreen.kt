package com.example.stacker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.stacker.Screen

@Composable
fun GameScreen(navController: NavController){
    Column {
        Text(
            text = "Game Screen",
        )

        Button(
            onClick = {
                navController.navigate(Screen.TitleScreen.route)
            },
            modifier = Modifier.align(Alignment.End),

            ) {
            Text(text = "Go back(pls delete)")
        }
    }
}