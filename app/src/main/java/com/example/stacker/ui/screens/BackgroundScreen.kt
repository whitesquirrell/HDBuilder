package com.example.stacker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stacker.Screen
import com.example.stacker.R

@Composable
fun BackgroundScreen(navController: NavController) {
    // Assuming you have a story image in your drawable, replace 'R.drawable.story_background' with the actual resource id
    val backgroundImage = painterResource(id = R.drawable.lky_transparent)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "The story so far...",
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(16.dp)
            )

            // Replace this text with your story paragraph
            Text(
                text = "The year is 2100. Overpopulation is on the rise worldwide, and Singapore has once again been hit by another housing crisis. \n" +
                        "\n" +
                        "Fortunately scientists have managed to bring Lee Kuan Yew back from the grave to steer our great nation out of this dark age.\n" +
                        "\n" +
                        "However, he cannot revitalise Singapore alone, help Lee Kuan Yew build HDBs and make Singapore great again!    ",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate(Screen.GameScreen.route) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Continue")
            }
        }
    }
}
