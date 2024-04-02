package com.example.stacker

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Destinations {
    const val TITLE_SCREEN = "title"
    const val GAME_SCREEN = "game"
}
@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination =  Screen.TitleScreen.route) {
        composable(route = Screen.TitleScreen.route) {
            TitleScreen(navController = navController)
        }

        composable(
            route = Screen.GameScreen.route,
        ){ entry ->
            GameScreen(navController = navController)
        }
    }
}

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