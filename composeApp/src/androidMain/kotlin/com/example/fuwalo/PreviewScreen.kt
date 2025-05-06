package com.example.fuwalo



import PianoKeyboardFourKeys
import PianoKeyboardThreeKeys
import PianoKeyboardTwoKeys
import androidx.compose.foundation.Image
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fuwalo.presentation.keyboard.EightyEightKeysPiano
import com.example.fuwalo.presentation.keyboard.FallingNote
import com.example.fuwalo.presentation.keyboard.FallingNotesScreen
import com.example.fuwalo.presentation.keyboard.PianoLearningUI
import fuwalo.composeapp.generated.resources.Res
import fuwalo.composeapp.generated.resources.background


@Preview(
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"

)
@Composable
fun PianoScreen2(){
    PianoScreen({})
    }












