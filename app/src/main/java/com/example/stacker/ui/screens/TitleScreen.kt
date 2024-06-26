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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.stacker.ThreadPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object TextData {
    val textList = listOf(
        "Make SG Great Again!",
        "Build The HDBs!",
        "Make Your Family Proud!",
        "Do It For Your Country!",
        "Singapore Is Proud!"
    )
}

@Composable
fun TitleScreen(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.hdbuilder)
    val backgroundColor = colorResource(
        id = R.color.background
    )

    val displayText = remember { mutableStateOf(TextData.textList.random()) }
    var continueFlag by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        ThreadPool.execute(object : Runnable {

            override fun run() {
                while (continueFlag) {
                    val startTime = System.currentTimeMillis()
                    val endTime = startTime + 5000L // 5 seconds

                    while (System.currentTimeMillis() < endTime) {
                        Thread.sleep(100) // Sleep for 100 milliseconds
                    }
                    displayText.value = TextData.textList.random()
                }
            }
        })
    }

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

        Text(
            text = displayText.value,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            fontSize = 30.sp
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(480.dp))

            FloatingActionButton(
                onClick = {
                    continueFlag = false
                    navController.navigate(Screen.BackgroundScreen.route)
                },
            ) {
                Text(text = "Start",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            FloatingActionButton(
                onClick = {
                    continueFlag = false
                    navController.navigate(Screen.ScoreScreen.route)
                },
            ) {
                Text(text = "Scoreboard",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FloatingActionButton(
                onClick = {
                    continueFlag = false
                    navController.navigate(Screen.SettingsScreen.route)
                },
            ) {
                Text(text = "Settings",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
                )
            }
        }
    }
}

