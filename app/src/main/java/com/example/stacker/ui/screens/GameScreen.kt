package com.example.stacker.ui.screens

import android.R
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stacker.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController){

    var isTap by remember { mutableStateOf(false) }
    var isDoubleTap by remember { mutableStateOf(false) }

    // Images
    val backgroundImage = painterResource(id = com.example.stacker.R.drawable.lky_transparent)

    // Game Variable
    var buildingX by remember { mutableIntStateOf(0) }
    var buildingY by remember { mutableIntStateOf(0) }



    val haptic = LocalHapticFeedback.current

    if (isTap) {
        AlertDialog(
            onDismissRequest = { isTap = false },
            title = { Text("Ni Hao") },
            text = { Text("You tapped") },
            confirmButton = { Button(onClick = { isTap = false }) { Text("OK") } }
        )

    }

    if (isDoubleTap) {
        AlertDialog(
            onDismissRequest = { isDoubleTap = false },
            title = { Text("Ni Hao") },
            text = { Text("You Double Tap") },
            confirmButton = { Button(onClick = { isDoubleTap = false }) { Text("OK") } }
        )

        // Vibrates the phone on double tap. If you need :)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Background
    Box(
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds // Ensures the image covers the available space
        )
    }

    // View
    Column(
        Modifier.pointerInput(Unit) {
                detectTapGestures (
                    onTap = { isTap = true },
                    onDoubleTap = {isDoubleTap = true}
//                    onDoubleTap = {},
//                    onLongPress = {}
                )
            }
    ) {
        PauseComponent(navController)
        GameBuilding()
        
    }
}

@Composable
fun GameBuilding() {

    Canvas(modifier = Modifier
        .size(100.dp)
        .padding(20.dp)
    ) {
    }
}

@Composable
fun PauseComponent(navController: NavController){

    var isPause by remember { mutableStateOf(false) }
    var isPauseButtonActive by remember { mutableStateOf(false) }


    if (isPause) {
        AlertDialog(
            onDismissRequest = { isPause = false; isPauseButtonActive = false; },
            title = { Text("Game Paused!") },
            dismissButton = { Button(onClick = { isPause = false; isPauseButtonActive = false; }) { Text("Continue") } },
            confirmButton = { Button(onClick = { isPause = false; navController.navigate(Screen.TitleScreen.route) }) { Text("I QUIT!") } }
        )

    }

    val interactionSource = remember { MutableInteractionSource() }

    val imageSource = when (isPauseButtonActive) {
        true -> com.example.stacker.R.drawable.pause_active
        else -> com.example.stacker.R.drawable.pause_inactive
    }


    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .size(80.dp)
            .padding(start = 20.dp, top = 30.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { isPauseButtonActive = true; isPause = true; },
            )
    ){
        Image(
            painter = painterResource(id = imageSource),
            contentDescription = "Pause",
            modifier = Modifier
                .fillMaxSize()


        )
    }
}