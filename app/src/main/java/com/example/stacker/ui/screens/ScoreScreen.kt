package com.example.stacker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stacker.R
import com.example.stacker.Screen
import com.example.stacker.ThreadPool
import com.google.firebase.database.FirebaseDatabase

// declare how score object should be stored, with what variables
data class Score(
    val name: String? = null,
    val score: Int? = null
)

object ScoreGlobals {
    // declare which database to use, uses the "scores" directory within db
    private val Database = FirebaseDatabase.getInstance("https://stacktheblocks-2764e-default-rtdb.asia-southeast1.firebasedatabase.app")
    val ScoresRef = Database.getReference("scores")
}

// main screen
@Composable
fun ScoreScreen(navController: NavController) {

    // background image, change as needed
    val backgroundImage = painterResource(id = R.drawable.lky_transparent)

    var loadingFlag by remember { mutableStateOf(false) }

    // list of top scores
    val topScores = remember { mutableStateListOf<Score>() }

    // fetches the scores upon opening activity
    LaunchedEffect(Unit) {
        ThreadPool.execute(
            ScoreThread(
                topScores = topScores,
                setLoadingFlag = { flagValue : Boolean -> loadingFlag = flagValue}
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // background image
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
            // Ensures the image covers the available space
        )

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ){

            // Header text for scoreboard
            Text(text = "Scoreboard",
                fontWeight = FontWeight.SemiBold,
                fontSize = 50.sp,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(start = 30.dp, end= 30.dp)
            )

            // Displays top few scores from database
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // change n within the take() to change number of scores you want to show
                itemsIndexed(topScores.take(5).reversed()) {
                   index, score ->
                        Text(
                            text = "${index + 1}. ${score.name} - ${score.score}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 30.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .clickable{
                        ThreadPool.execute(
                            ScoreThread(
                                topScores = topScores,
                                setLoadingFlag = { flagValue : Boolean -> loadingFlag = flagValue}
                            )
                        )
                    }
                    .border (
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text (
                    text = if (loadingFlag) "Loading..." else "Refresh",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Black
                )
            }

            // back button
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    )
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
                    text = "Back to Main Menu",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Black
                )
            }
        }
    }
}

class ScoreThread(private val topScores: MutableList<Score>,
    private val setLoadingFlag: (Boolean) -> Unit) : Runnable {
    override fun run() {
        setLoadingFlag(true)
        // function to fetch scores from db and store within topScores list
        ScoreGlobals.ScoresRef.get().addOnSuccessListener { dataSnapshot ->
            var scores = mutableListOf<Score>()
            for (scoreSnapshot in dataSnapshot.children) {
                val score = scoreSnapshot.getValue(Score::class.java)
                if (score != null) {
                    scores.add(score)
                }
            }
            scores = scores.sortedByDescending { it.score ?: 0 }.toMutableList()
            topScores.clear()
            setLoadingFlag(false)
            topScores.addAll(scores.take(5))
            topScores.reverse()
        }.addOnFailureListener {
            // Handle failure
        }
    }
}