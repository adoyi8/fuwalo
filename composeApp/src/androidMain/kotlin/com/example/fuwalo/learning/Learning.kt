package com.example.fuwalo.learning // Or a more suitable package

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.fuwalo.data.generatePianoKeys
import java.util.UUID
import java.io.InputStream
import android.content.Context

import dev.atsushieno.ktmidi.* // ktmidi main classes


// Represents a single note event extracted from MIDI, ready for display
data class FallingNoteInfo(
    val id: Int, // Unique ID for the note event
    val midiNote: Int,
    val startTimeMillis: Long, // When the note should ideally be played
    val durationMillis: Long,
    val xOffset: Dp, // Horizontal position on the screen, aligned with the key
    val width: Dp, // Width of the falling note visual
    val color: Color = if (generatePianoKeys().find { it.midi == midiNote }?.isBlack == true) Color.DarkGray else Color(0xFF8AB4F8), // Default color based on key type
    var currentYPositionPx: Float = 0f, // Current Y position in pixels (top of the note)
    var visualHeightPx: Float = 0f, // Visual height of the note, proportional to duration
    var isVisible: Boolean = false,
    var hasBeenHit: Boolean = false, // If the note has reached the play line and triggered
    var hasBeenMissed: Boolean = false // If the note passed the play line without being "played" by the system
)

data class ParsedMidiNote(
    val id: String = UUID.randomUUID().toString(),
    val midi: Int,
    val startTimeMs: Long,
    val durationMs: Long,
    var isProcessed: Boolean = false, // For PianoLearningScreen internal tracking
    var isPlayedOrMissed: Boolean = false // For PianoLearningScreen internal tracking
)
 // Create a new package for MIDI utilities



// Assuming these are your project's classes/objects.
// You'll need to ensure they are correctly defined and imported.
// object MidiMusic // Placeholder for your MidiMusic class
// object MidiMetaType // Placeholder
// object MidiChannelStatus // Placeholder
// object MidiFunctions // Placeholder
// data class ParsedMidiNote(val midi: Int, val startTimeMs: Long, val durationMs: Long) // Placeholder
// class MidiMusic { // Placeholder
//    var division: Short = 0
//    var tracks: List<MidiTrack> = emptyList()
//    fun read(data: ByteArray) { /* Implementation */ }
// }
// data class MidiTrack(val messages: List<MidiEventContainer>) // Placeholder
// data class MidiEventContainer(val deltaTime: Int, val event: MidiEvent) // Placeholder
// interface MidiEvent // Placeholder
// data class MidiMetaMessage(val type: Short, val data: ByteArray) : MidiEvent // Placeholder
// data class MidiShortMessage(val statusByte: Byte, val msb: Byte, val lsb: Byte) : MidiEvent // Placeholder


// --- START OF ACTUAL USER CODE WITH CORRECTION ---
fun parseMidiFileKt(context: Context, assetFileName: String): List<ParsedMidiNote> {
    val parsedNotes = mutableListOf<ParsedMidiNote>()
    val assetManager = context.assets

    try {
        // Read all bytes from the InputStream
        val midiDataBytes: ByteArray = assetManager.open(assetFileName).use { inputStream ->
            inputStream.readBytes()
        }

        val music = Midi1Music() // Create an instance of MidiMusic
        music.read(midiDataBytes.toList()) // Call its public read method with ByteArray

        // music.division is a Short, represents ticks per quarter note if positive
//        val ticksPerQuarterNote = music. .toInt()
//        if (ticksPerQuarterNote <= 0) {
//            println("Unsupported MIDI time division format (SMTPE not supported here): $ticksPerQuarterNote")
//            return emptyList()
//        }

        var currentGlobalTempoMicrosPerQuarterNote: Int = MidiMetaType.TEMPO // MidiMetaType.TEMPO // 500,000 µs / QN (120 BPM)
        // Replaced MidiMetaType.TEMPO with its common default value
        // as the definition was not provided.

        fun ticksToMs(ticks: Long, tempoMicrosPerQN: Int, division: Int): Long {
            if (division == 0) return 0L
            // Ensure floating point division for precision
            val microsecondsPerTick = tempoMicrosPerQN.toDouble() / division.toDouble()
            return (ticks * microsecondsPerTick / 1000.0).toLong()
        }

        // Pair<Channel, NotePitch> to StartTimeInTicks
        val activeNotesOnTrack = mutableMapOf<Pair<Int, Int>, Long>()
        // Pair<Channel, NotePitch> to TempoAtNoteOn (microseconds per quarter note)
        val tempoAtNoteOn = mutableMapOf<Pair<Int, Int>, Int>()

        music.tracks.forEach { track ->
            var currentTickTimeInTrack: Long = 0
            // Each track can have its own tempo changes, but starts with the global/last known tempo.
            var trackSpecificTempoMicrosPerQN = currentGlobalTempoMicrosPerQuarterNote

            activeNotesOnTrack.clear() // Clear for each track
            tempoAtNoteOn.clear() // Clear for each track

            track.messages.forEach { midiEventContainer ->
                currentTickTimeInTrack += midiEventContainer.deltaTime.toLong()
                val eventMessage = midiEventContainer.event

                when (eventMessage) {
                    is MidiMetaMessage -> {
                        // Assuming MidiMetaType.TEMPO_CHANGE is a Short/Int constant
                        if (eventMessage.type == 3.toShort()) { // Common value for tempo change meta event (0x51)
                            // Replaced MidiMetaType.TEMPO_CHANGE with a common value
                            if (eventMessage.data.size >= 3) {
                                // Assuming MidiFunctions.bytesToInt is available and correct
                                val newTempo = MidiFunctions.bytesToInt(eventMessage.data, 0, 3)
                                trackSpecificTempoMicrosPerQN = newTempo
                                // Optional: Update global tempo if this is the first track or a specific type of tempo event
                                // For simplicity here, tempo changes are mostly track-local but can influence subsequent notes
                                // currentGlobalTempoMicrosPerQuarterNote = newTempo
                            }
                        }
                    }
                    is MidiShortMessage -> {
                        val channel = eventMessage.statusByte.toInt() and 0x0F
                        val command = eventMessage.statusByte.toInt() and 0xF0
                        val notePitch = eventMessage.msb.toInt()
                        val velocity = eventMessage.lsb.toInt()
                        val noteKey = Pair(channel, notePitch)

                        // Assuming MidiChannelStatus.NOTE_ON and NOTE_OFF are Int constants
                        val NOTE_ON_COMMAND = 0x90
                        val NOTE_OFF_COMMAND = 0x80

                        if (command == NOTE_ON_COMMAND && velocity > 0) {
                            // Note On event
                            // If a note with the same pitch and channel is already on,
                            // treat this as the end of the previous one and start of a new one.
                            // This handles cases where NOTE_OFF might be missing.
                            if (activeNotesOnTrack.containsKey(noteKey)) {
                                val prevStartTimeTicks = activeNotesOnTrack.remove(noteKey)!!
                                val prevTempo = tempoAtNoteOn.remove(noteKey) ?: trackSpecificTempoMicrosPerQN
                                val durationTicks = currentTickTimeInTrack - prevStartTimeTicks

                                if (durationTicks >= 0) { // Ensure non-negative duration
                                    parsedNotes.add(
                                        ParsedMidiNote(
                                            midi = notePitch,
                                            startTimeMs = ticksToMs(prevStartTimeTicks, prevTempo, ticksPerQuarterNote),
                                            durationMs = ticksToMs(durationTicks, prevTempo, ticksPerQuarterNote).coerceAtLeast(1L) // Ensure min 1ms duration
                                        )
                                    )
                                }
                            }
                            activeNotesOnTrack[noteKey] = currentTickTimeInTrack
                            tempoAtNoteOn[noteKey] = trackSpecificTempoMicrosPerQN
                        } else if (command == NOTE_OFF_COMMAND || (command == NOTE_ON_COMMAND && velocity == 0)) {
                            // Note Off event (or Note On with velocity 0)
                            if (activeNotesOnTrack.containsKey(noteKey)) {
                                val startTimeTicks = activeNotesOnTrack.remove(noteKey)!!
                                val noteOnTempo = tempoAtNoteOn.remove(noteKey) ?: trackSpecificTempoMicrosPerQN
                                val durationTicks = currentTickTimeInTrack - startTimeTicks

                                if (durationTicks >= 0) { // Ensure non-negative duration
                                    parsedNotes.add(
                                        ParsedMidiNote(
                                            midi = notePitch,
                                            startTimeMs = ticksToMs(startTimeTicks, noteOnTempo, ticksPerQuarterNote),
                                            durationMs = ticksToMs(durationTicks, noteOnTempo, ticksPerQuarterNote).coerceAtLeast(1L) // Ensure min 1ms duration
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // After processing all events in a track, handle any notes that are still "on"
            // (i.e., NOTE_ON without a corresponding NOTE_OFF by the end of the track)
            activeNotesOnTrack.keys.toList().forEach { noteKeyToClear -> // Iterate over a copy of keys
                val startTimeTicks = activeNotesOnTrack.remove(noteKeyToClear)!!
                val noteOnTempo = tempoAtNoteOn.remove(noteKeyToClear) ?: trackSpecificTempoMicrosPerQN
                // Consider the track's end time as the end for these notes
                val durationTicks = currentTickTimeInTrack - startTimeTicks
                if (durationTicks > 0) { // Only add if duration is positive
                    parsedNotes.add(
                        ParsedMidiNote(
                            midi = noteKeyToClear.second, // notePitch
                            startTimeMs = ticksToMs(startTimeTicks, noteOnTempo, ticksPerQuarterNote),
                            durationMs = ticksToMs(durationTicks, noteOnTempo, ticksPerQuarterNote).coerceAtLeast(1L)
                        )
                    )
                }
            }
            activeNotesOnTrack.clear() // Defensive clear
            tempoAtNoteOn.clear() // Defensive clear
        }
    } catch (e: Exception) {
        println("Error parsing MIDI file '$assetFileName': ${e.localizedMessage}")
        e.printStackTrace() // Good for debugging, consider a more robust logging strategy for production
        // Depending on requirements, you might want to return emptyList() or rethrow
    }

    // Sort all collected notes by their start time
    return parsedNotes.sortedBy { it.startTimeMs }
}

// --- HELPER/PLACEHOLDER DEFINITIONS (These would be in your actual MIDI library) ---

/**
 * Represents a parsed MIDI note with timing in milliseconds.
 */


/**
 * A placeholder for your MidiMusic class.
 * It would typically contain tracks, division (timing resolution), and format.
 */


/**
 * Represents a single MIDI track, containing a list of MIDI events.
 */
data class MidiTrack(val messages: List<MidiEventContainer>)

/**
 * Container for a MIDI event and its delta-time from the previous event.
 * @param deltaTime Ticks since the previous event in the track.
 * @param event The MIDI event itself.
 */
data class MidiEventContainer(val deltaTime: Int, val event: MidiEvent)

/**
 * Base interface for MIDI events (Short messages, Meta messages, Sysex messages).
 */
interface MidiEvent

/**
 * Represents a MIDI Meta Event (e.g., tempo change, time signature, lyrics).
 * @param type The type of meta event (e.g., 0x51 for Tempo Change).
 * @param data The data associated with the meta event.
 */
data class MidiMetaMessage(val type: Short, val data: ByteArray) : MidiEvent {
    // Optional: For cleaner comparison if type is used in when statements directly
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MidiMetaMessage
        if (type != other.type) return false
        if (!data.contentEquals(other.data)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = type.toInt()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

/**
 * Represents a MIDI Short Message (Channel messages like Note On, Note Off, Control Change).
 * @param statusByte The status byte (includes command and channel).
 * @param msb Most significant byte (e.g., note pitch for Note On/Off).
 * @param lsb Least significant byte (e.g., velocity for Note On/Off).
 */
data class MidiShortMessage(val statusByte: Byte, val msb: Byte, val lsb: Byte) : MidiEvent


/**
 * Placeholder for your MIDI constants.
 */
object MidiMetaType {
    const val TEMPO: Int = 500000 // Default tempo: 500,000 microseconds per quarter note (120 BPM)
    const val TEMPO_CHANGE: Short = 0x51 // Standard MIDI meta event type for tempo change
    // Add other meta types as needed, e.g., TIME_SIGNATURE, KEY_SIGNATURE, END_OF_TRACK
}

object MidiChannelStatus {
    const val NOTE_OFF: Int = 0x80         // Note Off event (channel n)
    const val NOTE_ON: Int = 0x90          // Note On event (channel n)
    // Add other channel statuses like POLY_PRESSURE, CONTROL_CHANGE, PROGRAM_CHANGE, etc.
}

/**
 * Placeholder for MIDI utility functions.
 */
object MidiFunctions {
    /**
     * Converts a portion of a byte array to an integer (big-endian).
     * @param bytes The byte array.
     * @param offset The starting offset in the byte array.
     * @param length The number of bytes to convert (usually 2, 3, or 4).
     * @return The integer value.
     */
    fun bytesToInt(bytes: ByteArray, offset: Int, length: Int): Int {
        var result = 0
        for (i in 0 until length) {
            result = (result shl 8) or (bytes[offset + i].toInt() and 0xFF)
        }
        return result
    }
}

// --- END OF HELPER/PLACEHOLDER DEFINITIONS ---
