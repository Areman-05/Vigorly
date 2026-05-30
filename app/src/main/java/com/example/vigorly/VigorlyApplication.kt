package com.example.vigorly

import android.app.Application
import android.content.Context
import com.example.vigorly.data.repository.VigorlyRepository
import com.example.vigorly.util.LocaleUtils
import kotlinx.coroutines.runBlocking

class VigorlyApplication : Application() {
    lateinit var repository: VigorlyRepository
        private set

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtils.wrap(base, "es"))
    }

    override fun onCreate() {
        super.onCreate()
        repository = VigorlyRepository(this)
        runBlocking { repository.initializeLocale() }
    }
}
