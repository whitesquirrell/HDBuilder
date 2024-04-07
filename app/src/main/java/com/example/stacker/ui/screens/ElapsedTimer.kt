package com.example.stacker.ui.screens

class ElapsedTimer {
    var updateStartTime = System.currentTimeMillis()
    private var initialized = false

    fun progress(): Long {
        val now = System.currentTimeMillis()
        if (!initialized) {
            initialized = true
            updateStartTime = now
        }
        val delta = now - updateStartTime
        updateStartTime = now
        return delta
    }
}