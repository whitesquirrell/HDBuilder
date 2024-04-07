package com.example.stacker.ui.screens

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.stacker.R
import androidx.compose.foundation.background
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun SettingsScreen(navController: NavController, main: ComponentActivity) {
    val backgroundColor = colorResource(
        id = R.color.background
    )

    var fullScreenFlag by remember { mutableStateOf(false) }
//    var screenHeight = getScreenHeight()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
//                    .align(Alignment.CenterVertically)
            ){
                Text(text = "Fullscreen")
                Switch(checked = fullScreenFlag, onCheckedChange = {
                    toggleFullscreen(
                        fullScreenFlag = fullScreenFlag,
                        setFullScreenFlag = { flagValue: Boolean -> fullScreenFlag = flagValue },
                        main = main
                    )
                })
            }
                
            
//            Spacer(modifier = Modifier.height(480.dp))

//            FloatingActionButton(
//                onClick = { navController.navigate(Screen.BackgroundScreen.route) },
//            ) {
//                Text(text = "Start",
//                    fontWeight = FontWeight.SemiBold,
//                    style = MaterialTheme.typography.displaySmall,
//                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            FloatingActionButton(
//                // use this to debug recordScoreScreen path
////                onClick = { navController.navigate("record_score_screen/3") },
//                onClick = { navController.navigate(Screen.ScoreScreen.route) },
//            ) {
//                Text(text = "Scoreboard",
//                    fontWeight = FontWeight.SemiBold,
//                    style = MaterialTheme.typography.displaySmall,
//                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
//                )
//            }
        }
    }
}


private fun toggleFullscreen(
    fullScreenFlag: Boolean,
    setFullScreenFlag: (Boolean) -> Unit,
    main: ComponentActivity) {
    if (fullScreenFlag) {
        main.window.decorView.systemUiVisibility = 0
        main.actionBar?.show()
    } else {
        main.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        main.actionBar?.hide()
    }
    setFullScreenFlag(!fullScreenFlag)
}