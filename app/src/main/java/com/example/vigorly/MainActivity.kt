package com.example.vigorly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.vigorly.ui.VigorlyViewModel
import com.example.vigorly.ui.theme.VigorlyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: VigorlyViewModel by viewModels()
    private val repository by lazy { (application as VigorlyApplication).repository }

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
