import com.example.fuwalo.core.utils.Util
import dev.atsushieno.ktmidi.Midi1Event
import dev.atsushieno.ktmidi.Midi1Music
import dev.atsushieno.ktmidi.MidiChannelStatus.NOTE_OFF
import dev.atsushieno.ktmidi.MidiChannelStatus.NOTE_ON
import dev.atsushieno.ktmidi.MidiEvent
import dev.atsushieno.ktmidi.MidiEventType
import dev.atsushieno.ktmidi.MidiMetaType
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.read

import java.io.InputStream
import dev.atsushieno.ktmidi.*

/**
 * Data class to store extracted MIDI note information.
 *
 * @property midiNoteNumber The MIDI note number (0-127).
 * @property startTimeMs The start time of the note in milliseconds from the beginning of the MIDI track.
 * @property durationMs The duration of the note in milliseconds.
 * @property velocity The velocity of the Note ON event (0-127).
 * @property channel The MIDI channel the note was played on (0-15).
 */
data class SimpleMidiNote(
    val midiNoteNumber: Int,
    val startTimeMs: Long,
    val durationMs: Long,
    val velocity: Int,
    val channel: Int,
    val noteType: String
)

// Helper class to manage events from all tracks with their absolute tick times.
// This is an internal detail for correct processing and doesn't complicate the public API.
private data class TimedMidiEvent(
    val absoluteTick: Long,
    val event: Midi1Event,
    val trackIndex: Int // For debugging or more advanced logic if needed
)

/**
 * Extracts MIDI note information (note number, start time, duration, velocity, channel)
 * from a given InputStream representing a .mid file.
 *
 * Uses the dev.atsushieno:ktmidi library for parsing.
 *
 * @param inputStream The InputStream of the MIDI file.
 * @return A list of SimpleMidiNote objects, sorted by start time. Returns an empty list on error
 * or if the MIDI time format is unsupported (e.g., SMPTE).
 */
// Make sure you have necessary imports

// Assuming TimedMidiEvent is defined something like:
// data class TimedMidiEvent(val absoluteTick: Long, val event: MidiMessage, val trackIndex: Int)
// And SimpleMidiNote:
// data class SimpleMidiNote(val midiNoteNumber: Int, val startTimeMs: Long, val durationMs: Long, val velocity: Int, val channel: Int)


fun extractMidiNotes(inputStream: InputStream): List<SimpleMidiNote> {
    val extractedNotes = mutableListOf<SimpleMidiNote>()
    val activeNotesMap = mutableMapOf<Pair<Int, Int>, Pair<Long, Int>>()

    try {
        val music = Midi1Music()
        music.read(inputStream.readBytes().toList())

        val ticksPerQuarterNote = music.deltaTimeSpec
        if (ticksPerQuarterNote <= 0) {
            System.err.println(
                "Unsupported MIDI time format: deltaTimeSpec is $ticksPerQuarterNote. " +
                        "This simple parser only supports positive PPQN (ticks per quarter note) values."
            )
            return emptyList()
        }

        val allTimedEvents = mutableListOf<TimedMidiEvent>() // Your TimedMidiEvent class
        music.tracks.forEachIndexed { trackIdx, track ->
            var currentTickInTrack = 0L
            track.events.forEach { ktMidiMessage -> // This is dev.atsushieno.ktmidi.MidiMessage
                currentTickInTrack += ktMidiMessage.deltaTime
                // Assuming your TimedMidiEvent stores the ktMidiMessage directly
                allTimedEvents.add(TimedMidiEvent(currentTickInTrack, ktMidiMessage, trackIdx))
            }
        }
        allTimedEvents.sortBy { it.absoluteTick }

        var currentGlobalTimeMillis = 0L
        var lastEventAbsoluteTick = 0L
        var microsecondsPerQuarterNote = 500000L // Default tempo: 120 BPM

        for (timedEvent in allTimedEvents) {
            val eventAbsoluteTick = timedEvent.absoluteTick
            // Assuming timedEvent.event IS the dev.atsushieno.ktmidi.MidiMessage
            val midiMessage = timedEvent.event // This is the ktmidi.MidiMessage

            val deltaTicks = eventAbsoluteTick - lastEventAbsoluteTick
            if (deltaTicks > 0) {
                val millisElapsed = (deltaTicks.toDouble() / ticksPerQuarterNote.toDouble() *
                        microsecondsPerQuarterNote.toDouble() / 1000.0).toLong()
                currentGlobalTimeMillis += millisElapsed
            }
            lastEventAbsoluteTick = eventAbsoluteTick

            val statusByte = midiMessage.message.statusByte.toInt()
            val eventTypeWithoutChannel = statusByte and 0xF0
            val channel = statusByte and 0x0F

            when (eventTypeWithoutChannel) {
                NOTE_ON.toInt() -> { // MidiEventType.NOTE_ON is 0x90
                    // Correctly get note and velocity from msb and lsb
                    val noteNumber = midiMessage.message.msb.toInt() and 0xFF
                    val velocityValue = midiMessage.message.lsb.toInt() and 0xFF
                    val noteKey = Pair(channel, noteNumber)

                    if (velocityValue > 0) { // Actual Note On event
                        activeNotesMap.remove(noteKey)?.let { (startTimeMsPrev, velocityPrev) ->
                            val durationMsPrev = currentGlobalTimeMillis - startTimeMsPrev
                            if (durationMsPrev >= 0) {
                                extractedNotes.add(SimpleMidiNote(
                                    midiNoteNumber = noteNumber, // Corrected noteNumber
                                    startTimeMs = startTimeMsPrev,
                                    durationMs = durationMsPrev,
                                    velocity = velocityPrev,
                                    channel = channel,
                                    noteType = Util.NOTE_ON
                                ))
                            }
                        }
                        activeNotesMap[noteKey] = Pair(currentGlobalTimeMillis, velocityValue)
                    } else { // Note On with velocity 0 is equivalent to Note Off
                        activeNotesMap.remove(noteKey)?.let { (startTimeMs, originalVelocity) ->
                            val durationMs = currentGlobalTimeMillis - startTimeMs
                            if (durationMs >= 0) {
                                extractedNotes.add(SimpleMidiNote(
                                    midiNoteNumber = noteNumber, // Corrected noteNumber
                                    startTimeMs = startTimeMs,
                                    durationMs = durationMs,
                                    velocity = originalVelocity,
                                    channel = channel,
                                    noteType = Util.NOTE_ON
                                ))
                            }
                        }
                    }
                }
                NOTE_OFF.toInt() -> { // MidiEventType.NOTE_OFF is 0x80
                    // Correctly get note from msb
                    val noteNumber = midiMessage.message.msb.toInt() and 0xFF
                    // val releaseVelocity = midiMessage.lsb.toInt() and 0xFF // If you need it
                    val noteKey = Pair(channel, noteNumber)

                    activeNotesMap.remove(noteKey)?.let { (startTimeMs, originalVelocity) ->
                        val durationMs = currentGlobalTimeMillis - startTimeMs
                        if (durationMs >= 0) {
                            extractedNotes.add(SimpleMidiNote(
                                midiNoteNumber = noteNumber, // Corrected noteNumber
                                startTimeMs = startTimeMs,
                                durationMs = durationMs,
                                velocity = originalVelocity,
                                channel = channel,
                                noteType = Util.NOTE_OFF
                            ))
                        }
                    }
                }
                // No specific handling for other channel messages in this snippet, but could be added.
            }

            // Handle Meta Events (like Tempo) - outside the channel message switch if statusByte is META
//            if (statusByte == Midi1Status.META.toInt()) { // 0xFF for Meta events
//                if (midiMessage.message.metaType.toInt() == MidiMetaType.TEMPO && midiMessage .size >= 3) {
//                    microsecondsPerQuarterNote = (midiMessage.data[0].toInt() and 0xFF shl 16) or
//                            (midiMessage.message.data[1].toInt() and 0xFF shl 8) or
//                            (midiMessage.data[2].toInt() and 0xFF)
//                }
//                // Add other meta event handling here if needed (e.g., End of Track)
//            }
        }

        activeNotesMap.forEach { (key, value) ->
            val (ch, nn) = key
            val (startTimeMs, vel) = value
            val durationMs = currentGlobalTimeMillis - startTimeMs
            if (durationMs >= 0) {
                extractedNotes.add(SimpleMidiNote(
                    midiNoteNumber = nn,
                    startTimeMs = startTimeMs,
                    durationMs = durationMs,
                    velocity = vel,
                    channel = ch,
                    noteType = Util.NOTE_OFF
                ))
            }
        }
        activeNotesMap.clear()

    } catch (e: Exception) {
        System.err.println("Error parsing MIDI file: ${e.message}")
        e.printStackTrace()
        return emptyList()
    }

    extractedNotes.sortWith(compareBy({ it.startTimeMs }, { it.channel }, { it.midiNoteNumber }))
    return extractedNotes
}

// You would need your TimedMidiEvent data class, e.g.:
// data class TimedMidiEvent(val absoluteTick: Long, val event: MidiMessage, val trackIndex: Int)

// Example Usage (requires a .mid file and a way to get an InputStream):
/*
fun main() { // Or in your Android Activity/ViewModel
    // In a real Android app, you'd get this from assets, file picker, etc.
    // For testing, you can place a midi file in resources or use a File path.
    val midiFilePath = "path/to/your/file.mid" // Replace with actual path or resource loading
    try {
        val inputStream: InputStream = File(midiFilePath).inputStream() // Example for local file
        // Or from Android assets:
        // val assets: AssetManager = context.assets
        // val inputStream: InputStream = assets.open("your_midi_file.mid")


        inputStream.use { stream ->
            val notes = extractMidiNotes(stream)
            if (notes.isNotEmpty()) {
                println("Successfully extracted ${notes.size} notes:")
                notes.forEach { note ->
                    println(
                        "  Note: ${note.midiNoteNumber}, Start: ${note.startTimeMs}ms, " +
                        "Duration: ${note.durationMs}ms, Velocity: ${note.velocity}, Channel: ${note.channel}"
                    )
                }
            } else {
                println("No notes extracted or an error occurred.")
            }
        }
    } catch (e: java.io.FileNotFoundException) {
        System.err.println("MIDI file not found: $midiFilePath")
    } catch (e: Exception) {
        System.err.println("An error occurred during example usage: ${e.message}")
        e.printStackTrace()
    }
}
*/
