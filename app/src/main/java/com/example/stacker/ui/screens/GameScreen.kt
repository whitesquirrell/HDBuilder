package com.example.stacker.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stacker.Screen
import com.example.stacker.model.BuildingDetail
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.sqrt

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, context: Context){


    // Flags
    var pauseFlag: MutableState<Boolean> = remember { mutableStateOf(false) }

    var isTap by remember { mutableStateOf(false) }
//    var isDoubleTap by remember { mutableStateOf(false) }
    var playableAreaHeightDP by remember { mutableIntStateOf(0) }

    val vibrator = context.getSystemService(Vibrator::class.java)

    // Images

    // Images & Sound
    val backgroundImage = painterResource(id = com.example.stacker.R.drawable.lky_transparent)
    val blockDrop: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.block_drop)
    val towerCrash: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.tower_crash)

    // Game Variable
    var buildingX by remember { mutableIntStateOf(0) }
    var buildingY by remember { mutableIntStateOf(0) }
    var buildingAlpha by remember { mutableFloatStateOf(0.3f) } // Start with 50% transparency



    val localDensity = LocalDensity.current
    val haptic = LocalHapticFeedback.current

//    if (isDoubleTap) {
//        AlertDialog(
//            onDismissRequest = { isDoubleTap = false },
//            title = { Text("Ni Hao") },
//            text = { Text("You Double Tap") },
//            confirmButton = { Button(onClick = { isDoubleTap = false }) { Text("OK") } }
//        )
//
//        // Vibrates the phone on double tap. If you need :)
//        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//    }


    val SCREEN_WIDTH = getScreenWidth();
    val SCREEN_HEIGHT = getScreenHeight();
    val GRAVITATIONAL_CONSTANT = 10

    val BUILDING_BASE_HEIGHT = 1 * 58
    val BUILDING_BASE_WIDTH = 1 * 74
    val BUILDING_VAR_1_HEIGHT = 1 * 51
    val BUILDING_VAR_1_WIDTH = 1 * 72

//    val BUILDING_SIZE = 150
//    val BUILDING_INIT_DIFFICULTY = 50
    val BUILDING_INIT_X_OFFSET = 0
    val BUILDING_INIT_Y_OFFSET = BUILDING_VAR_1_HEIGHT * 2
    val BUILDING_DROP_STOP_Y_OFFSET = playableAreaHeightDP/3
//    val BUILDING_STACK_ADD_OFFSET = BUILDING_SIZE/4 + 8
//    val BUILDING_STACK_BASE_OFFSET = BUILDING_SIZE/10 + 2
    val BUILDING_STACK_ADD_OFFSET = 0
    val BUILDING_STACK_BASE_OFFSET = 0
    val BUILDING_X_OFFSET_LIMIT = SCREEN_WIDTH - BUILDING_BASE_WIDTH

    var buildingXOffset by remember { mutableIntStateOf(BUILDING_INIT_X_OFFSET) }
    var buildingYOffset by remember { mutableIntStateOf(BUILDING_INIT_Y_OFFSET) }
    var buildingRotation by remember { mutableFloatStateOf(0f) }

    var isMovingRight by remember { mutableStateOf(true) }
    var isBuildingDropping by remember { mutableStateOf(false) }
    var buildingMovementSpeed by remember { mutableIntStateOf(5) }

    var buildingDropSpeed by remember { mutableIntStateOf(1) }
    var buildingNumber by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var buildingDesign by remember { mutableIntStateOf(0) }

    val buildingStackList = remember { mutableStateListOf<BuildingDetail>(
        BuildingDetail(id = buildingNumber++, buildingXOffset = SCREEN_WIDTH / 2 - BUILDING_BASE_WIDTH / 2,
//        screenWidth/2-BUILDING_SIZE/2,
            buildingDesign = com.example.stacker.R.drawable.building_base_t,
            buildingHeight = BUILDING_BASE_HEIGHT,
            buildingWidth = BUILDING_BASE_WIDTH,
            buildingYOffset = 0
        ))};



    var stackHeight by remember {mutableIntStateOf(BUILDING_BASE_HEIGHT)}
//    buildingStackList.forEach {building -> stackHeight += building.buildingHeight }
    var stackOffset = SCREEN_HEIGHT - BUILDING_VAR_1_HEIGHT - stackHeight

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

//    val listState = rememberLazyListState()
    LazyColumn(
//        state = listState,
        modifier = Modifier
            .fillMaxWidth(),
//            .heightIn(max=(SCREEN_HEIGHT / 2).dp),
//            .height((BUILDING_SIZE * 3).dp)
//            .padding(top = (BUILDING_SIZE * 3).dp)
//            .heightIn((BUILDING_SIZE * 3).dp)
//            .background(Color.Red),
        verticalArrangement = Arrangement.Bottom,

        ) {

        itemsIndexed(buildingStackList.reversed()) { _, item ->
            BuildingImage(
                buildingXOffset = item.buildingXOffset,
                buildingYOffset = BUILDING_STACK_BASE_OFFSET + BUILDING_STACK_ADD_OFFSET * item.id + item.buildingYOffset,
                buildingHeight = BUILDING_VAR_1_HEIGHT,
                buildingWidth = BUILDING_VAR_1_WIDTH,
                buildingDesign = item.buildingDesign,
            )
        }

}
    Box(
    ){
        buildingDesign = com.example.stacker.R.drawable.building_1_t

        Image(
            modifier = Modifier
                .offset(x = buildingXOffset.dp, y = buildingYOffset.dp)
//                .size(BUILDING_SIZE.dp)
                .size(height = BUILDING_BASE_HEIGHT.dp, width = BUILDING_BASE_WIDTH.dp)
                .rotate(buildingRotation)
                .alpha(buildingAlpha)
                .padding(0.dp),
            contentDescription = null, // Set your content description here
            painter = painterResource(id = buildingDesign)
        )

        if (!pauseFlag.value && !isBuildingDropping){
            if (isMovingRight) {
                if (buildingXOffset < BUILDING_X_OFFSET_LIMIT) {
                    buildingXOffset += buildingMovementSpeed
                } else {
                    isMovingRight = false
                }
            } else {
                if (buildingXOffset > 0){
                    buildingXOffset -= buildingMovementSpeed
                } else {
                    isMovingRight = true
                }
            }
        }
    }

    // View
    Column(
        Modifier
            .onGloballyPositioned { coordinates ->
                playableAreaHeightDP = coordinates.size.height
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isBuildingDropping = true; blockDrop.start(); vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))

                    },
//                    onDoubleTap = { isDoubleTap = true; towerCrash.start() }
                )
            }
            .fillMaxWidth()
    ) {
        Row(
        ) {
            PauseComponent(navController, pauseFlag)
            Spacer(Modifier.weight(1f))
            Text(text = "Score\n${score}", modifier = Modifier
                .padding(end = 20.dp, top = 30.dp)
                .size(80.dp), textAlign = TextAlign.Right, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }

        if (pauseFlag.value || !isBuildingDropping) {

            return


        } else if (buildingYOffset < stackOffset) {

            buildingYOffset += calculateVelocity(buildingDropSpeed, GRAVITATIONAL_CONSTANT,
                buildingYOffset - BUILDING_INIT_Y_OFFSET)
            if (buildingYOffset > stackOffset) {
                buildingYOffset = stackOffset
            }
            return
        }

        if(!(buildingXOffset <=  ((buildingStackList[buildingStackList.size-1].buildingXOffset)+BUILDING_BASE_WIDTH/2) &&
                    buildingXOffset >= ((buildingStackList[buildingStackList.size-1].buildingXOffset)-BUILDING_BASE_WIDTH/2) )){

            //Simple Lose effect :)
            buildingRotation = 180f
            towerCrash.start()
            navController.navigate(Screen.RecordScoreScreen.route+"/${score}")

            return
        }

        // Down is positive offset
        buildingAlpha = 1f
        isBuildingDropping = false
        buildingYOffset = BUILDING_INIT_Y_OFFSET
        buildingDropSpeed = 1

        buildingStackList.add(
            BuildingDetail(
                id = buildingNumber++,
                buildingDesign = com.example.stacker.R.drawable.building_1_t,
                buildingXOffset = buildingXOffset,
                buildingYOffset = 0,
                buildingHeight = BUILDING_VAR_1_HEIGHT,
                buildingWidth = BUILDING_VAR_1_WIDTH
            )
        )
        score++
        buildingAlpha = 0.3f
        stackHeight += BUILDING_VAR_1_HEIGHT
        if (stackHeight > SCREEN_HEIGHT / 2) {
            var lowestBuilding = buildingStackList.removeFirst()
            stackHeight -= lowestBuilding.buildingHeight
        }
        if(buildingNumber % 5 == 0){
            buildingMovementSpeed += 1
        }
        buildingXOffset = (0..SCREEN_WIDTH-BUILDING_BASE_WIDTH).random()
    }
}

@Composable
fun BuildingImage(buildingXOffset: Int, buildingYOffset:Int,
                  buildingHeight: Int, buildingWidth: Int,
                  buildingDesign: Int){
    val painter = painterResource(buildingDesign)

    return Image(
        modifier = Modifier
            .absoluteOffset(x = buildingXOffset.dp, y = buildingYOffset.dp)
            .size(height = buildingHeight.dp, width = buildingWidth.dp),
//            .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height),
//            .background(Color.Black),
        contentDescription = null, // Set your content description here
        painter = painterResource(id = buildingDesign),
        contentScale = ContentScale.FillBounds
    )
}

fun calculateVelocity(initialVelocity: Int, acceleration: Int, distance: Int) : Int {
    return sqrt((initialVelocity * initialVelocity + 2 * acceleration * distance).toDouble()).toInt()
}

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

/*

//    AutoScrollingLazyColumn(list = buildingStackList.take(4)) {
//        getBuildingImage(
//            buildingXOffset = ${it.buildingXOffset},
//            buildingYOffset = ${it.id},
//            buildingSize = "${}",
//            buildingDesign = "${}"
//        )
//    }
//
//    @Composable
//    fun getLazyBuildingImage(buildingXOffset: Int, buildingYOffset:Int, buildingSize: Int, buildingDesign: Int){
//        val painter = painterResource(buildingDesign)
//
//        return Image(
//            modifier = Modifier
//                .absoluteOffset(x = buildingXOffset.dp, y = (BUILDING_STACK_BASE_OFFSET + BUILDING_STACK_ADD_OFFSET * buildingYOffset).dp)
//                .size(buildingSize.dp)
//                .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
//                .background(Color.Black),
//            contentDescription = null, // Set your content description here
//            painter = painterResource(id = buildingDesign),
//            contentScale = ContentScale.FillBounds
//        )
//
//    }

//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .offset(y = 0.dp),
//        verticalArrangement = Arrangement.Bottom
//    ){
//        getBuildingImage(
//            buildingXOffset = buildingStackList[1].buildingXOffset-50,
//            buildingYOffset = 5 + 53 * 1,
//            buildingSize = BUILDING_SIZE,
//            buildingDesign = buildingStackList[1].buildingDesign
//        )
//            getBuildingImage(
//                buildingXOffset = buildingStackList[0].buildingXOffset,
//                buildingYOffset = 5 + 50 * 0,
//                buildingSize = BUILDING_SIZE,
//                buildingDesign = buildingStackList[0].buildingDesign
//            )
//
//    }


//        items(items = buildingStackList.reversed(), key = {build -> build.id}) { item ->
//
//
//                getBuildingImage(
//                    buildingXOffset = item.buildingXOffset,
//                    buildingYOffset = BUILDING_STACK_BASE_OFFSET + BUILDING_STACK_ADD_OFFSET * item.id,
//                    buildingSize = BUILDING_SIZE,
//                    buildingDesign = item.buildingDesign
//                )
//
//
//
//        }


//        if (!isPause.value && isTap) {
//            isBuildingDropping = true
//            println("DROP")
//        }

//        if(buildingNumber == 0){
//            buildingDesign = com.example.stacker.R.drawable.building_base
//        }else{
//            buildingDesign = com.example.stacker.R.drawable.building_1
//        }

//        buildingDesign = com.example.stacker.R.drawable.building_1_t
//
//        Image(
//            modifier = Modifier
//                .offset(x = buildingXOffset.dp, y = buildingYOffset.dp)
////                .size(BUILDING_SIZE.dp)
//                .size(height=BUILDING_BASE_HEIGHT.dp, width=BUILDING_BASE_WIDTH.dp)
//                .padding(0.dp),
//            contentDescription = null, // Set your content description here
//            painter = painterResource(id = buildingDesign)
//        )



//        getBuildingImage(buildingXOffset, buildingYOffset, BUILDING_SIZE, com.example.stacker.R.drawable.building_1)
//        getBuildingImage(getScreenWidth()/2-BUILDING_SIZE, 200, BUILDING_SIZE, com.example.stacker.R.drawable.building_base)




//    Column(){
//        val painter = painterResource(buildingDesign)
//            Image(
//                modifier = Modifier
//                    .absoluteOffset(x = 50.dp, y = 0.dp)
//                    .absolutePadding(top = 0.dp, bottom = 0.dp)
//                    .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
//                    .size(50.dp)
//                    .background(Color.Black),
//                contentDescription = null, // Set your content description here
//                painter = painterResource(id = buildingDesign),
////                contentScale = ContentScale.Fit
//            )
//            Image(
//                modifier = Modifier
//                    .absoluteOffset(x = 50.dp, y = 0.dp)
//                    .absolutePadding(top = 0.dp, bottom = 0.dp)
//                    .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
//                    .size(50.dp)
//                    .background(Color.Black),
//                contentDescription = null, // Set your content description here
//                painter = painterResource(id = buildingDesign),
////                contentScale = ContentScale.Fit
//
//
//            )
//        Text(text = "HELLO")
//        Text(text = "HELLO")
//
//    }
 */