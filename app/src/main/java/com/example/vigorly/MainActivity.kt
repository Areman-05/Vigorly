package com.example.vigorly

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.presentation.app.AppViewModel
import com.example.vigorly.ui.theme.VigorlyTheme
import com.example.vigorly.util.ActivityPermission
import com.example.vigorly.util.LocaleUtils
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private val repository by lazy { (application as VigorlyApplication).repository }
    private val viewModelFactory by lazy { AppViewModelFactory(repository) }
    private val appViewModel: AppViewModel by viewModels { viewModelFactory }

    private val activityRecognitionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) repository.startActivityTracking()
    }

    override fun attachBaseContext(newBase: Context) {
        val locale = runBlocking {
            try {
                (newBase.applicationContext as VigorlyApplication).repository.effectiveLocale()
            } catch (_: UninitializedPropertyAccessException) {
                "es"
            } catch (_: Exception) {
                "es"
            }
        }
        super.attachBaseContext(LocaleUtils.wrap(newBase, locale))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestActivityRecognitionIfNeeded()
        setContent {
            VigorlyTheme {
                VigorlyApp(repository = repository, appViewModel = appViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityPermission.hasRecognitionPermission(this)) {
            repository.startActivityTracking()
        }
    }

    override fun onPause() {
        repository.stopActivityTracking()
        super.onPause()
    }

    private fun requestActivityRecognitionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !ActivityPermission.hasRecognitionPermission(this)
        ) {
            activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }
}
