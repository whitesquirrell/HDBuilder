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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import java.util.concurrent.locks.ReentrantLock
import kotlinx.coroutines.withContext
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object GameGlobals {
    const val GRAVITATIONAL_CONSTANT = 10

    const val BUILDING_BASE_HEIGHT =  58
    const val BUILDING_BASE_WIDTH =  74
    const val BUILDING_VAR_1_HEIGHT =  51
    const val BUILDING_VAR_1_WIDTH = 72

    const val BUILDING_INIT_X_OFFSET = 0
    const val BUILDING_INIT_Y_OFFSET = BUILDING_VAR_1_HEIGHT * 2
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, context: Context){
    // Constants
    val SCREEN_WIDTH = getScreenWidth();
    val SCREEN_HEIGHT = getScreenHeight();

    // Flags
    var pauseFlag: MutableState<Boolean> = remember { mutableStateOf(false) }
    var playableAreaHeightDP by remember { mutableIntStateOf(0) }
    val vibrator = context.getSystemService(Vibrator::class.java)

    // Images & Sound
    val backgroundImage = painterResource(id = com.example.stacker.R.drawable.lky_transparent)
    val blockDrop: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.block_drop)
    val towerCrash: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.tower_crash)

    var buildingAlpha by remember { mutableFloatStateOf(0.3f) } // Start with 50% transparency


    // Timer
    val coroutineScope = rememberCoroutineScope()
    val frameTimeMillis = 1000L / 60L  // For 60 FPS
    val countdownDuration = 10  // 10 seconds for the countdown
    var remainingTime by remember { mutableStateOf(countdownDuration) }

    val BUILDING_X_OFFSET_LIMIT = SCREEN_WIDTH - GameGlobals.BUILDING_BASE_WIDTH

    var buildingXOffset by remember { mutableIntStateOf(GameGlobals.BUILDING_INIT_X_OFFSET) }
    var buildingYOffset by remember { mutableIntStateOf(GameGlobals.BUILDING_INIT_Y_OFFSET) }
    var buildingRotation by remember { mutableFloatStateOf(0f) }

    var isMovingRight by remember { mutableStateOf(true) }
    var isBuildingDropping by remember { mutableStateOf(false) }
    var buildingMovementSpeed by remember { mutableIntStateOf(5) }

    var buildingDropSpeed by remember { mutableIntStateOf(1) }
    var buildingNumber by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var buildingDesign by remember { mutableIntStateOf(0) }

    val buildingStackList = remember { mutableStateListOf<BuildingDetail>(
        BuildingDetail(
            id = buildingNumber++,
            buildingXOffset = SCREEN_WIDTH / 2
                - GameGlobals.BUILDING_BASE_WIDTH / 2,
            buildingDesign = com.example.stacker.R.drawable.building_base_t,
            buildingHeight = GameGlobals.BUILDING_BASE_HEIGHT,
            buildingWidth = GameGlobals.BUILDING_BASE_WIDTH,
            buildingYOffset = 0
        ))};

    val buildingListLock = ReentrantLock()


    var stackHeight by remember {mutableIntStateOf(GameGlobals.BUILDING_BASE_HEIGHT)}
//    buildingStackList.forEach {building -> stackHeight += building.buildingHeight }
    var stackOffset = SCREEN_HEIGHT
        - GameGlobals.BUILDING_VAR_1_HEIGHT
        - stackHeight

    // Start the game loop
    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            var lastUpdateTime = System.currentTimeMillis()
            var elapsedTimeSinceLastUpdate = 0L
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = currentTime - lastUpdateTime
                lastUpdateTime = currentTime
                elapsedTimeSinceLastUpdate += deltaTime

                while (elapsedTimeSinceLastUpdate >= 1000) {
                    elapsedTimeSinceLastUpdate -= 1000
                    remainingTime--
                }

                if (remainingTime <= 0) {
                    isBuildingDropping = true
                    remainingTime = countdownDuration
                }

                val sleepTime = frameTimeMillis - deltaTime % frameTimeMillis
                if (sleepTime > 0) {
                    delay(sleepTime)
                }
            }
        }

        onDispose {
            job.cancel()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Time left: $remainingTime",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
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


    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,

        ) {

        itemsIndexed(buildingStackList.reversed()) { _, item ->
            BuildingImage(
                buildingXOffset = item.buildingXOffset,
                buildingYOffset = item.buildingYOffset,
                buildingHeight = GameGlobals.BUILDING_VAR_1_HEIGHT,
                buildingWidth = GameGlobals.BUILDING_VAR_1_WIDTH,
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
                .size(
                    height = GameGlobals.BUILDING_BASE_HEIGHT.dp,
                    width = GameGlobals.BUILDING_BASE_WIDTH.dp)
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
                        isBuildingDropping = true
                        blockDrop.start()
                        vibrator.vibrate(
                            VibrationEffect.createPredefined(
                                VibrationEffect.EFFECT_CLICK
                            )
                        )
                        remainingTime = countdownDuration

                    },
                )
            }
            .fillMaxWidth()
    ) {
        Row(
        ) {
            PauseComponent(navController, pauseFlag)
            Spacer(Modifier.weight(1f))
            Text(text = "Score\n${score}",
                modifier = Modifier
                    .padding(end = 20.dp, top = 30.dp)
                    .size(80.dp),
                textAlign =TextAlign.Right,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (pauseFlag.value || !isBuildingDropping) {
            return
        } else if (buildingYOffset < stackOffset) {
            buildingYOffset += calculateVelocity(
                initialVelocity = buildingDropSpeed,
                acceleration = GameGlobals.GRAVITATIONAL_CONSTANT,
                distance = buildingYOffset - GameGlobals.BUILDING_INIT_Y_OFFSET)
            if (buildingYOffset > stackOffset) {
                buildingYOffset = stackOffset
            }
            return
        }

        if(!(buildingXOffset <= ((buildingStackList[buildingStackList.size - 1].buildingXOffset)
                    + GameGlobals.BUILDING_BASE_WIDTH / 2) &&
                    buildingXOffset >= ((buildingStackList[buildingStackList.size - 1].buildingXOffset)
                    - GameGlobals.BUILDING_BASE_WIDTH / 2) )){

            //Simple Lose effect :)
            buildingRotation = 180f
            towerCrash.start()
            navController.navigate(Screen.RecordScoreScreen.route+"/${score}")

            return
        }

        fun addBuilding(building: BuildingDetail) {
            buildingListLock.lock()
            try {
                buildingStackList.add(building)
            } finally {
                buildingListLock.unlock()
            }
        }

        fun removeFirstBuilding(): BuildingDetail? {
            buildingListLock.lock()
            try {
                return if (buildingStackList.isNotEmpty()) {
                    stackHeight -= buildingStackList.first().buildingHeight
                    buildingStackList.removeFirst()
                } else {
                    null
                }
            } finally {
                buildingListLock.unlock()
            }
        }
        // Down is positive offset
        buildingAlpha = 1f
        isBuildingDropping = false
        buildingYOffset = GameGlobals.BUILDING_INIT_Y_OFFSET
        buildingDropSpeed = 1

        addBuilding(
            BuildingDetail(
                id = buildingNumber++,
                buildingDesign = com.example.stacker.R.drawable.building_1_t,
                buildingXOffset = buildingXOffset,
                buildingYOffset = 0,
                buildingHeight = GameGlobals.BUILDING_VAR_1_HEIGHT,
                buildingWidth = GameGlobals.BUILDING_VAR_1_WIDTH
            )
        )

        score++
        buildingAlpha = 0.3f
        stackHeight += GameGlobals.BUILDING_VAR_1_HEIGHT
        if (stackHeight > SCREEN_HEIGHT / 2) {
            removeFirstBuilding()
        }
        if(buildingNumber % 5 == 0){
            buildingMovementSpeed += 1
        }
        buildingXOffset = (0..SCREEN_WIDTH
                - GameGlobals.BUILDING_BASE_WIDTH).random()
    }
}

@Composable
fun BuildingImage(buildingXOffset: Int, buildingYOffset:Int,
                  buildingHeight: Int, buildingWidth: Int,
                  buildingDesign: Int){
    val painter = painterResource(buildingDesign)

    return Image(
        modifier = Modifier
            .absoluteOffset(
                x = buildingXOffset.dp,
                y = buildingYOffset.dp
            )
            .size(
                height = buildingHeight.dp,
                width = buildingWidth.dp
            ),
        contentDescription = null, // Set your content description here
        painter = painterResource(
            id = buildingDesign
        ),
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
            dismissButton = {
                Button(onClick = {
                isPause.value = false
                isPauseButtonActive = false
            }) {
                Text("Continue") }
            },
            confirmButton = {
                Button(onClick = {
                isPause.value = false
                navController.navigate(Screen.TitleScreen.route)
            }) {
                Text("I QUIT!") }
            }
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
            .padding(
                start = 20.dp,
                top = 30.dp
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    isPauseButtonActive = true
                    isPause.value = true
                  },
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