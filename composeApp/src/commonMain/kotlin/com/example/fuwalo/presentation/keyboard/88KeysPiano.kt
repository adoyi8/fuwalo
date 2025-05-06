package com.example.fuwalo.presentation.keyboard

import PianoKeyboardFourKeys
import PianoKeyboardOneKey
import PianoKeyboardThreeKeys
import PianoKeyboardTwoKeys
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.fuwalo.data.generatePianoKeys

@Composable
fun EightyEightKeysPiano(modifier: Modifier){
    val pianoKeys = remember { generatePianoKeys() }
    Row(modifier = Modifier){
     PianoKeyboardTwoKeys(pianoKeys[0],pianoKeys[1], pianoKeys[2])
      PianoKeyboardThreeKeys(pianoKeys[3],pianoKeys[4], pianoKeys[5], pianoKeys[6],pianoKeys[7] )
     PianoKeyboardFourKeys(pianoKeys[8],pianoKeys[9], pianoKeys[10], pianoKeys[11],pianoKeys[12], pianoKeys[13],pianoKeys[14] )
        PianoKeyboardThreeKeys(pianoKeys[15],pianoKeys[16], pianoKeys[17], pianoKeys[18],pianoKeys[19] )
        PianoKeyboardFourKeys(pianoKeys[20],pianoKeys[21], pianoKeys[22], pianoKeys[23],pianoKeys[24], pianoKeys[25],pianoKeys[26] )
        PianoKeyboardThreeKeys(pianoKeys[27],pianoKeys[28], pianoKeys[29], pianoKeys[30],pianoKeys[31] )
        PianoKeyboardFourKeys(pianoKeys[32],pianoKeys[33], pianoKeys[34], pianoKeys[35],pianoKeys[36], pianoKeys[37],pianoKeys[38] )
        PianoKeyboardThreeKeys(pianoKeys[39],pianoKeys[40], pianoKeys[41], pianoKeys[42],pianoKeys[43] )
        PianoKeyboardFourKeys(pianoKeys[44],pianoKeys[45], pianoKeys[46], pianoKeys[47],pianoKeys[48], pianoKeys[49],pianoKeys[50] )
        PianoKeyboardThreeKeys(pianoKeys[51],pianoKeys[52], pianoKeys[53], pianoKeys[54],pianoKeys[55] )
        PianoKeyboardFourKeys(pianoKeys[56],pianoKeys[57], pianoKeys[58], pianoKeys[59],pianoKeys[60], pianoKeys[61],pianoKeys[62] )
        PianoKeyboardThreeKeys(pianoKeys[63],pianoKeys[64], pianoKeys[65], pianoKeys[66],pianoKeys[67] )
        PianoKeyboardFourKeys(pianoKeys[68],pianoKeys[69], pianoKeys[70], pianoKeys[71],pianoKeys[72], pianoKeys[73],pianoKeys[74] )
        PianoKeyboardThreeKeys(pianoKeys[75],pianoKeys[76], pianoKeys[77], pianoKeys[78],pianoKeys[79] )
        PianoKeyboardFourKeys(pianoKeys[80],pianoKeys[81], pianoKeys[82], pianoKeys[83],pianoKeys[84], pianoKeys[85],pianoKeys[86])
        PianoKeyboardOneKey(pianoKeys[87])
    }
}