package com.example.fuwalo

import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.*
import com.example.fuwalo.presentation.PianoScreen
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
@Preview
fun App() {
    MaterialTheme {
        PianoScreen()
    }
}