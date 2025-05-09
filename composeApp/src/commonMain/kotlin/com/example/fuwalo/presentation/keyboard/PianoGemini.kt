package com.example.fuwalo.presentation.keyboard

import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow // Optional: for visual feedback
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.interaction.MutableInteractionSource // Needed if you want to track press state easily
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.unit.Dp
import com.example.fuwalo.core.utils.Util.BLACK_KEY_HEIGHT
import com.example.fuwalo.core.utils.Util.BLACK_KEY_WIDTH
import com.example.fuwalo.core.utils.Util.WHITE_KEY_HEIGHT
import com.example.fuwalo.core.utils.Util.WHITE_KEY_WIDTH
import com.example.fuwalo.core.utils.Util.getBlackKeyOffSet
import com.example.fuwalo.data.PianoKey
import com.example.fuwalo.data.generatePianoKeys

// Assuming you have a data class like this for your keys

// You'll need to implement generatePianoKeys() to create a list of 88 PianoKey objects

// Define your key dimensions (replace with actual values)
// Example

// Implement your note playing logic (needs to handle start and stop)
// Example stubs:
fun startNote(midi: Int) {
    // Your sound playing logic to start the note corresponding to midi
    println("Start Note: $midi") // For demonstration
}

fun stopNote(midi: Int) {
    // Your sound playing logic to stop the note corresponding to midi
    println("Stop Note: $midi") // For demonstration
}

// Helper function for black key offset (replace with your logic)


@Composable
fun WhiteKey(
    pianoKey: PianoKey,
    onKeyPress: (Int) -> Unit, // Lambda to call when key is pressed down
    onKeyRelease: (Int) -> Unit, // Lambda to call when key is released
    modifier: Modifier = Modifier // Modifier should be the last parameter
) {
    // State to track if the key is currently being pressed for visual feedback
    var isPressed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .width(WHITE_KEY_WIDTH)
            .height(WHITE_KEY_HEIGHT)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            // Change background/appearance based on pressed state
            .background(if (isPressed) Color(0xFFD0E6FF) else MaterialTheme.colorScheme.background)
            // Use pointerInput for multi-touch handling
            .pointerInput(pianoKey.midi) { // Key the pointerInput on the specific key's MIDI value
                awaitPointerEventScope { // Scope for handling pointer events
                    while (true) { // Loop to continuously listen for events
                        val event = awaitPointerEvent() // Wait for the next pointer event

                        // Check all changes in the current event
                        event.changes.forEach { change ->
                            // If a pointer went down within this Composable's bounds
                            if (change.changedToDown()) {
                                isPressed = true // Set state to pressed
                                onKeyPress(pianoKey.midi) // Trigger the press action
                                change.consume() // Consume the event so it's not passed further
                            }
                            // If a pointer went up (that was previously down somewhere)
                            if (change.changedToUp()) {
                                // Note: This will trigger for *any* pointer lifting.
                                // A more complex implementation might track individual pointer IDs.
                                // For a basic piano, triggering release on any up event is often sufficient,
                                // assuming your sound engine can handle stopping a note that might already be stopped.
                                isPressed = false // Set state to not pressed
                                onKeyRelease(pianoKey.midi) // Trigger the release action
                                change.consume() // Consume the event
                            }
                        }
                        // Optional: wait for all pointers to be up if you need a different behavior
                        // waitForUpOrCancellation()
                        // isPressed = false // Ensure state is false after all pointers are up
                    }
                }
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Your existing note label Box and Text
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(Color(0xFFD0E6FF).copy(alpha = if (isPressed) 0.8f else 0.5f), shape = RoundedCornerShape(8.dp)) // Visual feedback on label too
                .padding(horizontal = 8.dp, vertical = 4.dp)
            // Removed the inner clickable here as the pointerInput on the parent Column handles the touch
        ) {
            Text(
                text = pianoKey.note,
                color = Color.DarkGray,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// BlackKey Composable will be very similar, adjusting dimensions, color, and potentially shape
@Composable
fun BlackKey(
    pianoKey: PianoKey,
    onKeyPress: (Int) -> Unit,
    onKeyRelease: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .width(BLACK_KEY_WIDTH)
            .height(BLACK_KEY_HEIGHT)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            // Black key colors
            .background(if (isPressed) Color.DarkGray else Color.Black)
            // Use pointerInput for multi-touch
            .pointerInput(pianoKey.midi) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.changedToDown()) {
                                isPressed = true
                                onKeyPress(pianoKey.midi)
                                change.consume()
                            }
                            if (change.changedToUp()) {
                                isPressed = false
                                onKeyRelease(pianoKey.midi)
                                change.consume()
                            }
                        }
                    }
                }
            },
        // Black keys might not have a label like white keys, adjust arrangement/alignment as needed
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Potentially add a smaller label or indicator for black keys if needed
    }
}


// Example of updating a PianoKeyboard Composable (e.g., Four Keys)
@Composable
fun PianoKeyboardFourKeysGemini(
    firstWhite: PianoKey, firstBlack: PianoKey, secondWhite: PianoKey, secondBlack: PianoKey, thirdWhite: PianoKey, thirdBlack: PianoKey, fourthWhite: PianoKey,
    onKeyPress: (Int) -> Unit, // Accept callbacks
    onKeyRelease: (Int) -> Unit // Accept callbacks
) {
    Surface(
        color = Color(0xFFEFEFFF), // Or remove Surface if you handle padding/background in the parent
        modifier = Modifier.padding(horizontal = 0.dp) // Adjust padding as needed to make keys touchable
    ) {
        Box {
            // White keys: C4 and D4
            Row {
                WhiteKey(pianoKey = firstWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease) // Pass callbacks down
                WhiteKey(pianoKey = secondWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
                WhiteKey(pianoKey = thirdWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
                WhiteKey(pianoKey = fourthWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
            }

            // Black keys positioned on top
            // Ensure BlackKeys are layered correctly and their modifiers/offsets are correct
            // to align them visually and for touch detection.
            // Use absolute positioning or a ZStack/Box with alignment if needed.
            // The current offset approach might be okay if the Box handles layering.
            BlackKey(pianoKey = firstBlack, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease, modifier = Modifier.offset(x = getBlackKeyOffSet(1))) // Pass callbacks
            BlackKey(pianoKey = secondBlack, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease, modifier = Modifier.offset(x = getBlackKeyOffSet(2)))
            BlackKey(pianoKey = thirdBlack, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease, modifier = Modifier.offset(x = getBlackKeyOffSet(3)))
        }
    }
}

@Composable
fun PianoKeyboardThreeKeysGemini(
    firstWhite: PianoKey, firstBlack: PianoKey, secondWhite: PianoKey, secondBlack: PianoKey, thirdWhite: PianoKey,
    onKeyPress: (Int) -> Unit, // Accept callbacks
    onKeyRelease: (Int) -> Unit // Accept callbacks
) {
    Surface(
        color = Color(0xFFEFEFFF), // Or remove Surface if you handle padding/background in the parent
        modifier = Modifier.padding(horizontal = 0.dp) // Adjust padding as needed to make keys touchable
    ) {
        Box {
            // White keys: C4 and D4
            Row {
                WhiteKey(pianoKey = firstWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease) // Pass callbacks down
                WhiteKey(pianoKey = secondWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
                WhiteKey(pianoKey = thirdWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
            }

            // Black keys positioned on top
            // Ensure BlackKeys are layered correctly and their modifiers/offsets are correct
            // to align them visually and for touch detection.
            // Use absolute positioning or a ZStack/Box with alignment if needed.
            // The current offset approach might be okay if the Box handles layering.
            BlackKey(pianoKey = firstBlack, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease, modifier = Modifier.offset(x = getBlackKeyOffSet(1))) // Pass callbacks
            BlackKey(pianoKey = secondBlack, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease, modifier = Modifier.offset(x = getBlackKeyOffSet(2)))
        }
    }
}

@Composable
fun PianoKeyboardTwoKeysGemini(
    firstWhite: PianoKey, firstBlack: PianoKey, secondWhite: PianoKey,
    onKeyPress: (Int) -> Unit, // Accept callbacks
    onKeyRelease: (Int) -> Unit // Accept callbacks
) {
    Surface(
        color = Color(0xFFEFEFFF), // Or remove Surface if you handle padding/background in the parent
        modifier = Modifier.padding(horizontal = 0.dp) // Adjust padding as needed to make keys touchable
    ) {
        Box {
            // White keys: C4 and D4
            Row {
                WhiteKey(pianoKey = firstWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease) // Pass callbacks down
                WhiteKey(pianoKey = secondWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
            }

            // Black keys positioned on top
            // Ensure BlackKeys are layered correctly and their modifiers/offsets are correct
            // to align them visually and for touch detection.
            // Use absolute positioning or a ZStack/Box with alignment if needed.
            // The current offset approach might be okay if the Box handles layering.
            BlackKey(pianoKey = firstBlack, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease, modifier = Modifier.offset(x = getBlackKeyOffSet(1))) // Pass callbacks
        }
    }
}

@Composable
fun PianoKeyboardOneKeyGemini(
    firstWhite: PianoKey,
    onKeyPress: (Int) -> Unit, // Accept callbacks
    onKeyRelease: (Int) -> Unit // Accept callbacks
) {
    Surface(
        color = Color(0xFFEFEFFF), // Or remove Surface if you handle padding/background in the parent
        modifier = Modifier.padding(horizontal = 0.dp) // Adjust padding as needed to make keys touchable
    ) {
        Box {
            // White keys: C4 and D4
            Row {
                WhiteKey(pianoKey = firstWhite, onKeyPress = onKeyPress, onKeyRelease = onKeyRelease) // Pass callbacks down
            }

            // Black keys positioned on top
            // Ensure BlackKeys are layered correctly and their modifiers/offsets are correct
            // to align them visually and for touch detection.
            // Use absolute positioning or a ZStack/Box with alignment if needed.
            // The current offset approach might be okay if the Box handles layering.
        }
    }
}

// Update the main EightyEightKeysPiano Composable to accept the press/release actions
@Composable
fun EightyEightKeysPianoGemini(
    modifier: Modifier = Modifier, // Use the passed modifier
    onKeyPress: (Int) -> Unit, // Accept key press action
    onKeyRelease: (Int) -> Unit // Accept key release action
){
    val pianoKeys = remember { generatePianoKeys() } // Implement this function

    Row(modifier = modifier){
        // Pass the onKeyPress and onKeyRelease lambdas to all child keyboard Composables
        PianoKeyboardTwoKeysGemini(pianoKeys[0],pianoKeys[1], pianoKeys[2], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
        PianoKeyboardThreeKeysGemini(pianoKeys[3],pianoKeys[4], pianoKeys[5], pianoKeys[6],pianoKeys[7], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardFourKeysGemini(pianoKeys[8],pianoKeys[9], pianoKeys[10], pianoKeys[11],pianoKeys[12], pianoKeys[13],pianoKeys[14], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardThreeKeysGemini(pianoKeys[15],pianoKeys[16], pianoKeys[17], pianoKeys[18],pianoKeys[19], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardFourKeysGemini(pianoKeys[20],pianoKeys[21], pianoKeys[22], pianoKeys[23],pianoKeys[24], pianoKeys[25],pianoKeys[26], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardThreeKeysGemini(pianoKeys[27],pianoKeys[28], pianoKeys[29], pianoKeys[30],pianoKeys[31], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardFourKeysGemini(pianoKeys[32],pianoKeys[33], pianoKeys[34], pianoKeys[35],pianoKeys[36], pianoKeys[37],pianoKeys[38], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardThreeKeysGemini(pianoKeys[39],pianoKeys[40], pianoKeys[41], pianoKeys[42],pianoKeys[43], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardFourKeysGemini(pianoKeys[44],pianoKeys[45], pianoKeys[46], pianoKeys[47],pianoKeys[48], pianoKeys[49],pianoKeys[50], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardThreeKeysGemini(pianoKeys[51],pianoKeys[52], pianoKeys[53], pianoKeys[54],pianoKeys[55], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardFourKeysGemini(pianoKeys[56],pianoKeys[57], pianoKeys[58], pianoKeys[59],pianoKeys[60], pianoKeys[61],pianoKeys[62], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardThreeKeysGemini(pianoKeys[63],pianoKeys[64], pianoKeys[65], pianoKeys[66],pianoKeys[67], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardFourKeysGemini(pianoKeys[68],pianoKeys[69], pianoKeys[70], pianoKeys[71],pianoKeys[72], pianoKeys[73],pianoKeys[74], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardThreeKeysGemini(pianoKeys[75],pianoKeys[76], pianoKeys[77], pianoKeys[78],pianoKeys[79], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease )
        PianoKeyboardFourKeysGemini(pianoKeys[80],pianoKeys[81], pianoKeys[82], pianoKeys[83],pianoKeys[84], pianoKeys[85],pianoKeys[86], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
        PianoKeyboardOneKeyGemini(pianoKeys[87], onKeyPress = onKeyPress, onKeyRelease = onKeyRelease)
    }
}

// You will need to implement PianoKeyboardTwoKeys, PianoKeyboardThreeKeys, and PianoKeyboardOneKey similarly,
// ensuring they accept onKeyPress and onKeyRelease lambdas and pass them down to their
// WhiteKey and BlackKey children.

// Example usage in your Activity or main Composable:
/*
@Composable
fun MyApp() {
    // ... your app setup ...
    EightyEightKeysPiano(
        onKeyPress = { midiNote ->
            // Call your function to START playing the note
            startNote(midiNote)
        },
        onKeyRelease = { midiNote ->
            // Call your function to STOP playing the note
            stopNote(midiNote)
        }
    )
    // ... rest of your app ...
}
*/