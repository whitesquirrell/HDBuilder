package com.example.stacker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stacker.Screen
import com.example.stacker.R
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScoreScreen(navController: NavController, score: Int) {
    val backgroundImage = painterResource(id = R.drawable.stacktheblocks3)
    val backgroundColor = colorResource(
        id = R.color.background
    )
    var text by remember { mutableStateOf("Anonymous") }

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

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(480.dp))

            Text("Your score is $score, thank you for your service! Enter your name to record your score in the history books.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Name") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FloatingActionButton(
                onClick = {
                    submitScore(name = text, score = score)
                    navController.navigate(Screen.TitleScreen.route) },
            ) {
                Text(text = "Submit and Return",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(start = 30.dp, end= 30.dp)
                )
            }
        }
    }
}

fun submitScore(name: String, score: Int) {
    val database = FirebaseDatabase.getInstance("https://stacktheblocks-2764e-default-rtdb.asia-southeast1.firebasedatabase.app")
    val myRef = database.getReference("/")
    val scoresRef = database.getReference("scores")
    val score = Score(name, score)
    val newScoreRef = scoresRef.push()
    newScoreRef.setValue(score)
        .addOnSuccessListener { println("Insertion successful") }
        .addOnFailureListener { println("Insertion failed") }
}