import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.fuwalo.data.playNote
import java.awt.Color.black

@Composable
fun PianoKeyboardTwoKeys(firstWhite: PianoKey, black:PianoKey, secondWhite: PianoKey) {
    Surface(
        color = Color(0xFFEFEFFF),
        modifier = Modifier.padding()
    ) {
        Box {
            // White keys: C4 and D4
            Row() {
                WhiteKey(pianoKey = firstWhite)
                WhiteKey(pianoKey = secondWhite)
            }
                BlackKey(pianoKey = black, modifier = Modifier.offset(x = getBlackKeyOffSet(1)))

        }
    }
}

@Composable
fun PianoKeyboardOneKey(firstWhite: PianoKey) {
    Surface(
        color = Color(0xFFEFEFFF),
        modifier = Modifier.padding()
    ) {
        Box {
            // White keys: C4 and D4
            Row() {
                WhiteKey(pianoKey = firstWhite)
            }
        }
    }
}

@Composable
fun WhiteKey(pianoKey: PianoKey, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(WHITE_KEY_WIDTH)
            .height(WHITE_KEY_HEIGHT)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                onClick = { playNote(pianoKey.midi) }
            ),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(Color(0xFFD0E6FF), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable(
                    onClick = { playNote(pianoKey.midi) }
                )
        ) {
            Text(
                text = pianoKey.note,
                color = Color.DarkGray,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
@Composable
fun BlackKey(pianoKey: PianoKey, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(BLACK_KEY_WIDTH)
            .height(BLACK_KEY_HEIGHT)
            .shadow(4.dp, shape = RoundedCornerShape(4.dp))
            .background(Color.Black, shape = RoundedCornerShape(4.dp))
            .clickable(onClick = { playNote(pianoKey.midi)})
    )
}