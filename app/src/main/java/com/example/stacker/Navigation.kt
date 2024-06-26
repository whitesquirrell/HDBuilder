package com.example.stacker

import android.content.Context
import android.icu.text.CaseMap.Title
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.rememberCompositionContext
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
import com.example.stacker.ui.screens.BackgroundScreen
import com.example.stacker.ui.screens.GameScreen
import com.example.stacker.ui.screens.ScoreScreen
import com.example.stacker.ui.screens.TitleScreen
import com.example.stacker.ui.screens.RecordScoreScreen
import com.example.stacker.ui.screens.SettingsScreen

object Destinations {
    const val TITLE_SCREEN = "title"
    const val GAME_SCREEN = "game"
    const val SCORE_SCREEN = "score"
    const val SETTINGS_SCREEN = "settings"
    const val RECORD_SCORE_SCREEN = "record_score"
    const val BACKGROUND_SCREEN = "background"
}
@Composable
fun Navigation(context: Context, main: ComponentActivity) {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination =  Screen.TitleScreen.route) {
        composable(route = Screen.TitleScreen.route) {
            TitleScreen(navController = navController)
        }

        composable(route = Screen.BackgroundScreen.route) {
            BackgroundScreen(navController = navController)
        }

        composable(
            route = Screen.GameScreen.route,
        ){
            GameScreen(navController = navController, context = context)
        }

        composable(
            route = Screen.ScoreScreen.route,
        ) {
            ScoreScreen(navController = navController)
        }

        composable(
            route = "${Screen.RecordScoreScreen.route}/{score}",
        ) { backStackEntry ->
            val score: Int = (backStackEntry.arguments?.getString("score"))?.toInt() ?: -1
            RecordScoreScreen(navController = navController, score = score)
        }

        composable(
            route = Screen.SettingsScreen.route,
        ) {
            SettingsScreen(navController = navController, main = main)
        }
    }
}



