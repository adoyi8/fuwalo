import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fuwalo.core.utils.Util.BLACK_KEY_HEIGHT
import com.example.fuwalo.core.utils.Util.BLACK_KEY_WIDTH
import com.example.fuwalo.core.utils.Util.WHITE_KEY_HEIGHT
import com.example.fuwalo.core.utils.Util.WHITE_KEY_WIDTH
import com.example.fuwalo.core.utils.Util.getBlackKeyOffSet
import com.example.fuwalo.data.PianoKey

@Composable
fun PianoKeyboardThreeKeys(firstWhite: PianoKey, firstBlack:PianoKey, secondWhite: PianoKey, secondBlack: PianoKey,thirdWhite: PianoKey) {
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
            }


                BlackKey(pianoKey = firstBlack, modifier = Modifier.offset(x = getBlackKeyOffSet(1)))
            BlackKey(pianoKey = secondBlack,modifier = Modifier.offset(x = getBlackKeyOffSet(2)))

        }
    }
}


