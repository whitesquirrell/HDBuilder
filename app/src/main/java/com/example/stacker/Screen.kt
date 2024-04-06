package com.example.stacker

sealed class Screen(val route: String) {
    object TitleScreen : Screen("title_screen")
    object BackgroundScreen : Screen("background_screen")
    object GameScreen : Screen("game_screen")
    object ScoreScreen : Screen("score_screen")
    object RecordScoreScreen : Screen("record_score_screen")
}
