package com.example.fuwalo.data


/**
 * Data class representing a single piano key with its properties.
 *
 * @param index The sequential index of the key (0-87).
 * @param note The full note name including octave (e.g., "A0", "C#4", "C8").
 * @param midi The MIDI note number (21-108 for an 88-key piano).
 * @param isBlack True if the key is a black key (sharp/flat), false otherwise.
 */
data class PianoKey(val index: Int, val note: String, val midi: Int, val isBlack: Boolean)

/**
 * Generates a list of PianoKey objects representing an 88-key piano.
 * The note names include the octave number (e.g., "A0", "C4").
 *
 * @return A list of 88 PianoKey objects.
 */
fun generatePianoKeys(): List<PianoKey> {
    // Note names starting with C for easier calculation with midi % 12
    val noteNames = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val keys = mutableListOf<PianoKey>()
    var midi = 21 // Starting MIDI note for an 88-key piano (A0)

    // Loop through all 88 keys
    for (i in 0 until 88) {
        // Calculate the base note name (without octave)
        // midi % 12 gives the index in noteNames (0=C, 1=C#, ..., 9=A, 10=A#, 11=B)
        val baseNote = noteNames[midi % 12]

        // Calculate the octave number based on MIDI value
        // MIDI 60 is C4 -> (60 / 12) - 1 = 4
        // MIDI 21 is A0 -> (21 / 12) - 1 = 0
        // MIDI 24 is C1 -> (24 / 12) - 1 = 1
        val octave = (midi / 12) - 1

        // Combine base note and octave number for the full note name
        val fullNote = "$baseNote$octave"

        // Determine if the key is black based *only* on the base note name
        val isBlack = baseNote.contains("#")

        // Add the new PianoKey object to the list
        keys.add(PianoKey(i, fullNote, midi, isBlack))

        // Move to the next MIDI note
        midi++
    }
    return keys
}
var playNote: (Int) -> Unit = {}