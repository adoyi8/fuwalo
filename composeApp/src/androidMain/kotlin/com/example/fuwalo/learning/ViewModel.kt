//package com.example.fuwalo.learning
//
//
//import android.content.Context
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.fuwalo.core.utils.Util
//import com.example.fuwalo.data.PianoKey
//import com.example.fuwalo.data.generatePianoKeys
//import com.example.fuwalo.data.playNote // Assuming your playNote is accessible
//import com.example.fuwalo.data.releaseNote // Assuming your releaseNote is accessible
//
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.android.awaitFrame
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//import java.io.InputStream
//
//// Constants for the falling note animation
//const val FALL_AREA_HEIGHT_DP = 300 // Height of the area where notes fall
//const val NOTE_APPEAR_OFFSET_MILLIS = 3000L // Notes appear 3s before they should be played
//const val PIXELS_PER_SECOND = 150f // Speed of falling notes (adjust for desired speed)
//const val NOTE_MIN_VISUAL_HEIGHT_PX = 20f // Minimum visual height for very short notes
//const val NOTE_MAX_VISUAL_HEIGHT_PX = 300f // Maximum visual height for long notes
//const val DURATION_TO_HEIGHT_SCALE_FACTOR = 0.2f // Adjust to scale duration to visual height
//
//class PianoLearningViewModel : ViewModel() {
//
//    val allPianoKeys: List<PianoKey> = generatePianoKeys()
//    private val keyXOffsetMap: Map<Int, Pair<Dp, Dp>> = calculateKeyXOffsetsAndWidths(allPianoKeys) // Midi -> (X Offset, Width)
//
//    val fallingNotes = mutableStateListOf<FallingNoteInfo>()
//    val isPlaying = mutableStateOf(false)
//    val currentSongTimeMillis = mutableStateOf(0L)
//
//    private var animationJob: Job? = null
//    private var notePlaybackJob: Job? = null
//    private val activeAutoPlayedNotes = mutableMapOf<Int, Job>() // MidiNote -> Job for releasing the note
//
//    // Pre-calculate X offsets and widths for all keys
//    private fun calculateKeyXOffsetsAndWidths(pianoKeys: List<PianoKey>): Map<Int, Pair<Dp, Dp>> {
//        val map = mutableMapOf<Int, Pair<Dp, Dp>>()
//        var currentWhiteKeyX = 0.dp
//        val whiteKeyIndices = pianoKeys.withIndex().filter { !it.value.isBlack }.map { it.index }
//
//        pianoKeys.forEachIndexed { index, key ->
//            if (!key.isBlack) {
//                map[key.midi] = Pair(currentWhiteKeyX, Util.WHITE_KEY_WIDTH)
//                currentWhiteKeyX += Util.WHITE_KEY_WIDTH
//            } else {
//                // Find the previous white key's X position to base the black key's X on
//                // This is a simplified positioning. A more robust solution might involve
//                // knowing the exact black key pattern (2-3-2-3...)
//                // For this example, we'll place it relative to the *logical* previous white key.
//                // A common way is to find the white key it's "attached" to.
//                // C# is after C, D# is after D, etc.
//                // The key.midi - 1 should be the white key it's related to for sharps.
//                val relatedWhiteKeyMidi = key.midi -1
//                val relatedWhiteKeyInfo = map[relatedWhiteKeyMidi]
//
//                if (relatedWhiteKeyInfo != null) {
//                    // Position black key towards the right edge of the related white key
//                    val blackKeyX = relatedWhiteKeyInfo.first + Util.WHITE_KEY_WIDTH - (Util.BLACK_KEY_WIDTH / 2f) - (Util.WHITE_KEY_WIDTH * 0.15f) // Adjust centering
//                    map[key.midi] = Pair(blackKeyX, Util.BLACK_KEY_WIDTH)
//                } else {
//                    // Fallback for A0's black key if needed, or if logic is incomplete
//                    // For simplicity, we'll assume MIDI structure where black keys always have a preceding white key MIDI
//                    map[key.midi] = Pair(currentWhiteKeyX, Util.BLACK_KEY_WIDTH) // Fallback, less accurate
//                }
//            }
//        }
//        return map
//    }
//
//
//    fun loadMidiFile(context: Context, rawResourceId: Int) {
//        viewModelScope.launch {
//            fallingNotes.clear()
//            currentSongTimeMillis.value = 0L
//            val inputStream: InputStream = context.resources.openRawResource(rawResourceId)
//            val midiFile = MidiFile(inputStream)
//            val TicksPerQuarterNote = midiFile.resolution
//            var microSecsPerQuarterNote = 500000L // Default tempo: 120 BPM
//
//            // Get the first track (often contains tempo and time signature)
//            // More robust parsing would iterate all tracks for Tempo events.
//            midiFile.tracks.firstOrNull()?.events?.forEach { event ->
//                if (event is Tempo) {
//                    microSecsPerQuarterNote = event.microsecondsPerQuarter.toLong()
//                }
//            }
//
//            var noteIdCounter = 0
//            midiFile.tracks.forEach { track ->
//                val activeNotes = mutableMapOf<Int, NoteOn>() // Pitch -> NoteOn event
//
//                track.events.forEach { event ->
//                    val eventTimeMillis = (event.tick * microSecsPerQuarterNote) / (TicksPerQuarterNote * 1000L)
//
//                    when (event) {
//                        is NoteOn -> {
//                            if (event.velocity > 0) { // Note On
//                                activeNotes[event.noteValue] = event
//                            } else { // Note On with velocity 0 is equivalent to Note Off
//                                activeNotes.remove(event.noteValue)?.let { noteOnEvent ->
//                                    val noteOnTimeMillis = (noteOnEvent.tick * microSecsPerQuarterNote) / (TicksPerQuarterNote * 1000L)
//                                    val duration = eventTimeMillis - noteOnTimeMillis
//                                    if (duration > 0) {
//                                        addFallingNote(noteOnEvent.noteValue, noteOnTimeMillis, duration, noteIdCounter++)
//                                    }
//                                }
//                            }
//                        }
//                        is NoteOff -> {
//                            activeNotes.remove(event.noteValue)?.let { noteOnEvent ->
//                                val noteOnTimeMillis = (noteOnEvent.tick * microSecsPerQuarterNote) / (TicksPerQuarterNote * 1000L)
//                                val duration = eventTimeMillis - noteOnTimeMillis
//                                if (duration > 0) {
//                                    addFallingNote(noteOnEvent.noteValue, noteOnTimeMillis, duration, noteIdCounter++)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            fallingNotes.sortBy { it.startTimeMillis } // Ensure notes are ordered by start time
//            inputStream.close()
//        }
//    }
//
//    private fun addFallingNote(midi: Int, startTime: Long, duration: Long, id: Int) {
//        val keyInfo = keyXOffsetMap[midi] ?: return // Skip if key not found (shouldn't happen for standard piano)
//        val pianoKey = allPianoKeys.find { it.midi == midi }
//
//        val note = FallingNoteInfo(
//            id = id,
//            midiNote = midi,
//            startTimeMillis = startTime,
//            durationMillis = duration,
//            xOffset = keyInfo.first,
//            width = keyInfo.second,
//            color = if (pianoKey?.isBlack == true) Color.DarkGray else Color(0xFF4285F4) // Blue for white, DarkGray for black
//        )
//        fallingNotes.add(note)
//    }
//
//    fun play() {
//        if (isPlaying.value || fallingNotes.isEmpty()) return
//        isPlaying.value = true
//
//        animationJob?.cancel() // Cancel previous job if any
//        notePlaybackJob?.cancel()
//
//        animationJob = viewModelScope.launch {
//            var lastFrameTimeNanos = System.nanoTime()
//            while (isActive && isPlaying.value) {
//                val currentTimeNanos = awaitFrame() // Waits for the next frame, provides smooth animation
//                val deltaTimeMillis = (currentTimeNanos - lastFrameTimeNanos) / 1_000_000f
//                lastFrameTimeNanos = currentTimeNanos
//
//                if (isPlaying.value) { // Check again in case it was paused/stopped
//                    currentSongTimeMillis.value += deltaTimeMillis.toLong()
//                    updateNotesPositions(deltaTimeMillis)
//                } else {
//                    break // Exit loop if no longer playing
//                }
//            }
//        }
//    }
//
//
//    private fun updateNotesPositions(deltaTimeMillis: Float) {
//        val fallAreaHeightPx = (FALL_AREA_HEIGHT_DP * Util.density).toFloat() // Convert Dp to Px
//        val pixelsToFallThisFrame = (PIXELS_PER_SECOND * Util.density) * (deltaTimeMillis / 1000f)
//
//        fallingNotes.forEach { note ->
//            // Make note visible if it's about to enter the screen or is on screen
//            if (currentSongTimeMillis.value >= note.startTimeMillis - NOTE_APPEAR_OFFSET_MILLIS && !note.hasBeenMissed) {
//                note.isVisible = true
//            }
//
//            if (note.isVisible && !note.hasBeenHit && !note.hasBeenMissed) {
//                // Calculate initial Y position based on when it should hit the "play line"
//                // The "play line" is at the bottom of the fall area.
//                // Note should start NOTE_APPEAR_OFFSET_MILLIS "above" the play line in terms of time.
//                val timeToPlayLine = note.startTimeMillis - currentSongTimeMillis.value
//                val distanceToPlayLinePx = (timeToPlayLine / 1000f) * (PIXELS_PER_SECOND * Util.density)
//
//                note.currentYPositionPx = fallAreaHeightPx - distanceToPlayLinePx
//
//                // Calculate visual height based on duration
//                note.visualHeightPx = (note.durationMillis * DURATION_TO_HEIGHT_SCALE_FACTOR * Util.density)
//                    .coerceIn(NOTE_MIN_VISUAL_HEIGHT_PX * Util.density, NOTE_MAX_VISUAL_HEIGHT_PX * Util.density)
//
//
//                // Check for "collision" with the play line (bottom of the fall area)
//                if (note.currentYPositionPx >= fallAreaHeightPx && !note.hasBeenHit) {
//                    note.hasBeenHit = true // Mark as hit to play once
//                    // Trigger the sound
//                    playNote(note.midiNote) // Your existing function
//                    // Change color or add visual feedback for hit note
//                    // note.color = Color.Green // Example
//                    // Schedule release
//                    activeAutoPlayedNotes[note.midiNote]?.cancel() // Cancel previous release if any for this note
//                    activeAutoPlayedNotes[note.midiNote] = viewModelScope.launch {
//                        delay(note.durationMillis)
//                        if (isActive) { // Ensure the job wasn't cancelled by a new play of the same note
//                            releaseNote(note.midiNote) // Your existing function
//                        }
//                    }
//                }
//
//                // If note has fallen past the play line and its visual end
//                if (note.currentYPositionPx > fallAreaHeightPx + note.visualHeightPx) {
//                    note.hasBeenMissed = true // Mark as missed if it goes off screen
//                    note.isVisible = false
//                }
//            }
//        }
//        // Optional: Clean up notes that are no longer visible and won't reappear
//        // fallingNotes.removeAll { it.hasBeenMissed && !it.isVisible }
//    }
//
//
//    fun pause() {
//        isPlaying.value = false
//        animationJob?.cancel()
//        // Do not cancel notePlaybackJob or clear activeAutoPlayedNotes,
//        // as notes that were triggered should continue their duration if desired,
//        // or handle pausing sound engine if necessary. For simplicity here, let them play out.
//    }
//
//    fun stop() {
//        isPlaying.value = false
//        animationJob?.cancel()
//        notePlaybackJob?.cancel()
//        activeAutoPlayedNotes.values.forEach { it.cancel() } // Cancel all scheduled releases
//        activeAutoPlayedNotes.clear()
//        // Optionally release all currently sounding notes via your sound engine if it tracks them globally
//        fallingNotes.forEach {
//            it.currentYPositionPx = 0f
//            it.isVisible = false
//            it.hasBeenHit = false
//            it.hasBeenMissed = false
//        }
//        currentSongTimeMillis.value = 0L
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        animationJob?.cancel()
//        notePlaybackJob?.cancel()
//        activeAutoPlayedNotes.values.forEach { it.cancel() }
//    }
//}