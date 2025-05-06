package com.example.fuwalo.presentation.keyboard
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FallingNote(
    val xOffset: Dp,          // Horizontal position
    val width: Dp,            // Width of the note
    val height: Dp,           // Height of the note (note duration)
    val color: Color = Color.Cyan,
    val delayMs: Int = 0      // Optional delay before falling starts
)

@Composable
fun FallingNotesScreen(
    notes: List<FallingNote>,
    fallDuration: Int = 3000,  // Total duration from top to bottom
    bottomOffset: Dp = 0.dp    // How far from bottom it should stop (e.g., keyboard top)
) {
    BoxWithConstraints(modifier = Modifier.background(Color.Black)) {
        val screenHeight = maxHeight

        notes.forEach { note ->
            FallingNoteBlock(
                note = note,
                screenHeight = screenHeight,
                fallDuration = fallDuration,
                bottomOffset = bottomOffset
            )
        }
    }
}

@Composable
fun FallingNoteBlock(
    note: FallingNote,
    screenHeight: Dp,
    fallDuration: Int,
    bottomOffset: Dp
) {
    val yPos = remember { Animatable(-note.height.value) }

    LaunchedEffect(Unit) {
        delay(note.delayMs.toLong()) // Optional delay before falling
        yPos.animateTo(
            targetValue = (screenHeight - bottomOffset).value,
            animationSpec = tween(durationMillis = fallDuration)
        )
    }

    Box(
        modifier = Modifier
            .offset(x = note.xOffset, y = yPos.value.dp)
            .width(note.width)
            .height(note.height)
            .background(note.color, shape = MaterialTheme.shapes.small)
    )
}
