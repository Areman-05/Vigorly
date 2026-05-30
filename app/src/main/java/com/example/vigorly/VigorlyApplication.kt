package com.example.vigorly

import android.app.Application
import com.example.vigorly.data.repository.VigorlyRepository
import kotlinx.coroutines.runBlocking

class VigorlyApplication : Application() {
    lateinit var repository: VigorlyRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = VigorlyRepository(this)
        runBlocking { repository.initializeLocale() }
    }
}
