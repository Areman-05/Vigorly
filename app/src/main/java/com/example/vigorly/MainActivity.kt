package com.example.vigorly

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.vigorly.di.AppViewModelFactory
import com.example.vigorly.ui.VigorlyViewModel
import com.example.vigorly.ui.theme.VigorlyTheme
import com.example.vigorly.util.LocaleUtils
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private val repository by lazy { (application as VigorlyApplication).repository }
    private val viewModelFactory by lazy { AppViewModelFactory(repository) }
    private val viewModel: VigorlyViewModel by viewModels { viewModelFactory }

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
        setContent {
            VigorlyTheme {
                VigorlyApp(repository = repository, viewModel = viewModel)
            }
        }
    }
}
