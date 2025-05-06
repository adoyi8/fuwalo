package com.example.fuwalo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage
import javax.sound.midi.Synthesizer

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fuwalo",
    ) {
        App(onKeyPress = { note -> playNoteOnDesktop(note) })
    }
}

// Keep synthesizer open globally
val synthesizer: Synthesizer = MidiSystem.getSynthesizer().apply { open() }
val midiChannel = synthesizer.channels[0] // Channel 0 usually piano

fun playNoteOnDesktop(midiNote: Int) {
    val velocity: Int = 80
    val durationMs: Long = 300L
    CoroutineScope(Dispatchers.IO).launch {
        try {
            midiChannel.noteOn(midiNote, velocity)
            Thread.sleep(durationMs)
            midiChannel.noteOff(midiNote)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}