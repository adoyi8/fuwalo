import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.fuwalo.core.utils.Util.getBlackKeyOffSet
import com.example.fuwalo.data.PianoKey

@Composable
fun PianoKeyboardFourKeys(firstWhite: PianoKey, firstBlack: PianoKey, secondWhite: PianoKey, secondBlack: PianoKey, thirdWhite: PianoKey, thirdBlack: PianoKey, fourthWhite: PianoKey) {
    Surface(
        color = Color(0xFFEFEFFF),
        modifier = Modifier.padding()
    ) {
        Box {
            // White keys: C4 and D4
            Row {
                WhiteKey(pianoKey = firstWhite)
                WhiteKey(pianoKey = secondWhite)
                WhiteKey(pianoKey = thirdWhite)
                WhiteKey(pianoKey = fourthWhite)
            }


                BlackKey(pianoKey = firstBlack, modifier = Modifier.offset(x = getBlackKeyOffSet(1)))
            BlackKey(pianoKey = secondBlack, modifier = Modifier.offset(x = getBlackKeyOffSet(2)))
            BlackKey(pianoKey = thirdBlack, modifier = Modifier.offset(x = getBlackKeyOffSet(3)))

        }
    }
}


