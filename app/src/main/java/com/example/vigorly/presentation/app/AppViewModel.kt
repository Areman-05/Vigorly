package com.example.vigorly.presentation.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** ViewModel de aplicación: mensajes globales (snackbar) y eventos transversales. */
class AppViewModel : ViewModel() {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun showMessage(text: String) {
        _messages.tryEmit(text)
    }
}
