package com.example.fuwalo

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fuwalo.composeapp.generated.resources.Res
import fuwalo.composeapp.generated.resources.piano_strip
import org.billthefarmer.mididriver.MidiDriver
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt


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
                        PianoScreen(onKeyPress = { note -> playNote(note) })
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
   // val whiteKeys = listOf(60, 62, 64, 65, 67, 69, 71, 72)
    val whiteKeys = listOf(
        60, // C4
        62, // D4
        64, // E4
        65, // F4
        67, // G4
        69, // A4
        71, // B4
        62, // D4 (again)
        64  // E4 (again)
    )// C4 to C5
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


@Preview(
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"

)
@Composable
fun AppAndroidPreview() {
  PianoScreen {  }
}
@Composable
fun PianoScreen(onKeyPress: (Int) -> Unit) {

    val backgroundColor = remember{Color(0xffbec1ea)}
    val buttonsColor = remember{Color(0xff7f90c6)}
    Column(modifier = Modifier.fillMaxSize().background(backgroundColor), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f), horizontalArrangement = Arrangement.SpaceBetween){
            CustomButton(modifier = Modifier.weight(1f), buttonColor = buttonsColor)
            Spacer(modifier = Modifier.weight(5f))
            CustomButton(modifier = Modifier.weight(1f), buttonColor = buttonsColor)
        }
        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp), horizontalArrangement = Arrangement.Center){
            CustomButton(modifier = Modifier.fillMaxWidth(0.7f).height(25.dp), buttonColor = buttonsColor)
        }
        val whiteKeys = listOf(60, 62, 64, 65, 67, 69, 71, 72, 60 , 62) // C4 to C5
        PianoKeyboard(onKeyPress = onKeyPress)
    }
}

@Composable
fun ImagePianoKeyboard(
    modifier: Modifier = Modifier,
    numWhiteKeys: Int = 8,
    firstMidiNote: Int = 60,            // C4
    onKeyPress: (note: Int) -> Unit
) {
    // Load your piano strip image from drawable
    val pianoImage = painterResource(Res.drawable.piano_strip)

    // Track which key index is currently pressed
    var pressedKeyIndex by remember { mutableStateOf<Int?>(null) }

    // We'll need the rendered width to compute key regions
    var imageWidthPx by remember { mutableStateOf(0f) }
    var imageHeightPx by remember { mutableStateOf(0f) }

    Box(modifier = modifier
        .aspectRatio( numWhiteKeys.toFloat() / 1f )       // keep same aspect ratio
        .pointerInput(numWhiteKeys) {
            detectTapGestures { offset ->
                // map touch x → key index
                val keyWidth = imageWidthPx / numWhiteKeys
                val idx = (offset.x / keyWidth).toInt().coerceIn(0, numWhiteKeys - 1)
                pressedKeyIndex = idx
                onKeyPress(firstMidiNote + idx)
            }
        }
    ) {
        Image(
            painter = pianoImage,
            contentDescription = "Piano keyboard",
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    imageWidthPx = size.width.toFloat()
                    imageHeightPx = size.height.toFloat()
                }
        )

        // translucent overlay on pressed key
        pressedKeyIndex?.let { idx ->
            val keyWidth = imageWidthPx / numWhiteKeys
            Box(modifier = Modifier
                .offset { IntOffset((idx * keyWidth).roundToInt(), 0) }
                .size(keyWidth.dp, imageHeightPx.dp)
                .background(Color.Black.copy(alpha = 0.2f))
            )
        }
    }
}
