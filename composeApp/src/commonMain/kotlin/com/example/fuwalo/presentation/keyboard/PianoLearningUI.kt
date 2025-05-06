package com.example.fuwalo.presentation.keyboard

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PianoLearningUI() {
    Column() {
        val notes = listOf(
            FallingNote(xOffset = 0.dp, width = 40.dp, height = 100.dp, delayMs = 0),
            FallingNote(xOffset = 60.dp, width = 40.dp, height = 150.dp, delayMs = 500),
            FallingNote(xOffset = 120.dp, width = 40.dp, height = 80.dp, delayMs = 1000)
        )

        FallingNotesScreen(notes = notes, bottomOffset = 100.dp)
        EightyEightKeysPiano(modifier = Modifier)
    }
}