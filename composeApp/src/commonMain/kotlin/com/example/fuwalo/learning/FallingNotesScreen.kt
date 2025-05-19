package com.example.fuwalo.learning

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.fuwalo.core.utils.Util
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.random.Random

// Data class to hold the properties of a single falling note.
// It's marked @Stable for Compose performance optimization.
@Stable
data class NoteProperties(
    val id: String = UUID.randomUUID().toString(), // Unique identifier for each note
    val xOffsetRatio: Float, // Horizontal position as a fraction of screen width (0.0f to 1.0f)
    val width: Dp,   // Width of the note as a fraction of screen width
    val heightRatio: Float,  // Height (visual length) of the note as a fraction of screen height
    val fallDurationMillis: Long, // Time in milliseconds for the note to fall across the screen
    val color: Color = Color( // Random color for the note
        Random.nextInt(128, 256), // Brighter colors
        Random.nextInt(128, 256),
        Random.nextInt(128, 256),
        255
    )
)

/**
 * Main Composable for the falling notes animation.
 * This Composable manages the state of all falling notes and launches new notes periodically.
 */
@Composable
fun FallingNotesAnimationScreen(modifier: Modifier) {
    // A mutable state list to hold the properties of currently visible notes.
    // Changes to this list will trigger recomposition.
    val notes = remember { mutableStateListOf<NoteProperties>() }

    // BoxWithConstraints provides the actual width and height of the available space,
    // allowing the animation to be responsive.
    BoxWithConstraints(
        modifier = modifier
            .background(Color(0xFF202020)) // Dark background for better contrast
    ) {
        // Convert maxHeight and maxWidth from Dp to Px for precise calculations
        val screenHeightPx = with(LocalDensity.current) { maxHeight.toPx() }
        val screenWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

        // LaunchedEffect to continuously generate new notes.
        // Keyed on Unit to run once when the Composable enters the composition and stop when it leaves.
        LaunchedEffect(Unit) {
            while (isActive) { // isActive is from the coroutine scope of LaunchedEffect
                // Wait for a random duration before launching the next note
                delay(Random.nextLong(300, 1200)) // Between 0.3 to 1.2 seconds

                // Create a new note with random properties
                val newNote = NoteProperties(
                    xOffsetRatio = Random.nextFloat(), // Random horizontal start (0.0 to 1.0)
                    // Note width: 5% to 15% of screen width
                    width = Util.WHITE_KEY_WIDTH,
                    // Note height (length): 5% to 20% of screen height
                    heightRatio = (Random.nextFloat() * 0.15f + 0.05f).coerceIn(0.05f, 0.20f),
                    // Fall duration: 3 to 6 seconds
                    fallDurationMillis = Random.nextLong(3000, 6000)
                )
                // Add the new note to the list. This is safe as LaunchedEffect runs on the main dispatcher.
                notes.add(newNote)
            }
        }

        // Iterate through the list of notes and display each one.
        // Using note.id as a key helps Compose optimize recompositions if the list changes order,
        // though for forEach it's mainly for the LaunchedEffect within FallingNoteItem.
        notes.forEach { noteProperties ->
            key(noteProperties.id) { // Ensure LaunchedEffect inside FallingNoteItem restarts for new notes
                FallingNoteItem(
                    properties = noteProperties,
                    parentWidthPx = screenWidthPx,
                    parentHeightPx = screenHeightPx,
                    onFinished = {
                        // Callback when a note's animation finishes. Remove it from the list.
                        notes.remove(noteProperties)
                    }
                )
            }
        }
    }
}

/**
 * Composable responsible for rendering and animating a single falling note.
 *
 * @param properties The [NoteProperties] defining the note's appearance and behavior.
 * @param parentWidthPx The width of the parent container in pixels.
 * @param parentHeightPx The height of the parent container in pixels.
 * @param onFinished Lambda to be called when the note's falling animation is complete.
 */
@Composable
fun FallingNoteItem(
    properties: NoteProperties,
    parentWidthPx: Float,
    parentHeightPx: Float,
    onFinished: () -> Unit
) {
    // Calculate actual pixel dimensions and offset for the note
   // val noteWidthPx = properties.width
    val noteHeightPx = parentHeightPx * properties.heightRatio

    // Calculate the X offset, ensuring the note stays within the parent's horizontal bounds.
    // The xOffsetRatio determines the position of the note's leading edge.
    val maxPossibleXOffsetPx = parentWidthPx - properties.width.value.toFloat()
    val noteXOffsetPx = (properties.xOffsetRatio * maxPossibleXOffsetPx).coerceAtLeast(0f)

    // Animatable for the Y position of the note.
    // Starts at -noteHeightPx (just above the screen)
    val animatedY = remember { Animatable(-noteHeightPx) }

    // LaunchedEffect to run the animation when the note (identified by properties.id) enters composition.
    LaunchedEffect(properties.id) {
        // Animate the Y position from its initial value to parentHeightPx (bottom of the screen).
        animatedY.animateTo(
            targetValue = parentHeightPx,
            animationSpec = tween(
                durationMillis = properties.fallDurationMillis.toInt(),
                easing = LinearEasing // Constant falling speed
            )
        )
        // Once the animation is complete, call the onFinished lambda.
        onFinished()
    }

    // A simple Box to represent the falling note.
    Box(
        modifier = Modifier
            .offset { // Apply the calculated X offset and animated Y offset.
                IntOffset(
                    x = noteXOffsetPx.roundToInt(),
                    y = animatedY.value.roundToInt()
                )
            }
            .size( // Set the size of the note.
                width = Util.WHITE_KEY_WIDTH,
                height = with(LocalDensity.current) { noteHeightPx.toDp() }
            )
            .background(properties.color) // Apply the note's color.
            .border(1.dp, Color.Black.copy(alpha = 0.2f)) // Optional: add a subtle border.
    )
}


