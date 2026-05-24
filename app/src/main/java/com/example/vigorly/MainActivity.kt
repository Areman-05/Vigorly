package com.example.vigorly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vigorly.ui.theme.VigorlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as VigorlyApplication).repository
        enableEdgeToEdge()
        setContent {
            VigorlyTheme {
                VigorlyApp(repository = repository)
            }
        }
    }
}
