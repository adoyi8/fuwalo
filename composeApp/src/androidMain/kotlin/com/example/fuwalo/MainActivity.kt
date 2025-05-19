package com.example.fuwalo

import SimpleMidiNote
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.fuwalo.core.utils.Util
import extractMidiNotes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity(){
    companion object {
        /** @brief Initialize: Load the Native Library. */
        init { System.loadLibrary("synth-lib") }
    }
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var synthManager: SynthManager




    @OptIn(DelicateCoroutinesApi::class)
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


        synthManager = SynthManager(this)
        synthManager.loadSF("KawaiStereoGrand.sf3")
        synthManager.setVolume(127)



        // Example: Name of your MIDI file in the 'assets' folder
        val midiFileName = "thriller.mid" // IMPORTANT: Replace with your actual MIDI file name

        // It's good practice to run file operations off the main thread.
        // Here's an example using Kotlin Coroutines.
        GlobalScope.launch(Dispatchers.Main) { // Dispatchers.Main is okay for launching, but synth operations might prefer Dispatchers.Default
            val parsedEvents: List<SimpleMidiNote> = withContext(Dispatchers.IO) {
                // 'this' refers to the Activity context, assuming this code is inside an Activity
                // Replace with your actual context if needed:
                // extractMidiNotes(this@YourActivityName.assets.open(midiFileName))
                extractMidiNotes(assets.open(midiFileName)) // Assuming 'assets' is accessible
            }

            if (parsedEvents.isNotEmpty()) {
                println("Successfully parsed ${parsedEvents.size} MIDI events.")

                // 1. Sort events by their startTimeMs. This is crucial for correct playback order.
                val sortedEvents = parsedEvents.sortedBy { it.startTimeMs }

                var currentTimeMs = 0L // Keeps track of the playback timeline

                // 2. Iterate through the sorted events and play them with correct timing.
                for (event in sortedEvents) {
                    // Calculate how long to wait before this event needs to occur
                    val delayDuration = event.startTimeMs - currentTimeMs
                    if (delayDuration > 0) {
                        delay(delayDuration)
                    }
                    currentTimeMs = event.startTimeMs // Update current time to this event's timestamp

                    // Log the event being processed
                    // Consider using Android's Log.d for debugging if in an Android app
                    println("Time: ${currentTimeMs}ms - Type: ${event.noteType}, MIDI=${event.midiNoteNumber}, Vel=${event.velocity}, Chan=${event.channel}")

                    // 3. Perform action based on event type
                    if (event.noteType == Util.NOTE_ON) {
                        // You might want to use event.velocity and event.channel here
                     //   playNoteSustain(event.midiNoteNumber /*, event.velocity, event.channel */)
                        //delay(event.durationMs)
                        //release(event.midiNoteNumber)
                    } else if (event.noteType == Util.NOTE_OFF) {
                        // Channel might also be relevant for note off
                       // release(event.midiNoteNumber /*, event.channel */)
                    }
                    // The 'durationMs' field in SimpleMidiNote is not used in this version
                    // because the playback relies on explicit NOTE_OFF events found in the parsed list.
                    // If your 'SimpleMidiNote' for a NOTE_ON event is meant to define its own duration
                    // (and you don't have separate NOTE_OFF events), the playback logic would be different.
                }
                println("Finished processing all MIDI events.")

            } else {
                println("No MIDI events parsed or an error occurred. Check Logcat for details.")
            }
        }

//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // API 30+
//            window.insetsController?.let { c ->
//                c.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
//                c.systemBarsBehavior =
//                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            }
//        } else {
//            // pre-API 30
//            @Suppress("DEPRECATION")
//            window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        App(onKeyPress = { note -> playNoteSf2(note) }, onKeyRelease = { note -> release(note) }, playNoteSustains = { note -> playNoteSustain(note) }, loadSoundFont = { soundFont -> loadSoundFonts(soundFont) }, )
                    }
                }
            }
        }
    }


fun loadSoundFonts(soundFonts: String){
    synthManager.loadSF(soundFonts)
    synthManager.setVolume(127)
}


    fun playNoteSf2(noteNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            synthManager.noteOn(noteNumber)
            delay(500)
            synthManager.noteOff(noteNumber)
        }
        }
    fun playNoteSustain(noteNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            synthManager.noteOn(noteNumber)
        }
    }
    fun release(noteNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            synthManager.noteOff(noteNumber)
        }
    }



    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    /*
    * @brief On MIDI message received callback.
    * @param message Message received.
    */
    private fun onMidiMessageReceived(message: String) {
        // messages are received on some other thread, so switch to the UI thread
        // before attempting to access the UI
      //  runOnUiThread { txtLog.append(message + "\n") }
    }
    /** @brief On destroy event. */
    override fun onDestroy() {
        synthManager.finalize()
        super.onDestroy()
    }
}

// Composable: a simple one-octave piano keyboard



@Preview(
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun AppAndroidPreview() {
  Home()
}



