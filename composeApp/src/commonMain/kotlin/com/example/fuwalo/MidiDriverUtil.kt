package com.example.fuwalo

import org.billthefarmer.mididriver.MidiDriver


object MidiDriverUtil {
     var midiDriver: MidiDriver? = null



     private fun playNote(note: Int) {
          val noteOn = byteArrayOf(0x90.toByte(), note.toByte(), 127.toByte())
          val noteOff = byteArrayOf(0x80.toByte(), note.toByte(), 0)
          midiDriver?.write(noteOn)
        //  handler.postDelayed({ midiDriver.write(noteOff) }, 500)
     }
}