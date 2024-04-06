package com.example.stacker

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class BackgroundMusicService: Service() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.home)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(1.0f, 1.0f) //max volume
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        super.onDestroy()
    }

}