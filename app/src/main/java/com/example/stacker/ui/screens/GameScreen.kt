package com.example.stacker.ui.screens

import android.R
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.widget.ImageView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import com.example.stacker.Screen
import com.example.stacker.model.BuildingDetail
import java.time.format.TextStyle
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.example.autoscroll.AutoScrollingLazyColumn
import com.example.autoscroll.autoScroll
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, context: Context){



    var isTap by remember { mutableStateOf(false) }
    var isDoubleTap by remember { mutableStateOf(false) }
    var isPause: MutableState<Boolean> = remember { mutableStateOf(false) }
    var playableAreaHeightDP by remember { mutableIntStateOf(0) }

    // Images

    // Images & Sound
    val backgroundImage = painterResource(id = com.example.stacker.R.drawable.lky_transparent)
    val blockDrop: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.block_drop)
    val towerCrash: MediaPlayer = MediaPlayer.create(context, com.example.stacker.R.raw.tower_crash)

    // Game Variable
    var buildingX by remember { mutableIntStateOf(0) }
    var buildingY by remember { mutableIntStateOf(0) }


    val localDensity = LocalDensity.current
    val haptic = LocalHapticFeedback.current

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

    val BUILDING_SIZE = 150
    val BUILDING_INIT_DIFFICULTY = 50
    val BUILDING_INIT_X_OFFSET = 0
    val BUILDING_INIT_Y_OFFSET = 0
    val BUILDING_DROP_STOP_Y_OFFSET = playableAreaHeightDP/3
    val BUILDING_STACK_ADD_OFFSET = BUILDING_SIZE/4 + 8
    val BUILDING_STACK_BASE_OFFSET = BUILDING_SIZE/10

    var buildingXOffset by remember { mutableIntStateOf(BUILDING_INIT_X_OFFSET) }
    var buildingYOffset by remember { mutableIntStateOf(BUILDING_INIT_Y_OFFSET) }
    var isMovingRight by remember { mutableStateOf(true) }
    var isBuildingDropping by remember { mutableStateOf(false) }
    var buildingMovementSpeed by remember { mutableIntStateOf(5) }
    var buildingDropSpeed by remember { mutableIntStateOf(5) }
    var buildingNumber by remember { mutableIntStateOf(0) }
    var buildingDesign by remember { mutableIntStateOf(0) }
    var screenWidth = getScreenWidth();
    var screenHeight = getScreenHeight();
    val buildingStackList = remember { mutableStateListOf<BuildingDetail>(
        BuildingDetail(id = buildingNumber++, buildingXOffset = screenWidth/2-BUILDING_SIZE/2,
            buildingDesign = com.example.stacker.R.drawable.building_base_t))};


//    val listState = rememberLazyListState()



    LazyColumn(
//        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height((BUILDING_SIZE*3).dp)
            .padding(top = (BUILDING_SIZE*3).dp)
            .heightIn((BUILDING_SIZE*3).dp)
            .background(Color.Red),
        verticalArrangement = Arrangement.Bottom,

        ) {

        items(items = buildingStackList.reversed(), key = {build -> build.id}) { item ->


            if (item.id == 0) {
                getBuildingImage(
                    buildingXOffset = item.buildingXOffset,
                    buildingYOffset = BUILDING_STACK_BASE_OFFSET,
                    buildingSize = BUILDING_SIZE,
                    buildingDesign = item.buildingDesign
                )
            } else {
                getBuildingImage(
                    buildingXOffset = item.buildingXOffset,
                    buildingYOffset = BUILDING_STACK_BASE_OFFSET + BUILDING_STACK_ADD_OFFSET * item.id,
                    buildingSize = BUILDING_SIZE,
                    buildingDesign = item.buildingDesign
                )
            }


        }

//        itemsIndexed(buildingStackList.reversed()) { index, item ->
//
//            if (item.id == 0) {
//                getBuildingImage(
//                    buildingXOffset = item.buildingXOffset,
//                    buildingYOffset = BUILDING_STACK_BASE_OFFSET,
//                    buildingSize = BUILDING_SIZE,
//                    buildingDesign = item.buildingDesign
//                )
//            } else {
//                getBuildingImage(
//                    buildingXOffset = item.buildingXOffset,
//                    buildingYOffset = BUILDING_STACK_BASE_OFFSET + BUILDING_STACK_ADD_OFFSET * item.id,
//                    buildingSize = BUILDING_SIZE,
//                    buildingDesign = item.buildingDesign
//                )
//            }
//
//
//        }

}



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



    // View
    Column(
        Modifier
            .onGloballyPositioned { coordinates ->
                playableAreaHeightDP = coordinates.size.height
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { isBuildingDropping = true; blockDrop.start() },
                    onDoubleTap = { isDoubleTap = true; towerCrash.start() }
//                    onDoubleTap = {},
//                    onLongPress = {}
                )
            }
            .fillMaxWidth()
    ) {
        PauseComponent(navController, isPause)





        if(!isPause.value &&
            !isBuildingDropping &&
            isMovingRight &&
            buildingXOffset < getScreenWidth()-BUILDING_SIZE
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

        if(!isPause.value && isBuildingDropping && buildingYOffset <= (screenHeight/2)){
            buildingYOffset += buildingDropSpeed
        }else if(!isPause.value && isBuildingDropping){
            isBuildingDropping = false
            buildingYOffset = BUILDING_INIT_Y_OFFSET
            buildingStackList.add(
                BuildingDetail(
                    id = buildingNumber++,
                    buildingDesign = com.example.stacker.R.drawable.building_1_t,
                    buildingXOffset = buildingXOffset)
            )

        }

//        if (!isPause.value && isTap) {
//            isBuildingDropping = true
//            println("DROP")
//        }

//        if(buildingNumber == 0){
//            buildingDesign = com.example.stacker.R.drawable.building_base
//        }else{
//            buildingDesign = com.example.stacker.R.drawable.building_1
//        }


        buildingDesign = com.example.stacker.R.drawable.building_1_t

        Image(
            modifier = Modifier
                .offset(x = buildingXOffset.dp, y = buildingYOffset.dp)
                .size(BUILDING_SIZE.dp)
                .padding(0.dp),
            contentDescription = null, // Set your content description here
            painter = painterResource(id = buildingDesign)
        )



//        getBuildingImage(buildingXOffset, buildingYOffset, BUILDING_SIZE, com.example.stacker.R.drawable.building_1)
//        getBuildingImage(getScreenWidth()/2-BUILDING_SIZE, 200, BUILDING_SIZE, com.example.stacker.R.drawable.building_base)

    }

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


}

@Composable
fun getBuildingImage(buildingXOffset: Int, buildingYOffset:Int, buildingSize: Int, buildingDesign: Int){
    val painter = painterResource(buildingDesign)

    return Image(
        modifier = Modifier
            .absoluteOffset(x = buildingXOffset.dp, y = buildingYOffset.dp)
            .size(buildingSize.dp)
            .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
            .background(Color.Black),
        contentDescription = null, // Set your content description here
        painter = painterResource(id = buildingDesign),
        contentScale = ContentScale.FillBounds
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
