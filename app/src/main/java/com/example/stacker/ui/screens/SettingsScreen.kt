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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stacker.Screen


@Composable
fun SettingsScreen(navController: NavController, main: ComponentActivity) {
    val backgroundColor = colorResource(
        id = R.color.background
    )

    var fullScreenFlag by remember { mutableStateOf(false) }

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

            Text(text = "Fullscreen")
            Switch(checked = fullScreenFlag, onCheckedChange = {
                toggleFullscreen(
                    fullScreenFlag = fullScreenFlag,
                    setFullScreenFlag = { flagValue: Boolean -> fullScreenFlag = flagValue },
                    main = main
                )
            })

            Spacer(modifier = Modifier.height(20.dp))

            // back button
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .clickable {
                        navController.navigate(Screen.TitleScreen.route)
                    }
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
                Text(
                    text = "Back",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(all = 8.dp),
                    color = Color.Black
                )
            }
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