package com.example.fuwalo

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import org.billthefarmer.mididriver.MidiDriver


class MainActivity : ComponentActivity(), MidiDriver.OnMidiStartListener {
    private lateinit var midiDriver: MidiDriver
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Permission for external SF2 loading
        val readPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this, readPermission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(readPermission)
        }
        // Init synth
        midiDriver = MidiDriver.getInstance(this).apply {
            setOnMidiStartListener(this@MainActivity)
            start()
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SF2 Player", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
                        PianoKeyboard(onKeyPress = { note -> playNote(note) })
                    }
                }
            }
        }
    }

    private fun playNote(note: Int) {
        val noteOn = byteArrayOf(0x90.toByte(), note.toByte(), 127.toByte())
        val noteOff = byteArrayOf(0x80.toByte(), note.toByte(), 0)
        midiDriver.write(noteOn)
        handler.postDelayed({ midiDriver.write(noteOff) }, 500)
    }

    override fun onMidiStart() {}
    override fun onResume() { super.onResume(); midiDriver.start() }
    override fun onPause() { super.onPause(); midiDriver.stop() }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
}

// Composable: a simple one-octave piano keyboard
@Composable
fun PianoKeyboard(onKeyPress: (Int) -> Unit) {
    val whiteKeys = listOf(60, 62, 64, 65, 67, 69, 71, 72) // C4 to C5
    Row(modifier = Modifier.padding(8.dp)) {
        whiteKeys.forEach { note ->
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 160.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .clickable { onKeyPress(note) }
                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            ){}
        }
    }
}


@Preview
@Composable
fun AppAndroidPreview() {
    App()
}