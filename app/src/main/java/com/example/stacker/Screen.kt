package com.example.stacker

sealed class Screen(val route: String) {
    object TitleScreen : Screen("title_screen")
    object GameScreen : Screen("game_screen")
    object ScoreScreen : Screen("score_screen")
}
