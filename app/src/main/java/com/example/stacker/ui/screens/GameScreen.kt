package com.example.stacker.ui.screens

import android.R
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import com.example.stacker.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, context: Context){


    var isTap by remember { mutableStateOf(false) }
    var isDoubleTap by remember { mutableStateOf(false) }
    var isPause: MutableState<Boolean> = remember { mutableStateOf(false) }

    // Images & Sound
    val backgroundImage = painterResource(id = com.example.stacker.R.drawable.lky_transparent)
    val blockDrop: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.block_drop)
    val towerCrash: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.tower_crash)

    // Game Variable
    var buildingX by remember { mutableIntStateOf(0) }
    var buildingY by remember { mutableIntStateOf(0) }



    val haptic = LocalHapticFeedback.current

//    if (isTap) {
//        AlertDialog(
//            onDismissRequest = { isTap = false },
//            title = { Text("Ni Hao") },
//            text = { Text("You tapped") },
//            confirmButton = { Button(onClick = { isTap = false }) { Text("OK") } }
//        )
//
//    }

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
                    onTap = {
                        blockDrop.start()
                        isTap = true },
                    onDoubleTap = {
                        towerCrash.start()
                        isDoubleTap = true}
//                    onDoubleTap = {},
//                    onLongPress = {}
                )
            }
    ) {
        PauseComponent(navController, isPause)

        val BUILDING_WIDTH = 100
        val BUILDING_INIT_DIFFICULTY = 50
        val BUILDING_INIT_X_OFFSET = 0
        val BUILDING_INIT_Y_OFFSET = 50
        val BUILDING_DROP_STOP_Y_OFFSET = getScreenHeight()/2

        var buildingXOffset by remember { mutableIntStateOf(BUILDING_INIT_X_OFFSET) }
        var buildingYOffset by remember { mutableIntStateOf(BUILDING_INIT_Y_OFFSET) }
        var isMovingRight by remember { mutableStateOf(true) }
        var isBuildingDropping by remember { mutableStateOf(false) }
        var buildingMovementSpeed by remember { mutableIntStateOf(5) }
        var buildingDropSpeed by remember { mutableIntStateOf(5) }



        if(!isPause.value &&
            !isBuildingDropping &&
            isMovingRight &&
            buildingXOffset < getScreenWidth()-BUILDING_WIDTH
            ){
            buildingXOffset += buildingMovementSpeed;
        }else if(!isPause.value &&
            !isBuildingDropping
            ){
            isMovingRight = false
        }

        if(!isPause.value &&
            !isBuildingDropping &&
            !isMovingRight &&
            buildingXOffset > 0
            ){
            buildingXOffset -= buildingMovementSpeed;
        }else if(!isPause.value &&
            !isBuildingDropping
            ){
            isMovingRight = true
        }

        if(!isPause.value && isBuildingDropping && buildingYOffset <= BUILDING_DROP_STOP_Y_OFFSET){
            buildingYOffset += buildingDropSpeed
        }else if(!isPause.value ){
            isBuildingDropping = false
        }

        if (!isPause.value && isTap) {
            isBuildingDropping = true
        }


        Image(
            modifier = Modifier
                .offset(x = buildingXOffset.dp, y = buildingYOffset.dp)
                .size(BUILDING_WIDTH.dp),
            contentDescription = null, // Set your content description here
            painter = painterResource(id = com.example.stacker.R.drawable.building_base)
        )

    }
}

//@Composable
//fun GameBuilding(nacController: NavController, isPause: MutableState<Boolean>) {
//
//
//
//}

@Composable
fun PauseComponent(navController: NavController, isPause: MutableState<Boolean>){

    var isPauseButtonActive by remember { mutableStateOf(false) }

    if (isPause.value) {
        AlertDialog(
            onDismissRequest = { isPause.value = false; isPauseButtonActive = false; },
            title = { Text("Game Paused!") },
            dismissButton = { Button(onClick = { isPause.value = false; isPauseButtonActive = false; }) { Text("Continue") } },
            confirmButton = { Button(onClick = { isPause.value = false; navController.navigate(Screen.TitleScreen.route) }) { Text("I QUIT!") } }
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
                onClick = { isPauseButtonActive = true; isPause.value = true; },
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

@Composable
fun getScreenHeight(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp
}
@Composable
fun getScreenWidth(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp
}
