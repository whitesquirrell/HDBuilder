package com.example.stacker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.stacker.ui.theme.StackTheBlockTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadPool {
    private val pool : ExecutorService = Executors.newFixedThreadPool(4)

    fun execute(func: Runnable) {
        pool.execute(func)
    }

    fun shutdown() {
        pool.shutdown()
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        ThreadPool.execute(BGMThread(this))

//        startBackgroundMusicService()
        setContent {
            StackTheBlockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(context = this)
                }
            }
        }
    }
//    private fun startBackgroundMusicService() {
//        val serviceIntent = Intent(this, BackgroundMusicService::class.java)
//        startService(serviceIntent)
//    }

    override fun onDestroy() {
        super.onDestroy()
        ThreadPool.shutdown()
//        stopBackgroundMusicService()
    }

    override fun onStop() {
        super.onStop()
        ThreadPool.shutdown()
//        stopBackgroundMusicService()
    }

//    private fun stopBackgroundMusicService() {
//        val serviceIntent = Intent(this, BackgroundMusicService::class.java)
//        stopService(serviceIntent)
//    }
}

class BGMThread(private val main : ComponentActivity) : Runnable{
    override fun run() {
        val serviceIntent = Intent(main, BackgroundMusicService::class.java)
        main.startService(serviceIntent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StackTheBlockTheme {
        Greeting("Android")
    }
}